function Button({ children, className = '', ...props }) {
  const resolvedClassName = ['button', className].filter(Boolean).join(' ')

  return (
    <button className={resolvedClassName} {...props}>
      {children}
    </button>
  )
}

export default Button
