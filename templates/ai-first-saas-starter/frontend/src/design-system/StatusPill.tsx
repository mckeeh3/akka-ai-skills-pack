export type StatusTone = 'success' | 'warning' | 'danger' | 'info' | 'neutral';

export function StatusPill({ tone = 'neutral', children }: { tone?: StatusTone; children: string }) {
  return <span className={`status-pill ${tone}`}>{children}</span>;
}
