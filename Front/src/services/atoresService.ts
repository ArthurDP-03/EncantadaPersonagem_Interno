import { request } from './api'

export interface Ator {
  id: number
  nome: string
  email: string
  telefone: string
  genero: string
  altura: number
  peso: number
  observacao: string
  ativo: boolean
}
// GET /atores
export const getAtores = async (): Promise<Ator[]> => {
  return request('/atores')
}

// GET /atores/{id}
export const getAtorById = async (id: number): Promise<Ator> => {
  return request(`/atores/${id}`)
}

// POST /atores
export const criarAtor = async (ator: Ator): Promise<Ator> => {
  return request('/atores', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(ator)
  })
}

// PUT /atores/{id}
export const atualizarAtor = async (id: number, ator: Ator): Promise<Ator> => {
  return request(`/atores/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(ator)
  })
}

// DELETE /atores/{id}
export const deletarAtor = async (id: number): Promise<void> => {
  return request(`/atores/${id}`, {
    method: 'DELETE'
  })
}