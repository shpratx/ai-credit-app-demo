import { PageShell } from '../components/PageShell';
import { useState } from 'react';
import { useGetScoreTrend } from '../api/history';
import { TrendChart } from '../components/TrendChart';
import { Card } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Alert } from '../components/ui/Alert';

type Period = '6' | '12' | '24';

export default function ScoreHistoryPage() {
  const [period, setPeriod] = useState<Period>('12');
  const { data, isLoading, error } = useGetScoreTrend(period);

  if (isLoading) {
    return (
      <div className="p-4 max-w-md mx-auto" aria-label="Loading score history">
        <div className="animate-pulse space-y-4">
          <div className="h-10 bg-gray-200 rounded-full w-3/4" />
          <div className="h-[180px] bg-gray-200 rounded-card" />
          <div className="h-32 bg-gray-200 rounded-card" />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-4 max-w-md mx-auto">
        <Alert variant="error">Couldn't load your score history. Please try again later.</Alert>
      </div>
    );
  }

  if (!data || data.points.length < 2) {
    return (
      <div className="p-4 max-w-md mx-auto">
        <Card>
          <div className="text-center py-6">
            <div className="text-4xl mb-3">📈</div>
            <h2 className="text-[17px] font-bold">Your trend is building</h2>
            <p className="text-sm text-text-secondary mt-2 leading-relaxed">
              We need at least 2 months of score data to show a meaningful trend.
            </p>
          </div>
        </Card>
      </div>
    );
  }

  const periods: { value: Period; label: string }[] = [
    { value: '6', label: '6 months' },
    { value: '12', label: '12 months' },
    { value: '24', label: '24 months' },
  ];

  return (
    <div className="p-4 max-w-md mx-auto">
      {/* Period selector */}
      <div className="flex gap-2 mb-4" role="radiogroup" aria-label="Time period">
        {periods.map((p) => (
          <button
            key={p.value}
            onClick={() => setPeriod(p.value)}
            role="radio"
            aria-checked={period === p.value}
            className={`px-3.5 py-2 rounded-full border-[1.5px] text-caption font-bold transition-colors ${
              period === p.value
                ? 'border-lloyds-green bg-lloyds-green text-white'
                : 'border-border bg-transparent text-text-secondary'
            }`}
            aria-label={`Show ${p.label}`}
          >
            {p.label}
          </button>
        ))}
      </div>

      {/* Chart */}
      <div className="relative mb-4">
        <div className="absolute top-3 right-5 z-10">
          <Badge variant="positive">↑ +{data.statistics.totalChange} pts</Badge>
        </div>
        <TrendChart points={data.points} annotations={data.annotations} />
      </div>

      {/* Statistics */}
      <Card title="Summary">
        <dl>
          <div className="flex justify-between py-2.5 border-b border-border text-sm">
            <dt className="text-text-secondary">Current score</dt>
            <dd className="font-bold">{data.statistics.current}</dd>
          </div>
          <div className="flex justify-between py-2.5 border-b border-border text-sm">
            <dt className="text-text-secondary">Highest</dt>
            <dd className="font-bold">{data.statistics.highest} ({data.statistics.highestDate})</dd>
          </div>
          <div className="flex justify-between py-2.5 border-b border-border text-sm">
            <dt className="text-text-secondary">Lowest</dt>
            <dd className="font-bold">{data.statistics.lowest} ({data.statistics.lowestDate})</dd>
          </div>
          <div className="flex justify-between py-2.5 border-b border-border text-sm">
            <dt className="text-text-secondary">Average</dt>
            <dd className="font-bold">{data.statistics.average}</dd>
          </div>
          <div className="flex justify-between py-2.5 text-sm">
            <dt className="text-text-secondary">Trend</dt>
            <dd className="font-bold text-status-positive">↑ {data.statistics.trend}</dd>
          </div>
        </dl>
      </Card>

      {/* Annotations */}
      {data.annotations.length > 0 && (
        <Card title="Key events" className="mt-4">
          <ul aria-label="Score annotations">
            {data.annotations.map((ann) => (
              <li key={ann.date + ann.title} className="flex gap-2 items-start py-3 border-b border-border last:border-none">
                <div className={`w-2 h-2 rounded-full mt-1.5 shrink-0 ${ann.type === 'event' ? 'bg-status-info' : 'bg-status-positive'}`} />
                <div>
                  <p className="text-sm font-bold">{ann.date} · {ann.title}</p>
                  <p className="text-caption text-text-secondary">{ann.description}</p>
                </div>
              </li>
            ))}
          </ul>
        </Card>
      )}
    </div>
  );
}
