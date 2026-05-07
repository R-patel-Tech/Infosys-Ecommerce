import { useEffect, useState } from 'react'
import { Link, NavLink } from 'react-router-dom'
import Button from '../components/Button.jsx'
import { get } from '../services/api.js'
import { getProductImage } from '../utils/productImage.js'

function normalizeProducts(data) {
  if (Array.isArray(data)) {
    return data
  }

  if (Array.isArray(data?.products)) {
    return data.products
  }

  return []
}

function formatPrice(price) {
  const value = Number(price)
  return Number.isFinite(value) ? `$${value.toFixed(2)}` : 'Price unavailable'
}

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

function CartIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="nav-icon">
      <path
        d="M3 5h2l2.2 9.2A2 2 0 0 0 9.15 16h7.9a2 2 0 0 0 1.95-1.55L21 8H7"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <circle cx="10" cy="20" r="1.5" fill="currentColor" />
      <circle cx="17" cy="20" r="1.5" fill="currentColor" />
    </svg>
  )
}

function UserIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="nav-icon">
      <path
        d="M20 21a8 8 0 0 0-16 0"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
      />
      <circle
        cx="12"
        cy="8"
        r="4"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
      />
    </svg>
  )
}

function MenuIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="nav-icon">
      <path
        d="M4 7h16M4 12h16M4 17h16"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
      />
    </svg>
  )
}

function CloseIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="nav-icon">
      <path
        d="M6 6l12 12M18 6L6 18"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
      />
    </svg>
  )
}

