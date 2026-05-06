import { useNavigate, useLocation } from 'react-router-dom';
import { type ReactNode } from 'react';

const navItems = [
  { path: '/credit-coach/dashboard', icon: '🏠', label: 'Home' },
  { path: '/credit-coach/plan', icon: '📋', label: 'Plan' },
  { path: '/credit-coach/simulator', icon: '🔮', label: 'What If' },
  { path: '/credit-coach/offers', icon: '💰', label: 'Offers' },
  { path: '/credit-coach/alerts', icon: '🔔', label: 'Alerts' },
];

export function PageShell({ title, children, showBack = true }: { title: string; children: ReactNode; showBack?: boolean }) {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <div style={{ background: '#F5F5F5', minHeight: '100vh', maxWidth: 430, margin: '0 auto', display: 'flex', flexDirection: 'column', fontFamily: "'GT Ultra Standard',-apple-system,BlinkMacSystemFont,'Segoe UI',Arial,sans-serif", color: '#1A1A1A', fontSize: 16, lineHeight: 1.6, position: 'relative' }}>
      {/* Top Bar */}
      <div style={{ height: 56, background: '#fff', borderBottom: '1px solid #E0E0E0', display: 'flex', alignItems: 'center', padding: '0 16px', position: 'sticky', top: 0, zIndex: 10 }}>
        {showBack ? <span style={{ width: 32, cursor: 'pointer', color: '#595959', fontSize: 20 }} onClick={() => navigate(-1)}>←</span> : <span style={{ width: 32 }}></span>}
        <span style={{ flex: 1, fontSize: 17, fontWeight: 700, textAlign: 'center' }}>{title}</span>
        <span style={{ width: 32 }}></span>
      </div>
      {/* Content */}
      <div style={{ flex: 1, padding: 16, paddingBottom: 76, overflowY: 'auto' }}>
        {children}
      </div>
      {/* Bottom Nav */}
      <div style={{ position: 'fixed', bottom: 0, left: '50%', transform: 'translateX(-50%)', width: '100%', maxWidth: 430, height: 60, background: '#fff', borderTop: '1px solid #E0E0E0', display: 'flex', alignItems: 'center', zIndex: 10 }}>
        {navItems.map(item => {
          const active = location.pathname.startsWith(item.path);
          return (
            <a
              key={item.path}
              onClick={() => navigate(item.path)}
              style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4, fontSize: 11, color: active ? '#006A4D' : '#888', fontWeight: active ? 700 : 400, cursor: 'pointer', textDecoration: 'none', minHeight: 48, justifyContent: 'center' }}
            >
              <span style={{ fontSize: 18 }}>{item.icon}</span>
              <span>{item.label}</span>
            </a>
          );
        })}
      </div>
    </div>
  );
}
