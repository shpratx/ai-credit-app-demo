interface ScenarioChipProps {
  label: string;
  icon: string;
  selected: boolean;
  onSelect: () => void;
}

export function ScenarioChip({ label, icon, selected, onSelect }: ScenarioChipProps) {
  return (
    <button
      onClick={onSelect}
      className={`flex items-center gap-2 px-4 py-2.5 rounded-lg border-[1.5px] text-sm text-left transition-colors cursor-pointer ${
        selected
          ? 'border-lloyds-green bg-lloyds-green-light font-bold'
          : 'border-border bg-surface hover:border-lloyds-green hover:bg-lloyds-green-light'
      }`}
      role="radio"
      aria-checked={selected}
      aria-label={`Scenario: ${label}`}
    >
      <span aria-hidden="true">{icon}</span>
      <span>{label}</span>
    </button>
  );
}
