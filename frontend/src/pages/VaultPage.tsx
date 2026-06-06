import { useState, useEffect, useCallback } from 'react'
import { ShieldCheck } from 'lucide-react'
import type { CredentialSummary, CredentialDetail, CredentialRequest } from '../types'
import { listCredentials, getCredentialDetail, createCredential, updateCredential, deleteCredential } from '../api/vault'
import { CredentialList } from '../components/vault/CredentialList'
import { CredentialDetail as DetailPanel } from '../components/vault/CredentialDetail'
import { CredentialForm } from '../components/vault/CredentialForm'

type ModalMode = 'create' | 'edit' | null

export function VaultPage() {
  const [credentials,    setCredentials]    = useState<CredentialSummary[]>([])
  const [selectedId,     setSelectedId]     = useState<number | null>(null)
  const [detail,         setDetail]         = useState<CredentialDetail | null>(null)
  const [passwordCache,  setPasswordCache]  = useState<Record<number, string>>({})
  const [modal,          setModal]          = useState<ModalMode>(null)
  const [loading,        setLoading]        = useState(true)
  const [detailLoading,  setDetailLoading]  = useState(false)

  // Carga la lista al montar la página
  const loadList = useCallback(async () => {
    try {
      const list = await listCredentials()
      setCredentials(list)
    } catch {
      // El interceptor de Axios redirige si hay 401; otros errores los ignoramos aquí.
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { loadList() }, [loadList])

  // Al seleccionar una credencial, carga el detalle (con la contraseña descifrada)
  async function handleSelect(id: number) {
    if (id === selectedId) return
    setSelectedId(id)
    setDetail(null)
    setDetailLoading(true)
    try {
      const d = await getCredentialDetail(id)
      setDetail(d)
      // Guardamos la contraseña en caché para mostrar el dot de fuerza en la lista.
      setPasswordCache(prev => ({ ...prev, [id]: d.password }))
    } finally {
      setDetailLoading(false)
    }
  }

  async function handleSave(req: CredentialRequest) {
    if (modal === 'create') {
      const created = await createCredential(req)
      setCredentials(prev => [...prev, created])
      setPasswordCache(prev => ({ ...prev, [created.id]: created.password }))
      setSelectedId(created.id)
      setDetail(created)
    } else if (modal === 'edit' && detail) {
      const updated = await updateCredential(detail.id, req)
      setCredentials(prev => prev.map(c => c.id === updated.id ? updated : c))
      setPasswordCache(prev => ({ ...prev, [updated.id]: updated.password }))
      setDetail(updated)
    }
    setModal(null)
  }

  async function handleDelete() {
    if (!detail) return
    if (!confirm(`¿Borrar "${detail.titulo}"? Esta acción no se puede deshacer.`)) return
    await deleteCredential(detail.id)
    setCredentials(prev => prev.filter(c => c.id !== detail.id))
    setPasswordCache(prev => { const p = { ...prev }; delete p[detail.id]; return p })
    setSelectedId(null)
    setDetail(null)
  }

  return (
    <div className="flex h-full">
      {/* Columna central: lista */}
      {loading ? (
        <div className="flex-1 flex items-center justify-center text-[var(--text-muted)] text-sm">
          Cargando bóveda…
        </div>
      ) : (
        <CredentialList
          credentials={credentials}
          selectedId={selectedId}
          passwordCache={passwordCache}
          onSelect={handleSelect}
          onAdd={() => setModal('create')}
        />
      )}

      {/* Columna derecha: detalle */}
      <div className="flex-1 flex items-center justify-center bg-[var(--bg-app)]">
        {detailLoading ? (
          <p className="text-sm text-[var(--text-muted)]">Cargando…</p>
        ) : detail ? (
          <DetailPanel
            credential={detail}
            onEdit={() => setModal('edit')}
            onDelete={handleDelete}
          />
        ) : (
          <div className="flex flex-col items-center gap-3 text-center p-8">
            <ShieldCheck className="w-10 h-10 text-[var(--border)]" />
            <p className="text-sm text-[var(--text-muted)]">
              Selecciona una credencial para ver su detalle
            </p>
          </div>
        )}
      </div>

      {/* Modal crear/editar */}
      {modal && (
        <CredentialForm
          initial={modal === 'edit' ? detail ?? undefined : undefined}
          onSave={handleSave}
          onCancel={() => setModal(null)}
        />
      )}
    </div>
  )
}
