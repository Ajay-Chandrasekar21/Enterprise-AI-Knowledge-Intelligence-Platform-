import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface ChatCitation {
  citationKey: string;
  bookId: string;
  bookTitle: string;
  pageNumber: number;
  textSegment: string;
}

export interface ChatMessage {
  id: string;
  sender: 'USER' | 'AGENT';
  agentId?: string;
  content: string;
  citations?: ChatCitation[];
  timestamp: string;
}

export interface AgentStep {
  step: number;
  agentName: string;
  status: 'PENDING' | 'EXECUTING' | 'COMPLETED' | 'FAILED';
  outputDetails?: string;
}

interface AiState {
  chatSessions: Record<string, ChatMessage[]>;
  activeSessionId: string | null;
  activeSteps: AgentStep[];
  isThinking: boolean;
  confidenceScore: number;
}

const initialState: AiState = {
  chatSessions: {},
  activeSessionId: null,
  activeSteps: [],
  isThinking: false,
  confidenceScore: 1.0,
};

const aiSlice = createSlice({
  name: 'ai',
  initialState,
  reducers: {
    startNewSession(state, action: PayloadAction<string>) {
      state.activeSessionId = action.payload;
      state.chatSessions[action.payload] = [];
      state.activeSteps = [];
      state.confidenceScore = 1.0;
    },
    addMessageToSession(state, action: PayloadAction<{ sessionId: string; message: ChatMessage }>) {
      const { sessionId, message } = action.payload;
      if (!state.chatSessions[sessionId]) {
        state.chatSessions[sessionId] = [];
      }
      state.chatSessions[sessionId].push(message);
    },
    setThinking(state, action: PayloadAction<boolean>) {
      state.isThinking = action.payload;
    },
    setAgentSteps(state, action: PayloadAction<AgentStep[]>) {
      state.activeSteps = action.payload;
    },
    updateAgentStep(state, action: PayloadAction<AgentStep>) {
      const idx = state.activeSteps.findIndex((s) => s.step === action.payload.step);
      if (idx !== -1) {
        state.activeSteps[idx] = action.payload;
      } else {
        state.activeSteps.push(action.payload);
      }
    },
    setConfidenceScore(state, action: PayloadAction<number>) {
      state.confidenceScore = action.payload;
    },
  },
});

export const {
  startNewSession,
  addMessageToSession,
  setThinking,
  setAgentSteps,
  updateAgentStep,
  setConfidenceScore,
} = aiSlice.actions;
export default aiSlice.reducer;
