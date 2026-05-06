import { PageShell } from '../components/PageShell';
import { useGetMultiBureau } from '../api/compliance';
import { Card } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Alert } from '../components/ui/Alert';

export default function MultiBureauPage() {
  const { data, isLoading, error } = useGetMultiBureau();

  if (isLoading) {
    return (
      <div className="p-4 max-w-md mx-auto" aria-label="Loading score comparison">
        <div className="animate-pulse space-y-4">
          <div className="h-24 bg-gray-200 rounded-card" />
          <div className="h-24 bg-gray-200 rounded-card" />
          <div className="h-24 bg-gray-200 rounded-card" />
        </div>
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="p-4 max-w-md mx-auto">
        <Alert variant="error">Couldn't load score comparison.</Alert>
      </div>
    );
  }

  return (
    <div className="p-4 max-w-md mx-auto">
      <p className="text-sm text-text-secondary mb-4">
        Each bureau uses a different scale. All three scores indicate the same credit health — they're just measured differently.
      </p>

      {data.scores.map((bureau, i) => (
        <Card key={bureau.provider} className={`mb-4 ${i === 0 ? 'border-lloyds-green' : ''}`}>
          <div className="flex justify-between items-center" aria-label={`${bureau.provider} score ${bureau.score} out of ${bureau.maxScore}`}>
            <div>
              <p className="text-caption text-text-secondary">{bureau.provider.charAt(0) + bureau.provider.slice(1).toLowerCase()}</p>
              <p className="text-[28px] font-bold">{bureau.score}</p>
              <p className="text-xs text-text-muted">out of {bureau.maxScore.toLocaleString()}</p>
            </div>
            <Badge variant="positive">{bureau.band}</Badge>
          </div>
          <div className="mt-3">
            <div className="w-full h-1.5 bg-border rounded-full" aria-label={`${bureau.normalisedPercent}% of maximum`}>
              <div className="h-full bg-lloyds-green rounded-full" style={{ width: `${bureau.normalisedPercent}%` }} />
            </div>
          </div>
        </Card>
      ))}

      <div className="p-3 bg-status-info-bg rounded-lg text-caption text-[#0D47A1] leading-relaxed">
        <strong>Why are the numbers different?</strong> {data.explanation}
      </div>
    </div>
  );
}
