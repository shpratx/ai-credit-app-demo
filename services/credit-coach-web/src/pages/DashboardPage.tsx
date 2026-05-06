import { useNavigate } from 'react-router-dom';
import { useGetScore, useGetFactors, useGetChangeExplanation } from '../api/score';

function DashboardPage() {
  const navigate = useNavigate();
  const { data: scoreData, isLoading } = useGetScore();
  const { data: factorsData } = useGetFactors();
  const { data: changeData } = useGetChangeExplanation();

  const score = scoreData;
  const factors = factorsData?.factors ?? [];
  const change = changeData;

  const s = {
    shell: { background: '#F5F5F5', minHeight: '100vh', display: 'flex', flexDirection: 'column' as const, fontFamily: "'GT Ultra Standard',-apple-system,BlinkMacSystemFont,'Segoe UI',Arial,sans-serif" },
    topBar: { height: 56, background: '#fff', borderBottom: '1px solid #E0E0E0', display: 'flex', alignItems: 'center', padding: '0 16px' },
    title: { flex: 1, fontSize: 17, fontWeight: 700, textAlign: 'center' as const },
    content: { flex: 1, padding: 16, paddingBottom: 80 },
    scoreHero: { background: '#006A4D', borderRadius: 16, padding: 24, color: '#fff', textAlign: 'center' as const, marginBottom: 16 },
    scoreValue: { fontSize: 48, fontWeight: 700, letterSpacing: '-0.02em' },
    card: { background: '#fff', border: '1px solid #E0E0E0', borderRadius: 12, padding: 20, marginBottom: 16, boxShadow: '0 1px 4px rgba(0,0,0,0.06)' },
    factorRow: { display: 'flex', alignItems: 'center', gap: 12, padding: '14px 0', borderBottom: '1px solid #E0E0E0' },
    factorIcon: (pos: boolean) => ({ width: 32, height: 32, borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 14, flexShrink: 0, background: pos ? '#E6F2EE' : '#FDECEC', color: pos ? '#006A4D' : '#C0392B' }),
    badge: (pos: boolean) => ({ display: 'inline-flex', padding: '3px 10px', borderRadius: 20, fontSize: 12, fontWeight: 700, background: pos ? '#E6F2EE' : '#FDECEC', color: pos ? '#006A4D' : '#C0392B' }),
    kv: { display: 'flex', justifyContent: 'space-between', padding: '10px 0', borderBottom: '1px solid #E0E0E0', fontSize: 14 },
    btnPrimary: { flex: 1, height: 44, background: '#006A4D', color: '#fff', fontSize: 14, fontWeight: 700, border: 'none', borderRadius: 8, cursor: 'pointer' },
    btnSecondary: { flex: 1, height: 44, background: 'transparent', color: '#006A4D', fontSize: 14, fontWeight: 700, border: '2px solid #006A4D', borderRadius: 8, cursor: 'pointer' },
    bottomNav: { position: 'fixed' as const, bottom: 0, left: '50%', transform: 'translateX(-50%)', width: '100%', maxWidth: 430, height: 60, background: '#fff', borderTop: '1px solid #E0E0E0', display: 'flex', alignItems: 'center', zIndex: 10 },
    navItem: { flex: 1, display: 'flex', flexDirection: 'column' as const, alignItems: 'center', gap: 4, fontSize: 11, color: '#888', textDecoration: 'none', cursor: 'pointer' },
    navActive: { flex: 1, display: 'flex', flexDirection: 'column' as const, alignItems: 'center', gap: 4, fontSize: 11, color: '#006A4D', fontWeight: 700, cursor: 'pointer' },
  };

  if (isLoading) {
    return (
      <div style={s.shell}>
        <div style={s.topBar}><span style={{ width: 24 }}>←</span><span style={s.title}>Credit Score</span><span style={{ width: 24 }}></span></div>
        <div style={s.content}>
          <div style={{ background: 'linear-gradient(90deg,#f0f0f0 25%,#e8e8e8 50%,#f0f0f0 75%)', height: 200, borderRadius: 16, marginBottom: 16 }}></div>
          <div style={s.card}><div style={{ background: '#f0f0f0', height: 20, width: '60%', borderRadius: 6, marginBottom: 16 }}></div><div style={{ background: '#f0f0f0', height: 48, borderRadius: 6, marginBottom: 12 }}></div><div style={{ background: '#f0f0f0', height: 48, borderRadius: 6 }}></div></div>
          <p style={{ fontSize: 13, color: '#888', textAlign: 'center' }}>Retrieving your credit score...</p>
        </div>
      </div>
    );
  }

  return (
    <div style={s.shell}>
      <div style={s.topBar}>
        <span style={{ width: 24, cursor: 'pointer' }} onClick={() => navigate('/')}>←</span>
        <span style={s.title}>Credit Score</span>
        <span style={{ width: 24, cursor: 'pointer' }}>⟳</span>
      </div>
      <div style={s.content}>
        {/* Score Hero */}
        <div style={s.scoreHero} aria-label={`Credit score ${score?.score} out of ${score?.maxScore}`}>
          <div style={{ fontSize: 13, opacity: 0.75 }}>Experian · Updated 3 May 2026</div>
          <div style={s.scoreValue}>{score?.score ?? '—'}</div>
          <div style={{ fontSize: 14, opacity: 0.85, marginTop: 4 }}>{score?.bandLabel}</div>
          <div style={{ fontSize: 13, opacity: 0.75, marginTop: 8 }}>
            {score?.changeDirection === 'up' ? '↑' : score?.changeDirection === 'down' ? '↓' : '—'} {Math.abs(score?.change ?? 0)} points since last month
          </div>
          <div style={{ marginTop: 16, height: 8, background: 'rgba(255,255,255,0.25)', borderRadius: 100, overflow: 'hidden' }}>
            <div style={{ height: '100%', width: `${((score?.score ?? 0) / (score?.maxScore ?? 1250)) * 100}%`, background: '#fff', borderRadius: 100 }}></div>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 11, opacity: 0.6, marginTop: 4 }}>
            <span>0</span><span>Poor</span><span>Fair</span><span>Good</span><span>Excellent</span><span>1250</span>
          </div>
        </div>

        {/* Factors */}
        <div style={s.card}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
            <span style={{ fontSize: 17, fontWeight: 700 }}>What's affecting your score</span>
            <a onClick={() => navigate('/credit-coach/factors')} style={{ fontSize: 13, color: '#006A4D', fontWeight: 700, cursor: 'pointer' }}>See all →</a>
          </div>
          {factors.slice(0, 4).map((f, i) => (
            <div key={f.factorId} style={{ ...s.factorRow, borderBottom: i < 3 ? '1px solid #E0E0E0' : 'none' }}>
              <div style={s.factorIcon(f.direction === 'positive')}>{f.direction === 'positive' ? '✓' : '!'}</div>
              <div style={{ flex: 1, fontSize: 14 }}>{f.title}</div>
              <span style={s.badge(f.direction === 'positive')}>{f.direction === 'positive' ? '+' : '−'}{f.impact === 'high' ? 'High' : f.impact === 'medium' ? 'Med' : 'Low'}</span>
            </div>
          ))}
        </div>

        {/* Change Explanation */}
        {change && (
          <div style={s.card}>
            <div style={{ fontSize: 17, fontWeight: 700 }}>Why your score changed</div>
            <p style={{ fontSize: 14, color: '#595959', marginTop: 8 }}>Your score increased by {change.totalChange} points this month:</p>
            {change.contributors?.map((c: any, i: number) => (
              <div key={i} style={{ ...s.kv, borderBottom: i < (change.contributors?.length ?? 0) - 1 ? '1px solid #E0E0E0' : 'none' }}>
                <span style={{ color: '#595959' }}>{c.factor}</span>
                <span style={{ fontWeight: 700, color: c.pointImpact > 0 ? '#006A4D' : '#C0392B' }}>{c.pointImpact > 0 ? '+' : ''}{c.pointImpact} pts</span>
              </div>
            ))}
          </div>
        )}

        {/* Actions */}
        <div style={{ display: 'flex', gap: 12 }}>
          <button style={s.btnSecondary} onClick={() => navigate('/credit-coach/history')}>View History</button>
          <button style={s.btnPrimary} onClick={() => navigate('/credit-coach/plan')}>Improve Score</button>
        </div>
      </div>

      {/* Bottom Nav */}
      <div style={s.bottomNav}>
        <a style={s.navItem} onClick={() => navigate('/credit-coach/dashboard')}>🏠 Home</a>
        <a style={s.navItem} onClick={() => navigate('/credit-coach/plan')}>📋 Plan</a>
        <a style={s.navActive}>📊 Credit</a>
        <a style={s.navItem} onClick={() => navigate('/credit-coach/simulator')}>🔮 What If</a>
        <a style={s.navItem} onClick={() => navigate('/credit-coach/alerts')}>🔔 Alerts</a>
      </div>
    </div>
  );
}

export default DashboardPage;
