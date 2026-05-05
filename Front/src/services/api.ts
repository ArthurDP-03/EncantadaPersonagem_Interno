const BASE_URL = 'http://localhost:8080/api'

export async function request(path: string, options: RequestInit = {}): Promise<any> {
  const token = localStorage.getItem('token')

  const headers: any = {
    'Content-Type': 'application/json',
    ...(options.headers || {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {})
  }

  const response = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers
  })

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
    throw { status: response.status, data }
  }

  return data
}