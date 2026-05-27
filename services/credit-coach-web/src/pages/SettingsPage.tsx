import { useState } from 'react';
import { useGetConsents, useWithdrawConsent } from '../api/consent';

function SettingsPage() {
  const { data: consents, isLoading } = useGetConsents('cust-1');
  const withdrawConsent = useWithdrawConsent();
  const [frequency, setFrequency] = useState('monthly');

  const activeConsent = consents?.find((c) => c.status === 'GRANTED');

  const s = {
    card: { background: '#fff', border: '1px solid #E0E0E0', borderRadius: 12, padding: 20, marginBottom: 16, boxShadow: '0 1px 4px rgba(0,0,0,0.06)' },
    cardTitle: { fontSize: 17, fontWeight: 700, color: '#1A1A1A' },
    desc: { fontSize: 14, color: '#595959', marginTop: 8, lineHeight: 1.5 },
    kvRow: { display: 'flex', justifyContent: 'space-between', padding: '10px 0', borderBottom: '1px solid #E0E0E0', fontSize: 14 },
    kvLabel: { color: '#595959' },
    kvValue: { fontWeight: 700, color: '#1A1A1A' },
    badge: { display: 'inline-block', padding: '3px 10px', borderRadius: 20, fontSize: 12, fontWeight: 700, background: '#E6F2EE', color: '#006A4D' },
    select: { width: '100%', height: 44, padding: '0 16px', border: '1.5px solid #ccc', borderRadius: 8, fontSize: 14, marginTop: 12 },
    btnDanger: { width: '100%', height: 44, background: 'transparent', color: '#C0392B', fontSize: 14, fontWeight: 700, border: '2px solid #C0392B', borderRadius: 8, cursor: 'pointer', marginTop: 16 },
    success: { background: '#E6F2EE', border: '1px solid #006A4D', borderRadius: 8, padding: 16, marginTop: 16, fontSize: 14, color: '#006A4D' },
  };

  if (isLoading) {
    return <div style={{ padding: 16 }}><div style={{ background: '#f0f0f0', height: 200, borderRadius: 12 }} /></div>;
  }

  return (
    <div>
      {activeConsent && (
        <div style={s.card}>
          <div style={s.cardTitle}>Data sharing consent</div>
          <p style={s.desc}>You've given consent for Lloyds to retrieve your credit score from Experian.</p>
          <div style={{ marginTop: 16 }}>
            <div style={s.kvRow}><span style={s.kvLabel}>Provider</span><span style={s.kvValue}>Experian</span></div>
            <div style={s.kvRow}><span style={s.kvLabel}>Consent given</span><span style={s.kvValue}>{new Date(activeConsent.grantedAt).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' })}</span></div>
            <div style={s.kvRow}><span style={s.kvLabel}>Purpose</span><span style={s.kvValue}>Credit coaching only</span></div>
            <div style={s.kvRow}><span style={s.kvLabel}>Search type</span><span style={s.kvValue}>Soft search (no footprint)</span></div>
            <div style={{ ...s.kvRow, borderBottom: 'none' }}><span style={s.kvLabel}>Status</span><span style={s.badge}>Active</span></div>
          </div>
        </div>
      )}

      <div style={s.card}>
        <div style={s.cardTitle}>Refresh frequency</div>
        <p style={s.desc}>How often we check your score with Experian.</p>
        <select style={s.select} value={frequency} onChange={e => setFrequency(e.target.value)}>
          <option value="monthly">Monthly (recommended)</option>
          <option value="fortnightly">Fortnightly</option>
          <option value="weekly">Weekly</option>
        </select>
      </div>

      {activeConsent && (
        <div style={{ ...s.card, borderColor: '#C0392B', borderWidth: '1.5px' }}>
          <div style={{ ...s.cardTitle, color: '#C0392B' }}>Withdraw consent</div>
          <p style={s.desc}>If you withdraw consent, we'll stop retrieving your score. Your existing score history will be retained for 6 years per regulatory requirements, unless you request deletion.</p>
          <button
            style={s.btnDanger}
            onClick={() => withdrawConsent.mutate({ consentId: activeConsent.consentId, reason: 'customer_request' })}
            disabled={withdrawConsent.isPending}
          >
            {withdrawConsent.isPending ? 'Withdrawing...' : 'Withdraw Consent'}
          </button>
        </div>
      )}

      {withdrawConsent.isSuccess && (
        <div style={s.success}>✓ Your consent has been withdrawn. We will no longer retrieve your credit score.</div>
      )}
    </div>
  );
}

export default SettingsPage;
