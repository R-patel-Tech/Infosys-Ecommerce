import { Navigate, Route, Routes, useNavigate } from 'react-router-dom'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import AdminLogin from './pages/AdminLogin.jsx'
import Dashboard from './pages/Dashboard.jsx'
import ProductAdmin from './pages/ProductAdmin.jsx'
import ProductListing from './pages/ProductListing.jsx'
import ProductDetails from './pages/ProductDetails.jsx'
import Cart from './pages/Cart.jsx'
import Checkout from './pages/Checkout.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'
import { isAuthenticated, logout as clearAuth } from './services/authService.js'
import { getStoredUserId } from './services/cartService.js'
import ToastHost from './components/ToastHost.jsx'
import { showToast } from './utils/toast.js'

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
    const userId = getStoredUserId()

    if (!userId) {
      showToast('Please login to view your cart', 'error')
      return
    }

    navigate('/cart')
  }

  function handleShowProductDetails(productId) {
    navigate(`/products/${encodeURIComponent(productId)}`)
  }

  function redirectAfterAuth() {
    return <Navigate to={isAuthenticated() ? '/dashboard' : '/login'} replace />
  }

  return (
    <>
      <ToastHost />
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
        <Route path="/cart" element={<Cart onBack={() => navigate('/dashboard')} />} />
        <Route path="/checkout" element={<Checkout onBack={() => navigate('/cart')} />} />
        <Route path="*" element={<Navigate to={isAuthenticated() ? '/dashboard' : '/login'} replace />} />
      </Routes>
    </>
  )
}

export default App
