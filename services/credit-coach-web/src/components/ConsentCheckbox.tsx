interface ConsentCheckboxProps {
  id: string;
  label: string;
  checked: boolean;
  onChange: (checked: boolean) => void;
}

export function ConsentCheckbox({ id, label, checked, onChange }: ConsentCheckboxProps) {
  return (
    <div className="flex items-start gap-3 py-3 border-b border-border last:border-none">
      <input
        type="checkbox"
        id={id}
        checked={checked}
        onChange={(e) => onChange(e.target.checked)}
        className="w-5 h-5 mt-0.5 flex-shrink-0 accent-lloyds-green min-w-[48px] min-h-[48px] p-3 -m-3"
        aria-label={label}
      />
      <label htmlFor={id} className="text-body-sm leading-relaxed cursor-pointer">
        {label}
      </label>
    </div>
  );
}
