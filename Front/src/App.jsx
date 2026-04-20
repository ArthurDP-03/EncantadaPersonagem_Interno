import { Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'

import Login from './components/login'
import Demo from './components/demo'
import HomeTeste from './components/homeTeste'

import PrivateRoute from './components/PrivateRoute'
import PrivateLayout from './components/PrivateLayout'

import './App.css'
import Personagens from './components/personagens'

function App() {
  return (
    <AuthProvider>
      <Routes>

        {/* rotas públicas */}
        <Route path="/login" element={<Login />} />
        <Route path="/testes" element={<Demo />} />

        {/* rotas protegidas com layout */}
        <Route
          element={
            <PrivateRoute>
              <PrivateLayout />
            </PrivateRoute>
          }
        >
          <Route path="/" element={<HomeTeste />} />
          <Route path="/personagens" element={<Personagens />} />
        </Route>

      </Routes>
    </AuthProvider>
  )
}

export default App