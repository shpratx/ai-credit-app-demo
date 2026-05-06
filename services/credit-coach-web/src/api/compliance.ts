import { useMutation, useQuery } from '@tanstack/react-query';
import { apiClient } from './client';

interface DecisionFactor {
  factor: string;
  value: string;
}

interface DecisionExplanation {
  offerId: string;
  factors: DecisionFactor[];
  riskTier: string;
  rateOffered: string;
  rights: string[];
}

interface DecisionExplanationResponse {
  data: DecisionExplanation;
}

interface BureauScore {
  provider: 'EXPERIAN' | 'EQUIFAX' | 'TRANSUNION';
  score: number;
  maxScore: number;
  band: string;
  normalisedPercent: number;
}

interface MultiBureauResponse {
  data: { scores: BureauScore[]; explanation: string };
}

export function useDeleteData() {
  return useMutation({
    mutationFn: async () => {
      const res = await apiClient.delete('/data', {
        headers: { 'Idempotency-Key': crypto.randomUUID() },
      });
      return res.data;
    },
  });
}

export function useExportDsar() {
  return useMutation({
    mutationFn: async () => {
      const res = await apiClient.post('/data/export', null, {
        headers: { 'Idempotency-Key': crypto.randomUUID() },
      });
      return res.data;
    },
  });
}

export function useGetDecisionExplanation(offerId: string) {
  return useQuery({
    queryKey: ['decision-explanation', offerId],
    queryFn: async () => {
      const res = await apiClient.get<DecisionExplanationResponse>(
        `/offers/${offerId}/explanation`,
      );
      return res.data.data;
    },
    enabled: !!offerId,
  });
}

export function useGetMultiBureau() {
  return useQuery({
    queryKey: ['multi-bureau'],
    queryFn: async () => {
      const res = await apiClient.get<MultiBureauResponse>('/scores/compare');
      return res.data.data;
    },
  });
}

export type { DecisionExplanation, DecisionFactor, BureauScore };
