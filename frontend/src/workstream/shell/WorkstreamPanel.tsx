import type { ReactNode } from 'react';
import type { FunctionalAgentSummary, WorkstreamItem } from '../types';

type WorkstreamPanelProps = {
  selectedAgent?: FunctionalAgentSummary;
  items?: WorkstreamItem[];
  children?: ReactNode;
};

export function WorkstreamPanel({ selectedAgent, items = [], children }: WorkstreamPanelProps) {
  return (
    <main id="main-content" className="content workstream-panel" aria-labelledby="workstream-panel-title" tabIndex={-1} data-workstream-scroll-container="true">
      <h1 id="workstream-panel-title" className="sr-only">{selectedAgent?.label ?? 'Select a workstream'}</h1>
      {children ?? (
        <section className="flow-stack workstream-flow" aria-label="Workstream interaction flow">
          {items.map((item) => (
            <article key={item.itemId} id={item.itemId} className={`ds-card workstream-item ${item.kind}`} tabIndex={-1}>
              <p className="eyebrow">{item.kind.replace(/-/g, ' ')}</p>
              <h3>{item.title ?? item.itemId}</h3>
              {item.body && <p>{item.body}</p>}
              {item.status && <span className="status-pill info">{item.status}</span>}
            </article>
          ))}
        </section>
      )}
    </main>
  );
}
