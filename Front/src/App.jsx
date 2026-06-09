import { Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'

import Login from './components/login'
import Home from './components/home'

import PrivateRoute from './components/PrivateRoute'
import PrivateLayout from './components/PrivateLayout'
import { ErrorBoundary } from './components/ErrorBoundary'

import './App.css'
import Personagens from './components/personagens'
import Clientes from './components/clientes'
import Eventos from './components/eventos'
import Colaboradores from './components/colaboradores'
import { useDarkMode } from './hooks/useDarkMode'
import Dashboard from './components/dashboard'

function App() {
  const [dark, setDark] = useDarkMode();
  return (
    <ErrorBoundary>
      <AuthProvider>
        <Routes>
          {/* rotas públicas */}
          <Route path="/login" element={<Login />} />
          <Route path="/testes" element={<Demo />} />
          {/* privadas com layout */}
          <Route element={<PrivateLayout />}>
            <Route path="/" element={<Home/>}/>
            <Route path="/personagens" element={<PrivateRoute role="ADMIN"><Personagens /></PrivateRoute>} />
            <Route path="/clientes" element={<PrivateRoute role="ADMIN"><Clientes /></PrivateRoute>} />
            <Route path="/colaboradores" element={<PrivateRoute role="ADMIN"><Colaboradores /></PrivateRoute>} />
            <Route path="/eventos" element={<Eventos />} />
          </Route>
        </Routes>
      </AuthProvider >
    </ErrorBoundary>
  )
}

export default App