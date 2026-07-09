import React from 'react';
import { Link } from 'react-router-dom';
import { BookOpen, Sparkles, Shield, Cpu, ChevronRight } from 'lucide-react';

export const Landing: React.FC = () => {
  return (
    <div className="relative bg-slate-50 dark:bg-slate-950 overflow-hidden">
      
      {/* Decorative gradients */}
      <div className="absolute top-0 left-1/2 -translate-x-1/2 w-full max-w-7xl h-[600px] bg-gradient-to-b from-primary-500/10 via-transparent to-transparent blur-3xl pointer-events-none"></div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24 pb-20 text-center relative z-10">
        
        {/* Badge */}
        <div className="inline-flex items-center space-x-2 bg-primary-50 dark:bg-primary-950/40 text-primary-700 dark:text-primary-400 px-4 py-1.5 rounded-full text-xs font-semibold mb-6 border border-primary-200/30">
          <Sparkles className="h-3.5 w-3.5" />
          <span>Enterprise Agentic AI Integration ready</span>
        </div>

        {/* Title */}
        <h1 className="text-4xl sm:text-6xl font-extrabold tracking-tight text-slate-900 dark:text-white max-w-4xl mx-auto leading-tight">
          Intelligent Knowledge Ecosystems for <span className="bg-gradient-to-r from-primary-600 to-indigo-500 bg-clip-text text-transparent">Enterprise Libraries</span>
        </h1>

        {/* Subtitle */}
        <p className="mt-6 text-lg text-slate-500 max-w-2xl mx-auto">
          EAKIP converts unstructured publications into semantic intelligence clusters, routing questions to specialized autonomous AI agents.
        </p>

        {/* Call to Actions */}
        <div className="mt-10 flex flex-col sm:flex-row justify-center items-center gap-4">
          <Link to="/register" className="w-full sm:w-auto bg-primary-600 hover:bg-primary-700 text-white px-8 py-3.5 rounded-xl font-medium transition-all shadow-lg shadow-primary-500/20 flex items-center justify-center space-x-2">
            <span>Get Started</span>
            <ChevronRight className="h-5 w-5" />
          </Link>
          <Link to="/login" className="w-full sm:w-auto border border-slate-300 dark:border-slate-800 hover:bg-slate-50 dark:hover:bg-slate-900 text-slate-700 dark:text-slate-300 px-8 py-3.5 rounded-xl font-medium transition-all flex items-center justify-center">
            Log in to Portal
          </Link>
        </div>

        {/* Feature Grid */}
        <div className="mt-24 grid grid-cols-1 md:grid-cols-3 gap-8">
          <div className="p-6 rounded-2xl border border-slate-200/50 dark:border-slate-800/40 bg-white dark:bg-slate-900/50 backdrop-blur-sm text-left space-y-3">
            <div className="h-10 w-10 rounded-xl bg-primary-50 dark:bg-primary-950/40 text-primary-600 dark:text-primary-400 flex items-center justify-center">
              <Cpu className="h-5 w-5" />
            </div>
            <h3 className="font-bold text-lg">Agentic AI Routing</h3>
            <p className="text-slate-500 text-sm">Orchestrates workflows between discovery, summarization, planning, and coaching agent interfaces.</p>
          </div>

          <div className="p-6 rounded-2xl border border-slate-200/50 dark:border-slate-800/40 bg-white dark:bg-slate-900/50 backdrop-blur-sm text-left space-y-3">
            <div className="h-10 w-10 rounded-xl bg-primary-50 dark:bg-primary-950/40 text-primary-600 dark:text-primary-400 flex items-center justify-center">
              <BookOpen className="h-5 w-5" />
            </div>
            <h3 className="font-bold text-lg">Verifiable Citations RAG</h3>
            <p className="text-slate-500 text-sm">Retrieve relevant book segments with page-number anchors, guaranteeing complete hallucination guardrails.</p>
          </div>

          <div className="p-6 rounded-2xl border border-slate-200/50 dark:border-slate-800/40 bg-white dark:bg-slate-900/50 backdrop-blur-sm text-left space-y-3">
            <div className="h-10 w-10 rounded-xl bg-primary-50 dark:bg-primary-950/40 text-primary-600 dark:text-primary-400 flex items-center justify-center">
              <Shield className="h-5 w-5" />
            </div>
            <h3 className="font-bold text-lg">RBAC Gateways</h3>
            <p className="text-slate-500 text-sm">Distinct dashboard tools configured specifically for Students, Faculty, Librarians, and Administrators.</p>
          </div>
        </div>

      </div>

    </div>
  );
};
