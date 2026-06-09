import { request } from "./api";

export enum PersonagemItemStatus {
  DISPONIVEL = "DISPONIVEL",
  EM_USO = "EM_USO",
  MANUTENCAO = "MANUTENCAO",
}

export interface PersonagemItemResponse {
  id: number;
  idPersonagem: number;
  nomePersonagem: string;
  codigo: string;
  status: PersonagemItemStatus;
}

export interface PersonagemItemRequest {
  idPersonagem: number;
}

export interface PersonagemItemStatusRequest {
  status: PersonagemItemStatus;
}

export const getPersonagemItens = async (): Promise<PersonagemItemResponse[]> => {
  return request("/personagem-item");
};

export const getPersonagemItemById = async (id: number): Promise<PersonagemItemResponse> => {
  return request(`/personagem-item/${id}`);
};

export const criarPersonagemItem = async (
  personagemItem: PersonagemItemRequest
): Promise<PersonagemItemResponse> => {
  return request("/personagem-item", {
    method: "POST",
    body: JSON.stringify(personagemItem),
  });
};

export const atualizarStatusPersonagemItem = async (
  id: number,
  personagemItem: PersonagemItemStatusRequest
): Promise<PersonagemItemResponse> => {
  return request(`/personagem-item/${id}/status`, {
    method: "PATCH",
    body: JSON.stringify(personagemItem),
  });
};

export const deletarPersonagemItem = async (id: number): Promise<void> => {
  return request(`/personagem-item/${id}`, {
    method: "DELETE",
  });
};
