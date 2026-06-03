const BASE_URL = 'http://localhost:8080/api'
const REQUEST_TIMEOUT = 30000 // 30 segundos

export async function request(path: string, options: RequestInit = {}): Promise<any> {
  const token = localStorage.getItem('token')

  const headers: any = {
    'Content-Type': 'application/json',
    ...(options.headers || {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {})
  }

  const controller = new AbortController()
  const timeoutId = setTimeout(() => controller.abort(), REQUEST_TIMEOUT)

  try {
    const response = await fetch(`${BASE_URL}${path}`, {
      ...options,
      headers,
      signal: controller.signal
    })

    clearTimeout(timeoutId)

    let data = null

    try {
      const text = await response.text()
      data = text ? JSON.parse(text) : null
    } catch {
      data = null
    }

    if (response.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }

    if (!response.ok) {
      throw {
        status: response.status,
        data,
        message: data?.message || data?.error || `Erro ${response.status}`
      }
    }

    return data
  } catch (error: any) {
    clearTimeout(timeoutId)

    // Erro de timeout
    if (error.name === 'AbortError') {
      throw {
        status: 0,
        data: null,
        message: 'Tempo limite de requisição excedido. Verifique sua conexão e tente novamente.',
        error: 'TIMEOUT'
      }
    }

    // Erro de rede
    if (error instanceof TypeError && error.message.includes('fetch')) {
      throw {
        status: 0,
        data: null,
        message: 'Erro de conexão. Verifique se o servidor está disponível.',
        error: 'NETWORK_ERROR'
      }
    }

    throw error
  }
}