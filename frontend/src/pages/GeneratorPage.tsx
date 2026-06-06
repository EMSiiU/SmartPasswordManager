import { useState, useCallback } from 'react'
import { Copy, Check, RefreshCw } from 'lucide-react'
import { generatePassword, getPasswordStrength } from '../utils/passwordStrength'
import { PasswordStrengthDot } from '../components/ui/PasswordStrengthDot'

export function GeneratorPage() {
  const [length,    setLength]    = useState(16)
  const [uppercase, setUppercase] = useState(true)
  const [numbers,   setNumbers]   = useState(true)
  const [symbols,   setSymbols]   = useState(true)
  const [copied,    setCopied]    = useState(false)

  const [password, setPassword] = useState(() =>
    generatePassword({ length: 16, uppercase: true, numbers: true, symbols: true })
  )

  const generate = useCallback(() => {
    setPassword(generatePassword({ length, uppercase, numbers, symbols }))
    setCopied(false)
  }, [length, uppercase, numbers, symbols])

  async function handleCopy() {
    await navigator.clipboard.writeText(password)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  const strength = getPasswordStrength(password)

  return (
    <div className="flex flex-col items-center justify-center h-full p-8 bg-[var(--bg-app)]">
      <div className="w-full max-w-md flex flex-col gap-6">
        <div className="text-center">
          <h1 className="text-lg font-medium text-[var(--text-primary)] mb-1">Generador de contraseñas</h1>
          <p className="text-sm text-[var(--text-secondary)]">
            Usa <code className="bg-[var(--bg-hover)] px-1 rounded text-xs">crypto.getRandomValues</code> — seguro por diseño
          </p>
        </div>

        {/* Contraseña generada */}
        <div className="bg-[var(--bg-surface)] border border-[var(--border)] rounded-xl p-4 flex flex-col gap-3">
          <div className="flex items-center gap-2">
            <span className="flex-1 font-mono text-base text-[var(--text-primary)] break-all select-all">
              {password}
            </span>
          </div>
          <div className="flex items-center justify-between">
            <PasswordStrengthDot strength={strength} showLabel />
            <div className="flex gap-2">
              <button
                onClick={generate}
                title="Regenerar"
                className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-medium bg-[var(--bg-hover)] hover:bg-[var(--border)] text-[var(--text-secondary)] transition-colors"
              >
                <RefreshCw className="w-3.5 h-3.5" />
                Regenerar
              </button>
              <button
                onClick={handleCopy}
                className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-medium bg-[var(--accent)] hover:bg-[var(--accent-hover)] text-white transition-colors"
              >
                {copied
                  ? <><Check className="w-3.5 h-3.5" /> ¡Copiado!</>
                  : <><Copy className="w-3.5 h-3.5" /> Copiar</>
                }
              </button>
            </div>
          </div>
        </div>

        {/* Controles */}
        <div className="bg-[var(--bg-surface)] border border-[var(--border)] rounded-xl p-4 flex flex-col gap-4">
          {/* Longitud */}
          <div className="flex flex-col gap-2">
            <div className="flex items-center justify-between">
              <label className="text-xs font-medium text-[var(--text-secondary)] uppercase tracking-wide">
                Longitud
              </label>
              <span className="text-sm font-medium text-[var(--text-primary)]">{length}</span>
            </div>
            <input
              type="range"
              min={8} max={64}
              value={length}
              onChange={e => { setLength(Number(e.target.value)); generate() }}
              className="w-full accent-[var(--accent)]"
            />
            <div className="flex justify-between text-xs text-[var(--text-muted)]">
              <span>8</span><span>64</span>
            </div>
          </div>

          {/* Toggles */}
          <div className="flex flex-col gap-2.5">
            {[
              { label: 'Mayúsculas (A–Z)',  value: uppercase, set: setUppercase },
              { label: 'Números (0–9)',     value: numbers,   set: setNumbers   },
              { label: 'Símbolos (!@#…)',   value: symbols,   set: setSymbols   },
            ].map(({ label, value, set }) => (
              <label key={label} className="flex items-center justify-between cursor-pointer">
                <span className="text-sm text-[var(--text-primary)]">{label}</span>
                <button
                  role="switch"
                  aria-checked={value}
                  onClick={() => { set(v => !v); generate() }}
                  className={`relative w-9 h-5 rounded-full transition-colors ${value ? 'bg-[var(--accent)]' : 'bg-[var(--border)]'}`}
                >
                  <span
                    className={`absolute top-0.5 left-0.5 w-4 h-4 bg-white rounded-full shadow transition-transform ${value ? 'translate-x-4' : 'translate-x-0'}`}
                  />
                </button>
              </label>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
