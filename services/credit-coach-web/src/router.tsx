import { createBrowserRouter, Navigate, Outlet, useLocation } from 'react-router-dom';
import { PageShell } from './components/PageShell';
import ConsentPage from './pages/ConsentPage';
import DashboardPage from './pages/DashboardPage';
import FactorsPage from './pages/FactorsPage';
import SettingsPage from './pages/SettingsPage';
import ImprovementPlanPage from './pages/ImprovementPlanPage';
import SpendingImpactPage from './pages/SpendingImpactPage';
import ScoreHistoryPage from './pages/ScoreHistoryPage';
import DebtOverviewPage from './pages/DebtOverviewPage';
import SimulatorPage from './pages/SimulatorPage';
import OffersPage from './pages/OffersPage';
import SecciPage from './pages/SecciPage';
import AlertsPage from './pages/AlertsPage';
import AlertPreferencesPage from './pages/AlertPreferencesPage';
import DeleteDataPage from './pages/DeleteDataPage';
import DecisionExplanationPage from './pages/DecisionExplanationPage';
import DsarExportPage from './pages/DsarExportPage';
import MultiBureauPage from './pages/MultiBureauPage';

const titles: Record<string, string> = {
  '/credit-coach/factors': 'Score Factors',
  '/credit-coach/settings/consent': 'Settings',
  '/credit-coach/plan': 'Your Plan',
  '/credit-coach/spending-impact': 'Spending & Credit',
  '/credit-coach/history': 'Score History',
  '/credit-coach/debt': 'Your Debt',
  '/credit-coach/simulator': 'What If?',
  '/credit-coach/offers': 'Your Offers',
  '/credit-coach/alerts': 'Alerts',
  '/credit-coach/alerts/settings': 'Alert Settings',
  '/credit-coach/settings/delete': 'Delete My Data',
  '/credit-coach/settings/export': 'Export My Data',
  '/credit-coach/scores/compare': 'Score Comparison',
};

function ShellWrapper() {
  const location = useLocation();
  const title = titles[location.pathname] || 'Credit Coach';
  return <PageShell title={title}><Outlet /></PageShell>;
}

export const router = createBrowserRouter([
  // Pages with their own shell (custom layout)
  { path: '/', element: <Navigate to="/credit-coach/consent" replace /> },
  { path: '/credit-coach/consent', element: <ConsentPage /> },
  { path: '/credit-coach/dashboard', element: <DashboardPage /> },
  // Pages wrapped in shared shell
  {
    element: <ShellWrapper />,
    children: [
      { path: '/credit-coach/factors', element: <FactorsPage /> },
      { path: '/credit-coach/settings/consent', element: <SettingsPage /> },
      { path: '/credit-coach/plan', element: <ImprovementPlanPage /> },
      { path: '/credit-coach/spending-impact', element: <SpendingImpactPage /> },
      { path: '/credit-coach/history', element: <ScoreHistoryPage /> },
      { path: '/credit-coach/debt', element: <DebtOverviewPage /> },
      { path: '/credit-coach/simulator', element: <SimulatorPage /> },
      { path: '/credit-coach/offers', element: <OffersPage /> },
      { path: '/credit-coach/offers/:offerId/secci', element: <SecciPage /> },
      { path: '/credit-coach/offers/:offerId/explanation', element: <DecisionExplanationPage /> },
      { path: '/credit-coach/alerts', element: <AlertsPage /> },
      { path: '/credit-coach/alerts/settings', element: <AlertPreferencesPage /> },
      { path: '/credit-coach/settings/delete', element: <DeleteDataPage /> },
      { path: '/credit-coach/settings/export', element: <DsarExportPage /> },
      { path: '/credit-coach/scores/compare', element: <MultiBureauPage /> },
    ],
  },
]);
