import { Button } from './ui/Button';

interface OfferCardProps {
  productName: string;
  amount: number;
  apr: number;
  term: number;
  monthlyPayment: number;
  totalPayable: number;
  totalChargeForCredit: number;
  isPreApproved: boolean;
  onApply: () => void;
  isApplying?: boolean;
}

export function OfferCard({
  productName,
  amount,
  apr,
  term,
  monthlyPayment,
  totalPayable,
  totalChargeForCredit,
  isPreApproved,
  onApply,
  isApplying = false,
}: OfferCardProps) {
  return (
    <div className="bg-surface border-2 border-lloyds-green rounded-[16px] p-6 relative" aria-label={`${productName} offer for £${amount.toLocaleString()}`}>
      {isPreApproved && (
        <span className="absolute -top-2.5 right-4 bg-lloyds-green text-white px-3 py-1 rounded-full text-[11px] font-bold">
          Pre-approved
        </span>
      )}
      <p className="text-caption text-text-secondary">{productName}</p>
      <p className="text-[28px] font-bold mt-1">£{amount.toLocaleString()}</p>
      <p className="text-sm text-text-secondary mt-1">at <strong>{apr}% APR</strong> (fixed)</p>

      <dl className="mt-5 pt-4 border-t border-border">
        <div className="flex justify-between py-2.5 border-b border-border text-sm">
          <dt className="text-text-secondary">Monthly payment</dt>
          <dd className="font-bold">£{monthlyPayment.toFixed(2)}</dd>
        </div>
        <div className="flex justify-between py-2.5 border-b border-border text-sm">
          <dt className="text-text-secondary">Term</dt>
          <dd className="font-bold">{term} months</dd>
        </div>
        <div className="flex justify-between py-2.5 border-b border-border text-sm">
          <dt className="text-text-secondary">Total amount payable</dt>
          <dd className="font-bold">£{totalPayable.toFixed(2)}</dd>
        </div>
        <div className="flex justify-between py-2.5 text-sm">
          <dt className="text-text-secondary">Total charge for credit</dt>
          <dd className="font-bold">£{totalChargeForCredit.toFixed(2)}</dd>
        </div>
      </dl>

      <Button className="mt-5 w-full" onClick={onApply} isLoading={isApplying} aria-label="Apply now">
        Apply now — one tap
      </Button>
      <p className="text-xs text-text-muted text-center mt-2">14-day cooling-off period applies after acceptance</p>
    </div>
  );
}
