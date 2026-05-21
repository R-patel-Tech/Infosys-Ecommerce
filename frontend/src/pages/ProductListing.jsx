import Button from '../components/Button.jsx'
import ProductCard from '../components/ProductCard.jsx'
import { useProducts } from '../hooks/useProducts.js'

function ProductListing({ onBack, onShowDetails }) {
  const { products, loading, error } = useProducts()

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
          <div className="product-listing-header">
            <h2>Available items</h2>
            {!loading && !error ? <p className="product-count">{products.length} items</p> : null}
          </div>

          {loading ? (
            <p className="product-state">Loading products...</p>
          ) : error ? (
            <p className="product-state product-state-error">{error}</p>
          ) : products.length === 0 ? (
            <p className="product-state">No products available right now.</p>
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
