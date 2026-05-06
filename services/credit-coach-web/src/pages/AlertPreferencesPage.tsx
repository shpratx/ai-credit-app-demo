import { PageShell } from '../components/PageShell';
import { useGetPreferences, useUpdatePreferences } from '../api/alerts';
import { ToggleSwitch } from '../components/ToggleSwitch';
import { Card } from '../components/ui/Card';
import { Alert } from '../components/ui/Alert';

const thresholds = [30, 50, 75] as const;

export default function AlertPreferencesPage() {
  const { data: prefs, isLoading, error } = useGetPreferences();
  const updatePrefs = useUpdatePreferences();

  if (isLoading) {
    return (
      <div className="p-4 max-w-md mx-auto" aria-label="Loading alert preferences">
        <div className="animate-pulse space-y-4">
          <div className="h-48 bg-gray-200 rounded-card" />
          <div className="h-24 bg-gray-200 rounded-card" />
        </div>
      </div>
    );
  }

  if (error || !prefs) {
    return (
      <div className="p-4 max-w-md mx-auto">
        <Alert variant="error">Couldn't load your preferences.</Alert>
      </div>
    );
  }

  const handleToggle = (key: keyof typeof prefs, value: boolean) => {
    updatePrefs.mutate({ [key]: value });
  };

  const handleTurnOffAll = () => {
    updatePrefs.mutate({
      utilisationWarnings: false,
      paymentReminders: false,
      productEligibility: false,
      scoreChanges: false,
    });
  };

  return (
    <div className="p-4 max-w-md mx-auto">
      <Card title="Alert types">
        <ToggleSwitch
          label="Utilisation warnings"
          description="When credit usage approaches your threshold"
          checked={prefs.utilisationWarnings}
          onChange={(v) => handleToggle('utilisationWarnings', v)}
        />
        <ToggleSwitch
          label="Payment reminders"
          description="Upcoming payments that could affect your score"
          checked={prefs.paymentReminders}
          onChange={(v) => handleToggle('paymentReminders', v)}
        />
        <ToggleSwitch
          label="Product eligibility"
          description="When score improvements unlock new offers"
          checked={prefs.productEligibility}
          onChange={(v) => handleToggle('productEligibility', v)}
        />
        <ToggleSwitch
          label="Score changes"
          description="Monthly score refresh notifications"
          checked={prefs.scoreChanges}
          onChange={(v) => handleToggle('scoreChanges', v)}
        />
      </Card>

      <Card title="Utilisation threshold" className="mt-4">
        <p className="text-caption text-text-secondary mb-3">Alert me when utilisation reaches:</p>
        <div className="flex gap-2" role="radiogroup" aria-label="Utilisation threshold">
          {thresholds.map((t) => (
            <button
              key={t}
              role="radio"
              aria-checked={prefs.utilisationThreshold === t}
              onClick={() => updatePrefs.mutate({ utilisationThreshold: t })}
              className={`px-4 py-2.5 rounded-full border-[1.5px] text-sm ${
                prefs.utilisationThreshold === t
                  ? 'border-lloyds-green bg-lloyds-green-light text-lloyds-green font-bold'
                  : 'border-border bg-transparent'
              }`}
              aria-label={`${t}% threshold`}
            >
              {t}%
            </button>
          ))}
        </div>
      </Card>

      <Card className="mt-4 border-status-negative border-[1.5px]">
        <h3 className="text-card-title font-bold text-status-negative">Turn off all alerts</h3>
        <p className="text-caption text-text-secondary mt-1">
          You'll stop receiving credit health notifications. You can turn them back on at any time.
        </p>
        <button
          onClick={handleTurnOffAll}
          className="mt-3 px-4 py-2.5 rounded-lg border-[1.5px] border-status-negative bg-transparent text-status-negative text-sm font-bold cursor-pointer"
          aria-label="Turn off all alerts"
        >
          Turn off all alerts
        </button>
      </Card>
    </div>
  );
}
