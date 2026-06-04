import { useAuthSession } from '../hooks/useAuthSession.js'
import { Navigate } from 'react-router-dom'

function ProtectedRoute({ children }) {
  const authSession = useAuthSession()

  if (!authSession.isAuthenticated()) {
    return <Navigate to="/login" replace />
  }

  return children
}

export default ProtectedRoute
