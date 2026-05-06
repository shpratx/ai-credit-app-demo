import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useGrantConsent } from '../api/consent';

function ConsentPage() {
  const navigate = useNavigate();
  const [checks, setChecks] = useState([false, false, false]);
  const grantConsent = useGrantConsent();
  const allChecked = checks.every(Boolean);

  const handleSubmit = () => {
    grantConsent.mutate(
      { craProvider: 'EXPERIAN', consentTextVersion: '1.0', consentTextHash: 'sha256-placeholder', channel: 'WEB', privacyNoticeAccepted: true },
      { onSuccess: () => navigate('/credit-coach/dashboard') },
    );
  };

  const s = {
    shell: { background: '#F5F5F5', minHeight: '100vh', display: 'flex', flexDirection: 'column' as const, fontFamily: "'GT Ultra Standard',-apple-system,BlinkMacSystemFont,'Segoe UI',Arial,sans-serif" },
    topBar: { height: 56, background: '#fff', borderBottom: '1px solid #E0E0E0', display: 'flex', alignItems: 'center', padding: '0 16px', gap: 12 },
    title: { flex: 1, fontSize: 17, fontWeight: 700, textAlign: 'center' as const },
    content: { flex: 1, padding: 16 },
    card: { background: '#fff', border: '1px solid #E0E0E0', borderRadius: 12, padding: 20, boxShadow: '0 1px 4px rgba(0,0,0,0.06)' },
    cardTitle: { fontSize: 17, fontWeight: 700, color: '#1A1A1A' },
    desc: { marginTop: 8, fontSize: 14, lineHeight: 1.5, color: '#595959' },
    alert: { display: 'flex', gap: 12, padding: 16, borderRadius: 10, fontSize: 14, border: '1px solid #90CAF9', background: '#E3F2FD', color: '#0D47A1', marginTop: 20, marginBottom: 16 },
    consentItem: { display: 'flex', alignItems: 'flex-start', gap: 12, padding: '12px 0', borderBottom: '1px solid #E0E0E0' },
    consentItemLast: { display: 'flex', alignItems: 'flex-start', gap: 12, padding: '12px 0' },
    checkbox: { width: 20, height: 20, accentColor: '#006A4D', marginTop: 2, flexShrink: 0, cursor: 'pointer' },
    consentText: { fontSize: 14, lineHeight: 1.5, cursor: 'pointer' },
    btnPrimary: { width: '100%', height: 52, background: allChecked ? '#006A4D' : '#ccc', color: '#fff', fontSize: 16, fontWeight: 700, border: 'none', borderRadius: 8, cursor: allChecked ? 'pointer' : 'not-allowed', marginTop: 24, display: 'flex', alignItems: 'center', justifyContent: 'center' },
    btnSecondary: { width: '100%', height: 52, background: 'transparent', color: '#006A4D', fontSize: 16, fontWeight: 700, border: '2px solid #006A4D', borderRadius: 8, cursor: 'pointer', marginTop: 12, display: 'flex', alignItems: 'center', justifyContent: 'center' },
    footer: { fontSize: 12, color: '#888', textAlign: 'center' as const, marginTop: 12 },
    bottomNav: { height: 60, background: '#fff', borderTop: '1px solid #E0E0E0', display: 'flex', alignItems: 'center' },
    navItem: { flex: 1, display: 'flex', flexDirection: 'column' as const, alignItems: 'center', gap: 4, fontSize: 11, color: '#888', textDecoration: 'none', cursor: 'pointer' },
    navActive: { flex: 1, display: 'flex', flexDirection: 'column' as const, alignItems: 'center', gap: 4, fontSize: 11, color: '#006A4D', fontWeight: 700, textDecoration: 'none', cursor: 'pointer' },
  };

  const labels = [
    'I consent to Lloyds retrieving my credit score from Experian for credit coaching purposes only',
    'I understand this is a soft search and will not appear on my credit file',
    'I have read the privacy notice explaining how my data will be used',
  ];

  return (
    <div style={s.shell}>
      <div style={s.topBar}>
        <span style={{ width: 24 }}></span>
        <span style={s.title}>Credit Coach</span>
        <span style={{ width: 24 }}></span>
      </div>
      <div style={s.content}>
        <div style={s.card}>
          <div style={s.cardTitle}>Check your credit score</div>
          <p style={s.desc}>We'll retrieve your credit score from Experian using a soft search — this won't affect your credit file.</p>

          <div style={s.alert}>
            <span>ℹ️</span>
            <div>
              <strong>What we'll access:</strong><br/>
              • Your credit score and band<br/>
              • Score factors (what's helping/hurting)<br/>
              • Used for coaching only — never marketing
            </div>
          </div>

          <div style={{ marginTop: 16 }}>
            {labels.map((label, i) => (
              <div key={i} style={i < 2 ? s.consentItem : s.consentItemLast}>
                <input
                  type="checkbox"
                  style={s.checkbox}
                  checked={checks[i]}
                  onChange={() => setChecks(prev => prev.map((v, j) => j === i ? !v : v))}
                  id={`consent-${i}`}
                  aria-label={label}
                />
                <label htmlFor={`consent-${i}`} style={s.consentText}>{label}</label>
              </div>
            ))}
          </div>

          <button style={s.btnPrimary} disabled={!allChecked} onClick={handleSubmit}>
            {grantConsent.isPending ? 'Loading...' : 'Agree & Check My Score'}
          </button>
          <button style={s.btnSecondary} onClick={() => navigate('/credit-coach/dashboard')}>Not now</button>
          <p style={s.footer}>You can withdraw consent at any time in Settings</p>
        </div>
      </div>
      <div style={s.bottomNav}>
        <a style={s.navItem} onClick={() => navigate('/credit-coach/dashboard')}>🏠 Home</a>
        <a style={s.navItem}>💳 Cards</a>
        <a style={s.navActive}>📊 Credit</a>
        <a style={s.navItem} onClick={() => navigate('/credit-coach/alerts')}>💬 Chat</a>
        <a style={s.navItem} onClick={() => navigate('/credit-coach/settings/consent')}>⚙️ More</a>
      </div>
    </div>
  );
}

export default ConsentPage;
