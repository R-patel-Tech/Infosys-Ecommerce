import { useEffect, useState } from 'react'
import Button from '../components/Button.jsx'
import { get, post, put } from '../services/api.js'
import { getProductImage } from '../utils/productImage.js'

const initialFormState = {
  name: '',
  description: '',
  price: '',
  category: '',
  imageUrl: '',
  stockQuantity: '',
}

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

function formatShortNumber(value) {
  if (!Number.isFinite(value)) {
    return '0'
  }

  return value >= 1000 ? `${Math.round(value / 100) / 10}k` : String(value)
}

function getProductKey(product) {
  return product.productId ?? product.id ?? null
}

function getInventoryStats(products) {
  const totalProducts = products.length
  const lowStockProducts = products.filter((product) => {
    const stock = Number(product.stockQuantity ?? product.stock ?? 0)
    return Number.isFinite(stock) && stock <= 5
  }).length

  const categories = new Set(
    products
      .map((product) => product.category?.trim())
      .filter(Boolean)
  )

  return {
    totalProducts,
    lowStockProducts,
    categoryCount: categories.size,
  }
}

function ProductAdmin({ onBack }) {
  const [products, setProducts] = useState([])
  const [formData, setFormData] = useState(initialFormState)
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')
  const [feedback, setFeedback] = useState('')
  const [editingProduct, setEditingProduct] = useState(null)
  const [clearingAll, setClearingAll] = useState(false)
  const stats = getInventoryStats(products)

  async function loadProducts() {
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

  useEffect(() => {
    loadProducts()
  }, [])

  function handleChange(event) {
    const { name, value } = event.target
    setFormData((current) => ({
      ...current,
      [name]: value,
    }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setFeedback('')
    setError('')

    const price = Number(formData.price)
    const stockQuantity = Number(formData.stockQuantity)

    if (!formData.name.trim()) {
      setError('Product name is required.')
      return
    }

    if (!Number.isFinite(price) || price <= 0) {
      setError('Enter a valid product price.')
      return
    }

    if (!Number.isInteger(stockQuantity) || stockQuantity < 0) {
      setError('Enter a valid stock quantity.')
      return
    }

    setSubmitting(true)

    try {
      const created = await post('/products', {
        name: formData.name.trim(),
        description: formData.description.trim(),
        price,
        category: formData.category.trim(),
        imageUrl: formData.imageUrl.trim(),
        stockQuantity,
      })

      setProducts((current) => [created, ...current])
      setFormData(initialFormState)
      setFeedback(`Created ${created.name ?? 'product'} successfully.`)
    } catch (err) {
      setError(err.message || 'Unable to create product.')
    } finally {
      setSubmitting(false)
    }
  }

  function handleEdit(product) {
    setEditingProduct(product)
    setFormData({
      name: product.name || '',
      description: product.description || '',
      price: product.price || '',
      category: product.category || '',
      imageUrl: product.imageUrl || '',
      stockQuantity: product.stockQuantity || '',
    })
    setError('')
    setFeedback('')
  }

  function handleCancelEdit() {
    setEditingProduct(null)
    setFormData(initialFormState)
    setError('')
    setFeedback('')
  }

  async function handleUpdate(event) {
    event.preventDefault()
    if (!editingProduct) return

    setFeedback('')
    setError('')

    const price = Number(formData.price)
    const stockQuantity = Number(formData.stockQuantity)

    if (!formData.name.trim()) {
      setError('Product name is required.')
      return
    }

    if (!Number.isFinite(price) || price <= 0) {
      setError('Enter a valid product price.')
      return
    }

    if (!Number.isInteger(stockQuantity) || stockQuantity < 0) {
      setError('Enter a valid stock quantity.')
      return
    }

    setSubmitting(true)

    try {
      const updated = await put(`/products/${editingProduct.productId ?? editingProduct.id}`, {
        name: formData.name.trim(),
        description: formData.description.trim(),
        price,
        category: formData.category.trim(),
        imageUrl: formData.imageUrl.trim(),
        stockQuantity,
      })

      setProducts((current) =>
        current.map((product) =>
          (product.productId ?? product.id) === (editingProduct.productId ?? editingProduct.id)
            ? updated
            : product
        )
      )
      setEditingProduct(null)
      setFormData(initialFormState)
      setFeedback(`Updated ${updated.name ?? 'product'} successfully.`)
    } catch (err) {
      setError(err.message || 'Unable to update product.')
    } finally {
      setSubmitting(false)
    }
  }

  async function handleDelete(productId) {
    if (!productId) {
      setError('Unable to delete this product because its id is missing.')
      return
    }

    if (!window.confirm('Are you sure you want to delete this product?')) {
      return
    }

    setError('')
    setFeedback('')

    try {
      const response = await fetch(`${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'}/products/${productId}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          ...(sessionStorage.getItem('authToken') ? { Authorization: `Bearer ${sessionStorage.getItem('authToken')}` } : {}),
        },
      })

      if (!response.ok) {
        throw new Error('Unable to delete product.')
      }

      setProducts((current) => current.filter((product) => (product.productId ?? product.id) !== productId))
      setFeedback('Product deleted successfully.')
    } catch (err) {
      setError('Unable to delete product.')
    }
  }

  async function handleClearAll() {
    if (!window.confirm('Are you sure you want to delete ALL products? This action cannot be undone.')) {
      return
    }

    setClearingAll(true)
    setError('')
    setFeedback('')

    try {
      const response = await fetch(`${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'}/products`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          ...(sessionStorage.getItem('authToken') ? { Authorization: `Bearer ${sessionStorage.getItem('authToken')}` } : {}),
        },
      })

      if (!response.ok) {
        throw new Error('Unable to clear products.')
      }

      setProducts([])
      setFeedback('All products cleared successfully.')
    } catch (err) {
      setError('Unable to clear all products.')
    } finally {
      setClearingAll(false)
    }
  }

  return (
    <main className="page-shell admin-page-shell">
      <section className="admin-workspace">
        <header className="admin-hero">
          <div className="admin-hero-copy">
            <p className="eyebrow">Admin console</p>
            <h1>Catalog studio</h1>
            <p className="dashboard-copy">
              Curate products, update inventory, and keep the storefront polished from one focused workspace.
            </p>
          </div>

          <div className="admin-hero-actions">
            <Button type="button" variant="secondary" onClick={onBack}>
              Back
            </Button>
            <Button type="button" onClick={loadProducts} disabled={loading}>
              {loading ? 'Refreshing...' : 'Refresh'}
            </Button>
            {products.length > 0 ? (
              <Button
                type="button"
                onClick={handleClearAll}
                disabled={clearingAll}
                variant="danger"
              >
                {clearingAll ? 'Clearing...' : 'Clear All'}
              </Button>
            ) : null}
          </div>
        </header>

        <div className="admin-stat-grid">
          <article className="admin-stat-card">
            <span className="admin-stat-label">Products</span>
            <strong className="admin-stat-value">{formatShortNumber(stats.totalProducts)}</strong>
            <span className="admin-stat-meta">Total items in the catalog</span>
          </article>
          <article className="admin-stat-card">
            <span className="admin-stat-label">Low stock</span>
            <strong className="admin-stat-value">{formatShortNumber(stats.lowStockProducts)}</strong>
            <span className="admin-stat-meta">Needs attention soon</span>
          </article>
          <article className="admin-stat-card">
            <span className="admin-stat-label">Categories</span>
            <strong className="admin-stat-value">{formatShortNumber(stats.categoryCount)}</strong>
            <span className="admin-stat-meta">Distinct groups across products</span>
          </article>
        </div>

        {error ? <p className="product-state product-state-error">{error}</p> : null}
        {feedback ? <p className="product-state product-state-success">{feedback}</p> : null}

        <div className="admin-grid">
          <aside className="admin-editor-panel">
            <div className="admin-panel-top">
              <div>
                <p className="eyebrow">Catalog editor</p>
                <h2>{editingProduct ? 'Edit product' : 'Add product'}</h2>
                <p className="admin-panel-subtitle">
                  Fields marked with an asterisk are required by the backend.
                </p>
              </div>

              {editingProduct ? (
                <Button type="button" onClick={handleCancelEdit} variant="secondary" size="small">
                  Cancel Edit
                </Button>
              ) : null}
            </div>

            <form className="admin-form-modern" onSubmit={editingProduct ? handleUpdate : handleSubmit}>
              <div className="form-group">
                <label className="form-label" htmlFor="name">
                  Product name *
                </label>
                <input
                  id="name"
                  className="form-input"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  placeholder="Example product"
                />
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="description">
                  Description
                </label>
                <textarea
                  id="description"
                  className="form-input admin-textarea"
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  placeholder="Short product description"
                  rows="5"
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label className="form-label" htmlFor="price">
                    Price *
                  </label>
                  <input
                    id="price"
                    className="form-input"
                    type="number"
                    min="0"
                    step="0.01"
                    name="price"
                    value={formData.price}
                    onChange={handleChange}
                    placeholder="19.99"
                  />
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="stockQuantity">
                    Stock quantity *
                  </label>
                  <input
                    id="stockQuantity"
                    className="form-input"
                    type="number"
                    min="0"
                    step="1"
                    name="stockQuantity"
                    value={formData.stockQuantity}
                    onChange={handleChange}
                    placeholder="25"
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label className="form-label" htmlFor="category">
                    Category
                  </label>
                  <input
                    id="category"
                    className="form-input"
                    name="category"
                    value={formData.category}
                    onChange={handleChange}
                    placeholder="Accessories"
                  />
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="imageUrl">
                    Image URL
                  </label>
                  <input
                    id="imageUrl"
                    className="form-input"
                    name="imageUrl"
                    value={formData.imageUrl}
                    onChange={handleChange}
                    placeholder="https://example.com/image.jpg"
                  />
                </div>
              </div>

              <div className="admin-form-actions">
                <Button type="submit" disabled={submitting}>
                  {submitting ? 'Saving...' : editingProduct ? 'Update Product' : 'Create Product'}
                </Button>
              </div>
            </form>
          </aside>

          <section className="admin-catalog-panel">
            <div className="admin-panel-top">
              <div>
                <p className="eyebrow">Preview board</p>
                <h2>Product library</h2>
                <p className="admin-panel-subtitle">
                  {products.length} products synced from the backend.
                </p>
              </div>
              <span className={`admin-status ${loading ? 'is-loading' : 'is-ready'}`}>
                {loading ? 'Syncing' : 'Live'}
              </span>
            </div>

            {loading ? (
              <div className="admin-loading-grid" aria-label="Loading products">
                {Array.from({ length: 4 }).map((_, index) => (
                  <article className="admin-skeleton-card" key={index}>
                    <div className="admin-skeleton-media" />
                    <div className="admin-skeleton-line short" />
                    <div className="admin-skeleton-line" />
                    <div className="admin-skeleton-line" />
                    <div className="admin-skeleton-actions">
                      <div className="admin-skeleton-pill" />
                      <div className="admin-skeleton-pill" />
                    </div>
                  </article>
                ))}
              </div>
            ) : products.length === 0 ? (
              <div className="admin-empty-state">
                <h3>No products available yet</h3>
                <p>Add your first product on the left to start building the catalog.</p>
              </div>
            ) : (
              <div className="admin-product-grid">
                {products.map((product, index) => {
                  const key = getProductKey(product) ?? `${product.name ?? 'product'}-${index}`
                  const imageSrc = getProductImage(product)
                  const stock = Number(product.stockQuantity ?? product.stock ?? 0)
                  const isLowStock = Number.isFinite(stock) && stock <= 5
                  const productId = product.productId ?? product.id

                  return (
                    <article className="admin-product-card" key={key}>
                      <div className="admin-product-media">
                        <img
                          className="product-image admin-product-image"
                          src={imageSrc}
                          alt={product.name ?? 'Product image'}
                          loading="lazy"
                        />
                        <span className={`admin-chip ${isLowStock ? 'warning' : 'success'}`}>
                          {isLowStock ? 'Low stock' : 'In stock'}
                        </span>
                      </div>

                      <div className="admin-product-body">
                        <div className="admin-product-heading">
                          <div>
                            <h3>{product.name ?? 'Unnamed product'}</h3>
                            <p className="admin-product-description">
                              {product.description || 'No description available.'}
                            </p>
                          </div>
                          <span className="product-price">{formatPrice(product.price)}</span>
                        </div>

                        <div className="admin-product-meta">
                          <span className="product-category">
                            {product.category || 'Uncategorized'}
                          </span>
                          <span className="product-stock">
                            Stock: {product.stockQuantity ?? product.stock ?? 'N/A'}
                          </span>
                        </div>

                        <div className="admin-product-actions">
                          <Button
                            type="button"
                            onClick={() => handleEdit(product)}
                            size="small"
                            variant="secondary"
                          >
                            Edit
                          </Button>
                          <Button
                            type="button"
                            onClick={() => handleDelete(productId)}
                            size="small"
                            variant="danger"
                            disabled={!productId}
                          >
                            Delete
                          </Button>
                        </div>
                      </div>
                    </article>
                  )
                })}
              </div>
            )}
          </section>
        </div>
      </section>
    </main>
  )
}

export default ProductAdmin
