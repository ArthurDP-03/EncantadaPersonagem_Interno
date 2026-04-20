// hooks/useLogin.js
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { login } from '../services/authService'

export function useLogin() {
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)

  const { salvarToken } = useAuth()
  const navigate = useNavigate()

  const validar = (email, senha) => {
    if (!email || !senha) return 'Preencha todos os campos'
    if (senha.length < 6) return 'Senha muito curta'
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

      if (!dados?.token) throw new Error('Token não recebido')

      salvarToken(dados.token)
      navigate('/')
    } catch (e) {
      const status = e?.status

      if (status === 401) setErro('Email ou senha inválidos')
      else if (status === 403) setErro('Acesso negado')
      else setErro('Erro ao fazer login')
    } finally {
      setCarregando(false)
    }
  }

  return { entrar, erro, setErro, carregando }
}