import { useState } from 'react'
import Button from '../components/Button.jsx'
import Navbar from '../components/Navbar.jsx'
import ProductCard from '../components/ProductCard.jsx'
import { useProducts } from '../hooks/useProducts.js'

function ShoppingBagIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="button-icon">
      <path
        d="M6 7h12l-1 12H7L6 7Z"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinejoin="round"
      />
      <path
        d="M9 7a3 3 0 0 1 6 0"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
      />
    </svg>
  )
}

function CartIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="button-icon">
      <path
        d="M3.5 5h2l1.8 9.2A2 2 0 0 0 9.3 16h8.1a2 2 0 0 0 1.9-1.4L21.4 8H7"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <circle cx="10" cy="20" r="1.4" fill="currentColor" />
      <circle cx="17" cy="20" r="1.4" fill="currentColor" />
    </svg>
  )
}

function HistoryIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="button-icon">
      <path
        d="M4 12a8 8 0 1 1 2.34 5.66"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
      />
      <path
        d="M4 4v4h4"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        d="M12 7v5l3 2"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  )
}

function GridIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="dashboard-metric-icon">
      <path d="M4 4h7v7H4zM13 4h7v7h-7zM4 13h7v7H4zM13 13h7v7h-7z" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinejoin="round" />
    </svg>
  )
}

function SparkIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="dashboard-metric-icon">
      <path
        d="M12 3l1.9 5.1L19 10l-5.1 1.9L12 17l-1.9-5.1L5 10l5.1-1.9L12 3Z"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinejoin="round"
      />
    </svg>
  )
}

function TruckIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="dashboard-metric-icon">
      <path
        d="M3 7h12v9H3zM15 10h3l3 3v3h-6z"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinejoin="round"
      />
      <circle cx="7" cy="19" r="1.5" fill="currentColor" />
      <circle cx="18" cy="19" r="1.5" fill="currentColor" />
    </svg>
  )
}

function Dashboard({ onLogout, onShowProducts, onShowCart, onShowOrders, onShowProfile }) {
  const { products, loading, error, loadProducts } = useProducts()
  const [menuOpen, setMenuOpen] = useState(false)
  const totalProducts = products.length
  const totalCategories = new Set(products.map((product) => product.category?.trim()).filter(Boolean)).size
  const lowStockCount = products.filter((product) => {
    const stock = Number(product.stockQuantity ?? product.stock ?? 0)
    return Number.isFinite(stock) && stock <= 5
  }).length

  const handleNavigation = (handler) => {
    closeMenu()
    if (typeof handler === 'function') {
      handler()
    }
  }

  function closeMenu() {
    setMenuOpen(false)
  }

  function handleShowProducts() {
    handleNavigation(onShowProducts)
  }

  function handleShowCart() {
    handleNavigation(onShowCart)
  }

  function handleShowOrders() {
    handleNavigation(onShowOrders)
  }

  function handleShowProfile() {
    handleNavigation(onShowProfile)
  }

  function handleLogout() {
    handleNavigation(onLogout)
  }

  return (
    <main className="dashboard-page">
      <header className="dashboard-navbar">
        <Navbar
          menuOpen={menuOpen}
          onToggleMenu={() => setMenuOpen((current) => !current)}
          onCloseMenu={closeMenu}
          onShowProducts={handleShowProducts}
          onShowCart={handleShowCart}
          onShowOrders={handleShowOrders}
          onShowProfile={handleShowProfile}
          onLogout={handleLogout}
        />
      </header>

      <div className="dashboard-scroll-shell">
        <section className="dashboard-hero-shell">
          <div className="dashboard-hero-copy">
            <p className="eyebrow">Protected dashboard</p>
            <div className="dashboard-hero-title-row">
              <div className="dashboard-avatar" aria-hidden="true">
                <span>R</span>
              </div>
              <div>
                <h1>Welcome back</h1>
              </div>
            </div>
            <p className="dashboard-copy">
              A calm, modern workspace for shopping, order management, and quick decisions.
            </p>
          </div>

          <div className="dashboard-hero-actions">
            <Button type="button" onClick={handleShowProducts} iconLeft={<ShoppingBagIcon />}>
              Browse Products
            </Button>
            <Button type="button" variant="secondary" onClick={handleShowCart} iconLeft={<CartIcon />}>
              View Cart
            </Button>
            <Button type="button" variant="secondary" onClick={handleShowOrders} iconLeft={<HistoryIcon />}>
              Order History
            </Button>
          </div>
        </section>

        <section className="dashboard-metrics" aria-label="Store metrics">
          <article className="dashboard-metric-card">
            <GridIcon />
            <span className="dashboard-metric-label">Products</span>
            <strong className="dashboard-metric-value">{totalProducts}</strong>
            <span className="dashboard-metric-subtitle">Live items in the catalog</span>
          </article>
          <article className="dashboard-metric-card">
            <SparkIcon />
            <span className="dashboard-metric-label">Categories</span>
            <strong className="dashboard-metric-value">{totalCategories}</strong>
            <span className="dashboard-metric-subtitle">Distinct groups available</span>
          </article>
          <article className="dashboard-metric-card">
            <TruckIcon />
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
            ) : products.length === 0 ? (
              <div className="dashboard-empty-state">
                <h3>{error ? 'Unable to load products right now.' : 'No products available.'}</h3>
                <p>
                  {error
                    ? 'The live catalog is temporarily unavailable. You can try loading again.'
                    : 'Once products are added from the backend, they will appear here automatically.'}
                </p>
                <Button type="button" variant="secondary" onClick={loadProducts}>
                  Retry
                </Button>
              </div>
            ) : (
              <div className="product-grid">
                {products.map((product, index) => {
                  const key = product.productId ?? product.id ?? `${product.name ?? 'product'}-${index}`
                  return <ProductCard key={key} product={product} />
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
