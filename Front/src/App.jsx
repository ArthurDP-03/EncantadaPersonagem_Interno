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

      {currentView === views.login ? <Login /> : <Demo />}
    </div>
  )
}

export default App
