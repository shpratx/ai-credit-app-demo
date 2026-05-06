import { PageShell } from '../components/PageShell';
import { useState } from 'react';
import { useRunSimulation } from '../api/simulator';
import { ScenarioChip } from '../components/ScenarioChip';
import { SimulationResult } from '../components/SimulationResult';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Alert } from '../components/ui/Alert';

const scenarios = [
  { id: 'pay_down', label: 'Pay down credit card debt', icon: '💳' },
  { id: 'close_account', label: 'Close an old account', icon: '🔒' },
  { id: 'open_credit', label: 'Open new credit', icon: '📝' },
  { id: 'miss_payment', label: 'Miss a payment', icon: '⚠️' },
  { id: 'reduce_utilisation', label: 'Reduce utilisation to 30%', icon: '📉' },
];

export default function SimulatorPage() {
  const [selectedScenario, setSelectedScenario] = useState<string | null>(null);
  const [amount, setAmount] = useState('');
  const simulation = useRunSimulation();

  const handleSimulate = () => {
    if (!selectedScenario) return;
    simulation.mutate({
      scenario: selectedScenario,
      amount: amount ? parseFloat(amount.replace(/[£,]/g, '')) : undefined,
    });
  };

  const handleReset = () => {
    simulation.reset();
    setSelectedScenario(null);
    setAmount('');
  };

  return (
    <div className="p-4 max-w-md mx-auto">
      <Card title="Simulate a scenario" subtitle="See how different actions could affect your credit score. Choose a scenario below."><span /></Card>

      {!simulation.data && (
        <>
          <div className="flex flex-col gap-3 my-4" role="radiogroup" aria-label="Simulation scenarios">
            {scenarios.map((s) => (
              <ScenarioChip
                key={s.id}
                label={s.label}
                icon={s.icon}
                selected={selectedScenario === s.id}
                onSelect={() => setSelectedScenario(s.id)}
              />
            ))}
          </div>

          {selectedScenario && (
            <Card className="border-lloyds-green">
              <div className="space-y-4">
                <Input
                  label="How much would you pay off?"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  placeholder="Enter amount (e.g. £1,200)"
                  aria-label="Amount to simulate"
                />
                <Button onClick={handleSimulate} isLoading={simulation.isPending} aria-label="See the impact">
                  See the impact
                </Button>
              </div>
            </Card>
          )}
        </>
      )}

      {simulation.error && (
        <Alert variant="error">
          Simulation taking too long. Try a simpler scenario, or check back later.
        </Alert>
      )}

      {simulation.data && (
        <>
          <SimulationResult {...simulation.data} />
          <div className="mt-4 space-y-3">
            <Button onClick={handleReset} aria-label="Try another scenario">Try another scenario</Button>
          </div>
        </>
      )}

      <p className="text-micro text-text-muted italic mt-4">
        Simulations are estimates based on typical scoring models. Your actual score may differ.
      </p>
    </div>
  );
}
