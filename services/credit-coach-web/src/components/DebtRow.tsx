import { Badge } from './ui/Badge';

interface DebtRowProps {
  lender: string;
  type: string;
  balance: number;
  monthlyPayment: number;
  utilisationPercent: number | null;
  isDisputed: boolean;
}

const typeIcons: Record<string, string> = {
  credit_card: '💳',
  personal_loan: '🏦',
  mortgage: '🏠',
  store_card: '🛍️',
  overdraft: '🏦',
};

export function DebtRow({ lender, type, balance, monthlyPayment, utilisationPercent, isDisputed }: DebtRowProps) {
  const icon = typeIcons[type] ?? '💳';
  const utilisationColor =
    utilisationPercent !== null && utilisationPercent > 50
      ? 'bg-status-pending'
      : utilisationPercent !== null && utilisationPercent > 75
        ? 'bg-status-negative'
        : 'bg-lloyds-green';

  return (
    <div className="flex items-center gap-3 py-3.5 border-b border-border last:border-none" aria-label={`${lender} ${type} account, balance £${balance.toLocaleString()}`}>
      <div className={`w-10 h-10 rounded-[10px] flex items-center justify-center text-lg shrink-0 ${isDisputed ? 'bg-status-negative-bg' : 'bg-bg-app'}`}>
        {isDisputed ? '⚠️' : icon}
      </div>
      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-1">
          <span className="text-[15px] font-bold truncate">{lender}</span>
          {isDisputed && <Badge variant="info">Disputed</Badge>}
        </div>
        <p className="text-xs text-text-muted">
          {type.replace('_', ' ')}{utilisationPercent !== null ? ` · ${utilisationPercent}% used` : ''}
        </p>
        {utilisationPercent !== null && (
          <div className="w-4/5 h-1 bg-border rounded-full mt-1.5">
            <div className={`h-full rounded-full ${utilisationColor}`} style={{ width: `${utilisationPercent}%` }} />
          </div>
        )}
      </div>
      <div className="text-right shrink-0">
        <p className="text-[15px] font-bold">£{balance.toLocaleString()}</p>
        <p className="text-xs text-text-secondary">£{monthlyPayment}/mo</p>
      </div>
    </div>
  );
}
