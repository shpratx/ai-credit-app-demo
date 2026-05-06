import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import DeleteDataPage from '../pages/DeleteDataPage';

const mockMutate = vi.fn();

vi.mock('../api/compliance', () => ({
  useDeleteData: vi.fn(() => ({ mutate: mockMutate, isPending: false })),
}));

function renderPage() {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <DeleteDataPage />
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

describe('DeleteDataPage', () => {
  it('renders warning and delete button is disabled by default', () => {
    renderPage();
    expect(screen.getByText(/this action cannot be undone/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /delete my data/i })).toBeDisabled();
  });

  it('enables delete button only after checkbox is checked', async () => {
    renderPage();
    const checkbox = screen.getByRole('checkbox');
    const deleteBtn = screen.getByRole('button', { name: /delete my data/i });

    expect(deleteBtn).toBeDisabled();
    await userEvent.click(checkbox);
    expect(deleteBtn).toBeEnabled();
  });

  it('calls deleteData when confirmed and button clicked', async () => {
    renderPage();
    await userEvent.click(screen.getByRole('checkbox'));
    await userEvent.click(screen.getByRole('button', { name: /delete my data/i }));
    expect(mockMutate).toHaveBeenCalled();
  });

  it('shows retention explanation', () => {
    renderPage();
    expect(screen.getByText(/what we must retain/i)).toBeInTheDocument();
    expect(screen.getByText(/consent records/i)).toBeInTheDocument();
  });
});
