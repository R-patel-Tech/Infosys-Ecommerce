import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Button from '../components/Button.jsx'
import OrderDetailsModal from '../components/OrderDetailsModal.jsx'
import { formatCurrency } from '../utils/currency.js'
import { useOrders } from '../hooks/useOrders.js'
import { formatDateTime } from '../utils/date.js'

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
  const { orders, loading, error } = useOrders()
  const [selectedOrder, setSelectedOrder] = useState(null)

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
          <div className="order-history-table-wrap">
            <table className="order-history-table">
              <thead>
                <tr>
                  <th>Order ID</th>
                  <th>Date</th>
                  <th>Total Amount</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {orders.map((order) => (
                  <tr key={order.orderId}>
                    <td>#{order.orderId}</td>
                    <td>{formatDateTime(order.orderDate)}</td>
                    <td>{formatCurrency(order.totalAmount)}</td>
                    <td>
                      <span className={getStatusClass(order.status)}>
                        {String(order.status || 'Unknown').replaceAll('_', ' ')}
                      </span>
                    </td>
                    <td>
                      <button
                        type="button"
                        className="order-details-link"
                        onClick={() => setSelectedOrder(order)}
                      >
                        View Details
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {selectedOrder ? <OrderDetailsModal order={selectedOrder} onClose={() => setSelectedOrder(null)} /> : null}
      </section>
    </main>
  )
}

export default OrderHistory
