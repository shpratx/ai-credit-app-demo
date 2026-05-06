import { Link } from 'react-router-dom';
import { useGetOffers, useAcceptOffer } from '../api/offers';
import { OfferCard } from '../components/OfferCard';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Alert } from '../components/ui/Alert';

export default function OffersPage() {
  const { data, isLoading, error } = useGetOffers();
  const acceptOffer = useAcceptOffer();

  if (isLoading) {
    return (
      <div className="p-4 max-w-md mx-auto" aria-label="Loading offers">
        <div className="animate-pulse space-y-4">
          <div className="h-12 bg-gray-200 rounded-lg" />
          <div className="h-64 bg-gray-200 rounded-card" />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-4 max-w-md mx-auto">
        <Alert variant="error">Couldn't load your offers. Please try again later.</Alert>
      </div>
    );
  }

  if (!data) return null;

  // Suppressed state (financial distress)
  if (data.suppressed) {
    return (
      <div className="p-4 max-w-md mx-auto">
        <div className="text-center py-8">
          <div className="text-4xl mb-3">💚</div>
          <h1 className="text-lg font-bold">We're here to help</h1>
          <p className="text-sm text-text-secondary mt-3 leading-relaxed">
            We're focused on helping you improve your financial health right now. Your improvement plan and score monitoring are still available.
          </p>
        </div>
        <Card title="Free support available">
          <div className="text-sm leading-[2]">
            <a href="tel:08001381111" className="text-lloyds-green font-bold no-underline block">📞 StepChange — 0800 138 1111</a>
            <a href="https://citizensadvice.org.uk" className="text-lloyds-green font-bold no-underline block">🌐 Citizens Advice</a>
            <a href="tel:08088084000" className="text-lloyds-green font-bold no-underline block">📞 National Debtline — 0808 808 4000</a>
          </div>
        </Card>
      </div>
    );
  }

  // No offers
  if (data.offers.length === 0) {
    return (
      <div className="p-4 max-w-md mx-auto text-center py-12">
        <div className="w-16 h-16 rounded-[16px] bg-status-info-bg flex items-center justify-center text-[28px] mx-auto mb-4">🎯</div>
        <h1 className="text-lg font-bold">No offers right now</h1>
        <p className="text-sm text-text-secondary mt-2 max-w-[280px] mx-auto leading-relaxed">
          Based on your current credit profile, we don't have a pre-approved offer for you yet. Keep improving your score and we'll let you know when you qualify.
        </p>
        <Link to="/credit-coach/plan">
          <Button variant="secondary" className="mt-6">View your plan →</Button>
        </Link>
      </div>
    );
  }

  const offer = data.offers[0]!;

  return (
    <div className="p-4 max-w-md mx-auto">
      <div className="p-3 bg-status-positive-bg rounded-lg text-sm text-[#004D36] mb-4 flex gap-2 items-center">
        <span>✓</span>
        <span><strong>Pre-approved</strong> — You're guaranteed acceptance if you apply.</span>
      </div>

      <OfferCard
        productName={offer.productName}
        amount={offer.amount}
        apr={offer.apr}
        term={offer.term}
        monthlyPayment={offer.monthlyPayment}
        totalPayable={offer.totalPayable}
        totalChargeForCredit={offer.totalChargeForCredit}
        isPreApproved={offer.isPreApproved}
        onApply={() => acceptOffer.mutate(offer.offerId)}
        isApplying={acceptOffer.isPending}
      />

      {/* Representative Example */}
      <div className="bg-bg-app border border-border rounded-lg p-4 mt-4 text-caption leading-relaxed">
        <p className="text-sm font-bold mb-2">Representative Example</p>
        Borrow <strong>£{offer.amount.toLocaleString()}</strong> over <strong>{offer.term} months</strong> at an interest rate of <strong>{offer.apr}% p.a.</strong> (fixed). Representative <strong>{offer.apr}% APR</strong>. Monthly repayment: <strong>£{offer.monthlyPayment.toFixed(2)}</strong>. Total amount payable: <strong>£{offer.totalPayable.toFixed(2)}</strong>. Total charge for credit: <strong>£{offer.totalChargeForCredit.toFixed(2)}</strong>.
      </div>

      <div className="mt-4 flex gap-3">
        <Link to={`/credit-coach/offers/${offer.offerId}/secci`} className="flex-1">
          <Button variant="secondary" className="w-full text-sm" aria-label="View full loan details">View full details</Button>
        </Link>
        <Link to={`/credit-coach/offers/${offer.offerId}/explanation`} className="flex-1">
          <Button variant="ghost" className="w-full text-sm" aria-label="How we decided">How we decided</Button>
        </Link>
      </div>
    </div>
  );
}
