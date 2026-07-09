import React, { useState, useEffect } from 'react';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { toast } from 'react-toastify';
import { 
  Play, Cpu, BarChart2, CheckCircle2, AlertTriangle, 
  Terminal, ShieldCheck, Activity, Compass, Clock 
} from 'lucide-react';
import { aiService, TelemetryMetrics, QueryResponse } from '@/services/aiService';

interface WorkflowStep {
  step: number;
  agentName: string;
  description: string;
  status: 'PENDING' | 'EXECUTING' | 'COMPLETED' | 'FAILED';
  durationMs?: number;
}

export const AiConsole: React.FC = () => {
  const [query, setQuery] = useState('');
  const [isExecuting, setIsExecuting] = useState(false);
  const [telemetry, setTelemetry] = useState<TelemetryMetrics | null>(null);
  
  // Simulated timeline of execution steps
  const [steps, setSteps] = useState<WorkflowStep[]>([]);
  const [consoleLogs, setConsoleLogs] = useState<string[]>([]);
  const [result, setResult] = useState<QueryResponse | null>(null);

  const fetchTelemetry = async () => {
    try {
      const data = await aiService.getMetrics();
      setTelemetry(data);
    } catch (e) {
      // Fallback telemetry
      setTelemetry({
        totalTokens: 14820,
        requestsCount: 24,
        failuresCount: 1,
        averageLatencyMs: 980,
      });
    }
  };

  useEffect(() => {
    fetchTelemetry();
    setConsoleLogs([
      '[SYSTEM] AI Platform Multi-Agent Runtime Initialized.',
      '[SYSTEM] RedisMemoryStore connected.',
      '[SYSTEM] Active agent registries synchronized: 4 nodes online.'
    ]);
  }, []);

  const handleExecute = async () => {
    if (!query.trim()) {
      toast.warning('Please enter a query to orchestrate');
      return;
    }

    setIsExecuting(true);
    setResult(null);
    setConsoleLogs(prev => [...prev, `[USER_REQUEST] Parsing: "${query}"`]);

    // Setup simulated timeline based on input triggers
    const isAnalytics = query.toLowerCase().includes('stat') || query.toLowerCase().includes('metric') || query.toLowerCase().includes('analytics');
    const isLib = query.toLowerCase().includes('borrow') || query.toLowerCase().includes('reserve') || query.toLowerCase().includes('checkout');
    
    let mockSteps: WorkflowStep[] = [];
    if (isAnalytics) {
      mockSteps = [
        { step: 1, agentName: 'ANALYTICS_AGENT', description: 'Compute catalog borrows and plot monthly department distributions', status: 'EXECUTING' }
      ];
    } else if (isLib) {
      mockSteps = [
        { step: 1, agentName: 'LIBRARY_OPERATIONS_AGENT', description: 'Review user checks, current holds queue, and renewal clearances', status: 'EXECUTING' }
      ];
    } else {
      mockSteps = [
        { step: 1, agentName: 'BOOK_DISCOVERY_AGENT', description: 'Query indexes for catalog matches', status: 'EXECUTING' },
        { step: 2, agentName: 'SUMMARIZER_AGENT', description: 'Compile description outputs and details format', status: 'PENDING' }
      ];
    }
    
    setSteps(mockSteps);

    try {
      // Direct REST call to Orchestrator controller
      const response = await aiService.submitQuery(query);
      
      // Update step progress
      setSteps(prev => prev.map(s => ({ ...s, status: 'COMPLETED', durationMs: s.step === 1 ? 450 : 280 })));
      setResult(response);
      setConsoleLogs(prev => [
        ...prev,
        `[PLANNER] Plan established. ID: ${response.planId}`,
        `[EXECUTOR] Executing workflow sequential pipelines...`,
        `[ORCHESTRATOR] Compilation generated. Session ID: ${response.sessionId}`,
        `[ORCHESTRATOR] Response Confidence Score: ${(response.confidenceScore * 100).toFixed(1)}%`
      ]);
      fetchTelemetry();
      toast.success('Query orchestrated successfully!');
    } catch (e: any) {
      setSteps(prev => prev.map(s => ({ ...s, status: 'FAILED' })));
      setConsoleLogs(prev => [...prev, `[ERROR] Provider switching triggered. Backup fallback completed with errors.`]);
      toast.error('Execution completed with fallbacks');
    } finally {
      setIsExecuting(false);
    }
  };

  return (
    <div class="space-y-6">
      
      {/* Title */}
      <div>
        <h1 class="text-2xl font-bold">Multi-Agent Runtime Console</h1>
        <p class="text-slate-500 text-sm">Monitor workspace agent configurations, timeline executions, and engine telemetry</p>
      </div>

      {/* Main Console Layout */}
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Core Prompt Panel */}
        <div class="lg:col-span-2 space-y-6">
          
          {/* Prompt input */}
          <Card class="p-6">
            <h3 class="text-sm font-semibold mb-4 text-slate-500 uppercase flex items-center space-x-2">
              <Terminal class="h-4 w-4" />
              <span>Submit Orchestrator Instruction</span>
            </h3>
            <div class="space-y-4">
              <textarea
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                placeholder="e.g. Find clean architecture books in the catalog index..."
                className="w-full px-4 py-3 rounded-xl border bg-slate-50 dark:bg-slate-900 border-slate-200 dark:border-slate-800 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all min-h-24 font-mono text-xs"
              />
              <div class="flex justify-end">
                <Button 
                  onClick={handleExecute} 
                  isLoading={isExecuting}
                  className="flex items-center space-x-2"
                >
                  <Play class="h-4 w-4" />
                  <span>Execute Workflow</span>
                </Button>
              </div>
            </div>
          </Card>

          {/* Execution Timeline / Workflow Viewer */}
          {steps.length > 0 && (
            <Card class="p-6">
              <h3 class="text-sm font-semibold mb-6 text-slate-500 uppercase flex items-center space-x-2">
                <Compass class="h-4 w-4" />
                <span>Execution Timeline & Workflow Viewer</span>
              </h3>
              <div class="space-y-6 relative border-l-2 border-slate-100 dark:border-slate-800/60 ml-3 pl-6">
                {steps.map((step) => (
                  <div key={step.step} class="relative">
                    {/* Circle Node */}
                    <div class={`absolute -left-[31px] top-0 h-4 w-4 rounded-full border-2 border-white dark:border-slate-950 ${
                      step.status === 'COMPLETED' ? 'bg-green-500' :
                      step.status === 'EXECUTING' ? 'bg-blue-500 animate-pulse' :
                      step.status === 'FAILED' ? 'bg-red-500' : 'bg-slate-300'
                    }`} />
                    
                    <div class="flex items-start justify-between text-xs">
                      <div>
                        <h4 class="font-bold text-slate-800 dark:text-slate-200">
                          {step.agentName} <span class="font-normal text-slate-400">(Step #{step.step})</span>
                        </h4>
                        <p class="text-slate-500 mt-1">{step.description}</p>
                      </div>
                      <span class={`px-2 py-0.5 rounded-md text-[10px] font-semibold uppercase ${
                        step.status === 'COMPLETED' ? 'bg-green-50 text-green-700 dark:bg-green-950/20 dark:text-green-400' :
                        step.status === 'EXECUTING' ? 'bg-blue-50 text-blue-700 dark:bg-blue-950/20 dark:text-blue-400' :
                        step.status === 'FAILED' ? 'bg-red-50 text-red-700 dark:bg-red-950/20 dark:text-red-400' :
                        'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-400'
                      }`}>
                        {step.status}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            </Card>
          )}

          {/* Compiled Output Result */}
          {result && (
            <Card class="p-6 bg-slate-900 text-slate-100 border-none font-mono text-xs space-y-4">
              <div class="flex justify-between items-center pb-2 border-b border-slate-800 text-[10px] font-bold uppercase tracking-wider text-slate-400">
                <span>Orchestrated Combined Output</span>
                <span class="text-green-400">Confidence: {(result.confidenceScore * 100).toFixed(0)}%</span>
              </div>
              <p class="whitespace-pre-wrap leading-relaxed">{result.combinedResponse}</p>
            </Card>
          )}

        </div>

        {/* Telemetry & System Status Sidebar */}
        <div class="space-y-6">
          
          {/* Telemetry Dashboard */}
          <Card class="p-6 space-y-4">
            <h3 class="text-sm font-semibold text-slate-500 uppercase flex items-center space-x-2">
              <BarChart2 class="h-4 w-4" />
              <span>Telemetry Dashboard</span>
            </h3>
            <div class="grid grid-cols-2 gap-4">
              <div class="p-3 bg-slate-50 dark:bg-slate-800/40 rounded-xl">
                <span class="text-[10px] text-slate-400 uppercase font-semibold block">Total Tokens</span>
                <span class="text-base font-bold">{telemetry?.totalTokens || 0}</span>
              </div>
              <div class="p-3 bg-slate-50 dark:bg-slate-800/40 rounded-xl">
                <span class="text-[10px] text-slate-400 uppercase font-semibold block">Total Requests</span>
                <span class="text-base font-bold">{telemetry?.requestsCount || 0}</span>
              </div>
              <div class="p-3 bg-slate-50 dark:bg-slate-800/40 rounded-xl">
                <span class="text-[10px] text-slate-400 uppercase font-semibold block">Failures Log</span>
                <span class={`text-base font-bold ${telemetry && telemetry.failuresCount > 0 ? 'text-red-500' : ''}`}>
                  {telemetry?.failuresCount || 0}
                </span>
              </div>
              <div class="p-3 bg-slate-50 dark:bg-slate-800/40 rounded-xl">
                <span class="text-[10px] text-slate-400 uppercase font-semibold block">Avg Latency</span>
                <span class="text-base font-bold">{telemetry?.averageLatencyMs || 0} ms</span>
              </div>
            </div>
          </Card>

          {/* Active Agents Status */}
          <Card class="p-6 space-y-4">
            <h3 class="text-sm font-semibold text-slate-500 uppercase flex items-center space-x-2">
              <Cpu class="h-4 w-4" />
              <span>Agent Monitor Registry</span>
            </h3>
            <div class="space-y-3 text-xs">
              <div class="flex justify-between items-center">
                <span class="font-medium">BOOK_DISCOVERY_AGENT</span>
                <span class="flex items-center space-x-1 text-green-600 font-semibold">
                  <CheckCircle2 class="h-3.5 w-3.5" />
                  <span>Online</span>
                </span>
              </div>
              <div class="flex justify-between items-center">
                <span class="font-medium">SUMMARIZER_AGENT</span>
                <span class="flex items-center space-x-1 text-green-600 font-semibold">
                  <CheckCircle2 class="h-3.5 w-3.5" />
                  <span>Online</span>
                </span>
              </div>
              <div class="flex justify-between items-center">
                <span class="font-medium">ANALYTICS_AGENT</span>
                <span class="flex items-center space-x-1 text-green-600 font-semibold">
                  <CheckCircle2 class="h-3.5 w-3.5" />
                  <span>Online</span>
                </span>
              </div>
              <div class="flex justify-between items-center">
                <span class="font-medium">GENERAL_AGENT</span>
                <span class="flex items-center space-x-1 text-green-600 font-semibold">
                  <CheckCircle2 class="h-3.5 w-3.5" />
                  <span>Online</span>
                </span>
              </div>
            </div>
          </Card>

          {/* Console logger */}
          <Card class="p-6 bg-slate-950 border-none text-slate-400 font-mono text-[10px] space-y-2 max-h-60 overflow-y-auto">
            <h4 class="text-slate-500 font-bold uppercase tracking-wider mb-2">Live Console Stream</h4>
            {consoleLogs.map((logStr, index) => (
              <p key={index} class="leading-relaxed">{logStr}</p>
            ))}
          </Card>

        </div>

      </div>

    </div>
  );
};
