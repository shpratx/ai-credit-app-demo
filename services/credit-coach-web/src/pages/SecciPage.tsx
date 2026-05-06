import { PageShell } from '../components/PageShell';
import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useGetSecci, useAcceptOffer } from '../api/offers';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Alert } from '../components/ui/Alert';

export default function SecciPage() {
  const { offerId } = useParams<{ offerId: string }>();
  const { data, isLoading, error } = useGetSecci(offerId ?? '');
  const acceptOffer = useAcceptOffer();
  const [agreed, setAgreed] = useState(false);

  if (isLoading) {
    return (
      <div className="p-4 max-w-md mx-auto" aria-label="Loading pre-contract information">
        <div className="animate-pulse space-y-4">
          <div className="h-64 bg-gray-200 rounded-card" />
          <div className="h-32 bg-gray-200 rounded-card" />
        </div>
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="p-4 max-w-md mx-auto">
        <Alert variant="error">Couldn't load pre-contract information.</Alert>
      </div>
    );
  }

  return (
    <div className="p-4 max-w-md mx-auto">
      <Card title="Pre-Contract Credit Information" subtitle="Please read carefully before accepting">
        <dl className="mt-4">
          <div className="flex justify-between py-2.5 border-b border-border text-sm">
            <dt className="text-text-secondary">Type of credit</dt>
            <dd className="font-bold">{data.typeOfCredit}</dd>
          </div>
          <div className="flex justify-between py-2.5 border-b border-border text-sm">
            <dt className="text-text-secondary">Total amount of credit</dt>
            <dd className="font-bold">£{data.totalAmountOfCredit.toLocaleString()}</dd>
          </div>
          <div className="flex justify-between py-2.5 border-b border-border text-sm">
            <dt className="text-text-secondary">Duration</dt>
            <dd className="font-bold">{data.duration} months</dd>
          </div>
          <div className="flex justify-between py-2.5 border-b border-border text-sm">
            <dt className="text-text-secondary">Rate of interest</dt>
            <dd className="font-bold">{data.rateOfInterest}</dd>
          </div>
          <div className="flex justify-between py-2.5 border-b border-border text-sm">
            <dt className="text-text-secondary">APR</dt>
            <dd className="font-bold text-lloyds-green">{data.apr}% APR</dd>
          </div>
          <div className="flex justify-between py-2.5 border-b border-border text-sm">
            <dt className="text-text-secondary">Monthly repayment</dt>
            <dd className="font-bold">£{data.monthlyRepayment.toFixed(2)}</dd>
          </div>
          <div className="flex justify-between py-2.5 border-b border-border text-sm">
            <dt className="text-text-secondary">Total amount payable</dt>
            <dd className="font-bold text-base">£{data.totalAmountPayable.toFixed(2)}</dd>
          </div>
          <div className="flex justify-between py-2.5 border-b border-border text-sm">
            <dt className="text-text-secondary">Total charge for credit</dt>
            <dd className="font-bold">£{data.totalChargeForCredit.toFixed(2)}</dd>
          </div>
          <div className="flex justify-between py-2.5 border-b border-border text-sm">
            <dt className="text-text-secondary">Early repayment</dt>
            <dd className="font-bold">{data.earlyRepaymentFee}</dd>
          </div>
          <div className="flex justify-between py-2.5 text-sm">
            <dt className="text-text-secondary">Late payment fee</dt>
            <dd className="font-bold">{data.latePaymentFee}</dd>
          </div>
        </dl>
      </Card>

      <Card title="Your rights" className="mt-4">
        <div className="text-sm leading-[1.8] mt-2">
          <p><strong>Right to withdraw:</strong> {data.rightToWithdraw}</p>
          <p className="mt-2"><strong>Right to early repayment:</strong> {data.rightToEarlyRepayment}</p>
          <p className="mt-2"><strong>Right to information:</strong> You can request a copy of your agreement and statement at any time.</p>
        </div>
      </Card>

      <label className="flex gap-2 items-start mt-4 cursor-pointer">
        <input
          type="checkbox"
          checked={agreed}
          onChange={(e) => setAgreed(e.target.checked)}
          className="w-5 h-5 accent-lloyds-green mt-0.5 shrink-0"
          aria-label="I have read and understood the pre-contract credit information"
        />
        <span className="text-sm leading-relaxed">
          I have read and understood the pre-contract credit information and wish to proceed
        </span>
      </label>

      <Button
        className="mt-4 w-full"
        disabled={!agreed}
        onClick={() => offerId && acceptOffer.mutate(offerId)}
        isLoading={acceptOffer.isPending}
        aria-label="Accept and apply"
      >
        Accept & Apply
      </Button>
      <Button variant="secondary" className="mt-3 w-full" aria-label="Download as PDF">
        Download as PDF
      </Button>
    </div>
  );
}
