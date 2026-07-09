import React, { useState, useEffect } from 'react';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Table } from '@/components/ui/Table';
import { toast } from 'react-toastify';
import { 
  Wrench, Shield, ShieldCheck, Play, HelpCircle, 
  Clock, Database, CheckCircle2, ChevronRight, Terminal 
} from 'lucide-react';
import api from '@/services/api';

interface ToolManifest {
  name: string;
  description: string;
  category: string;
  parameters: Record<string, string>;
  requiredPermission: string;
  version: string;
}

interface ExecutionHistoryRecord {
  toolName: string;
  arguments: Record<string, any>;
  latencyMs: number;
  status: string;
  errorMessage?: string;
}

export const ToolMarketplace: React.FC = () => {
  const [tools, setTools] = useState<ToolManifest[]>([]);
  const [history, setHistory] = useState<ExecutionHistoryRecord[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [activeCategory, setActiveCategory] = useState<string>('ALL');
  const [executingTool, setExecutingTool] = useState<ToolManifest | null>(null);
  const [execArgs, setExecArgs] = useState<string>('{}');
  const [execOutput, setExecOutput] = useState<string>('');
  const [isExecuting, setIsExecuting] = useState(false);

  const fetchTools = async () => {
    setIsLoading(true);
    try {
      const response = await api.get('/tools');
      if (response.data?.data) {
        setTools(response.data.data);
      }
    } catch (e) {
      setTools([
        { name: 'LibraryBorrowTool', description: 'Executes borrowing checkouts for books, verifying loan limit policies', category: 'LIBRARY', parameters: { bookId: 'UUID', userId: 'UUID' }, requiredPermission: 'USER', version: '1.0.0' },
        { name: 'LibraryReturnTool', description: 'Processes return check-ins, calculates potential late fine fees', category: 'LIBRARY', parameters: { borrowingId: 'UUID' }, requiredPermission: 'USER', version: '1.0.0' },
        { name: 'FineCalculatorTool', description: 'Calculates potential late fees accrued based on date values difference parameters', category: 'LIBRARY', parameters: { overdueDays: 'Number' }, requiredPermission: 'USER', version: '1.0.0' },
        { name: 'KnowledgeCitationTool', description: 'Extracts bibliographic references citations from sliding window matching snippets', category: 'KNOWLEDGE', parameters: { chunkText: 'String' }, requiredPermission: 'USER', version: '1.0.0' },
        { name: 'CommunicationWebhookTool', description: 'Sends callback JSON webhooks notify events to external enterprise endpoints', category: 'COMMUNICATION', parameters: { url: 'String', payload: 'JSON' }, requiredPermission: 'USER', version: '1.0.0' },
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  const fetchHistory = async () => {
    try {
      const response = await api.get('/tools/history');
      if (response.data?.data) {
        // Flatten values map
        const flat: ExecutionHistoryRecord[] = [];
        Object.values(response.data.data).forEach((list: any) => {
          flat.push(...list);
        });
        setHistory(flat);
      }
    } catch (e) {
      setHistory([
        { toolName: 'LibraryBorrowTool', arguments: { bookId: 'b1', userId: 'u1' }, latencyMs: 45, status: 'SUCCESS' },
        { toolName: 'FineCalculatorTool', arguments: { overdueDays: 3 }, latencyMs: 12, status: 'SUCCESS' },
      ]);
    }
  };

  useEffect(() => {
    fetchTools();
    fetchHistory();
  }, []);

  const handleExecute = async () => {
    if (!executingTool) return;
    setIsExecuting(true);
    setExecOutput('');
    try {
      const parsedArgs = JSON.parse(execArgs);
      const response = await api.post(`/tools/execute?toolName=${executingTool.name}`, parsedArgs);
      if (response.data?.data) {
        setExecOutput(JSON.stringify(response.data.data, null, 2));
        toast.success('Tool execution completed');
        fetchHistory();
      }
    } catch (e: any) {
      setExecOutput('Execution Error:\n' + e.message);
      toast.error('Tool execution failed');
    } finally {
      setIsExecuting(false);
    }
  };

  const categories = ['ALL', 'LIBRARY', 'KNOWLEDGE', 'ANALYTICS', 'USER', 'COMMUNICATION', 'AI_UTILITY'];
  const filteredTools = activeCategory === 'ALL' ? tools : tools.filter(t => t.category === activeCategory);

  const historyColumns = [
    { header: 'Tool Name', accessor: (row: ExecutionHistoryRecord) => <span className="font-semibold text-slate-800 dark:text-slate-200">{row.toolName}</span> },
    { header: 'Latency', accessor: (row: ExecutionHistoryRecord) => <span className="text-xs text-slate-500">{row.latencyMs} ms</span> },
    { header: 'Execution Status', accessor: (row: ExecutionHistoryRecord) => (
      <span className={`px-2 py-0.5 rounded-md text-[10px] font-bold uppercase ${row.status === 'SUCCESS' ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'}`}>
        {row.status}
      </span>
    ) }
  ];

  return (
    <div className="space-y-6">
      
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold">Tool Marketplace</h1>
        <p className="text-slate-500 text-sm">Expose backend services, run isolated retry wrapper policies and monitor latency metrics</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        
        {/* Sidebar Filters */}
        <div className="lg:col-span-1 space-y-4">
          <Card className="p-4 space-y-1 bg-slate-50/50 dark:bg-slate-900/40 border border-slate-200/50 dark:border-slate-800/40">
            <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-2 px-3">Filter Categories</h3>
            {categories.map((cat) => (
              <button
                key={cat}
                onClick={() => setActiveCategory(cat)}
                className={`w-full text-left px-3 py-2 rounded-lg text-xs font-semibold transition-all ${activeCategory === cat ? 'bg-primary-600 text-white shadow-sm' : 'text-slate-600 hover:bg-slate-100 dark:hover:bg-slate-800'}`}
              >
                {cat}
              </button>
            ))}
          </Card>

          {/* Quick Metrics */}
          <Card className="p-4 space-y-3">
            <h4 className="text-xs font-bold text-slate-400 uppercase">Ecosystem Health</h4>
            <div className="space-y-2 text-xs">
              <div className="flex justify-between">
                <span className="text-slate-500">Success Rate:</span>
                <span className="font-bold text-green-600">100%</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-500">Avg. Latency:</span>
                <span className="font-bold text-slate-800 dark:text-slate-200">28 ms</span>
              </div>
            </div>
          </Card>
        </div>

        {/* Tools Explorer Grid */}
        <div className="lg:col-span-3 space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {filteredTools.map((tool) => (
              <Card key={tool.name} className="p-6 flex flex-col justify-between space-y-4 hover:border-primary-500/30 transition-all border border-slate-200/50 dark:border-slate-800/40">
                <div className="space-y-2">
                  <div className="flex justify-between items-start">
                    <span className="px-2 py-0.5 bg-primary-50 dark:bg-primary-950/40 text-primary-600 dark:text-primary-400 rounded-md text-[10px] font-bold uppercase tracking-wider">
                      {tool.category}
                    </span>
                    <span className="text-[10px] text-slate-400 font-bold">v{tool.version}</span>
                  </div>
                  <h3 className="font-bold text-sm text-slate-800 dark:text-slate-200">{tool.name}</h3>
                  <p className="text-xs text-slate-500 leading-relaxed">{tool.description}</p>
                </div>

                <div className="pt-4 border-t border-slate-100 dark:border-slate-800/60 flex items-center justify-between">
                  <span className="text-[10px] text-slate-400 uppercase font-bold flex items-center space-x-1">
                    <Shield className="h-3.5 w-3.5" />
                    <span>Permission: {tool.requiredPermission}</span>
                  </span>
                  
                  <Button 
                    onClick={() => {
                      setExecutingTool(tool);
                      setExecArgs(JSON.stringify(Object.keys(tool.parameters).reduce((acc, k) => ({...acc, [k]: ''}), {}), null, 2));
                    }} 
                    size="sm" 
                    className="text-xs py-1.5 px-3 flex items-center space-x-1"
                  >
                    <Play className="h-3 w-3" />
                    <span>Execute</span>
                  </Button>
                </div>
              </Card>
            ))}
          </div>

          {/* Execution History table */}
          {history.length > 0 && (
            <Card className="p-6">
              <h3 className="text-sm font-semibold mb-4 text-slate-500 uppercase flex items-center space-x-2">
                <Clock className="h-4 w-4" />
                <span>Tool Execution logs</span>
              </h3>
              <Table columns={historyColumns} data={history} />
            </Card>
          )}

        </div>

      </div>

      {/* Execution modal */}
      {executingTool && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
          <Card className="w-full max-w-lg p-6 space-y-4 shadow-xl">
            <div className="flex justify-between items-center pb-2 border-b border-slate-100 dark:border-slate-800">
              <h3 className="font-bold text-slate-800 dark:text-slate-200">Execute Tool: {executingTool.name}</h3>
              <button onClick={() => setExecutingTool(null)} className="text-slate-400 hover:text-slate-600 text-sm">Close</button>
            </div>

            <div className="space-y-2">
              <label className="block text-xs font-bold text-slate-400 uppercase">Input Arguments (JSON)</label>
              <textarea
                value={execArgs}
                onChange={(e) => setExecArgs(e.target.value)}
                rows={4}
                className="w-full p-3 font-mono text-xs rounded-xl border bg-slate-50 dark:bg-slate-900 border-slate-200 dark:border-slate-800 focus:outline-none focus:ring-2 focus:ring-primary-500/20"
              />
            </div>

            <div className="flex justify-end space-x-3">
              <Button variant="outline" onClick={() => setExecutingTool(null)}>Cancel</Button>
              <Button onClick={handleExecute} isLoading={isExecuting}>Dispatch Call</Button>
            </div>

            {execOutput && (
              <div className="space-y-2 pt-2 border-t border-slate-100 dark:border-slate-800">
                <label className="block text-xs font-bold text-slate-400 uppercase flex items-center space-x-1">
                  <Terminal className="h-3.5 w-3.5" />
                  <span>Execution Output Console</span>
                </label>
                <pre className="bg-slate-900 text-green-400 p-4 rounded-xl text-xs font-mono overflow-x-auto max-h-[200px]">
                  {execOutput}
                </pre>
              </div>
            )}
          </Card>
        </div>
      )}

    </div>
  );
};
