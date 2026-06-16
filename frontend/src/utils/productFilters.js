export function filterProducts(products, search = '', category = '') {
  const searchTerm = search.trim().toLowerCase()
  const selectedCategory = category.trim().toLowerCase()

  return products.filter((product) => {
    const productName = (product?.name ?? '').toLowerCase()
    const productDescription = (product?.description ?? '').toLowerCase()
    const productCategory = (product?.category ?? '').toLowerCase()

    const matchesSearch =
      !searchTerm ||
      productName.includes(searchTerm) ||
      productDescription.includes(searchTerm) ||
      productCategory.includes(searchTerm)

    const matchesCategory = !selectedCategory || productCategory === selectedCategory

    return matchesSearch && matchesCategory
  })
}
