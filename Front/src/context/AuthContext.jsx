import { createContext, useContext, useState, useMemo, useEffect } from 'react'

const AuthContext = createContext(null)

export function decodeToken(token) {
  try {
    const base64Url = token.split('.')[1]

    const base64 = base64Url
      .replace(/-/g, '+')
      .replace(/_/g, '/')
      .padEnd(base64Url.length + (4 - base64Url.length % 4) % 4, '=')

    const payload = JSON.parse(atob(base64))

    // expiração
    if (payload.exp * 1000 < Date.now()) {
      return null
    }

    return payload
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(localStorage.getItem('token'))

  useEffect(() => {
    const sync = () => setToken(localStorage.getItem('token'))
    window.addEventListener('storage', sync)
    return () => window.removeEventListener('storage', sync)
  }, [])

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
    return (
      user?.role === role ||
      user?.roles?.includes?.(role) ||
      user?.authorities?.includes?.(role)
    )
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