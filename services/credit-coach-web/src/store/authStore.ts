import { create } from 'zustand';

interface AuthState {
  token: string | null;
  customerId: string | null;
  setAuth: (token: string, customerId: string) => void;
  clearAuth: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  token: null,
  customerId: null,
  setAuth: (token, customerId) => set({ token, customerId }),
  clearAuth: () => set({ token: null, customerId: null }),
}));
