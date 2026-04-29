import { useAuth } from '../../context/AuthContext'

function HomeAtor() {
  const { user, logout } = useAuth()

  function handleLogout() {
    logout()
  }

  return (
    <section>
      <h1>Painel Ator</h1>

      <p>Bem-vindo, {user?.name}</p>
      <p>Email: {user?.sub}</p>
      <p>Perfil: {user?.role}</p>

      <button onClick={handleLogout}>Sair</button>
    </section>
  )
}

export default HomeAtor