import { API_BASE_URL } from '../config.js'

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

  return data
}

export async function post(path, payload) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  return parseResponse(response)
}
