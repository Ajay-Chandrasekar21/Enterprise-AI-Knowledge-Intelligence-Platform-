import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { useNavigate, Link } from 'react-router-dom';
import api from '@/services/api';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { BookOpen, AlertCircle } from 'lucide-react';

const registerSchema = z.object({
  username: z.string().min(3, 'Username must be at least 3 characters'),
  email: z.string().email('Invalid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
  firstName: z.string().min(1, 'First name is required'),
  lastName: z.string().min(1, 'Last name is required'),
  department: z.string().optional(),
  role: z.enum(['STUDENT', 'FACULTY', 'LIBRARIAN', 'ADMIN']),
});

type RegisterFields = z.infer<typeof registerSchema>;

export const Register: React.FC = () => {
  const navigate = useNavigate();
  const [apiError, setApiError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { register, handleSubmit, formState: { errors } } = useForm<RegisterFields>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      role: 'STUDENT',
    },
  });

  const onSubmit = async (data: RegisterFields) => {
    setIsSubmitting(true);
    setApiError(null);
    try {
      await api.post('/auth/register', data);
      navigate('/login?registered=true');
    } catch (err: any) {
      setApiError(err.response?.data?.message || 'Registration failed. Try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div class="min-h-screen flex items-center justify-center p-4 bg-slate-50 dark:bg-slate-950">
      <Card class="w-full max-w-lg p-8 glass-card">
        
        {/* Header */}
        <div class="flex flex-col items-center space-y-2 mb-8 text-center">
          <div class="h-12 w-12 rounded-2xl bg-primary-600 flex items-center justify-center text-white shadow-lg shadow-primary-500/20">
            <BookOpen class="h-6 w-6" />
          </div>
          <h1 class="text-2xl font-bold">Profile Creation</h1>
          <p class="text-slate-500 text-xs">Create your portal login and profile mappings</p>
        </div>

        {/* Global errors */}
        {apiError && (
          <div class="mb-6 p-4 rounded-xl bg-red-50 dark:bg-red-950/10 border border-red-200/50 dark:border-red-900/20 text-red-600 dark:text-red-400 flex items-start space-x-3 text-xs font-semibold">
            <AlertCircle class="h-5 w-5 shrink-0" />
            <span>{apiError}</span>
          </div>
        )}

        {/* Register Form */}
        <form onSubmit={handleSubmit(onSubmit)} class="space-y-4">
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Input
              label="First Name"
              type="text"
              placeholder="e.g. Jane"
              error={errors.firstName?.message}
              {...register('firstName')}
            />
            <Input
              label="Last Name"
              type="text"
              placeholder="e.g. Smith"
              error={errors.lastName?.message}
              {...register('lastName')}
            />
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Input
              label="Username"
              type="text"
              placeholder="e.g. janesmith"
              error={errors.username?.message}
              {...register('username')}
            />
            <Input
              label="Email Address"
              type="email"
              placeholder="e.g. jsmith@academy.edu"
              error={errors.email?.message}
              {...register('email')}
            />
          </div>

          <Input
            label="Password"
            type="password"
            placeholder="••••••••"
            error={errors.password?.message}
            {...register('password')}
          />

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Input
              label="Department (Optional)"
              type="text"
              placeholder="e.g. Bio-Chemistry"
              error={errors.department?.message}
              {...register('department')}
            />
            
            {/* Role Select Dropdown */}
            <div class="flex flex-col space-y-1.5">
              <label class="text-xs font-semibold text-slate-500 dark:text-slate-400">Portal Role</label>
              <select
                className="w-full px-4 py-2.5 rounded-xl border bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-800 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all"
                {...register('role')}
              >
                <option value="STUDENT">Student</option>
                <option value="FACULTY">Faculty</option>
                <option value="LIBRARIAN">Librarian</option>
                <option value="ADMIN">Administrator</option>
              </select>
            </div>
          </div>

          <Button type="submit" isLoading={isSubmitting} class="w-full mt-4">
            Create Profile
          </Button>
        </form>

        <div class="mt-6 text-center text-xs font-medium text-slate-500">
          <span>Already have an account? </span>
          <Link to="/login" class="text-primary-600 dark:text-primary-400 hover:underline">Log in</Link>
        </div>

      </Card>
    </div>
  );
};
