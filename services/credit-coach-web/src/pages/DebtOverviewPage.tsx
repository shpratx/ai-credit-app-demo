import { PageShell } from '../components/PageShell';
import { useGetDebtOverview } from '../api/history';
import { DebtRow } from '../components/DebtRow';
import { Card } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Alert } from '../components/ui/Alert';

export default function DebtOverviewPage() {
  const { data, isLoading, error } = useGetDebtOverview();

  if (isLoading) {
    return (
      <div className="p-4 max-w-md mx-auto" aria-label="Loading debt overview">
        <div className="animate-pulse space-y-4">
          <div className="h-24 bg-gray-200 rounded-card" />
          <div className="h-48 bg-gray-200 rounded-card" />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-4 max-w-md mx-auto">
        <Alert variant="error">Couldn't load your debt overview. Please try again later.</Alert>
      </div>
    );
  }

  if (!data) return null;

  const dbrVariant = data.dbrBand === 'good' ? 'positive' : data.dbrBand === 'fair' ? 'pending' : 'negative';

  return (
    <div className="p-4 max-w-md mx-auto">
      {/* Summary */}
      <div className="bg-bg-app rounded-card p-4 mb-4">
        <div className="flex justify-between mb-4">
          <div>
            <p className="text-caption text-text-secondary">Total debt</p>
            <p className="text-2xl font-bold">£{data.totalDebt.toLocaleString()}</p>
          </div>
          <div className="text-right">
            <p className="text-caption text-text-secondary">Monthly payments</p>
            <p className="text-2xl font-bold">£{data.totalMonthlyPayments.toLocaleString()}</p>
          </div>
        </div>
        <div className="flex justify-between items-center py-2">
          <span className="text-sm text-text-secondary">Debt-to-income ratio</span>
          <span className="text-sm font-bold">
            {data.debtToIncomeRatio}% <Badge variant={dbrVariant}>{data.dbrBand}</Badge>
          </span>
        </div>
      </div>

      {/* Account list */}
      <Card title={`Accounts (${data.accounts.length})`} subtitle={`From ${data.source} · ${new Date(data.retrievedAt).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' })}`}>
        <div aria-label="Debt accounts">
          {data.accounts.map((account) => (
            <DebtRow
              key={account.accountId}
              lender={account.lender}
              type={account.type}
              balance={account.balance}
              monthlyPayment={account.monthlyPayment}
              utilisationPercent={account.utilisationPercent}
              isDisputed={account.isDisputed}
            />
          ))}
        </div>
      </Card>

      <div className="p-3 bg-status-info-bg rounded-lg text-caption text-[#0D47A1] leading-relaxed mt-4">
        <strong>About this data:</strong> Sourced from {data.source}. May not reflect very recent changes. If you believe any information is incorrect, contact {data.source} to dispute it.
      </div>
    </div>
  );
}