function Dashboard({ onLogout, onShowProducts, onShowCart }) {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [menuOpen, setMenuOpen] = useState(false)
  const cartPath = getStoredUserId() ? `/cart/${getStoredUserId()}` : '/cart'
  const totalProducts = products.length
  const totalCategories = new Set(
    products.map((product) => product.category?.trim()).filter(Boolean)
  ).size
  const lowStockCount = products.filter((product) => {
    const stock = Number(product.stockQuantity ?? product.stock ?? 0)
    return Number.isFinite(stock) && stock <= 5
  }).length

  useEffect(() => {
    async function fetchProducts() {
      setLoading(true)
      setError('')
      try {
        const data = await get('/products')
        setProducts(normalizeProducts(data))
      } catch (err) {
        setError(err.message || 'Unable to load products.')
      } finally {
        setLoading(false)
      }
    }

    fetchProducts()
  }, [])

  function closeMenu() {
    setMenuOpen(false)
  }

  function handleShowProducts() {
    closeMenu()
  }

  function handleShowCart() {
    closeMenu()
  }

  function handleLogout() {
    closeMenu()
    if (typeof onLogout === 'function') {
      onLogout()
    }
  }

  const navLinkClass = ({ isActive }) =>
    `dashboard-nav-link ${isActive ? 'active' : ''}`

  return (
    <main className="dashboard-page">
      <header className="dashboard-navbar">
        <div className="navbar-brand">
          <Link to="/dashboard" className="brand-link" onClick={closeMenu}>
            <span className="brand-mark">R</span>
            <span className="brand-copy">
              <strong>Raj_ecommerce</strong>
              <small>Shopping dashboard</small>
            </span>
          </Link>
        </div>

        <nav className={`navbar-links ${menuOpen ? 'open' : ''}`} aria-label="Primary">
          <NavLink to="/dashboard" className={navLinkClass} onClick={closeMenu} end>
            Dashboard
          </NavLink>
          <NavLink to="/products" className={navLinkClass} onClick={handleShowProducts}>
            Browse Products
          </NavLink>
          <NavLink to={cartPath} className={navLinkClass} onClick={handleShowCart}>
            <CartIcon />
            <span>View Cart</span>
          </NavLink>
        </nav>

        <div className="navbar-actions">
          <button type="button" className="profile-pill" aria-label="User profile">
            <UserIcon />
            <span>Profile</span>
          </button>
          <Button type="button" variant="secondary" className="navbar-logout" onClick={handleLogout}>
            Logout
          </Button>
          <button
            type="button"
            className="navbar-toggle"
            onClick={() => setMenuOpen((current) => !current)}
            aria-label={menuOpen ? 'Close menu' : 'Open menu'}
            aria-expanded={menuOpen}
          >
            {menuOpen ? <CloseIcon /> : <MenuIcon />}
          </button>
        </div>

        <div className={`navbar-mobile-panel ${menuOpen ? 'open' : ''}`}>
          <NavLink to="/dashboard" className={navLinkClass} onClick={closeMenu} end>
            Dashboard
          </NavLink>
          <NavLink to="/products" className={navLinkClass} onClick={handleShowProducts}>
            Browse Products
          </NavLink>
          <NavLink to={cartPath} className={navLinkClass} onClick={handleShowCart}>
            <CartIcon />
            <span>View Cart</span>
          </NavLink>
          <button type="button" className="mobile-profile" onClick={closeMenu}>
            <UserIcon />
            <span>Profile</span>
          </button>
          <Button type="button" variant="secondary" className="mobile-logout" onClick={handleLogout}>
            Logout
          </Button>
        </div>
      </header>

      <div className="dashboard-scroll-shell">
        <section className="dashboard-hero-shell">
          <div className="dashboard-hero-copy">
            <p className="eyebrow">Protected dashboard</p>
            <h1>Welcome back</h1>
            <p className="dashboard-copy">
              Browse the catalog, inspect products, and manage your cart from one clean workspace.
            </p>
          </div>

          <div className="dashboard-hero-actions">
            <Button type="button" onClick={onShowProducts}>
              Browse Products
            </Button>
            <Button type="button" variant="secondary" onClick={onShowCart}>
              View Cart
            </Button>
          </div>
        </section>

        <section className="dashboard-metrics" aria-label="Store metrics">
          <article className="dashboard-metric-card">
            <span className="dashboard-metric-label">Products</span>
            <strong className="dashboard-metric-value">{totalProducts}</strong>
            <span className="dashboard-metric-subtitle">Live items in the catalog</span>
          </article>
          <article className="dashboard-metric-card">
            <span className="dashboard-metric-label">Categories</span>
            <strong className="dashboard-metric-value">{totalCategories}</strong>
            <span className="dashboard-metric-subtitle">Distinct groups available</span>
          </article>
          <article className="dashboard-metric-card">
            <span className="dashboard-metric-label">Low stock</span>
            <strong className="dashboard-metric-value">{lowStockCount}</strong>
            <span className="dashboard-metric-subtitle">Products needing attention</span>
          </article>
        </section>

        <section className="dashboard-card dashboard-panel">
          <div className="product-listing">
            <div className="product-listing-header">
              <div>
                <p className="eyebrow">Catalog</p>
                <h2>Product feed</h2>
              </div>
              <p className="product-count">{products.length} products found</p>
            </div>

            {loading ? (
              <div className="product-grid product-grid-loading" aria-label="Loading products">
                {Array.from({ length: 6 }).map((_, index) => (
                  <article className="product-card product-card-skeleton" key={index}>
                    <div className="product-media skeleton-box" />
                    <div className="skeleton-line" />
                    <div className="skeleton-line short" />
                    <div className="skeleton-chip-row">
                      <div className="skeleton-chip" />
                      <div className="skeleton-chip" />
                    </div>
                  </article>
                ))}
              </div>
            ) : error ? (
              <p className="alert alert-error">{error}</p>
            ) : products.length === 0 ? (
              <div className="dashboard-empty-state">
                <h3>No products available.</h3>
                <p>Once products are added from the backend, they will appear here automatically.</p>
              </div>
            ) : (
              <div className="product-grid">
                {products.map((product, index) => {
                  const key = product.productId ?? product.id ?? `${product.name ?? 'product'}-${index}`
                  const imageSrc = getProductImage(product)
                  const stock = Number(product.stockQuantity ?? product.stock ?? 0)
                  const isLowStock = Number.isFinite(stock) && stock <= 5

                  return (
                    <article className="product-card product-card-modern" key={key}>
                      <div className="product-media product-media-modern">
                        <img
                          className="product-image"
                          src={imageSrc}
                          alt={product.name ?? 'Product image'}
                          loading="lazy"
                        />
                        <span className={`product-chip ${isLowStock ? 'warning' : 'success'}`}>
                          {isLowStock ? 'Low stock' : 'In stock'}
                        </span>
                      </div>
                      <div className="product-card-body">
                        <div className="product-card-head">
                          <h3>{product.name ?? 'Unnamed product'}</h3>
                          <span className="product-price">{formatPrice(product.price)}</span>
                        </div>
                        <p>{product.description || 'No description available.'}</p>
                        <div className="product-meta">
                          <span className="product-category">
                            {product.category || 'Uncategorized'}
                          </span>
                          <span className="product-stock">
                            Stock: {product.stockQuantity ?? product.stock ?? 'N/A'}
                          </span>
                        </div>
                      </div>
                    </article>
                  )
                })}
              </div>
            )}
          </div>
        </section>
      </div>
    </main>
  )
}

export default Dashboard
