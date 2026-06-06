import { Outlet } from 'react-router-dom'
import { Sidebar } from './Sidebar'
import { Moon, Sun } from 'lucide-react'
import { useEffect, useState } from 'react'

// Detecta la preferencia del sistema si no hay ninguna guardada.
function getInitialTheme(): 'light' | 'dark' {
  const stored = localStorage.getItem('spm_theme')
  if (stored === 'light' || stored === 'dark') return stored
  return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
}

export function AppLayout() {
  const [theme, setTheme] = useState<'light' | 'dark'>(getInitialTheme)

  // Aplica el data-theme al <html> cada vez que cambia.
  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme)
    localStorage.setItem('spm_theme', theme)
  }, [theme])

  // También aplicarlo en el primer render (antes del primer estado).
  useEffect(() => {
    document.documentElement.setAttribute('data-theme', getInitialTheme())
  }, [])

  function toggleTheme() {
    setTheme(t => t === 'light' ? 'dark' : 'light')
  }

  return (
    <div className="flex h-screen overflow-hidden bg-[var(--bg-app)]">
      <Sidebar />

      <div className="flex-1 flex flex-col min-w-0">
        {/* Barra superior con toggle de tema */}
        <header className="h-10 shrink-0 flex items-center justify-end px-4 border-b border-[var(--border)] bg-[var(--bg-surface)]">
          <button
            onClick={toggleTheme}
            title={theme === 'dark' ? 'Modo claro' : 'Modo oscuro'}
            className="p-1.5 rounded-lg hover:bg-[var(--bg-hover)] text-[var(--text-secondary)] transition-colors"
          >
            {theme === 'dark' ? <Sun className="w-4 h-4" /> : <Moon className="w-4 h-4" />}
          </button>
        </header>

        {/* Aquí React Router inyecta la página activa */}
        <main className="flex-1 overflow-hidden">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
