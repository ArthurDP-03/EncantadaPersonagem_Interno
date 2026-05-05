const BASE_URL = 'http://localhost:8080/api' //mudar de acordo com cada 1 

export async function request(
  path: string,
  options: RequestInit = {}
): Promise<any> {
  const token = localStorage.getItem('token')

  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {})
  }

  const response = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers
  })

  const text = await response.text()
  const data = text ? JSON.parse(text) : null

  if (!response.ok) {
    throw { status: response.status, data }
  }

  return data
}