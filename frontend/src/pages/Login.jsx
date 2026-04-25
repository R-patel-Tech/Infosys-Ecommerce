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
      const token = data.message || data.token

      if (token) {
        sessionStorage.setItem('authToken', token)
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
    <main className="page-shell">
      <form className="auth-form simple-auth-form" onSubmit={handleSubmit}>
        <label className="field">
          <span>Email</span>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            placeholder="Enter email"
            autoComplete="email"
            required
          />
        </label>

        <label className="field">
          <span>Password</span>
          <input
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            placeholder="Enter password"
            autoComplete="current-password"
            required
          />
        </label>

        <Button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Logging in...' : 'Login'}
        </Button>

        {feedback.message ? (
          <p className={`form-message ${feedback.type}`}>{feedback.message}</p>
        ) : null}

        <p className="auth-switch">
          Don't have an account?{' '}
          <button type="button" className="inline-button" onClick={() => onSwitch('register')}>
            Register
          </button>
        </p>
      </form>
    </main>
  )
}

export default Login
