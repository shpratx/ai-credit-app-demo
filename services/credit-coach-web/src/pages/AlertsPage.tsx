import { Link } from 'react-router-dom';
import { useGetAlerts, useDismissAlert } from '../api/alerts';
import { AlertCard } from '../components/AlertCard';
import { Alert } from '../components/ui/Alert';

export default function AlertsPage() {
  const { data: alerts, isLoading, error } = useGetAlerts();
  const dismissAlert = useDismissAlert();

  if (isLoading) {
    return (
      <div className="p-4 max-w-md mx-auto" aria-label="Loading alerts">
        <div className="animate-pulse space-y-3">
          <div className="h-24 bg-gray-200 rounded-card" />
          <div className="h-24 bg-gray-200 rounded-card" />
          <div className="h-24 bg-gray-200 rounded-card" />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-4 max-w-md mx-auto">
        <Alert variant="error">Couldn't load your alerts. Please try again later.</Alert>
      </div>
    );
  }

  const activeAlerts = alerts?.filter((a) => !a.dismissed) ?? [];

  return (
    <div className="p-4 max-w-md mx-auto">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-lg font-bold">Alerts</h1>
        <Link to="/credit-coach/alerts/settings" className="text-sm font-bold text-lloyds-green" aria-label="Alert settings">
          Settings
        </Link>
      </div>

      {activeAlerts.length === 0 ? (
        <div className="text-center py-12">
          <div className="text-4xl mb-3">🔔</div>
          <p className="text-sm text-text-secondary">No alerts right now. We'll notify you when something needs your attention.</p>
        </div>
      ) : (
        <div role="feed" aria-label="Credit health alerts">
          {activeAlerts.map((alert) => (
            <AlertCard
              key={alert.alertId}
              severity={alert.severity}
              title={alert.title}
              message={alert.message}
              timestamp={alert.timestamp}
              onDismiss={() => dismissAlert.mutate(alert.alertId)}
            />
          ))}
        </div>
      )}
    </div>
  );
}
