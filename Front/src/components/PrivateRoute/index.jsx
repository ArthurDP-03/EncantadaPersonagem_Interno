import { Navigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

export default function PrivateRoute({ children, role }) {
  const { user, hasRole } = useAuth()

  if (!user) {
    return <Navigate to="/login" />
  }

  if (role && !hasRole(role)) {
    return <Navigate to="/login" />
  }

  return children
}