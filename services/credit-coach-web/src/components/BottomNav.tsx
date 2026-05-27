import { useNavigate, useLocation } from 'react-router-dom';

const navItems = [
  { path: '/credit-coach/dashboard', icon: '🏠', label: 'Home', matchAlso: [] as string[] },
  { path: '/credit-coach/history', icon: '📈', label: 'Credit', matchAlso: ['/credit-coach/dashboard'] },
  { path: '/credit-coach/plan', icon: '📋', label: 'Plan', matchAlso: [] as string[] },
  { path: '/credit-coach/factors', icon: '📊', label: 'Factors', matchAlso: [] as string[] },
  { path: '/credit-coach/chat', icon: '💬', label: 'Chat', matchAlso: [] as string[] },
  { path: '/credit-coach/settings/consent', icon: '⚙️', label: 'Settings', matchAlso: [] as string[] },
];

export function BottomNav() {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <div style={{ position: 'fixed', bottom: 0, left: '50%', transform: 'translateX(-50%)', width: '100%', maxWidth: 430, height: 60, background: '#fff', borderTop: '1px solid #E0E0E0', display: 'flex', alignItems: 'center', zIndex: 10 }}>
      {navItems.map(item => {
        const active = location.pathname === item.path || item.matchAlso.some(p => location.pathname === p);
        return (
          <a
            key={item.path}
            onClick={() => navigate(item.path)}
            style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2, fontSize: 10, color: active ? '#006A4D' : '#888', fontWeight: active ? 700 : 400, cursor: 'pointer', textDecoration: 'none', minHeight: 48, justifyContent: 'center' }}
          >
            <span style={{ fontSize: 18 }}>{item.icon}</span>
            <span>{item.label}</span>
          </a>
        );
      })}
    </div>
  );
}
