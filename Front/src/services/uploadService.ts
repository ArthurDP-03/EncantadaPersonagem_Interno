import { API_BASE_URL } from "./api";

const UPLOAD_URL = `${API_BASE_URL}/upload`;
const UPLOAD_TIMEOUT = 30000;

const parseJsonResponse = async (response: Response) => {
  try {
    const text = await response.text();
    return text ? JSON.parse(text) : null;
  } catch {
    return null;
  }
};

export const uploadImagem = async (file: File): Promise<string> => {
  const token = localStorage.getItem("token");
  const formData = new FormData();
  formData.append("file", file);

  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), UPLOAD_TIMEOUT);

  try {
    const response = await fetch(UPLOAD_URL, {
      method: "POST",
      headers: {
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      body: formData,
      signal: controller.signal,
    });

    clearTimeout(timeoutId);

    const data = await parseJsonResponse(response);

    if (response.status === 401) {
      localStorage.removeItem("token");
      window.location.href = "/login";
    }

    if (!response.ok) {
      throw {
        status: response.status,
        data,
        message: data?.message || data?.error || `Erro ${response.status}`,
      };
    }

    if (!data?.url) {
      throw {
        status: 500,
        data,
        message: "Resposta de upload invalida",
      };
    }

    return data.url;
  } catch (error: any) {
    clearTimeout(timeoutId);

    if (error.name === "AbortError") {
      throw {
        status: 0,
        data: null,
        message:
          "Tempo limite de upload excedido. Verifique sua conexao e tente novamente.",
        error: "TIMEOUT",
      };
    }

    if (error instanceof TypeError && error.message.includes("fetch")) {
      throw {
        status: 0,
        data: null,
        message: "Erro de conexao. Verifique se o servidor esta disponivel.",
        error: "NETWORK_ERROR",
      };
    }

    throw error;
  }
};
