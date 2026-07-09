import React, { Suspense, lazy } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { PublicLayout } from '@/layouts/PublicLayout';
import { DashboardLayout } from '@/layouts/DashboardLayout';
import { ProtectedRoute } from './ProtectedRoute';
import { Spinner } from '@/components/ui/Spinner';

// Lazy loaded pages
const Landing = lazy(() => import('@/pages/landing/Landing').then((m) => ({ default: m.Landing })));
const Login = lazy(() => import('@/pages/auth/Login').then((m) => ({ default: m.Login })));
const Register = lazy(() => import('@/pages/auth/Register').then((m) => ({ default: m.Register })));
const Books = lazy(() => import('@/pages/books/Books').then((m) => ({ default: m.Books })));

// Dashboards
const DashboardSelector = lazy(() =>
  import('@/pages/dashboards/DashboardSelector').then((m) => ({ default: m.DashboardSelector }))
);

// Fallbacks
const NotFound = lazy(() => import('@/pages/fallback/NotFound').then((m) => ({ default: m.NotFound })));
const Unauthorized = lazy(() =>
  import('@/pages/fallback/Unauthorized').then((m) => ({ default: m.Unauthorized }))
);

// Lazy imports for feature pages
const BorrowedBooks = lazy(() => import('@/pages/borrow/BorrowedBooks').then((m) => ({ default: m.BorrowedBooks })));
const Reservations = lazy(() => import('@/pages/reserve/Reservations').then((m) => ({ default: m.Reservations })));
const Analytics = lazy(() => import('@/pages/analytics/Analytics').then((m) => ({ default: m.Analytics })));
const Profile = lazy(() => import('@/pages/profile/Profile').then((m) => ({ default: m.Profile })));
const AiConsole = lazy(() => import('@/pages/ai/AiConsole').then((m) => ({ default: m.AiConsole })));
const KnowledgeCenter = lazy(() => import('@/pages/rag/KnowledgeCenter').then((m) => ({ default: m.KnowledgeCenter })));
const RecommendationCenter = lazy(() => import('@/pages/ai/RecommendationCenter').then((m) => ({ default: m.RecommendationCenter })));
const ReadingCoach = lazy(() => import('@/pages/ai/ReadingCoach').then((m) => ({ default: m.ReadingCoach })));
const SemanticSearchUI = lazy(() => import('@/pages/ai/SemanticSearchUI').then((m) => ({ default: m.SemanticSearchUI })));
const PlanningDashboard = lazy(() => import('@/pages/ai/PlanningDashboard').then((m) => ({ default: m.PlanningDashboard })));
const ToolMarketplace = lazy(() => import('@/pages/tools/ToolMarketplace').then((m) => ({ default: m.ToolMarketplace })));
const McpDashboard = lazy(() => import('@/pages/mcp/McpDashboard').then((m) => ({ default: m.McpDashboard })));
const MemoryDashboard = lazy(() => import('@/pages/memory/MemoryDashboard').then((m) => ({ default: m.MemoryDashboard })));
const WorkflowDesigner = lazy(() => import('@/pages/workflow/WorkflowDesigner').then((m) => ({ default: m.WorkflowDesigner })));
const SaaSAdminPortal = lazy(() => import('@/pages/saas/SaaSAdminPortal').then((m) => ({ default: m.SaaSAdminPortal })));

const Settings = () => (
  <div class="p-6 bg-white dark:bg-slate-900 border border-slate-200/50 dark:border-slate-800/40 rounded-2xl">
    <h1 class="text-xl font-bold mb-2">System Settings</h1>
    <p class="text-slate-500 text-xs">Platform settings, color schemes preferences, and API keys.</p>
  </div>
);

const LoadingFallback = () => (
  <div class="min-h-screen flex items-center justify-center bg-slate-50 dark:bg-slate-950">
    <Spinner size="lg" />
  </div>
);

export const AppRoutes: React.FC = () => {
  return (
    <Suspense fallback={<LoadingFallback />}>
      <Routes>
        
        {/* Public Routes */}
        <Route element={<PublicLayout />}>
          <Route path="/" element={<Landing />} />
          <Route path="/about" element={<div class="p-8 max-w-4xl mx-auto"><h1 class="text-2xl font-bold">About EAKIP</h1></div>} />
          <Route path="/features" element={<div class="p-8 max-w-4xl mx-auto"><h1 class="text-2xl font-bold">Platform Features</h1></div>} />
          <Route path="/contact" element={<div class="p-8 max-w-4xl mx-auto"><h1 class="text-2xl font-bold">Contact Support</h1></div>} />
        </Route>

        {/* Auth routes */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/unauthorized" element={<Unauthorized />} />

        {/* Protected Dashboard Portal Routes */}
        <Route
          element={
            <ProtectedRoute>
              <DashboardLayout />
            </ProtectedRoute>
          }
        >
          <Route path="/dashboard" element={<DashboardSelector />} />
          <Route path="/books" element={<Books />} />
          <Route path="/borrowed" element={<BorrowedBooks />} />
          <Route path="/reservations" element={<Reservations />} />
          <Route path="/analytics" element={<Analytics />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/settings" element={<Settings />} />
          <Route path="/ai" element={<AiConsole />} />
          <Route path="/recommendations" element={<RecommendationCenter />} />
          <Route path="/coach" element={<ReadingCoach />} />
          <Route path="/semantic-search" element={<SemanticSearchUI />} />
          <Route path="/rag" element={<KnowledgeCenter />} />
          <Route path="/planning" element={<PlanningDashboard />} />
          <Route path="/tools" element={<ToolMarketplace />} />
          <Route path="/mcp" element={<McpDashboard />} />
          <Route path="/memory" element={<MemoryDashboard />} />
          <Route path="/workflow" element={<WorkflowDesigner />} />
          <Route path="/saas" element={<SaaSAdminPortal />} />
        </Route>

        {/* Redirects & fallback */}
        <Route path="/home" element={<Navigate to="/" replace />} />
        <Route path="*" element={<NotFound />} />

      </Routes>
    </Suspense>
  );
};
