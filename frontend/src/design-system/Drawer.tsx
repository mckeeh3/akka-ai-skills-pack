import type { ReactNode } from 'react';
import { Button } from './Button';

export function Drawer({ open, title, children, onClose }: { open: boolean; title: string; children: ReactNode; onClose: () => void }) {
  if (!open) return null;
  return (
    <div className="drawer-backdrop" role="presentation">
      <aside className="drawer-panel" role="dialog" aria-modal="true" aria-labelledby="drawer-title">
        <header className="drawer-header">
          <h2 id="drawer-title">{title}</h2>
          <Button tone="ghost" aria-label="Close drawer" onClick={onClose}>×</Button>
        </header>
        {children}
      </aside>
    </div>
  );
}
