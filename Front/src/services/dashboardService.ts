import { request } from "./api";

export async function getDashboard() {
  return request("/dashboard");
}