import { Badge } from './ui/Badge';

interface AlertCardProps {
  severity: 'high' | 'medium' | 'low';
  title: string;
  message: string;
  timestamp: string;
  onDismiss?: () => void;
}

const severityConfig = {
  high: { border: 'border-l-status-negative', badge: 'negative' as const, label: 'High impact' },
  medium: { border: 'border-l-status-pending', badge: 'pending' as const, label: 'Action needed' },
  low: { border: 'border-l-status-positive', badge: 'positive' as const, label: 'Good news' },
};

export function AlertCard({ severity, title, message, timestamp, onDismiss }: AlertCardProps) {
  const config = severityConfig[severity];
  return (
    <article
      className={`border-l-4 ${config.border} rounded-card p-4 mb-3 bg-surface shadow-card`}
      aria-label={`${severity} alert: ${title}`}
    >
      <div className="flex justify-between items-start">
        <h3 className="text-[15px] font-bold">{title}</h3>
        <Badge variant={config.badge}>{config.label}</Badge>
      </div>
      <p className="text-sm text-text-secondary mt-2 leading-relaxed">{message}</p>
      <div className="flex justify-between items-center mt-2">
        <time className="text-xs text-text-muted">{timestamp}</time>
        {onDismiss && (
          <button onClick={onDismiss} className="text-xs text-lloyds-green font-bold" aria-label={`Dismiss alert: ${title}`}>
            Dismiss
          </button>
        )}
      </div>
    </article>
  );
}
