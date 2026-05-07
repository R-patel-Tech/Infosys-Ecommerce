import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Button from '../components/Button.jsx'
import { checkoutCart, getStoredUserId } from '../services/cartService.js'
import { showToast } from '../utils/toast.js'

function Checkout({ onBack }) {
  const navigate = useNavigate()
  const userId = getStoredUserId()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  function handleBack() {
    if (typeof onBack === 'function') {
      onBack()
      return
    }

    navigate('/cart')
  }

  async function handleCheckout() {
    if (!userId) {
      setError('Please login to checkout')
      showToast('Please login to checkout', 'error')
      return
    }

    setLoading(true)
    setError('')
    setMessage('')

    try {
      await checkoutCart(userId, { userId })
      setMessage('Order placed successfully')
      showToast('Order placed successfully', 'success')
    } catch (requestError) {
      const nextMessage = requestError?.message || 'Unable to complete checkout.'
      setError(nextMessage)
      showToast(nextMessage, 'error')
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="page-shell dashboard-shell">
      <section className="dashboard-card">
        <div className="dashboard-header">
          <div>
            <p className="eyebrow">Checkout</p>
            <h1>Place your order</h1>
            <p className="dashboard-copy">
              Review your cart and complete checkout with the backend order endpoint.
            </p>
          </div>
          <Button type="button" variant="secondary" onClick={handleBack}>
            Back
          </Button>
        </div>

        {!userId ? (
          <div className="product-state cart-empty-state">
            <p>Please login to checkout</p>
            <Button type="button" onClick={() => navigate('/login')}>
              Login
            </Button>
          </div>
        ) : (
          <div className="cart-summary">
            <p className="product-state">Confirm checkout to place the order.</p>
            {error ? <p className="form-message error">{error}</p> : null}
            {message ? <p className="form-message success">{message}</p> : null}
            <div className="cart-summary-row cart-checkout-row">
              <Button type="button" variant="secondary" onClick={handleBack} disabled={loading}>
                Cancel
              </Button>
              <Button type="button" onClick={handleCheckout} disabled={loading}>
                {loading ? 'Processing...' : 'Place Order'}
              </Button>
            </div>
          </div>
        )}
      </section>
    </main>
  )
}

export default Checkout
