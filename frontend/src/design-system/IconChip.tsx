import type { ReactNode } from 'react';
import type { StatusTone } from './StatusPill';

export function IconChip({ icon, label, tone = 'info' }: { icon: ReactNode; label: string; tone?: StatusTone }) {
  return (
    <span className={`icon-chip ${tone}`} aria-label={label}>
      <span aria-hidden="true">{icon}</span>
    </span>
  );
}
