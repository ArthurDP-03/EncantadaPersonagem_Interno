import Header from '../header'
import { Outlet, Navigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

function PrivateLayout() {
  const { isAuthenticated } = useAuth()

  if (!isAuthenticated()) {
    return <Navigate to="/login" />
  }

  return (
    <>
      <Header />
      <Outlet />
    </>
  )
}

export default PrivateLayout