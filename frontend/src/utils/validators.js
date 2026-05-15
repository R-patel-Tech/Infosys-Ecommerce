export function isValidEmail(email) {
  return /\S+@\S+\.\S+/.test(email)
}

export function isStrongPassword(password) {
  return /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&]).{8,}$/.test(password)
}

export function isValidPhone(phone) {
  return /^[0-9+\-\s]{10,15}$/.test(phone.trim())
}

export function isValidName(name) {
  return name.trim().length >= 2 && name.trim().length <= 80
}

export function isValidAddress(address) {
  return address.trim().length >= 10 && address.trim().length <= 500
}
