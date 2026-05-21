import { useState } from 'react'
import Button from '../components/Button.jsx'
import { isValidEmail } from '../utils/validators.js'
import '../styles/AdminLogin.css'
import { showToast } from '../utils/toast.js'

const initialState = {
  email: '',
  password: '',
}

function AdminLogin({ onSwitch, onAdminLoginSuccess }) {
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
      const message = 'Enter a valid admin email address.'
      setFeedback({ type: 'error', message })
      showToast(message, 'error')
      return
    }

    if (!formData.password.trim()) {
      const message = 'Password is required.'
      setFeedback({ type: 'error', message })
      showToast(message, 'error')
      return
    }

    setIsSubmitting(true)
    setFeedback({ type: '', message: '' })

    try {
      await new Promise((resolve) => window.setTimeout(resolve, 800))
      const message = 'Admin login successful.'
      setFeedback({ type: 'success', message })
      showToast(message, 'success')
      onAdminLoginSuccess?.()
    } catch (error) {
      const message = error.message || 'Admin login failed. Please try again.'
      setFeedback({ type: 'error', message })
      showToast(message, 'error')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="admin-login-container">
      <div className="admin-login-card">
        <div className="admin-login-header">
          <div className="admin-logo">
            <div className="admin-logo-icon">A</div>
          </div>
          <h1 className="admin-login-title">Admin Portal</h1>
          <p className="admin-login-subtitle">Secure access for administrators</p>
        </div>

        <form className="admin-login-form" onSubmit={handleSubmit}>
          <div className="admin-form-group">
            <label className="admin-form-label" htmlFor="admin-email">
              Admin Email Address
            </label>
            <div className="admin-input-wrapper">
              <input
                id="admin-email"
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="Enter admin email"
                autoComplete="email"
                required
                className="admin-form-input"
              />
            </div>
          </div>

          <div className="admin-form-group">
            <label className="admin-form-label" htmlFor="admin-password">
              Password
            </label>
            <div className="admin-input-wrapper">
              <input
                id="admin-password"
                type={showPassword ? 'text' : 'password'}
                name="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="Enter password"
                autoComplete="current-password"
                required
                className="admin-form-input"
              />
              <button
                type="button"
                className="admin-password-toggle"
                onClick={() => setShowPassword((current) => !current)}
                aria-label={showPassword ? 'Hide password' : 'Show password'}
              >
                {showPassword ? 'Hide' : 'Show'}
              </button>
            </div>
          </div>

          <Button type="submit" disabled={isSubmitting} className="admin-login-btn">
            {isSubmitting ? (
              <>
                <span className="admin-spinner" />
                Authenticating...
              </>
            ) : (
              'Secure Login'
            )}
          </Button>

          {feedback.message ? (
            <div className={`admin-alert admin-alert-${feedback.type}`}>
              <span className="admin-alert-icon">{feedback.type === 'error' ? 'ERR' : 'OK'}</span>
              {feedback.message}
            </div>
          ) : null}
        </form>

        <div className="admin-login-footer">
          <a href="/" className="admin-forgot-link">
            Forgot Admin Password?
          </a>
          <p>
            Not an admin?{' '}
            <button type="button" className="admin-switch-link" onClick={() => onSwitch('login')}>
              User Login
            </button>
          </p>
        </div>
      </div>

      <div className="admin-login-decoration">
        <div className="admin-decoration-content">
          <h2>Administrative Access</h2>
          <p>Manage products, users, and system settings securely</p>
          <div className="admin-decoration-stats">
            <div className="admin-stat">
              <span className="admin-stat-number">24/7</span>
              <span className="admin-stat-label">Monitoring</span>
            </div>
            <div className="admin-stat">
              <span className="admin-stat-number">100%</span>
              <span className="admin-stat-label">Security</span>
            </div>
            <div className="admin-stat">
              <span className="admin-stat-number">Admin</span>
              <span className="admin-stat-label">Only</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default AdminLogin
