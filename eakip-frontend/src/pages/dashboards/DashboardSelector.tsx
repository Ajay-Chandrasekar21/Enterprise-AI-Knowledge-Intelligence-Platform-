import React from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '@/store';
import { StudentDashboard } from './StudentDashboard';
import { FacultyDashboard } from './FacultyDashboard';
import { LibrarianDashboard } from './LibrarianDashboard';
import { AdminDashboard } from './AdminDashboard';
import { Unauthorized } from '@/pages/fallback/Unauthorized';

export const DashboardSelector: React.FC = () => {
  const { user } = useSelector((state: RootState) => state.auth);

  if (!user) return null;

  switch (user.role) {
    case 'STUDENT':
      return <StudentDashboard />;
    case 'FACULTY':
      return <FacultyDashboard />;
    case 'LIBRARIAN':
      return <LibrarianDashboard />;
    case 'ADMIN':
      return <AdminDashboard />;
    default:
      return <Unauthorized />;
  }
};
