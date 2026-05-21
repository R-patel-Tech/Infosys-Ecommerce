const TOAST_EVENT = 'app:toast'

export function showToast(message, type = 'info') {
  if (typeof window === 'undefined') {
    return
  }

  window.dispatchEvent(
    new CustomEvent(TOAST_EVENT, {
      detail: {
        id: `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
        message,
        type,
      },
    })
  )
}

export const TOAST_CHANNEL = TOAST_EVENT
