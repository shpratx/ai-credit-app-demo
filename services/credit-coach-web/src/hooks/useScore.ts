import { useGetScore, useGetFactors, useGetChangeExplanation, useRefreshScore } from '../api/score';

export function useScore() {
  const score = useGetScore();
  const factors = useGetFactors();
  const changeExplanation = useGetChangeExplanation();
  const refresh = useRefreshScore();

  return { score, factors, changeExplanation, refresh };
}
