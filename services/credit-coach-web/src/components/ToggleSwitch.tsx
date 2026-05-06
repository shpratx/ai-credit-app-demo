interface ToggleSwitchProps {
  label: string;
  description?: string;
  checked: boolean;
  onChange: (checked: boolean) => void;
}

export function ToggleSwitch({ label, description, checked, onChange }: ToggleSwitchProps) {
  return (
    <div className="flex items-center justify-between py-3.5 border-b border-border last:border-none">
      <div className="flex-1 mr-4">
        <span className="text-[15px]">{label}</span>
        {description && <p className="text-caption text-text-secondary mt-0.5">{description}</p>}
      </div>
      <button
        role="switch"
        aria-checked={checked}
        aria-label={label}
        onClick={() => onChange(!checked)}
        className={`relative w-[52px] h-7 rounded-full shrink-0 transition-colors cursor-pointer ${
          checked ? 'bg-lloyds-green' : 'bg-gray-300'
        }`}
      >
        <span
          className={`absolute top-[3px] left-[3px] w-[22px] h-[22px] bg-white rounded-full shadow transition-transform ${
            checked ? 'translate-x-6' : ''
          }`}
        />
      </button>
    </div>
  );
}
