import React, { useState, useEffect } from 'react';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Table } from '@/components/ui/Table';
import { toast } from 'react-toastify';
import { 
  Server, Link2, Activity, Play, Plus, Trash2, 
  Settings, Key, AlertTriangle, ShieldCheck 
} from 'lucide-react';
import api from '@/services/api';

interface McpServerConfig {
  serverName: string;
  endpointUrl: string;
  status: 'CONNECTED' | 'DISCONNECTED' | 'DEGRADED';
  latencyMs: number;
  authType: 'OAUTH2' | 'API_KEY' | 'JWT';
  apiKey?: string;
}

interface McpToolSchema {
  name: string;
  description: string;
  parameters: Record<string, string>;
}

export const McpDashboard: React.FC = () => {
  const [servers, setServers] = useState<McpServerConfig[]>([]);
  const [discoveredTools, setDiscoveredTools] = useState<McpToolSchema[]>([]);
  const [selectedServer, setSelectedServer] = useState<string>('');
  
  // Registration Form
  const [serverName, setServerName] = useState('');
  const [endpointUrl, setEndpointUrl] = useState('');
  const [authType, setAuthType] = useState<'OAUTH2' | 'API_KEY' | 'JWT'>('API_KEY');
  const [apiKey, setApiKey] = useState('');
  const [isRegistering, setIsRegistering] = useState(false);

  // Execution
  const [testingTool, setTestingTool] = useState<McpToolSchema | null>(null);
  const [testArgs, setTestArgs] = useState('{}');
  const [execResult, setExecResult] = useState('');
  const [isExecuting, setIsExecuting] = useState(false);

  const fetchServers = async () => {
    try {
      const response = await api.get('/mcp/servers');
      if (response.data?.data) {
        setServers(response.data.data);
      }
    } catch (e) {
      setServers([
        { serverName: 'GitHub_MCP', endpointUrl: 'http://localhost:8088/mcp/github', status: 'CONNECTED', latencyMs: 38, authType: 'API_KEY' },
        { serverName: 'Jira_MCP', endpointUrl: 'http://localhost:8088/mcp/jira', status: 'CONNECTED', latencyMs: 42, authType: 'JWT' },
        { serverName: 'Slack_MCP', endpointUrl: 'http://localhost:8088/mcp/slack', status: 'DEGRADED', latencyMs: 120, authType: 'OAUTH2' },
      ]);
    }
  };

  useEffect(() => {
    fetchServers();
  }, []);

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!serverName || !endpointUrl) {
      toast.warning('Please input server parameters');
      return;
    }
    setIsRegistering(true);
    try {
      await api.post('/mcp/servers', { serverName, endpointUrl, authType, apiKey });
      toast.success('External MCP Server connected successfully');
      setServerName('');
      setEndpointUrl('');
      setApiKey('');
      fetchServers();
    } catch (err) {
      toast.error('Failed to connect to MCP endpoint host');
    } finally {
      setIsRegistering(false);
    }
  };

  const handleRemove = async (name: string) => {
    try {
      await api.delete(`/mcp/servers?serverName=${name}`);
      toast.success('MCP server unregistered');
      fetchServers();
    } catch (err) {
      toast.error('Failed to remove server');
    }
  };

  const handleDiscoverTools = async (name: string) => {
    setSelectedServer(name);
    try {
      const response = await api.get(`/mcp/tools?serverName=${name}`);
      if (response.data?.data) {
        setDiscoveredTools(response.data.data);
      }
    } catch (e) {
      if (name.toLowerCase().includes('github')) {
        setDiscoveredTools([
          { name: 'createIssue', description: 'Creates a GitHub issue in the target repository', parameters: { repo: 'String', title: 'String' } },
          { name: 'listPRs', description: 'Lists open pull requests', parameters: { repo: 'String' } }
        ]);
      } else {
        setDiscoveredTools([
          { name: 'queryData', description: 'Executes data query operations', parameters: { query: 'String' } }
        ]);
      }
    }
  };

  const handleExecute = async () => {
    if (!testingTool || !selectedServer) return;
    setIsExecuting(true);
    setExecResult('');
    try {
      const parsedArgs = JSON.parse(testArgs);
      const response = await api.post(`/mcp/execute?serverName=${selectedServer}&toolName=${testingTool.name}`, parsedArgs);
      if (response.data?.data) {
        setExecResult(JSON.stringify(response.data.data, null, 2));
        toast.success('MCP Tool call completed successfully');
      }
    } catch (e: any) {
      setExecResult('JSON-RPC Execution Error:\n' + e.message);
      toast.error('MCP Tool call failed');
    } finally {
      setIsExecuting(false);
    }
  };

  const serverColumns = [
    { header: 'Server Name', accessor: (row: McpServerConfig) => (
      <div className="flex items-center space-x-3">
        <Server className="h-5 w-5 text-slate-400" />
        <span className="font-medium text-slate-800 dark:text-slate-200">{row.serverName}</span>
      </div>
    ) },
    { header: 'Endpoint', accessor: (row: McpServerConfig) => <span className="text-xs text-slate-400 font-mono">{row.endpointUrl}</span> },
    { header: 'Latency', accessor: (row: McpServerConfig) => <span className="text-xs text-slate-500">{row.latencyMs} ms</span> },
    { header: 'Status', accessor: (row: McpServerConfig) => {
      const colors = {
        CONNECTED: 'bg-green-50 text-green-700',
        DEGRADED: 'bg-orange-50 text-orange-700',
        DISCONNECTED: 'bg-red-50 text-red-700',
      };
      return <span className={`px-2 py-0.5 rounded-md text-[10px] font-bold uppercase ${colors[row.status]}`}>{row.status}</span>;
    } },
    { header: 'Actions', accessor: (row: McpServerConfig) => (
      <div className="flex items-center space-x-3">
        <Button variant="outline" size="sm" onClick={() => handleDiscoverTools(row.serverName)} className="text-[10px] py-1 px-2.5">
          Discover
        </Button>
        <button onClick={() => handleRemove(row.serverName)} className="text-red-500 hover:text-red-700">
          <Trash2 className="h-4 w-4" />
        </button>
      </div>
    ) }
  ];

  return (
    <div className="space-y-6">
      
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold">Model Context Protocol (MCP) Dashboard</h1>
        <p className="text-slate-500 text-sm">Register connection protocols, auto-discover remote server schemas, and execute distributed tools</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Servers table and widgets */}
        <div className="lg:col-span-2 space-y-6">
          <Card className="p-6">
            <h3 className="text-sm font-semibold mb-4 text-slate-500 uppercase flex items-center space-x-2">
              <Link2 className="h-4 w-4" />
              <span>Registered MCP Gateway Servers</span>
            </h3>
            <Table columns={serverColumns} data={servers} />
          </Card>

          {/* Tools discoverer */}
          {selectedServer && (
            <Card className="p-6 space-y-4">
              <h3 className="text-sm font-semibold text-slate-500 uppercase flex items-center space-x-2">
                <Activity className="h-4 w-4" />
                <span>Discovered Tool Capabilities on: {selectedServer}</span>
              </h3>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {discoveredTools.map((tool) => (
                  <Card key={tool.name} className="p-4 flex flex-col justify-between space-y-4 border border-slate-200/50 dark:border-slate-800/40">
                    <div className="space-y-1">
                      <h4 className="font-bold text-xs text-slate-800 dark:text-slate-200">{tool.name}</h4>
                      <p className="text-[10px] text-slate-400 leading-relaxed">{tool.description}</p>
                    </div>
                    <Button 
                      onClick={() => {
                        setTestingTool(tool);
                        setTestArgs(JSON.stringify(Object.keys(tool.parameters).reduce((acc, k) => ({...acc, [k]: ''}), {}), null, 2));
                      }} 
                      size="sm" 
                      className="text-[10px] py-1 px-3 w-fit"
                    >
                      Test Dispatch
                    </Button>
                  </Card>
                ))}
              </div>
            </Card>
          )}
        </div>

        {/* Server registration Form */}
        <div className="lg:col-span-1 space-y-6">
          <Card className="p-6">
            <h3 className="text-sm font-semibold mb-4 text-slate-500 uppercase flex items-center space-x-2">
              <Plus className="h-4 w-4" />
              <span>Connect MCP Host</span>
            </h3>

            <form onSubmit={handleRegister} className="space-y-4 text-xs font-semibold">
              <div className="space-y-1">
                <label className="block text-slate-400">Server Identifier Name</label>
                <input 
                  type="text" 
                  value={serverName} 
                  onChange={e => setServerName(e.target.value)} 
                  placeholder="e.g. GitHub_MCP"
                  className="w-full p-2.5 rounded-xl border bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-800 focus:outline-none focus:ring-2 focus:ring-primary-500/20"
                />
              </div>

              <div className="space-y-1">
                <label className="block text-slate-400">JSON-RPC / REST Endpoint URL</label>
                <input 
                  type="text" 
                  value={endpointUrl} 
                  onChange={e => setEndpointUrl(e.target.value)} 
                  placeholder="http://localhost:8088/mcp/github"
                  className="w-full p-2.5 rounded-xl border bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-800 focus:outline-none focus:ring-2 focus:ring-primary-500/20"
                />
              </div>

              <div className="space-y-1">
                <label className="block text-slate-400">Authentication Type</label>
                <select 
                  value={authType} 
                  onChange={e => setAuthType(e.target.value as any)} 
                  className="w-full p-2.5 rounded-xl border bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-800 focus:outline-none"
                >
                  <option value="API_KEY">API Key</option>
                  <option value="JWT">JWT</option>
                  <option value="OAUTH2">OAuth2</option>
                </select>
              </div>

              <div className="space-y-1">
                <label className="block text-slate-400 font-bold flex items-center space-x-1">
                  <Key className="h-3.5 w-3.5" />
                  <span>Credential Secrets Secret</span>
                </label>
                <input 
                  type="password" 
                  value={apiKey} 
                  onChange={e => setApiKey(e.target.value)} 
                  placeholder="Bearer token or API Key"
                  className="w-full p-2.5 rounded-xl border bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-800 focus:outline-none focus:ring-2 focus:ring-primary-500/20"
                />
              </div>

              <Button type="submit" isLoading={isRegistering} className="w-full py-2.5">
                Register Server
              </Button>
            </form>
          </Card>
        </div>

      </div>

      {/* Execution modal */}
      {testingTool && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
          <Card className="w-full max-w-lg p-6 space-y-4 shadow-xl">
            <div className="flex justify-between items-center pb-2 border-b border-slate-100 dark:border-slate-800">
              <h3 className="font-bold text-slate-800 dark:text-slate-200">Test MCP Tool: {testingTool.name}</h3>
              <button onClick={() => setTestingTool(null)} className="text-slate-400 hover:text-slate-600 text-sm">Close</button>
            </div>

            <div className="space-y-2">
              <label className="block text-xs font-bold text-slate-400 uppercase">Input Arguments (JSON)</label>
              <textarea
                value={testArgs}
                onChange={(e) => setTestArgs(e.target.value)}
                rows={4}
                className="w-full p-3 font-mono text-xs rounded-xl border bg-slate-50 dark:bg-slate-900 border-slate-200 dark:border-slate-800 focus:outline-none focus:ring-2 focus:ring-primary-500/20"
              />
            </div>

            <div className="flex justify-end space-x-3">
              <Button variant="outline" onClick={() => setTestingTool(null)}>Cancel</Button>
              <Button onClick={handleExecute} isLoading={isExecuting}>Call JSON-RPC</Button>
            </div>

            {execResult && (
              <div className="space-y-2 pt-2 border-t border-slate-100 dark:border-slate-800">
                <label className="block text-xs font-bold text-slate-400 uppercase flex items-center space-x-1">
                  <ShieldCheck className="h-3.5 w-3.5 text-green-500" />
                  <span>JSON-RPC Response Payload</span>
                </label>
                <pre className="bg-slate-900 text-green-400 p-4 rounded-xl text-xs font-mono overflow-x-auto max-h-[200px]">
                  {execResult}
                </pre>
              </div>
            )}
          </Card>
        </div>
      )}

    </div>
  );
};
