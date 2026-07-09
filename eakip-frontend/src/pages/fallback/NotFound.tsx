import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/Button';
import { AlertCircle } from 'lucide-react';

export const NotFound: React.FC = () => {
  return (
    <div class="min-h-screen flex items-center justify-center p-4 bg-slate-50 dark:bg-slate-950">
      <div class="text-center space-y-6 max-w-md">
        <div class="h-16 w-16 mx-auto rounded-2xl bg-slate-100 dark:bg-slate-900 border border-slate-200/50 dark:border-slate-800/40 flex items-center justify-center text-slate-500">
          <AlertCircle class="h-8 w-8" />
        </div>
        <div class="space-y-2">
          <h1 class="text-4xl font-extrabold">Page Not Found</h1>
          <p class="text-slate-500 text-sm">
            The page you are looking for does not exist or has been moved.
          </p>
        </div>
        <Link to="/dashboard" class="inline-block">
          <Button>Back to Dashboard</Button>
        </Link>
      </div>
    </div>
  );
};
