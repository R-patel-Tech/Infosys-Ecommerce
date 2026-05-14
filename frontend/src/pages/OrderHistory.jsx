import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Button from '../components/Button.jsx'
import { getStoredUserId } from '../services/cartService.js'
import { getStoredLastOrderHistory, getUserOrders } from '../services/orderService.js'
import { formatCurrency } from '../utils/currency.js'
import { getProductImage } from '../utils/productImage.js'

function formatOrderDate(value) {
  if (!value) {
    return 'Date unavailable'
  }

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return 'Date unavailable'
  }

  return new Intl.DateTimeFormat('en-IN', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(date)
}

function getStatusClass(status) {
  const normalized = String(status || '').toLowerCase()

  if (normalized.includes('delivered') || normalized.includes('completed')) {
    return 'order-status success'
  }

  if (normalized.includes('cancel')) {
    return 'order-status danger'
  }

  return 'order-status neutral'
}

function OrderHistory() {
  const navigate = useNavigate()
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    async function loadOrders() {
      const userId = getStoredUserId()
      if (!userId) {
        setOrders([])
        setError('Please sign in to view your order history.')
        setLoading(false)
        return
      }

      setLoading(true)
      setError('')

      try {
        const data = await getUserOrders(userId)
        const sorted = [...data].sort((left, right) => {
          const leftTime = new Date(left.orderDate || 0).getTime()
          const rightTime = new Date(right.orderDate || 0).getTime()
          return rightTime - leftTime
        })
        if (sorted.length > 0) {
          setOrders(sorted)
        } else {
          setOrders(getStoredLastOrderHistory())
        }
      } catch (err) {
        const fallbackOrders = getStoredLastOrderHistory()

        if (fallbackOrders.length > 0) {
          setOrders(fallbackOrders)
          setError('')
        } else {
          setError(err.message || 'Unable to load your orders.')
        }
      } finally {
        setLoading(false)
      }
    }

    loadOrders()
  }, [])

  return (
    <main className="page-shell dashboard-shell">
      <section className="dashboard-card order-history-shell">
        <div className="order-history-hero">
          <div>
            <p className="eyebrow">Order history</p>
            <h1>All your recent orders in one place</h1>
            <p className="dashboard-copy">
              Review order totals, delivery status, and the items included in each purchase.
            </p>
          </div>
          <Button type="button" variant="secondary" onClick={() => navigate('/products')}>
            Continue Shopping
          </Button>
        </div>

        {loading ? (
          <div className="order-history-loading" aria-live="polite" aria-busy="true">
            <div className="spinner order-spinner" />
            <p>Loading your orders...</p>
          </div>
        ) : error ? (
          <div className="dashboard-empty-state">
            <h3>We could not load your orders.</h3>
            <p>{error}</p>
          </div>
        ) : orders.length === 0 ? (
          <div className="dashboard-empty-state">
            <h3>No orders yet.</h3>
            <p>Once you place an order, it will appear here with the full item breakdown.</p>
          </div>
        ) : (
          <div className="order-history-list">
            {orders.map((order) => (
              <article className="order-card" key={order.orderId}>
                <div className="order-card-header">
                  <div>
                    <p className="order-label">Order ID</p>
                    <h2>#{order.orderId}</h2>
                  </div>
                  <div className={getStatusClass(order.status)}>
                    {String(order.status || 'Unknown').replaceAll('_', ' ')}
                  </div>
                </div>

                <div className="order-meta-grid">
                  <div>
                    <span>Date</span>
                    <strong>{formatOrderDate(order.orderDate)}</strong>
                  </div>
                  <div>
                    <span>Amount</span>
                    <strong>{formatCurrency(order.totalAmount)}</strong>
                  </div>
                </div>

                <div className="order-items-list">
                  {order.orderItems.map((item) => (
                    <div className="order-item-row" key={item.orderItemId}>
                      <div className="order-item-media">
                        <img
                          src={item.productImage || getProductImage({ name: item.productName })}
                          alt={item.productName}
                          className="order-item-image"
                          onError={(event) => {
                            event.currentTarget.src = getProductImage({ name: item.productName })
                          }}
                        />
                      </div>
                      <div className="order-item-copy">
                        <h3>{item.productName}</h3>
                        <p>
                          Qty {item.quantity} &middot; {formatCurrency(item.price)}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              </article>
            ))}
          </div>
        )}
      </section>
    </main>
  )
}

export default OrderHistory
