import { useEffect, useLayoutEffect, useRef, useState } from 'react';
import type { SurfaceAction, SurfaceEnvelope, WorkstreamItem as WorkstreamItemContract } from '../types';
import { SurfaceRenderer } from '../surfaces';
import { WorkstreamItemCard } from './WorkstreamItem';

type WorkstreamStreamProps = {
  items: WorkstreamItemContract[];
  selectedItemId?: string;
  requestScrollTargetId?: string;
  autoAnchorPaused?: boolean;
  surfaces?: SurfaceEnvelope<unknown>[];
  onOpenSurface?: (surfaceId: string) => void;
  onSurfaceAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
  onSurfaceFieldValueChange?: (fieldId: string, value: string, surfaceId: string) => void;
  onAutoAnchorPaused?: (requestScrollTargetId: string) => void;
};

export function WorkstreamStream({ items, selectedItemId, requestScrollTargetId, autoAnchorPaused = false, surfaces = [], onOpenSurface, onSurfaceAction, onSurfaceFieldValueChange, onAutoAnchorPaused }: WorkstreamStreamProps) {
  const streamRef = useRef<HTMLElement | null>(null);
  const [pausedAnchorTargetId, setPausedAnchorTargetId] = useState<string | undefined>();
  const shouldAutoAnchor = Boolean(requestScrollTargetId && !autoAnchorPaused && pausedAnchorTargetId !== requestScrollTargetId);

  useEffect(() => {
    setPausedAnchorTargetId(undefined);
  }, [requestScrollTargetId]);

  useLayoutEffect(() => {
    if (!requestScrollTargetId || !shouldAutoAnchor) return;
    const requestSurface = findRequestScrollTarget(requestScrollTargetId, streamRef.current);
    if (!requestSurface) return;
    const behavior = window.matchMedia('(prefers-reduced-motion: reduce)').matches ? 'auto' : 'smooth';
    scrollTargetToContainerTop(requestSurface, streamRef.current, behavior);
    requestSurface.focus({ preventScroll: true });
  }, [requestScrollTargetId, shouldAutoAnchor, items.length, surfaces.length]);

  function pauseAutoAnchorForManualScroll() {
    if (!requestScrollTargetId || pausedAnchorTargetId === requestScrollTargetId) return;
    setPausedAnchorTargetId(requestScrollTargetId);
    onAutoAnchorPaused?.(requestScrollTargetId);
  }

  if (items.length === 0) {
    return (
      <section ref={streamRef} className="flow-stack workstream-stream workstream-flow empty" aria-label="Workstream interaction flow">
        <article className="ds-card workstream-item empty">
          <p className="eyebrow">Empty workstream</p>
          <h3>No workstream items yet</h3>
          <p>Use the persistent composer to ask the selected functional agent for help.</p>
        </article>
      </section>
    );
  }

  return (
    <section
      ref={streamRef}
      className="flow-stack workstream-stream workstream-flow"
      aria-label="Workstream interaction flow"
      data-selected-item-id={selectedItemId}
      data-request-scroll-target-id={requestScrollTargetId}
      data-auto-anchor-paused={autoAnchorPaused || pausedAnchorTargetId === requestScrollTargetId ? 'true' : 'false'}
      onWheel={pauseAutoAnchorForManualScroll}
      onTouchMove={pauseAutoAnchorForManualScroll}
      onKeyDown={(event) => {
        if (isManualScrollKey(event.key)) pauseAutoAnchorForManualScroll();
      }}
    >
      {items.map((item) => (
        <div key={item.itemId} className="workstream-flow-entry">
          {item.kind !== 'surface' && <WorkstreamItemCard item={item} onOpenSurface={onOpenSurface} />}
          {item.surfaceId && (item.kind === 'surface' || item.kind === 'markdown_response') && (
            <SurfaceRenderer envelopes={surfaces} selectedSurfaceId={item.surfaceId} onAction={onSurfaceAction} onFieldValueChange={onSurfaceFieldValueChange} />
          )}
        </div>
      ))}
    </section>
  );
}

function isManualScrollKey(key: string): boolean {
  return ['ArrowDown', 'ArrowUp', 'PageDown', 'PageUp', 'Home', 'End', ' '].includes(key);
}

function findRequestScrollTarget(requestScrollTargetId: string, stream: HTMLElement | null): HTMLElement | null {
  const escapedTargetId = escapeCssIdentifier(requestScrollTargetId);
  const scopedTarget = stream?.querySelector<HTMLElement>(`#${escapedTargetId}, [data-surface-id="${escapedTargetId}"]`);
  return scopedTarget ?? document.getElementById(requestScrollTargetId) ?? document.querySelector<HTMLElement>(`[data-surface-id="${escapedTargetId}"]`);
}

function scrollTargetToContainerTop(target: HTMLElement, stream: HTMLElement | null, behavior: ScrollBehavior) {
  const scrollContainer = target.closest<HTMLElement>('[data-workstream-scroll-container="true"]') ?? stream?.closest<HTMLElement>('[data-workstream-scroll-container="true"]');
  if (!scrollContainer) {
    const targetTop = target.getBoundingClientRect().top + window.scrollY;
    window.scrollTo({ top: targetTop, behavior });
    return;
  }

  const containerRect = scrollContainer.getBoundingClientRect();
  const targetRect = target.getBoundingClientRect();
  const scrollPaddingTop = Number.parseFloat(window.getComputedStyle(scrollContainer).scrollPaddingTop || '0') || 0;
  scrollContainer.scrollTo({
    top: targetRect.top - containerRect.top + scrollContainer.scrollTop - scrollPaddingTop,
    behavior
  });
}

function escapeCssIdentifier(value: string): string {
  return typeof CSS !== 'undefined' && typeof CSS.escape === 'function' ? CSS.escape(value) : value.replace(/[^a-zA-Z0-9_-]/g, '\\$&');
}
