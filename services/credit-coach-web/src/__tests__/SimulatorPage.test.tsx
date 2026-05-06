import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import SimulatorPage from '../pages/SimulatorPage';

const mockMutate = vi.fn();
const mockReset = vi.fn();

vi.mock('../api/simulator', () => ({
  useRunSimulation: vi.fn(() => ({
    mutate: mockMutate,
    reset: mockReset,
    data: null,
    error: null,
    isPending: false,
  })),
}));

import { useRunSimulation } from '../api/simulator';

function renderPage() {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <SimulatorPage />
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

describe('SimulatorPage', () => {
  it('renders scenario chips', () => {
    renderPage();
    expect(screen.getByRole('radio', { name: /pay down credit card debt/i })).toBeInTheDocument();
    expect(screen.getByRole('radio', { name: /close an old account/i })).toBeInTheDocument();
    expect(screen.getByRole('radio', { name: /miss a payment/i })).toBeInTheDocument();
  });

  it('shows amount input when scenario is selected', async () => {
    renderPage();
    await userEvent.click(screen.getByRole('radio', { name: /pay down credit card debt/i }));
    expect(screen.getByLabelText(/amount to simulate/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /see the impact/i })).toBeInTheDocument();
  });

  it('displays simulation results', () => {
    vi.mocked(useRunSimulation).mockReturnValue({
      mutate: mockMutate,
      reset: mockReset,
      data: {
        currentScore: 742,
        estimatedScore: 767,
        change: 25,
        confidence: 'high' as const,
        details: { utilisationBefore: '64%', utilisationAfter: '40%', bandChange: 'Good → Very Good', timeframe: '~1–2 months' },
      },
      error: null,
      isPending: false,
    } as unknown as ReturnType<typeof useRunSimulation>);

    renderPage();
    expect(screen.getByText('767')).toBeInTheDocument();
    expect(screen.getByText('+25 points')).toBeInTheDocument();
  });

  it('shows disclaimer text', () => {
    renderPage();
    expect(screen.getByText(/simulations are estimates/i)).toBeInTheDocument();
  });
});
