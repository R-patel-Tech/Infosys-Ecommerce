import { useEffect, useState } from 'react'
import Button from './Button.jsx'
import { addProductToCart, getStoredUserId, resolveProductId } from '../services/cartService.js'
import { formatCurrency } from '../utils/currency.js'
import { getProductImage } from '../utils/productImage.js'
import { showToast } from '../utils/toast.js'

function CartPlusIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="button-icon">
      <path
        d="M3.5 5h2l1.8 9.2A2 2 0 0 0 9.3 16h8.1a2 2 0 0 0 1.9-1.4L21.4 8H7"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path d="M12 10v6M9 13h6" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" />
    </svg>
  )
}

function ProductCard({ product, onShowDetails }) {
  const [message, setMessage] = useState('')
  const [messageType, setMessageType] = useState('success')
  const [isAdding, setIsAdding] = useState(false)
  const userId = getStoredUserId()
  const productId = resolveProductId(product)
  const imageSrc = getProductImage(product)
  const stockQuantity = Number(product?.stockQuantity ?? product?.stock ?? 0)
  const isLowStock = stockQuantity > 0 && stockQuantity <= 5
  const isOutOfStock = stockQuantity <= 0

  useEffect(() => {
    if (!message) {
      return undefined
    }

    const timeout = window.setTimeout(() => {
      setMessage('')
    }, 2400)

    return () => window.clearTimeout(timeout)
  }, [message])

  async function handleAddToCart(event) {
    event.stopPropagation()

    if (!userId) {
      setMessageType('error')
      setMessage('Login required to add items to cart')
      showToast('Please login to add items to cart', 'error')
      return
    }

    if (!productId) {
      setMessageType('error')
      setMessage('This product cannot be added right now.')
      return
    }

    setIsAdding(true)
    setMessage('')

    try {
      await addProductToCart({ userId, productId, quantity: 1 })
      setMessageType('success')
      setMessage('Added to cart')
      showToast('Item added to cart', 'success')
    } catch (error) {
      setMessageType('error')
      const nextMessage = error?.message || 'Unable to add item to cart.'
      setMessage(nextMessage)
      showToast(nextMessage, 'error')
    } finally {
      setIsAdding(false)
    }
  }

  return (
    <article
      className="product-card product-card-modern glass-card"
      onClick={() => onShowDetails?.(productId)}
      role="button"
      tabIndex={0}
      onKeyDown={(event) => {
        if (event.key === 'Enter' || event.key === ' ') {
          event.preventDefault()
          onShowDetails?.(productId)
        }
      }}
    >
      <div className="product-media product-media-modern">
        <img className="product-image" src={imageSrc} alt={product?.name ?? 'Product image'} loading="lazy" />
        <span className={`product-chip ${isOutOfStock ? 'warning' : isLowStock ? 'info' : 'success'}`}>
          {isOutOfStock ? 'Out of stock' : isLowStock ? 'Low stock' : 'In stock'}
        </span>
      </div>
      <div className="product-card-body">
        <div className="product-card-head">
          <div>
            <p className="product-card-eyebrow">{product?.category || 'Uncategorized'}</p>
            <h3>{product?.name ?? 'Unnamed product'}</h3>
          </div>
          <span className="product-price">{formatCurrency(product?.price)}</span>
        </div>
        <p className="product-description">{product?.description || 'No description available.'}</p>
        <div className="product-meta">
          <span className="product-stock">Stock: {product?.stockQuantity ?? product?.stock ?? 'N/A'}</span>
        </div>
        <div className="product-actions">
          <Button
            type="button"
            size="small"
            className="product-action-button"
            onClick={handleAddToCart}
            disabled={isAdding || isOutOfStock}
            iconLeft={<CartPlusIcon />}
          >
            {isAdding ? 'Adding...' : 'Add to Cart'}
          </Button>
        </div>
        {message ? <p className={`form-message ${messageType === 'error' ? 'error' : 'success'}`}>{message}</p> : null}
      </div>
    </article>
  )
}

export default ProductCard
