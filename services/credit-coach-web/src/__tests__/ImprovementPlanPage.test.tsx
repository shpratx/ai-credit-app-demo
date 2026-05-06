import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import ImprovementPlanPage from '../pages/ImprovementPlanPage';

vi.mock('../api/plan', () => ({
  useGetPlan: vi.fn(),
  useRefreshPlan: vi.fn(() => ({ mutate: vi.fn(), isPending: false })),
  useCompleteAction: vi.fn(() => ({ mutate: vi.fn() })),
  useGetMilestones: vi.fn(() => ({ data: undefined })),
}));

import { useGetPlan, useCompleteAction } from '../api/plan';

const mockPlan = {
  customerId: '123',
  totalPotentialPoints: 45,
  actionsTotal: 3,
  actionsCompleted: 1,
  confidence: 'high' as const,
  updatedAt: '2026-05-03T10:00:00Z',
  actions: [
    { actionId: '1', rank: 1, title: 'Reduce credit card utilisation', description: 'Pay down £1,200', explanation: 'Utilisation above 30% hurts your score', impactPoints: 25, timeframe: '2 months', completed: false, completedAt: null },
    { actionId: '2', rank: 2, title: 'Avoid new credit applications', description: 'Wait 6 months', explanation: 'Hard searches reduce score', impactPoints: 10, timeframe: '6 months', completed: false, completedAt: null },
    { actionId: '3', rank: 0, title: 'Set up Direct Debit', description: 'Ensures on-time payments', explanation: '', impactPoints: 10, timeframe: '', completed: true, completedAt: '28 Apr' },
  ],
};

function renderPage() {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <ImprovementPlanPage />
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

describe('ImprovementPlanPage', () => {
  it('renders loading skeleton', () => {
    vi.mocked(useGetPlan).mockReturnValue({ data: undefined, isLoading: true, error: null } as ReturnType<typeof useGetPlan>);
    renderPage();
    expect(screen.getByLabelText(/loading improvement plan/i)).toBeInTheDocument();
  });

  it('renders actions when data is loaded', () => {
    vi.mocked(useGetPlan).mockReturnValue({ data: mockPlan, isLoading: false, error: null } as unknown as ReturnType<typeof useGetPlan>);
    renderPage();
    expect(screen.getByText('Reduce credit card utilisation')).toBeInTheDocument();
    expect(screen.getByText('Avoid new credit applications')).toBeInTheDocument();
    expect(screen.getByText('+45 points possible')).toBeInTheDocument();
  });

  it('shows progress bar with correct completion', () => {
    vi.mocked(useGetPlan).mockReturnValue({ data: mockPlan, isLoading: false, error: null } as unknown as ReturnType<typeof useGetPlan>);
    renderPage();
    expect(screen.getByText('1 of 3 completed')).toBeInTheDocument();
    expect(screen.getByRole('progressbar')).toBeInTheDocument();
  });

  it('calls completeAction when mark as done is clicked', async () => {
    const mutateFn = vi.fn();
    vi.mocked(useCompleteAction).mockReturnValue({ mutate: mutateFn } as unknown as ReturnType<typeof useCompleteAction>);
    vi.mocked(useGetPlan).mockReturnValue({ data: mockPlan, isLoading: false, error: null } as unknown as ReturnType<typeof useGetPlan>);
    renderPage();
    const buttons = screen.getAllByRole('button', { name: /mark .* as done/i });
    await userEvent.click(buttons[0]!);
    expect(mutateFn).toHaveBeenCalledWith('1');
  });
});
