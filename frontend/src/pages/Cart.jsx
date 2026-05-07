import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Button from '../components/Button.jsx'
import {
  clearCart,
  fetchCartForUser,
  getStoredUserId,
  checkoutCart,
  removeCartItem,
  resolveCartItemId,
  resolveProductId,
  updateCartQuantity,
} from '../services/cartService.js'
import { getProductImage } from '../utils/productImage.js'
import { showToast } from '../utils/toast.js'

function formatPrice(price) {
  const value = Number(price)
  return Number.isFinite(value) ? `$${value.toFixed(2)}` : '$0.00'
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

function getCartItemId(item) {
  return item?.cartItemId ?? item?.cartId ?? item?.id ?? ''
}

function Cart({ onBack }) {
  const navigate = useNavigate()
  const [cart, setCart] = useState({ items: [], totalAmount: 0, totalQuantity: 0 })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [actionMessage, setActionMessage] = useState('')
  const [actionError, setActionError] = useState('')
  const [updatingId, setUpdatingId] = useState(null)
  const [removingId, setRemovingId] = useState(null)
  const [checkoutLoading, setCheckoutLoading] = useState(false)
  const userId = getStoredUserId()

  const totals = calculateTotals(cart.items)
  const displayTotalAmount = Number.isFinite(Number(cart.totalAmount))
    ? Number(cart.totalAmount)
    : totals.totalAmount
  const displayTotalQuantity = Number.isFinite(Number(cart.totalQuantity))
    ? Number(cart.totalQuantity)
    : totals.totalQuantity

  async function loadCart() {
    if (!userId) {
      setLoading(false)
      setCart({ items: [], totalAmount: 0, totalQuantity: 0 })
      setError('')
      setActionError('')
      return
    }

    setLoading(true)
    setError('')
    setActionMessage('')
    setActionError('')

    try {
      const nextCart = await fetchCartForUser(userId)
      setCart(nextCart)
    } catch (requestError) {
      const message = requestError?.message || 'Unable to load cart.'
      setError(message)
      showToast(message, 'error')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    void loadCart()
  }, [userId])

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

  function handleLoginClick() {
    navigate('/login')
  }

  async function handleClearCart() {
    if (!userId) {
      return
    }

    const confirmed = window.confirm('Clear all items from your cart?')
    if (!confirmed) {
      return
    }

    setLoading(true)
    setError('')
    setActionMessage('')
    setActionError('')

    try {
      await clearCart(userId)
      await loadCart()
      showToast('Cart cleared successfully', 'success')
    } catch (requestError) {
      const message = requestError?.message || 'Unable to clear cart.'
      setError(message)
      showToast(message, 'error')
    } finally {
      setLoading(false)
    }
  }

  async function handleCheckout() {
    if (!userId) {
      return
    }

    if (cart.items.length === 0) {
      showToast('Your cart is empty.', 'error')
      return
    }

    const confirmed = window.confirm('Proceed to checkout and place this order?')
    if (!confirmed) {
      return
    }

    setCheckoutLoading(true)
    setError('')
    setActionMessage('')
    setActionError('')

    try {
      await checkoutCart(userId, {
        userId,
        items: cart.items,
        totalAmount: displayTotalAmount,
        totalQuantity: displayTotalQuantity,
      })
      await loadCart()
      setActionMessage('Checkout completed successfully.')
      showToast('Checkout completed successfully', 'success')
    } catch (requestError) {
      const message = requestError?.message || 'Unable to complete checkout.'
      setError(message)
      showToast(message, 'error')
    } finally {
      setCheckoutLoading(false)
    }
  }

  async function updateQuantity(cartItem, nextQuantity) {
    const latestCart = await fetchCartForUser(userId)
    const cartItemId = resolveCartItemId(
      latestCart.items.find((item) => resolveProductId(item) === resolveProductId(cartItem)) || cartItem
    )

    if (!cartItemId || nextQuantity < 1) {
      return
    }

    setUpdatingId(cartItemId)
    setError('')
    setActionMessage('')
    setActionError('')

    try {
      await updateCartQuantity({
        cartItemId,
        quantity: nextQuantity,
      })

      await loadCart()
      setActionMessage('Cart updated successfully.')
      showToast('Cart updated successfully', 'success')
    } catch (requestError) {
      const message = requestError?.message || 'Unable to update cart item.'
      setError(message)
      showToast(message, 'error')
    } finally {
      setUpdatingId(null)
    }
  }

  async function removeItem(cartItem) {
    const latestCart = await fetchCartForUser(userId)
    const cartItemId = resolveCartItemId(
      latestCart.items.find((item) => resolveProductId(item) === resolveProductId(cartItem)) || cartItem
    )
    if (!cartItemId) {
      return
    }

    setRemovingId(cartItemId)
    setError('')
    setActionMessage('')
    setActionError('')

    try {
      await removeCartItem(cartItemId)
      await loadCart()
      setActionMessage('Cart item removed successfully.')
      showToast('Cart item removed successfully', 'success')
    } catch (requestError) {
      const message = requestError?.message || 'Unable to remove cart item.'
      setError(message)
      showToast(message, 'error')
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
            <Button type="button" onClick={loadCart} disabled={loading || !userId}>
              {loading ? 'Loading...' : 'Refresh'}
            </Button>
          </div>
        </div>

        {error ? <p className="form-message error">{error}</p> : null}
        {actionMessage ? <p className="form-message success">{actionMessage}</p> : null}
        {actionError ? <p className="form-message error">{actionError}</p> : null}

        {!userId ? (
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
                const itemId = getCartItemId(item)
                const key = itemId || `${item.product?.productId ?? 'item'}-${index}`
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

                      <p className="cart-item-subtotal">Subtotal: {formatPrice(subtotal)}</p>

                      <div className="cart-item-controls">
                        <div className="cart-quantity-controls">
                          <Button
                            type="button"
                            variant="secondary"
                            size="small"
                            disabled={quantity <= 1 || updatingId === itemId}
                            onClick={() => updateQuantity(item, quantity - 1)}
                          >
                            -
                          </Button>
                          <span className="cart-quantity-value">{quantity}</span>
                          <Button
                            type="button"
                            variant="secondary"
                            size="small"
                            disabled={updatingId === itemId}
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
                          disabled={removingId === itemId}
                        >
                          {removingId === itemId ? 'Removing...' : 'Remove'}
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
              <div className="cart-summary-row cart-checkout-row">
                <Button type="button" variant="secondary" onClick={handleClearCart} disabled={loading}>
                  Clear Cart
                </Button>
                <Button type="button" onClick={handleCheckout} disabled={loading || checkoutLoading}>
                  {checkoutLoading ? 'Checking out...' : 'Checkout'}
                </Button>
              </div>
            </aside>
          </div>
        )}
      </section>
    </main>
  )
}

export default Cart
