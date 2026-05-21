import { Navigate, Route, Routes, useNavigate } from 'react-router-dom'
import ToastHost from './components/ToastHost.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'
import { useAuthSession } from './hooks/useAuthSession.js'
import AdminLogin from './pages/AdminLogin.jsx'
import Cart from './pages/Cart.jsx'
import Checkout from './pages/Checkout.jsx'
import Dashboard from './pages/Dashboard.jsx'
import Login from './pages/Login.jsx'
import OrderHistory from './pages/OrderHistory.jsx'
import OrderSuccess from './pages/OrderSuccess.jsx'
import ProductAdmin from './pages/ProductAdmin.jsx'
import ProductDetails from './pages/ProductDetails.jsx'
import ProductListing from './pages/ProductListing.jsx'
import Profile from './pages/Profile.jsx'
import Register from './pages/Register.jsx'
import { showToast } from './utils/toast.js'
import { getStoredUserId, isAdminAuthenticated, setAdminAuthenticated } from './utils/session.js'

function App() {
  const navigate = useNavigate()
  const authSession = useAuthSession()

  function handleLoginSuccess() {
    setAdminAuthenticated(false)
    navigate('/dashboard', { replace: true })
  }

  function handleAdminLoginSuccess() {
    setAdminAuthenticated(true)
    navigate('/admin-dashboard', { replace: true })
  }

  async function handleLogout() {
    await authSession.logout()
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

  function handleShowOrders() {
    const userId = getStoredUserId()

    if (!userId) {
      showToast('Please login to view your order history', 'error')
      return
    }

    navigate('/orders')
  }

  function handleShowProfile() {
    navigate('/profile')
  }

  function handleShowProductDetails(productId) {
    navigate(`/products/${encodeURIComponent(productId)}`)
  }

  function redirectAfterAuth() {
    return <Navigate to={authSession.isAuthenticated() ? '/dashboard' : '/login'} replace />
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
        <Route path="/register" element={<Register onSwitch={(nextPage) => navigate(`/${nextPage}`)} />} />
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
                  setAdminAuthenticated(false)
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
                onShowOrders={handleShowOrders}
                onShowProfile={handleShowProfile}
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
              <Cart onBack={() => navigate('/dashboard')} />
            </ProtectedRoute>
          }
        />
        <Route
          path="/checkout"
          element={
            <ProtectedRoute onRequireAuth={() => navigate('/login', { replace: true })}>
              <Checkout onBack={() => navigate('/cart')} />
            </ProtectedRoute>
          }
        />
        <Route
          path="/order-success"
          element={
            <ProtectedRoute onRequireAuth={() => navigate('/login', { replace: true })}>
              <OrderSuccess />
            </ProtectedRoute>
          }
        />
        <Route
          path="/orders"
          element={
            <ProtectedRoute onRequireAuth={() => navigate('/login', { replace: true })}>
              <OrderHistory />
            </ProtectedRoute>
          }
        />
        <Route
          path="/profile"
          element={
            <ProtectedRoute onRequireAuth={() => navigate('/login', { replace: true })}>
              <Profile onLogout={handleLogout} />
            </ProtectedRoute>
          }
        />
        <Route path="*" element={<Navigate to={authSession.isAuthenticated() ? '/dashboard' : '/login'} replace />} />
      </Routes>
    </>
  )
}

export default App
