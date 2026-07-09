import React from 'react';
import { Card } from '@/components/ui/Card';
import { Line } from 'react-chartjs-2';
import { BookOpen, Calendar, Clock, Bookmark } from 'lucide-react';

export const StudentDashboard: React.FC = () => {
  const statCards = [
    { title: 'Books Borrowed', value: '3', icon: BookOpen, change: '1 book due in 3 days', color: 'text-blue-600 bg-blue-50 dark:bg-blue-950/20' },
    { title: 'Active Reservations', value: '1', icon: Calendar, change: 'Clean Architecture (Queue #2)', color: 'text-purple-600 bg-purple-50 dark:bg-purple-950/20' },
    { title: 'Weekly Reading Time', value: '8.5h', icon: Clock, change: '+1.5h compared to last week', color: 'text-green-600 bg-green-50 dark:bg-green-950/20' },
    { title: 'Active Study Tasks', value: '5', icon: Bookmark, change: 'Next milestone: Chapter 4', color: 'text-yellow-600 bg-yellow-50 dark:bg-yellow-950/20' },
  ];

  const readingHoursData = {
    labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
    datasets: [
      {
        label: 'Daily Reading Hours',
        data: [1.2, 0.8, 1.5, 2.0, 0.5, 1.0, 1.5],
        borderColor: '#5356ff',
        backgroundColor: 'rgba(83, 86, 255, 0.1)',
        tension: 0.3,
        fill: true,
      },
    ],
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Student Workspace</h1>
        <p className="text-slate-500 text-sm">Track your reading metrics, active borrowings, and study milestones</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {statCards.map((card, idx) => {
          const Icon = card.icon;
          return (
            <Card key={idx} className="p-6">
              <div className="flex items-center justify-between">
                <div className="space-y-1">
                  <span className="text-xs font-semibold text-slate-500 uppercase">{card.title}</span>
                  <h3 className="text-2xl font-bold">{card.value}</h3>
                  <span className="text-xs text-slate-400 block">{card.change}</span>
                </div>
                <div className={`h-12 w-12 rounded-xl flex items-center justify-center ${card.color}`}>
                  <Icon className="h-6 w-6" />
                </div>
              </div>
            </Card>
          );
        })}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <Card className="p-6 lg:col-span-2">
          <h3 className="text-sm font-semibold mb-4 text-slate-500 uppercase">Weekly Activity Tracking</h3>
          <div className="h-64 flex items-center justify-center">
            <Line data={readingHoursData} options={{ responsive: true, maintainAspectRatio: false }} />
          </div>
        </Card>

        <Card className="p-6 lg:col-span-1 flex flex-col justify-between">
          <div>
            <h3 className="text-sm font-semibold mb-4 text-slate-500 uppercase">Upcoming Deadlines</h3>
            <div className="space-y-4">
              <div className="flex items-start space-x-3 p-3 rounded-xl bg-red-50/50 dark:bg-red-950/10 border border-red-100/50 dark:border-red-900/10 text-xs">
                <div className="font-bold text-red-600">Due 10 Jul</div>
                <div>
                  <h4 className="font-medium text-slate-800 dark:text-slate-200">Introduction to Algorithms</h4>
                  <p className="text-slate-500">ISBN: 978-0262033848</p>
                </div>
              </div>
              <div className="flex items-start space-x-3 p-3 rounded-xl bg-slate-50 dark:bg-slate-800/40 text-xs">
                <div className="font-bold text-slate-500">Due 24 Jul</div>
                <div>
                  <h4 className="font-medium text-slate-800 dark:text-slate-200">Linear Algebra and Its Applications</h4>
                  <p className="text-slate-500">ISBN: 978-0321385178</p>
                </div>
              </div>
            </div>
          </div>
          <button className="mt-4 w-full bg-primary-50 hover:bg-primary-100 dark:bg-primary-950/20 dark:hover:bg-primary-950/40 text-primary-600 dark:text-primary-400 py-2 rounded-xl text-xs font-semibold transition-all">
            Renew Borrowed Books
          </button>
        </Card>
      </div>
    </div>
  );
};
