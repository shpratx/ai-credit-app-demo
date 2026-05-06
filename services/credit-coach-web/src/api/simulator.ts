import { useMutation } from '@tanstack/react-query';
import { apiClient } from './client';

interface SimulationRequest {
  scenario: string;
  amount?: number;
}

interface SimulationResult {
  currentScore: number;
  estimatedScore: number;
  change: number;
  confidence: 'high' | 'medium' | 'low';
  details: {
    utilisationBefore: string;
    utilisationAfter: string;
    bandChange: string;
    timeframe: string;
  };
}

interface SimulationResponse {
  data: SimulationResult;
}

export function useRunSimulation() {
  return useMutation({
    mutationFn: async (request: SimulationRequest) => {
      const res = await apiClient.post<SimulationResponse>('/simulator/run', request, {
        headers: { 'Idempotency-Key': crypto.randomUUID() },
      });
      return res.data.data;
    },
  });
}

export type { SimulationRequest, SimulationResult };
