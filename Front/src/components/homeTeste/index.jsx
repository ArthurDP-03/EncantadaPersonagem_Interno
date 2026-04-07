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
    <section style={{ padding: '2rem' }}>
      <h1>Bem-vindo!</h1>

      {usuario && (
        <div>
          <p><strong>Email:</strong> {usuario.sub}</p>
          <p><strong>Expira em:</strong> {new Date(usuario.exp * 1000).toLocaleString()}</p>
        </div>
      )}

      <button onClick={handleLogout}>Sair</button>
    </section>
  )
}

export default HomeTeste