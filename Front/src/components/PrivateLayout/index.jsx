import Header from '../header'
import { Outlet, Navigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

function PrivateLayout({ dark, setDark }) {
  const { isAuthenticated } = useAuth()

  if (!isAuthenticated()) {
    return <Navigate to="/login" />
  }

  return (
    <>
      <Header dark={dark} setDark={setDark}/>
      <Outlet />
    </>
  )
}

export default PrivateLayout