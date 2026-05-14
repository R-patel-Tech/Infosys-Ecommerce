import { useEffect, useState } from 'react'
import Button from './Button.jsx'
import { addProductToCart, getStoredUserId, resolveProductId } from '../services/cartService.js'
import { formatCurrency } from '../utils/currency.js'
import { getProductImage } from '../utils/productImage.js'
import { showToast } from '../utils/toast.js'

function ProductCard({ product, onShowDetails }) {
  const [message, setMessage] = useState('')
  const [messageType, setMessageType] = useState('success')
  const [isAdding, setIsAdding] = useState(false)
  const userId = getStoredUserId()
  const productId = resolveProductId(product)
  const imageSrc = getProductImage(product)

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
      const message = error?.message || 'Unable to add item to cart.'
      setMessage(message)
      showToast(message, 'error')
    } finally {
      setIsAdding(false)
    }
  }

  return (
    <article
      className="product-card product-card-modern"
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
        <span className={`product-chip ${(product?.stockQuantity ?? product?.stock ?? 0) > 0 ? 'success' : 'warning'}`}>
          {(product?.stockQuantity ?? product?.stock ?? 0) > 0 ? 'In stock' : 'Out of stock'}
        </span>
      </div>
      <div className="product-card-body">
        <div className="product-card-head">
          <h3>{product?.name ?? 'Unnamed product'}</h3>
          <span className="product-price">{formatCurrency(product?.price)}</span>
        </div>
        <p>{product?.description || 'No description available.'}</p>
        <div className="product-meta">
          <span className="product-category">{product?.category || 'Uncategorized'}</span>
          <span className="product-stock">Stock: {product?.stockQuantity ?? product?.stock ?? 'N/A'}</span>
        </div>
        <div className="product-actions">
          <Button type="button" size="small" onClick={handleAddToCart} disabled={isAdding}>
            {isAdding ? 'Adding...' : 'Add to Cart'}
          </Button>
        </div>
        {message ? <p className={`form-message ${messageType === 'error' ? 'error' : 'success'}`}>{message}</p> : null}
      </div>
    </article>
  )
}

export default ProductCard
