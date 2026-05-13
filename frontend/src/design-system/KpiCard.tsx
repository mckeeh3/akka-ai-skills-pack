import type { ReactNode } from 'react';
import { StatusPill, type StatusTone } from './StatusPill';

export function KpiCard({ label, value, detail, status, statusTone = 'info', icon }: { label: string; value: ReactNode; detail: string; status: string; statusTone?: StatusTone; icon?: ReactNode }) {
  return (
    <article className="kpi-card">
      <div className="kpi-card-top">
        <StatusPill tone={statusTone}>{status}</StatusPill>
        {icon && <span className="kpi-icon" aria-hidden="true">{icon}</span>}
      </div>
      <h2>{label}</h2>
      <div className="kpi-value">{value}</div>
      <p>{detail}</p>
    </article>
  );
}
