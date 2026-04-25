import { isAuthenticated } from '../services/authService.js'

function ProtectedRoute({ children, onRequireAuth }) {
  if (!isAuthenticated()) {
    onRequireAuth()
    return null
  }

  return children
}

export default ProtectedRoute
