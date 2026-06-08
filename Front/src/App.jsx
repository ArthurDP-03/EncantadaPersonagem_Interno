import { Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'

import Login from './components/login'
import Demo from './components/demo'
import Home from './components/home'

import PrivateRoute from './components/PrivateRoute'
import PrivateLayout from './components/PrivateLayout'

import './App.css'
import Personagens from './components/personagens'
import Clientes from './components/clientes'
import Eventos from './components/eventos'
import EventoDetalhes from './components/eventoDetalhes'
import Colaboradores from './components/colaboradores'

function App() {
  return (
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
          <Route path="/eventos/:id" element={<EventoDetalhes />} />
        </Route>
      </Routes>
    </AuthProvider >
  )
}

export default App