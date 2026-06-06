import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ShieldCheck } from 'lucide-react'
import { login } from '../api/auth'
import { useAuth } from '../context/AuthContext'
import { Button } from '../components/ui/Button'
import { Input } from '../components/ui/Input'

export function LoginPage() {
  const { login: saveSession } = useAuth()
  const navigate = useNavigate()

  const [correo, setCorreo]     = useState('')
  const [password, setPassword] = useState('')
  const [error, setError]       = useState('')
  const [loading, setLoading]   = useState(false)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const { accessToken } = await login(correo, password)
      saveSession(accessToken)
      navigate('/vault', { replace: true })
    } catch {
      setError('Correo o contraseña incorrectos.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-[var(--bg-app)]">
      <div className="w-full max-w-sm p-8 rounded-2xl bg-[var(--bg-surface)] border border-[var(--border)] shadow-[var(--shadow-md)]">
        {/* Logo */}
        <div className="flex flex-col items-center gap-2 mb-8">
          <div className="w-12 h-12 rounded-xl bg-[var(--accent-light)] flex items-center justify-center">
            <ShieldCheck className="w-6 h-6 text-[var(--accent)]" />
          </div>
          <h1 className="text-xl font-medium text-[var(--text-primary)]">SmartPass</h1>
          <p className="text-sm text-[var(--text-secondary)]">Inicia sesión en tu bóveda</p>
        </div>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <Input
            label="Correo electrónico"
            type="email"
            placeholder="tu@correo.com"
            value={correo}
            onChange={e => setCorreo(e.target.value)}
            required
            autoFocus
          />
          <Input
            label="Contraseña"
            type="password"
            placeholder="••••••••"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
          />

          {error && (
            <p className="text-sm text-[var(--danger)] text-center">{error}</p>
          )}

          <Button type="submit" isLoading={loading} className="w-full mt-2">
            Entrar
          </Button>
        </form>

        <p className="mt-6 text-center text-sm text-[var(--text-secondary)]">
          ¿No tienes cuenta?{' '}
          <Link to="/register" className="text-[var(--accent)] hover:underline">
            Regístrate
          </Link>
        </p>
      </div>
    </div>
  )
}
