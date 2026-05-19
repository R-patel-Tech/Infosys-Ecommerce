import { useEffect, useState } from 'react'
import Button from '../components/Button.jsx'
import { addProductToCart, getStoredUserId, resolveProductId } from '../services/cartService.js'
import { get } from '../services/api.js'
import { formatCurrency } from '../utils/currency.js'
import { getProductImage } from '../utils/productImage.js'
import { showToast } from '../utils/toast.js'

function getProductIdFromUrl() {
  const match = window.location.pathname.match(/\/products\/([^/]+)\/?$/)
  return match ? decodeURIComponent(match[1]) : ''
}

function ProductDetails({ productId, onBack }) {
  const [product, setProduct] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [notFound, setNotFound] = useState(false)
  const [actionMessage, setActionMessage] = useState('')
  const [actionError, setActionError] = useState('')
  const [isAdding, setIsAdding] = useState(false)

  useEffect(() => {
    let isMounted = true
    const resolvedProductId = productId || getProductIdFromUrl()

    async function fetchProduct() {
      setLoading(true)
      setError('')
      setNotFound(false)
      setProduct(null)

      try {
        const response = await get(`/products/${resolvedProductId}`)
        const nextProduct = response?.product ?? response?.data ?? response

        if (!nextProduct) {
          setNotFound(true)
          return
        }

        setProduct(nextProduct)
      } catch (err) {
        if (!isMounted) {
          return
        }

        if (err?.status === 404 || err?.originalError?.response?.status === 404) {
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

  useEffect(() => {
    if (!actionMessage && !actionError) {
      return undefined
    }

    const timeout = window.setTimeout(() => {
      setActionMessage('')
      setActionError('')
    }, 2400)

    return () => window.clearTimeout(timeout)
  }, [actionMessage, actionError])

  async function handleAddToCart() {
    const userId = getStoredUserId()

    if (!userId) {
      setActionError('Login required to add items to cart')
      setActionMessage('')
      showToast('Please login to add items to cart', 'error')
      return
    }

    const productIdValue = resolveProductId(product)
    if (!productIdValue) {
      setActionError('This product cannot be added right now.')
      setActionMessage('')
      return
    }

    setIsAdding(true)
    setActionError('')
    setActionMessage('')

    try {
      await addProductToCart({
        userId,
        productId: productIdValue,
        quantity: 1,
      })
      setActionMessage('Added to cart')
      showToast('Item added to cart', 'success')
    } catch (err) {
      const message = err.message || 'Unable to add item to cart.'
      setActionError(message)
      showToast(message, 'error')
    } finally {
      setIsAdding(false)
    }
  }

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
  const isOutOfStock = (product.stockQuantity ?? product.stock ?? 0) <= 0

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
            <img className="product-image-large" src={imageSrc} alt={product.name ?? 'Product image'} />
          </div>

          <div className="product-info">
            <div className="product-meta">
              <span className="product-category">Category: {product.category || 'Uncategorized'}</span>
              <span className="product-price-large">{formatCurrency(product.price)}</span>
            </div>

            <div className="product-description">
              <h3>Description</h3>
              <p>{product.description || 'No description available.'}</p>
            </div>

            <div className="product-stock-info">
              <h3>Availability</h3>
              <p className="product-stock">Stock: {product.stockQuantity ?? product.stock ?? 'N/A'}</p>
              {!isOutOfStock ? (
                <p className="stock-status in-stock">In Stock</p>
              ) : (
                <p className="stock-status out-of-stock">Out of Stock</p>
              )}
            </div>

            <div className="product-actions">
              <Button type="button" disabled={isOutOfStock || isAdding} onClick={handleAddToCart}>
                {isAdding ? 'Adding...' : 'Add to Cart'}
              </Button>
            </div>

            {actionError ? <p className="form-message error">{actionError}</p> : null}
            {actionMessage ? <p className="form-message success">{actionMessage}</p> : null}
          </div>
        </div>
      </section>
    </main>
  )
}

export default ProductDetails
