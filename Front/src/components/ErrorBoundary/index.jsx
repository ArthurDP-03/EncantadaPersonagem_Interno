import { useEffect, useState } from 'react';
import Swal from 'sweetalert2';
import './index.css';

export function ErrorBoundary({ children }) {
  const [error, setError] = useState(null);

  useEffect(() => {
    const handleError = (event) => {
      console.error('Erro não tratado:', event.error);

      setError({
        message: event.error?.message || event.message || 'Erro desconhecido',
        stack: event.error?.stack,
        source: event.filename,
        lineno: event.lineno
      });

      Swal.fire({
        icon: "error",
        title: "Erro não tratado",
        text: "Ocorreu um erro inesperado. A página será recarregada.",
        confirmButtonText: "OK",
        allowOutsideClick: false,
        allowEscapeKey: false
      }).then(() => {
        window.location.reload();
      });
    };

    const handleUnhandledRejection = (event) => {
      console.error('Promise rejeitada não tratada:', event.reason);

      setError({
        message: event.reason?.message || 'Promise rejeitada não tratada',
        reason: event.reason
      });

      Swal.fire({
        icon: "error",
        title: "Erro não tratado",
        text: "Ocorreu um erro inesperado. A página será recarregada.",
        confirmButtonText: "OK",
        allowOutsideClick: false,
        allowEscapeKey: false
      }).then(() => {
        window.location.reload();
      });
    };

    window.addEventListener('error', handleError);
    window.addEventListener('unhandledrejection', handleUnhandledRejection);

    return () => {
      window.removeEventListener('error', handleError);
      window.removeEventListener('unhandledrejection', handleUnhandledRejection);
    };
  }, []);

  if (error) {
    return (
      <div className="error-boundary">
        <div className="error-container">
          <h1>⚠️ Erro</h1>
          <p className="error-message">{error.message}</p>
          {error.source && (
            <p className="error-details">
              Arquivo: {error.source}:{error.lineno}
            </p>
          )}
          <button
            className="error-button"
            onClick={() => window.location.reload()}
          >
            Recarregar página
          </button>
        </div>
      </div>
    );
  }

  return children;
}

