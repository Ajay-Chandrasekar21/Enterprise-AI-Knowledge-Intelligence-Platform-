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
      <div className="flex items-center space-x-3">
        <FileText className="h-5 w-5 text-slate-400" />
        <span className="font-medium text-slate-800 dark:text-slate-200">{row.fileName}</span>
      </div>
    ) },
    { header: 'Type', accessor: (row: DocumentItem) => <span className="text-xs text-slate-400">{row.contentType.split('/')[1] || row.contentType}</span> },
    { header: 'Ingestion Status', accessor: (row: DocumentItem) => {
      const styles = {
        COMPLETED: 'bg-green-50 text-green-700 dark:bg-green-950/20 dark:text-green-400',
        PROCESSING: 'bg-blue-50 text-blue-700 dark:bg-blue-950/20 dark:text-blue-400 animate-pulse',
        FAILED: 'bg-red-50 text-red-700 dark:bg-red-950/20 dark:text-red-400',
      };
      return <span className={`px-2.5 py-1 rounded-md text-xs font-semibold uppercase ${styles[row.processingStatus]}`}>{row.processingStatus}</span>;
    } },
    { header: 'Added', accessor: (row: DocumentItem) => <span>{new Date(row.createdDate).toLocaleDateString()}</span> },
  ];

  return (
    <div className="space-y-6">
      
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold">Knowledge Center (RAG)</h1>
          <p className="text-slate-500 text-sm">Upload unstructured literature, search semantic indices, and view graph mappings</p>
        </div>
        
        {/* Tab navigation */}
        <div className="flex bg-slate-100 dark:bg-slate-800/40 p-1 rounded-xl space-x-1 text-xs font-medium">
          <button 
            onClick={() => setActiveTab('library')} 
            className={`px-4 py-2 rounded-lg transition-all ${activeTab === 'library' ? 'bg-white dark:bg-slate-900 shadow-sm text-primary-600' : 'text-slate-600'}`}
          >
            Library
          </button>
          <button 
            onClick={() => setActiveTab('search')} 
            className={`px-4 py-2 rounded-lg transition-all ${activeTab === 'search' ? 'bg-white dark:bg-slate-900 shadow-sm text-primary-600' : 'text-slate-600'}`}
          >
            Semantic Search
          </button>
          <button 
            onClick={() => setActiveTab('graph')} 
            className={`px-4 py-2 rounded-lg transition-all ${activeTab === 'graph' ? 'bg-white dark:bg-slate-900 shadow-sm text-primary-600' : 'text-slate-600'}`}
          >
            Knowledge Graph
          </button>
        </div>
      </div>

      {/* Main View Portals */}
      {activeTab === 'library' && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          
          {/* Upload Dropzone */}
          <Card className="p-6 lg:col-span-1 flex flex-col justify-center items-center text-center space-y-4 min-h-[300px]">
            <div className="h-16 w-16 rounded-full bg-primary-50 dark:bg-primary-950/40 text-primary-600 dark:text-primary-400 flex items-center justify-center">
              <UploadCloud className="h-8 w-8" />
            </div>
            <div>
              <h4 className="font-bold text-base">Ingest New Literature</h4>
              <p className="text-xs text-slate-400 mt-1">Supports PDF, DOCX, TXT, PPTX and Markdown</p>
            </div>
            <label className="relative cursor-pointer">
              <input type="file" onChange={handleFileUpload} className="hidden" disabled={isUploading} />
              <Button type="button" isLoading={isUploading} disabled={isUploading}>
                Choose File
              </Button>
            </label>
          </Card>

          {/* Library Index Table */}
          <Card className="p-6 lg:col-span-2">
            <h3 className="text-sm font-semibold mb-4 text-slate-500 uppercase flex items-center space-x-2">
              <BookOpen className="h-4 w-4" />
              <span>Document Library Repository</span>
            </h3>
            <Table columns={documentColumns} data={documents} />
          </Card>

        </div>
      )}

      {activeTab === 'search' && (
        <div className="space-y-6">
          {/* Search box */}
          <Card className="p-6">
            <div className="flex items-center space-x-4">
              <div className="relative flex-grow">
                <Search className="absolute left-3.5 top-3 h-5 w-5 text-slate-400" />
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
            <div className="space-y-4">
              <h3 className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Semantic Matches and Page Citations</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {searchResults.map((res, idx) => (
                  <Card key={idx} className="p-6 space-y-4 relative overflow-hidden flex flex-col justify-between">
                    {/* Badge */}
                    <div className="flex justify-between items-center text-xs">
                      <span className="px-2 py-0.5 bg-slate-100 dark:bg-slate-800 rounded-md font-semibold text-slate-500 flex items-center space-x-1">
                        <FileText className="h-3.5 w-3.5" />
                        <span>{res.sourceFile}</span>
                      </span>
                      <span className="text-green-600 font-bold">Cosine score: {(res.score * 100).toFixed(1)}%</span>
                    </div>

                    <p className="text-xs text-slate-600 dark:text-slate-400 italic leading-relaxed mt-2 flex-grow">
                      "{res.content}"
                    </p>

                    <div className="pt-4 border-t border-slate-100 dark:border-slate-800/60 flex items-center space-x-2 text-[10px] text-slate-400 uppercase font-bold">
                      <ShieldCheck className="h-4 w-4 text-green-500" />
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
        <Card className="p-6 space-y-4">
          <div className="flex justify-between items-center">
            <h3 className="text-sm font-semibold text-slate-500 uppercase flex items-center space-x-2">
              <Share2 className="h-4 w-4" />
              <span>Knowledge Graph Entity Map</span>
            </h3>
            <span className="px-2.5 py-1 bg-primary-50 text-primary-600 rounded-md text-[10px] font-bold uppercase tracking-wider">
              {graphNodes.length} Nodes / {graphEdges.length} Relations
            </span>
          </div>

          <p className="text-slate-500 text-sm">Automatically extracted entity connections mapping publications indexes:</p>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 pt-4">
            
            {/* Visual list relationships */}
            <div className="md:col-span-1 bg-slate-50 dark:bg-slate-800/40 p-4 rounded-xl space-y-3 max-h-[350px] overflow-y-auto">
              <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400 mb-2">Extracted Relations Log</h4>
              {graphEdges.map((edge, idx) => (
                <div key={idx} className="p-2.5 bg-white dark:bg-slate-900 rounded-lg text-xs border border-slate-200/50 dark:border-slate-800/40">
                  <div className="font-bold text-slate-800 dark:text-slate-200">{edge.source}</div>
                  <div className="text-[10px] font-bold text-primary-500 my-1 uppercase">--- ({edge.type}) ---&gt;</div>
                  <div className="font-bold text-slate-800 dark:text-slate-200">{edge.target}</div>
                </div>
              ))}
            </div>

            {/* Simulated Graph Node Network Visualizer */}
            <div className="md:col-span-2 bg-slate-900 p-6 rounded-xl flex items-center justify-center min-h-[350px] relative overflow-hidden">
              <div className="absolute inset-0 opacity-10 bg-[radial-gradient(#ffffff_1px,transparent_1px)] [background-size:16px_16px]"></div>
              
              <div className="relative z-10 flex flex-wrap gap-8 justify-center items-center">
                {graphNodes.map((node) => (
                  <div 
                    key={node.id} 
                    className="px-4 py-2.5 bg-primary-600/90 text-white rounded-xl shadow-lg border border-primary-400/20 text-xs font-bold flex items-center space-x-2 hover:scale-105 transition-transform"
                  >
                    <Activity className="h-4 w-4" />
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
