import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { Book, Borrowing, Reservation } from '@/types';

interface BookState {
  books: Book[];
  borrowings: Borrowing[];
  reservations: Reservation[];
  searchQuery: string;
  isLoading: boolean;
  error: string | null;
}

const initialState: BookState = {
  books: [],
  borrowings: [],
  reservations: [],
  searchQuery: '',
  isLoading: false,
  error: null,
};

const bookSlice = createSlice({
  name: 'books',
  initialState,
  reducers: {
    setBooks(state, action: PayloadAction<Book[]>) {
      state.books = action.payload;
    },
    setBorrowings(state, action: PayloadAction<Borrowing[]>) {
      state.borrowings = action.payload;
    },
    setReservations(state, action: PayloadAction<Reservation[]>) {
      state.reservations = action.payload;
    },
    setSearchQuery(state, action: PayloadAction<String | any>) {
      state.searchQuery = action.payload;
    },
    setLoading(state, action: PayloadAction<boolean>) {
      state.isLoading = action.payload;
    },
    setError(state, action: PayloadAction<string | null>) {
      state.error = action.payload;
    },
  },
});

export const { setBooks, setBorrowings, setReservations, setSearchQuery, setLoading, setError } = bookSlice.actions;
export default bookSlice.reducer;
