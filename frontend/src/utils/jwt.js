function base64UrlDecode(input) {
  const normalized = input.replace(/-/g, '+').replace(/_/g, '/')
  const padded = normalized + '='.repeat((4 - (normalized.length % 4)) % 4)

  try {
    return atob(padded)
  } catch {
    return ''
  }
}

export function parseJwt(token) {
  if (!token || typeof token !== 'string') {
    return null
  }

  const parts = token.split('.')
  if (parts.length !== 3) {
    return null
  }

  const payload = base64UrlDecode(parts[1])
  if (!payload) {
    return null
  }

  try {
    return JSON.parse(payload)
  } catch {
    return null
  }
}

export function isJwtExpired(token, clockSkewSeconds = 30) {
  const payload = parseJwt(token)
  if (!payload?.exp) {
    return true
  }

  const now = Math.floor(Date.now() / 1000)
  return Number(payload.exp) <= now + clockSkewSeconds
}

export function isJwtValid(token, clockSkewSeconds = 30) {
  return !isJwtExpired(token, clockSkewSeconds)
}
