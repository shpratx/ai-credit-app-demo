import { useQuery } from '@tanstack/react-query';
import { apiClient } from './client';

interface ScorePoint {
  date: string;
  score: number;
}

interface Annotation {
  date: string;
  title: string;
  description: string;
  type: 'action' | 'event' | 'milestone';
}

interface ScoreTrend {
  points: ScorePoint[];
  annotations: Annotation[];
  statistics: {
    current: number;
    highest: number;
    highestDate: string;
    lowest: number;
    lowestDate: string;
    average: number;
    trend: 'improving' | 'declining' | 'stable';
    totalChange: number;
  };
}

interface ScoreTrendResponse {
  data: ScoreTrend;
}

interface DebtAccount {
  accountId: string;
  lender: string;
  type: 'credit_card' | 'personal_loan' | 'mortgage' | 'store_card' | 'overdraft';
  balance: number;
  limit: number | null;
  utilisationPercent: number | null;
  monthlyPayment: number;
  isDisputed: boolean;
}

interface DebtOverview {
  totalDebt: number;
  totalMonthlyPayments: number;
  debtToIncomeRatio: number;
  dbrBand: 'good' | 'fair' | 'poor';
  accounts: DebtAccount[];
  source: string;
  retrievedAt: string;
}

interface DebtOverviewResponse {
  data: DebtOverview;
}

export function useGetScoreTrend(period: '6' | '12' | '24' = '12') {
  return useQuery({
    queryKey: ['score-trend', period],
    queryFn: async () => {
      const res = await apiClient.get<ScoreTrendResponse>(`/history?months=${period}`);
      return res.data.data;
    },
  });
}

export function useGetDebtOverview() {
  return useQuery({
    queryKey: ['debt-overview'],
    queryFn: async () => {
      const res = await apiClient.get<DebtOverviewResponse>('/debt');
      return res.data.data;
    },
  });
}

export type { ScorePoint, Annotation, ScoreTrend, DebtAccount, DebtOverview };
