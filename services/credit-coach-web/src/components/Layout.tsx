import { Link, useLocation } from 'react-router-dom';

const navItems = [
  { path: '/credit-coach/dashboard', icon: '📊', label: 'Score' },
  { path: '/credit-coach/plan', icon: '📋', label: 'Plan' },
  { path: '/credit-coach/simulator', icon: '🔮', label: 'What If' },
  { path: '/credit-coach/offers', icon: '💰', label: 'Offers' },
  { path: '/credit-coach/alerts', icon: '🔔', label: 'Alerts' },
];

export function Layout({ children }: { children: React.ReactNode }) {
  const location = useLocation();

  return (
    <div className="min-h-screen bg-background max-w-[430px] mx-auto relative">
      <div className="pb-[70px]">
        {children}
      </div>
      <nav className="fixed bottom-0 left-1/2 -translate-x-1/2 w-full max-w-[430px] h-[60px] bg-surface border-t border-border flex items-center z-10" aria-label="Main navigation">
        {navItems.map(item => {
          const active = location.pathname.startsWith(item.path);
          return (
            <Link
              key={item.path}
              to={item.path}
              className={`flex-1 flex flex-col items-center justify-center gap-1 text-[11px] no-underline min-h-[48px] ${active ? 'text-lloyds-green font-bold' : 'text-text-muted'}`}
              aria-current={active ? 'page' : undefined}
            >
              <span className="text-lg">{item.icon}</span>
              <span>{item.label}</span>
            </Link>
          );
        })}
      </nav>
    </div>
  );
}
