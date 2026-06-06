import { createContext, useContext, useState, useEffect, type ReactNode } from 'react'
import { getToken, saveToken, clearToken, isTokenExpired, getEmailFromToken } from '../utils/token'

// La "forma" del contexto: qué datos y funciones exponemos al resto de la app.
interface AuthContextType {
  token: string | null
  userEmail: string | null
  login: (token: string) => void
  logout: () => void
}

const AuthContext = createContext<AuthContextType | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  // Al inicializar, intentamos restaurar el token de sessionStorage.
  // Si existe pero ya expiró, lo descartamos (evitamos peticiones que van a fallar).
  const [token, setToken] = useState<string | null>(() => {
    const stored = getToken()
    if (stored && !isTokenExpired(stored)) return stored
    if (stored) clearToken() // limpiar el expirado
    return null
  })

  const [userEmail, setUserEmail] = useState<string | null>(() =>
    token ? getEmailFromToken(token) : null
  )

  // Sincronizar el email cuando cambia el token
  useEffect(() => {
    setUserEmail(token ? getEmailFromToken(token) : null)
  }, [token])

  function login(newToken: string) {
    saveToken(newToken)
    setToken(newToken)
  }

  function logout() {
    clearToken()
    setToken(null)
  }

  return (
    <AuthContext.Provider value={{ token, userEmail, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

// Hook personalizado: en vez de importar AuthContext y useContext en cada componente,
// importas solo este hook. Si lo usas fuera del AuthProvider, lanza un error claro.
export function useAuth(): AuthContextType {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth debe usarse dentro de <AuthProvider>')
  return ctx
}
