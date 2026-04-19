import { request } from "./api";

export const getPersonagens = async () => {
  return request("/personagens");
};