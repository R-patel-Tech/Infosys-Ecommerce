import { useAuthSession } from '../hooks/useAuthSession.js'

function ProtectedRoute({ children, onRequireAuth }) {
  const authSession = useAuthSession()

  if (!authSession.isAuthenticated()) {
    onRequireAuth()
    return null
  }

  return children
}

export default ProtectedRoute
