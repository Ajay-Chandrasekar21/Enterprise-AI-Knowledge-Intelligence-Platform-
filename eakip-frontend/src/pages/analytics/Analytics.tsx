import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '@/store';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Line, Doughnut } from 'react-chartjs-2';
import { toast } from 'react-toastify';
import { FileDown, Activity, AlertCircle } from 'lucide-react';
import api from '@/services/api';

export const Analytics: React.FC = () => {
  const { user } = useSelector((state: RootState) => state.auth);
  const [isExporting, setIsExporting] = useState(false);

  const lineData = {
    labels: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
    datasets: [
      {
        label: 'My Reading Hours',
        data: [12, 18, 24, 30, 28, 35],
        borderColor: '#5356ff',
        backgroundColor: 'rgba(83, 86, 255, 0.1)',
        tension: 0.35,
        fill: true,
      },
    ],
  };

  const donutData = {
    labels: ['Software Engineering', 'Algorithms', 'Mathematics', 'Literature'],
    datasets: [
      {
        data: [45, 30, 15, 10],
        backgroundColor: ['#5356ff', '#3432be', '#757cff', '#c2c9ff'],
        borderWidth: 0,
      },
    ],
  };

  const handleCsvExport = async (type: 'books' | 'borrowings') => {
    setIsExporting(true);
    try {
      const response = await api.get(`/reports/${type}/csv`, { responseType: 'blob' });
      const blob = new Blob([response.data], { type: 'text/csv' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `${type}_catalog_report.csv`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      toast.success(`${type.toUpperCase()} report exported successfully!`);
    } catch (e) {
      toast.error('Failed to export report CSV. Ensure admin privileges.');
    } finally {
      setIsExporting(false);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Analytics & Audits</h1>
        <p className="text-slate-500 text-sm">Monitor system audit indexes, check logs, and retrieve operational exports</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Weekly activity hours */}
        <Card className="p-6 lg:col-span-2">
          <h3 className="text-sm font-semibold mb-4 text-slate-500 uppercase">Reading Habits Trajectory</h3>
          <div className="h-64 flex items-center justify-center">
            <Line data={lineData} options={{ responsive: true, maintainAspectRatio: false }} />
          </div>
        </Card>

        {/* Categories distribution */}
        <Card className="p-6 lg:col-span-1">
          <h3 className="text-sm font-semibold mb-4 text-slate-500 uppercase">Reading Preferences Distribution</h3>
          <div className="h-64 flex items-center justify-center">
            <Doughnut data={donutData} options={{ responsive: true, maintainAspectRatio: false }} />
          </div>
        </Card>

      </div>

      {/* Exporter Block for Admins & Librarians */}
      {(user?.role === 'ADMIN' || user?.role === 'LIBRARIAN') && (
        <Card className="p-6 space-y-4">
          <div className="flex items-center space-x-2 text-primary-600 dark:text-primary-400">
            <Activity className="h-5 w-5" />
            <h3 className="font-bold text-base">Administrative Exporters</h3>
          </div>
          <p className="text-slate-500 text-sm">Download complete transactional audits and catalog indexes mapping database statistics as CSV files.</p>
          <div className="flex flex-wrap gap-4 pt-2">
            <Button 
              onClick={() => handleCsvExport('books')} 
              isLoading={isExporting} 
              variant="outline" 
              className="flex items-center space-x-2"
            >
              <FileDown className="h-4 w-4" />
              <span>Export Books Catalog (CSV)</span>
            </Button>
            <Button 
              onClick={() => handleCsvExport('borrowings')} 
              isLoading={isExporting} 
              variant="outline" 
              className="flex items-center space-x-2"
            >
              <FileDown className="h-4 w-4" />
              <span>Export Borrowings Logs (CSV)</span>
            </Button>
          </div>
        </Card>
      )}

    </div>
  );
};
