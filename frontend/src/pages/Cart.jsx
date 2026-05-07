import { useEffect, useState } from 'react'
import axios from 'axios'
import { useParams } from 'react-router-dom'
import Button from '../components/Button.jsx'
import { API_BASE_URL } from '../config.js'
import { getProductImage } from '../utils/productImage.js'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

function getAuthHeaders() {
  const token = sessionStorage.getItem('authToken')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

function formatPrice(price) {
  const value = Number(price)
  return Number.isFinite(value) ? `$${value.toFixed(2)}` : '$0.00'
}

function readStoredUserId(storage, key) {
  const value = storage.getItem(key)
  if (value == null || value === '') {
    return null
  }

  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : null
}

function resolveUserId(routeUserId, explicitUserId) {
  if (routeUserId != null && routeUserId !== '') {
    const value = Number(routeUserId)
    return Number.isFinite(value) ? value : null
  }

  if (explicitUserId != null && explicitUserId !== '') {
    const value = Number(explicitUserId)
    if (Number.isFinite(value)) {
      return value
    }
  }

  const sessionUserId = readStoredUserId(window.sessionStorage, 'userId')
  if (sessionUserId != null) {
    return sessionUserId
  }

  const localUserId = readStoredUserId(window.localStorage, 'userId')
  if (localUserId != null) {
    return localUserId
  }

  return null
}

function normalizeCart(data) {
  if (!data || typeof data !== 'object') {
    return { items: [], totalAmount: 0, totalQuantity: 0 }
  }

  return {
    items: Array.isArray(data.items) ? data.items : [],
    totalAmount: data.totalAmount ?? 0,
    totalQuantity: data.totalQuantity ?? 0,
  }
}

function calculateTotals(items) {
  return items.reduce(
    (accumulator, item) => {
      const quantity = Number(item.quantity || 0)
      const unitPrice = Number(item.priceAtTime ?? item.product?.price ?? 0)
      const subtotal = Number.isFinite(Number(item.subtotal)) ? Number(item.subtotal) : unitPrice * quantity

      accumulator.totalQuantity += quantity
      accumulator.totalAmount += subtotal
      return accumulator
    },
    { totalAmount: 0, totalQuantity: 0 }
  )
}

function Cart({ onBack, onLogin, userId: explicitUserId }) {
  const params = useParams()
  const [cart, setCart] = useState({ items: [], totalAmount: 0, totalQuantity: 0 })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [actionMessage, setActionMessage] = useState('')
  const [updatingId, setUpdatingId] = useState(null)
  const [removingId, setRemovingId] = useState(null)
  const [requiresLogin, setRequiresLogin] = useState(false)
  const resolvedUserId = resolveUserId(params.userId, explicitUserId)
  const totals = calculateTotals(cart.items)
  const displayTotalAmount = Number.isFinite(Number(cart.totalAmount))
    ? Number(cart.totalAmount)
    : totals.totalAmount
  const displayTotalQuantity = Number.isFinite(Number(cart.totalQuantity))
    ? Number(cart.totalQuantity)
    : totals.totalQuantity

  async function loadCart() {
    if (!resolvedUserId) {
      setLoading(false)
      setRequiresLogin(true)
      setError('')
      return
    }

    setRequiresLogin(false)
    setLoading(true)
    setError('')
    setActionMessage('')

    try {
      const response = await api.get(`/cart/${resolvedUserId}`, {
        headers: getAuthHeaders(),
      })

      setCart(normalizeCart(response.data))
    } catch (requestError) {
      setError(requestError?.response?.data?.message || requestError.message || 'Unable to load cart.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    void loadCart()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [resolvedUserId])

  function handleLoginClick() {
    if (typeof onLogin === 'function') {
      onLogin()
      return
    }

    window.history.pushState({}, '', '/login')
    window.dispatchEvent(new PopStateEvent('popstate'))
  }

  async function updateQuantity(cartItem, nextQuantity) {
    if (!cartItem?.cartItemId || nextQuantity < 1) {
      return
    }

    setUpdatingId(cartItem.cartItemId)
    setError('')
    setActionMessage('')

    try {
      await api.put(
        '/cart/update',
        {
          cartId: cartItem.cartItemId,
          quantity: nextQuantity,
        },
        { headers: getAuthHeaders() }
      )

      await loadCart()
      setActionMessage('Cart updated successfully.')
    } catch (requestError) {
      setError(requestError?.response?.data?.message || requestError.message || 'Unable to update cart item.')
    } finally {
      setUpdatingId(null)
    }
  }

  async function removeItem(cartItem) {
    if (!cartItem?.cartItemId) {
      return
    }

    setRemovingId(cartItem.cartItemId)
    setError('')
    setActionMessage('')

    try {
      const response = await api.delete(`/cart/remove/${cartItem.cartItemId}`, {
        headers: getAuthHeaders(),
      })

      setCart(normalizeCart(response.data?.cart))
      setActionMessage(response.data?.message || 'Cart item removed successfully.')
    } catch (requestError) {
      setError(requestError?.response?.data?.message || requestError.message || 'Unable to remove cart item.')
    } finally {
      setRemovingId(null)
    }
  }

  return (
    <main className="page-shell">
      <section className="dashboard-card cart-shell">
        <div className="dashboard-header">
          <div>
            <p className="eyebrow">Cart</p>
            <h1>Your cart</h1>
            <p className="dashboard-copy">Review items, adjust quantities, or remove products before checkout.</p>
          </div>

          <div className="dashboard-actions">
            {onBack ? (
              <Button type="button" variant="secondary" onClick={onBack}>
                Back
              </Button>
            ) : null}
            <Button type="button" onClick={loadCart} disabled={loading}>
              {loading ? 'Loading...' : 'Refresh'}
            </Button>
          </div>
        </div>

        {error ? <p className="form-message error">{error}</p> : null}
        {actionMessage ? <p className="form-message success">{actionMessage}</p> : null}

        {requiresLogin ? (
          <div className="product-state cart-empty-state">
            <p>Please login to view your cart</p>
            <Button type="button" onClick={handleLoginClick}>
              Login
            </Button>
          </div>
        ) : loading ? (
          <p className="product-state">Loading cart...</p>
        ) : cart.items.length === 0 ? (
          <p className="product-state">Your cart is empty.</p>
        ) : (
          <div className="cart-layout">
            <div className="cart-items">
              {cart.items.map((item, index) => {
                const key = item.cartItemId ?? `${item.product?.productId ?? 'item'}-${index}`
                const product = item.product || {}
                const imageSrc = getProductImage(product)
                const quantity = Number(item.quantity || 0)
                const unitPrice = item.priceAtTime ?? product.price ?? 0
                const subtotal = item.subtotal ?? unitPrice * quantity

                return (
                  <article className="cart-item-card" key={key}>
                    <div className="cart-item-media">
                      <img
                        className="product-image"
                        src={imageSrc}
                        alt={product.name ?? 'Cart product'}
                        loading="lazy"
                      />
                    </div>

                    <div className="cart-item-body">
                      <div className="cart-item-header">
                        <div>
                          <h3>{product.name ?? 'Unnamed product'}</h3>
                          <p className="cart-item-category">{product.category || 'Uncategorized'}</p>
                        </div>
                        <strong className="product-price">{formatPrice(unitPrice)}</strong>
                      </div>

                      <p className="cart-item-subtotal">
                        Subtotal: {formatPrice(subtotal)}
                      </p>

                      <div className="cart-item-controls">
                        <div className="cart-quantity-controls">
                          <Button
                            type="button"
                            variant="secondary"
                            size="small"
                            disabled={quantity <= 1 || updatingId === item.cartItemId}
                            onClick={() => updateQuantity(item, quantity - 1)}
                          >
                            -
                          </Button>
                          <span className="cart-quantity-value">{quantity}</span>
                          <Button
                            type="button"
                            variant="secondary"
                            size="small"
                            disabled={updatingId === item.cartItemId}
                            onClick={() => updateQuantity(item, quantity + 1)}
                          >
                            +
                          </Button>
                        </div>

                        <Button
                          type="button"
                          variant="danger"
                          size="small"
                          onClick={() => removeItem(item)}
                          disabled={removingId === item.cartItemId}
                        >
                          {removingId === item.cartItemId ? 'Removing...' : 'Remove'}
                        </Button>
                      </div>
                    </div>
                  </article>
                )
              })}
            </div>

            <aside className="cart-summary">
              <h2>Order Summary</h2>
              <div className="cart-summary-row">
                <span>Subtotal</span>
                <strong>{formatPrice(displayTotalAmount)}</strong>
              </div>
              <div className="cart-summary-row">
                <span>Total quantity</span>
                <strong>{displayTotalQuantity}</strong>
              </div>
            </aside>
          </div>
        )}
      </section>
    </main>
  )
}

export default Cart
