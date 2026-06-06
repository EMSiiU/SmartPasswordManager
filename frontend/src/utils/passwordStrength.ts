import type { PasswordStrength } from '../types'

// Evalúa la fuerza de una contraseña sin librerías externas.
// El resultado se usa para el punto de color en la lista de credenciales.
export function getPasswordStrength(password: string): PasswordStrength {
  if (!password || password.length < 8) return 'weak'

  const hasUpper   = /[A-Z]/.test(password)
  const hasLower   = /[a-z]/.test(password)
  const hasNumber  = /[0-9]/.test(password)
  const hasSymbol  = /[^A-Za-z0-9]/.test(password)
  const varietyCount = [hasUpper, hasLower, hasNumber, hasSymbol].filter(Boolean).length

  if (password.length >= 12 && varietyCount >= 3) return 'strong'
  if (password.length >= 8  && varietyCount >= 2) return 'medium'
  return 'weak'
}

// Genera una contraseña aleatoria según los parámetros del generador.
export function generatePassword(options: {
  length: number
  uppercase: boolean
  numbers: boolean
  symbols: boolean
}): string {
  const lower   = 'abcdefghijklmnopqrstuvwxyz'
  const upper   = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'
  const nums    = '0123456789'
  const symbols = '!@#$%^&*()_+-=[]{}|;:,.<>?'

  let charset = lower
  if (options.uppercase) charset += upper
  if (options.numbers)   charset += nums
  if (options.symbols)   charset += symbols

  // crypto.getRandomValues es criptográficamente seguro (no Math.random).
  const array = new Uint32Array(options.length)
  crypto.getRandomValues(array)
  return Array.from(array, (n) => charset[n % charset.length]).join('')
}
