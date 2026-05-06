import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDeleteData } from '../api/compliance';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';

export default function DeleteDataPage() {
  const [confirmed, setConfirmed] = useState(false);
  const deleteData = useDeleteData();
  const navigate = useNavigate();

  const handleDelete = () => {
    deleteData.mutate(undefined, {
      onSuccess: () => navigate('/credit-coach/consent'),
    });
  };

  return (
    <div className="p-4 max-w-md mx-auto">
      <div className="p-4 bg-status-negative-bg border border-red-200 rounded-lg mb-4 text-sm text-[#922B21] leading-relaxed" role="alert">
        <strong>⚠️ This action cannot be undone.</strong> Deleting your data will remove your score history, improvement plans, and simulation history.
      </div>

      <Card title="What will be deleted">
        <div className="text-sm leading-[2]">
          ✓ Score history (all months)<br />
          ✓ Improvement action plans<br />
          ✓ Simulation history<br />
          ✓ Alert preferences<br />
          ✓ Offer interaction history
        </div>
      </Card>

      <Card title="What we must retain" className="mt-4">
        <p className="text-caption text-text-secondary mt-1">By law, we retain certain records:</p>
        <div className="text-sm leading-[2] text-text-secondary mt-3">
          • Consent records (regulatory audit trail)<br />
          • Offer acceptance records (CCA — 6 years)<br />
          • CRA API call logs (compliance)
        </div>
        <p className="text-xs text-text-muted mt-2">
          Retained per FCA SYSC 9.1.1R and CCA record-keeping obligations.
        </p>
      </Card>

      <label className="flex gap-2 items-start mt-5 cursor-pointer">
        <input
          type="checkbox"
          checked={confirmed}
          onChange={(e) => setConfirmed(e.target.checked)}
          className="w-5 h-5 accent-status-negative mt-0.5 shrink-0"
          aria-label="I understand this will permanently delete my credit coaching data"
        />
        <span className="text-sm">
          I understand this will permanently delete my credit coaching data and opt me out of the Credit Coach service
        </span>
      </label>

      <Button
        variant="destructive"
        className="mt-5 w-full"
        disabled={!confirmed}
        onClick={handleDelete}
        isLoading={deleteData.isPending}
        aria-label="Delete my data and opt out"
      >
        Delete my data & opt out
      </Button>
      <Button variant="secondary" className="mt-3 w-full" onClick={() => navigate(-1)} aria-label="Cancel">
        Cancel
      </Button>
    </div>
  );
}
