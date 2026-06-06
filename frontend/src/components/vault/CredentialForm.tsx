import { useState, type FormEvent, useEffect } from 'react'
import { X } from 'lucide-react'
import type { CredentialDetail, CredentialRequest } from '../../types'
import { Input } from '../ui/Input'
import { Button } from '../ui/Button'

interface Props {
  initial?: CredentialDetail   // si viene relleno, es edición; si no, es creación
  onSave: (req: CredentialRequest) => Promise<void>
  onCancel: () => void
}

export function CredentialForm({ initial, onSave, onCancel }: Props) {
  const isEdit = !!initial

  const [titulo,        setTitulo]        = useState(initial?.titulo        ?? '')
  const [usuarioCuenta, setUsuarioCuenta] = useState(initial?.usuarioCuenta ?? '')
  const [email,         setEmail]         = useState(initial?.email         ?? '')
  const [password,      setPassword]      = useState(initial?.password      ?? '')
  const [url,           setUrl]           = useState(initial?.url           ?? '')
  const [notas,         setNotas]         = useState(initial?.notas         ?? '')
  const [error,         setError]         = useState('')
  const [loading,       setLoading]       = useState(false)

  // Si cambia el inicial (el usuario abrió un registro diferente), resetear el form.
  useEffect(() => {
    setTitulo(initial?.titulo ?? '')
    setUsuarioCuenta(initial?.usuarioCuenta ?? '')
    setEmail(initial?.email ?? '')
    setPassword(initial?.password ?? '')
    setUrl(initial?.url ?? '')
    setNotas(initial?.notas ?? '')
  }, [initial?.id])

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError('')
    if (!titulo.trim() || !password.trim()) {
      setError('El título y la contraseña son obligatorios.')
      return
    }
    setLoading(true)
    try {
      await onSave({ titulo, usuarioCuenta, email, password, url, notas })
    } catch {
      setError('No se pudo guardar. Inténtalo de nuevo.')
    } finally {
      setLoading(false)
    }
  }

  return (
    // Overlay — cierra si se hace clic fuera del modal
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm"
      onClick={e => { if (e.target === e.currentTarget) onCancel() }}
    >
      <div className="w-full max-w-md bg-[var(--bg-surface)] rounded-2xl border border-[var(--border)] shadow-[var(--shadow-md)] flex flex-col max-h-[90vh]">
        {/* Header del modal */}
        <div className="flex items-center justify-between px-5 py-4 border-b border-[var(--border)]">
          <h2 className="text-sm font-medium text-[var(--text-primary)]">
            {isEdit ? 'Editar credencial' : 'Nueva credencial'}
          </h2>
          <button
            onClick={onCancel}
            className="p-1 rounded hover:bg-[var(--bg-hover)] text-[var(--text-muted)] transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Formulario */}
        <form onSubmit={handleSubmit} className="flex flex-col gap-3 p-5 overflow-y-auto">
          <Input label="Título *" value={titulo} onChange={e => setTitulo(e.target.value)}
            placeholder="Google, GitHub, BBVA…" required autoFocus />
          <Input label="Usuario" value={usuarioCuenta} onChange={e => setUsuarioCuenta(e.target.value)}
            placeholder="mi_usuario" />
          <Input label="Correo" type="email" value={email} onChange={e => setEmail(e.target.value)}
            placeholder="tu@correo.com" />
          <Input label="Contraseña *" type="password" value={password} onChange={e => setPassword(e.target.value)}
            placeholder="••••••••" required />
          <Input label="URL" type="url" value={url} onChange={e => setUrl(e.target.value)}
            placeholder="https://..." />
          <div className="flex flex-col gap-1">
            <label className="text-xs font-medium text-[var(--text-secondary)] uppercase tracking-wide">
              Notas
            </label>
            <textarea
              value={notas}
              onChange={e => setNotas(e.target.value)}
              placeholder="Notas adicionales..."
              rows={3}
              className="w-full px-3 py-2 rounded-lg text-sm bg-[var(--bg-hover)] border border-[var(--border)] text-[var(--text-primary)] placeholder:text-[var(--text-muted)] focus:outline-none focus:border-[var(--border-focus)] transition-colors resize-none"
            />
          </div>

          {error && <p className="text-sm text-[var(--danger)]">{error}</p>}

          <div className="flex gap-2 pt-2">
            <Button type="button" variant="secondary" onClick={onCancel} className="flex-1">
              Cancelar
            </Button>
            <Button type="submit" isLoading={loading} className="flex-1">
              {isEdit ? 'Guardar cambios' : 'Crear'}
            </Button>
          </div>
        </form>
      </div>
    </div>
  )
}
