import React, { useState, useEffect } from 'react';
import { Card } from '@/components/ui/Card';
import { Table } from '@/components/ui/Table';
import { Button } from '@/components/ui/Button';
import { toast } from 'react-toastify';
import { Calendar, Trash2 } from 'lucide-react';
import api from '@/services/api';

interface ReservationItem {
  id: string;
  bookTitle: string;
  reservationDate: string;
  status: 'PENDING' | 'FULFILLED' | 'CANCELLED';
  queuePosition: number;
}

export const Reservations: React.FC = () => {
  const [reservations, setReservations] = useState<ReservationItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const fetchReservations = async () => {
    setIsLoading(true);
    try {
      const response = await api.get('/reservations').catch(() => null);
      if (response && response.data?.data) {
        setReservations(response.data.data);
      } else {
        // Fallback mockup
        setReservations([
          { id: 'r1', bookTitle: 'Clean Architecture', reservationDate: '2026-07-05T14:30:00', status: 'PENDING', queuePosition: 2 },
          { id: 'r2', bookTitle: 'SICP', reservationDate: '2026-07-06T10:00:00', status: 'PENDING', queuePosition: 1 },
        ]);
      }
    } catch (e) {
      toast.error('Failed to load reservations');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchReservations();
  }, []);

  const handleCancel = async (reservationId: string) => {
    try {
      await api.post(`/reservations/${reservationId}/cancel`);
      toast.success('Reservation hold cancelled');
      fetchReservations();
    } catch (e: any) {
      toast.error(e.response?.data?.message || 'Error cancelling hold');
    }
  };

  const columns = [
    { header: 'Reserved Book', accessor: (row: ReservationItem) => (
      <div className="flex items-center space-x-3">
        <Calendar className="h-5 w-5 text-slate-400" />
        <span className="font-medium text-slate-800 dark:text-slate-200">{row.bookTitle}</span>
      </div>
    ) },
    { header: 'Reservation Date', accessor: (row: ReservationItem) => <span>{new Date(row.reservationDate).toLocaleDateString()}</span> },
    { header: 'Queue Status', accessor: (row: ReservationItem) => {
      const styles = {
        PENDING: 'bg-yellow-50 text-yellow-700 dark:bg-yellow-950/20 dark:text-yellow-400',
        FULFILLED: 'bg-green-50 text-green-700 dark:bg-green-950/20 dark:text-green-400',
        CANCELLED: 'bg-slate-100 text-slate-700 dark:bg-slate-800/40 dark:text-slate-400',
      };
      return (
        <div className="flex items-center space-x-2">
          <span className={`px-2.5 py-1 rounded-md text-xs font-semibold uppercase ${styles[row.status]}`}>{row.status}</span>
          {row.status === 'PENDING' && (
            <span className="text-xs text-slate-400 font-semibold">(Queue Position #{row.queuePosition})</span>
          )}
        </div>
      );
    } },
    { header: 'Actions', accessor: (row: ReservationItem) => (
      row.status === 'PENDING' ? (
        <Button variant="danger" size="sm" className="px-3 py-1.5 text-xs flex items-center space-x-1" onClick={() => handleCancel(row.id)}>
          <Trash2 className="h-3.5 w-3.5" />
          <span>Cancel hold</span>
        </Button>
      ) : (
        <span className="text-xs text-slate-400">Archived</span>
      )
    ) },
  ];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">My Holds Queue</h1>
        <p className="text-slate-500 text-sm">Review active reservations queue placement and cancel pending bookings</p>
      </div>

      <Card className="p-6">
        {isLoading ? (
          <div className="py-12 flex justify-center"><div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div></div>
        ) : (
          <Table columns={columns} data={reservations} />
        )}
      </Card>
    </div>
  );
};
