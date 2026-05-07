import axios from 'axios'
import { API_BASE_URL } from '../config.js'

const axiosClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: false,
  timeout: 15000,
})

axiosClient.interceptors.request.use((config) => {
  const token = sessionStorage.getItem('authToken')

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
    const requestError = new Error(message)
    requestError.status = error?.response?.status
    requestError.data = error?.response?.data
    requestError.originalError = error
    return Promise.reject(requestError)
  }
)

export default axiosClient
