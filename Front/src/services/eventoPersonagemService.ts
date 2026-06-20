import { request } from "./api";
import type { ConviteAtorItemRequest, ConviteResponse } from "./conviteService";

export interface EventoPersonagemResponse {
  id: number;
  eventoId: number;
  eventoTitulo: string;
  personagemItemId: number;
  personagemItemCodigo: string;
  personagemNome: string;
}

export interface AdicionarPersonagemRequest {
  eventoId: number;
  personagemItemId: number;
}

export interface TrocarPersonagemRequest {
  novoPersonagemItemId: number;
}

export interface ReabrirConvitesRequest {
  convites: ConviteAtorItemRequest[];
}

export const listarPersonagensDoEvento = async (
  eventoId: number
): Promise<EventoPersonagemResponse[]> => {
  return request(`/evento-personagens?eventoId=${eventoId}`);
};

export const adicionarPersonagemAoEvento = async (
  personagem: AdicionarPersonagemRequest
): Promise<EventoPersonagemResponse> => {
  return request("/evento-personagens", {
    method: "POST",
    body: JSON.stringify(personagem),
  });
};

export const removerPersonagemDoEvento = async (epId: number): Promise<void> => {
  return request(`/evento-personagens/${epId}`, {
    method: "DELETE",
  });
};

export const trocarPersonagemDoEvento = async (
  id: number,
  personagem: TrocarPersonagemRequest
): Promise<EventoPersonagemResponse> => {
  return request(`/evento-personagens/${id}/trocar-personagem`, {
    method: "PATCH",
    body: JSON.stringify(personagem),
  });
};

export const reabrirConvites = async (
  id: number,
  convites: ReabrirConvitesRequest
): Promise<ConviteResponse[]> => {
  return request(`/evento-personagens/${id}/reabrir-convites`, {
    method: "POST",
    body: JSON.stringify(convites),
  });
};
