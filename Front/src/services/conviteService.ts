import { request } from "./api";

export enum ConviteStatus {
  PENDENTE = "PENDENTE",
  ACEITO = "ACEITO",
  RECUSADO = "RECUSADO",
  EXPIRADO = "EXPIRADO",
  CANCELADO = "CANCELADO",
}

export interface ConviteResponse {
  id: number;
  eventoPersonagemId: number;
  eventoId: number;
  eventoTitulo: string;
  personagemId: number;
  personagemNome: string;
  personagemItemId: number;
  personagemItemCodigo: string;
  atorId: number;
  atorNome: string;
  administradorId: number;
  status: ConviteStatus;
  dataEnvio: string;
  dataExpiracao: string;
  dataResposta: string | null;
}

export interface ConviteAtorItemRequest {
  atorId: number;
  personagemItemId: number;
}

export interface ConviteCreateRequest {
  eventoPersonagemId: number;
  convites: ConviteAtorItemRequest[];
}

export interface ConviteRespostaRequest {
  status: ConviteStatus.ACEITO | ConviteStatus.RECUSADO;
}

export const enviarConvites = async (convite: ConviteCreateRequest): Promise<ConviteResponse[]> => {
  return request("/convites", {
    method: "POST",
    body: JSON.stringify(convite),
  });
};

export const listarPorEventoPersonagem = async (epId: number): Promise<ConviteResponse[]> => {
  return request(`/convites/evento-personagem/${epId}`);
};

export const listarEnviadosADM = async (): Promise<ConviteResponse[]> => {
  return request("/convites/enviadosADM");
};

export const listarMeusConvites = async (): Promise<ConviteResponse[]> => {
  return request("/convites/conviteAtor");
};

export const responderConvite = async (
  id: number,
  resposta: ConviteRespostaRequest
): Promise<ConviteResponse> => {
  return request(`/convites/${id}/responder`, {
    method: "PATCH",
    body: JSON.stringify(resposta),
  });
};

export const cancelarConvite = async (id: number): Promise<void> => {
  return request(`/convites/${id}`, {
    method: "DELETE",
  });
};

