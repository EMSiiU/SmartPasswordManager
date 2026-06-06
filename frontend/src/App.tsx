import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import { ProtectedRoute } from './router/ProtectedRoute'
import { AppLayout } from './components/layout/AppLayout'
import { LoginPage } from './pages/LoginPage'
import { RegisterPage } from './pages/RegisterPage'
import { VaultPage } from './pages/VaultPage'
import { GeneratorPage } from './pages/GeneratorPage'

export default function App() {
  return (
    <BrowserRouter>
      {/* AuthProvider envuelve todo para que cualquier componente pueda usar useAuth() */}
      <AuthProvider>
        <Routes>
          {/* Rutas públicas */}
          <Route path="/login"    element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          {/* Rutas protegidas: AppLayout renderiza la sidebar + header + <Outlet /> */}
          <Route
            element={
              <ProtectedRoute>
                <AppLayout />
              </ProtectedRoute>
            }
          >
            <Route path="/vault"     element={<VaultPage />} />
            <Route path="/generator" element={<GeneratorPage />} />
          </Route>

          {/* Cualquier otra ruta → login */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}
