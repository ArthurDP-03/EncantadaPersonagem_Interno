import { Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'

import Login from './components/login'
import Demo from './components/demo'
import HomeTeste from './components/homeTeste'

import PrivateRoute from './components/PrivateRoute'
import PrivateLayout from './components/PrivateLayout'

import './App.css'

function App() {
  return (
    <AuthProvider>
      <Routes>

        {/* rotas públicas */}
        <Route path="/" element={<Login />} />
        <Route path="/testes" element={<Demo />} />

        {/* rotas protegidas com layout */}
        <Route
          element={
            <PrivateRoute>
              <PrivateLayout />
            </PrivateRoute>
          }
        >
          <Route path="/HomeTeste" element={<HomeTeste />} />
        </Route>

      </Routes>
    </AuthProvider>
  )
}

export default App