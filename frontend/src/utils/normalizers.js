export function normalizeListResponse(data, preferredKeys = []) {
  if (Array.isArray(data)) {
    return data
  }

  for (const key of preferredKeys) {
    if (Array.isArray(data?.[key])) {
      return data[key]
    }
  }

  if (Array.isArray(data?.data)) {
    return data.data
  }

  if (Array.isArray(data?.content)) {
    return data.content
  }

  return []
}

export function normalizeProducts(data) {
  return normalizeListResponse(data, ['products'])
}

export function normalizeCart(data) {
  if (!data || typeof data !== 'object') {
    return { items: [], totalAmount: 0, totalQuantity: 0 }
  }

  const source = data.cart && typeof data.cart === 'object' ? data.cart : data
  const items = normalizeListResponse(source, ['items', 'cartItems'])

  return {
    items,
    totalAmount: Number(source.totalAmount ?? source.totalPrice ?? 0) || 0,
    totalQuantity: Number(source.totalQuantity ?? source.totalItems ?? 0) || 0,
  }
}

export function normalizeOrderItem(item) {
  return {
    orderItemId: item?.orderItemId ?? item?.id ?? '',
    productName: item?.productName ?? item?.product?.name ?? item?.name ?? 'Product',
    productImage: item?.productImage ?? item?.imageUrl ?? item?.product?.imageUrl ?? '',
    quantity: Number(item?.quantity ?? 0) || 0,
    price: Number(item?.price ?? item?.unitPrice ?? item?.subtotal ?? item?.product?.price ?? 0) || 0,
    unitPrice: Number(item?.unitPrice ?? item?.price ?? item?.product?.price ?? 0) || 0,
    subtotal: Number(item?.subtotal ?? item?.price ?? item?.unitPrice ?? 0) || 0,
  }
}

export function normalizeOrder(order) {
  const items = normalizeListResponse(order, ['orderItems', 'items']).map(normalizeOrderItem)

  return {
    orderId: order?.orderId ?? order?.id ?? '',
    orderDate: order?.orderDate ?? order?.createdAt ?? '',
    totalAmount: Number(order?.totalAmount ?? order?.amount ?? 0) || 0,
    status: order?.status ?? order?.orderStatus ?? 'UNKNOWN',
    paymentMethod: order?.paymentMethod ?? order?.orderPaymentStatus ?? '',
    shippingAddress: order?.shippingAddress ?? '',
    itemCount: Number(order?.itemCount ?? items.reduce((count, item) => count + item.quantity, 0)) || 0,
    orderItems: items,
    items,
  }
}

export function normalizeCheckoutOrder(response) {
  const order = response?.order

  if (!order || typeof order !== 'object') {
    return null
  }

  return normalizeOrder({
    ...order,
    orderStatus: order.orderStatus ?? order.status,
    paymentMethod: order.paymentMethod ?? response?.paymentMethod,
    orderItems: normalizeListResponse(order, ['items']).map((item) => ({
      orderItemId: item?.orderItemId ?? item?.id ?? '',
      productName: item?.productName ?? item?.name ?? 'Product',
      productImage: item?.productImage ?? '',
      quantity: Number(item?.quantity ?? 0) || 0,
      unitPrice: Number(item?.unitPrice ?? item?.price ?? 0) || 0,
      subtotal: Number(item?.subtotal ?? item?.price ?? 0) || 0,
    })),
  })
}

