import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { apiClient } from './client';

interface Offer {
  offerId: string;
  productName: string;
  amount: number;
  apr: number;
  term: number;
  monthlyPayment: number;
  totalPayable: number;
  totalChargeForCredit: number;
  isPreApproved: boolean;
  status: 'available' | 'suppressed' | 'accepted' | 'expired';
  suppressionReason?: string;
}

interface OffersResponse {
  data: { offers: Offer[]; suppressed: boolean; suppressionReason: string | null };
}

interface SecciData {
  typeOfCredit: string;
  totalAmountOfCredit: number;
  duration: number;
  rateOfInterest: string;
  apr: number;
  monthlyRepayment: number;
  totalAmountPayable: number;
  totalChargeForCredit: number;
  earlyRepaymentFee: string;
  latePaymentFee: string;
  rightToWithdraw: string;
  rightToEarlyRepayment: string;
}

interface SecciResponse {
  data: SecciData;
}

export function useGetOffers() {
  return useQuery({
    queryKey: ['offers'],
    queryFn: async () => {
      const res = await apiClient.get<OffersResponse>('/offers');
      return res.data.data;
    },
  });
}

export function useAcceptOffer() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (offerId: string) => {
      const res = await apiClient.post(`/offers/${offerId}/accept`, null, {
        headers: { 'Idempotency-Key': crypto.randomUUID() },
      });
      return res.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['offers'] });
    },
  });
}

export function useGetSecci(offerId: string) {
  return useQuery({
    queryKey: ['secci', offerId],
    queryFn: async () => {
      const res = await apiClient.get<SecciResponse>(`/offers/${offerId}/secci`);
      return res.data.data;
    },
    enabled: !!offerId,
  });
}

export type { Offer, SecciData };
