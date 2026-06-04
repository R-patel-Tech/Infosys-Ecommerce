import {
  clearAuthSession,
  getAuthToken,
  getStoredUserEmail,
  getStoredUserId,
  hasValidAuthToken,
  isAdminAuthenticated,
  saveAuthSession,
  setAdminAuthenticated,
} from '../utils/session.js'
import { loginUser as apiLoginUser, registerUser as apiRegisterUser } from '../services/authService.js'
import { logoutUser as apiLogoutUser } from '../services/userService.js'

export function useAuthSession() {
  function login(credentials) {
    return apiLoginUser(credentials)
  }

  function register(payload) {
    return apiRegisterUser(payload)
  }

  async function logout() {
    try {
      await apiLogoutUser()
    } finally {
      clearAuthSession()
      setAdminAuthenticated(false)
    }
  }

  return {
    login,
    register,
    logout,
    getAuthToken,
    getStoredUserId,
    getStoredUserEmail,
    isAuthenticated: () => hasValidAuthToken(),
    isAdminAuthenticated,
    saveAuthSession,
    setAdminAuthenticated,
  }
}
