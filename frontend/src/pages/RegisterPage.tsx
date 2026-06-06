import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ShieldCheck } from 'lucide-react'
import { register } from '../api/auth'
import { useAuth } from '../context/AuthContext'
import { Button } from '../components/ui/Button'
import { Input } from '../components/ui/Input'

export function RegisterPage() {
  const { login: saveSession } = useAuth()
  const navigate = useNavigate()

  const [nombre, setNombre]     = useState('')
  const [correo, setCorreo]     = useState('')
  const [password, setPassword] = useState('')
  const [error, setError]       = useState('')
  const [loading, setLoading]   = useState(false)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError('')
    if (password.length < 8) {
      setError('La contraseña debe tener al menos 8 caracteres.')
      return
    }
    setLoading(true)
    try {
      const { accessToken } = await register(nombre, correo, password)
      saveSession(accessToken)
      navigate('/vault', { replace: true })
    } catch {
      setError('No se pudo crear la cuenta. El correo ya puede estar en uso.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-[var(--bg-app)]">
      <div className="w-full max-w-sm p-8 rounded-2xl bg-[var(--bg-surface)] border border-[var(--border)] shadow-[var(--shadow-md)]">
        <div className="flex flex-col items-center gap-2 mb-8">
          <div className="w-12 h-12 rounded-xl bg-[var(--accent-light)] flex items-center justify-center">
            <ShieldCheck className="w-6 h-6 text-[var(--accent)]" />
          </div>
          <h1 className="text-xl font-medium text-[var(--text-primary)]">SmartPass</h1>
          <p className="text-sm text-[var(--text-secondary)]">Crea tu cuenta</p>
        </div>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <Input
            label="Nombre"
            type="text"
            placeholder="Luis M."
            value={nombre}
            onChange={e => setNombre(e.target.value)}
            required
            autoFocus
          />
          <Input
            label="Correo electrónico"
            type="email"
            placeholder="tu@correo.com"
            value={correo}
            onChange={e => setCorreo(e.target.value)}
            required
          />
          <Input
            label="Contraseña"
            type="password"
            placeholder="Mínimo 8 caracteres"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
          />

          {error && (
            <p className="text-sm text-[var(--danger)] text-center">{error}</p>
          )}

          <Button type="submit" isLoading={loading} className="w-full mt-2">
            Crear cuenta
          </Button>
        </form>

        <p className="mt-6 text-center text-sm text-[var(--text-secondary)]">
          ¿Ya tienes cuenta?{' '}
          <Link to="/login" className="text-[var(--accent)] hover:underline">
            Inicia sesión
          </Link>
        </p>
      </div>
    </div>
  )
}
