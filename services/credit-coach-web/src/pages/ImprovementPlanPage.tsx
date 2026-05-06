import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useGetPlan, useRefreshPlan, useCompleteAction, useGetMilestones } from '../api/plan';
import { ActionItem } from '../components/ActionItem';
import { MilestoneModal } from '../components/MilestoneModal';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Alert } from '../components/ui/Alert';

export default function ImprovementPlanPage() {
  const { data: plan, isLoading, error } = useGetPlan();
  const refreshPlan = useRefreshPlan();
  const completeAction = useCompleteAction();
  const { data: milestones } = useGetMilestones();
  const [showMilestone, setShowMilestone] = useState(true);

  const latestMilestone = milestones?.[0];

  if (isLoading) {
    return (
      <div className="p-4 max-w-md mx-auto" aria-label="Loading improvement plan">
        <div className="animate-pulse space-y-4">
          <div className="h-32 bg-gray-200 rounded-card" />
          <div className="h-48 bg-gray-200 rounded-card" />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-4 max-w-md mx-auto">
        <Alert variant="error">
          We couldn't generate a personalised plan yet. Check back after your next score refresh.
        </Alert>
        <Link to="/credit-coach/dashboard">
          <Button variant="secondary" className="mt-4">View your score →</Button>
        </Link>
      </div>
    );
  }

  if (!plan || plan.actions.length === 0) {
    return (
      <div className="p-4 max-w-md mx-auto text-center py-12">
        <div className="text-5xl mb-4">⭐</div>
        <h1 className="text-xl font-bold">Your credit is excellent!</h1>
        <p className="text-sm text-text-secondary mt-3 max-w-[280px] mx-auto leading-relaxed">
          There are no improvement actions needed right now. Here's how to maintain your great score:
        </p>
        <Card className="text-left mt-5">
          <div className="text-sm leading-[1.8]">
            ✓ Keep paying on time<br />
            ✓ Keep utilisation below 30%<br />
            ✓ Avoid unnecessary credit applications<br />
            ✓ Keep your oldest accounts open
          </div>
        </Card>
      </div>
    );
  }

  const progressPercent = (plan.actionsCompleted / plan.actionsTotal) * 100;

  return (
    <div className="p-4 max-w-md mx-auto">
      {latestMilestone && showMilestone && (
        <MilestoneModal
          title={latestMilestone.title}
          description={latestMilestone.description}
          scoreStart={latestMilestone.scoreStart}
          scoreCurrent={latestMilestone.scoreCurrent}
          pointsGained={latestMilestone.pointsGained}
          duration={latestMilestone.duration}
          onDismiss={() => setShowMilestone(false)}
          onContinue={() => setShowMilestone(false)}
        />
      )}

      {/* Plan Summary */}
      <div className="bg-lloyds-green text-white rounded-card p-5 mb-4">
        <p className="text-caption opacity-75">Personalised for you · Updated {new Date(plan.updatedAt).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' })}</p>
        <p className="text-2xl font-bold my-2">+{plan.totalPotentialPoints} points possible</p>
        <p className="text-sm opacity-85">{plan.actionsTotal} actions · Estimated 2–4 months</p>
        <div className="mt-4">
          <div className="flex justify-between text-xs opacity-70 mb-1">
            <span>Progress</span>
            <span>{plan.actionsCompleted} of {plan.actionsTotal} completed</span>
          </div>
          <div className="w-full h-2 bg-white/25 rounded-full">
            <div className="h-full bg-white rounded-full transition-all" style={{ width: `${progressPercent}%` }} role="progressbar" aria-valuenow={progressPercent} aria-valuemin={0} aria-valuemax={100} aria-label="Plan progress" />
          </div>
        </div>
      </div>

      {/* Actions */}
      <Card title="Your actions">
        <ul className="list-none" aria-label="Improvement actions">
          {plan.actions.map((action) => (
            <ActionItem
              key={action.actionId}
              rank={action.rank}
              title={action.title}
              description={action.description}
              explanation={action.explanation}
              impactPoints={action.impactPoints}
              timeframe={action.timeframe}
              completed={action.completed}
              completedAt={action.completedAt}
              onComplete={() => completeAction.mutate(action.actionId)}
            />
          ))}
        </ul>
      </Card>

      <p className="text-micro text-text-muted italic mt-2">
        These are estimates based on typical scoring patterns. Actual results may vary.
      </p>

      <Button variant="ghost" onClick={() => refreshPlan.mutate()} isLoading={refreshPlan.isPending} className="mt-4" aria-label="Refresh plan">
        Refresh plan
      </Button>
    </div>
  );
}
