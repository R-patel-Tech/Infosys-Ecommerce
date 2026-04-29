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

function ProductListing({ onBack }) {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let isMounted = true

    async function fetchProducts() {
      setLoading(true)
      setError('')

      try {
        const data = await get('/products')

        if (!isMounted) {
          return
        }

        setProducts(normalizeProducts(data))
      } catch (err) {
        if (!isMounted) {
          return
        }

        setError(err.message || 'Unable to load products.')
      } finally {
        if (isMounted) {
          setLoading(false)
        }
      }
    }

    fetchProducts()

    return () => {
      isMounted = false
    }
  }, [])

  return (
    <main className="page-shell dashboard-shell">
      <section className="dashboard-card product-page-card">
        <div className="dashboard-header">
          <div>
            <p className="eyebrow">Product listing</p>
            <h1>Featured products</h1>
            <p className="dashboard-copy">Products are loaded dynamically from the API.</p>
          </div>
          <Button onClick={onBack}>Back</Button>
        </div>

        <div className="product-listing">
          <div className="product-listing-header">
            <h2>Available items</h2>
            {!loading && !error ? <p className="product-count">{products.length} items</p> : null}
          </div>

          {loading ? (
            <p className="product-state">Loading products...</p>
          ) : error ? (
            <p className="product-state product-state-error">{error}</p>
          ) : products.length === 0 ? (
            <p className="product-state">No products available right now.</p>
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
                    <p className="product-stock">
                      Stock: {product.stockQuantity ?? product.stock ?? 'N/A'}
                    </p>
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

export default ProductListing
