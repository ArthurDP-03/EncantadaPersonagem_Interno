import { request } from './api'

interface ClienteResponse {
  id: number
  nome: string
  telefone: string
  email: string
}

export const getClientes = async () => {
  return request('/clientes')
}

export const getClienteById = async (id: number) => {
  return request(`/clientes/${id}`)
}

export const criarCliente = async (cliente: ClienteResponse) => {
  return request('/clientes', {
    method: 'POST',
    body: JSON.stringify(cliente)
  })
}

export const atualizarCliente = async (id: number, cliente: ClienteResponse) => {
  return request(`/clientes/${id}`, {
    method: 'PUT',
    body: JSON.stringify(cliente)
  })
}

export const deletarCliente = async (id: number) => {
  return request(`/clientes/${id}`, {
    method: 'DELETE'
  })
}