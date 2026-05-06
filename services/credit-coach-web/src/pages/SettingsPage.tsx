import { PageShell } from '../components/PageShell';
import { useGetConsents, useWithdrawConsent } from '../api/consent';
import { useAuthStore } from '../store/authStore';
import { Card } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Alert } from '../components/ui/Alert';

function SettingsPage() {
  const customerId = useAuthStore((s) => s.customerId) ?? '';
  const { data: consents, isLoading } = useGetConsents(customerId);
  const withdrawConsent = useWithdrawConsent();

  const activeConsent = consents?.find((c) => c.status === 'GRANTED');

  if (isLoading) {
    return (
      <main className="min-h-screen bg-background p-4 max-w-[420px] mx-auto" aria-label="Credit Coach settings" aria-busy="true">
        <div className="skeleton h-48 rounded-card" />
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-background p-4 max-w-[420px] mx-auto" aria-label="Credit Coach settings">
      <header className="h-14 flex items-center justify-center border-b border-border bg-surface -mx-4 -mt-4 px-4 mb-4">
        <h1 className="text-card-title font-bold">Credit Coach Settings</h1>
      </header>

      {activeConsent && (
        <Card title="Data sharing consent" className="mb-4">
          <p className="text-body-sm text-text-secondary">
            You've given consent for Lloyds to retrieve your credit score from Experian.
          </p>
          <div className="mt-4">
            <div className="flex justify-between py-2.5 border-b border-border text-body-sm">
              <span className="text-text-secondary">Provider</span>
              <span className="font-bold">Experian</span>
            </div>
            <div className="flex justify-between py-2.5 border-b border-border text-body-sm">
              <span className="text-text-secondary">Consent given</span>
              <span className="font-bold">{new Date(activeConsent.grantedAt).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' })}</span>
            </div>
            <div className="flex justify-between py-2.5 border-b border-border text-body-sm">
              <span className="text-text-secondary">Purpose</span>
              <span className="font-bold">Credit coaching only</span>
            </div>
            <div className="flex justify-between py-2.5 text-body-sm">
              <span className="text-text-secondary">Status</span>
              <Badge variant="positive">Active</Badge>
            </div>
          </div>
        </Card>
      )}

      {activeConsent && (
        <Card title="Withdraw consent" className="border-status-negative border-[1.5px]">
          <p className="text-body-sm text-text-secondary">
            If you withdraw consent, we'll stop retrieving your score. Your existing score history will be retained for 6 years per regulatory requirements.
          </p>
          <Button
            variant="destructive"
            className="w-full mt-4"
            isLoading={withdrawConsent.isPending}
            onClick={() => withdrawConsent.mutate({ consentId: activeConsent.consentId, reason: 'customer_request' })}
            aria-label="Withdraw consent"
          >
            Withdraw Consent
          </Button>
        </Card>
      )}

      {withdrawConsent.isSuccess && (
        <Alert variant="success" title="Consent withdrawn" className="mt-4">
          Your consent has been withdrawn. We will no longer retrieve your credit score.
        </Alert>
      )}
    </main>
  );
}

export default SettingsPage;
