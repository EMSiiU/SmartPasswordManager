import { useState } from 'react'
import { Search, Plus } from 'lucide-react'
import type { CredentialSummary } from '../../types'
import { CredentialItem } from './CredentialItem'

interface Props {
  credentials: CredentialSummary[]
  selectedId: number | null
  passwordCache: Record<number, string>   // contraseñas ya cargadas, para el dot
  onSelect: (id: number) => void
  onAdd: () => void
}

export function CredentialList({ credentials, selectedId, passwordCache, onSelect, onAdd }: Props) {
  const [query, setQuery] = useState('')

  const filtered = credentials.filter(c => {
    const q = query.toLowerCase()
    return (
      c.titulo.toLowerCase().includes(q) ||
      (c.email?.toLowerCase().includes(q) ?? false) ||
      (c.usuarioCuenta?.toLowerCase().includes(q) ?? false) ||
      (c.url?.toLowerCase().includes(q) ?? false)
    )
  })

  return (
    <div className="flex flex-col w-72 shrink-0 border-r border-[var(--border)] bg-[var(--bg-surface)] h-full">
      {/* Encabezado */}
      <div className="flex items-center justify-between px-4 py-3 border-b border-[var(--border)]">
        <h2 className="text-sm font-medium text-[var(--text-primary)]">Bóveda</h2>
        <button
          onClick={onAdd}
          className="flex items-center gap-1 px-2.5 py-1.5 rounded-lg bg-[var(--accent)] hover:bg-[var(--accent-hover)] text-white text-xs font-medium transition-colors"
        >
          <Plus className="w-3.5 h-3.5" />
          Añadir
        </button>
      </div>

      {/* Búsqueda */}
      <div className="px-3 py-2 border-b border-[var(--border)]">
        <div className="relative">
          <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-[var(--text-muted)]" />
          <input
            type="text"
            placeholder="Buscar credenciales..."
            value={query}
            onChange={e => setQuery(e.target.value)}
            className="w-full pl-8 pr-3 py-1.5 text-xs rounded-lg bg-[var(--bg-hover)] border border-[var(--border)] text-[var(--text-primary)] placeholder:text-[var(--text-muted)] focus:outline-none focus:border-[var(--border-focus)] transition-colors"
          />
        </div>
      </div>

      {/* Lista */}
      <div className="flex-1 overflow-y-auto p-2 flex flex-col gap-0.5">
        {filtered.length === 0 ? (
          <div className="flex flex-col items-center justify-center h-full text-center gap-2 py-8">
            <p className="text-sm text-[var(--text-muted)]">
              {query ? 'Sin resultados' : 'Tu bóveda está vacía'}
            </p>
            {!query && (
              <p className="text-xs text-[var(--text-muted)]">Pulsa "Añadir" para guardar tu primera credencial</p>
            )}
          </div>
        ) : (
          filtered.map(c => (
            <CredentialItem
              key={c.id}
              credential={c}
              isSelected={c.id === selectedId}
              cachedPassword={passwordCache[c.id]}
              onClick={() => onSelect(c.id)}
            />
          ))
        )}
      </div>
    </div>
  )
}
