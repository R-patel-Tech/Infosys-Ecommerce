import { post } from './api.js'
import {
  clearAuthSession as clearStoredAuthSession,
  getAuthToken as readAuthToken,
  saveAuthSession,
} from '../utils/session.js'

export async function loginUser(credentials) {
  const data = await post('/users/login', credentials)
  const token = data?.token
  const userId = data?.userId

  if (!token) {
    throw new Error('Login failed: invalid server response.')
  }

  saveAuthSession({ token, userId, email: credentials?.email })

  return { token, userId }
}

export function registerUser(payload) {
  return post('/users/register', payload).then((data) => data?.data ?? data)
}

export function getAuthToken() {
  return readAuthToken()
}

export function isAuthenticated() {
  return Boolean(getAuthToken())
}

export function logout() {
  clearStoredAuthSession()
}

export function clearAuthSession() {
  clearStoredAuthSession()
}
