import React from 'react';

interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  glass?: boolean;
}

export const Card: React.FC<CardProps> = ({
  children,
  glass = false,
  className = '',
  ...props
}) => {
  const style = glass 
    ? 'glass-card' 
    : 'bg-white dark:bg-slate-900 border border-slate-200/50 dark:border-slate-800/40 rounded-2xl shadow-sm p-6';

  return (
    <div className={`${style} ${className}`} {...props}>
      {children}
    </div>
  );
};
