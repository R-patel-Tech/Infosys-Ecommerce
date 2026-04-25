import { useState } from 'react'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import Dashboard from './pages/Dashboard.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'
import { isAuthenticated, logout as clearAuth } from './services/authService.js'

function App() {
  const [activePage, setActivePage] = useState(isAuthenticated() ? 'dashboard' : 'login')

  function handleLoginSuccess() {
    setActivePage('dashboard')
  }

  function handleLogout() {
    clearAuth()
    setActivePage('login')
  }

  return (
    <div className="app-shell">
      {activePage === 'login' ? (
        <Login onSwitch={setActivePage} onLoginSuccess={handleLoginSuccess} />
      ) : null}
      {activePage === 'register' ? <Register onSwitch={setActivePage} /> : null}
      {activePage === 'dashboard' ? (
        <ProtectedRoute onRequireAuth={() => setActivePage('login')}>
          <Dashboard onLogout={handleLogout} />
        </ProtectedRoute>
      ) : null}
    </div>
  )
}

export default App
