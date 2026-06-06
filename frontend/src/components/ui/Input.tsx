import { forwardRef, type InputHTMLAttributes, type ReactNode } from 'react'

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string
  error?: string
  rightElement?: ReactNode
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, rightElement, className = '', ...props }, ref) => (
    <div className="flex flex-col gap-1">
      {label && (
        <label className="text-xs font-medium text-[var(--text-secondary)] uppercase tracking-wide">
          {label}
        </label>
      )}
      <div className="relative">
        <input
          ref={ref}
          className={`
            w-full px-3 py-2 rounded-lg text-sm
            bg-[var(--bg-hover)] border border-[var(--border)]
            text-[var(--text-primary)] placeholder:text-[var(--text-muted)]
            focus:outline-none focus:border-[var(--border-focus)]
            transition-colors duration-150
            ${rightElement ? 'pr-10' : ''}
            ${error ? 'border-[var(--danger)]' : ''}
            ${className}
          `}
          {...props}
        />
        {rightElement && (
          <div className="absolute right-2 top-1/2 -translate-y-1/2">
            {rightElement}
          </div>
        )}
      </div>
      {error && <p className="text-xs text-[var(--danger)]">{error}</p>}
    </div>
  )
)
Input.displayName = 'Input'
