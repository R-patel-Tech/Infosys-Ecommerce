import axios from 'axios'
import { API_BASE_URL } from '../config.js'
import { clearAuthSession, getAuthToken, hasValidAuthToken } from '../utils/session.js'
import { isJwtExpired } from '../utils/jwt.js'

function redirectToLogin() {
  if (typeof window === 'undefined') {
    return
  }

  if (window.location.pathname !== '/login') {
    window.location.replace('/login')
  }
}

const axiosClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: false,
  timeout: 15000,
})

axiosClient.interceptors.request.use((config) => {
  if (config.skipAuthHeader) {
    return config
  }

  const token = getAuthToken()

  if (token) {
    if (!hasValidAuthToken() || isJwtExpired(token)) {
      clearAuthSession()
      redirectToLogin()
      return Promise.reject(new Error('Your session has expired. Please sign in again.'))
    }

    config.headers = config.headers || {}
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const message =
      error?.response?.data?.message ||
      error?.response?.data?.error ||
      error?.message ||
      'Request failed. Please try again.'

    if (error?.response?.status === 401) {
      clearAuthSession()
      redirectToLogin()
    }

    const requestError = new Error(message)
    requestError.status = error?.response?.status
    requestError.data = error?.response?.data
    requestError.originalError = error
    return Promise.reject(requestError)
  }
)

export default axiosClient
