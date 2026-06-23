import { request } from "./api";

export enum EscalacaoStatus {
  PENDENTE_CONFIRMACAO_ATOR = "PENDENTE_CONFIRMACAO_ATOR",
  CONFIRMADA = "CONFIRMADA",
  CANCELADA = "CANCELADA",
}

export interface EscalacaoResponse {
  id: number;
  eventoPersonagemId: number;
  eventoId: number;
  eventoTitulo: string;
  personagemId: number;
  personagemNome: string;
  atorId: number;
  atorNome: string;
  personagemItemId: number;
  personagemItemCodigo: string;
  administradorId: number;
  status: EscalacaoStatus;
  dataEscolha: string;
  dataConfirmacaoAtor: string | null;
}

export interface EscalacaoCreateRequest {
  eventoPersonagemId: number;
  atorId: number;
  personagemItemId: number;
}

export const escolherAtorFinal = async (
  escalacao: EscalacaoCreateRequest
): Promise<EscalacaoResponse> => {
  return request("/escalacoes", {
    method: "POST",
    body: JSON.stringify(escalacao),
  });
};

export const confirmarPresenca = async (id: number): Promise<EscalacaoResponse> => {
  return request(`/escalacoes/${id}/confirmar`, {
    method: "PATCH",
  });
};

export const cancelarEscalacao = async (id: number): Promise<void> => {
  return request(`/escalacoes/${id}`, {
    method: "DELETE",
  });
};

export const getEscalacaoById = async (id: number): Promise<EscalacaoResponse> => {
  return request(`/escalacoes/${id}`);
};

export const getEscalacoesByEventoId = async (
  eventoId: number
): Promise<EscalacaoResponse[]> => {
  return request(`/escalacoes/evento/${eventoId}`);
};

export const getMinhasEscalacoes = async (): Promise<EscalacaoResponse[]> => {
  return request("/escalacoes/ator/me");
};
