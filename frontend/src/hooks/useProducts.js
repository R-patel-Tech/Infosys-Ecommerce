import { useEffect, useState } from 'react'
import { get } from '../services/api.js'
import { normalizeProducts } from '../utils/normalizers.js'
import { defaultProducts } from '../utils/defaultProducts.js'

function getCacheKey(queryString = '') {
  return `product-cache:${queryString || 'all'}`
}

function readCachedProducts(queryString = '') {
  if (typeof window === 'undefined') {
    return []
  }

  try {
    const raw = window.localStorage.getItem(getCacheKey(queryString))
    const parsed = raw ? JSON.parse(raw) : []
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

function writeCachedProducts(queryString, products) {
  if (typeof window === 'undefined') {
    return
  }

  try {
    window.localStorage.setItem(getCacheKey(queryString), JSON.stringify(products))
  } catch {
    // Ignore cache write failures.
  }
}

function getFallbackProducts(queryString = '') {
  return queryString ? [] : defaultProducts
}

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
      const nextProducts = await fetchProducts(queryString)
      writeCachedProducts(queryString, nextProducts)
      setProducts(nextProducts)
      return nextProducts
    } catch (requestError) {
      const cachedProducts = readCachedProducts(queryString)
      const fallbackProducts = getFallbackProducts(queryString)

      if (cachedProducts.length > 0) {
        setProducts(cachedProducts)
        setError('')
        return cachedProducts
      }

      if (fallbackProducts.length > 0) {
        setProducts(fallbackProducts)
        writeCachedProducts(queryString, fallbackProducts)
        setError('')
        return fallbackProducts
      }

      setError('Unable to load products right now. Please try again.')
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
        writeCachedProducts(queryString, nextProducts)
        if (!cancelled) {
          setProducts(nextProducts)
        }
      } catch (requestError) {
        if (!cancelled) {
          const cachedProducts = readCachedProducts(queryString)
          const fallbackProducts = getFallbackProducts(queryString)

          if (cachedProducts.length > 0) {
            setProducts(cachedProducts)
            setError('')
          } else if (fallbackProducts.length > 0) {
            setProducts(fallbackProducts)
            writeCachedProducts(queryString, fallbackProducts)
            setError('')
          } else {
            setError('Unable to load products right now. Please try again.')
          }
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
