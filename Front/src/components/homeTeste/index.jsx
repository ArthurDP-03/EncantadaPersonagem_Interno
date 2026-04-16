import { useAuth } from '../../context/AuthContext'
import { useNavigate } from 'react-router-dom'
import { jwtDecode } from 'jwt-decode'

function HomeTeste() {
  const { token, logout } = useAuth()
  const navigate = useNavigate()

  // decodifica o token pra pegar os dados do usuário
  const usuario = token ? jwtDecode(token) : null

  function handleLogout() {
    logout()
    navigate('/')
  }

  return (
    <section>
      <button onClick={handleLogout}>Sair</button>
    </section>
  )
}

export default HomeTeste