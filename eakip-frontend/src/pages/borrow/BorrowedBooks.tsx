import React, { useState, useEffect } from 'react';
import { Card } from '@/components/ui/Card';
import { Table } from '@/components/ui/Table';
import { Button } from '@/components/ui/Button';
import { toast } from 'react-toastify';
import { BookOpen, Calendar, Clock, AlertCircle } from 'lucide-react';
import api from '@/services/api';

interface BorrowingItem {
  id: string;
  bookTitle: string;
  isbn: string;
  borrowDate: string;
  dueDate: string;
  returnDate?: string;
  status: 'BORROWED' | 'RETURNED' | 'OVERDUE';
  renewalCount: number;
}

export const BorrowedBooks: React.FC = () => {
  const [borrowings, setBorrowings] = useState<BorrowingItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const fetchBorrowings = async () => {
    setIsLoading(true);
    try {
      const response = await api.get('/borrowings').catch(() => null);
      if (response && response.data?.data) {
        setBorrowings(response.data.data);
      } else {
        // Fallback mockup
        setBorrowings([
          { id: 'b1', bookTitle: 'Clean Code', isbn: '978-0132350884', borrowDate: '2026-07-01T10:00:00', dueDate: '2026-07-15T10:00:00', status: 'BORROWED', renewalCount: 0 },
          { id: 'b2', bookTitle: 'Introduction to Algorithms', isbn: '978-0262033848', borrowDate: '2026-06-10T09:00:00', dueDate: '2026-06-24T09:00:00', returnDate: '2026-06-22T14:00:00', status: 'RETURNED', renewalCount: 1 },
          { id: 'b3', bookTitle: 'Design Patterns', isbn: '978-0201633610', borrowDate: '2026-06-15T11:00:00', dueDate: '2026-06-29T11:00:00', status: 'OVERDUE', renewalCount: 0 },
        ]);
      }
    } catch (e) {
      toast.error('Failed to load checkouts');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchBorrowings();
  }, []);

  const handleReturn = async (borrowingId: string) => {
    try {
      await api.post(`/borrowings/${borrowingId}/return`);
      toast.success('Book returned successfully!');
      fetchBorrowings();
    } catch (e: any) {
      toast.error(e.response?.data?.message || 'Error processing return');
    }
  };

  const handleRenew = async (borrowingId: string) => {
    try {
      await api.post(`/borrowings/${borrowingId}/renew`);
      toast.success('Check-out period extended by 14 days');
      fetchBorrowings();
    } catch (e: any) {
      toast.error(e.response?.data?.message || 'Error processing renewal');
    }
  };

  const columns = [
    { header: 'Book Title', accessor: (row: BorrowingItem) => (
      <div class="flex items-center space-x-3">
        <BookOpen class="h-5 w-5 text-slate-400" />
        <div>
          <p class="font-medium text-slate-800 dark:text-slate-200">{row.bookTitle}</p>
          <span class="text-xs text-slate-400">{row.isbn}</span>
        </div>
      </div>
    ) },
    { header: 'Borrow Date', accessor: (row: BorrowingItem) => <span>{new Date(row.borrowDate).toLocaleDateString()}</span> },
    { header: 'Due Date', accessor: (row: BorrowingItem) => <span>{new Date(row.dueDate).toLocaleDateString()}</span> },
    { header: 'Status', accessor: (row: BorrowingItem) => {
      const styles = {
        BORROWED: 'bg-blue-50 text-blue-700 dark:bg-blue-950/20 dark:text-blue-400',
        RETURNED: 'bg-green-50 text-green-700 dark:bg-green-950/20 dark:text-green-400',
        OVERDUE: 'bg-red-50 text-red-700 dark:bg-red-950/20 dark:text-red-400',
      };
      return <span class={`px-2.5 py-1 rounded-md text-xs font-semibold uppercase ${styles[row.status]}`}>{row.status}</span>;
    } },
    { header: 'Renewals', accessor: (row: BorrowingItem) => <span>{row.renewalCount} / 2</span> },
    { header: 'Actions', accessor: (row: BorrowingItem) => (
      row.status !== 'RETURNED' ? (
        <div class="flex items-center space-x-2">
          <Button variant="outline" size="sm" className="px-3 py-1.5 text-xs" onClick={() => handleRenew(row.id)}>Renew</Button>
          <Button variant="primary" size="sm" className="px-3 py-1.5 text-xs" onClick={() => handleReturn(row.id)}>Return</Button>
        </div>
      ) : (
        <span class="text-xs text-slate-400">Transaction completed</span>
      )
    ) },
  ];

  return (
    <div class="space-y-6">
      <div>
        <h1 class="text-2xl font-bold">My Active Checkouts</h1>
        <p class="text-slate-500 text-sm">Manage borrowing renewals, check active deadlines, and review return receipts</p>
      </div>

      <Card class="p-6">
        {isLoading ? (
          <div class="py-12 flex justify-center"><div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div></div>
        ) : (
          <Table columns={columns} data={borrowings} />
        )}
      </Card>
    </div>
  );
};
