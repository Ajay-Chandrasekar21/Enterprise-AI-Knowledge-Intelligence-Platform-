import React from 'react';
import { Card } from '@/components/ui/Card';
import { Bar } from 'react-chartjs-2';
import { Award, BookOpen, UserCheck, Calendar } from 'lucide-react';

export const FacultyDashboard: React.FC = () => {
  const statCards = [
    { title: 'Class Subscriptions', value: '128', icon: UserCheck, change: 'Students active: 94%', color: 'text-blue-600 bg-blue-50 dark:bg-blue-950/20' },
    { title: 'Curriculum Syllabi', value: '4', icon: Calendar, change: 'Syllabus changes: 0', color: 'text-purple-600 bg-purple-50 dark:bg-purple-950/20' },
    { title: 'Syllabus Textbooks', value: '16', icon: BookOpen, change: 'All copies in repository', color: 'text-green-600 bg-green-50 dark:bg-green-950/20' },
    { title: 'Student Evaluations', value: '32', icon: Award, change: 'Average performance: A-', color: 'text-yellow-600 bg-yellow-50 dark:bg-yellow-950/20' },
  ];

  const classActivityData = {
    labels: ['Class A', 'Class B', 'Class C', 'Class D'],
    datasets: [
      {
        label: 'Books Borrowed per Class',
        data: [42, 58, 25, 30],
        backgroundColor: '#757cff',
        borderRadius: 6,
      },
    ],
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Faculty Workspace</h1>
        <p className="text-slate-500 text-sm">Coordinate syllabus textbooks, review student checkouts, and manage classes</p>
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
          <h3 className="text-sm font-semibold mb-4 text-slate-500 uppercase">Class Borrowings Metrics</h3>
          <div className="h-64 flex items-center justify-center">
            <Bar data={classActivityData} options={{ responsive: true, maintainAspectRatio: false }} />
          </div>
        </Card>

        <Card className="p-6 lg:col-span-1 flex flex-col justify-between">
          <div>
            <h3 className="text-sm font-semibold mb-4 text-slate-500 uppercase">Syllabus Textbooks In Catalog</h3>
            <div className="space-y-3">
              <div className="p-3 rounded-xl bg-slate-50 dark:bg-slate-800/40 text-xs">
                <h4 className="font-medium text-slate-800 dark:text-slate-200">Artificial Intelligence: A Modern Approach</h4>
                <span className="text-green-600 font-semibold block mt-1">Status: Available (8 copies)</span>
              </div>
              <div className="p-3 rounded-xl bg-slate-50 dark:bg-slate-800/40 text-xs">
                <h4 className="font-medium text-slate-800 dark:text-slate-200">Design Patterns: Elements of Reusable Object-Oriented Software</h4>
                <span className="text-yellow-600 font-semibold block mt-1">Status: Low Stock (1 copy left)</span>
              </div>
            </div>
          </div>
          <button className="mt-4 w-full bg-primary-600 hover:bg-primary-700 text-white py-2 rounded-xl text-xs font-semibold transition-all">
            Recommend New Textbook
          </button>
        </Card>
      </div>
    </div>
  );
};
