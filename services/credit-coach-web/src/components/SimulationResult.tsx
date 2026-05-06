import { Badge } from './ui/Badge';

interface SimulationResultProps {
  currentScore: number;
  estimatedScore: number;
  change: number;
  confidence: 'high' | 'medium' | 'low';
  details: {
    utilisationBefore: string;
    utilisationAfter: string;
    bandChange: string;
    timeframe: string;
  };
}

const confidenceVariant = { high: 'info', medium: 'pending', low: 'negative' } as const;

export function SimulationResult({ currentScore, estimatedScore, change, confidence, details }: SimulationResultProps) {
  return (
    <div aria-label={`Estimated score ${estimatedScore}, increase of ${change} points, ${confidence} confidence`}>
      <div className="text-center p-6 bg-lloyds-green-light rounded-card">
        <div className="flex items-center justify-center gap-4 my-4">
          <div>
            <p className="text-caption text-text-muted">Current</p>
            <p className="text-[28px] font-bold">{currentScore}</p>
          </div>
          <span className="text-2xl text-lloyds-green" aria-hidden="true">→</span>
          <div>
            <p className="text-caption text-status-positive">Estimated</p>
            <p className="text-[28px] font-bold text-status-positive">{estimatedScore}</p>
          </div>
        </div>
        <Badge variant="positive">+{change} points</Badge>
        <div className="mt-3">
          <Badge variant={confidenceVariant[confidence]}>{confidence} confidence</Badge>
        </div>
      </div>

      <div className="bg-surface border border-border rounded-card p-5 mt-4">
        <h3 className="text-card-title font-bold">How this works</h3>
        <dl className="mt-3">
          <div className="flex justify-between py-2.5 border-b border-border text-sm">
            <dt className="text-text-secondary">Utilisation drops from</dt>
            <dd className="font-bold">{details.utilisationBefore} → {details.utilisationAfter}</dd>
          </div>
          <div className="flex justify-between py-2.5 border-b border-border text-sm">
            <dt className="text-text-secondary">Band change</dt>
            <dd className="font-bold">{details.bandChange}</dd>
          </div>
          <div className="flex justify-between py-2.5 border-b border-border text-sm">
            <dt className="text-text-secondary">Timeframe</dt>
            <dd className="font-bold">{details.timeframe}</dd>
          </div>
          <div className="flex justify-between py-2.5 text-sm">
            <dt className="text-text-secondary">Confidence</dt>
            <dd><Badge variant={confidenceVariant[confidence]}>{confidence}</Badge></dd>
          </div>
        </dl>
      </div>

      <div className="p-3 bg-status-pending-bg rounded-lg text-caption text-[#7D4600] leading-relaxed mt-4" role="note">
        <strong>⚠️ This is an estimate, not a guarantee.</strong> Actual score changes depend on multiple factors including other account activity and CRA scoring model updates.
      </div>
    </div>
  );
}
