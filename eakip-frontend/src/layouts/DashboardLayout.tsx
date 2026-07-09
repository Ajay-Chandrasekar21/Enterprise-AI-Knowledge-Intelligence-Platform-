import React, { useState, useEffect } from 'react';
import { Outlet, Link, useNavigate, useLocation } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { RootState } from '@/store';
import { logout } from '@/store/slices/authSlice';
import { 
  BookOpen, LayoutDashboard, Book, History, Calendar, Bell, 
  Sun, Moon, LogOut, Menu, X, ChevronRight, User, Settings, Activity, Cpu, Share2, Sparkles, Compass, Search, GitBranch, Wrench, Network, Brain, GitPullRequest, Building 
} from 'lucide-react';

export const DashboardLayout: React.FC = () => {
  const { user } = useSelector((state: RootState) => state.auth);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const [isDarkMode, setIsDarkMode] = useState(
    localStorage.getItem('theme') === 'dark' || 
    (!localStorage.getItem('theme') && window.matchMedia('(prefers-color-scheme: dark)').matches)
  );
  const [showNotifications, setShowNotifications] = useState(false);

  // Sync theme changes with DOM class
  useEffect(() => {
    if (isDarkMode) {
      document.documentElement.classList.add('dark');
      localStorage.setItem('theme', 'dark');
    } else {
      document.documentElement.classList.remove('dark');
      localStorage.setItem('theme', 'light');
    }
  }, [isDarkMode]);

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  const navItems = [
    { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { name: 'Catalog', path: '/books', icon: Book },
    { name: 'Borrowings', path: '/borrowed', icon: History },
    { name: 'Reservations', path: '/reservations', icon: Calendar },
    { name: 'Recommendations', path: '/recommendations', icon: Sparkles },
    { name: 'Reading Coach', path: '/coach', icon: Compass },
    { name: 'Semantic Search', path: '/semantic-search', icon: Search },
    { name: 'Planning Engine', path: '/planning', icon: GitBranch },
    { name: 'Tool Marketplace', path: '/tools', icon: Wrench },
    { name: 'MCP Gateway', path: '/mcp', icon: Network },
    { name: 'Memory & Learning', path: '/memory', icon: Brain },
    { name: 'Workflow Engine', path: '/workflow', icon: GitPullRequest },
    { name: 'SaaS Admin', path: '/saas', icon: Building },
    { name: 'Knowledge Center', path: '/rag', icon: Share2 },
    { name: 'AI Console', path: '/ai', icon: Cpu },
    { name: 'Analytics', path: '/analytics', icon: Activity },
    { name: 'Profile', path: '/profile', icon: User },
    { name: 'Settings', path: '/settings', icon: Settings },
  ];

  // Dynamic breadcrumbs
  const pathnames = location.pathname.split('/').filter((x) => x);

  return (
    <div className="min-h-screen flex bg-slate-50 text-slate-900 dark:bg-slate-950 dark:text-slate-50 transition-colors duration-200">
      
      {/* Sidebar - Desktop */}
      <aside className={`fixed top-0 bottom-0 left-0 z-30 w-64 bg-white dark:bg-slate-900 border-r border-slate-200/50 dark:border-slate-800/40 transform transition-transform duration-300 ease-in-out ${isSidebarOpen ? 'translate-x-0' : '-translate-x-full'} lg:translate-x-0 lg:static`}>
        <div className="h-16 flex items-center px-6 border-b border-slate-200/50 dark:border-slate-800/40">
          <Link to="/dashboard" className="flex items-center space-x-2 text-primary-600 dark:text-primary-400 font-bold text-xl">
            <BookOpen className="h-6 w-6" />
            <span>EAKIP Portal</span>
          </Link>
        </div>
        
        {/* User Card */}
        <div className="p-4 border-b border-slate-200/50 dark:border-slate-800/40">
          <div className="flex items-center space-x-3 p-2 rounded-xl bg-slate-50 dark:bg-slate-800/40">
            <div className="h-10 w-10 rounded-full bg-primary-100 dark:bg-primary-950 flex items-center justify-center text-primary-600 dark:text-primary-400 font-bold">
              {user?.username.substring(0, 2).toUpperCase()}
            </div>
            <div className="overflow-hidden">
              <h4 className="font-medium text-sm truncate">{user?.username}</h4>
              <span className="text-xs text-slate-500 dark:text-slate-400 font-semibold">{user?.role}</span>
            </div>
          </div>
        </div>

        {/* Navigation list */}
        <nav className="flex-1 px-4 py-4 space-y-1 overflow-y-auto">
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = location.pathname === item.path;
            return (
              <Link
                key={item.name}
                to={item.path}
                className={`flex items-center space-x-3 px-4 py-3 rounded-xl text-sm font-medium transition-all ${
                  isActive 
                    ? 'bg-primary-50 text-primary-600 dark:bg-primary-950/40 dark:text-primary-400' 
                    : 'text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800/50 hover:text-slate-900 dark:hover:text-slate-100'
                }`}
              >
                <Icon className="h-5 w-5" />
                <span>{item.name}</span>
              </Link>
            );
          })}
        </nav>

        {/* Footer actions */}
        <div className="p-4 border-t border-slate-200/50 dark:border-slate-800/40">
          <button
            onClick={handleLogout}
            className="flex items-center space-x-3 w-full px-4 py-3 rounded-xl text-sm font-medium text-red-600 hover:bg-red-50 dark:hover:bg-red-950/20 transition-all"
          >
            <LogOut className="h-5 w-5" />
            <span>Sign Out</span>
          </button>
        </div>
      </aside>

      {/* Main Panel Wrapper */}
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        
        {/* Navbar */}
        <header className="h-16 bg-white dark:bg-slate-900 border-b border-slate-200/50 dark:border-slate-800/40 flex items-center justify-between px-6 z-20">
          <div className="flex items-center space-x-4">
            <button 
              onClick={() => setIsSidebarOpen(!isSidebarOpen)}
              className="text-slate-500 hover:text-slate-700 dark:hover:text-slate-300 lg:hidden"
            >
              <Menu className="h-6 w-6" />
            </button>
            
            {/* Breadcrumbs */}
            <nav className="hidden sm:flex items-center space-x-2 text-xs font-medium text-slate-500">
              <Link to="/dashboard" className="hover:text-primary-600">Home</Link>
              {pathnames.map((value, index) => {
                const to = `/${pathnames.slice(0, index + 1).join('/')}`;
                const isLast = index === pathnames.length - 1;
                return (
                  <React.Fragment key={to}>
                    <ChevronRight className="h-3 w-3" />
                    {isLast ? (
                      <span className="text-slate-800 dark:text-slate-200 capitalize">{value}</span>
                    ) : (
                      <Link to={to} className="hover:text-primary-600 capitalize">{value}</Link>
                    )}
                  </React.Fragment>
                );
              })}
            </nav>
          </div>

          {/* Action Tools */}
          <div className="flex items-center space-x-4">
            {/* Theme Toggle */}
            <button 
              onClick={() => setIsDarkMode(!isDarkMode)}
              className="p-2 rounded-xl hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-500 hover:text-slate-700 dark:hover:text-slate-300 transition-colors"
            >
              {isDarkMode ? <Sun className="h-5 w-5" /> : <Moon className="h-5 w-5" />}
            </button>

            {/* Notification Bell */}
            <div className="relative">
              <button 
                onClick={() => setShowNotifications(!showNotifications)}
                className="p-2 rounded-xl hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-500 hover:text-slate-700 dark:hover:text-slate-300 transition-colors"
              >
                <Bell className="h-5 w-5" />
                <span className="absolute top-1 right-1 h-2.5 w-2.5 bg-primary-600 border-2 border-white dark:border-slate-900 rounded-full"></span>
              </button>

              {/* Notifications Popover */}
              {showNotifications && (
                <div className="absolute right-0 mt-2 w-80 bg-white dark:bg-slate-900 rounded-2xl border border-slate-200/50 dark:border-slate-800/40 shadow-lg p-4 z-50">
                  <div className="flex justify-between items-center mb-3">
                    <h3 className="font-semibold text-sm">Notifications</h3>
                    <button className="text-xs text-primary-600 dark:text-primary-400 hover:underline">Mark all read</button>
                  </div>
                  <div className="space-y-2 max-h-60 overflow-y-auto">
                    <div className="p-2.5 rounded-lg bg-slate-50 dark:bg-slate-800/50 text-xs">
                      <p className="font-medium">Welcome to EAKIP Platform</p>
                      <span className="text-slate-400">Just now</span>
                    </div>
                    <div className="p-2.5 rounded-lg bg-slate-50 dark:bg-slate-800/50 text-xs">
                      <p className="font-medium">Book 'Clean Architecture' registered</p>
                      <span className="text-slate-400">1 hour ago</span>
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>
        </header>

        {/* Content Portal */}
        <main className="flex-grow p-6 overflow-y-auto">
          <Outlet />
        </main>
      </div>

    </div>
  );
};
