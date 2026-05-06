import { type ButtonHTMLAttributes, type ReactNode } from 'react';

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'ghost' | 'destructive';
  isLoading?: boolean;
  children: ReactNode;
}

const variants = {
  primary:
    'bg-lloyds-green text-white hover:bg-lloyds-green-dark disabled:bg-gray-300 disabled:text-text-muted',
  secondary:
    'bg-transparent text-lloyds-green border-2 border-lloyds-green hover:bg-lloyds-green-light disabled:border-gray-300 disabled:text-gray-300',
  ghost:
    'bg-transparent text-lloyds-green underline underline-offset-[3px] hover:text-lloyds-green-dark',
  destructive:
    'bg-status-negative text-white hover:bg-red-800 disabled:bg-gray-300 disabled:text-text-muted',
} as const;

export function Button({
  variant = 'primary',
  isLoading = false,
  children,
  className = '',
  disabled,
  ...props
}: ButtonProps) {
  return (
    <button
      className={`inline-flex items-center justify-center gap-2 h-13 px-7 rounded-btn text-base font-bold font-bold min-w-[120px] transition-colors duration-150 cursor-pointer focus-visible:outline-2 focus-visible:outline-lloyds-green focus-visible:outline-offset-[3px] ${variants[variant]} ${className}`}
      disabled={disabled || isLoading}
      aria-busy={isLoading}
      {...props}
    >
      {isLoading && (
        <span
          className="w-5 h-5 border-2 border-white/35 border-t-white rounded-full animate-spin"
          aria-hidden="true"
        />
      )}
      {children}
    </button>
  );
}
