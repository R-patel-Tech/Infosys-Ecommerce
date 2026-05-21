import { useEffect, useState } from 'react'
import { get } from '../services/api.js'
import { normalizeProducts } from '../utils/normalizers.js'

export function useProducts() {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  async function fetchProducts() {
    const data = await get('/products')
    return normalizeProducts(data)
  }

  async function loadProducts() {
    setLoading(true)
    setError('')

    try {
      const nextProducts = await fetchProducts()
      setProducts(nextProducts)
      return nextProducts
    } catch (requestError) {
      setError(requestError.message || 'Unable to load products.')
      return []
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    let cancelled = false

    async function initialLoad() {
      setLoading(true)
      setError('')

      try {
        const nextProducts = await fetchProducts()
        if (!cancelled) {
          setProducts(nextProducts)
        }
      } catch (requestError) {
        if (!cancelled) {
          setError(requestError.message || 'Unable to load products.')
        }
      } finally {
        if (!cancelled) {
          setLoading(false)
        }
      }
    }

    initialLoad()

    return () => {
      cancelled = true
    }
  }, [])

  return { products, loading, error, setProducts, setLoading, setError, loadProducts }
}
