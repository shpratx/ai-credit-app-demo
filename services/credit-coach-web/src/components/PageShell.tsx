import { useNavigate } from 'react-router-dom';
import { type ReactNode } from 'react';
import { BottomNav } from './BottomNav';

export function PageShell({ title, children, showBack = true }: { title: string; children: ReactNode; showBack?: boolean }) {
  const navigate = useNavigate();

  return (
    <div style={{ background: '#F5F5F5', minHeight: '100vh', maxWidth: 430, margin: '0 auto', display: 'flex', flexDirection: 'column', fontFamily: "'GT Ultra Standard',-apple-system,BlinkMacSystemFont,'Segoe UI',Arial,sans-serif", color: '#1A1A1A', fontSize: 16, lineHeight: 1.6, position: 'relative' }}>
      {/* Top Bar */}
      <div style={{ height: 56, background: '#006A4D', borderBottom: '1px solid #005238', display: 'flex', alignItems: 'center', padding: '0 16px', position: 'sticky', top: 0, zIndex: 10 }}>
        {showBack ? <span style={{ width: 32, cursor: 'pointer', color: '#fff', fontSize: 20 }} onClick={() => navigate(-1)}>←</span> : <span style={{ width: 32 }}></span>}
        <span style={{ flex: 1, fontSize: 17, fontWeight: 700, textAlign: 'center', color: '#fff' }}>{title}</span>
        <span style={{ width: 32 }}></span>
      </div>
      {/* Content */}
      <div style={{ flex: 1, padding: 16, paddingBottom: 76, overflowY: 'auto' }}>
        {children}
      </div>
      <BottomNav />
    </div>
  );
}
