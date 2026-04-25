import { useState } from 'react'
import Button from '../components/Button.jsx'
import { registerUser } from '../services/authService.js'
import { isStrongPassword, isValidEmail, isValidPhone } from '../utils/validators.js'

const initialState = {
  name: '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: '',
}

function Register({ onSwitch }) {
  const [formData, setFormData] = useState(initialState)
  const [feedback, setFeedback] = useState({ type: '', message: '' })
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const [passwordStrength, setPasswordStrength] = useState('')

  function handleChange(event) {
    const { name, value } = event.target
    setFormData((current) => ({
      ...current,
      [name]: value,
    }))

    // Update password strength indicator
    if (name === 'password') {
      updatePasswordStrength(value)
    }
  }

  function updatePasswordStrength(password) {
    if (!password) {
      setPasswordStrength('')
      return
    }

    const hasLength = password.length >= 8
    const hasUpper = /[A-Z]/.test(password)
    const hasLower = /[a-z]/.test(password)
    const hasNumber = /\d/.test(password)
    const hasSpecial = /[@$!%*?&]/.test(password)

    const criteria = [hasLength, hasUpper, hasLower, hasNumber, hasSpecial]
    const met = criteria.filter(Boolean).length

    if (met < 3) setPasswordStrength('weak')
    else if (met < 5) setPasswordStrength('medium')
    else setPasswordStrength('strong')
  }

  async function handleSubmit(event) {
    event.preventDefault()

    if (!formData.name.trim()) {
      setFeedback({ type: 'error', message: 'Name is required.' })
      return
    }

    if (!isValidEmail(formData.email)) {
      setFeedback({ type: 'error', message: 'Enter a valid email address.' })
      return
    }

    if (!isStrongPassword(formData.password)) {
      setFeedback({
        type: 'error',
        message:
          'Password must be 8+ characters and include uppercase, lowercase, number, and special character.',
      })
      return
    }

    if (!isValidPhone(formData.phone)) {
      setFeedback({ type: 'error', message: 'Enter a valid phone number.' })
      return
    }

    if (formData.password !== formData.confirmPassword) {
      setFeedback({ type: 'error', message: 'Passwords do not match.' })
      return
    }

    setIsSubmitting(true)
    setFeedback({ type: '', message: '' })

    try {
      const data = await registerUser({
        name: formData.name,
        phone: formData.phone,
        email: formData.email,
        password: formData.password,
      })

      setFeedback({
        type: 'success',
        message: `Account created for ${data.name || formData.name}.`,
      })
      setFormData(initialState)
      setPasswordStrength('')
    } catch (error) {
      setFeedback({
        type: 'error',
        message: error.message || 'Registration failed. Please check the backend and try again.',
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
          <h1 className="auth-title">Create Account</h1>
          <p className="auth-subtitle">Join us and start your shopping journey</p>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label className="form-label" htmlFor="name">
                Full Name
              </label>
              <div className="input-wrapper">
                <input
                  id="name"
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  placeholder="Enter your full name"
                  autoComplete="name"
                  required
                  className="form-input"
                />
              </div>
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="phone">
                Phone Number
              </label>
              <div className="input-wrapper">
                <input
                  id="phone"
                  type="tel"
                  name="phone"
                  value={formData.phone}
                  onChange={handleChange}
                  placeholder="Enter your phone number"
                  autoComplete="tel"
                  required
                  className="form-input"
                />
              </div>
            </div>
          </div>

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
                placeholder="Create a strong password"
                autoComplete="new-password"
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
            {passwordStrength && (
              <div className={`password-strength strength-${passwordStrength}`}>
                <span className="strength-indicator"></span>
                <span className="strength-text">
                  {passwordStrength === 'weak' && 'Weak password'}
                  {passwordStrength === 'medium' && 'Good password'}
                  {passwordStrength === 'strong' && 'Strong password'}
                </span>
              </div>
            )}
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="confirmPassword">
              Confirm Password
            </label>
            <div className="input-wrapper">
              <input
                id="confirmPassword"
                type={showConfirmPassword ? 'text' : 'password'}
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                placeholder="Confirm your password"
                autoComplete="new-password"
                required
                className="form-input"
              />
              <button
                type="button"
                className="password-toggle"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                aria-label={showConfirmPassword ? 'Hide password' : 'Show password'}
              >
                {showConfirmPassword ? '🙈' : '👁️'}
              </button>
            </div>
          </div>

          <Button type="submit" disabled={isSubmitting} className="auth-submit-btn">
            {isSubmitting ? (
              <>
                <span className="spinner"></span>
                Creating account...
              </>
            ) : (
              'Create Account'
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
            Already have an account?{' '}
            <button
              type="button"
              className="auth-link"
              onClick={() => onSwitch('login')}
            >
              Sign in
            </button>
          </p>
        </div>
      </div>

      <div className="auth-decoration">
        <div className="decoration-content">
          <h2>Start Your Journey</h2>
          <p>Create your account and unlock exclusive deals and offers</p>
          <div className="decoration-features">
            <div className="feature">
              <span className="feature-icon">🚚</span>
              <span className="feature-text">Free Shipping</span>
            </div>
            <div className="feature">
              <span className="feature-icon">🔒</span>
              <span className="feature-text">Secure Payments</span>
            </div>
            <div className="feature">
              <span className="feature-icon">🎯</span>
              <span className="feature-text">Personalized Deals</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Register
