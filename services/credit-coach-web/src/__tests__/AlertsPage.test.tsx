import { render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import AlertsPage from '../pages/AlertsPage';

vi.mock('../api/alerts', () => ({
  useGetAlerts: vi.fn(),
  useDismissAlert: vi.fn(() => ({ mutate: vi.fn() })),
}));

import { useGetAlerts } from '../api/alerts';

const mockAlerts = [
  { alertId: '1', type: 'utilisation' as const, severity: 'medium' as const, title: '⚠️ Utilisation approaching 50%', message: 'Your Barclays credit card utilisation is now 48%.', timestamp: 'Today, 09:15', dismissed: false },
  { alertId: '2', type: 'payment' as const, severity: 'high' as const, title: '🔴 Payment due in 3 days', message: 'Your Lloyds loan payment of £350 is due on 8 May.', timestamp: 'Yesterday, 14:30', dismissed: false },
  { alertId: '3', type: 'eligibility' as const, severity: 'low' as const, title: '🎉 New product available!', message: 'Your score improved to 750.', timestamp: '2 May 2026', dismissed: false },
];

function renderPage() {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <AlertsPage />
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

describe('AlertsPage', () => {
  it('renders alert feed', () => {
    vi.mocked(useGetAlerts).mockReturnValue({ data: mockAlerts, isLoading: false, error: null } as unknown as ReturnType<typeof useGetAlerts>);
    renderPage();
    expect(screen.getByText(/utilisation approaching 50%/i)).toBeInTheDocument();
    expect(screen.getByText(/payment due in 3 days/i)).toBeInTheDocument();
    expect(screen.getByText(/new product available/i)).toBeInTheDocument();
  });

  it('renders severity badges', () => {
    vi.mocked(useGetAlerts).mockReturnValue({ data: mockAlerts, isLoading: false, error: null } as unknown as ReturnType<typeof useGetAlerts>);
    renderPage();
    expect(screen.getByText('Action needed')).toBeInTheDocument();
    expect(screen.getByText('High impact')).toBeInTheDocument();
    expect(screen.getByText('Good news')).toBeInTheDocument();
  });

  it('renders loading skeleton', () => {
    vi.mocked(useGetAlerts).mockReturnValue({ data: undefined, isLoading: true, error: null } as ReturnType<typeof useGetAlerts>);
    renderPage();
    expect(screen.getByLabelText(/loading alerts/i)).toBeInTheDocument();
  });

  it('shows empty state when no alerts', () => {
    vi.mocked(useGetAlerts).mockReturnValue({ data: [], isLoading: false, error: null } as unknown as ReturnType<typeof useGetAlerts>);
    renderPage();
    expect(screen.getByText(/no alerts right now/i)).toBeInTheDocument();
  });
});
