import { useState } from 'react'
import { Eye, EyeOff, Copy, Check, ExternalLink, Pencil, Trash2 } from 'lucide-react'
import type { CredentialDetail as CredentialDetailType } from '../../types'
import { getPasswordStrength } from '../../utils/passwordStrength'
import { PasswordStrengthDot } from '../ui/PasswordStrengthDot'
import { Button } from '../ui/Button'

interface Props {
  credential: CredentialDetailType
  onEdit: () => void
  onDelete: () => void
}

// Hook pequeño: gestiona el estado "Copiado" con reset automático.
function useCopy() {
  const [copied, setCopied] = useState<string | null>(null)

  async function copy(text: string, key: string) {
    await navigator.clipboard.writeText(text)
    setCopied(key)
    setTimeout(() => setCopied(null), 2000)
  }

  return { copied, copy }
}

function CopyButton({ text, id }: { text: string; id: string }) {
  const { copied, copy } = useCopy()
  return (
    <button
      onClick={() => copy(text, id)}
      title="Copiar"
      className="p-1 rounded hover:bg-[var(--bg-hover)] text-[var(--text-muted)] hover:text-[var(--text-primary)] transition-colors"
    >
      {copied === id
        ? <Check className="w-3.5 h-3.5 text-[var(--success)]" />
        : <Copy className="w-3.5 h-3.5" />
      }
    </button>
  )
}

export function CredentialDetail({ credential, onEdit, onDelete }: Props) {
  const [showPassword, setShowPassword] = useState(false)
  const { titulo, usuarioCuenta, email, password, url, notas } = credential
  const strength = getPasswordStrength(password)

  // Genera color del icono igual que CredentialItem
  function colorFromTitle(t: string) {
    const colors = ['#3b82f6','#8b5cf6','#ec4899','#f59e0b','#10b981','#ef4444','#06b6d4','#f97316']
    let h = 0; for (const c of t) h = (h * 31 + c.charCodeAt(0)) & 0xffffffff
    return colors[Math.abs(h) % colors.length]
  }

  return (
    <div className="flex-1 flex flex-col p-6 overflow-y-auto">
      {/* Encabezado */}
      <div className="flex flex-col items-center gap-2 mb-6">
        <div
          className="w-14 h-14 rounded-2xl flex items-center justify-center text-white text-xl font-medium"
          style={{ background: colorFromTitle(titulo) }}
        >
          {titulo.charAt(0).toUpperCase()}
        </div>
        <h2 className="text-base font-medium text-[var(--text-primary)]">{titulo}</h2>
        <PasswordStrengthDot strength={strength} showLabel />
      </div>

      {/* Campos */}
      <div className="flex flex-col gap-4 max-w-md w-full mx-auto">
        {usuarioCuenta && (
          <Field label="Usuario" value={usuarioCuenta}>
            <CopyButton text={usuarioCuenta} id="usuario" />
          </Field>
        )}

        {email && (
          <Field label="Correo" value={email}>
            <CopyButton text={email} id="email" />
          </Field>
        )}

        {/* Contraseña con reveal */}
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-[var(--text-secondary)] uppercase tracking-wide">
            Contraseña
          </label>
          <div className="flex items-center gap-2 px-3 py-2 rounded-lg bg-[var(--bg-hover)] border border-[var(--border)]">
            <span className="flex-1 text-sm font-mono text-[var(--text-primary)]">
              {showPassword ? password : '••••••••••••'}
            </span>
            <button
              onClick={() => setShowPassword(v => !v)}
              title={showPassword ? 'Ocultar' : 'Revelar'}
              className="p-1 rounded hover:bg-[var(--bg-surface)] text-[var(--text-muted)] hover:text-[var(--text-primary)] transition-colors"
            >
              {showPassword
                ? <EyeOff className="w-3.5 h-3.5" />
                : <Eye className="w-3.5 h-3.5" />
              }
            </button>
            <CopyButton text={password} id="password" />
          </div>
        </div>

        {url && (
          <div className="flex flex-col gap-1">
            <label className="text-xs font-medium text-[var(--text-secondary)] uppercase tracking-wide">
              Sitio web
            </label>
            <div className="flex items-center gap-2 px-3 py-2 rounded-lg bg-[var(--bg-hover)] border border-[var(--border)]">
              <a
                href={url.startsWith('http') ? url : `https://${url}`}
                target="_blank"
                rel="noopener noreferrer"
                className="flex-1 text-sm text-[var(--accent)] hover:underline truncate"
              >
                {url}
              </a>
              <ExternalLink className="w-3.5 h-3.5 text-[var(--text-muted)] shrink-0" />
            </div>
          </div>
        )}

        {notas && (
          <Field label="Notas" value={notas} multiline />
        )}
      </div>

      {/* Acciones */}
      <div className="flex items-center gap-2 mt-8 max-w-md w-full mx-auto">
        <Button variant="secondary" onClick={onEdit} className="flex-1">
          <Pencil className="w-3.5 h-3.5" />
          Editar
        </Button>
        <Button variant="danger" onClick={onDelete}>
          <Trash2 className="w-3.5 h-3.5" />
        </Button>
      </div>
    </div>
  )
}

function Field({ label, value, multiline, children }: {
  label: string; value: string; multiline?: boolean; children?: React.ReactNode
}) {
  return (
    <div className="flex flex-col gap-1">
      <label className="text-xs font-medium text-[var(--text-secondary)] uppercase tracking-wide">
        {label}
      </label>
      <div className="flex items-start gap-2 px-3 py-2 rounded-lg bg-[var(--bg-hover)] border border-[var(--border)]">
        <span className={`flex-1 text-sm text-[var(--text-primary)] ${multiline ? 'whitespace-pre-wrap' : 'truncate'}`}>
          {value}
        </span>
        {children}
      </div>
    </div>
  )
}
