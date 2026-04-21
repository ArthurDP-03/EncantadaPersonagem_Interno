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

function App() {
  return (
    <AuthProvider>
      <Routes>

        {/* rotas públicas */}
        <Route path="/" element={<Login />} />
        <Route path="/testes" element={<Demo />} />

        {/* privadas com layout */}
        <Route element={<PrivateLayout />}>

          <Route
            path="/homeAdmin"
            element={
              <PrivateRoute role="ADMIN">
                <HomeAdmin />
              </PrivateRoute>
            }
          />

          <Route
            path="/homeAtor"
            element={
              <PrivateRoute role="ATOR">
                <HomeAtor />
              </PrivateRoute>
            }
          />

        </Route>

      </Routes>
    </AuthProvider >
  )
}

export default App