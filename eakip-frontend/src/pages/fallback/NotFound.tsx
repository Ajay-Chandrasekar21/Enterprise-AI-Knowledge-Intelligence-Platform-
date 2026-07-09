import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/Button';
import { AlertCircle } from 'lucide-react';

export const NotFound: React.FC = () => {
  return (
    <div className="min-h-screen flex items-center justify-center p-4 bg-slate-50 dark:bg-slate-950">
      <div className="text-center space-y-6 max-w-md">
        <div className="h-16 w-16 mx-auto rounded-2xl bg-slate-100 dark:bg-slate-900 border border-slate-200/50 dark:border-slate-800/40 flex items-center justify-center text-slate-500">
          <AlertCircle className="h-8 w-8" />
        </div>
        <div className="space-y-2">
          <h1 className="text-4xl font-extrabold">Page Not Found</h1>
          <p className="text-slate-500 text-sm">
            The page you are looking for does not exist or has been moved.
          </p>
        </div>
        <Link to="/dashboard" className="inline-block">
          <Button>Back to Dashboard</Button>
        </Link>
      </div>
    </div>
  );
};
