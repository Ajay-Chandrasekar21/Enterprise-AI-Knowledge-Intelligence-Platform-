import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/Button';
import { ShieldAlert } from 'lucide-react';

export const Unauthorized: React.FC = () => {
  return (
    <div className="min-h-screen flex items-center justify-center p-4 bg-slate-50 dark:bg-slate-950">
      <div className="text-center space-y-6 max-w-md">
        <div className="h-16 w-16 mx-auto rounded-2xl bg-red-50 dark:bg-red-950/20 flex items-center justify-center text-red-600 dark:text-red-400">
          <ShieldAlert className="h-8 w-8" />
        </div>
        <div className="space-y-2">
          <h1 className="text-3xl font-extrabold text-red-600 dark:text-red-400">Access Denied</h1>
          <p className="text-slate-500 text-sm">
            You do not have the security clearance required to view this portal directory.
          </p>
        </div>
        <Link to="/dashboard" className="inline-block">
          <Button variant="secondary">Back to Dashboard</Button>
        </Link>
      </div>
    </div>
  );
};
