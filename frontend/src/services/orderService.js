import { get } from './api.js'

function normalizeOrderItem(item) {
  return {
    orderItemId: item?.orderItemId ?? item?.id ?? '',
    productName: item?.productName ?? item?.product?.name ?? item?.name ?? 'Product',
    productImage: item?.productImage ?? item?.imageUrl ?? item?.product?.imageUrl ?? '',
    quantity: Number(item?.quantity ?? 0) || 0,
    price: item?.price ?? item?.unitPrice ?? item?.subtotal ?? item?.product?.price ?? 0,
  }
}

function normalizeOrder(order) {
  const items = Array.isArray(order?.orderItems)
    ? order.orderItems
    : Array.isArray(order?.items)
      ? order.items
      : []

  return {
    orderId: order?.orderId ?? order?.id ?? '',
    orderDate: order?.orderDate ?? order?.createdAt ?? '',
    totalAmount: order?.totalAmount ?? order?.amount ?? 0,
    status: order?.status ?? order?.orderStatus ?? 'UNKNOWN',
    orderItems: items.map(normalizeOrderItem),
  }
}

function normalizeCheckoutOrder(response) {
  const order = response?.order

  if (!order || typeof order !== 'object') {
    return null
  }

  const items = Array.isArray(order.items) ? order.items : []

  return {
    orderId: order.orderId ?? '',
    orderDate: order.orderDate ?? '',
    totalAmount: order.totalAmount ?? 0,
    status: order.orderStatus ?? order.status ?? 'PLACED',
    orderItems: items.map((item) => ({
      orderItemId: item?.orderItemId ?? item?.id ?? '',
      productName: item?.productName ?? item?.name ?? 'Product',
      productImage: item?.productImage ?? '',
      quantity: Number(item?.quantity ?? 0) || 0,
      price: item?.unitPrice ?? item?.price ?? 0,
    })),
  }
}

export async function getUserOrders(userId) {
  if (!userId) {
    throw new Error('Missing user id.')
  }

  const data = await get(`/orders/user/${encodeURIComponent(userId)}`)
  const orders = Array.isArray(data)
    ? data
    : Array.isArray(data?.orders)
      ? data.orders
      : Array.isArray(data?.content)
        ? data.content
        : []

  return orders.map(normalizeOrder)
}

export function getStoredLastOrderHistory() {
  const savedOrder = sessionStorage.getItem('lastOrder')

  if (!savedOrder) {
    return []
  }

  try {
    const parsed = JSON.parse(savedOrder)
    const normalized = normalizeCheckoutOrder(parsed)
    return normalized ? [normalized] : []
  } catch {
    return []
  }
}
