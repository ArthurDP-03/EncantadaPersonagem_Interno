import { useAuth } from '../../context/AuthContext'

function Home() {
  const { user, logout } = useAuth()

  function handleLogout() {
    logout()
  }

  return (
    <></>
  )
}

export default Home