import { useState } from 'react'
import Login from './components/login'
import Demo from './components/demo'
import './App.css'

const views = {
  login: 'login',
  demo: 'demo',
}

function App() {
  const [currentView, setCurrentView] = useState(views.login)

  return (
    <div className="app-shell">
      <nav className="app-nav">
        <div className="app-brand">
          <span className="app-kicker">Encantada Personagens</span>
          <strong>Sistema Interno</strong>
        </div>

        <div className="app-switcher">
          <button
            type="button"
            className={currentView === views.login ? 'app-switch active' : 'app-switch'}
            onClick={() => setCurrentView(views.login)}
          >
            Login
          </button>
          <button
            type="button"
            className={currentView === views.demo ? 'app-switch active' : 'app-switch'}
            onClick={() => setCurrentView(views.demo)}
          >
            Demo
          </button>
        </div>
      </nav>

      {currentView === views.login ? <Login /> : <Demo />}
    </div>
  )
}

export default App
