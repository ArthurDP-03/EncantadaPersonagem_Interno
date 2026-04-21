import { useAuth } from '../../context/AuthContext'

function HomeAdmin() {
  const { user, logout } = useAuth()

  function handleLogout() {
    logout()
  }

  return (
    <section>
      <h1>Painel Admin</h1>

      <p>Bem-vindo, {user?.name}</p>
      <p>Email: {user?.sub}</p>
      <p>Perfil: {user?.role}</p>

      <button onClick={handleLogout}>Sair</button>
    </section>
  )
}

export default HomeAdmin