import { formatCurrency } from '../utils/currency.js'
import { formatDateTime } from '../utils/date.js'
import { getProductImage } from '../utils/productImage.js'

function OrderDetailsModal({ order, onClose }) {
  if (!order) {
    return null
  }

  return (
    <div className="order-modal-backdrop" role="presentation" onClick={onClose}>
      <section
        className="order-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="order-details-title"
        onClick={(event) => event.stopPropagation()}
      >
        <div className="order-modal-header">
          <div>
            <p className="eyebrow">Order details</p>
            <h2 id="order-details-title">Order #{order.orderId}</h2>
            <p className="order-modal-subtitle">
              Placed on {formatDateTime(order.orderDate)} - {String(order.status || 'Unknown').replaceAll('_', ' ')}
            </p>
          </div>
          <button type="button" className="order-modal-close" onClick={onClose} aria-label="Close order details">
            X
          </button>
        </div>

        <div className="order-modal-summary">
          <div>
            <span>Total amount</span>
            <strong>{formatCurrency(order.totalAmount)}</strong>
          </div>
          <div>
            <span>Items</span>
            <strong>{order.orderItems?.length ?? 0}</strong>
          </div>
        </div>

        <div className="order-modal-items">
          {(order.orderItems || []).map((item) => (
            <article className="order-modal-item" key={item.orderItemId}>
              <div className="order-modal-item-media">
                <img
                  src={item.productImage || getProductImage({ name: item.productName })}
                  alt={item.productName}
                  className="order-modal-item-image"
                  onError={(event) => {
                    event.currentTarget.src = getProductImage({ name: item.productName })
                  }}
                />
              </div>

              <div className="order-modal-item-copy">
                <h3>{item.productName}</h3>
                <p>Qty {item.quantity}</p>
                <p>{formatCurrency(item.price)} each</p>
              </div>
            </article>
          ))}
        </div>
      </section>
    </div>
  )
}

export default OrderDetailsModal
