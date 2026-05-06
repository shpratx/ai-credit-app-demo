import { render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import OffersPage from '../pages/OffersPage';

vi.mock('../api/offers', () => ({
  useGetOffers: vi.fn(),
  useAcceptOffer: vi.fn(() => ({ mutate: vi.fn(), isPending: false })),
}));

import { useGetOffers } from '../api/offers';

const mockOffer = {
  offers: [
    {
      offerId: 'offer-1',
      productName: 'Lloyds Personal Loan',
      amount: 12000,
      apr: 8.9,
      term: 60,
      monthlyPayment: 258.43,
      totalPayable: 15505.80,
      totalChargeForCredit: 3505.80,
      isPreApproved: true,
      status: 'available' as const,
    },
  ],
  suppressed: false,
  suppressionReason: null,
};

function renderPage() {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <OffersPage />
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

describe('OffersPage', () => {
  it('renders offer card with amount and APR', () => {
    vi.mocked(useGetOffers).mockReturnValue({ data: mockOffer, isLoading: false, error: null } as unknown as ReturnType<typeof useGetOffers>);
    renderPage();
    expect(screen.getAllByText(/£12,000/).length).toBeGreaterThanOrEqual(1);
    expect(screen.getAllByText(/8.9% APR/).length).toBeGreaterThanOrEqual(1);
  });

  it('shows representative example', () => {
    vi.mocked(useGetOffers).mockReturnValue({ data: mockOffer, isLoading: false, error: null } as unknown as ReturnType<typeof useGetOffers>);
    renderPage();
    expect(screen.getByText(/Representative Example/)).toBeInTheDocument();
    expect(screen.getAllByText(/Total amount payable/).length).toBeGreaterThanOrEqual(1);
  });

  it('shows suppressed state with support links', () => {
    vi.mocked(useGetOffers).mockReturnValue({
      data: { offers: [], suppressed: true, suppressionReason: 'financial_distress' },
      isLoading: false,
      error: null,
    } as unknown as ReturnType<typeof useGetOffers>);
    renderPage();
    expect(screen.getByText(/we're here to help/i)).toBeInTheDocument();
    expect(screen.getByText(/stepchange/i)).toBeInTheDocument();
  });

  it('shows no offers state', () => {
    vi.mocked(useGetOffers).mockReturnValue({
      data: { offers: [], suppressed: false, suppressionReason: null },
      isLoading: false,
      error: null,
    } as unknown as ReturnType<typeof useGetOffers>);
    renderPage();
    expect(screen.getByText(/no offers right now/i)).toBeInTheDocument();
  });
});
