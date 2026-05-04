import { useEffect, useState } from 'react'
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

function Dashboard({ onLogout, onShowProducts, onShowAdmin }) {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

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

  return (
    <main className="page-shell dashboard-shell">
      <section className="dashboard-card">
        <div className="dashboard-header">
          <div>
            <p className="eyebrow">Protected dashboard</p>
            <h1>Welcome back</h1>
          </div>

          <div className="dashboard-actions">
            <Button onClick={onShowProducts}>Browse products</Button>
            <Button onClick={onShowAdmin}>Product admin</Button>
            <Button onClick={onLogout}>Logout</Button>
          </div>
        </div>

        <div className="product-listing">
          <div className="product-listing-header">
            <h2>Product Catalog</h2>
            <p className="product-count">{products.length} products found</p>
          </div>

          {loading ? (
            <p>Loading products...</p>
          ) : error ? (
            <p className="alert alert-error">{error}</p>
          ) : products.length === 0 ? (
            <p>No products available.</p>
          ) : (
            <div className="product-grid">
              {products.map((product, index) => {
                const key = product.productId ?? product.id ?? `${product.name ?? 'product'}-${index}`
                const imageSrc = getProductImage(product)

                return (
                  <article className="product-card" key={key}>
                    <div className="product-media">
                      <img
                        className="product-image"
                        src={imageSrc}
                        alt={product.name ?? 'Product image'}
                        loading="lazy"
                      />
                    </div>
                    <h3>{product.name ?? 'Unnamed product'}</h3>
                    <p>{product.description || 'No description available.'}</p>
                    <div className="product-meta">
                      <span className="product-category">
                        {product.category || 'Uncategorized'}
                      </span>
                      <span className="product-price">{formatPrice(product.price)}</span>
                    </div>
                    <p className="product-stock">Stock: {product.stockQuantity ?? product.stock ?? 'N/A'}</p>
                  </article>
                )
              })}
            </div>
          )}
        </div>
      </section>
    </main>
  )
}

export default Dashboard
