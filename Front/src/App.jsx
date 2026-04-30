import { Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'

import Login from './components/login'
import Demo from './components/demo'
import HomeAtor from './components/homeAtor'
import HomeAdmin from './components/homeAdmin'

import PrivateRoute from './components/PrivateRoute'
import PrivateLayout from './components/PrivateLayout'

import './App.css'
import Personagens from './components/personagens'
import Clientes from './components/clientes'
import Eventos from './components/eventos'

function App() {
  return (
    <AuthProvider>
      <Routes>
        {/* rotas públicas */}
        <Route path="/login" element={<Login />} />
        <Route path="/" element={<Login />} /> 
        <Route path="/testes" element={<Demo />} />
        {/* privadas com layout */}
        <Route element={<PrivateLayout />}>
          <Route path="/homeAdmin" element={<PrivateRoute role="ADMIN"><HomeAdmin /></PrivateRoute>}/>
          <Route path="/homeAtor" element={<PrivateRoute role="ATOR"> <HomeAtor /> </PrivateRoute>}/>
          <Route path="/personagens" element={<Personagens />} />
          <Route path="/clientes" element={<Clientes />} />
          <Route path="/eventos" element={<Eventos />} />
        </Route>
      </Routes>
    </AuthProvider >
  )
}

export default App