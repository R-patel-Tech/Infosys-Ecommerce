import { useState } from 'react'
import Button from '../components/Button.jsx'
import { loginUser } from '../services/authService.js'
import { isValidEmail } from '../utils/validators.js'

const initialState = {
  email: '',
  password: '',
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
      return
    }

    setIsSubmitting(true)
    setFeedback({ type: '', message: '' })

    try {
      const data = await loginUser(formData)
      const token = data.token || data.message || data

      if (token) {
        sessionStorage.setItem('authToken', token)
        if (data?.userId != null) {
          sessionStorage.setItem('userId', String(data.userId))
        }
        onLoginSuccess?.()
        return
      }

      setFeedback({
        type: 'success',
        message: 'Login successful.',
      })
    } catch (error) {
      setFeedback({
        type: 'error',
        message: error.message || 'Login failed. Please check the backend and try again.',
      })
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-header">
          <div className="auth-logo">
            <div className="logo-icon">🛒</div>
          </div>
          <h1 className="auth-title">Welcome Back</h1>
          <p className="auth-subtitle">Sign in to your account to continue</p>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label" htmlFor="email">
              Email Address
            </label>
            <div className="input-wrapper">
              <input
                id="email"
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="Enter your email"
                autoComplete="email"
                required
                className="form-input"
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="password">
              Password
            </label>
            <div className="input-wrapper">
              <input
                id="password"
                type={showPassword ? 'text' : 'password'}
                name="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="Enter your password"
                autoComplete="current-password"
                required
                className="form-input"
              />
              <button
                type="button"
                className="password-toggle"
                onClick={() => setShowPassword(!showPassword)}
                aria-label={showPassword ? 'Hide password' : 'Show password'}
              >
                {showPassword ? '🙈' : '👁️'}
              </button>
            </div>
          </div>

          <Button type="submit" disabled={isSubmitting} className="auth-submit-btn">
            {isSubmitting ? (
              <>
                <span className="spinner"></span>
                Signing in...
              </>
            ) : (
              'Sign In'
            )}
          </Button>

          {feedback.message && (
            <div className={`alert alert-${feedback.type}`}>
              <span className="alert-icon">
                {feedback.type === 'error' ? '❌' : '✅'}
              </span>
              {feedback.message}
            </div>
          )}
        </form>

        <div className="auth-footer">
          <p>
            Don't have an account?{' '}
            <button
              type="button"
              className="auth-link"
              onClick={() => onSwitch('register')}
            >
              Sign up
            </button>
          </p>
          <p>
            Admin access?{' '}
            <button
              type="button"
              className="auth-link"
              onClick={() => onSwitch('admin-login')}
            >
              Admin Login
            </button>
          </p>
        </div>
      </div>

      <div className="auth-decoration">
        <div className="decoration-content">
          <h2>Discover Amazing Products</h2>
          <p>Join thousands of satisfied customers shopping with us</p>
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
      </div>
    </div>
  )
}

export default Login
