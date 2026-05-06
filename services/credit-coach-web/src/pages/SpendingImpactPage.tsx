import { PageShell } from '../components/PageShell';
import { useGetSpendingImpact } from '../api/plan';
import { Card } from '../components/ui/Card';
import { Alert } from '../components/ui/Alert';

export default function SpendingImpactPage() {
  const { data, isLoading, error } = useGetSpendingImpact();

  if (isLoading) {
    return (
      <div className="p-4 max-w-md mx-auto" aria-label="Loading spending impact">
        <div className="animate-pulse space-y-3">
          <div className="h-24 bg-gray-200 rounded-card" />
          <div className="h-16 bg-gray-200 rounded-lg" />
          <div className="h-16 bg-gray-200 rounded-lg" />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-4 max-w-md mx-auto">
        <Alert variant="error">Couldn't load spending impact data. Please try again later.</Alert>
      </div>
    );
  }

  return (
    <div className="p-4 max-w-md mx-auto">
      <Card title="How your spending affects your credit" subtitle="Based on your Financial Assistant spending data, here's how changes could improve your affordability score."><span /></Card>

      <ul className="space-y-3 mt-4" aria-label="Spending insights">
        {data?.insights.map((insight) => (
          <li key={insight.category} className="flex items-center gap-3 p-3.5 bg-lloyds-green-light rounded-lg">
            <div className="w-9 h-9 rounded-full bg-surface flex items-center justify-center text-base shrink-0" aria-hidden="true">
              {insight.icon}
            </div>
            <div className="flex-1">
              <p className="text-sm font-bold">{insight.category} · £{insight.monthlyAmount}/month</p>
              <p className="text-caption text-text-secondary">
                Reducing by £{insight.suggestedReduction}/month → <strong className="text-status-positive">+{insight.affordabilityImpactPercent}% affordability</strong>
              </p>
            </div>
          </li>
        ))}
      </ul>

      <Card className="mt-4">
        <p className="text-sm text-text-secondary leading-relaxed">
          <strong>What is affordability?</strong><br />
          Lenders check if you can comfortably repay a loan after your essential spending. Higher affordability = better loan offers and rates.
        </p>
      </Card>

      <p className="text-micro text-text-muted italic mt-3">
        Spending data from your Financial Assistant · Affordability estimates are indicative
      </p>
    </div>
  );
}
