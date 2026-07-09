import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { useNavigate, Link, useLocation } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '@/store';
import { loginStart, loginSuccess, loginFailure } from '@/store/slices/authSlice';
import api from '@/services/api';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { BookOpen, AlertCircle } from 'lucide-react';

const loginSchema = z.object({
  username: z.string().min(3, 'Username must be at least 3 characters'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
});

type LoginFields = z.infer<typeof loginSchema>;

export const Login: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useDispatch();
  
  const { isLoading, error } = useSelector((state: RootState) => state.auth);
  const [apiError, setApiError] = useState<string | null>(null);

  const { register, handleSubmit, formState: { errors } } = useForm<LoginFields>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFields) => {
    dispatch(loginStart());
    setApiError(null);
    try {
      const response = await api.post('/auth/login', data);
      const { user, accessToken, refreshToken } = response.data.data;
      dispatch(loginSuccess({ user, accessToken, refreshToken }));
      
      // Redirect to the page they tried to access or dashboard
      const from = (location.state as any)?.from?.pathname || '/dashboard';
      navigate(from, { replace: true });
    } catch (err: any) {
      const errMsg = err.response?.data?.message || 'Invalid credentials or network failure';
      dispatch(loginFailure(errMsg));
      setApiError(errMsg);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-4 bg-slate-50 dark:bg-slate-950">
      <Card className="w-full max-w-md p-8 glass-card">
        
        {/* Header */}
        <div className="flex flex-col items-center space-y-2 mb-8 text-center">
          <div className="h-12 w-12 rounded-2xl bg-primary-600 flex items-center justify-center text-white shadow-lg shadow-primary-500/20">
            <BookOpen className="h-6 w-6" />
          </div>
          <h1 className="text-2xl font-bold">Portal Access</h1>
          <p className="text-slate-500 text-xs">Enter credentials to enter EAKIP Platform</p>
        </div>

        {/* Global errors */}
        {apiError && (
          <div className="mb-6 p-4 rounded-xl bg-red-50 dark:bg-red-950/10 border border-red-200/50 dark:border-red-900/20 text-red-600 dark:text-red-400 flex items-start space-x-3 text-xs font-semibold">
            <AlertCircle className="h-5 w-5 shrink-0" />
            <span>{apiError}</span>
          </div>
        )}

        {/* Login Form */}
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input
            label="Username"
            type="text"
            placeholder="e.g. janesmith"
            error={errors.username?.message}
            {...register('username')}
          />
          <Input
            label="Password"
            type="password"
            placeholder="••••••••"
            error={errors.password?.message}
            {...register('password')}
          />
          
          <div className="flex items-center justify-between text-xs font-medium">
            <label className="flex items-center space-x-2 text-slate-500 hover:text-slate-700 cursor-pointer">
              <input type="checkbox" className="rounded border-slate-300 dark:border-slate-800 text-primary-600 focus:ring-primary-500" />
              <span>Remember me</span>
            </label>
            <Link to="/forgot-password" className="text-primary-600 dark:text-primary-400 hover:underline">Forgot password?</Link>
          </div>

          <Button type="submit" isLoading={isLoading} className="w-full mt-2">
            Sign In
          </Button>
        </form>

        {/* Register navigation link */}
        <div className="mt-6 text-center text-xs font-medium text-slate-500">
          <span>Don't have an account? </span>
          <Link to="/register" className="text-primary-600 dark:text-primary-400 hover:underline">Create profile</Link>
        </div>

      </Card>
    </div>
  );
};
