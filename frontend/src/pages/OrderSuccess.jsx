import { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import Button from '../components/Button.jsx'
import { formatCurrency } from '../utils/currency.js'

function readSavedOrder() {
  const savedOrder = sessionStorage.getItem('lastOrder')
  if (!savedOrder) {
    return null
  }

  try {
    return JSON.parse(savedOrder)
  } catch {
    return null
  }
}

function OrderSuccess() {
  const navigate = useNavigate()
  const location = useLocation()
  const [orderResponse] = useState(() => location.state || readSavedOrder())

  useEffect(() => {
    if (location.state) {
      sessionStorage.setItem('lastOrder', JSON.stringify(location.state))
    }
  }, [location.state])

  const order = orderResponse?.order

  return (
    <main className="page-shell dashboard-shell">
      <section className="dashboard-card success-shell">
        <div className="success-hero">
          <div>
            <p className="eyebrow">Order complete</p>
            <h1>Your order has been placed</h1>
            <p className="dashboard-copy">
              We have saved the order, cleared your cart, and generated a summary below.
            </p>
          </div>
          <Button type="button" variant="secondary" onClick={() => navigate('/products')}>
            Continue Shopping
          </Button>
        </div>

        {!order ? (
          <div className="product-state">
            <p>No saved order summary was found.</p>
          </div>
        ) : (
          <div className="success-layout">
            <article className="success-card">
              <h2>Order summary</h2>
              <div className="success-summary-grid">
                <div>
                  <span>Order ID</span>
                  <strong>#{order.orderId}</strong>
                </div>
                <div>
                  <span>Status</span>
                  <strong>{order.orderStatus}</strong>
                </div>
                <div>
                  <span>Payment</span>
                  <strong>{order.paymentMethod}</strong>
                </div>
                <div>
                  <span>Total</span>
                  <strong>{formatCurrency(order.totalAmount)}</strong>
                </div>
              </div>
              <div className="success-address">
                <span>Shipping address</span>
                <p>{order.shippingAddress}</p>
              </div>
            </article>

            <aside className="success-card">
              <h2>Items</h2>
              <div className="checkout-summary-items">
                {order.items?.map((item) => (
                  <div className="checkout-summary-item" key={item.orderItemId}>
                    <div>
                      <strong>{item.productName}</strong>
                      <p>{item.quantity} x {formatCurrency(item.unitPrice)}</p>
                    </div>
                    <strong>{formatCurrency(item.subtotal)}</strong>
                  </div>
                ))}
              </div>
              <div className="cart-summary-row">
                <span>Total items</span>
                <strong>{order.itemCount}</strong>
              </div>
              <div className="success-actions">
                <Button type="button" onClick={() => navigate('/checkout')}>
                  Place Another Order
                </Button>
                <Button type="button" variant="secondary" onClick={() => navigate('/cart')}>
                  View Cart
                </Button>
              </div>
            </aside>
          </div>
        )}
      </section>
    </main>
  )
}

export default OrderSuccess
