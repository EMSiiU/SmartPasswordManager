// Contratos TypeScript que espejo exacto de los DTOs del backend.
// Si el backend cambia un campo, TypeScript nos avisa aquí.

export interface AuthResponse {
  accessToken: string
  tokenType: string
}

// Lo que devuelve GET /vault (lista, sin contraseña)
export interface CredentialSummary {
  id: number
  titulo: string
  usuarioCuenta: string | null
  email: string | null
  url: string | null
}

// Lo que devuelve GET /vault/{id} (detalle, con contraseña descifrada)
export interface CredentialDetail extends CredentialSummary {
  password: string
  notas: string | null
}

// Lo que enviamos en POST /vault y PUT /vault/{id}
export interface CredentialRequest {
  titulo: string
  usuarioCuenta?: string
  email?: string
  password: string
  url?: string
  notas?: string
}

export type PasswordStrength = 'weak' | 'medium' | 'strong'
