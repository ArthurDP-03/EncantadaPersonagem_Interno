import { request } from "./api";

export interface PersonagemFormData {
  nome: string;
  descricao: string;
  foto: string;
}

export interface PersonagemResponse {
  id: number;
  nome: string;
  descricao: string;
  foto: string;
}

export const getPersonagens = async (): Promise<PersonagemResponse[]> => {
  return request("/personagens");
};

export const getPersonagemById = async (id: number): Promise<PersonagemResponse> => {
  return request(`/personagens/${id}`);
};

export const criarPersonagem = async (
  personagem: PersonagemFormData
): Promise<PersonagemResponse> => {
  return request("/personagens", {
    method: "POST",
    body: JSON.stringify(personagem),
  });
};

export const atualizarPersonagem = async (
  id: number,
  personagem: PersonagemFormData
): Promise<PersonagemResponse> => {
  return request(`/personagens/${id}`, {
    method: "PUT",
    body: JSON.stringify(personagem),
  });
};

export const deletarPersonagem = async (id: number): Promise<void> => {
  return request(`/personagens/${id}`, {
    method: "DELETE",
  });
};
