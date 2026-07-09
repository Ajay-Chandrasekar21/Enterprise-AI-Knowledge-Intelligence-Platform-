import React from 'react';

interface Column<T> {
  header: string;
  accessor: (row: T) => React.ReactNode;
}

interface TableProps<T> {
  columns: Column<T>[];
  data: T[];
  emptyMessage?: string;
}

export function Table<T>({ columns, data, emptyMessage = 'No data available' }: TableProps<T>) {
  return (
    <div className="w-full overflow-x-auto rounded-xl border border-slate-200/50 dark:border-slate-800/40 bg-white dark:bg-slate-900">
      <table className="w-full text-left border-collapse text-sm">
        <thead className="bg-slate-50 dark:bg-slate-800/40 border-b border-slate-200/50 dark:border-slate-800/40 text-slate-500 font-semibold text-xs uppercase tracking-wider">
          <tr>
            {columns.map((col, idx) => (
              <th key={idx} className="px-6 py-4">{col.header}</th>
            ))}
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-200/50 dark:divide-slate-800/40">
          {data.length > 0 ? (
            data.map((row, rowIdx) => (
              <tr key={rowIdx} className="hover:bg-slate-50/50 dark:hover:bg-slate-800/20 transition-colors">
                {columns.map((col, colIdx) => (
                  <td key={colIdx} className="px-6 py-4 text-slate-700 dark:text-slate-300">
                    {col.accessor(row)}
                  </td>
                ))}
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan={columns.length} className="px-6 py-12 text-center text-slate-400">
                {emptyMessage}
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
