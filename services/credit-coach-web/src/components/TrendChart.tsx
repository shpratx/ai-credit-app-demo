import type { ScorePoint, Annotation } from '../api/history';

interface TrendChartProps {
  points: ScorePoint[];
  annotations: Annotation[];
}

export function TrendChart({ points, annotations }: TrendChartProps) {
  if (points.length < 2) return null;

  const scores = points.map((p) => p.score);
  const minScore = Math.min(...scores) - 10;
  const maxScore = Math.max(...scores) + 10;
  const range = maxScore - minScore;

  const width = 360;
  const height = 140;
  const padding = 10;

  const getX = (i: number) => padding + (i / (points.length - 1)) * (width - padding * 2);
  const getY = (score: number) => height - padding - ((score - minScore) / range) * (height - padding * 2);

  const polylinePoints = points.map((p, i) => `${getX(i)},${getY(p.score)}`).join(' ');

  const annotationDates = new Set(annotations.map((a) => a.date));

  const firstPoint = points[0];
  const lastPoint = points[points.length - 1];
  const textAlt = `Score trend: ${firstPoint?.score} in ${firstPoint?.date} to ${lastPoint?.score} in ${lastPoint?.date}`;

  return (
    <div className="bg-surface border border-border rounded-card p-5" role="img" aria-label={textAlt}>
      <svg viewBox={`0 0 ${width} ${height}`} className="w-full h-[140px]" aria-hidden="true">
        <polyline
          points={polylinePoints}
          fill="none"
          stroke="#006A4D"
          strokeWidth="2.5"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
        {points.map((p, i) => {
          const isAnnotated = annotationDates.has(p.date);
          return (
            <circle
              key={p.date}
              cx={getX(i)}
              cy={getY(p.score)}
              r={isAnnotated ? 5 : i === points.length - 1 ? 5 : 0}
              fill={isAnnotated ? '#1565C0' : '#006A4D'}
              stroke={isAnnotated ? '#fff' : 'none'}
              strokeWidth={isAnnotated ? 2 : 0}
            />
          );
        })}
      </svg>
      <div className="flex justify-between text-micro text-text-muted mt-2 px-1">
        {points.filter((_, i) => i % Math.ceil(points.length / 6) === 0 || i === points.length - 1).map((p) => (
          <span key={p.date}>{new Date(p.date).toLocaleDateString('en-GB', { month: 'short' })}</span>
        ))}
      </div>
    </div>
  );
}
