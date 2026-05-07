import { Navigate, Route, Routes, useNavigate } from 'react-router-dom'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import AdminLogin from './pages/AdminLogin.jsx'
import Dashboard from './pages/Dashboard.jsx'
import ProductAdmin from './pages/ProductAdmin.jsx'
import ProductListing from './pages/ProductListing.jsx'
import ProductDetails from './pages/ProductDetails.jsx'
import Cart from './pages/Cart.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'
import { isAuthenticated, logout as clearAuth } from './services/authService.js'

function getStoredUserId() {
  const sessionUserId = sessionStorage.getItem('userId')
  if (sessionUserId) {
    return sessionUserId
  }

  const localUserId = localStorage.getItem('userId')
  if (localUserId) {
    return localUserId
  }

  return ''
}

function isAdminAuthenticated() {
  return sessionStorage.getItem('adminAuth') === 'true'
}

function App() {
  const navigate = useNavigate()

  function handleLoginSuccess() {
    sessionStorage.removeItem('adminAuth')
    navigate('/dashboard', { replace: true })
  }

  function handleAdminLoginSuccess() {
    sessionStorage.setItem('adminAuth', 'true')
    navigate('/admin-dashboard', { replace: true })
  }

  function handleLogout() {
    clearAuth()
    sessionStorage.removeItem('adminAuth')
    navigate('/login', { replace: true })
  }

  function handleShowProducts() {
    navigate('/products')
  }

  function handleShowCart() {
    const storedUserId = getStoredUserId()
    navigate(storedUserId ? `/cart/${storedUserId}` : '/cart')
  }

  function handleShowProductDetails(productId) {
    navigate(`/products/${encodeURIComponent(productId)}`)
  }

  function redirectAfterAuth() {
    return <Navigate to={isAuthenticated() ? '/dashboard' : '/login'} replace />
  }

  return (
    <Routes>
      <Route path="/" element={redirectAfterAuth()} />
      <Route
        path="/login"
        element={
          <Login
            onSwitch={(nextPage) => navigate(`/${nextPage}`)}
            onLoginSuccess={handleLoginSuccess}
          />
        }
      />
      <Route
        path="/register"
        element={<Register onSwitch={(nextPage) => navigate(`/${nextPage}`)} />}
      />
      <Route
        path="/admin-login"
        element={
          <AdminLogin
            onSwitch={(nextPage) => navigate(`/${nextPage}`)}
            onAdminLoginSuccess={handleAdminLoginSuccess}
          />
        }
      />
      <Route
        path="/admin-dashboard"
        element={
          isAdminAuthenticated() ? (
            <ProductAdmin
              onBack={() => {
                sessionStorage.removeItem('adminAuth')
                navigate('/admin-login', { replace: true })
              }}
            />
          ) : (
            <Navigate to="/admin-login" replace />
          )
        }
      />
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute onRequireAuth={() => navigate('/login', { replace: true })}>
            <Dashboard
              onLogout={handleLogout}
              onShowProducts={handleShowProducts}
              onShowCart={handleShowCart}
            />
          </ProtectedRoute>
        }
      />
      <Route
        path="/products"
        element={
          <ProtectedRoute onRequireAuth={() => navigate('/login', { replace: true })}>
            <ProductListing
              onBack={() => navigate('/dashboard')}
              onShowDetails={handleShowProductDetails}
            />
          </ProtectedRoute>
        }
      />
      <Route
        path="/products/:productId"
        element={
          <ProtectedRoute onRequireAuth={() => navigate('/login', { replace: true })}>
            <ProductDetails onBack={() => navigate('/products')} />
          </ProtectedRoute>
        }
      />
      <Route
        path="/cart"
        element={
          <ProtectedRoute onRequireAuth={() => navigate('/login', { replace: true })}>
            <Cart onBack={() => navigate('/dashboard')} onLogin={() => navigate('/login')} />
          </ProtectedRoute>
        }
      />
      <Route
        path="/cart/:userId"
        element={
          <ProtectedRoute onRequireAuth={() => navigate('/login', { replace: true })}>
            <Cart onBack={() => navigate('/dashboard')} onLogin={() => navigate('/login')} />
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<Navigate to={isAuthenticated() ? '/dashboard' : '/login'} replace />} />
    </Routes>
  )
}

export default App
