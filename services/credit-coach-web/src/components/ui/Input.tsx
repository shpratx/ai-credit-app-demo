import { type InputHTMLAttributes, forwardRef } from 'react';

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string;
  error?: string;
  helper?: string;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, helper, id, className = '', ...props }, ref) => {
    const inputId = id ?? label.toLowerCase().replace(/\s+/g, '-');
    return (
      <div className="flex flex-col gap-1.5 mb-5">
        <label htmlFor={inputId} className="text-body-sm font-bold text-text-primary">
          {label}
        </label>
        <input
          ref={ref}
          id={inputId}
          className={`w-full h-13 px-4 border-[1.5px] rounded-btn text-base text-text-primary bg-surface transition-colors focus:outline-none focus:border-lloyds-green focus:ring-[3px] focus:ring-lloyds-green/15 ${error ? 'border-status-negative ring-status-negative/10' : 'border-gray-300'} ${className}`}
          aria-invalid={!!error}
          aria-describedby={error ? `${inputId}-error` : helper ? `${inputId}-helper` : undefined}
          {...props}
        />
        {error && (
          <p id={`${inputId}-error`} className="text-caption text-status-negative">
            {error}
          </p>
        )}
        {helper && !error && (
          <p id={`${inputId}-helper`} className="text-caption text-text-secondary">
            {helper}
          </p>
        )}
      </div>
    );
  },
);

Input.displayName = 'Input';
