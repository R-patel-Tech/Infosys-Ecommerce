import { post } from './api.js'

const AUTH_TOKEN_KEY = 'authToken'

export function loginUser(credentials) {
  return post('/api/users/login', credentials)
}

export function registerUser(payload) {
  return post('/api/users/register', payload)
}

export function getAuthToken() {
  return sessionStorage.getItem(AUTH_TOKEN_KEY)
}

export function isAuthenticated() {
  return Boolean(getAuthToken())
}

export function logout() {
  sessionStorage.removeItem(AUTH_TOKEN_KEY)
}
