import React, { useState, useEffect } from 'react';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { toast } from 'react-toastify';
import { 
  Brain, Clock, Sparkles, Star, Heart, 
  Trash2, Compass, ShieldAlert, Award 
} from 'lucide-react';
import api from '@/services/api';

interface MemoryNode {
  id: string;
  memoryType: string;
  content: string;
  relevanceScore: number;
  createdDate: string;
  retentionDays: number;
}

export const MemoryDashboard: React.FC = () => {
  const [timeline, setTimeline] = useState<MemoryNode[]>([]);
  const [interestTags, setInterestTags] = useState('');
  const [feedbackTarget, setFeedbackTarget] = useState('BOOK_DISCOVERY_AGENT');
  const [feedbackRating, setFeedbackRating] = useState(5);
  const [feedbackComment, setFeedbackComment] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const fetchTimeline = async () => {
    setIsLoading(true);
    try {
      const response = await api.get('/memory/timeline');
      if (response.data?.data) {
        setTimeline(response.data.data);
      }
    } catch (e) {
      setTimeline([
        { id: 'm1', memoryType: 'PREFERENCE', content: 'User set favorite categories interest tags: backend development, compilers design', relevanceScore: 1.0, createdDate: '2026-07-08T09:00:00', retentionDays: 90 },
        { id: 'm2', memoryType: 'EPISODIC', content: 'User successfully borrowed Designing Data-Intensive Applications.', relevanceScore: 0.95, createdDate: '2026-07-08T09:30:00', retentionDays: 30 },
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchTimeline();
  }, []);

  const handleUpdatePreferences = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!interestTags.trim()) {
      toast.warning('Please input interest tags');
      return;
    }
    try {
      await api.post('/memory/preferences', { interests: interestTags });
      toast.success('Interests preference registered successfully');
      setInterestTags('');
      fetchTimeline();
    } catch (err) {
      toast.error('Failed to save preferences');
    }
  };

  const handleFeedback = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post(`/memory/feedback?target=${feedbackTarget}&rating=${feedbackRating}`, feedbackComment);
      toast.success('Feedback recorded. Agent learning metrics adjusted.');
      setFeedbackComment('');
      fetchTimeline();
    } catch (err) {
      toast.error('Failed to submit feedback');
    }
  };

  return (
    <div class="space-y-6">
      
      {/* Header */}
      <div>
        <h1 class="text-2xl font-bold">Learning & Memory Center</h1>
        <p class="text-slate-500 text-sm">Review episodic memory timelines, set explicit learning interests, and submit feedback scores</p>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Memory Timeline */}
        <div class="lg:col-span-2 space-y-6">
          <Card class="p-6">
            <h3 class="text-sm font-semibold mb-4 text-slate-500 uppercase flex items-center space-x-2">
              <Clock class="h-4 w-4" />
              <span>Episodic Memory Logs Timeline</span>
            </h3>

            <div class="relative pl-6 border-l border-slate-200 dark:border-slate-800 space-y-8">
              {timeline.map((node) => (
                <div key={node.id} class="relative">
                  {/* Dot */}
                  <span class="absolute -left-[30px] top-1.5 h-4 w-4 rounded-full border-4 border-white dark:border-slate-900 bg-primary-600"></span>
                  
                  <div class="space-y-1">
                    <div class="flex justify-between items-center text-xs font-semibold text-slate-400">
                      <div class="flex items-center space-x-2">
                        <span class="px-2 py-0.5 bg-primary-50 dark:bg-primary-950/40 text-primary-600 dark:text-primary-400 rounded text-[9px] uppercase tracking-wider">
                          {node.memoryType}
                        </span>
                        <span class="text-green-600">Relevance: {(node.relevanceScore * 100).toFixed(0)}%</span>
                      </div>
                      <span>{new Date(node.createdDate).toLocaleTimeString()}</span>
                    </div>
                    <p class="text-xs text-slate-700 dark:text-slate-300 leading-relaxed font-medium">
                      "{node.content}"
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </Card>
        </div>

        {/* Preference Center & Feedback */}
        <div class="lg:col-span-1 space-y-6">
          
          {/* explicit preference center */}
          <Card class="p-6">
            <h3 class="text-sm font-semibold mb-4 text-slate-500 uppercase flex items-center space-x-2">
              <Heart class="h-4 w-4 text-red-500" />
              <span>Preferences Center</span>
            </h3>
            
            <form onSubmit={handleUpdatePreferences} class="space-y-4 text-xs font-semibold">
              <div class="space-y-1">
                <label class="block text-slate-400">Explicit Interest Tags</label>
                <input 
                  type="text" 
                  value={interestTags} 
                  onChange={e => setInterestTags(e.target.value)} 
                  placeholder="e.g. databases, algorithms, Rust"
                  className="w-full p-2.5 rounded-xl border bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-800 focus:outline-none focus:ring-2 focus:ring-primary-500/20"
                />
              </div>
              <Button type="submit" className="w-full py-2">
                Save Preference Node
              </Button>
            </form>
          </Card>

          {/* Feedback Center */}
          <Card class="p-6">
            <h3 class="text-sm font-semibold mb-4 text-slate-500 uppercase flex items-center space-x-2">
              <Star class="h-4 w-4 text-yellow-500" />
              <span>Feedback Center</span>
            </h3>

            <form onSubmit={handleFeedback} class="space-y-4 text-xs font-semibold">
              <div class="space-y-1">
                <label class="block text-slate-400">Target Agent / Tool</label>
                <select 
                  value={feedbackTarget} 
                  onChange={e => setFeedbackTarget(e.target.value)} 
                  className="w-full p-2.5 rounded-xl border bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-800 focus:outline-none"
                >
                  <option value="BOOK_DISCOVERY_AGENT">Book Discovery Agent</option>
                  <option value="RECOMMENDATION_AGENT">Recommendation Agent</option>
                  <option value="RAGDocumentSearchTool">RAG Search Tool</option>
                </select>
              </div>

              <div class="space-y-1">
                <label class="block text-slate-400">Rating Satisfaction Score</label>
                <div class="flex items-center space-x-2">
                  {[1, 2, 3, 4, 5].map((val) => (
                    <button 
                      key={val} 
                      type="button" 
                      onClick={() => setFeedbackRating(val)}
                      class="text-slate-300 hover:text-yellow-500 transition-colors"
                    >
                      <Star className={`h-6 w-6 ${val <= feedbackRating ? 'text-yellow-500 fill-yellow-500' : 'text-slate-300'}`} />
                    </button>
                  ))}
                </div>
              </div>

              <div class="space-y-1">
                <label class="block text-slate-400">Comment / Reflection Feedback</label>
                <textarea
                  value={feedbackComment}
                  onChange={e => setFeedbackComment(e.target.value)}
                  placeholder="e.g. Recommendations were perfectly tailored..."
                  rows={3}
                  className="w-full p-2.5 rounded-xl border bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-800 focus:outline-none focus:ring-2 focus:ring-primary-500/20"
                />
              </div>

              <Button type="submit" className="w-full py-2">
                Submit Learning Score
              </Button>
            </form>
          </Card>

        </div>

      </div>

    </div>
  );
};
