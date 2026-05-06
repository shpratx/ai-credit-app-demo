import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { FactorList } from '../components/FactorList';
import type { ScoreFactor } from '../api/score';

const mockFactors: ScoreFactor[] = [
  {
    factorId: '1',
    category: 'payment_history',
    impact: 'high',
    direction: 'positive',
    title: 'Payment history is strong',
    description: 'No missed payments in 6 years',
    weightingPercent: 35,
  },
  {
    factorId: '2',
    category: 'utilisation',
    impact: 'high',
    direction: 'negative',
    title: 'Credit utilisation is 62%',
    description: 'Above recommended 30%',
    weightingPercent: 30,
  },
  {
    factorId: '3',
    category: 'credit_age',
    impact: 'medium',
    direction: 'positive',
    title: 'Long credit history',
    description: 'Oldest account is 8 years',
    weightingPercent: 15,
  },
];

describe('FactorList', () => {
  it('renders all factor titles', () => {
    render(<FactorList factors={mockFactors} />);
    expect(screen.getByText('Payment history is strong')).toBeInTheDocument();
    expect(screen.getByText('Credit utilisation is 62%')).toBeInTheDocument();
    expect(screen.getByText('Long credit history')).toBeInTheDocument();
  });

  it('renders positive factors with +Impact badge', () => {
    render(<FactorList factors={mockFactors} />);
    expect(screen.getByText('+High')).toBeInTheDocument();
    expect(screen.getByText('+Medium')).toBeInTheDocument();
  });

  it('renders negative factors with −Impact badge', () => {
    render(<FactorList factors={mockFactors} />);
    expect(screen.getByText('−High')).toBeInTheDocument();
  });

  it('renders correct icon for positive factors', () => {
    const { container } = render(<FactorList factors={[mockFactors[0]!]} />);
    const icon = container.querySelector('.bg-status-positive-bg');
    expect(icon).toBeInTheDocument();
    expect(icon).toHaveTextContent('✓');
  });

  it('renders correct icon for negative factors', () => {
    const { container } = render(<FactorList factors={[mockFactors[1]!]} />);
    const icon = container.querySelector('.bg-status-negative-bg');
    expect(icon).toBeInTheDocument();
    expect(icon).toHaveTextContent('!');
  });

  it('has accessible container label', () => {
    render(<FactorList factors={mockFactors} />);
    expect(screen.getByLabelText('Score factors')).toBeInTheDocument();
  });
});
