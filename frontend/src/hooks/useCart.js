import { useEffect, useState } from 'react'
import {
  clearCart,
  fetchCartForUser,
  removeCartItem,
  resolveCartItemId,
  resolveProductId,
  updateCartQuantity,
} from '../services/cartService.js'
import { normalizeCart } from '../utils/normalizers.js'

export function useCart(userId) {
  const [cart, setCart] = useState({ items: [], totalAmount: 0, totalQuantity: 0 })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  async function loadCart() {
    if (!userId) {
      setCart({ items: [], totalAmount: 0, totalQuantity: 0 })
      setLoading(false)
      return
    }

    setLoading(true)
    setError('')

    try {
      const nextCart = await fetchCartForUser(userId)
      setCart(normalizeCart(nextCart))
    } catch (requestError) {
      setError(requestError.message || 'Unable to load cart.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadCart()
  }, [userId])

  async function refreshCart() {
    await loadCart()
  }

  async function clearUserCart() {
    if (!userId) {
      return
    }

    await clearCart(userId)
    await loadCart()
  }

  async function setItemQuantity(cartItem, nextQuantity) {
    const latestCart = await fetchCartForUser(userId)
    const matchedItem = latestCart.items.find((item) => resolveProductId(item) === resolveProductId(cartItem)) || cartItem
    const cartItemId = resolveCartItemId(matchedItem)

    if (!cartItemId || nextQuantity < 1) {
      return
    }

    await updateCartQuantity({ cartItemId, quantity: nextQuantity })
    await loadCart()
  }

  async function deleteItem(cartItem) {
    const latestCart = await fetchCartForUser(userId)
    const matchedItem = latestCart.items.find((item) => resolveProductId(item) === resolveProductId(cartItem)) || cartItem
    const cartItemId = resolveCartItemId(matchedItem)

    if (!cartItemId) {
      return
    }

    await removeCartItem(cartItemId)
    await loadCart()
  }

  return {
    cart,
    loading,
    error,
    setCart,
    setLoading,
    setError,
    loadCart,
    refreshCart,
    clearUserCart,
    setItemQuantity,
    deleteItem,
  }
}

