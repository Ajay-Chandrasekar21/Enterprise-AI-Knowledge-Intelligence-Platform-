import React, { useState, useEffect } from 'react';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Table } from '@/components/ui/Table';
import { toast } from 'react-toastify';
import { 
  GitPullRequest, Play, CheckCircle, Clock, 
  HelpCircle, Settings, Share2, Plus, AlertCircle 
} from 'lucide-react';
import api from '@/services/api';

interface WorkflowDefinition {
  id: string;
  name: string;
  triggerType: string;
  version: number;
  createdDate: string;
}

interface WorkflowInstance {
  id: string;
  status: 'RUNNING' | 'COMPLETED' | 'WAITING_APPROVAL' | 'FAILED';
  currentNodeId: string;
  lastUpdated: string;
  definition: { name: string };
}

export const WorkflowDesigner: React.FC = () => {
  const [definitions, setDefinitions] = useState<WorkflowDefinition[]>([]);
  const [instances, setInstances] = useState<WorkflowInstance[]>([]);
  const [name, setName] = useState('');
  const [triggerType, setTriggerType] = useState('BORROW_CREATED');
  const [isCreating, setIsCreating] = useState(false);
  const [activeTab, setActiveTab] = useState<'templates' | 'designer' | 'instances'>('templates');

  const fetchDefinitions = async () => {
    try {
      const response = await api.get('/workflow/definitions');
      if (response.data?.data) {
        setDefinitions(response.data.data);
      }
    } catch (e) {
      setDefinitions([
        { id: 'def1', name: 'Borrow Approval Pipeline', triggerType: 'BORROW_CREATED', version: 1, createdDate: '2026-07-08T09:00:00' },
        { id: 'def2', name: 'Document Ingestion Loop', triggerType: 'DOCUMENT_UPLOADED', version: 1, createdDate: '2026-07-08T10:00:00' }
      ]);
    }
  };

  const fetchInstances = async () => {
    try {
      const response = await api.get('/workflow/instances');
      if (response.data?.data) {
        setInstances(response.data.data);
      }
    } catch (e) {
      setInstances([
        { id: 'inst1', status: 'WAITING_APPROVAL', currentNodeId: 'node_2', lastUpdated: '2026-07-08T10:10:00', definition: { name: 'Borrow Approval Pipeline' } }
      ]);
    }
  };

  useEffect(() => {
    fetchDefinitions();
    fetchInstances();
  }, []);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;
    setIsCreating(true);
    try {
      await api.post(`/workflow/definitions?name=${name}&triggerType=${triggerType}`);
      toast.success('Autonomous Workflow Template saved successfully');
      setName('');
      fetchDefinitions();
    } catch (err) {
      toast.error('Failed to save workflow template');
    } finally {
      setIsCreating(false);
    }
  };

  const handleLaunch = async (defId: string) => {
    try {
      await api.post(`/workflow/execute?definitionId=${defId}`);
      toast.success('Workflow instance launched and running autonomously');
      fetchInstances();
    } catch (err) {
      toast.error('Failed to launch workflow execution');
    }
  };

  const handleApprove = async (instanceId: string) => {
    try {
      await api.post(`/workflow/approve?instanceId=${instanceId}`);
      toast.success('Librarian Review approved. Resuming workflow steps...');
      fetchInstances();
    } catch (err) {
      toast.error('Failed to approve step');
    }
  };

  const definitionColumns = [
    { header: 'Workflow Name', accessor: (row: WorkflowDefinition) => (
      <div class="flex items-center space-x-3">
        <GitPullRequest class="h-5 w-5 text-slate-400" />
        <span class="font-medium text-slate-800 dark:text-slate-200">{row.name}</span>
      </div>
    ) },
    { header: 'Trigger Event', accessor: (row: WorkflowDefinition) => <span class="px-2 py-0.5 bg-slate-100 dark:bg-slate-800 text-[10px] font-bold uppercase rounded">{row.triggerType}</span> },
    { header: 'Version', accessor: (row: WorkflowDefinition) => <span class="text-slate-400">v{row.version}</span> },
    { header: 'Actions', accessor: (row: WorkflowDefinition) => (
      <Button size="sm" onClick={() => handleLaunch(row.id)} className="text-[10px] py-1 px-3 flex items-center space-x-1">
        <Play class="h-3 w-3" />
        <span>Launch</span>
      </Button>
    ) }
  ];

  const instanceColumns = [
    { header: 'Workflow Definition', accessor: (row: WorkflowInstance) => <span class="font-semibold text-slate-800 dark:text-slate-200">{row.definition.name}</span> },
    { header: 'Current Node', accessor: (row: WorkflowInstance) => <span class="text-xs text-slate-400 font-mono">{row.currentNodeId || 'COMPLETED'}</span> },
    { header: 'Status', accessor: (row: WorkflowInstance) => {
      const colors = {
        RUNNING: 'bg-blue-50 text-blue-700 animate-pulse',
        WAITING_APPROVAL: 'bg-orange-50 text-orange-700 font-bold',
        COMPLETED: 'bg-green-50 text-green-700',
        FAILED: 'bg-red-50 text-red-700',
      };
      return <span class={`px-2 py-0.5 rounded-md text-[10px] uppercase ${colors[row.status]}`}>{row.status}</span>;
    } },
    { header: 'Actions', accessor: (row: WorkflowInstance) => (
      row.status === 'WAITING_APPROVAL' ? (
        <Button size="sm" onClick={() => handleApprove(row.id)} className="text-[10px] py-1 px-3">
          Approve step
        </Button>
      ) : <span class="text-xs text-slate-400">Autonomous Execution</span>
    ) }
  ];

  return (
    <div class="space-y-6">
      
      {/* Header */}
      <div class="flex justify-between items-center">
        <div>
          <h1 class="text-2xl font-bold">Workflow Engine</h1>
          <p class="text-slate-500 text-sm">Design, schedule, and orchestrate autonomous multi-agent pipelines</p>
        </div>

        {/* Tab navigation */}
        <div class="flex bg-slate-100 dark:bg-slate-800/40 p-1 rounded-xl space-x-1 text-xs font-medium">
          <button 
            onClick={() => setActiveTab('templates')} 
            class={`px-4 py-2 rounded-lg transition-all ${activeTab === 'templates' ? 'bg-white dark:bg-slate-900 shadow-sm text-primary-600' : 'text-slate-600'}`}
          >
            Templates
          </button>
          <button 
            onClick={() => setActiveTab('designer')} 
            class={`px-4 py-2 rounded-lg transition-all ${activeTab === 'designer' ? 'bg-white dark:bg-slate-900 shadow-sm text-primary-600' : 'text-slate-600'}`}
          >
            Designer Canvas
          </button>
          <button 
            onClick={() => setActiveTab('instances')} 
            class={`px-4 py-2 rounded-lg transition-all ${activeTab === 'instances' ? 'bg-white dark:bg-slate-900 shadow-sm text-primary-600' : 'text-slate-600'}`}
          >
            Execution Logs
          </button>
        </div>
      </div>

      {activeTab === 'templates' && (
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Templates list */}
          <Card class="p-6 lg:col-span-2">
            <h3 class="text-sm font-semibold mb-4 text-slate-500 uppercase flex items-center space-x-2">
              <GitPullRequest class="h-4 w-4" />
              <span>Workflow Templates Catalog</span>
            </h3>
            <Table columns={definitionColumns} data={definitions} />
          </Card>

          {/* New template form */}
          <Card class="p-6 lg:col-span-1">
            <h3 class="text-sm font-semibold mb-4 text-slate-500 uppercase flex items-center space-x-2">
              <Plus class="h-4 w-4" />
              <span>Define Workflow</span>
            </h3>
            <form onSubmit={handleCreate} class="space-y-4 text-xs font-semibold">
              <div class="space-y-1">
                <label class="block text-slate-400">Workflow Name</label>
                <input 
                  type="text" 
                  value={name} 
                  onChange={e => setName(e.target.value)} 
                  placeholder="e.g. Borrow Review Pipeline"
                  className="w-full p-2.5 rounded-xl border bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-800 focus:outline-none focus:ring-2 focus:ring-primary-500/20"
                />
              </div>

              <div class="space-y-1">
                <label class="block text-slate-400">Trigger Event</label>
                <select 
                  value={triggerType} 
                  onChange={e => setTriggerType(e.target.value)} 
                  className="w-full p-2.5 rounded-xl border bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-800 focus:outline-none"
                >
                  <option value="BORROW_CREATED">Borrow Created</option>
                  <option value="DOCUMENT_UPLOADED">Document Uploaded</option>
                  <option value="BOOK_UPLOADED">Book Uploaded</option>
                  <option value="MANUAL">Manual Trigger</option>
                </select>
              </div>

              <Button type="submit" isLoading={isCreating} className="w-full py-2.5">
                Save Template
              </Button>
            </form>
          </Card>
        </div>
      )}

      {activeTab === 'designer' && (
        <Card class="p-6 space-y-4">
          <h3 class="text-sm font-semibold text-slate-500 uppercase flex items-center space-x-2">
            <Share2 class="h-4 w-4" />
            <span>Workflow Designer Canvas</span>
          </h3>

          <div class="bg-slate-950 rounded-xl p-8 min-h-[350px] flex items-center justify-center relative overflow-hidden">
            <div class="absolute inset-0 opacity-5 bg-[radial-gradient(#ffffff_1px,transparent_1px)] [background-size:20px_20px]"></div>
            
            <div class="relative z-10 flex flex-col md:flex-row items-center justify-center space-y-6 md:space-y-0 md:space-x-8">
              {/* Node 1 */}
              <div class="p-4 bg-slate-900 border border-slate-800 rounded-xl w-[200px] text-xs space-y-1">
                <div class="flex justify-between items-center text-[10px] text-primary-500 font-bold uppercase">
                  <span>Trigger Node</span>
                </div>
                <h4 class="font-bold text-white">Event: Borrow Created</h4>
              </div>

              <div class="h-8 w-0.5 md:h-0.5 md:w-8 bg-slate-800"></div>

              {/* Node 2 */}
              <div class="p-4 bg-slate-900 border border-slate-800 rounded-xl w-[200px] text-xs space-y-1">
                <div class="flex justify-between items-center text-[10px] text-orange-500 font-bold uppercase">
                  <span>Approval Node</span>
                </div>
                <h4 class="font-bold text-white">Librarian Review</h4>
              </div>

              <div class="h-8 w-0.5 md:h-0.5 md:w-8 bg-slate-800"></div>

              {/* Node 3 */}
              <div class="p-4 bg-slate-900 border border-slate-800 rounded-xl w-[200px] text-xs space-y-1">
                <div class="flex justify-between items-center text-[10px] text-green-500 font-bold uppercase">
                  <span>Action Tool Node</span>
                </div>
                <h4 class="font-bold text-white">Send Receipt Alert</h4>
              </div>
            </div>
          </div>
        </Card>
      )}

      {activeTab === 'instances' && (
        <Card class="p-6">
          <h3 class="text-sm font-semibold mb-4 text-slate-500 uppercase flex items-center space-x-2">
            <Clock class="h-4 w-4" />
            <span>Workflow Active Instances logs</span>
          </h3>
          <Table columns={instanceColumns} data={instances} />
        </Card>
      )}

    </div>
  );
};
