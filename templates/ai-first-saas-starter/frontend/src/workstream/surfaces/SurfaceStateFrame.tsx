import type { ReactNode } from 'react';
import type { RegionState, SurfaceEnvelope } from '../types';

type SurfaceStateFrameProps<T> = {
  state?: RegionState<SurfaceEnvelope<T>>;
  envelope?: SurfaceEnvelope<T>;
  children?: ReactNode;
};

export function SurfaceStateFrame<T>({ state, envelope, children }: SurfaceStateFrameProps<T>) {
  if (state?.status === 'loading') {
    return <section className="ds-card surface-frame loading" aria-busy="true"><p>Loading surface…</p></section>;
  }
  if (state?.status === 'empty') {
    return <section className="ds-card surface-frame empty"><p>{state.message}</p></section>;
  }
  if (state?.status === 'forbidden') {
    return <section className="ds-card surface-frame forbidden" role="alert"><h3>Access denied</h3><p>{state.message}</p>{state.recovery && <p>{state.recovery}</p>}</section>;
  }
  if (state?.status === 'error') {
    return <section className="ds-card surface-frame error" role="alert"><h3>Surface unavailable</h3><p>{state.message}</p>{state.retryable && <p>Retry may succeed.</p>}</section>;
  }

  const visibleEnvelope = state?.status === 'ready' || state?.status === 'stale' ? state.value : envelope;
  if (!visibleEnvelope) {
    return <section className="ds-card surface-frame empty"><p>No surface selected.</p></section>;
  }

  return (
    <section id={visibleEnvelope.surfaceId} className={`structured-surface surface-frame ${visibleEnvelope.surfaceType}${visibleEnvelope.stale?.isStale || state?.status === 'stale' ? ' stale' : ''}`} aria-labelledby={`${visibleEnvelope.surfaceId}-title`} data-surface-id={visibleEnvelope.surfaceId} data-surface-version={visibleEnvelope.surfaceVersion} tabIndex={-1}>
      <header className="surface-header">
        <p className="eyebrow">{visibleEnvelope.surfaceType} · {visibleEnvelope.surfaceVersion}</p>
        <h3 id={`${visibleEnvelope.surfaceId}-title`}>{visibleEnvelope.title}</h3>
        {(visibleEnvelope.stale?.isStale || state?.status === 'stale') && <p role="status">Stale: {visibleEnvelope.stale?.reason ?? (state?.status === 'stale' ? state.message : 'Refresh recommended.')}</p>}
      </header>
      {children}
      <footer className="surface-footer">
        <p>Correlation: {visibleEnvelope.correlationId}</p>
        <p>Redaction profile: {visibleEnvelope.redaction.profile}</p>
      </footer>
    </section>
  );
}
