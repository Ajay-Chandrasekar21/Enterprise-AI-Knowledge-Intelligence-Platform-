import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useSelector, useDispatch } from 'react-redux';
import { RootState } from '@/store';
import { setUser } from '@/store/slices/authSlice';
import { Card } from '@/components/ui/Card';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';
import { toast } from 'react-toastify';
import { User, Award, Shield } from 'lucide-react';
import api from '@/services/api';

interface ProfileFields {
  firstName: string;
  lastName: string;
  department: string;
  readingInterests: string;
}

export const Profile: React.FC = () => {
  const { user } = useSelector((state: RootState) => state.auth);
  const dispatch = useDispatch();
  const [isUpdating, setIsUpdating] = useState(false);

  const profile = user?.profile;
  const initialInterests = profile?.preferences ? JSON.parse(profile.preferences).readingInterests || '' : '';

  const { register, handleSubmit, formState: { errors } } = useForm<ProfileFields>({
    defaultValues: {
      firstName: profile?.firstName || '',
      lastName: profile?.lastName || '',
      department: profile?.department || '',
      readingInterests: initialInterests,
    },
  });

  const onSubmit = async (data: ProfileFields) => {
    setIsUpdating(true);
    try {
      const preferencesObj = { readingInterests: data.readingInterests };
      const response = await api.put('/profiles', {
        firstName: data.firstName,
        lastName: data.lastName,
        department: data.department,
        preferences: JSON.stringify(preferencesObj),
      });

      if (response.data?.data && user) {
        dispatch(setUser({
          ...user,
          profile: response.data.data,
        }));
        toast.success('Profile details saved successfully!');
      }
    } catch (e) {
      toast.error('Failed to update profile settings');
    } finally {
      setIsUpdating(false);
    }
  };

  return (
    <div className="space-y-6 max-w-4xl">
      <div>
        <h1 className="text-2xl font-bold">Profile Settings</h1>
        <p className="text-slate-500 text-sm">Update personal parameters, department alignments, and reading interests tags</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Profile Card */}
        <Card className="p-6 lg:col-span-1 text-center space-y-4">
          <div className="h-20 w-20 mx-auto rounded-full bg-primary-100 dark:bg-primary-950 flex items-center justify-center text-primary-600 dark:text-primary-400 font-extrabold text-2xl">
            {user?.username.substring(0, 2).toUpperCase()}
          </div>
          <div>
            <h3 className="font-bold text-lg">{profile?.firstName} {profile?.lastName}</h3>
            <p className="text-xs text-slate-400">@{user?.username}</p>
          </div>
          <div className="py-2 border-t border-b border-slate-200/50 dark:border-slate-800/40 text-left space-y-2 text-xs font-semibold">
            <div className="flex items-center space-x-2 text-slate-500">
              <Shield className="h-4 w-4" />
              <span>Library Card: {profile?.libraryCardNumber || 'N/A'}</span>
            </div>
            <div className="flex items-center space-x-2 text-slate-500">
              <Award className="h-4 w-4" />
              <span>Clearance: {user?.role}</span>
            </div>
          </div>
        </Card>

        {/* Profile details form */}
        <Card className="p-6 lg:col-span-2">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Input
                label="First Name"
                type="text"
                error={errors.firstName?.message}
                {...register('firstName', { required: 'First name is required' })}
              />
              <Input
                label="Last Name"
                type="text"
                error={errors.lastName?.message}
                {...register('lastName', { required: 'Last name is required' })}
              />
            </div>

            <Input
              label="Department (Optional)"
              type="text"
              error={errors.department?.message}
              {...register('department')}
            />

            <div className="flex flex-col space-y-1.5">
              <label className="text-xs font-semibold text-slate-500 dark:text-slate-400">Reading Interests (Comma-separated)</label>
              <textarea
                className="w-full px-4 py-2.5 rounded-xl border bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-800 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all min-h-24"
                placeholder="e.g. Algorithms, Machine Learning, Compiler Design"
                {...register('readingInterests')}
              />
            </div>

            <div className="pt-4 flex justify-end">
              <Button type="submit" isLoading={isUpdating}>
                Save Profile
              </Button>
            </div>
          </form>
        </Card>

      </div>
    </div>
  );
};
