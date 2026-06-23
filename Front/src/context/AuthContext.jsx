/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useState, useMemo, useEffect } from 'react'

const AuthContext = createContext(null)

const normalizarRole = (role) => {
  if (!role) return null
  if (typeof role === 'string') return role
  return role.authority || role.role || null
}

const listarRoles = (user) => {
  if (!user) return []

  return [user.role, ...(user.roles || []), ...(user.authorities || [])]
    .map(normalizarRole)
    .filter(Boolean)
}

export function decodeToken(token) {
  try {
    const base64Url = token.split('.')[1]

    const base64 = base64Url
      .replace(/-/g, '+')
      .replace(/_/g, '/')
      .padEnd(base64Url.length + (4 - base64Url.length % 4) % 4, '=')

  const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + c.charCodeAt(0).toString(16).padStart(2, '0'))
        .join('')
    )

    const payload = JSON.parse(jsonPayload)

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
    const roles = listarRoles(user)
    return roles.includes(role) || roles.includes(`ROLE_${role}`)
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
