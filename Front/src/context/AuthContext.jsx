import { createContext, useContext, useState, useMemo } from 'react'

const AuthContext = createContext(null)

function decodeToken(token) {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))

    if (payload.exp * 1000 < Date.now()) {
      return null
    }

    return payload
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(
    localStorage.getItem('token')
  )

  const user = useMemo(() => {
    if (!token) return null
    return decodeToken(token)
  }, [token])

  function salvarToken(novoToken) {
    localStorage.setItem('token', novoToken)
    setToken(novoToken)
  }

  function logout() {
    localStorage.removeItem('token')
    setToken(null)
  }

  function isAuthenticated() {
    return !!user
  }

  function hasRole(role) {
    return user?.role === role
  }

  return (
    <AuthContext.Provider
      value={{
        token,
        user,
        salvarToken,
        logout,
        isAuthenticated,
        hasRole
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)