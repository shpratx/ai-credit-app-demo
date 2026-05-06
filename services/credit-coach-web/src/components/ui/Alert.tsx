import { type ReactNode } from 'react';

interface AlertProps {
  variant: 'info' | 'success' | 'warning' | 'error';
  title?: string;
  children: ReactNode;
  className?: string;
}

const variants = {
  info: 'bg-status-info-bg border-blue-300 text-blue-900',
  success: 'bg-status-positive-bg border-green-300 text-green-900',
  warning: 'bg-status-pending-bg border-orange-300 text-amber-900',
  error: 'bg-status-negative-bg border-red-200 text-red-900',
} as const;

const icons = { info: 'ℹ️', success: '✓', warning: '⚠️', error: '✕' } as const;

export function Alert({ variant, title, children, className = '' }: AlertProps) {
  return (
    <div
      className={`flex items-start gap-3 p-4 rounded-[10px] text-body-sm border ${variants[variant]} ${className}`}
      role="alert"
      aria-live="polite"
    >
      <span aria-hidden="true" className="flex-shrink-0 mt-0.5">
        {icons[variant]}
      </span>
      <div>
        {title && <p className="font-bold mb-1">{title}</p>}
        {children}
      </div>
    </div>
  );
}
