import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { apiClient } from './client';

interface PlanAction {
  actionId: string;
  rank: number;
  title: string;
  description: string;
  explanation: string;
  impactPoints: number;
  timeframe: string;
  completed: boolean;
  completedAt: string | null;
}

interface Plan {
  customerId: string;
  totalPotentialPoints: number;
  actionsTotal: number;
  actionsCompleted: number;
  confidence: 'high' | 'medium' | 'low';
  updatedAt: string;
  actions: PlanAction[];
}

interface PlanResponse {
  data: Plan;
}

interface Milestone {
  milestoneId: string;
  title: string;
  description: string;
  scoreStart: number;
  scoreCurrent: number;
  pointsGained: number;
  duration: string;
  achievedAt: string;
}

interface MilestonesResponse {
  data: { milestones: Milestone[] };
}

interface SpendingInsight {
  category: string;
  icon: string;
  monthlyAmount: number;
  suggestedReduction: number;
  affordabilityImpactPercent: number;
}

interface SpendingImpactResponse {
  data: { insights: SpendingInsight[]; explanation: string };
}

export function useGetPlan() {
  return useQuery({
    queryKey: ['plan'],
    queryFn: async () => {
      const res = await apiClient.get<PlanResponse>('/plan');
      return res.data.data;
    },
  });
}

export function useRefreshPlan() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async () => {
      const res = await apiClient.post('/plan/refresh', null, {
        headers: { 'Idempotency-Key': crypto.randomUUID() },
      });
      return res.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['plan'] });
    },
  });
}

export function useCompleteAction() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (actionId: string) => {
      const res = await apiClient.post(`/plan/actions/${actionId}/complete`, null, {
        headers: { 'Idempotency-Key': crypto.randomUUID() },
      });
      return res.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['plan'] });
      queryClient.invalidateQueries({ queryKey: ['milestones'] });
    },
  });
}

export function useGetMilestones() {
  return useQuery({
    queryKey: ['milestones'],
    queryFn: async () => {
      const res = await apiClient.get<MilestonesResponse>('/plan/milestones');
      return res.data.data.milestones;
    },
  });
}

export function useGetSpendingImpact() {
  return useQuery({
    queryKey: ['spending-impact'],
    queryFn: async () => {
      const res = await apiClient.get<SpendingImpactResponse>('/plan/spending-impact');
      return res.data.data;
    },
  });
}

export type { Plan, PlanAction, Milestone, SpendingInsight };
