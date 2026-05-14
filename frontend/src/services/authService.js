import { post } from './api.js'

const AUTH_TOKEN_KEY = 'authToken'

function saveAuthToken(token) {
  if (token) {
    sessionStorage.setItem(AUTH_TOKEN_KEY, token)
  }
}

export async function loginUser(credentials) {
  const data = await post('/users/login', credentials)
  const token = data?.token
  const userId = data?.userId

  if (!token) {
    throw new Error('Login failed: invalid server response.')
  }

  saveAuthToken(token)

  if (userId != null && userId !== '') {
    sessionStorage.setItem('userId', String(userId))
  }

  if (credentials?.email) {
    sessionStorage.setItem('userEmail', String(credentials.email))
  }

  return { token, userId }
}

export function registerUser(payload) {
  return post('/users/register', payload)
}

export function getAuthToken() {
  return sessionStorage.getItem(AUTH_TOKEN_KEY)
}

export function isAuthenticated() {
  return Boolean(getAuthToken())
}

export function logout() {
  sessionStorage.removeItem(AUTH_TOKEN_KEY)
  sessionStorage.removeItem('userId')
  sessionStorage.removeItem('userEmail')
}
