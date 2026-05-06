import { useAuthStore } from '../store/authStore';
import { useGetConsents, useGrantConsent, useWithdrawConsent } from '../api/consent';

export function useConsent() {
  const customerId = useAuthStore((s) => s.customerId) ?? '';
  const consents = useGetConsents(customerId);
  const grant = useGrantConsent();
  const withdraw = useWithdrawConsent();

  const hasActiveConsent = consents.data?.some((c) => c.status === 'GRANTED') ?? false;

  return { consents, grant, withdraw, hasActiveConsent };
}
