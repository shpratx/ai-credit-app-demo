import { Badge } from './ui/Badge';

interface ActionItemProps {
  rank: number;
  title: string;
  description: string;
  explanation: string;
  impactPoints: number;
  timeframe: string;
  completed: boolean;
  completedAt: string | null;
  onComplete?: () => void;
}

export function ActionItem({
  rank,
  title,
  description,
  explanation,
  impactPoints,
  timeframe,
  completed,
  completedAt,
  onComplete,
}: ActionItemProps) {
  return (
    <li className="flex gap-3 py-4 border-b border-border last:border-none" aria-label={`Action ${rank}: ${title}`}>
      <div
        className={`w-7 h-7 rounded-full flex items-center justify-center text-micro font-bold shrink-0 ${
          completed ? 'bg-status-positive-bg text-status-positive' : 'bg-lloyds-green text-white'
        }`}
        aria-hidden="true"
      >
        {completed ? '✓' : rank}
      </div>
      <div className="flex-1">
        <p className={`text-[15px] font-bold ${completed ? 'line-through opacity-60' : ''}`}>{title}</p>
        <p className="text-caption text-text-secondary mt-0.5">{description}</p>
        <div className="flex gap-2 mt-2 flex-wrap">
          <Badge variant="positive">+{impactPoints} pts</Badge>
          {completed && completedAt ? (
            <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-micro font-bold bg-gray-100 text-text-muted">
              Completed {completedAt}
            </span>
          ) : (
            <Badge variant="pending">~{timeframe}</Badge>
          )}
        </div>
        {!completed && explanation && (
          <p className="text-xs text-text-secondary mt-2 italic">
            <strong>Why:</strong> {explanation}
          </p>
        )}
        {!completed && onComplete && (
          <button
            onClick={onComplete}
            className="mt-2 text-xs font-bold text-lloyds-green underline underline-offset-2"
            aria-label={`Mark "${title}" as done`}
          >
            Mark as done
          </button>
        )}
      </div>
    </li>
  );
}
