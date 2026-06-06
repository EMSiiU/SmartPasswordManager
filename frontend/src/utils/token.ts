// Utilidades para manejar el JWT en sessionStorage.
//
// ¿Por qué sessionStorage y no localStorage?
// - sessionStorage se borra al cerrar la pestaña/navegador.
// - localStorage persiste indefinidamente → mayor ventana de ataque XSS.
// - DEUDA TÉCNICA: en Etapa 4 migrar a httpOnly cookies (inmunes a XSS).

const TOKEN_KEY = 'spm_token'

export function saveToken(token: string): void {
  sessionStorage.setItem(TOKEN_KEY, token)
}

export function getToken(): string | null {
  return sessionStorage.getItem(TOKEN_KEY)
}

export function clearToken(): void {
  sessionStorage.removeItem(TOKEN_KEY)
}

// Un JWT tiene 3 partes separadas por '.': header.payload.signature
// El payload está en Base64 y contiene { sub, exp, rol, iat }.
// Lo decodificamos sin ninguna librería para verificar si expiró.
export function isTokenExpired(token: string): boolean {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    // exp está en segundos Unix; Date.now() en milisegundos
    return payload.exp * 1000 < Date.now()
  } catch {
    return true // si no podemos parsear, lo tratamos como expirado
  }
}

// Devuelve el correo del usuario extraído del JWT (sin llamar al backend).
export function getEmailFromToken(token: string): string | null {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    return payload.sub ?? null
  } catch {
    return null
  }
}
