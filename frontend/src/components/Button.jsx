function Button({
  children,
  className = '',
  variant = 'primary',
  size = 'medium',
  iconLeft = null,
  iconRight = null,
  type = 'button',
  ...props
}) {
  const resolvedClassName = [
    'button',
    `button-${variant}`,
    `button-${size}`,
    className
  ].filter(Boolean).join(' ')

  return (
    <button type={type} className={resolvedClassName} {...props}>
      {iconLeft ? <span className="button-icon button-icon-left" aria-hidden="true">{iconLeft}</span> : null}
      {children}
      {iconRight ? <span className="button-icon button-icon-right" aria-hidden="true">{iconRight}</span> : null}
    </button>
  )
}

export default Button
