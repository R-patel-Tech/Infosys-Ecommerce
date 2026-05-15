import axios from 'axios'
import { API_BASE_URL } from '../config.js'
import { getAuthToken } from '../services/authService.js'

function clearAuthSession() {
  localStorage.removeItem('authToken')
  localStorage.removeItem('userId')
  localStorage.removeItem('userEmail')
  localStorage.removeItem('adminAuth')
  sessionStorage.removeItem('authToken')
  sessionStorage.removeItem('userId')
  sessionStorage.removeItem('userEmail')
  sessionStorage.removeItem('adminAuth')
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
  const token = getAuthToken()

  if (token) {
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

    if (error?.response?.status === 401 && typeof window !== 'undefined' && window.location.pathname !== '/login') {
      clearAuthSession()
      window.location.replace('/login')
    }

    const requestError = new Error(message)
    requestError.status = error?.response?.status
    requestError.data = error?.response?.data
    requestError.originalError = error
    return Promise.reject(requestError)
  }
)

export default axiosClient
