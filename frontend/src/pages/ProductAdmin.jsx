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

function ProductAdmin({ onBack }) {
  const [products, setProducts] = useState([])
  const [formData, setFormData] = useState(initialFormState)
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')
  const [feedback, setFeedback] = useState('')
  const [editingProduct, setEditingProduct] = useState(null)
  const [clearingAll, setClearingAll] = useState(false)

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
    if (!window.confirm('Are you sure you want to delete this product?')) {
      return
    }

    setError('')
    setFeedback('')

    try {
      await fetch(`${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'}/products/${productId}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          ...(sessionStorage.getItem('authToken') ? { Authorization: `Bearer ${sessionStorage.getItem('authToken')}` } : {}),
        },
      })

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
      await fetch(`${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'}/products`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          ...(sessionStorage.getItem('authToken') ? { Authorization: `Bearer ${sessionStorage.getItem('authToken')}` } : {}),
        },
      })

      setProducts([])
      setFeedback('All products cleared successfully.')
    } catch (err) {
      setError('Unable to clear all products.')
    } finally {
      setClearingAll(false)
    }
  }

  return (
    <main className="page-shell detail-shell">
      <section className="dashboard-card admin-shell">
        <div className="dashboard-header">
          <div>
            <p className="eyebrow">Admin</p>
            <h1>Product management</h1>
            <p className="dashboard-copy">
              Create new products and review what is already in the catalog.
            </p>
          </div>

          <div className="dashboard-actions">
            <Button type="button" onClick={onBack}>
              Back
            </Button>
          </div>
        </div>

        <div className="admin-layout">
          <form className="admin-form" onSubmit={editingProduct ? handleUpdate : handleSubmit}>
            <div className="admin-form-header">
              <h2>{editingProduct ? 'Edit product' : 'Add product'}</h2>
              <p>Fields marked with an asterisk are required by the backend.</p>
              {editingProduct && (
                <Button type="button" onClick={handleCancelEdit} variant="secondary">
                  Cancel Edit
                </Button>
              )}
            </div>

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
                rows="4"
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

            <Button type="submit" disabled={submitting}>
              {submitting ? 'Saving...' : editingProduct ? 'Update Product' : 'Create Product'}
            </Button>

            {error ? <p className="product-state product-state-error">{error}</p> : null}
            {feedback ? <p className="product-state">{feedback}</p> : null}
          </form>

          <div className="admin-panel">
            <div className="product-listing-header">
              <div>
                <h2>Catalog preview</h2>
                <p className="product-count">{products.length} products</p>
              </div>
              <div className="admin-actions">
                <Button type="button" onClick={loadProducts} disabled={loading}>
                  Refresh
                </Button>
                {products.length > 0 && (
                  <Button
                    type="button"
                    onClick={handleClearAll}
                    disabled={clearingAll}
                    variant="danger"
                  >
                    {clearingAll ? 'Clearing...' : 'Clear All'}
                  </Button>
                )}
              </div>
            </div>

            {loading ? (
              <p className="product-state">Loading products...</p>
            ) : products.length === 0 ? (
              <p className="product-state">No products available yet.</p>
            ) : (
              <div className="product-grid">
                {products.map((product, index) => {
                  const key = product.productId ?? product.id ?? `${product.name ?? 'product'}-${index}`
                  const imageSrc = getProductImage(product)

                  return (
                    <article className="product-card admin-product-card" key={key}>
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
                      <div className="product-actions">
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
                          onClick={() => handleDelete(product.productId ?? product.id)}
                          size="small"
                          variant="danger"
                        >
                          Delete
                        </Button>
                      </div>
                    </article>
                  )
                })}
              </div>
            )}
          </div>
        </div>
      </section>
    </main>
  )
}

export default ProductAdmin
