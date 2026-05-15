import { post } from './api.js'

const AUTH_TOKEN_KEY = 'authToken'
const USER_ID_KEY = 'userId'
const USER_EMAIL_KEY = 'userEmail'

function saveAuthToken(token) {
  if (token) {
    localStorage.setItem(AUTH_TOKEN_KEY, token)
    sessionStorage.setItem(AUTH_TOKEN_KEY, token)
  }
}

function getStoredValue(key) {
  return localStorage.getItem(key) || sessionStorage.getItem(key)
}

function removeStoredValue(key) {
  localStorage.removeItem(key)
  sessionStorage.removeItem(key)
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
    localStorage.setItem(USER_ID_KEY, String(userId))
    sessionStorage.setItem(USER_ID_KEY, String(userId))
  }

  if (credentials?.email) {
    localStorage.setItem(USER_EMAIL_KEY, String(credentials.email))
    sessionStorage.setItem(USER_EMAIL_KEY, String(credentials.email))
  }

  return { token, userId }
}

export function registerUser(payload) {
  return post('/users/register', payload)
}

export function getAuthToken() {
  return getStoredValue(AUTH_TOKEN_KEY)
}

export function isAuthenticated() {
  return Boolean(getAuthToken())
}

export function logout() {
  removeStoredValue(AUTH_TOKEN_KEY)
  removeStoredValue(USER_ID_KEY)
  removeStoredValue(USER_EMAIL_KEY)
  removeStoredValue('adminAuth')
}

export function clearAuthSession() {
  removeStoredValue(AUTH_TOKEN_KEY)
  removeStoredValue(USER_ID_KEY)
  removeStoredValue(USER_EMAIL_KEY)
  removeStoredValue('adminAuth')
}

export function getStoredUserId() {
  const userId = getStoredValue(USER_ID_KEY)
  return userId && userId.trim() ? userId : ''
}
