import { request } from "./api";

export const getEventos = async () => {
  return request("/eventos");
};

export const getEventosById = async (id) => {
  return request(`/eventos/${id}`);
};

export const createEventos = async (eventos) => {
  return request("/eventos", {
    method: "POST",
    body: JSON.stringify(eventos),
  });
};

export const updateEventos = async (id, eventos) => {
  return request(`/eventos/${id}`, {
    method: "PUT",
    body: JSON.stringify(eventos),
  });
};

export const deleteEventos = async (id) => {
  return request(`/eventos/${id}`, {
    method: "DELETE",
  });
};
