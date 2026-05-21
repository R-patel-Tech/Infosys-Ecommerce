import { del, get, post, put } from './api.js'
import { getStoredUserId as readStoredUserId } from '../utils/session.js'
import { normalizeCart } from '../utils/normalizers.js'

export function getStoredUserId() {
  return readStoredUserId()
}

function getItemProductId(item) {
  return item?.product?.productId ?? item?.productId ?? item?.product?.id ?? item?.id ?? item?.product?.sku ?? ''
}

function getItemCartId(item) {
  return item?.cartItemId ?? item?.cartId ?? item?.id ?? ''
}

function normalizeId(value) {
  if (value === null || value === undefined || value === '') {
    return ''
  }

  const numericValue = Number(value)
  return Number.isFinite(numericValue) ? numericValue : value
}

export function resolveCartItemId(item) {
  const cartItemId = getItemCartId(item)
  return cartItemId ? String(cartItemId) : ''
}

export function resolveProductId(item) {
  const productId = getItemProductId(item)
  return productId ? String(productId) : ''
}

export async function fetchCartForUser(userId) {
  if (!userId) {
    return normalizeCart()
  }

  const data = await get(`/cart/${encodeURIComponent(normalizeId(userId))}`)
  return normalizeCart(data)
}

export async function addProductToCart({ userId, productId, quantity = 1 }) {
  if (!userId || !productId) {
    throw new Error('Missing user or product information.')
  }

  await post('/cart/add', {
    userId: normalizeId(userId),
    productId: normalizeId(productId),
    quantity,
  })

  return fetchCartForUser(userId)
}

export async function updateCartQuantity({ cartItemId, quantity }) {
  const resolvedCartId =
    cartItemId && typeof cartItemId === 'object' ? resolveCartItemId(cartItemId) : String(cartItemId || '')

  if (!resolvedCartId) {
    throw new Error('Missing cart item id.')
  }

  const data = await put('/cart/update', {
    cartId: resolvedCartId,
    quantity,
  })

  return normalizeCart(data?.cart || data)
}

export async function removeCartItem(cartItemId) {
  const resolvedCartId =
    cartItemId && typeof cartItemId === 'object' ? resolveCartItemId(cartItemId) : String(cartItemId || '')

  if (!resolvedCartId) {
    throw new Error('Missing cart item id.')
  }

  await del(`/cart/remove/${encodeURIComponent(resolvedCartId)}`, { withCredentials: false })
  return true
}

export async function clearCart(userId) {
  await del(`/cart/clear/${encodeURIComponent(normalizeId(userId))}`, { withCredentials: false })
  return true
}

export async function checkoutCart(userId, orderData = {}) {
  if (!userId) {
    throw new Error('Missing user id.')
  }

  const data = await post(`/checkout/${encodeURIComponent(normalizeId(userId))}`, orderData)
  return data
}

export async function rebuildCartWithoutItem(userId, cartItemId) {
  if (!userId) {
    throw new Error('Missing user id.')
  }

  const currentCart = await fetchCartForUser(userId)
  const removedId = String(cartItemId || '')
  const remainingItems = currentCart.items.filter((item) => resolveCartItemId(item) !== removedId)

  await clearCart(userId)

  for (const item of remainingItems) {
    const productId = resolveProductId(item)
    const quantity = Number(item.quantity || 1)
    if (!productId) {
      continue
    }

    await addProductToCart({
      userId,
      productId,
      quantity: Number.isFinite(quantity) && quantity > 0 ? quantity : 1,
    })
  }

  return fetchCartForUser(userId)
}
