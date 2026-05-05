import { request } from "./api";

export const getPersonagens = async () => {
  return request("/personagens");
};

export const getPersonagemById = async (id) => {
  return request(`/personagens/${id}`);
};

export const criarPersonagem = async (personagem) => {
  return request("/personagens", {
    method: "POST",
    body: JSON.stringify(personagem),
  });
};

export const atualizarPersonagem = async (id, personagem) => {
  return request(`/personagens/${id}`, {
    method: "PUT",
    body: JSON.stringify(personagem),
  });
};

export const deletarPersonagem = async (id) => {
  return request(`/personagens/${id}`, {
    method: "DELETE",
  });
};
