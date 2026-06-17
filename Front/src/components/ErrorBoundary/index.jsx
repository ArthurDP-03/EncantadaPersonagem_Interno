import { Component } from 'react';
import './index.css';
import i18n from '../../i18n';

export class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = {
      hasError: false,
      error: null,
    };
  }

  static getDerivedStateFromError(error) {
    return {
      hasError: true,
      error,
    };
  }

  componentDidCatch(error, info) {
    console.error('Erro de renderização React:', error, info);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="error-boundary">
          <div className="error-container">
            <h1>{i18n.t('errors.boundary.title')}</h1>
            <p className="error-message">
              {this.state.error?.message || i18n.t('errors.boundary.unexpected')}
            </p>
            <button
              className="error-button"
              onClick={() => window.location.reload()}
            >
              {i18n.t('errors.boundary.reload')}
            </button>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
