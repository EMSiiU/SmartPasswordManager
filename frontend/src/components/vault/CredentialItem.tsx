import { Globe } from 'lucide-react'
import type { CredentialSummary } from '../../types'
import { getPasswordStrength } from '../../utils/passwordStrength'
import { PasswordStrengthDot } from '../ui/PasswordStrengthDot'

interface Props {
  credential: CredentialSummary
  isSelected: boolean
  cachedPassword?: string   // si ya cargamos el detalle, usamos esa contraseña para el dot
  onClick: () => void
}

// Genera un color de fondo a partir del título (para simular favicons).
function colorFromTitle(title: string): string {
  const colors = [
    '#3b82f6', '#8b5cf6', '#ec4899', '#f59e0b',
    '#10b981', '#ef4444', '#06b6d4', '#f97316',
  ]
  let hash = 0
  for (const c of title) hash = (hash * 31 + c.charCodeAt(0)) & 0xffffffff
  return colors[Math.abs(hash) % colors.length]
}

export function CredentialItem({ credential, isSelected, cachedPassword, onClick }: Props) {
  const { titulo, usuarioCuenta, email, url } = credential
  const subtitle = usuarioCuenta || email || url || '—'
  const bg = colorFromTitle(titulo)
  const initial = titulo.charAt(0).toUpperCase()

  // Solo mostramos el dot si ya tenemos la contraseña cargada.
  const strength = cachedPassword ? getPasswordStrength(cachedPassword) : null

  return (
    <button
      onClick={onClick}
      className={`
        w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-left
        transition-colors duration-100 group
        ${isSelected
          ? 'bg-[var(--bg-selected)]'
          : 'hover:bg-[var(--bg-hover)]'
        }
      `}
    >
      {/* Icono / inicial */}
      <div
        className="w-8 h-8 rounded-lg flex items-center justify-center shrink-0 text-white text-sm font-medium"
        style={{ background: bg }}
      >
        {initial}
      </div>

      {/* Texto */}
      <div className="flex-1 min-w-0">
        <p className={`text-sm font-medium truncate ${isSelected ? 'text-[var(--accent)]' : 'text-[var(--text-primary)]'}`}>
          {titulo}
        </p>
        <p className="text-xs text-[var(--text-secondary)] truncate">{subtitle}</p>
      </div>

      {/* Dot de fuerza (solo si la contraseña está cargada) */}
      {strength ? (
        <PasswordStrengthDot strength={strength} />
      ) : (
        <Globe className="w-3 h-3 text-[var(--text-muted)] opacity-0 group-hover:opacity-100 transition-opacity" />
      )}
    </button>
  )
}
