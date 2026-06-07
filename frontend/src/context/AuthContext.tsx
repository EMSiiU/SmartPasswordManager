import { createContext, useContext, useState, useEffect, type ReactNode } from 'react'
import { getToken, saveToken, clearToken, isTokenExpired, getEmailFromToken } from '../utils/token'
import { callLogout } from '../api/auth'

interface AuthContextType {
  token: string | null
  userEmail: string | null
  login: (token: string) => void
  logout: () => void
}

const AuthContext = createContext<AuthContextType | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => {
    const stored = getToken()
    if (stored && !isTokenExpired(stored)) return stored
    if (stored) clearToken()
    return null
  })

  const [userEmail, setUserEmail] = useState<string | null>(() =>
    token ? getEmailFromToken(token) : null
  )

  useEffect(() => {
    setUserEmail(token ? getEmailFromToken(token) : null)
  }, [token])

  function login(newToken: string) {
    saveToken(newToken)
    setToken(newToken)
  }

  function logout() {
    // Fire-and-forget: limpiamos el estado local inmediatamente para
    // que la UI responda sin esperar. El backend revoca el refresh token
    // en segundo plano (la cookie se borra en la respuesta del servidor).
    callLogout().catch(() => {
      // Si el servidor no está disponible, la cookie expirará sola en 7 días.
      // El token local ya quedó limpio, así que el usuario no puede operar.
    })
    clearToken()
    setToken(null)
  }

  return (
    <AuthContext.Provider value={{ token, userEmail, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth(): AuthContextType {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth debe usarse dentro de <AuthProvider>')
  return ctx
}
