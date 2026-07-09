import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { SystemMetrics } from '@/types';

interface AnalyticsState {
  metrics: SystemMetrics | null;
  isLoading: boolean;
  error: string | null;
}

const initialState: AnalyticsState = {
  metrics: null,
  isLoading: false,
  error: null,
};

const analyticsSlice = createSlice({
  name: 'analytics',
  initialState,
  reducers: {
    setMetrics(state, action: PayloadAction<SystemMetrics>) {
      state.metrics = action.payload;
    },
    setLoading(state, action: PayloadAction<boolean>) {
      state.isLoading = action.payload;
    },
    setError(state, action: PayloadAction<string | null>) {
      state.error = action.payload;
    },
  },
});

export const { setMetrics, setLoading, setError } = analyticsSlice.actions;
export default analyticsSlice.reducer;
