import { PageShell } from '../components/PageShell';
import { useGetFactors } from '../api/score';
import { Card } from '../components/ui/Card';
import { Alert } from '../components/ui/Alert';

function FactorsPage() {
  const { data, isLoading, error } = useGetFactors();

  if (isLoading) {
    return (
      <main className="min-h-screen bg-background p-4 max-w-[420px] mx-auto" aria-label="Score factors" aria-busy="true">
        <div className="skeleton h-6 w-2/5 mb-4" />
        <div className="skeleton h-48 rounded-card mb-4" />
        <div className="skeleton h-36 rounded-card" />
      </main>
    );
  }

  if (error || !data) {
    return (
      <main className="min-h-screen bg-background p-4 max-w-[420px] mx-auto" aria-label="Score factors">
        <Alert variant="error" title="Couldn't load factors">
          Please try again later.
        </Alert>
      </main>
    );
  }

  const positive = data.factors.filter((f) => f.direction === 'positive');
  const negative = data.factors.filter((f) => f.direction === 'negative');

  return (
    <main
      className="min-h-screen bg-background p-4 max-w-[420px] mx-auto"
      aria-label={`${data.factors.length} factors: ${positive.length} positive, ${negative.length} negative`}
    >
      <header className="h-14 flex items-center justify-center border-b border-border bg-surface -mx-4 -mt-4 px-4 mb-4">
        <h1 className="text-card-title font-bold">Score Factors</h1>
      </header>

      <p className="text-body-sm text-text-secondary mb-4">
        These factors affect your Experian credit score. Factors are listed by impact — highest first.
      </p>

      {positive.length > 0 && (
        <Card className="mb-4">
          <p className="text-caption font-bold text-status-positive mb-3">HELPING YOUR SCORE</p>
          {positive.map((f) => (
            <div key={f.factorId} className="flex items-start gap-3 py-3.5 border-b border-border last:border-none">
              <div className="w-8 h-8 rounded-full bg-status-positive-bg text-status-positive flex items-center justify-center text-sm flex-shrink-0" aria-hidden="true">✓</div>
              <div>
                <p className="text-body-sm font-bold">{f.title}</p>
                <p className="text-caption text-text-secondary mt-0.5">{f.description}</p>
              </div>
            </div>
          ))}
        </Card>
      )}

      {negative.length > 0 && (
        <Card>
          <p className="text-caption font-bold text-status-negative mb-3">HURTING YOUR SCORE</p>
          {negative.map((f) => (
            <div key={f.factorId} className="flex items-start gap-3 py-3.5 border-b border-border last:border-none">
              <div className="w-8 h-8 rounded-full bg-status-negative-bg text-status-negative flex items-center justify-center text-sm flex-shrink-0" aria-hidden="true">!</div>
              <div>
                <p className="text-body-sm font-bold">{f.title}</p>
                <p className="text-caption text-text-secondary mt-0.5">{f.description}</p>
              </div>
            </div>
          ))}
        </Card>
      )}
    </main>
  );
}

export default FactorsPage;
