import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Button from '../components/Button.jsx'
import { checkoutCart, fetchCartForUser, getStoredUserId } from '../services/cartService.js'
import { initiatePayment } from '../services/paymentService.js'
import { formatCurrency } from '../utils/currency.js'
import { showToast } from '../utils/toast.js'

function Checkout({ onBack }) {
  const navigate = useNavigate()
  const userId = getStoredUserId()
  const [cart, setCart] = useState({ items: [], totalAmount: 0, totalQuantity: 0 })
  const [cartLoading, setCartLoading] = useState(true)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState({})
  const [form, setForm] = useState({
    name: '',
    phone: '',
    address: '',
    city: '',
    state: '',
    pincode: '',
    paymentMethod: 'COD',
  })

  function normalizeCart(data) {
    if (!data || typeof data !== 'object') {
      return { items: [], totalAmount: 0, totalQuantity: 0 }
    }

    const items = Array.isArray(data.items) ? data.items : []
    return {
      items,
      totalAmount: Number(data.totalAmount ?? 0) || 0,
      totalQuantity: Number(data.totalQuantity ?? 0) || 0,
    }
  }

  useEffect(() => {
    async function loadCart() {
      if (!userId) {
        setCartLoading(false)
        return
      }

      setCartLoading(true)
      try {
        const nextCart = await fetchCartForUser(userId)
        setCart(normalizeCart(nextCart))
      } catch (requestError) {
        const nextMessage = requestError?.message || 'Unable to load cart summary.'
        setError(nextMessage)
        showToast(nextMessage, 'error')
      } finally {
        setCartLoading(false)
      }
    }

    void loadCart()
  }, [userId])

  function handleBack() {
    if (typeof onBack === 'function') {
      onBack()
      return
    }

    navigate('/cart')
  }

  function validate(values = form) {
    const nextErrors = {}

    if (!values.name.trim()) nextErrors.name = 'Name is required'
    if (!values.phone.trim()) nextErrors.phone = 'Phone is required'
    else if (!/^\d{10}$/.test(values.phone.trim())) nextErrors.phone = 'Phone must be 10 digits'
    if (!values.address.trim()) nextErrors.address = 'Address is required'
    if (!values.city.trim()) nextErrors.city = 'City is required'
    if (!values.state.trim()) nextErrors.state = 'State is required'
    if (!values.pincode.trim()) nextErrors.pincode = 'Pincode is required'
    else if (!/^\d{6}$/.test(values.pincode.trim())) nextErrors.pincode = 'Pincode must be 6 digits'
    if (!values.paymentMethod.trim()) nextErrors.paymentMethod = 'Select a payment method'

    return nextErrors
  }

  function handleChange(event) {
    const { name, value } = event.target
    setForm((current) => ({
      ...current,
      [name]: value,
    }))
  }

  async function handleCheckout(event) {
    event.preventDefault()

    if (!userId) {
      setError('Please login to checkout')
      showToast('Please login to checkout', 'error')
      return
    }

    const nextErrors = validate()
    setFieldErrors(nextErrors)
    if (Object.keys(nextErrors).length > 0) {
      setError('Please fix the highlighted fields.')
      showToast('Please fix the highlighted fields.', 'error')
      return
    }

    if (cart.items.length === 0) {
      setError('Your cart is empty.')
      showToast('Your cart is empty.', 'error')
      return
    }

    setLoading(true)
    setError('')

    try {
      const response = await checkoutCart(userId, form)
      const order = response?.order
      const payment = response?.payment

      if (!order) {
        throw new Error('Checkout completed but order details were not returned.')
      }

      if (form.paymentMethod === 'COD') {
        sessionStorage.setItem('lastOrder', JSON.stringify(response))
        navigate('/order-success', {
          state: response,
          replace: true,
        })
        showToast('Order placed successfully', 'success')
      } else {
        const verification = await initiatePayment(order.totalAmount, {
          order,
          checkoutOrder: payment,
          customer: {
            name: form.name,
            email: sessionStorage.getItem('userEmail') || '',
            phone: form.phone,
          },
        })

        sessionStorage.setItem('lastOrder', JSON.stringify(verification))
        navigate('/order-success', {
          state: verification,
          replace: true,
        })
        showToast('Payment successful', 'success')
      }
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
      <section className="dashboard-card checkout-shell">
        <div className="dashboard-header">
          <div>
            <p className="eyebrow">Checkout</p>
            <h1>Place your order</h1>
            <p className="dashboard-copy">
              Review your cart, enter shipping details, and place your order.
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
          <div className="checkout-layout">
            <form className="checkout-form" onSubmit={handleCheckout}>
              <h2>Shipping and payment</h2>
              {error ? <p className="form-message error">{error}</p> : null}

              <div className="checkout-form-grid">
                <div className="form-group">
                  <label className="form-label" htmlFor="name">
                    Full name
                  </label>
                  <input
                    id="name"
                    name="name"
                    className="form-input"
                    value={form.name}
                    onChange={handleChange}
                    placeholder="Your name"
                  />
                  {fieldErrors.name ? <p className="field-error">{fieldErrors.name}</p> : null}
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="phone">
                    Phone number
                  </label>
                  <input
                    id="phone"
                    name="phone"
                    className="form-input"
                    value={form.phone}
                    onChange={handleChange}
                    placeholder="10 digit phone"
                    inputMode="numeric"
                  />
                  {fieldErrors.phone ? <p className="field-error">{fieldErrors.phone}</p> : null}
                </div>

                <div className="form-group checkout-full">
                  <label className="form-label" htmlFor="address">
                    Address
                  </label>
                  <input
                    id="address"
                    name="address"
                    className="form-input"
                    value={form.address}
                    onChange={handleChange}
                    placeholder="Street, apartment, landmark"
                  />
                  {fieldErrors.address ? <p className="field-error">{fieldErrors.address}</p> : null}
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="city">
                    City
                  </label>
                  <input
                    id="city"
                    name="city"
                    className="form-input"
                    value={form.city}
                    onChange={handleChange}
                    placeholder="City"
                  />
                  {fieldErrors.city ? <p className="field-error">{fieldErrors.city}</p> : null}
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="state">
                    State
                  </label>
                  <input
                    id="state"
                    name="state"
                    className="form-input"
                    value={form.state}
                    onChange={handleChange}
                    placeholder="State"
                  />
                  {fieldErrors.state ? <p className="field-error">{fieldErrors.state}</p> : null}
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="pincode">
                    Pincode
                  </label>
                  <input
                    id="pincode"
                    name="pincode"
                    className="form-input"
                    value={form.pincode}
                    onChange={handleChange}
                    placeholder="6 digit pincode"
                    inputMode="numeric"
                  />
                  {fieldErrors.pincode ? <p className="field-error">{fieldErrors.pincode}</p> : null}
                </div>

                <div className="form-group checkout-full">
                  <label className="form-label" htmlFor="paymentMethod">
                    Payment method
                  </label>
                  <select
                    id="paymentMethod"
                    name="paymentMethod"
                    className="form-input"
                    value={form.paymentMethod}
                    onChange={handleChange}
                  >
                    <option value="COD">Cash on Delivery</option>
                    <option value="UPI">UPI</option>
                    <option value="CARD">Card</option>
                    <option value="NET_BANKING">Net Banking</option>
                    <option value="WALLET">Wallet</option>
                  </select>
                  {fieldErrors.paymentMethod ? (
                    <p className="field-error">{fieldErrors.paymentMethod}</p>
                  ) : null}
                </div>
              </div>

              <div className="checkout-actions">
                <Button type="button" variant="secondary" onClick={handleBack} disabled={loading}>
                  Cancel
                </Button>
                <Button type="submit" disabled={loading || cartLoading || cart.items.length === 0}>
                  {loading ? 'Processing...' : 'Place Order'}
                </Button>
              </div>
            </form>

            <aside className="checkout-summary">
              <h2>Cart summary</h2>
              {cartLoading ? (
                <p className="product-state">Loading cart...</p>
              ) : cart.items.length === 0 ? (
                <p className="product-state">Your cart is empty.</p>
              ) : (
                <>
                  <div className="checkout-summary-items">
                    {cart.items.map((item, index) => {
                      const product = item.product || {}
                      const quantity = Number(item.quantity || 0)
                      const unitPrice = Number(item.priceAtTime ?? product.price ?? 0)
                      const subtotal = Number(item.subtotal ?? unitPrice * quantity)

                      return (
                        <div className="checkout-summary-item" key={item.cartItemId || `${product.productId ?? 'item'}-${index}`}>
                          <div>
                            <strong>{product.name ?? 'Unnamed product'}</strong>
                            <p>{quantity} x {formatCurrency(unitPrice)}</p>
                          </div>
                          <strong>{formatCurrency(subtotal)}</strong>
                        </div>
                      )
                    })}
                  </div>
                  <div className="cart-summary-row">
                    <span>Total quantity</span>
                    <strong>{cart.totalQuantity}</strong>
                  </div>
                  <div className="cart-summary-row">
                    <span>Total</span>
                    <strong>{formatCurrency(cart.totalAmount)}</strong>
                  </div>
                </>
              )}
            </aside>
          </div>
        )}
      </section>
    </main>
  )
}

export default Checkout
