import { Link, NavLink } from 'react-router-dom'
import Button from './Button.jsx'

function DashboardIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="nav-icon">
      <path
        d="M4 4h6v6H4zM14 4h6v9h-6zM4 14h6v6H4zM14 17h6v3h-6z"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  )
}

function ProductIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="nav-icon">
      <path
        d="M4 6.5L12 3l8 3.5V17l-8 4-8-4z"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        d="M12 3v18"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
      />
    </svg>
  )
}

function CartIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="nav-icon">
      <path
        d="M3.5 5h2l1.8 9.2A2 2 0 0 0 9.3 16h8.1a2 2 0 0 0 1.9-1.4L21.4 8H7"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <circle cx="10" cy="20" r="1.4" fill="currentColor" />
      <circle cx="17" cy="20" r="1.4" fill="currentColor" />
    </svg>
  )
}

function HistoryIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="nav-icon">
      <path
        d="M4 12a8 8 0 1 1 2.34 5.66"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
      />
      <path
        d="M4 4v4h4"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        d="M12 7v5l3 2"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  )
}

function UserIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="nav-icon">
      <path
        d="M20 21a8 8 0 0 0-16 0"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
      />
      <circle cx="12" cy="8" r="4" fill="none" stroke="currentColor" strokeWidth="1.8" />
    </svg>
  )
}

function MenuIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="nav-icon">
      <path
        d="M4 7h16M4 12h16M4 17h16"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
      />
    </svg>
  )
}

function CloseIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="nav-icon">
      <path
        d="M6 6l12 12M18 6L6 18"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
      />
    </svg>
  )
}

function LogOutIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="nav-icon">
      <path
        d="M10 17l5-5-5-5"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        d="M15 12H4"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
      />
      <path
        d="M15 4h4a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1h-4"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  )
}

function Navbar({
  menuOpen,
  onToggleMenu,
  onCloseMenu,
  onShowProducts,
  onShowCart,
  onShowOrders,
  onShowProfile,
  onLogout,
}) {
  const navLinkClass = ({ isActive }) => `dashboard-nav-link nav-pill ${isActive ? 'active' : ''}`

  function closeAndRun(handler) {
    onCloseMenu?.()
    if (typeof handler === 'function') {
      handler()
    }
  }

  return (
    <div className="navbar-shell">
      <div className="navbar-brand">
        <Link to="/dashboard" className="brand-link" onClick={onCloseMenu}>
          <span className="brand-mark">R</span>
          <span className="brand-copy">
            <strong>Raj_ecommerce</strong>
            <small>Premium commerce workspace</small>
          </span>
        </Link>
      </div>

      <nav className={`navbar-links ${menuOpen ? 'open' : ''}`} aria-label="Primary">
        <NavLink to="/dashboard" className={navLinkClass} onClick={onCloseMenu} end>
          <DashboardIcon />
          Dashboard
        </NavLink>
        <NavLink to="/products" className={navLinkClass} onClick={() => closeAndRun(onShowProducts)}>
          <ProductIcon />
          Browse Products
        </NavLink>
        <NavLink to="/cart" className={navLinkClass} onClick={() => closeAndRun(onShowCart)}>
          <CartIcon />
          <span>View Cart</span>
        </NavLink>
        <NavLink to="/orders" className={navLinkClass} onClick={() => closeAndRun(onShowOrders)}>
          <HistoryIcon />
          <span>Order History</span>
        </NavLink>
      </nav>

      <div className="navbar-actions">
        <button type="button" className="profile-pill" aria-label="User profile" onClick={() => closeAndRun(onShowProfile)}>
          <UserIcon />
          <span>Profile</span>
        </button>
        <Button
          type="button"
          variant="secondary"
          className="navbar-logout"
          onClick={() => closeAndRun(onLogout)}
          iconLeft={<LogOutIcon />}
        >
          Logout
        </Button>
        <button
          type="button"
          className={`navbar-toggle ${menuOpen ? 'open' : ''}`}
          onClick={onToggleMenu}
          aria-label={menuOpen ? 'Close menu' : 'Open menu'}
          aria-expanded={menuOpen}
        >
          {menuOpen ? <CloseIcon /> : <MenuIcon />}
        </button>
      </div>

      <div className={`navbar-mobile-panel ${menuOpen ? 'open' : ''}`}>
        <NavLink to="/dashboard" className={navLinkClass} onClick={onCloseMenu} end>
          <DashboardIcon />
          Dashboard
        </NavLink>
        <NavLink to="/products" className={navLinkClass} onClick={() => closeAndRun(onShowProducts)}>
          <ProductIcon />
          Browse Products
        </NavLink>
        <NavLink to="/cart" className={navLinkClass} onClick={() => closeAndRun(onShowCart)}>
          <CartIcon />
          <span>View Cart</span>
        </NavLink>
        <NavLink to="/orders" className={navLinkClass} onClick={() => closeAndRun(onShowOrders)}>
          <HistoryIcon />
          <span>Order History</span>
        </NavLink>
        <button type="button" className="mobile-profile" onClick={() => closeAndRun(onShowProfile)}>
          <UserIcon />
          <span>Profile</span>
        </button>
        <Button
          type="button"
          variant="secondary"
          className="mobile-logout"
          onClick={() => closeAndRun(onLogout)}
          iconLeft={<LogOutIcon />}
        >
          Logout
        </Button>
      </div>
    </div>
  )
}

export default Navbar
