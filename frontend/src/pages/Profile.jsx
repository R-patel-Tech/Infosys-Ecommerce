import { useEffect, useState } from 'react'
import Button from '../components/Button.jsx'
import { logout as clearAuth } from '../services/authService.js'
import { getMyProfile, updateMyPassword, updateMyProfile, logoutUser } from '../services/userService.js'
import { isValidAddress, isValidName, isValidPhone, isStrongPassword } from '../utils/validators.js'

const initialProfile = {
  name: '',
  phone: '',
  address: '',
}

const initialPassword = {
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
}

function Profile({ onLogout }) {
  const [profile, setProfile] = useState(initialProfile)
  const [password, setPassword] = useState(initialPassword)
  const [loading, setLoading] = useState(true)
  const [profileSaving, setProfileSaving] = useState(false)
  const [passwordSaving, setPasswordSaving] = useState(false)
  const [successMessage, setSuccessMessage] = useState('')
  const [errorMessage, setErrorMessage] = useState('')

  useEffect(() => {
    let isMounted = true

    async function loadProfile() {
      setLoading(true)
      setErrorMessage('')

      try {
        const data = await getMyProfile()
        if (!isMounted) {
          return
        }

        setProfile({
          name: data?.name || '',
          phone: data?.phone || '',
          address: data?.address || '',
        })
      } catch (error) {
        if (isMounted) {
          setErrorMessage(error.message || 'Unable to load profile.')
        }
      } finally {
        if (isMounted) {
          setLoading(false)
        }
      }
    }

    loadProfile()

    return () => {
      isMounted = false
    }
  }, [])

  function handleProfileChange(event) {
    const { name, value } = event.target
    setProfile((current) => ({ ...current, [name]: value }))
  }

  function handlePasswordChange(event) {
    const { name, value } = event.target
    setPassword((current) => ({ ...current, [name]: value }))
  }

  async function handleProfileSubmit(event) {
    event.preventDefault()
    setSuccessMessage('')
    setErrorMessage('')

    if (!isValidName(profile.name)) {
      setErrorMessage('Name must be between 2 and 80 characters.')
      return
    }

    if (!isValidPhone(profile.phone)) {
      setErrorMessage('Phone number is invalid.')
      return
    }

    if (!isValidAddress(profile.address)) {
      setErrorMessage('Address must be at least 10 characters.')
      return
    }

    setProfileSaving(true)
    try {
      await updateMyProfile(profile)
      setSuccessMessage('Profile updated successfully.')
    } catch (error) {
      setErrorMessage(error.message || 'Profile update failed.')
    } finally {
      setProfileSaving(false)
    }
  }

  async function handlePasswordSubmit(event) {
    event.preventDefault()
    setSuccessMessage('')
    setErrorMessage('')

    if (!password.oldPassword.trim()) {
      setErrorMessage('Old password is required.')
      return
    }

    if (!isStrongPassword(password.newPassword)) {
      setErrorMessage('Password must contain uppercase, lowercase, number, and special character.')
      return
    }

    if (password.newPassword !== password.confirmPassword) {
      setErrorMessage('Passwords do not match.')
      return
    }

    setPasswordSaving(true)
    try {
      await updateMyPassword({
        oldPassword: password.oldPassword,
        newPassword: password.newPassword,
      })
      setPassword(initialPassword)
      setSuccessMessage('Password updated successfully.')
    } catch (error) {
      setErrorMessage(error.message || 'Password update failed.')
    } finally {
      setPasswordSaving(false)
    }
  }

  async function handleLogout() {
    if (typeof onLogout === 'function') {
      await onLogout()
      return
    }

    try {
      await logoutUser()
    } finally {
      clearAuth()
      window.location.replace('/login')
    }
  }

  if (loading) {
    return (
      <main className="page-shell dashboard-shell">
        <section className="dashboard-card">
          <p>Loading profile...</p>
        </section>
      </main>
    )
  }

  return (
    <main className="page-shell dashboard-shell">
      <section className="dashboard-card profile-shell">
        <div className="profile-hero">
          <div>
            <p className="eyebrow">My account</p>
            <h1>Profile & security</h1>
            <p className="dashboard-copy">
              Update your contact details, change your password, and safely end your session.
            </p>
          </div>
          <Button type="button" variant="secondary" onClick={handleLogout}>
            Logout
          </Button>
        </div>

        {successMessage ? <div className="alert alert-success">{successMessage}</div> : null}
        {errorMessage ? <div className="alert alert-error">{errorMessage}</div> : null}

        <div className="profile-grid">
          <form className="profile-card" onSubmit={handleProfileSubmit}>
            <h2>Update profile</h2>

            <label className="form-group">
              <span className="form-label">Name</span>
              <input
                name="name"
                className="form-input"
                value={profile.name}
                onChange={handleProfileChange}
                placeholder="Your name"
              />
            </label>

            <label className="form-group">
              <span className="form-label">Phone</span>
              <input
                name="phone"
                className="form-input"
                value={profile.phone}
                onChange={handleProfileChange}
                placeholder="9876543210"
              />
            </label>

            <label className="form-group">
              <span className="form-label">Address</span>
              <textarea
                name="address"
                className="form-input admin-textarea"
                value={profile.address}
                onChange={handleProfileChange}
                placeholder="Full address"
              />
            </label>

            <Button type="submit" disabled={profileSaving}>
              {profileSaving ? 'Saving...' : 'Save Profile'}
            </Button>
          </form>

          <form className="profile-card" onSubmit={handlePasswordSubmit}>
            <h2>Change password</h2>

            <label className="form-group">
              <span className="form-label">Old password</span>
              <input
                type="password"
                name="oldPassword"
                className="form-input"
                value={password.oldPassword}
                onChange={handlePasswordChange}
                placeholder="Current password"
              />
            </label>

            <label className="form-group">
              <span className="form-label">New password</span>
              <input
                type="password"
                name="newPassword"
                className="form-input"
                value={password.newPassword}
                onChange={handlePasswordChange}
                placeholder="New password"
              />
            </label>

            <label className="form-group">
              <span className="form-label">Confirm new password</span>
              <input
                type="password"
                name="confirmPassword"
                className="form-input"
                value={password.confirmPassword}
                onChange={handlePasswordChange}
                placeholder="Confirm new password"
              />
            </label>

            <Button type="submit" disabled={passwordSaving}>
              {passwordSaving ? 'Updating...' : 'Update Password'}
            </Button>
          </form>
        </div>
      </section>
    </main>
  )
}

export default Profile
