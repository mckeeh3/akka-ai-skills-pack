import type { ReactNode } from 'react';
import type { FunctionalAgentSummary, WorkstreamItem } from '../types';
import { hasWorkstreamText, renderWorkstreamText } from '../stream/renderWorkstreamText';

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
          {items.map((item) => {
            const title = renderWorkstreamText(item.title);
            const body = renderWorkstreamText(item.body);
            return (
              <article key={item.itemId} id={item.itemId} className={`ds-card workstream-item ${item.kind}`} tabIndex={-1}>
                <p className="eyebrow">{item.kind.replace(/-/g, ' ')}</p>
                <h3>{title || item.itemId}</h3>
                {hasWorkstreamText(item.body) && <p>{body}</p>}
                {item.status && <span className="status-pill info">{item.status}</span>}
              </article>
            );
          })}
        </section>
      )}
    </main>
  );
}
