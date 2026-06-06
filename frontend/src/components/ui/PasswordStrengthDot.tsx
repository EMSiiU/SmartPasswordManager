import type { PasswordStrength } from '../../types'

const colors: Record<PasswordStrength, string> = {
  weak:   'bg-[var(--danger)]',
  medium: 'bg-[var(--warning)]',
  strong: 'bg-[var(--success)]',
}

const labels: Record<PasswordStrength, string> = {
  weak:   'Contraseña débil',
  medium: 'Contraseña media',
  strong: 'Contraseña fuerte',
}

interface Props {
  strength: PasswordStrength
  showLabel?: boolean
}

export function PasswordStrengthDot({ strength, showLabel }: Props) {
  return (
    <span className="inline-flex items-center gap-1.5" title={labels[strength]}>
      <span className={`w-2 h-2 rounded-full ${colors[strength]}`} />
      {showLabel && (
        <span className="text-xs text-[var(--text-secondary)]">{labels[strength]}</span>
      )}
    </span>
  )
}
