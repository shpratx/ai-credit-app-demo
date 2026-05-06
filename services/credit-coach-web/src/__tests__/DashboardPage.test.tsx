import { render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import DashboardPage from '../pages/DashboardPage';

const mockScore = {
  customerId: '123',
  provider: 'EXPERIAN' as const,
  score: 742,
  maxScore: 1250,
  band: 'good' as const,
  bandLabel: 'Good (700–849)',
  previousScore: 727,
  change: 15,
  changeDirection: 'up' as const,
  retrievedAt: '2026-05-03T10:00:00Z',
  isStale: false,
};

const mockFactors = {
  customerId: '123',
  factors: [
    { factorId: '1', category: 'payment_history', impact: 'high' as const, direction: 'positive' as const, title: 'Payment history is strong', description: 'No missed payments', weightingPercent: 35 },
    { factorId: '2', category: 'utilisation', impact: 'high' as const, direction: 'negative' as const, title: 'Credit utilisation is 62%', description: 'Above recommended 30%', weightingPercent: 30 },
  ],
  positiveCount: 1,
  negativeCount: 1,
  retrievedAt: '2026-05-03T10:00:00Z',
};

const mockChange = {
  customerId: '123',
  previousScore: 727,
  currentScore: 742,
  totalChange: 15,
  changeDirection: 'up' as const,
  contributors: [{ factor: 'Credit card balance reduced', pointImpact: 12, description: '' }],
  periodStart: '2026-04-01',
  periodEnd: '2026-05-01',
};

vi.mock('../api/score', () => ({
  useGetScore: vi.fn(),
  useGetFactors: vi.fn(),
  useGetChangeExplanation: vi.fn(),
}));

import { useGetScore, useGetFactors, useGetChangeExplanation } from '../api/score';

function renderPage() {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <DashboardPage />
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

describe('DashboardPage', () => {
  it('renders loading skeleton', () => {
    vi.mocked(useGetScore).mockReturnValue({ data: undefined, isLoading: true, error: null } as ReturnType<typeof useGetScore>);
    vi.mocked(useGetFactors).mockReturnValue({ data: undefined } as ReturnType<typeof useGetFactors>);
    vi.mocked(useGetChangeExplanation).mockReturnValue({ data: undefined } as ReturnType<typeof useGetChangeExplanation>);

    renderPage();
    expect(screen.getByLabelText(/loading credit score/i)).toBeInTheDocument();
  });

  it('renders error state', () => {
    vi.mocked(useGetScore).mockReturnValue({ data: undefined, isLoading: false, error: new Error('fail') } as unknown as ReturnType<typeof useGetScore>);
    vi.mocked(useGetFactors).mockReturnValue({ data: undefined } as ReturnType<typeof useGetFactors>);
    vi.mocked(useGetChangeExplanation).mockReturnValue({ data: undefined } as ReturnType<typeof useGetChangeExplanation>);

    renderPage();
    expect(screen.getByRole('alert')).toHaveTextContent(/couldn't load your credit score/i);
  });

  it('renders score and factors when data is loaded', () => {
    vi.mocked(useGetScore).mockReturnValue({ data: mockScore, isLoading: false, error: null } as unknown as ReturnType<typeof useGetScore>);
    vi.mocked(useGetFactors).mockReturnValue({ data: mockFactors } as unknown as ReturnType<typeof useGetFactors>);
    vi.mocked(useGetChangeExplanation).mockReturnValue({ data: mockChange } as unknown as ReturnType<typeof useGetChangeExplanation>);

    renderPage();
    expect(screen.getByText('742')).toBeInTheDocument();
    expect(screen.getByText('Good (700–849)')).toBeInTheDocument();
    expect(screen.getByText('Payment history is strong')).toBeInTheDocument();
  });
});
