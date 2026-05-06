import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { TrendChart } from '../components/TrendChart';

const mockPoints = [
  { date: '2025-06-01', score: 698 },
  { date: '2025-07-01', score: 705 },
  { date: '2025-08-01', score: 710 },
  { date: '2025-09-01', score: 715 },
  { date: '2025-10-01', score: 720 },
  { date: '2025-11-01', score: 725 },
  { date: '2025-12-01', score: 730 },
  { date: '2026-01-01', score: 732 },
  { date: '2026-02-01', score: 735 },
  { date: '2026-03-01', score: 738 },
  { date: '2026-04-01', score: 740 },
  { date: '2026-05-01', score: 742 },
];

const mockAnnotations = [
  { date: '2026-03-01', title: 'Credit card balance reduced', description: 'Paid down £1,200', type: 'event' as const },
  { date: '2026-01-01', title: 'Action completed', description: 'Set up Direct Debit', type: 'action' as const },
];

describe('TrendChart', () => {
  it('renders with accessible text alternative', () => {
    render(<TrendChart points={mockPoints} annotations={mockAnnotations} />);
    expect(screen.getByRole('img', { name: /score trend/i })).toBeInTheDocument();
  });

  it('renders SVG with polyline', () => {
    const { container } = render(<TrendChart points={mockPoints} annotations={mockAnnotations} />);
    expect(container.querySelector('polyline')).toBeInTheDocument();
  });

  it('renders annotation circles for annotated dates', () => {
    const { container } = render(<TrendChart points={mockPoints} annotations={mockAnnotations} />);
    const circles = container.querySelectorAll('circle');
    // Should have circles for annotated points + last point
    const visibleCircles = Array.from(circles).filter((c) => Number(c.getAttribute('r')) > 0);
    expect(visibleCircles.length).toBeGreaterThanOrEqual(3);
  });

  it('renders month labels', () => {
    render(<TrendChart points={mockPoints} annotations={mockAnnotations} />);
    expect(screen.getByText('Jun')).toBeInTheDocument();
    expect(screen.getByText('May')).toBeInTheDocument();
  });

  it('returns null for insufficient data', () => {
    const { container } = render(<TrendChart points={[{ date: '2026-05-01', score: 742 }]} annotations={[]} />);
    expect(container.firstChild).toBeNull();
  });
});
