export type Role = 'ADMIN' | 'LIBRARIAN' | 'FACULTY' | 'STUDENT';

export interface User {
  id: string;
  username: string;
  email: string;
  role: Role;
  profile?: Profile;
}

export interface Profile {
  firstName: string;
  lastName: string;
  libraryCardNumber: string;
  department?: string;
  preferences?: string;
}

export interface Book {
  id: string;
  title: string;
  isbn: string;
  description?: string;
  publisher?: string;
  category?: string;
  coverImageUrl?: string;
  totalCopies: number;
  availableCopies: number;
}

export interface Borrowing {
  id: string;
  userId: string;
  bookId: string;
  bookTitle?: string;
  borrowDate: string;
  dueDate: string;
  returnDate?: string;
  status: 'BORROWED' | 'RETURNED' | 'OVERDUE';
}

export interface Reservation {
  id: string;
  userId: string;
  bookId: string;
  bookTitle?: string;
  reservationDate: string;
  status: 'PENDING' | 'FULFILLED' | 'CANCELLED';
}

export interface Notification {
  id: string;
  title: string;
  message: string;
  read: boolean;
  createdAt: string;
}

export interface SystemMetrics {
  totalBooks: number;
  activeBorrowings: number;
  overdueBooks: number;
  pendingReservations: number;
  monthlyBorrowTrends: { labels: string[]; data: number[] };
  categoryDistribution: { labels: string[]; data: number[] };
}
