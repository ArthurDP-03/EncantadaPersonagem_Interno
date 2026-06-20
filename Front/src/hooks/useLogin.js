import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { login } from '../services/authService'
import { decodeToken } from '../context/AuthContext'
import i18n from '../i18n'

export function useLogin() {
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)

  const { salvarToken } = useAuth()
  const navigate = useNavigate()
  const t = i18n.t.bind(i18n)

  const validar = (email, senha) => {
    if (!email || !senha) return t('login.validation.requiredFields')
    if (senha.length < 6) return t('login.validation.shortPassword')
    return null
  }

  const entrar = async (email, senha) => {
    if (carregando) return

    const erroValidacao = validar(email, senha)
    if (erroValidacao) {
      setErro(erroValidacao)
      return
    }

    setErro('')
    setCarregando(true)

    try {
      const dados = await login(email, senha)

      if (!dados?.token) {
        throw new Error(t('login.errors.tokenMissing'))
      }

      salvarToken(dados.token)
      const payload = decodeToken(dados.token)

      if (!payload) {
        throw new Error(t('login.errors.tokenInvalid'))
      }

      navigate('/')

    } catch (e) {
      const status = e?.status

      if (status === 401) setErro(t('login.errors.invalidCredentials'))
      else if (status === 403) setErro(t('login.errors.accessDenied'))
      else setErro(e?.message || t('login.errors.generic'))
    } finally {
      setCarregando(false)
    }
  }

  return { entrar, erro, setErro, carregando }
}
