import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { Button } from '../Button';

describe('Button component', () => {
  it('renders button with correct text children', () => {
    render(<Button>Click Me</Button>);
    expect(screen.getByRole('button', { name: /click me/i })).toBeInTheDocument();
  });

  it('triggers onClick handler when clicked', () => {
    const handleClick = vi.fn();
    render(<Button onClick={handleClick}>Click Me</Button>);
    
    fireEvent.click(screen.getByRole('button'));
    expect(handleClick).toHaveBeenCalledTimes(1);
  });

  it('disables interactions and renders loading spinner when loading', () => {
    render(<Button isLoading>Click Me</Button>);
    
    const btn = screen.getByRole('button');
    expect(btn).toBeDisabled();
    expect(screen.getByText(/loading/i)).toBeInTheDocument();
  });
});
