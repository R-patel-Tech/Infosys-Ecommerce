import { get, post, put } from './api.js'

export async function getMyProfile() {
  const data = await get('/users/me')
  return data?.data ?? data
}

export async function updateMyProfile(payload) {
  const data = await put('/users/update-profile', payload)
  return data?.data ?? data
}

export async function updateMyPassword(payload) {
  const data = await put('/users/update-password', payload)
  return data?.data ?? data
}

export async function logoutUser() {
  try {
    await post('/users/logout', {})
  } catch {
    // Local logout still succeeds even if server-side revocation fails.
  }
}
