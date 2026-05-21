import { useEffect, useState } from 'react'
import { getUserOrders, getStoredLastOrderHistory } from '../services/orderService.js'

export function useOrders() {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let cancelled = false

    async function loadOrders() {
      setLoading(true)
      setError('')

      try {
        const data = await getUserOrders()
        const sorted = [...data].sort((left, right) => {
          const leftTime = new Date(left.orderDate || 0).getTime()
          const rightTime = new Date(right.orderDate || 0).getTime()
          return rightTime - leftTime
        })

        if (cancelled) {
          return
        }

        setOrders(sorted.length > 0 ? sorted : getStoredLastOrderHistory())
      } catch (requestError) {
        if (cancelled) {
          return
        }

        const fallbackOrders = getStoredLastOrderHistory()
        if (fallbackOrders.length > 0) {
          setOrders(fallbackOrders)
          setError('')
        } else {
          setError(requestError.message || 'Unable to load your orders.')
        }
      } finally {
        if (!cancelled) {
          setLoading(false)
        }
      }
    }

    loadOrders()

    return () => {
      cancelled = true
    }
  }, [])

  return { orders, loading, error, setOrders, setLoading, setError }
}

