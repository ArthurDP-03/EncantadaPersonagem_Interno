import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { login } from '../../services/authService'  // ← importa o service
import './index.css'

function Login() {
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)

  const { salvarToken } = useAuth()
  const navigate = useNavigate()

  async function handleSubmit(event) {
    event.preventDefault()
    setErro('')
    setCarregando(true)

    try {
      const dados = await login(email, senha)  // ← chama o AuthService
      salvarToken(dados.token)
      navigate('/HomeTeste')
    } catch (e) {
      if (e.status === 401) {
        setErro('Email ou senha inválidos')
      } else {
        setErro('Não foi possível conectar ao servidor')
      }
    } finally {
      setCarregando(false)
    }
  }

  return (
    <section className="section-login">
      <div className='form-container'>
        <div className="imagem-container">
          <img src="src/assets/logo.png" alt="Logo" className='imagem' />
        </div>
        <h1 className='title'>Login</h1>

        <form className='form' onSubmit={handleSubmit}>
          <div className='input-container text'>
            <label htmlFor="user" className='label'>Usuário</label>
            <input
              type="text"
              id="user"
              className='input'
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>

          <div className='input-container text'>
            <label htmlFor="password" className='label'>Senha</label>
            <input
              type="password"
              id="password"
              className='input'
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
            />
          </div>

          {erro && <p style={{ color: 'red', fontSize: '14px' }}>{erro}</p>}

          <button type="submit" className='button' disabled={carregando}>
            {carregando ? 'Entrando...' : 'Entrar'}
          </button>
        </form>
      </div>
    </section>
  )
}

export default Login