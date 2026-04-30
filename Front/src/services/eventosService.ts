import { request } from "./api";

export enum EventoStatus {
  RASCUNHO = "RASCUNHO",
  CONFIRMADO = "CONFIRMADO",
  EM_ANDAMENTO = "EM_ANDAMENTO",
  FINALIZADO = "FINALIZADO",
  CANCELADO = "CANCELADO",
}

export interface EventoResponse {
  id: number;
  titulo: string;
  descricao: string;
  dataInicio: string; 
  dataFim: string;
  endereco: string;
  status: EventoStatus;
  tipoPagamento: string;
  valorTotal: number; 
  clienteId: number;
  clienteNome: string;
  administradorCriadorId: number;
  administradorCriadorNome: string;
}

export const getEvento = async (): Promise<EventoResponse[]> => {
  return request("/eventos");
};

export const getEventoById = async (
  id: number
): Promise<EventoResponse> => {
  return request(`/eventos/${id}`);
};

export const criarEvento = async (
  personagemItem: EventoResponse
): Promise<EventoResponse> => {
  return request("/eventos", {
    method: "POST",
    body: JSON.stringify(personagemItem),
  });
};

export const atualizarEvento = async (
  id: number,
  personagemItem: EventoResponse
): Promise<EventoResponse> => {
  return request(`/eventos/${id}`, {
    method: "PUT",
    body: JSON.stringify(personagemItem),
  });
};

export const deletarPersonagemItem = async (id: number): Promise<void> => {
  return request(`/eventos/${id}`, {
    method: "DELETE",
  });
};