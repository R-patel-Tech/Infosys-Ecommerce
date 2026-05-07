import { useEffect, useState } from 'react'
import { TOAST_CHANNEL } from '../utils/toast.js'

function ToastHost() {
  const [toasts, setToasts] = useState([])

  useEffect(() => {
    function handleToast(event) {
      const nextToast = event?.detail
      if (!nextToast?.id || !nextToast?.message) {
        return
      }

      setToasts((current) => [...current, nextToast])
      window.setTimeout(() => {
        setToasts((current) => current.filter((toast) => toast.id !== nextToast.id))
      }, 2600)
    }

    window.addEventListener(TOAST_CHANNEL, handleToast)
    return () => window.removeEventListener(TOAST_CHANNEL, handleToast)
  }, [])

  if (toasts.length === 0) {
    return null
  }

  return (
    <div className="toast-viewport" aria-live="polite" aria-atomic="true">
      {toasts.map((toast) => (
        <div key={toast.id} className={`toast toast-${toast.type || 'info'}`}>
          {toast.message}
        </div>
      ))}
    </div>
  )
}

export default ToastHost
