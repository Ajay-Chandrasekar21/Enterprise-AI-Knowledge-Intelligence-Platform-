import React from 'react';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Clock, Award, Bookmark, ArrowUpRight, Flame } from 'lucide-react';

export const ReadingCoach: React.FC = () => {
  const habits = [
    { title: 'Daily Reading Goal', value: '45 mins', target: '60 mins', progress: 75, color: 'bg-blue-500' },
    { title: 'Weekly Milestone', value: '4 chapters', target: '5 chapters', progress: 80, color: 'bg-purple-500' },
  ];

  return (
    <div className="space-y-6">
      
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold">Reading Coach Mentor</h1>
        <p className="text-slate-500 text-sm">Monitor reading habits, set milestones, and track page completion times</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Core milestones */}
        <div className="lg:col-span-2 space-y-6">
          <Card className="p-6 space-y-6">
            <h3 className="text-sm font-semibold text-slate-500 uppercase flex items-center space-x-2">
              <Bookmark className="h-4 w-4" />
              <span>Active Book Goals</span>
            </h3>

            <div className="space-y-4">
              <div>
                <div className="flex justify-between items-center text-xs font-semibold mb-1">
                  <span>Designing Data-Intensive Applications</span>
                  <span className="text-slate-500">Page 180 / 450 (40%)</span>
                </div>
                <div className="w-full bg-slate-100 dark:bg-slate-800 rounded-full h-2">
                  <div className="bg-primary-600 h-2 rounded-full" style={{ width: '40%' }}></div>
                </div>
              </div>
              
              <div className="grid grid-cols-2 gap-4 pt-2 text-xs font-semibold text-slate-500">
                <div className="flex items-center space-x-2">
                  <Clock className="h-4 w-4" />
                  <span>Est. Completion: Jul 24, 2026</span>
                </div>
                <div className="flex items-center space-x-2">
                  <Flame className="h-4 w-4 text-orange-500" />
                  <span>Current Streak: 6 Days</span>
                </div>
              </div>
            </div>
          </Card>

          {/* Goal trackers */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {habits.map((item, idx) => (
              <Card key={idx} className="p-6 space-y-4">
                <div className="flex justify-between items-start">
                  <h4 className="font-bold text-sm text-slate-700 dark:text-slate-300">{item.title}</h4>
                  <span className="text-xs font-bold text-primary-600">{item.value} / {item.target}</span>
                </div>
                <div className="w-full bg-slate-100 dark:bg-slate-800 rounded-full h-2">
                  <div className={`${item.color} h-2 rounded-full`} style={{ width: `${item.progress}%` }}></div>
                </div>
              </Card>
            ))}
          </div>
        </div>

        {/* Coach Advice Sidebars */}
        <div className="lg:col-span-1 space-y-6">
          <Card className="p-6 space-y-4 bg-primary-600 text-white border-none relative overflow-hidden">
            <div className="absolute -right-8 -bottom-8 h-32 w-32 rounded-full bg-primary-500/20 blur-xl"></div>
            <div className="h-10 w-10 rounded-xl bg-white/10 flex items-center justify-center">
              <Flame className="h-6 w-6" />
            </div>
            <h3 className="font-bold text-base">Streak Alert!</h3>
            <p className="text-xs text-primary-100 leading-relaxed">
              You read for 45 minutes yesterday. Read today to keep your 6-day streak alive and claim the Java Specialist badge!
            </p>
            <Button variant="secondary" className="w-full text-xs text-primary-600 bg-white hover:bg-slate-50 py-2">
              Log Session Minutes
            </Button>
          </Card>

          <Card className="p-6 space-y-3">
            <h3 className="text-sm font-semibold text-slate-500 uppercase flex items-center space-x-2">
              <Award className="h-4 w-4" />
              <span>Mentor Insights Advice</span>
            </h3>
            <p className="text-xs text-slate-500 leading-relaxed">
              Based on your reading velocity (avg. 1.2 minutes per page), you will require approximately 6.5 hours of dedicated study to finish chapter 5. Propose reading 30 pages daily.
            </p>
          </Card>
        </div>

      </div>

    </div>
  );
};
