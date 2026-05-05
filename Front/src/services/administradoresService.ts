import { request } from './api'

export interface Administrador {
    id: number,
    nome: string,
    email: string,
    senha: string
    telefone: string,
    tipo: string
}
// GET /atores
export const getAdministrador = async (): Promise<Administrador[]> => {
    return request('/administradores')
}

// GET /administradores/{id}
export const getAdministradorById = async (id: number): Promise<Administrador> => {
    return request(`/administradores/${id}`)
}

// POST /administradores
export const criarAdministrador = async (ator: Administrador): Promise<Administrador> => {
    return request('/administradores', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(ator)
    })
}

// PUT /administradores/{id}
export const atualizarAdministrador = async (id: number, ator: Administrador): Promise<Administrador> => {
    return request(`/administradores/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(ator)
    })
}

// DELETE /administradores/{id}
export const deletarAdministrador = async (id: number): Promise<void> => {
    return request(`/administradores/${id}`, {
        method: 'DELETE'
    })
}