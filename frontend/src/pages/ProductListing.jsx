import { useState } from 'react'
import Button from '../components/Button.jsx'
import ProductCard from '../components/ProductCard.jsx'
import { useProducts } from '../hooks/useProducts.js'

function ProductListing({ onBack, onShowDetails }) {
  const [draftSearch, setDraftSearch] = useState('')
  const [draftCategory, setDraftCategory] = useState('')
  const [activeSearch, setActiveSearch] = useState('')
  const [activeCategory, setActiveCategory] = useState('')
  const params = new URLSearchParams()
  if (activeSearch.trim()) {
    params.set('search', activeSearch.trim())
  }
  if (activeCategory.trim()) {
    params.set('category', activeCategory.trim())
  }
  const queryString = params.toString()

  const { products, loading, error, loadProducts } = useProducts(queryString)
  const categories = [...new Set(products.map((product) => product.category?.trim()).filter(Boolean))].sort((left, right) =>
    left.localeCompare(right)
  )

  function handleApplyFilters(event) {
    event.preventDefault()
    setActiveSearch(draftSearch)
    setActiveCategory(draftCategory)
  }

  function handleResetFilters() {
    setDraftSearch('')
    setDraftCategory('')
    setActiveSearch('')
    setActiveCategory('')
  }

  const hasActiveFilters = Boolean(activeSearch.trim() || activeCategory.trim())

  return (
    <main className="page-shell dashboard-shell">
      <section className="dashboard-card product-page-card">
        <div className="dashboard-header">
          <div>
            <p className="eyebrow">Product listing</p>
            <h1>Featured products</h1>
            <p className="dashboard-copy">Products are loaded dynamically from the API.</p>
          </div>
          <Button onClick={onBack}>Back</Button>
        </div>

        <div className="product-listing">
          <form className="product-filter-bar" onSubmit={handleApplyFilters}>
            <div className="product-filter-group">
              <label className="form-label" htmlFor="product-search">
                Search
              </label>
              <input
                id="product-search"
                className="form-input"
                type="search"
                value={draftSearch}
                onChange={(event) => setDraftSearch(event.target.value)}
                placeholder="Search products by name or keyword"
              />
            </div>

            <div className="product-filter-group">
              <label className="form-label" htmlFor="product-category">
                Category
              </label>
              <select
                id="product-category"
                className="form-input"
                value={draftCategory}
                onChange={(event) => setDraftCategory(event.target.value)}
              >
                <option value="">All categories</option>
                {categories.map((category) => (
                  <option key={category} value={category}>
                    {category}
                  </option>
                ))}
              </select>
            </div>

            <div className="product-filter-actions">
              <Button type="submit">Apply Filters</Button>
              <Button
                type="button"
                variant="secondary"
                onClick={handleResetFilters}
                disabled={!hasActiveFilters && !draftSearch.trim() && !draftCategory.trim()}
              >
                Reset
              </Button>
              <Button type="button" variant="secondary" onClick={loadProducts} disabled={loading}>
                Refresh
              </Button>
            </div>
          </form>

          <div className="product-listing-header">
            <h2>Available items</h2>
            {!loading && !error ? (
              <p className="product-count">
                {products.length} {products.length === 1 ? 'item' : 'items'}
              </p>
            ) : null}
          </div>

          {loading ? (
            <p className="product-state">Loading products...</p>
          ) : products.length === 0 ? (
            <div className="product-state product-empty-state">
              <p>{error || 'No products match the current filters.'}</p>
              <div className="product-filter-actions">
                <Button type="button" variant="secondary" size="small" onClick={handleResetFilters}>
                  Clear filters
                </Button>
                <Button type="button" variant="secondary" size="small" onClick={loadProducts}>
                  Retry
                </Button>
              </div>
            </div>
          ) : (
            <div className="product-grid">
              {products.map((product, index) => {
                const key = product.productId ?? product.id ?? `${product.name ?? 'product'}-${index}`

                return <ProductCard key={key} product={product} onShowDetails={onShowDetails} />
              })}
            </div>
          )}
        </div>
      </section>
    </main>
  )
}

export default ProductListing
