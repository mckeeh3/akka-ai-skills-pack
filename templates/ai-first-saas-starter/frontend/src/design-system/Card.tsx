import type { ReactNode } from 'react';

export function Card({ title, subtitle, children, className = '' }: { title?: ReactNode; subtitle?: ReactNode; children: ReactNode; className?: string }) {
  return (
    <section className={`ds-card ${className}`.trim()}>
      {(title || subtitle) && (
        <header className="ds-card-header">
          {title && <h2>{title}</h2>}
          {subtitle && <p>{subtitle}</p>}
        </header>
      )}
      {children}
    </section>
  );
}
