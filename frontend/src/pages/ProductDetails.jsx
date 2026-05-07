import { useEffect, useState } from 'react'
import axios from 'axios'
import Button from '../components/Button.jsx'
import { API_BASE_URL } from '../config.js'
import { getProductImage } from '../utils/productImage.js'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

function formatPrice(price) {
  const value = Number(price)
  return Number.isFinite(value) ? `$${value.toFixed(2)}` : 'Price unavailable'
}

function getProductIdFromUrl() {
  const match = window.location.pathname.match(/\/products\/([^/]+)\/?$/)
  return match ? decodeURIComponent(match[1]) : ''
}

function ProductDetails({ productId, onBack }) {
  const [product, setProduct] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [notFound, setNotFound] = useState(false)

  useEffect(() => {
    let isMounted = true
    const resolvedProductId = productId || getProductIdFromUrl()

    async function fetchProduct() {
      setLoading(true)
      setError('')
      setNotFound(false)
      setProduct(null)

      try {
        const response = await api.get(`/products/${resolvedProductId}`)

        if (!isMounted) {
          return
        }

        const nextProduct = response.data?.product ?? response.data

        if (!nextProduct) {
          setNotFound(true)
          return
        }

        setProduct(nextProduct)
      } catch (err) {
        if (!isMounted) {
          return
        }

        if (err?.response?.status === 404) {
          setNotFound(true)
          return
        }

        setError(err.message || 'Unable to load product details.')
      } finally {
        if (isMounted) {
          setLoading(false)
        }
      }
    }

    if (resolvedProductId) {
      fetchProduct()
    } else {
      setLoading(false)
      setNotFound(true)
    }

    return () => {
      isMounted = false
    }
  }, [productId])

  if (loading) {
    return (
      <main className="page-shell dashboard-shell">
        <section className="dashboard-card product-details-card">
          <div className="dashboard-header">
            <div>
              <p className="eyebrow">Product details</p>
              <h1 className="skeleton skeleton-title" />
            </div>
            <Button type="button" onClick={onBack} disabled>
              Back
            </Button>
          </div>

          <div className="product-details">
            <div className="product-media-large">
              <div className="skeleton skeleton-media" />
            </div>

            <div className="product-info">
              <div className="product-meta">
                <span className="skeleton skeleton-chip" />
                <span className="skeleton skeleton-price" />
              </div>

              <div className="product-description">
                <h3>Description</h3>
                <div className="skeleton skeleton-line" />
                <div className="skeleton skeleton-line short" />
              </div>

              <div className="product-stock-info">
                <h3>Availability</h3>
                <div className="skeleton skeleton-line small" />
              </div>

              <div className="product-actions">
                <Button type="button" disabled>
                  Add to Cart
                </Button>
              </div>
            </div>
          </div>
        </section>
      </main>
    )
  }

  if (notFound) {
    return (
      <main className="page-shell dashboard-shell">
        <section className="dashboard-card">
          <div className="dashboard-header">
            <div>
              <p className="eyebrow">Product details</p>
              <h1>Product not found</h1>
            </div>
            <Button type="button" onClick={onBack}>
              Back
            </Button>
          </div>
          <p className="product-state">
            We could not find this product in the backend. It may have been removed or the URL is invalid.
          </p>
        </section>
      </main>
    )
  }

  if (error) {
    return (
      <main className="page-shell dashboard-shell">
        <section className="dashboard-card">
          <div className="dashboard-header">
            <div>
              <p className="eyebrow">Product details</p>
              <h1>Unable to load product</h1>
            </div>
            <Button type="button" onClick={onBack}>
              Back
            </Button>
          </div>
          <p className="product-state product-state-error">{error}</p>
        </section>
      </main>
    )
  }

  if (!product) {
    return null
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
              <Button type="button" disabled={(product.stockQuantity ?? product.stock ?? 0) <= 0}>
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
