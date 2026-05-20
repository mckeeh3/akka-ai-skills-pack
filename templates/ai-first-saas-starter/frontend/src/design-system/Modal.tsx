import type { ReactNode } from 'react';
import { Button } from './Button';

export function Modal({ open, title, children, onClose }: { open: boolean; title: string; children: ReactNode; onClose: () => void }) {
  if (!open) return null;
  return (
    <div className="modal-backdrop" role="presentation">
      <section className="modal-panel" role="dialog" aria-modal="true" aria-labelledby="modal-title">
        <header className="modal-header">
          <h2 id="modal-title">{title}</h2>
          <Button tone="ghost" aria-label="Close dialog" onClick={onClose}>×</Button>
        </header>
        {children}
      </section>
    </div>
  );
}
