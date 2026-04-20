import { createContext, useContext, useState } from 'react'

const AuthContext = createContext(null)

function decodificarToken(token) {
  if (!token) return null
  try {
    const base64 = token.split('.')[1]
      .replace(/-/g, '+')
      .replace(/_/g, '/')
    
    const jsonString = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + c.charCodeAt(0).toString(16).padStart(2, '0'))
        .join('')
    )
    
    return JSON.parse(jsonString)
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(
    localStorage.getItem('token')
  )

  function salvarToken(novoToken) {
    localStorage.setItem('token', novoToken)
    setToken(novoToken)
  }

  const user = decodificarToken(token)


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