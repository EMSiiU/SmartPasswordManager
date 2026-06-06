import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import type { ReactNode } from 'react'

// Componente "portero": envuelve las páginas que requieren autenticación.
// Si no hay token → redirige a /login (replace evita que /vault quede en el historial).
// Si hay token → renderiza los hijos normalmente.
export function ProtectedRoute({ children }: { children: ReactNode }) {
  const { token } = useAuth()
  return token ? <>{children}</> : <Navigate to="/login" replace />
}
