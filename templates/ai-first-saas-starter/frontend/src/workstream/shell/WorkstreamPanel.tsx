import type { ReactNode } from 'react';
import type { FunctionalAgentSummary, WorkstreamItem } from '../types';

type WorkstreamPanelProps = {
  selectedAgent?: FunctionalAgentSummary;
  items?: WorkstreamItem[];
  children?: ReactNode;
};

export function WorkstreamPanel({ selectedAgent, items = [], children }: WorkstreamPanelProps) {
  return (
    <main id="main-content" className="content workstream-panel" aria-labelledby="workstream-panel-title" tabIndex={-1}>
      <section className="page-header">
        <p className="eyebrow">Continuous workstream</p>
        <h2 id="workstream-panel-title">{selectedAgent?.label ?? 'Select a functional agent'}</h2>
        <p>{selectedAgent?.purpose ?? 'Choose a role-authorized functional agent to open its workstream.'}</p>
      </section>
      <section className="flow-stack" aria-label="Workstream items">
        {children ?? items.map((item) => (
          <article key={item.itemId} id={item.itemId} className={`ds-card workstream-item ${item.kind}`} tabIndex={-1}>
            <p className="eyebrow">{item.kind.replace(/-/g, ' ')}</p>
            <h3>{item.title ?? item.itemId}</h3>
            {item.body && <p>{item.body}</p>}
            {item.status && <span className="status-pill info">{item.status}</span>}
          </article>
        ))}
      </section>
    </main>
  );
}
