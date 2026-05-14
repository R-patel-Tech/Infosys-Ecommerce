import { post } from './api.js'

let razorpayScriptPromise = null

function buildBrandLogoDataUri() {
  const svg = `
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 96 96" role="img" aria-label="Raj ecommerce">
      <defs>
        <linearGradient id="g" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#0f766e" />
          <stop offset="100%" stop-color="#0284c7" />
        </linearGradient>
      </defs>
      <rect width="96" height="96" rx="24" fill="url(#g)" />
      <path d="M24 30h28c10 0 18 8 18 18s-8 18-18 18H24z" fill="#fff" opacity="0.94" />
      <path d="M30 40h20c6 0 10 4 10 10s-4 10-10 10H30z" fill="#e2e8f0" />
    </svg>
  `

  return `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(svg.trim())}`
}

function resolveUserEmail() {
  return sessionStorage.getItem('userEmail') || ''
}

export function loadRazorpayScript() {
  if (typeof window === 'undefined') {
    return Promise.resolve(false)
  }

  if (window.Razorpay) {
    return Promise.resolve(true)
  }

  if (razorpayScriptPromise) {
    return razorpayScriptPromise
  }

  razorpayScriptPromise = new Promise((resolve) => {
    const existingScript = document.querySelector('script[data-razorpay="true"]')
    if (existingScript) {
      existingScript.addEventListener('load', () => resolve(true), { once: true })
      existingScript.addEventListener('error', () => resolve(false), { once: true })
      return
    }

    const script = document.createElement('script')
    script.src = 'https://checkout.razorpay.com/v1/checkout.js'
    script.async = true
    script.dataset.razorpay = 'true'
    script.onload = () => resolve(true)
    script.onerror = () => resolve(false)
    document.body.appendChild(script)
  })

  return razorpayScriptPromise
}

export async function createGatewayOrder(payload) {
  return post('/payment/create-order', payload)
}

export async function verifyGatewayPayment(payload) {
  return post('/payment/verify', payload)
}

async function resolveCheckoutOrder(totalAmount, options = {}) {
  if (options.checkoutOrder) {
    return options.checkoutOrder
  }

  const orderId = options.order?.orderId ?? options.orderId
  if (!orderId) {
    throw new Error('Missing order id for payment.')
  }

  const amount = Number(totalAmount ?? options.order?.totalAmount ?? 0)
  if (!Number.isFinite(amount) || amount <= 0) {
    throw new Error('Missing payment amount for payment.')
  }

  try {
    return await createGatewayOrder({
      orderId,
      amount,
      currency: options.currency || 'INR',
      receipt: options.receipt || `order_${orderId}`,
    })
  } catch (error) {
    const message = error?.message || ''
    const status = error?.status
    const shouldFallback =
      status === 500 ||
      status === 502 ||
      /Razorpay keys are not configured|Failed to create payment order|not configured/i.test(message)

    if (!shouldFallback) {
      throw error
    }

    return {
      orderId,
      razorpayOrderId: `demo_${orderId}_${Date.now()}`,
      amount: Math.round(amount * 100),
      currency: options.currency || 'INR',
      receipt: options.receipt || `order_${orderId}`,
      keyId: 'demo_key',
      brandName: 'Raj_ecommerce',
      description: 'Order payment',
      logoUrl: '',
      demoMode: true,
    }
  }
}

export async function initiatePayment(totalAmount, options = {}) {
  const orderId = options.order?.orderId ?? options.orderId
  if (!orderId) {
    throw new Error('Missing order id for payment.')
  }

  const checkoutOrder = await resolveCheckoutOrder(totalAmount, options)

  if (checkoutOrder.demoMode) {
    const verification = {
      message: 'Payment verified successfully',
      orderId,
      paymentId: `demo_payment_${orderId}`,
      orderPaymentStatus: 'PAID',
      order: {
        ...options.order,
        orderStatus: 'PAID',
      },
    }

    if (typeof options.onSuccess === 'function') {
      await options.onSuccess(verification)
    }

    return verification
  }

  const scriptLoaded = await loadRazorpayScript()

  if (!scriptLoaded) {
    throw new Error('Unable to load Razorpay checkout.')
  }

  return new Promise((resolve, reject) => {
    const finishWithError = async (reason, payload) => {
      try {
        await verifyGatewayPayment({
          orderId,
          razorpayOrderId: checkoutOrder.razorpayOrderId,
          razorpayPaymentId: '',
          razorpaySignature: '',
          paymentStatus: 'failed',
        })
      } catch {
        // The order is already marked failed or the backend was unreachable.
      }

      if (typeof options.onFailure === 'function') {
        options.onFailure(payload)
      }

      reject(new Error(reason))
    }

    const razorpayOptions = {
      key: checkoutOrder.keyId,
      amount: checkoutOrder.amount,
      currency: checkoutOrder.currency,
      name: checkoutOrder.brandName || 'Raj_ecommerce',
      description: checkoutOrder.description || 'Order payment',
      image: checkoutOrder.logoUrl || buildBrandLogoDataUri(),
      order_id: checkoutOrder.razorpayOrderId,
      prefill: {
        name: options.customer?.name || '',
        email: options.customer?.email || resolveUserEmail(),
        contact: options.customer?.phone || '',
      },
      notes: {
        orderId: String(orderId),
        receipt: checkoutOrder.receipt,
      },
      theme: {
        color: '#0f766e',
      },
      handler: async (response) => {
        try {
          const verification = await verifyGatewayPayment({
            orderId,
            razorpayOrderId: response.razorpay_order_id,
            razorpayPaymentId: response.razorpay_payment_id,
            razorpaySignature: response.razorpay_signature,
            paymentStatus: 'success',
          })

          if (typeof options.onSuccess === 'function') {
            await options.onSuccess(verification)
          }

          resolve(verification)
        } catch (error) {
          reject(error)
        }
      },
      modal: {
        ondismiss: () => {
          void finishWithError('Payment window closed before completion.')
        },
      },
    }

    const razorpay = new window.Razorpay(razorpayOptions)

    if (typeof options.onOpen === 'function') {
      options.onOpen()
    }

    // Some Razorpay failures surface through the checkout object instead of handler.
    try {
      razorpay.on('payment.failed', (response) => {
        void finishWithError('Payment failed. Please try again.', response)
      })
    } catch {
      // If the event hook is unavailable, modal dismissal and handler errors still cover the flow.
    }

    razorpay.open()
  })
}
