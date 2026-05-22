import { useEffect, useState } from 'react'
import { get } from '../services/api.js'
import { normalizeProducts } from '../utils/normalizers.js'

function buildProductsPath(queryString = '') {
  return queryString ? `/products?${queryString}` : '/products'
}

export function useProducts(queryString = '') {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  async function fetchProducts(currentQueryString = queryString) {
    const data = await get(buildProductsPath(currentQueryString))
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
  }, [queryString])

  return { products, loading, error, setProducts, setLoading, setError, loadProducts }
}
