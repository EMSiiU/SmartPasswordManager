import type { ButtonHTMLAttributes, ReactNode } from 'react'

type Variant = 'primary' | 'secondary' | 'danger' | 'ghost'

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant
  isLoading?: boolean
  children: ReactNode
}

const styles: Record<Variant, string> = {
  primary:   'bg-[var(--accent)] hover:bg-[var(--accent-hover)] text-white',
  secondary: 'bg-[var(--bg-hover)] hover:bg-[var(--border)] text-[var(--text-primary)]',
  danger:    'bg-[var(--danger)] hover:opacity-90 text-white',
  ghost:     'hover:bg-[var(--bg-hover)] text-[var(--text-secondary)]',
}

export function Button({ variant = 'primary', isLoading, children, className = '', disabled, ...props }: ButtonProps) {
  return (
    <button
      disabled={disabled || isLoading}
      className={`
        inline-flex items-center justify-center gap-2
        px-4 py-2 rounded-lg text-sm font-medium
        transition-colors duration-150
        disabled:opacity-50 disabled:cursor-not-allowed
        ${styles[variant]} ${className}
      `}
      {...props}
    >
      {isLoading ? (
        <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
      ) : children}
    </button>
  )
}
