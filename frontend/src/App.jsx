import { useState } from 'react'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import AdminLogin from './pages/AdminLogin.jsx'
import Dashboard from './pages/Dashboard.jsx'
import ProductListing from './pages/ProductListing.jsx'
import ProductDetails from './pages/ProductDetails.jsx'
import ProductAdmin from './pages/ProductAdmin.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'
import { isAuthenticated, logout as clearAuth } from './services/authService.js'

function App() {
  const [activePage, setActivePage] = useState(isAuthenticated() ? 'dashboard' : 'login')
  const [selectedProductId, setSelectedProductId] = useState(null)

  function handleLoginSuccess() {
    setActivePage('dashboard')
  }

  function handleAdminLoginSuccess() {
    setActivePage('admin-dashboard') // Assuming admin dashboard, but for now, use dashboard
  }

  function handleLogout() {
    clearAuth()
    setActivePage('login')
  }

  function handleShowProducts() {
    setActivePage('products')
  }

  function handleShowProductDetails(productId) {
    setSelectedProductId(productId)
    setActivePage('product-details')
  }

  function handleShowAdmin() {
    setActivePage('admin-products')
  }

  return (
    <div className="app-shell">
      {activePage === 'login' ? (
        <Login onSwitch={setActivePage} onLoginSuccess={handleLoginSuccess} />
      ) : null}
      {activePage === 'register' ? <Register onSwitch={setActivePage} /> : null}
      {activePage === 'admin-login' ? (
        <AdminLogin onSwitch={setActivePage} onAdminLoginSuccess={handleAdminLoginSuccess} />
      ) : null}
      {activePage === 'dashboard' ? (
        <ProtectedRoute onRequireAuth={() => setActivePage('login')}>
          <Dashboard
            onLogout={handleLogout}
            onShowProducts={handleShowProducts}
            onShowAdmin={handleShowAdmin}
          />
        </ProtectedRoute>
      ) : null}
      {activePage === 'products' ? (
        <ProtectedRoute onRequireAuth={() => setActivePage('login')}>
          <ProductListing onBack={() => setActivePage('dashboard')} onShowDetails={handleShowProductDetails} />
        </ProtectedRoute>
      ) : null}
      {activePage === 'product-details' ? (
        <ProtectedRoute onRequireAuth={() => setActivePage('login')}>
          <ProductDetails productId={selectedProductId} onBack={() => setActivePage('products')} />
        </ProtectedRoute>
      ) : null}
      {activePage === 'admin-products' ? (
        <ProtectedRoute onRequireAuth={() => setActivePage('login')}>
          <ProductAdmin onBack={() => setActivePage('dashboard')} />
        </ProtectedRoute>
      ) : null}
    </div>
  )
}

export default App
