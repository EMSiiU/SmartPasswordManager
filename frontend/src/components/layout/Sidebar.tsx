import { NavLink } from 'react-router-dom'
import { ShieldCheck, Key, Zap, LogOut } from 'lucide-react'
import { useAuth } from '../../context/AuthContext'

const navItems = [
  { to: '/vault',     icon: Key,       label: 'Bóveda'    },
  { to: '/generator', icon: Zap,       label: 'Generador' },
]

export function Sidebar() {
  const { userEmail, logout } = useAuth()

  // Iniciales del correo para el avatar (ej: "lu" de "luis@...")
  const initials = userEmail
    ? userEmail.slice(0, 2).toUpperCase()
    : '?'

  return (
    <aside
      className="flex flex-col w-44 shrink-0 border-r border-[var(--border)] bg-[var(--bg-sidebar)]"
      style={{ height: '100vh', position: 'sticky', top: 0 }}
    >
      {/* Logo */}
      <div className="flex items-center gap-2 px-4 py-5 border-b border-[var(--border)]">
        <div className="w-7 h-7 rounded-lg bg-[var(--accent-light)] flex items-center justify-center shrink-0">
          <ShieldCheck className="w-4 h-4 text-[var(--accent)]" />
        </div>
        <span className="font-medium text-sm text-[var(--text-primary)]">SmartPass</span>
      </div>

      {/* Navegación */}
      <nav className="flex-1 p-2 flex flex-col gap-0.5">
        {navItems.map(({ to, icon: Icon, label }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) =>
              `flex items-center gap-2.5 px-3 py-2 rounded-lg text-sm transition-colors duration-100 ${
                isActive
                  ? 'bg-[var(--bg-selected)] text-[var(--accent)] font-medium'
                  : 'text-[var(--text-secondary)] hover:bg-[var(--bg-hover)] hover:text-[var(--text-primary)]'
              }`
            }
          >
            <Icon className="w-4 h-4 shrink-0" />
            {label}
          </NavLink>
        ))}
      </nav>

      {/* Avatar + logout */}
      <div className="p-3 border-t border-[var(--border)]">
        <div className="flex items-center gap-2">
          <div className="w-7 h-7 rounded-full bg-[var(--accent)] flex items-center justify-center shrink-0">
            <span className="text-white text-xs font-medium">{initials}</span>
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-xs text-[var(--text-primary)] font-medium truncate">
              {userEmail ?? 'Usuario'}
            </p>
            <p className="text-[10px] text-[var(--success)]">Cuenta segura</p>
          </div>
          <button
            onClick={logout}
            title="Cerrar sesión"
            className="p-1 rounded hover:bg-[var(--bg-hover)] text-[var(--text-muted)] hover:text-[var(--danger)] transition-colors"
          >
            <LogOut className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>
    </aside>
  )
}
