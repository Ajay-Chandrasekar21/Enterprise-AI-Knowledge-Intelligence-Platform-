import React, { useState, useEffect } from 'react';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { BookOpen, Sparkles, TrendingUp, Users, Award } from 'lucide-react';
import { toast } from 'react-toastify';
import api from '@/services/api';

interface RecommendedBook {
  title: string;
  author: string;
  category: string;
  reason: string;
  score: number;
}

export const RecommendationCenter: React.FC = () => {
  const [recommendations, setRecommendations] = useState<RecommendedBook[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const fetchRecommendations = async () => {
    setIsLoading(true);
    try {
      // Mock call to Agent Orchestrator to compile recommendations
      const response = await api.post('/ai/query', { query: 'Analyze my reading habits and recommend books' }).catch(() => null);
      if (response && response.data?.data) {
        // Parse LLM response mock structures
        setRecommendations([
          { title: 'Designing Data-Intensive Applications', author: 'Martin Kleppmann', category: 'Software Engineering', reason: 'Recommended based on your interest in backend architectures and Clean Code borrows.', score: 98 }
        ]);
      } else {
        setRecommendations([
          { title: 'Designing Data-Intensive Applications', author: 'Martin Kleppmann', category: 'Software Engineering', reason: 'Recommended based on your interest in backend architectures and Clean Code borrows.', score: 98 },
          { title: 'Introduction to Algorithms', author: 'Thomas H. Cormen', category: 'Algorithms', reason: 'Trending in Computer Science department workspace.', score: 91 },
          { title: 'Structure and Interpretation of Computer Programs', author: 'Harold Abelson', category: 'Computer Science', reason: 'Collaborative match from users with similar reading preferences.', score: 86 }
        ]);
      }
    } catch (e) {
      toast.error('Failed to query custom suggestions');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchRecommendations();
  }, []);

  return (
    <div className="space-y-6">
      
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold">Personalized Recommendations</h1>
          <p className="text-slate-500 text-sm">Analyze borrow history, reading speeds, and trending category matches</p>
        </div>
        <Button onClick={fetchRecommendations} isLoading={isLoading}>
          Re-Analyze Profile
        </Button>
      </div>

      {/* Recommendations Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        
        {/* Recommendation Cards */}
        {recommendations.map((book, idx) => (
          <Card key={idx} className="p-6 flex flex-col justify-between space-y-4 hover:border-primary-500/30 transition-all border border-slate-200/50 dark:border-slate-800/40">
            <div className="space-y-2">
              <div className="flex justify-between items-start">
                <span className="px-2 py-0.5 bg-primary-50 dark:bg-primary-950/40 text-primary-600 dark:text-primary-400 rounded-md text-[10px] font-bold uppercase tracking-wider">
                  {book.category}
                </span>
                <span className="text-xs text-green-600 font-bold">Match: {book.score}%</span>
              </div>
              <h3 className="font-bold text-base text-slate-800 dark:text-slate-200">{book.title}</h3>
              <p className="text-xs text-slate-400">by {book.author}</p>
            </div>
            
            <p className="text-xs text-slate-500 leading-relaxed italic bg-slate-50 dark:bg-slate-800/40 p-3 rounded-xl border border-slate-100 dark:border-slate-800/20">
              "{book.reason}"
            </p>

            <div className="pt-2">
              <Button variant="outline" size="sm" className="w-full text-xs py-2">
                View Catalog Entry
              </Button>
            </div>
          </Card>
        ))}

      </div>

      {/* Metrics Row */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 pt-4">
        <Card className="p-6 flex items-center space-x-4">
          <div className="h-12 w-12 rounded-xl bg-orange-50 dark:bg-orange-950/20 text-orange-600 dark:text-orange-400 flex items-center justify-center">
            <TrendingUp className="h-6 w-6" />
          </div>
          <div>
            <h4 className="text-sm font-bold">Trending in Faculty</h4>
            <p className="text-xs text-slate-400">Computer Science is the top borrowing category this month</p>
          </div>
        </Card>
        <Card className="p-6 flex items-center space-x-4">
          <div className="h-12 w-12 rounded-xl bg-blue-50 dark:bg-blue-950/20 text-blue-600 dark:text-blue-400 flex items-center justify-center">
            <Users className="h-6 w-6" />
          </div>
          <div>
            <h4 className="text-sm font-bold">Collaborative Filter</h4>
            <p className="text-xs text-slate-400">92% similarity index with Class B student borrow logs</p>
          </div>
        </Card>
        <Card className="p-6 flex items-center space-x-4">
          <div className="h-12 w-12 rounded-xl bg-purple-50 dark:bg-purple-950/20 text-purple-600 dark:text-purple-400 flex items-center justify-center">
            <Award className="h-6 w-6" />
          </div>
          <div>
            <h4 className="text-sm font-bold">Content-Based Filter</h4>
            <p className="text-xs text-slate-400">Interests parsed: backend development, compilers design</p>
          </div>
        </Card>
      </div>

    </div>
  );
};
