import { isJwtValid } from './jwt.js'

const AUTH_TOKEN_KEY = 'authToken'
const USER_ID_KEY = 'userId'
const USER_EMAIL_KEY = 'userEmail'
const ADMIN_AUTH_KEY = 'adminAuth'
const LAST_ORDER_KEY = 'lastOrder'

function safeStorageRead(storage, key) {
  try {
    return storage.getItem(key)
  } catch {
    return null
  }
}

function safeStorageWrite(storage, key, value) {
  try {
    storage.setItem(key, value)
  } catch {
    // Ignore storage quota / privacy mode failures.
  }
}

function safeStorageRemove(storage, key) {
  try {
    storage.removeItem(key)
  } catch {
    // Ignore storage access failures.
  }
}

export function getStoredValue(key) {
  return safeStorageRead(localStorage, key) || safeStorageRead(sessionStorage, key) || ''
}

export function setStoredValue(key, value) {
  const nextValue = String(value)
  safeStorageWrite(localStorage, key, nextValue)
  safeStorageWrite(sessionStorage, key, nextValue)
}

export function removeStoredValue(key) {
  safeStorageRemove(localStorage, key)
  safeStorageRemove(sessionStorage, key)
}

export function saveAuthSession({ token, userId, email }) {
  if (token) {
    setStoredValue(AUTH_TOKEN_KEY, token)
  }

  if (userId !== null && userId !== undefined && userId !== '') {
    setStoredValue(USER_ID_KEY, userId)
  }

  if (email) {
    setStoredValue(USER_EMAIL_KEY, email)
  }
}

export function clearAuthSession() {
  removeStoredValue(AUTH_TOKEN_KEY)
  removeStoredValue(USER_ID_KEY)
  removeStoredValue(USER_EMAIL_KEY)
  removeStoredValue(ADMIN_AUTH_KEY)
}

export function getAuthToken() {
  return getStoredValue(AUTH_TOKEN_KEY)
}

export function hasValidAuthToken() {
  const token = getAuthToken()

  return Boolean(token) && isJwtValid(token)
}

export function getStoredUserId() {
  const userId = getStoredValue(USER_ID_KEY)
  return userId && userId.trim() ? userId : ''
}

export function getStoredUserEmail() {
  const userEmail = getStoredValue(USER_EMAIL_KEY)
  return userEmail && userEmail.trim() ? userEmail : ''
}

export function isAdminAuthenticated() {
  return getStoredValue(ADMIN_AUTH_KEY) === 'true'
}

export function setAdminAuthenticated(isAuthenticated) {
  if (isAuthenticated) {
    setStoredValue(ADMIN_AUTH_KEY, 'true')
    return
  }

  removeStoredValue(ADMIN_AUTH_KEY)
}

export function saveLastOrder(value) {
  if (!value) {
    removeStoredValue(LAST_ORDER_KEY)
    return
  }

  setStoredValue(LAST_ORDER_KEY, JSON.stringify(value))
}

export function readLastOrder() {
  const savedOrder = getStoredValue(LAST_ORDER_KEY)
  if (!savedOrder) {
    return null
  }

  try {
    return JSON.parse(savedOrder)
  } catch {
    return null
  }
}
