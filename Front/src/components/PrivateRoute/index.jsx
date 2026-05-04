import { Navigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

export default function PrivateRoute({ children, role }) {
  const { isAuthenticated, hasRole } = useAuth()

  if (!isAuthenticated()) {
    return <Navigate to="/" />
  }

  if (role && !hasRole(role)) {
    return <Navigate to="/" />
  }

  return children
}