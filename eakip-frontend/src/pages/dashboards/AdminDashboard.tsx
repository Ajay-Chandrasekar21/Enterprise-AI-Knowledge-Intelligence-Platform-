import React from 'react';
import { Card } from '@/components/ui/Card';
import { 
  Chart as ChartJS, 
  CategoryScale, 
  LinearScale, 
  PointElement, 
  LineElement, 
  BarElement,
  ArcElement,
  Title, 
  Tooltip, 
  Legend 
} from 'chart.js';
import { Line, Bar } from 'react-chartjs-2';
import { Users, BookOpen, Calendar, AlertCircle } from 'lucide-react';

// Register ChartJS elements
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  ArcElement,
  Title,
  Tooltip,
  Legend
);

export const AdminDashboard: React.FC = () => {
  const statCards = [
    { title: 'Total Registered Users', value: '1,482', icon: Users, change: '+12% from last month', color: 'text-blue-600 bg-blue-50 dark:bg-blue-950/20' },
    { title: 'Total Books Cataloged', value: '12,450', icon: BookOpen, change: '+4% from last month', color: 'text-green-600 bg-green-50 dark:bg-green-950/20' },
    { title: 'Active Borrowings', value: '342', icon: Calendar, change: '+18% from last week', color: 'text-purple-600 bg-purple-50 dark:bg-purple-950/20' },
    { title: 'Overdue Books', value: '18', icon: AlertCircle, change: '-5% from last week', color: 'text-red-600 bg-red-50 dark:bg-red-950/20' },
  ];

  const lineData = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
    datasets: [
      {
        label: 'Monthly Platform Traffic',
        data: [1200, 1900, 3200, 5000, 4500, 6000],
        borderColor: '#5356ff',
        backgroundColor: 'rgba(83, 86, 255, 0.1)',
        tension: 0.4,
        fill: true,
      },
    ],
  };

  const barData = {
    labels: ['Computer Science', 'Mathematics', 'Literature', 'Physics', 'History'],
    datasets: [
      {
        label: 'Borrowings by Category',
        data: [420, 290, 180, 150, 90],
        backgroundColor: '#757cff',
        borderRadius: 8,
      },
    ],
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold">Admin Infrastructure Dashboard</h1>
          <p className="text-slate-500 text-sm">System performance metrics and catalog analysis</p>
        </div>
      </div>

      {/* Grid statistics */}
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

      {/* Charts section */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card className="p-6">
          <h3 className="text-sm font-semibold mb-4 text-slate-500 uppercase">Platform Activity Trend</h3>
          <div className="h-72 flex items-center justify-center">
            <Line data={lineData} options={{ responsive: true, maintainAspectRatio: false }} />
          </div>
        </Card>

        <Card className="p-6">
          <h3 className="text-sm font-semibold mb-4 text-slate-500 uppercase">Top Catalog Demands</h3>
          <div className="h-72 flex items-center justify-center">
            <Bar data={barData} options={{ responsive: true, maintainAspectRatio: false }} />
          </div>
        </Card>
      </div>
    </div>
  );
};
