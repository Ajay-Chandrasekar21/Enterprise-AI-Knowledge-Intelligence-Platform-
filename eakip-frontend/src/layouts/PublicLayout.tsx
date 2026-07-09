import React from 'react';
import { Outlet, Link } from 'react-router-dom';
import { BookOpen } from 'lucide-react';

export const PublicLayout: React.FC = () => {
  return (
    <div className="flex flex-col min-h-screen">
      {/* Public Header */}
      <header className="sticky top-0 z-40 bg-white/80 dark:bg-slate-900/80 backdrop-blur-md border-b border-slate-200/50 dark:border-slate-800/40">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <Link to="/" className="flex items-center space-x-2 text-primary-600 dark:text-primary-400 font-bold text-xl">
            <BookOpen className="h-6 w-6" />
            <span>EAKIP</span>
          </Link>
          <nav className="hidden md:flex space-x-8 text-sm font-medium">
            <Link to="/" className="text-slate-600 dark:text-slate-300 hover:text-primary-600">Home</Link>
            <Link to="/about" className="text-slate-600 dark:text-slate-300 hover:text-primary-600">About</Link>
            <Link to="/features" className="text-slate-600 dark:text-slate-300 hover:text-primary-600">Features</Link>
            <Link to="/contact" className="text-slate-600 dark:text-slate-300 hover:text-primary-600">Contact</Link>
          </nav>
          <div className="flex items-center space-x-4">
            <Link to="/login" className="text-sm font-semibold text-slate-700 dark:text-slate-200 hover:text-primary-600">Log in</Link>
            <Link to="/register" className="bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-xl text-sm font-medium transition-all shadow-sm shadow-primary-500/20">Register</Link>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-grow">
        <Outlet />
      </main>

      {/* Footer */}
      <footer className="bg-white dark:bg-slate-950 border-t border-slate-200/50 dark:border-slate-800/40 py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center text-sm text-slate-500">
          <p>&copy; {new Date().getFullYear()} EAKIP. All rights reserved. Enterprise AI Knowledge Intelligence Platform.</p>
        </div>
      </footer>
    </div>
  );
};
