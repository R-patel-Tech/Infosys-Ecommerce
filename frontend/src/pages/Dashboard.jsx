import { useState } from 'react'
import Button from '../components/Button.jsx'
import Navbar from '../components/Navbar.jsx'
import ProductCard from '../components/ProductCard.jsx'
import { useProducts } from '../hooks/useProducts.js'

function Dashboard({ onLogout, onShowProducts, onShowCart, onShowOrders, onShowProfile }) {
  const { products, loading, error } = useProducts()
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
            <h1>Welcome back</h1>
            <p className="dashboard-copy">
              Browse the catalog, inspect products, and manage your cart from one clean workspace.
            </p>
          </div>

          <div className="dashboard-hero-actions">
            <Button type="button" onClick={handleShowProducts}>
              Browse Products
            </Button>
            <Button type="button" variant="secondary" onClick={handleShowCart}>
              View Cart
            </Button>
            <Button type="button" variant="secondary" onClick={handleShowOrders}>
              Order History
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
