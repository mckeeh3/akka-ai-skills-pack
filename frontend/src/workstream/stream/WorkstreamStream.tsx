import { useLayoutEffect } from 'react';
import type { SurfaceAction, SurfaceEnvelope, WorkstreamItem as WorkstreamItemContract } from '../types';
import { SurfaceRenderer } from '../surfaces';
import { WorkstreamItemCard } from './WorkstreamItem';

type WorkstreamStreamProps = {
  items: WorkstreamItemContract[];
  selectedItemId?: string;
  requestScrollTargetId?: string;
  surfaces?: SurfaceEnvelope<unknown>[];
  onOpenSurface?: (surfaceId: string) => void;
  onSurfaceAction?: (action: SurfaceAction, surfaceId: string) => void;
};

export function WorkstreamStream({ items, selectedItemId, requestScrollTargetId, surfaces = [], onOpenSurface, onSurfaceAction }: WorkstreamStreamProps) {
  useLayoutEffect(() => {
    if (!requestScrollTargetId) return;
    const requestSurface = document.getElementById(requestScrollTargetId) ?? document.querySelector(`[data-surface-id="${CSS.escape(requestScrollTargetId)}"]`);
    const behavior = window.matchMedia('(prefers-reduced-motion: reduce)').matches ? 'auto' : 'smooth';
    requestSurface?.scrollIntoView({ block: 'start', inline: 'nearest', behavior });
    if (requestSurface instanceof HTMLElement) requestSurface.focus({ preventScroll: true });
  }, [requestScrollTargetId, items.length, surfaces.length]);

  if (items.length === 0) {
    return (
      <section className="flow-stack workstream-stream workstream-flow empty" aria-label="Workstream interaction flow">
        <article className="ds-card workstream-item empty">
          <p className="eyebrow">Empty workstream</p>
          <h3>No workstream items yet</h3>
          <p>Use the persistent composer to ask the selected functional agent for help.</p>
        </article>
      </section>
    );
  }

  return (
    <section className="flow-stack workstream-stream workstream-flow" aria-label="Workstream interaction flow" data-selected-item-id={selectedItemId}>
      {items.map((item) => (
        <div key={item.itemId} className="workstream-flow-entry">
          {item.kind !== 'surface' && <WorkstreamItemCard item={item} onOpenSurface={onOpenSurface} />}
          {item.surfaceId && (item.kind === 'surface' || item.kind === 'markdown_response') && (
            <SurfaceRenderer envelopes={surfaces} selectedSurfaceId={item.surfaceId} onAction={onSurfaceAction} />
          )}
        </div>
      ))}
    </section>
  );
}
