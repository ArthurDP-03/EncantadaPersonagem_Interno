import { createContext, useContext, useState } from 'react'
import { jwtDecode } from 'jwt-decode'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [token, setToken] = useState(
    localStorage.getItem('token')
  )

  function salvarToken(novoToken) {
    localStorage.setItem('token', novoToken)
    setToken(novoToken)
  }

  const user = token ? jwtDecode(token) : null


  function logout() {
    localStorage.removeItem('token')
    setToken(null)
  }

  return (
    <AuthContext.Provider value={{ token, user, salvarToken, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)