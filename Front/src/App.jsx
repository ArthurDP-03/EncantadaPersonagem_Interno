import { Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import Login from './components/login'
import Header from './components/header'
import Demo from './components/demo'
import HomeTeste from './components/HomeTeste'
import PrivateRoute from './components/PrivateRoute/PrivateRoute'
import './App.css'

function App() {
  return (
    <AuthProvider>
      <Header />
      <Routes>

        <Route path="/" element={<Login />} />
        <Route path="/testes" element={<Demo />} />

        {/* rota protegida */}
        <Route
          path="/HomeTeste"
          element={
            <PrivateRoute>
              <HomeTeste />
            </PrivateRoute>
          }
        />

      </Routes>
    </AuthProvider>
  )
}

export default App