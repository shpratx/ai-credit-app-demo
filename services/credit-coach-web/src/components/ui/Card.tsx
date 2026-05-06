import { type ReactNode } from 'react';

interface CardProps {
  title?: string;
  subtitle?: string;
  children: ReactNode;
  className?: string;
}

export function Card({ title, subtitle, children, className = '' }: CardProps) {
  return (
    <div
      className={`bg-surface border border-border rounded-card p-6 shadow-card ${className}`}
      aria-label={title}
    >
      {title && <h3 className="text-card-title font-bold text-text-primary">{title}</h3>}
      {subtitle && <p className="text-caption text-text-secondary mt-0.5">{subtitle}</p>}
      {(title || subtitle) && <div className="mt-4">{children}</div>}
      {!title && !subtitle && children}
    </div>
  );
}
