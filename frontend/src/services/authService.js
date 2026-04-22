import { post } from './api.js'

export function loginUser(credentials) {
  return post('/api/users/login', credentials)
}

export function registerUser(payload) {
  return post('/api/users/register', payload)
}
