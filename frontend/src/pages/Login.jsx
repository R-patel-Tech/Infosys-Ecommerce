import { useState } from 'react'
import Button from '../components/Button.jsx'
import { loginUser } from '../services/authService.js'
import { isValidEmail } from '../utils/validators.js'
import { showToast } from '../utils/toast.js'

const initialState = {
  email: '',
  password: '',
}

function MailIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="input-icon-svg">
      <path
        d="M4 6h16v12H4z"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinejoin="round"
      />
      <path
        d="M4 7l8 6 8-6"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  )
}

function LockIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="input-icon-svg">
      <path
        d="M7 11V8a5 5 0 0 1 10 0v3"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
      />
      <path
        d="M6 11h12v9H6z"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinejoin="round"
      />
    </svg>
  )
}

function EyeIcon({ open }) {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="input-icon-svg">
      {open ? (
        <>
          <path
            d="M4 12s3.5-6 8-6 8 6 8 6-3.5 6-8 6-8-6-8-6Z"
            fill="none"
            stroke="currentColor"
            strokeWidth="1.8"
            strokeLinejoin="round"
          />
          <circle cx="12" cy="12" r="2.5" fill="none" stroke="currentColor" strokeWidth="1.8" />
          <path d="M5 5l14 14" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" />
        </>
      ) : (
        <>
          <path
            d="M2.5 12s3.5-6 9.5-6 9.5 6 9.5 6-3.5 6-9.5 6-9.5-6-9.5-6Z"
            fill="none"
            stroke="currentColor"
            strokeWidth="1.8"
            strokeLinejoin="round"
          />
          <circle cx="12" cy="12" r="2.8" fill="none" stroke="currentColor" strokeWidth="1.8" />
        </>
      )}
    </svg>
  )
}

function Login({ onSwitch, onLoginSuccess }) {
  const [formData, setFormData] = useState(initialState)
  const [feedback, setFeedback] = useState({ type: '', message: '' })
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [showPassword, setShowPassword] = useState(false)

  function handleChange(event) {
    const { name, value } = event.target
    setFormData((current) => ({
      ...current,
      [name]: value,
    }))
  }

  async function handleSubmit(event) {
    event.preventDefault()

    if (!isValidEmail(formData.email)) {
      setFeedback({ type: 'error', message: 'Enter a valid email address.' })
      showToast('Enter a valid email address.', 'error')
      return
    }

    setIsSubmitting(true)
    setFeedback({ type: '', message: '' })

    try {
      await loginUser(formData)
      showToast('Login successful.', 'success')
      onLoginSuccess?.()
    } catch (error) {
      const message = error.message || 'Login failed. Please try again.'
      setFeedback({ type: 'error', message })
      showToast(message, 'error')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <main className="auth-shell">
      <section className="auth-container login-shell">
        <div className="auth-card glass-card login-card">
          <div className="auth-header">
            <div className="auth-logo">
              <div className="logo-icon">R</div>
            </div>
            <div className="auth-kicker">Premium store access</div>
            <h1 className="auth-title">Welcome back</h1>
            <p className="auth-subtitle">Sign in to continue shopping and manage your orders.</p>
          </div>

          <form className="auth-form" onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label" htmlFor="email">
                Email Address
              </label>
              <div className="input-wrapper premium-input">
                <span className="input-icon-shell" aria-hidden="true">
                  <MailIcon />
                </span>
                <input
                  id="email"
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  placeholder="you@example.com"
                  autoComplete="email"
                  required
                  className="form-input premium-form-input"
                />
              </div>
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="password">
                Password
              </label>
              <div className="input-wrapper premium-input">
                <span className="input-icon-shell" aria-hidden="true">
                  <LockIcon />
                </span>
                <input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  placeholder="Enter your password"
                  autoComplete="current-password"
                  required
                  className="form-input premium-form-input"
                />
                <button
                  type="button"
                  className="password-toggle premium-password-toggle"
                  onClick={() => setShowPassword((current) => !current)}
                  aria-label={showPassword ? 'Hide password' : 'Show password'}
                >
                  <EyeIcon open={showPassword} />
                </button>
              </div>
            </div>

            <Button type="submit" disabled={isSubmitting} className="auth-submit-btn premium-cta">
              {isSubmitting ? (
                <>
                  <span className="spinner" />
                  Signing in...
                </>
              ) : (
                'Sign In'
              )}
            </Button>

            {feedback.message ? (
              <div className={`alert alert-${feedback.type}`}>
                <span className="alert-icon">{feedback.type === 'error' ? 'ERR' : 'OK'}</span>
                {feedback.message}
              </div>
            ) : null}
          </form>

          <div className="auth-footer premium-footer">
            <p>
              Don&apos;t have an account?{' '}
              <button type="button" className="auth-link" onClick={() => onSwitch('register')}>
                Sign up
              </button>
            </p>
            <p>
              Admin access?{' '}
              <button type="button" className="auth-link" onClick={() => onSwitch('admin-login')}>
                Admin Login
              </button>
            </p>
          </div>
        </div>

        <aside className="auth-decoration login-decoration glass-card">
          <div className="decoration-content">
            <span className="decorative-chip">Apple-style smooth visuals</span>
            <h2>Discover amazing products with a calm, premium flow.</h2>
            <p>Glass surfaces, soft shadows, and motion that feels polished rather than busy.</p>
            <div className="decoration-stats">
              <div className="stat">
                <span className="stat-number">10K+</span>
                <span className="stat-label">Products</span>
              </div>
              <div className="stat">
                <span className="stat-number">50K+</span>
                <span className="stat-label">Customers</span>
              </div>
              <div className="stat">
                <span className="stat-number">99%</span>
                <span className="stat-label">Satisfaction</span>
              </div>
            </div>
          </div>
        </aside>
      </section>
    </main>
  )
}

export default Login
