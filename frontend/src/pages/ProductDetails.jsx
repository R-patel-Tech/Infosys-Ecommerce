import { useEffect, useState } from 'react'
import Button from '../components/Button.jsx'
import { get } from '../services/api.js'
import { getProductImage } from '../utils/productImage.js'

function formatPrice(price) {
  const value = Number(price)
  return Number.isFinite(value) ? `$${value.toFixed(2)}` : 'Price unavailable'
}

function ProductDetails({ productId, onBack }) {
  const [product, setProduct] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let isMounted = true

    async function fetchProduct() {
      setLoading(true)
      setError('')

      try {
        const data = await get(`/products/${productId}`)

        if (!isMounted) {
          return
        }

        setProduct(data)
      } catch (err) {
        if (!isMounted) {
          return
        }

        setError(err.message || 'Unable to load product details.')
      } finally {
        if (isMounted) {
          setLoading(false)
        }
      }
    }

    if (productId) {
      fetchProduct()
    }

    return () => {
      isMounted = false
    }
  }, [productId])

  if (loading) {
    return (
      <main className="page-shell dashboard-shell">
        <section className="dashboard-card">
          <p className="product-state">Loading product details...</p>
        </section>
      </main>
    )
  }

  if (error) {
    return (
      <main className="page-shell dashboard-shell">
        <section className="dashboard-card">
          <div className="dashboard-header">
            <h1>Product Details</h1>
            <Button onClick={onBack}>Back</Button>
          </div>
          <p className="product-state product-state-error">{error}</p>
        </section>
      </main>
    )
  }

  if (!product) {
    return (
      <main className="page-shell dashboard-shell">
        <section className="dashboard-card">
          <div className="dashboard-header">
            <h1>Product Details</h1>
            <Button onClick={onBack}>Back</Button>
          </div>
          <p className="product-state">Product not found.</p>
        </section>
      </main>
    )
  }

  const imageSrc = getProductImage(product)

  return (
    <main className="page-shell dashboard-shell">
      <section className="dashboard-card product-details-card">
        <div className="dashboard-header">
          <div>
            <p className="eyebrow">Product details</p>
            <h1>{product.name ?? 'Unnamed product'}</h1>
          </div>
          <Button onClick={onBack}>Back</Button>
        </div>

        <div className="product-details">
          <div className="product-media-large">
            <img
              className="product-image-large"
              src={imageSrc}
              alt={product.name ?? 'Product image'}
            />
          </div>

          <div className="product-info">
            <div className="product-meta">
              <span className="product-category">
                Category: {product.category || 'Uncategorized'}
              </span>
              <span className="product-price-large">{formatPrice(product.price)}</span>
            </div>

            <div className="product-description">
              <h3>Description</h3>
              <p>{product.description || 'No description available.'}</p>
            </div>

            <div className="product-stock-info">
              <h3>Availability</h3>
              <p className="product-stock">
                Stock: {product.stockQuantity ?? product.stock ?? 'N/A'}
              </p>
              {(product.stockQuantity ?? product.stock ?? 0) > 0 ? (
                <p className="stock-status in-stock">In Stock</p>
              ) : (
                <p className="stock-status out-of-stock">Out of Stock</p>
              )}
            </div>

            <div className="product-actions">
              <Button disabled={(product.stockQuantity ?? product.stock ?? 0) <= 0}>
                Add to Cart
              </Button>
            </div>
          </div>
        </div>
      </section>
    </main>
  )
}

export default ProductDetails