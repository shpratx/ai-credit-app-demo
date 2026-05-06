import type { ChangeContributor } from '../api/score';
import { Card } from './ui/Card';

interface ScoreChangeCardProps {
  totalChange: number;
  changeDirection: 'up' | 'down' | 'unchanged';
  contributors: ChangeContributor[];
}

export function ScoreChangeCard({ totalChange, changeDirection, contributors }: ScoreChangeCardProps) {
  const directionText =
    changeDirection === 'up' ? 'increased' : changeDirection === 'down' ? 'decreased' : 'unchanged';

  return (
    <Card title="Why your score changed">
      <p className="text-body-sm text-text-secondary">
        Your score {directionText} by {Math.abs(totalChange)} points this month because:
      </p>
      <div className="mt-3">
        {contributors.map((c) => (
          <div
            key={c.factor}
            className="flex justify-between py-2.5 border-b border-border last:border-none text-body-sm"
          >
            <span className="text-text-secondary">{c.factor}</span>
            <span
              className={`font-bold ${c.pointImpact >= 0 ? 'text-status-positive' : 'text-status-negative'}`}
            >
              {c.pointImpact >= 0 ? '+' : ''}
              {c.pointImpact} pts
            </span>
          </div>
        ))}
      </div>
    </Card>
  );
}
