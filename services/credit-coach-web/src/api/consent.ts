import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { apiClient } from './client';

interface ConsentGrantRequest {
  craProvider: 'EXPERIAN' | 'EQUIFAX' | 'TRANSUNION';
  consentTextVersion: string;
  consentTextHash: string;
  channel: 'IOS' | 'ANDROID' | 'WEB';
  privacyNoticeAccepted: boolean;
}

interface ConsentData {
  consentId: string;
  customerId: string;
  craProvider: string;
  status: 'GRANTED' | 'WITHDRAWN';
  consentTextVersion: string;
  grantedAt: string;
  withdrawnAt: string | null;
}

interface ConsentResponse {
  data: ConsentData;
}

interface ConsentsListResponse {
  data: ConsentData[];
}

export function useGetConsents(customerId: string) {
  return useQuery({
    queryKey: ['consents', customerId],
    queryFn: async () => {
      const res = await apiClient.get<ConsentsListResponse>(`/consent-status`);
      return res.data.data;
    },
    enabled: !!customerId,
  });
}

export function useGrantConsent() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (request: ConsentGrantRequest) => {
      const res = await apiClient.post<ConsentResponse>('/consent', request, {
        headers: { 'Idempotency-Key': crypto.randomUUID() },
      });
      return res.data.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['consents'] });
    },
  });
}

export function useWithdrawConsent() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({ consentId, reason }: { consentId: string; reason: string }) => {
      const res = await apiClient.post<ConsentResponse>(
        `/consents/${consentId}/withdraw`,
        { reason },
        { headers: { 'Idempotency-Key': crypto.randomUUID() } },
      );
      return res.data.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['consents'] });
    },
  });
}
