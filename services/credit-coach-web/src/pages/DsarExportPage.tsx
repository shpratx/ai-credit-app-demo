import { PageShell } from '../components/PageShell';
import { useState } from 'react';
import { useExportDsar } from '../api/compliance';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Alert } from '../components/ui/Alert';

export default function DsarExportPage() {
  const exportDsar = useExportDsar();
  const [requested, setRequested] = useState(false);

  const handleExport = () => {
    exportDsar.mutate(undefined, { onSuccess: () => setRequested(true) });
  };

  if (requested) {
    return (
      <div className="p-4 max-w-md mx-auto text-center py-12">
        <div className="w-16 h-16 rounded-full bg-status-positive-bg flex items-center justify-center text-[28px] mx-auto mb-4">✓</div>
        <h1 className="text-xl font-bold">Export requested</h1>
        <p className="text-sm text-text-secondary mt-3 leading-relaxed max-w-[280px] mx-auto">
          Your data export is being prepared. It will be available within 24 hours (usually minutes). We'll notify you when it's ready.
        </p>
      </div>
    );
  }

  return (
    <div className="p-4 max-w-md mx-auto">
      <Card title="Download your credit coaching data" subtitle="Under UK GDPR (Article 15), you can request a copy of all data we hold about you in the Credit Coach.">
        <div className="text-sm leading-[2] mt-4">
          <strong>Your export will include:</strong><br />
          • Score history (all months)<br />
          • Score factors at each refresh<br />
          • Improvement plans and actions<br />
          • Simulation history<br />
          • Offers presented and your responses<br />
          • Alert history<br />
          • Consent records
        </div>

        <div className="mt-4 p-3 bg-status-info-bg rounded-lg text-caption text-[#0D47A1]">
          <strong>Format:</strong> JSON (machine-readable) + PDF summary<br />
          <strong>Delivery:</strong> Available within 24 hours (usually minutes)<br />
          <strong>Legal deadline:</strong> 1 calendar month per GDPR Art. 15
        </div>

        <Button className="mt-5 w-full" onClick={handleExport} isLoading={exportDsar.isPending} aria-label="Request data export">
          Request data export
        </Button>
      </Card>

      {exportDsar.error && (
        <Alert variant="error">Couldn't submit your export request. Please try again.</Alert>
      )}
    </div>
  );
}
