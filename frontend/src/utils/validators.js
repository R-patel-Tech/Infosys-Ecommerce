export function isValidEmail(email) {
  return /\S+@\S+\.\S+/.test(email)
}

export function isStrongPassword(password) {
  return /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&]).{8,}$/.test(password)
}

export function isValidPhone(phone) {
  return phone.trim().length >= 10
}
