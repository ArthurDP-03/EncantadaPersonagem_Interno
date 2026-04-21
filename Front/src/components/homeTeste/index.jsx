import { useAuth } from '../../context/AuthContext'
import { jwtDecode } from 'jwt-decode'

function HomeTeste() {
  const { token, logout } = useAuth()

  function handleLogout() {
    logout()
  }

  return (
    <section>
      <button onClick={handleLogout}>Sair</button>
    </section>
  )
}

export default HomeTeste