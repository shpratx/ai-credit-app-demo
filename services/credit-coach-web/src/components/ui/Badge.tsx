import { type ReactNode } from 'react';

interface BadgeProps {
  variant: 'positive' | 'negative' | 'info' | 'pending';
  children: ReactNode;
}

const variants = {
  positive: 'bg-status-positive-bg text-status-positive border-green-300',
  negative: 'bg-status-negative-bg text-status-negative border-red-200',
  info: 'bg-status-info-bg text-status-info border-blue-200',
  pending: 'bg-status-pending-bg text-status-pending border-orange-200',
} as const;

export function Badge({ variant, children }: BadgeProps) {
  return (
    <span
      className={`inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-micro font-bold tracking-wide border ${variants[variant]}`}
      aria-label={`${variant}: ${typeof children === 'string' ? children : ''}`}
    >
      {children}
    </span>
  );
}
