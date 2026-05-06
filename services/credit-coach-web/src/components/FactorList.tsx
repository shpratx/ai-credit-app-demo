import type { ScoreFactor } from '../api/score';
import { Badge } from './ui/Badge';

interface FactorListProps {
  factors: ScoreFactor[];
}

export function FactorList({ factors }: FactorListProps) {
  return (
    <div aria-label="Score factors">
      {factors.map((factor) => (
        <div
          key={factor.factorId}
          className="flex items-center gap-3 py-3.5 border-b border-border last:border-none"
        >
          <div
            className={`w-8 h-8 rounded-full flex items-center justify-center text-sm flex-shrink-0 ${
              factor.direction === 'positive'
                ? 'bg-status-positive-bg text-status-positive'
                : 'bg-status-negative-bg text-status-negative'
            }`}
            aria-hidden="true"
          >
            {factor.direction === 'positive' ? '✓' : '!'}
          </div>
          <span className="flex-1 text-body-sm">{factor.title}</span>
          <Badge variant={factor.direction === 'positive' ? 'positive' : 'negative'}>
            {factor.direction === 'positive' ? '+' : '−'}
            {factor.impact.charAt(0).toUpperCase() + factor.impact.slice(1)}
          </Badge>
        </div>
      ))}
    </div>
  );
}
