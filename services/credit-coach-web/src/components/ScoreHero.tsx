import type { ScoreData } from '../api/score';

interface ScoreHeroProps {
  score: ScoreData;
}

function formatDate(iso: string): string {
  return new Date(iso).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' });
}

export function ScoreHero({ score }: ScoreHeroProps) {
  const percentage = Math.round((score.score / score.maxScore) * 100);
  const changeText =
    score.changeDirection === 'up'
      ? `↑ ${score.change} points since last month`
      : score.changeDirection === 'down'
        ? `↓ ${Math.abs(score.change ?? 0)} points since last month`
        : '— No change since last month';

  return (
    <div
      className="bg-lloyds-green rounded-hero p-6 text-white text-center mb-4"
      aria-label={`Credit score ${score.score} out of ${score.maxScore}, ${score.bandLabel}, ${changeText}`}
      role="region"
    >
      <p className="text-caption opacity-75">
        {score.provider} · Updated {formatDate(score.retrievedAt)}
        {score.isStale && ' (stale)'}
      </p>
      <p className="text-hero mt-1" aria-hidden="true">
        {score.score}
      </p>
      <p className="text-body-sm opacity-85 mt-1">{score.bandLabel}</p>
      <p className="text-caption opacity-75 mt-2">{changeText}</p>
      <div className="mt-4">
        <div
          className="w-full h-2 rounded-full bg-white/25"
          role="progressbar"
          aria-valuenow={score.score}
          aria-valuemin={0}
          aria-valuemax={score.maxScore}
          aria-label="Score progress"
        >
          <div className="h-full rounded-full bg-white" style={{ width: `${percentage}%` }} />
        </div>
        <div className="flex justify-between text-[11px] opacity-60 mt-1">
          <span>0</span>
          <span>Poor</span>
          <span>Fair</span>
          <span>Good</span>
          <span>Excellent</span>
          <span>{score.maxScore}</span>
        </div>
      </div>
    </div>
  );
}
