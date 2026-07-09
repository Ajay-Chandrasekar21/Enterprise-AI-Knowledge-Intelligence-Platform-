import React from 'react';
import { Card } from '@/components/ui/Card';
import { Doughnut, Bar } from 'react-chartjs-2';
import { Book, FileText, CheckCircle, RefreshCw } from 'lucide-react';

export const LibrarianDashboard: React.FC = () => {
  const statCards = [
    { title: 'Total Catalog Volume', value: '8,420', icon: Book, change: '12 new additions today', color: 'text-green-600 bg-green-50 dark:bg-green-950/20' },
    { title: 'Pending OCR Ingestions', value: '4', icon: RefreshCw, change: 'Running worker nodes: 2', color: 'text-blue-600 bg-blue-50 dark:bg-blue-950/20' },
    { title: 'Total Active Borrows', value: '298', icon: CheckCircle, change: 'Returns expected today: 14', color: 'text-purple-600 bg-purple-50 dark:bg-purple-950/20' },
    { title: 'Unparsed Document Files', value: '21', icon: FileText, change: 'Requires manual indexing', color: 'text-yellow-600 bg-yellow-50 dark:bg-yellow-950/20' },
  ];

  const doughnutData = {
    labels: ['PDF', 'DOCX', 'TXT', 'PPTX'],
    datasets: [
      {
        data: [60, 20, 15, 5],
        backgroundColor: ['#5356ff', '#3432be', '#757cff', '#c2c9ff'],
        borderWidth: 0,
      },
    ],
  };

  const inventoryData = {
    labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
    datasets: [
      {
        label: 'New Registrations',
        data: [45, 60, 35, 80],
        backgroundColor: '#5356ff',
        borderRadius: 6,
      },
    ],
  };

  return (
    <div class="space-y-6">
      <div>
        <h1 class="text-2xl font-bold">Librarian Operations Console</h1>
        <p class="text-slate-500 text-sm">Monitor catalog acquisitions, processing pipelines, and checkouts</p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {statCards.map((card, idx) => {
          const Icon = card.icon;
          return (
            <Card key={idx} class="p-6">
              <div class="flex items-center justify-between">
                <div class="space-y-1">
                  <span class="text-xs font-semibold text-slate-500 uppercase">{card.title}</span>
                  <h3 class="text-2xl font-bold">{card.value}</h3>
                  <span class="text-xs text-slate-400 block">{card.change}</span>
                </div>
                <div class={`h-12 w-12 rounded-xl flex items-center justify-center ${card.color}`}>
                  <Icon class="h-6 w-6" />
                </div>
              </div>
            </Card>
          );
        })}
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <Card class="p-6 lg:col-span-1">
          <h3 class="text-sm font-semibold mb-4 text-slate-500 uppercase">Document Ingestion Share</h3>
          <div class="h-64 flex items-center justify-center">
            <Doughnut data={doughnutData} options={{ responsive: true, maintainAspectRatio: false }} />
          </div>
        </Card>

        <Card class="p-6 lg:col-span-2">
          <h3 class="text-sm font-semibold mb-4 text-slate-500 uppercase">Catalog Scaling Trajectory</h3>
          <div class="h-64 flex items-center justify-center">
            <Bar data={inventoryData} options={{ responsive: true, maintainAspectRatio: false }} />
          </div>
        </Card>
      </div>
    </div>
  );
};
