import { API_BASE_URL } from '../config.js'

const AUTH_TOKEN_KEY = 'authToken'

function getAuthToken() {
  return sessionStorage.getItem(AUTH_TOKEN_KEY) || ''
}

async function parseResponse(response) {
  const contentType = response.headers.get('content-type') || ''
  let data

  if (contentType.includes('application/json')) {
    data = await response.json().catch(() => ({}))
  } else {
    const text = await response.text().catch(() => '')
    data = text ? { message: text } : {}
  }

  if (!response.ok) {
    throw new Error(data.error || data.message || 'Request failed. Please try again.')
  }

  if (typeof data === 'string') {
    data = { message: data }
  }

  return data
}

export async function get(path) {
  try {
    const token = getAuthToken()
    const response = await fetch(`${API_BASE_URL}${path}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
    })

    return parseResponse(response)
  } catch (error) {
    throw new Error(error?.message || 'Unable to reach the product API. Make sure the backend is running.')
  }
}

export async function post(path, payload) {
  try {
    const token = getAuthToken()
    const response = await fetch(`${API_BASE_URL}${path}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      body: JSON.stringify(payload),
    })

    return parseResponse(response)
  } catch (error) {
    throw new Error(error?.message || 'Unable to reach the API server. Make sure the backend is running.')
  }
}

export async function put(path, payload) {
  try {
    const token = getAuthToken()
    const response = await fetch(`${API_BASE_URL}${path}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      body: JSON.stringify(payload),
    })

    return parseResponse(response)
  } catch (error) {
    throw new Error(error?.message || 'Unable to reach the API server. Make sure the backend is running.')
  }
}
