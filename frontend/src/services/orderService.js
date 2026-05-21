import { get } from './api.js'
import { normalizeCheckoutOrder, normalizeListResponse, normalizeOrder } from '../utils/normalizers.js'
import { readLastOrder } from '../utils/session.js'

export async function getUserOrders() {
  const data = await get('/orders/user')
  return normalizeListResponse(data, ['orders']).map(normalizeOrder)
}

export function getStoredLastOrderHistory() {
  const normalized = normalizeCheckoutOrder(readLastOrder())
  return normalized ? [normalized] : []
}
