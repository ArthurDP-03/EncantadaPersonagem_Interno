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
import EventoDetalhes from './components/eventoDetalhes'
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
          {/* privadas com layout */}
          <Route element={<PrivateLayout dark={dark} setDark={setDark} />}>               
          <Route path="/" element={<Home />} />
            <Route path="/financeiro" element={<PrivateRoute role="ADMIN"><Dashboard /></PrivateRoute>} />
            <Route path="/personagens" element={<PrivateRoute role="ADMIN"><Personagens /></PrivateRoute>} />
            <Route path="/clientes" element={<PrivateRoute role="ADMIN"><Clientes /></PrivateRoute>} />
            <Route path="/colaboradores" element={<PrivateRoute role="ADMIN"><Colaboradores /></PrivateRoute>} />
            <Route path="/eventos" element={<Eventos />} />
            <Route path="/eventos/:id" element={<PrivateRoute role="ADMIN"><EventoDetalhes /></PrivateRoute>} />
          </Route>
        </Routes>
      </AuthProvider >
    </ErrorBoundary>
  )
}

export default App
