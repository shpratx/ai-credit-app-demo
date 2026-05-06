import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { ScoreHero } from '../components/ScoreHero';
import type { ScoreData } from '../api/score';

const baseScore: ScoreData = {
  customerId: '123',
  provider: 'EXPERIAN',
  score: 742,
  maxScore: 1250,
  band: 'good',
  bandLabel: 'Good (700–849)',
  previousScore: 727,
  change: 15,
  changeDirection: 'up',
  retrievedAt: '2026-05-03T10:00:00Z',
  isStale: false,
};

describe('ScoreHero', () => {
  it('renders score value', () => {
    render(<ScoreHero score={baseScore} />);
    expect(screen.getByText('742')).toBeInTheDocument();
  });

  it('renders band label', () => {
    render(<ScoreHero score={baseScore} />);
    expect(screen.getByText('Good (700–849)')).toBeInTheDocument();
  });

  it('renders upward change direction', () => {
    render(<ScoreHero score={baseScore} />);
    expect(screen.getByText(/↑ 15 points since last month/)).toBeInTheDocument();
  });

  it('renders downward change direction', () => {
    render(<ScoreHero score={{ ...baseScore, change: -10, changeDirection: 'down' }} />);
    expect(screen.getByText(/↓ 10 points since last month/)).toBeInTheDocument();
  });

  it('renders unchanged state', () => {
    render(<ScoreHero score={{ ...baseScore, change: 0, changeDirection: 'unchanged' }} />);
    expect(screen.getByText(/No change since last month/)).toBeInTheDocument();
  });

  it('has accessible aria-label with score details', () => {
    render(<ScoreHero score={baseScore} />);
    expect(screen.getByRole('region')).toHaveAttribute(
      'aria-label',
      expect.stringContaining('742 out of 1250'),
    );
  });

  it('renders progress bar with correct aria attributes', () => {
    render(<ScoreHero score={baseScore} />);
    const progressbar = screen.getByRole('progressbar');
    expect(progressbar).toHaveAttribute('aria-valuenow', '742');
    expect(progressbar).toHaveAttribute('aria-valuemax', '1250');
  });
});
