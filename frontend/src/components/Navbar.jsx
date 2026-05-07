import { Link, NavLink } from 'react-router-dom'
import Button from './Button.jsx'

function CartIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="nav-icon">
      <path
        d="M3 5h2l2.2 9.2A2 2 0 0 0 9.15 16h7.9a2 2 0 0 0 1.95-1.55L21 8H7"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <circle cx="10" cy="20" r="1.5" fill="currentColor" />
      <circle cx="17" cy="20" r="1.5" fill="currentColor" />
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

function Navbar({ menuOpen, onToggleMenu, onCloseMenu, onShowProducts, onShowCart, onLogout }) {
  const navLinkClass = ({ isActive }) => `dashboard-nav-link ${isActive ? 'active' : ''}`

  return (
    <>
      <div className="navbar-brand">
        <Link to="/dashboard" className="brand-link" onClick={onCloseMenu}>
          <span className="brand-mark">R</span>
          <span className="brand-copy">
            <strong>Raj_ecommerce</strong>
            <small>Shopping dashboard</small>
          </span>
        </Link>
      </div>

      <nav className={`navbar-links ${menuOpen ? 'open' : ''}`} aria-label="Primary">
        <NavLink to="/dashboard" className={navLinkClass} onClick={onCloseMenu} end>
          Dashboard
        </NavLink>
        <NavLink to="/products" className={navLinkClass} onClick={onShowProducts}>
          Browse Products
        </NavLink>
        <NavLink to="/cart" className={navLinkClass} onClick={onShowCart}>
          <CartIcon />
          <span>View Cart</span>
        </NavLink>
      </nav>

      <div className="navbar-actions">
        <button type="button" className="profile-pill" aria-label="User profile">
          <UserIcon />
          <span>Profile</span>
        </button>
        <Button type="button" variant="secondary" className="navbar-logout" onClick={onLogout}>
          Logout
        </Button>
        <button
          type="button"
          className="navbar-toggle"
          onClick={onToggleMenu}
          aria-label={menuOpen ? 'Close menu' : 'Open menu'}
          aria-expanded={menuOpen}
        >
          {menuOpen ? <CloseIcon /> : <MenuIcon />}
        </button>
      </div>

      <div className={`navbar-mobile-panel ${menuOpen ? 'open' : ''}`}>
        <NavLink to="/dashboard" className={navLinkClass} onClick={onCloseMenu} end>
          Dashboard
        </NavLink>
        <NavLink to="/products" className={navLinkClass} onClick={onShowProducts}>
          Browse Products
        </NavLink>
        <NavLink to="/cart" className={navLinkClass} onClick={onShowCart}>
          <CartIcon />
          <span>View Cart</span>
        </NavLink>
        <button type="button" className="mobile-profile" onClick={onCloseMenu}>
          <UserIcon />
          <span>Profile</span>
        </button>
        <Button type="button" variant="secondary" className="mobile-logout" onClick={onLogout}>
          Logout
        </Button>
      </div>
    </>
  )
}

export default Navbar
