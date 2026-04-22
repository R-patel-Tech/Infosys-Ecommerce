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

  function handleChange(event) {
    const { name, value } = event.target
    setFormData((current) => ({
      ...current,
      [name]: value,
    }))
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
    <main className="page-shell">
      <form className="auth-form simple-auth-form" onSubmit={handleSubmit}>
        <label className="field">
          <span>Name</span>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            placeholder="Enter name"
            autoComplete="name"
            required
          />
        </label>

        <label className="field">
          <span>Phone</span>
          <input
            type="tel"
            name="phone"
            value={formData.phone}
            onChange={handleChange}
            placeholder="Enter phone number"
            autoComplete="tel"
            required
          />
        </label>

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
            autoComplete="new-password"
            required
          />
        </label>

        <label className="field">
          <span>Confirm Password</span>
          <input
            type="password"
            name="confirmPassword"
            value={formData.confirmPassword}
            onChange={handleChange}
            placeholder="Confirm password"
            autoComplete="new-password"
            required
          />
        </label>

        <Button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Registering...' : 'Register'}
        </Button>

        {feedback.message ? (
          <p className={`form-message ${feedback.type}`}>{feedback.message}</p>
        ) : null}

        <p className="auth-switch">
          Already have an account?{' '}
          <button type="button" className="inline-button" onClick={() => onSwitch('login')}>
            Login
          </button>
        </p>
      </form>
    </main>
  )
}

export default Register
