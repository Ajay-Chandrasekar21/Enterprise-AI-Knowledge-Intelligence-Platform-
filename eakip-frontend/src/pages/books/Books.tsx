import React, { useState, useEffect } from 'react';
import { Card } from '@/components/ui/Card';
import { Table } from '@/components/ui/Table';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Book, Search, Plus } from 'lucide-react';
import api from '@/services/api';

interface BookItem {
  id: string;
  title: string;
  isbn: string;
  category: string;
  totalCopies: number;
  availableCopies: number;
}

export const Books: React.FC = () => {
  const [books, setBooks] = useState<BookItem[]>([]);
  const [search, setSearch] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const fetchBooks = async () => {
    setIsLoading(true);
    try {
      // Stub data for local development if endpoint returns empty or fails
      const response = await api.get('/books/search', { params: { query: search } }).catch(() => null);
      if (response && response.data?.data) {
        setBooks(response.data.data.content || response.data.data);
      } else {
        setBooks([
          { id: '1', title: 'Clean Architecture', isbn: '978-0134494166', category: 'Software Engineering', totalCopies: 5, availableCopies: 3 },
          { id: '2', title: 'Introduction to Algorithms', isbn: '978-0262033848', category: 'Computer Science', totalCopies: 3, availableCopies: 0 },
          { id: '3', title: 'Design Patterns', isbn: '978-0201633610', category: 'Software Engineering', totalCopies: 4, availableCopies: 4 },
        ]);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchBooks();
  }, [search]);

  const columns = [
    { header: 'Title', accessor: (row: BookItem) => (
      <div class="flex items-center space-x-3">
        <Book class="h-5 w-5 text-slate-400" />
        <span class="font-medium text-slate-800 dark:text-slate-200">{row.title}</span>
      </div>
    ) },
    { header: 'ISBN', accessor: (row: BookItem) => <span>{row.isbn}</span> },
    { header: 'Category', accessor: (row: BookItem) => <span class="px-2 py-1 bg-slate-100 dark:bg-slate-800 rounded-md text-xs font-semibold">{row.category}</span> },
    { header: 'Available Copies', accessor: (row: BookItem) => (
      <span class={row.availableCopies > 0 ? 'text-green-600 font-semibold' : 'text-red-500 font-semibold'}>
        {row.availableCopies} / {row.totalCopies}
      </span>
    ) },
    { header: 'Actions', accessor: (row: BookItem) => (
      <div class="flex items-center space-x-2">
        <Button variant="outline" size="sm" className="py-1 px-3 text-xs" disabled={row.availableCopies === 0}>Borrow</Button>
        <Button variant="secondary" size="sm" className="py-1 px-3 text-xs">Reserve</Button>
      </div>
    ) },
  ];

  return (
    <div class="space-y-6">
      <div class="flex justify-between items-center">
        <div>
          <h1 class="text-2xl font-bold">Catalog Index</h1>
          <p class="text-slate-500 text-sm">Search database indices and manage catalog checkouts</p>
        </div>
        <Button className="flex items-center space-x-2">
          <Plus class="h-4 w-4" />
          <span>Add Book</span>
        </Button>
      </div>

      <Card class="p-6">
        <div class="flex items-center space-x-4 mb-6">
          <div class="relative flex-grow">
            <Search class="absolute left-3.5 top-3 h-5 w-5 text-slate-400" />
            <input
              type="text"
              placeholder="Search by title, author, or ISBN..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-11 pr-4 py-2.5 rounded-xl border bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-800 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all"
            />
          </div>
        </div>

        {isLoading ? (
          <div class="py-12 flex justify-center"><div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div></div>
        ) : (
          <Table columns={columns} data={books} />
        )}
      </Card>
    </div>
  );
};
