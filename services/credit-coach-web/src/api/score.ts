import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { apiClient } from './client';

interface ScoreData {
  customerId: string;
  provider: 'EXPERIAN' | 'EQUIFAX' | 'TRANSUNION';
  score: number;
  maxScore: number;
  band: 'poor' | 'fair' | 'good' | 'very_good' | 'excellent';
  bandLabel: string;
  previousScore: number | null;
  change: number | null;
  changeDirection: 'up' | 'down' | 'unchanged' | null;
  retrievedAt: string;
  isStale: boolean;
}

interface ScoreResponse {
  data: ScoreData;
  meta: { source: string; cacheExpiresAt: string | null };
}

interface ScoreFactor {
  factorId: string;
  category: string;
  impact: 'high' | 'medium' | 'low';
  direction: 'positive' | 'negative';
  title: string;
  description: string;
  weightingPercent: number | null;
}

interface FactorsResponse {
  data: {
    customerId: string;
    factors: ScoreFactor[];
    positiveCount: number;
    negativeCount: number;
    retrievedAt: string;
  };
}

interface ChangeContributor {
  factor: string;
  pointImpact: number;
  description: string;
}

interface ChangeExplanationResponse {
  data: {
    customerId: string;
    previousScore: number;
    currentScore: number;
    totalChange: number;
    changeDirection: 'up' | 'down' | 'unchanged';
    contributors: ChangeContributor[];
    periodStart: string;
    periodEnd: string;
  };
}

export function useGetScore() {
  return useQuery({
    queryKey: ['score'],
    queryFn: async () => {
      const res = await apiClient.get<ScoreResponse>('/dashboard');
      return res.data.data;
    },
  });
}

export function useRefreshScore() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async () => {
      const res = await apiClient.post('/scores/refresh', null, {
        headers: { 'Idempotency-Key': crypto.randomUUID() },
      });
      return res.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['score'] });
    },
  });
}

export function useGetFactors() {
  return useQuery({
    queryKey: ['factors'],
    queryFn: async () => {
      const res = await apiClient.get<FactorsResponse>('/factors');
      return res.data.data;
    },
  });
}

export function useGetChangeExplanation() {
  return useQuery({
    queryKey: ['change-explanation'],
    queryFn: async () => {
      const res = await apiClient.get<ChangeExplanationResponse>('/change-explanation');
      return res.data.data;
    },
  });
}

export type { ScoreData, ScoreFactor, ChangeContributor };
