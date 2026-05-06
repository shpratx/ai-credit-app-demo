import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import ConsentPage from '../pages/ConsentPage';

vi.mock('../api/consent', () => ({
  useGrantConsent: () => ({ mutate: vi.fn(), isPending: false }),
}));

function renderPage() {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <ConsentPage />
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

describe('ConsentPage', () => {
  it('renders all consent checkboxes', () => {
    renderPage();
    expect(screen.getAllByRole('checkbox')).toHaveLength(3);
  });

  it('renders submit button disabled initially', () => {
    renderPage();
    expect(screen.getByRole('button', { name: /agree and check my score/i })).toBeDisabled();
  });

  it('enables submit button when all checkboxes are checked', async () => {
    const user = userEvent.setup();
    renderPage();

    const checkboxes = screen.getAllByRole('checkbox');
    for (const cb of checkboxes) {
      await user.click(cb);
    }

    expect(screen.getByRole('button', { name: /agree and check my score/i })).toBeEnabled();
  });

  it('keeps submit disabled when only some checkboxes are checked', async () => {
    const user = userEvent.setup();
    renderPage();

    const checkboxes = screen.getAllByRole('checkbox');
    await user.click(checkboxes[0]!);
    await user.click(checkboxes[1]!);

    expect(screen.getByRole('button', { name: /agree and check my score/i })).toBeDisabled();
  });
});
