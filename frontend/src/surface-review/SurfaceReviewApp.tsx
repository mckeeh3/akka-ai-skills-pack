import React from 'react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { SurfaceRenderer } from '../workstream/surfaces';
import type { SurfaceAction } from '../workstream/types';
import { findSurfaceReviewEntry, surfaceReviewEntries, surfaceReviewHref, surfaceReviewWorkstreams, type SurfaceReviewEntry } from './surfaceReviewManifest';
import { surfaceDescriptionExcerpt, workstreamDescriptions } from './surfaceReviewDescriptions';
import './surfaceReview.css';

export function SurfaceReviewApp() {
  const [selection, setSelection] = React.useState(() => readSelection());
  const [lastAction, setLastAction] = React.useState<string>();
  const selectedEntry = findSurfaceReviewEntry(selection.workstreamId, selection.surfaceId);

  React.useEffect(() => {
    const onLocationChange = () => setSelection(readSelection());
    window.addEventListener('popstate', onLocationChange);
    return () => window.removeEventListener('popstate', onLocationChange);
  }, []);

  React.useEffect(() => {
    document.documentElement.dataset.theme ||= 'aurora-light';
  }, []);

  function selectEntry(entry: SurfaceReviewEntry) {
    window.history.pushState(null, '', surfaceReviewHref(entry));
    setSelection({ workstreamId: entry.workstreamId, surfaceId: entry.surfaceId });
    setLastAction(undefined);
    requestAnimationFrame(() => document.getElementById('surface-review-title')?.focus());
  }

  function handleAction(action: SurfaceAction, surfaceId: string) {
    setLastAction(`Review mode intercepted ${action.actionId} from ${surfaceId}. No backend call was made.`);
  }

  return (
    <main className="surface-review-app" aria-labelledby="surface-review-title">
      <aside className="surface-review-nav" aria-label="Surface review index">
        <div className="surface-review-brand">
          <span className="brand-mark">SR</span>
          <div>
            <strong>Surface Review</strong>
            <span>{surfaceReviewEntries.length} rendered surfaces</span>
          </div>
        </div>
        <p className="surface-review-note">Frontend-only gallery. It renders the real structured surface components with local review envelopes, so no Akka backend or WorkOS session is required.</p>
        {surfaceReviewWorkstreams.map((workstream) => (
          <section key={workstream.workstreamId} className="surface-review-nav-group">
            <h2>{workstream.label}</h2>
            <ul>
              {workstream.surfaces.map((entry) => (
                <li key={entry.surfaceId}>
                  <button
                    type="button"
                    className={entry.surfaceId === selectedEntry.surfaceId ? 'active' : undefined}
                    onClick={() => selectEntry(entry)}
                  >
                    <span>{entry.title}</span>
                    <small>{entry.surfaceType}</small>
                  </button>
                </li>
              ))}
            </ul>
          </section>
        ))}
      </aside>
      <section className="surface-review-main">
        <header className="surface-review-header">
          <div>
            <p className="eyebrow">{selectedEntry.workstreamLabel} · {selectedEntry.surfaceType}</p>
            <h1 id="surface-review-title" tabIndex={-1}>{selectedEntry.title}</h1>
            <p><code>{selectedEntry.surfaceId}</code>{selectedEntry.contract ? <> · <code>{selectedEntry.contract}</code></> : null}</p>
          </div>
          <a className="ds-button secondary" href="/ui">Back to app</a>
        </header>
        <div className="surface-review-grid">
          <DescriptionPanel entry={selectedEntry} />
          <section className="surface-review-rendered" aria-labelledby="rendered-surface-title">
            <div className="surface-review-panel-heading">
              <div>
                <p className="eyebrow">Rendered with live frontend code</p>
                <h2 id="rendered-surface-title">Surface preview</h2>
              </div>
              <span className="surface-review-chip">backend-free</span>
            </div>
            {lastAction && <p className="surface-review-action-note" role="status">{lastAction}</p>}
            <div className="surface-review-canvas">
              <SurfaceRenderer envelope={selectedEntry.envelope} onAction={handleAction} />
            </div>
          </section>
        </div>
      </section>
    </main>
  );
}

function DescriptionPanel({ entry }: { entry: SurfaceReviewEntry }) {
  const description = workstreamDescriptions[entry.workstreamId];
  const markdown = description ? surfaceDescriptionExcerpt(description.markdown, entry.surfaceId) : `## ${entry.surfaceId}\n\nNo app-description file is mapped for this surface.`;
  return (
    <section className="surface-review-description" aria-labelledby="surface-description-title">
      <div className="surface-review-panel-heading">
        <div>
          <p className="eyebrow">App description source</p>
          <h2 id="surface-description-title">Surface description</h2>
        </div>
      </div>
      <p className="surface-review-source"><code>{entry.sourcePath}</code></p>
      <div className="surface-review-markdown">
        <ReactMarkdown remarkPlugins={[remarkGfm]}>{markdown}</ReactMarkdown>
      </div>
    </section>
  );
}

function readSelection(): { workstreamId: string | null; surfaceId: string | null } {
  const params = new URLSearchParams(window.location.search);
  return {
    workstreamId: params.get('workstream'),
    surfaceId: params.get('surface')
  };
}
