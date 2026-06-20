// components/Login.jsx
import { useState } from 'react'
import { useLogin } from '../../hooks/useLogin'
import logo from '../../assets/logo.png'
import './index.css'
import { useTranslation } from 'react-i18next'

function Login() {
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const { entrar, erro, setErro, carregando } = useLogin()
  const { t } = useTranslation()

  const handleSubmit = (event) => {
    event.preventDefault()
    entrar(email, senha)
  }

  return (
    <section className="section-login">
      <div className='form-container'>
        <div className="imagem-container">
          <img src={logo} alt={t('common.logoAlt')} className='imagem' />
        </div>

        <h1 className='title'>{t('login.title')}</h1>

        <form className='form' onSubmit={handleSubmit}>
          <div className='input-container text'>
            <label htmlFor="email" className='label'>{t('common.fields.email')}</label>
            <input
              type="email"
              id="email"
              className='input'
              value={email}
              onChange={(e) => {
                setEmail(e.target.value)
                setErro('')
              }}
            />
          </div>

          <div className='input-container text'>
            <label htmlFor="password" className='label'>{t('common.fields.password')}</label>
            <input
              type="password"
              id="password"
              className='input'
              value={senha}
              onChange={(e) => {
                setSenha(e.target.value)
                setErro('')
              }}
            />
          </div>

          {erro && <p style={{ color: 'red', fontSize: '14px' }}>{erro}</p>}

          <button type="submit" className='button' disabled={carregando}>
            {carregando ? t('login.entering') : t('login.enter')}
          </button>
        </form>
      </div>
    </section>
  )
}

export default Login
