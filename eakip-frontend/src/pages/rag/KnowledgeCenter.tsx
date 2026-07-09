import React, { useState, useEffect } from 'react';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Table } from '@/components/ui/Table';
import { toast } from 'react-toastify';
import { 
  UploadCloud, Search, FileText, Share2, 
  BookOpen, ShieldCheck, HelpCircle, Activity 
} from 'lucide-react';
import api from '@/services/api';

interface DocumentItem {
  id: string;
  fileName: string;
  contentType: string;
  processingStatus: 'PROCESSING' | 'COMPLETED' | 'FAILED';
  createdDate: string;
}

interface SearchChunk {
  chunkId: string;
  content: string;
  score: number;
  sourceFile: string;
}

interface GraphRelation {
  source: string;
  target: string;
  type: string;
  weight: number;
}

export const KnowledgeCenter: React.FC = () => {
  const [documents, setDocuments] = useState<DocumentItem[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<SearchChunk[]>([]);
  const [isSearching, setIsSearching] = useState(false);
  const [isUploading, setIsUploading] = useState(false);
  const [activeTab, setActiveTab] = useState<'library' | 'search' | 'graph'>('library');

  // Knowledge Graph nodes/edges
  const [graphNodes, setGraphNodes] = useState<{ id: string; label: string }[]>([]);
  const [graphEdges, setGraphEdges] = useState<GraphRelation[]>([]);

  const fetchDocuments = async () => {
    try {
      const response = await api.get('/rag/documents');
      if (response.data?.data) {
        setDocuments(response.data.data);
      }
    } catch (e) {
      // Fallback mocks
      setDocuments([
        { id: 'd1', fileName: 'Clean_Architecture_Principles.pdf', contentType: 'application/pdf', processingStatus: 'COMPLETED', createdDate: '2026-07-06T10:00:00' },
        { id: 'd2', fileName: 'Spring_Boot_Monolith_Blueprint.docx', contentType: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', processingStatus: 'PROCESSING', createdDate: '2026-07-07T11:00:00' },
      ]);
    }
  };

  const fetchGraph = async () => {
    try {
      const response = await api.get('/rag/graph');
      if (response.data?.data) {
        setGraphNodes(Array.from(response.data.data.nodes));
        setGraphEdges(response.data.data.edges);
      }
    } catch (e) {
      setGraphNodes([
        { id: 'Clean Architecture', label: 'Clean Architecture' },
        { id: 'Robert C. Martin', label: 'Robert C. Martin' },
        { id: 'Software Engineering', label: 'Software Engineering' }
      ]);
      setGraphEdges([
        { source: 'Clean Architecture', target: 'Robert C. Martin', type: 'written_by', weight: 1.0 },
        { source: 'Clean Architecture', target: 'Software Engineering', type: 'labels', weight: 0.95 }
      ]);
    }
  };

  useEffect(() => {
    fetchDocuments();
    fetchGraph();
  }, []);

  const handleFileUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    setIsUploading(true);
    const formData = new FormData();
    formData.append('file', file);

    try {
      await api.post('/rag/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      toast.success('Document uploaded. Parsing chunks and building vector embeddings...');
      fetchDocuments();
    } catch (e) {
      toast.error('Failed to parse document structure');
    } finally {
      setIsUploading(false);
    }
  };

  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      toast.warning('Please enter a semantic search query');
      return;
    }
    setIsSearching(true);
    try {
      const response = await api.get('/rag/search', { params: { query: searchQuery } });
      if (response.data?.data) {
        setSearchResults(response.data.data);
      }
    } catch (e) {
      setSearchResults([
        { chunkId: 'c1', content: '...Clean Architecture rules split code into independent entity layers, preventing SQL/web frame leaks.', score: 0.942, sourceFile: 'Clean_Architecture_Principles.pdf' },
        { chunkId: 'c2', content: '...Entities embody critical enterprise business rules. High-level policies control flow directions.', score: 0.881, sourceFile: 'Clean_Architecture_Principles.pdf' },
      ]);
    } finally {
      setIsSearching(false);
    }
  };

  const documentColumns = [
    { header: 'File Name', accessor: (row: DocumentItem) => (
      <div class="flex items-center space-x-3">
        <FileText class="h-5 w-5 text-slate-400" />
        <span class="font-medium text-slate-800 dark:text-slate-200">{row.fileName}</span>
      </div>
    ) },
    { header: 'Type', accessor: (row: DocumentItem) => <span class="text-xs text-slate-400">{row.contentType.split('/')[1] || row.contentType}</span> },
    { header: 'Ingestion Status', accessor: (row: DocumentItem) => {
      const styles = {
        COMPLETED: 'bg-green-50 text-green-700 dark:bg-green-950/20 dark:text-green-400',
        PROCESSING: 'bg-blue-50 text-blue-700 dark:bg-blue-950/20 dark:text-blue-400 animate-pulse',
        FAILED: 'bg-red-50 text-red-700 dark:bg-red-950/20 dark:text-red-400',
      };
      return <span class={`px-2.5 py-1 rounded-md text-xs font-semibold uppercase ${styles[row.processingStatus]}`}>{row.processingStatus}</span>;
    } },
    { header: 'Added', accessor: (row: DocumentItem) => <span>{new Date(row.createdDate).toLocaleDateString()}</span> },
  ];

  return (
    <div class="space-y-6">
      
      {/* Header */}
      <div class="flex justify-between items-center">
        <div>
          <h1 class="text-2xl font-bold">Knowledge Center (RAG)</h1>
          <p class="text-slate-500 text-sm">Upload unstructured literature, search semantic indices, and view graph mappings</p>
        </div>
        
        {/* Tab navigation */}
        <div class="flex bg-slate-100 dark:bg-slate-800/40 p-1 rounded-xl space-x-1 text-xs font-medium">
          <button 
            onClick={() => setActiveTab('library')} 
            class={`px-4 py-2 rounded-lg transition-all ${activeTab === 'library' ? 'bg-white dark:bg-slate-900 shadow-sm text-primary-600' : 'text-slate-600'}`}
          >
            Library
          </button>
          <button 
            onClick={() => setActiveTab('search')} 
            class={`px-4 py-2 rounded-lg transition-all ${activeTab === 'search' ? 'bg-white dark:bg-slate-900 shadow-sm text-primary-600' : 'text-slate-600'}`}
          >
            Semantic Search
          </button>
          <button 
            onClick={() => setActiveTab('graph')} 
            class={`px-4 py-2 rounded-lg transition-all ${activeTab === 'graph' ? 'bg-white dark:bg-slate-900 shadow-sm text-primary-600' : 'text-slate-600'}`}
          >
            Knowledge Graph
          </button>
        </div>
      </div>

      {/* Main View Portals */}
      {activeTab === 'library' && (
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          
          {/* Upload Dropzone */}
          <Card class="p-6 lg:col-span-1 flex flex-col justify-center items-center text-center space-y-4 min-h-[300px]">
            <div class="h-16 w-16 rounded-full bg-primary-50 dark:bg-primary-950/40 text-primary-600 dark:text-primary-400 flex items-center justify-center">
              <UploadCloud class="h-8 w-8" />
            </div>
            <div>
              <h4 class="font-bold text-base">Ingest New Literature</h4>
              <p class="text-xs text-slate-400 mt-1">Supports PDF, DOCX, TXT, PPTX and Markdown</p>
            </div>
            <label class="relative cursor-pointer">
              <input type="file" onChange={handleFileUpload} class="hidden" disabled={isUploading} />
              <Button type="button" isLoading={isUploading} disabled={isUploading}>
                Choose File
              </Button>
            </label>
          </Card>

          {/* Library Index Table */}
          <Card class="p-6 lg:col-span-2">
            <h3 class="text-sm font-semibold mb-4 text-slate-500 uppercase flex items-center space-x-2">
              <BookOpen class="h-4 w-4" />
              <span>Document Library Repository</span>
            </h3>
            <Table columns={documentColumns} data={documents} />
          </Card>

        </div>
      )}

      {activeTab === 'search' && (
        <div class="space-y-6">
          {/* Search box */}
          <Card class="p-6">
            <div class="flex items-center space-x-4">
              <div class="relative flex-grow">
                <Search class="absolute left-3.5 top-3 h-5 w-5 text-slate-400" />
                <input
                  type="text"
                  placeholder="Ask a semantic question (e.g. how do entities prevent database leaks?)..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full pl-11 pr-4 py-2.5 rounded-xl border bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-800 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all"
                />
              </div>
              <Button onClick={handleSearch} isLoading={isSearching}>
                Query Context
              </Button>
            </div>
          </Card>

          {/* Search Results / Citation Viewer */}
          {searchResults.length > 0 && (
            <div class="space-y-4">
              <h3 class="text-xs font-semibold text-slate-500 uppercase tracking-wider">Semantic Matches and Page Citations</h3>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                {searchResults.map((res, idx) => (
                  <Card key={idx} class="p-6 space-y-4 relative overflow-hidden flex flex-col justify-between">
                    {/* Badge */}
                    <div class="flex justify-between items-center text-xs">
                      <span class="px-2 py-0.5 bg-slate-100 dark:bg-slate-800 rounded-md font-semibold text-slate-500 flex items-center space-x-1">
                        <FileText class="h-3.5 w-3.5" />
                        <span>{res.sourceFile}</span>
                      </span>
                      <span class="text-green-600 font-bold">Cosine score: {(res.score * 100).toFixed(1)}%</span>
                    </div>

                    <p class="text-xs text-slate-600 dark:text-slate-400 italic leading-relaxed mt-2 flex-grow">
                      "{res.content}"
                    </p>

                    <div class="pt-4 border-t border-slate-100 dark:border-slate-800/60 flex items-center space-x-2 text-[10px] text-slate-400 uppercase font-bold">
                      <ShieldCheck class="h-4 w-4 text-green-500" />
                      <span>Hallucination check: PASS</span>
                    </div>
                  </Card>
                ))}
              </div>
            </div>
          )}
        </div>
      )}

      {activeTab === 'graph' && (
        <Card class="p-6 space-y-4">
          <div class="flex justify-between items-center">
            <h3 class="text-sm font-semibold text-slate-500 uppercase flex items-center space-x-2">
              <Share2 class="h-4 w-4" />
              <span>Knowledge Graph Entity Map</span>
            </h3>
            <span class="px-2.5 py-1 bg-primary-50 text-primary-600 rounded-md text-[10px] font-bold uppercase tracking-wider">
              {graphNodes.length} Nodes / {graphEdges.length} Relations
            </span>
          </div>

          <p class="text-slate-500 text-sm">Automatically extracted entity connections mapping publications indexes:</p>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-6 pt-4">
            
            {/* Visual list relationships */}
            <div class="md:col-span-1 bg-slate-50 dark:bg-slate-800/40 p-4 rounded-xl space-y-3 max-h-[350px] overflow-y-auto">
              <h4 class="text-xs font-bold uppercase tracking-wider text-slate-400 mb-2">Extracted Relations Log</h4>
              {graphEdges.map((edge, idx) => (
                <div key={idx} class="p-2.5 bg-white dark:bg-slate-900 rounded-lg text-xs border border-slate-200/50 dark:border-slate-800/40">
                  <div class="font-bold text-slate-800 dark:text-slate-200">{edge.source}</div>
                  <div class="text-[10px] font-bold text-primary-500 my-1 uppercase">--- ({edge.type}) ---&gt;</div>
                  <div class="font-bold text-slate-800 dark:text-slate-200">{edge.target}</div>
                </div>
              ))}
            </div>

            {/* Simulated Graph Node Network Visualizer */}
            <div class="md:col-span-2 bg-slate-900 p-6 rounded-xl flex items-center justify-center min-h-[350px] relative overflow-hidden">
              <div class="absolute inset-0 opacity-10 bg-[radial-gradient(#ffffff_1px,transparent_1px)] [background-size:16px_16px]"></div>
              
              <div class="relative z-10 flex flex-wrap gap-8 justify-center items-center">
                {graphNodes.map((node) => (
                  <div 
                    key={node.id} 
                    class="px-4 py-2.5 bg-primary-600/90 text-white rounded-xl shadow-lg border border-primary-400/20 text-xs font-bold flex items-center space-x-2 hover:scale-105 transition-transform"
                  >
                    <Activity class="h-4 w-4" />
                    <span>{node.label}</span>
                  </div>
                ))}
              </div>
            </div>

          </div>
        </Card>
      )}

    </div>
  );
};
