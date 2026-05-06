import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { apiClient } from './client';

interface Alert {
  alertId: string;
  type: 'utilisation' | 'payment' | 'eligibility' | 'score_change';
  severity: 'high' | 'medium' | 'low';
  title: string;
  message: string;
  timestamp: string;
  dismissed: boolean;
}

interface AlertsResponse {
  data: { alerts: Alert[] };
}

interface AlertPreferences {
  utilisationWarnings: boolean;
  paymentReminders: boolean;
  productEligibility: boolean;
  scoreChanges: boolean;
  utilisationThreshold: number;
}

interface PreferencesResponse {
  data: AlertPreferences;
}

export function useGetAlerts() {
  return useQuery({
    queryKey: ['alerts'],
    queryFn: async () => {
      const res = await apiClient.get<AlertsResponse>('/alerts');
      return res.data.data.alerts;
    },
  });
}

export function useGetPreferences() {
  return useQuery({
    queryKey: ['alert-preferences'],
    queryFn: async () => {
      const res = await apiClient.get<PreferencesResponse>('/alerts/preferences');
      return res.data.data;
    },
  });
}

export function useUpdatePreferences() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (prefs: Partial<AlertPreferences>) => {
      const res = await apiClient.put('/alerts/preferences', prefs);
      return res.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['alert-preferences'] });
    },
  });
}

export function useDismissAlert() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (alertId: string) => {
      const res = await apiClient.post(`/alerts/${alertId}/dismiss`);
      return res.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['alerts'] });
    },
  });
}

export type { Alert, AlertPreferences };
