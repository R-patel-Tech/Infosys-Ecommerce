const isLocalHost =
  typeof window !== 'undefined' &&
  /^(localhost|127\.0\.0\.1|::1)$/.test(window.location.hostname)

export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ??
  (isLocalHost ? 'http://localhost:8080/api' : '/api')
