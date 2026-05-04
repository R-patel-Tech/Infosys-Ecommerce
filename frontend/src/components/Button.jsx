function Button({ children, className = '', variant = 'primary', size = 'medium', ...props }) {
  const resolvedClassName = [
    'button',
    `button-${variant}`,
    `button-${size}`,
    className
  ].filter(Boolean).join(' ')

  return (
    <button className={resolvedClassName} {...props}>
      {children}
    </button>
  )
}

export default Button
