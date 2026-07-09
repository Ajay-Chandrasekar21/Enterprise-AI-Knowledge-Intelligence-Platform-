import api from './api';

export interface QueryResponse {
  sessionId: string;
  planId: string;
  combinedResponse: string;
  confidenceScore: number;
}

export interface TelemetryMetrics {
  totalTokens: number;
  requestsCount: number;
  failuresCount: number;
  averageLatencyMs: number;
}

export const aiService = {
  submitQuery: async (query: string): Promise<QueryResponse> => {
    const response = await api.post('/ai/query', { query });
    return response.data.data;
  },

  getMetrics: async (): Promise<TelemetryMetrics> => {
    const response = await api.get('/ai/metrics');
    return response.data.data;
  },
};
export default aiService;
