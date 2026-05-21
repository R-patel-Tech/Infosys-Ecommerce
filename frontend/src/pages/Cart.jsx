import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Button from '../components/Button.jsx'
import { useCart } from '../hooks/useCart.js'
import { getStoredUserId } from '../utils/session.js'
import { formatCurrency } from '../utils/currency.js'
import { getProductImage } from '../utils/productImage.js'
import { showToast } from '../utils/toast.js'

function Cart({ onBack }) {
  const navigate = useNavigate()
  const userId = getStoredUserId()
  const {
    cart,
    loading,
    error,
    loadCart,
    clearUserCart,
    setItemQuantity,
    deleteItem,
    setLoading,
    setError,
  } = useCart(userId)
  const [actionMessage, setActionMessage] = useState('')
  const [actionError, setActionError] = useState('')
  const [updatingId, setUpdatingId] = useState(null)
  const [removingId, setRemovingId] = useState(null)

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
      await clearUserCart()
      showToast('Cart cleared successfully', 'success')
    } catch (requestError) {
      const message = requestError?.message || 'Unable to clear cart.'
      setError(message)
      showToast(message, 'error')
    } finally {
      setLoading(false)
    }
  }

  function handleCheckout() {
    if (!userId) {
      navigate('/login')
      return
    }

    if (cart.items.length === 0) {
      showToast('Your cart is empty.', 'error')
      return
    }

    navigate('/checkout')
  }

  async function updateQuantity(cartItem, nextQuantity) {
    const cartItemId = cartItem?.cartItemId ?? cartItem?.cartId ?? cartItem?.id ?? ''
    if (!cartItemId || nextQuantity < 1) {
      return
    }

    setUpdatingId(cartItemId)
    setError('')
    setActionMessage('')
    setActionError('')

    try {
      await setItemQuantity(cartItem, nextQuantity)
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
    const cartItemId = cartItem?.cartItemId ?? cartItem?.cartId ?? cartItem?.id ?? ''
    if (!cartItemId) {
      return
    }

    setRemovingId(cartItemId)
    setError('')
    setActionMessage('')
    setActionError('')

    try {
      await deleteItem(cartItem)
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
                const itemId = item.cartItemId ?? item.cartId ?? item.id ?? ''
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
                        <strong className="product-price">{formatCurrency(unitPrice)}</strong>
                      </div>

                      <p className="cart-item-subtotal">Subtotal: {formatCurrency(subtotal)}</p>

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
                <strong>{formatCurrency(cart.totalAmount)}</strong>
              </div>
              <div className="cart-summary-row">
                <span>Total quantity</span>
                <strong>{cart.totalQuantity}</strong>
              </div>
              <div className="cart-summary-row cart-checkout-row">
                <Button type="button" variant="secondary" onClick={handleClearCart} disabled={loading}>
                  Clear Cart
                </Button>
                <Button type="button" onClick={handleCheckout} disabled={loading}>
                  Checkout
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
