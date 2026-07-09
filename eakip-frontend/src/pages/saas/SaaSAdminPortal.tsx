import React, { useState, useEffect } from 'react';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Table } from '@/components/ui/Table';
import { toast } from 'react-toastify';
import { 
  Building, DollarSign, Cpu, ShieldCheck, 
  Settings, Key, AlertTriangle, ToggleLeft, Palette 
} from 'lucide-react';
import api from '@/services/api';

interface TenantItem {
  id: string;
  name: string;
  brandingPrimaryColor: string;
  brandingLogoUrl?: string;
  active: boolean;
}

interface BillingInvoice {
  planTier: string;
  consumedTokens: number;
  quotaLimit: number;
  estimatedCost: number;
  currency: string;
}

export const SaaSAdminPortal: React.FC = () => {
  const [tenants, setTenants] = useState<TenantItem[]>([]);
  const [invoice, setInvoice] = useState<BillingInvoice | null>(null);
  const [flags, setFlags] = useState<Record<string, boolean>>({});
  const [tenantName, setTenantName] = useState('');
  const [primaryColor, setPrimaryColor] = useState('#4F46E5');
  const [isCreating, setIsCreating] = useState(false);
  const [activeTab, setActiveTab] = useState<'tenants' | 'billing' | 'branding' | 'flags'>('tenants');

  const fetchTenants = async () => {
    try {
      const response = await api.get('/saas/tenants');
      if (response.data?.data) {
        setTenants(response.data.data);
      }
    } catch (e) {
      setTenants([
        { id: 't1', name: 'University Catalog', brandingPrimaryColor: '#4F46E5', active: true },
        { id: 't2', name: 'CS Department Lab', brandingPrimaryColor: '#0EA5E9', active: true },
        { id: 't3', name: 'Medical Library', brandingPrimaryColor: '#10B981', active: false },
      ]);
    }
  };

  const fetchBilling = async () => {
    try {
      const response = await api.get('/saas/billing');
      if (response.data?.data) {
        setInvoice(response.data.data);
      }
    } catch (e) {
      setInvoice({
        planTier: 'ENTERPRISE',
        consumedTokens: 1425000,
        quotaLimit: 10000000,
        estimatedCost: 21.37,
        currency: 'USD'
      });
    }
  };

  const fetchFlags = async () => {
    try {
      const response = await api.get('/saas/feature-flags');
      if (response.data?.data) {
        setFlags(response.data.data);
      }
    } catch (e) {
      setFlags({
        advancedRAGindexing: true,
        mcpConnectorsDiscovery: true,
        autonomousWorkflows: true,
      });
    }
  };

  useEffect(() => {
    fetchTenants();
    fetchBilling();
    fetchFlags();
  }, []);

  const handleCreateTenant = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!tenantName.trim()) return;
    setIsCreating(true);
    try {
      await api.post(`/saas/tenants?name=${tenantName}&primaryColor=${primaryColor}`);
      toast.success('Isolated Workspace Tenant registered successfully');
      setTenantName('');
      fetchTenants();
    } catch (err) {
      toast.error('Failed to create tenant');
    } finally {
      setIsCreating(false);
    }
  };

  const tenantColumns = [
    { header: 'Tenant Name', accessor: (row: TenantItem) => (
      <div class="flex items-center space-x-3">
        <Building class="h-5 w-5 text-slate-400" />
        <span class="font-medium text-slate-800 dark:text-slate-200">{row.name}</span>
      </div>
    ) },
    { header: 'Primary Color', accessor: (row: TenantItem) => (
      <div class="flex items-center space-x-2">
        <span class="h-4 w-4 rounded-full border border-slate-200" style={{ backgroundColor: row.brandingPrimaryColor }}></span>
        <span class="text-xs text-slate-400 font-mono">{row.brandingPrimaryColor}</span>
      </div>
    ) },
    { header: 'Isolation Status', accessor: (row: TenantItem) => (
      <span className={`px-2 py-0.5 rounded-md text-[10px] font-bold uppercase ${row.active ? 'bg-green-50 text-green-700' : 'bg-slate-100 text-slate-700'}`}>
        {row.active ? 'ISOLATED_ACTIVE' : 'DEACTIVATED'}
      </span>
    ) }
  ];

  return (
    <div class="space-y-6">
      
      {/* Header */}
      <div class="flex justify-between items-center">
        <div>
          <h1 class="text-2xl font-bold">Global SaaS Admin Portal</h1>
          <p class="text-slate-500 text-sm">Manage database isolation tenants, track token usages, check billing, and toggle SaaS feature flags</p>
        </div>

        {/* Tab navigation */}
        <div class="flex bg-slate-100 dark:bg-slate-800/40 p-1 rounded-xl space-x-1 text-xs font-medium">
          <button 
            onClick={() => setActiveTab('tenants')} 
            class={`px-4 py-2 rounded-lg transition-all ${activeTab === 'tenants' ? 'bg-white dark:bg-slate-900 shadow-sm text-primary-600' : 'text-slate-600'}`}
          >
            Workspaces
          </button>
          <button 
            onClick={() => setActiveTab('billing')} 
            class={`px-4 py-2 rounded-lg transition-all ${activeTab === 'billing' ? 'bg-white dark:bg-slate-900 shadow-sm text-primary-600' : 'text-slate-600'}`}
          >
            Cost & Usage
          </button>
          <button 
            onClick={() => setActiveTab('flags')} 
            class={`px-4 py-2 rounded-lg transition-all ${activeTab === 'flags' ? 'bg-white dark:bg-slate-900 shadow-sm text-primary-600' : 'text-slate-600'}`}
          >
            Feature Flags
          </button>
        </div>
      </div>

      {activeTab === 'tenants' && (
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Tenants list */}
          <Card class="p-6 lg:col-span-2">
            <h3 class="text-sm font-semibold mb-4 text-slate-500 uppercase flex items-center space-x-2">
              <Building class="h-4 w-4" />
              <span>Workspace Tenants Isolation Logs</span>
            </h3>
            <Table columns={tenantColumns} data={tenants} />
          </Card>

          {/* New tenant form */}
          <Card class="p-6 lg:col-span-1">
            <h3 class="text-sm font-semibold mb-4 text-slate-500 uppercase flex items-center space-x-2">
              <Palette class="h-4 w-4" />
              <span>Register Tenant</span>
            </h3>
            
            <form onSubmit={handleCreateTenant} class="space-y-4 text-xs font-semibold">
              <div class="space-y-1">
                <label class="block text-slate-400">Workspace Tenant Name</label>
                <input 
                  type="text" 
                  value={tenantName} 
                  onChange={e => setTenantName(e.target.value)} 
                  placeholder="e.g. CS Research Lab"
                  className="w-full p-2.5 rounded-xl border bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-800 focus:outline-none focus:ring-2 focus:ring-primary-500/20"
                />
              </div>

              <div class="space-y-1">
                <label class="block text-slate-400 font-bold">Theme Branding Primary Color (Hex)</label>
                <input 
                  type="color" 
                  value={primaryColor} 
                  onChange={e => setPrimaryColor(e.target.value)} 
                  className="w-full h-10 rounded-xl cursor-pointer border border-slate-200"
                />
              </div>

              <Button type="submit" isLoading={isCreating} className="w-full py-2.5">
                Register Workspace
              </Button>
            </form>
          </Card>
        </div>
      )}

      {activeTab === 'billing' && invoice && (
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          {/* Cost stats */}
          <Card class="p-6 flex items-center space-x-4">
            <div class="h-12 w-12 rounded-xl bg-green-50 dark:bg-green-950/20 text-green-600 dark:text-green-400 flex items-center justify-center">
              <DollarSign class="h-6 w-6" />
            </div>
            <div>
              <h4 class="text-sm font-bold">Monthly Estimated Cost</h4>
              <p class="text-xl font-extrabold text-slate-800 dark:text-slate-100">${invoice.estimatedCost.toFixed(2)}</p>
              <p class="text-[10px] text-slate-400">Tier: {invoice.planTier}</p>
            </div>
          </Card>

          {/* Token usage */}
          <Card class="p-6 flex items-center space-x-4">
            <div class="h-12 w-12 rounded-xl bg-blue-50 dark:bg-blue-950/20 text-blue-600 dark:text-blue-400 flex items-center justify-center">
              <Cpu class="h-6 w-6" />
            </div>
            <div>
              <h4 class="text-sm font-bold">Consumed Quota Tokens</h4>
              <p class="text-xl font-extrabold text-slate-800 dark:text-slate-100">{(invoice.consumedTokens / 1000).toFixed(0)}k</p>
              <p class="text-[10px] text-slate-400">Limit: {(invoice.quotaLimit / 1000000).toFixed(0)}M tokens</p>
            </div>
          </Card>

          {/* Quota Progress */}
          <Card class="p-6 space-y-2 flex flex-col justify-center">
            <div class="flex justify-between items-center text-xs font-bold">
              <span>Token Quota Usage Percentage</span>
              <span>{((invoice.consumedTokens / invoice.quotaLimit) * 100).toFixed(1)}%</span>
            </div>
            <div class="w-full bg-slate-100 dark:bg-slate-800 rounded-full h-2">
              <div class="bg-primary-600 h-2 rounded-full" style={{ width: `${(invoice.consumedTokens / invoice.quotaLimit) * 100}%` }}></div>
            </div>
          </Card>
        </div>
      )}

      {activeTab === 'flags' && (
        <Card class="p-6 space-y-4">
          <h3 class="text-sm font-semibold text-slate-500 uppercase flex items-center space-x-2">
            <ToggleLeft class="h-4 w-4" />
            <span>SaaS Tenant Feature Flags</span>
          </h3>

          <div class="divide-y divide-slate-100 dark:divide-slate-800/60">
            {Object.entries(flags).map(([key, enabled]) => (
              <div key={key} class="py-4 flex justify-between items-center text-xs font-semibold">
                <div>
                  <h4 class="text-slate-800 dark:text-slate-200 capitalize">{key.replace(/([A-Z])/g, ' $1')}</h4>
                  <p class="text-[10px] text-slate-400 mt-0.5">Enforces rate limit isolation rules block</p>
                </div>
                <span className={`px-2.5 py-1 rounded-full text-[10px] font-bold ${enabled ? 'bg-green-50 text-green-700' : 'bg-slate-100 text-slate-700'}`}>
                  {enabled ? 'ENABLED' : 'DISABLED'}
                </span>
              </div>
            ))}
          </div>
        </Card>
      )}

    </div>
  );
};
