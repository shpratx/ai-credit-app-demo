import { PageShell } from '../components/PageShell';
import { useParams } from 'react-router-dom';
import { useGetDecisionExplanation } from '../api/compliance';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Alert } from '../components/ui/Alert';

export default function DecisionExplanationPage() {
  const { offerId } = useParams<{ offerId: string }>();
  const { data, isLoading, error } = useGetDecisionExplanation(offerId ?? '');

  if (isLoading) {
    return (
      <div className="p-4 max-w-md mx-auto" aria-label="Loading decision explanation">
        <div className="animate-pulse space-y-4">
          <div className="h-48 bg-gray-200 rounded-card" />
          <div className="h-32 bg-gray-200 rounded-card" />
        </div>
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="p-4 max-w-md mx-auto">
        <Alert variant="error">Couldn't load decision explanation.</Alert>
      </div>
    );
  }

  return (
    <div className="p-4 max-w-md mx-auto">
      <Card title="How your offer was determined" subtitle="This offer was generated automatically based on your credit profile. Here's what we considered:">
        <dl className="mt-4">
          {data.factors.map((f) => (
            <div key={f.factor} className="flex justify-between py-2.5 border-b border-border text-sm last:border-none">
              <dt className="text-text-secondary">{f.factor}</dt>
              <dd className="font-bold">{f.value}</dd>
            </div>
          ))}
          <div className="flex justify-between py-2.5 border-b border-border text-sm">
            <dt className="text-text-secondary">Risk tier</dt>
            <dd className="font-bold">{data.riskTier}</dd>
          </div>
          <div className="flex justify-between py-2.5 text-sm">
            <dt className="text-text-secondary">Rate offered</dt>
            <dd className="font-bold">{data.rateOffered}</dd>
          </div>
        </dl>
      </Card>

      <Card title="Your rights" className="mt-4">
        <div className="text-sm leading-[1.8] mt-2">
          <p>Under data protection law (UK GDPR Article 22), you have the right to:</p>
          <ul className="mt-2 space-y-1">
            <li>• <strong>Request human review</strong> of this decision</li>
            <li>• <strong>Express your point of view</strong> about the offer</li>
            <li>• <strong>Contest the decision</strong> if you believe it's unfair</li>
          </ul>
        </div>
        <Button variant="secondary" className="mt-4" aria-label="Request human review">
          Request human review
        </Button>
      </Card>
    </div>
  );
}
