import type { WorkstreamItem as WorkstreamItemContract } from '../types';
import { ActionFeedbackItem } from './ActionFeedbackItem';
import { StreamStatusItem } from './StreamStatusItem';
import { TraceLinkList } from './TraceLinkList';

type WorkstreamItemCardProps = {
  item: WorkstreamItemContract;
  onOpenSurface?: (surfaceId: string) => void;
};

export function WorkstreamItemCard({ item, onOpenSurface }: WorkstreamItemCardProps) {
  if (item.kind === 'action-feedback') {
    return <ActionFeedbackItem item={item} onOpenSurface={onOpenSurface} />;
  }

  if (item.kind === 'workflow-status' || item.kind === 'system-status' || item.status === 'stale') {
    return <StreamStatusItem item={item} />;
  }

  return (
    <article id={item.itemId} className={`ds-card workstream-item ${item.kind}`} aria-labelledby={`${item.itemId}-title`} tabIndex={-1}>
      <p className="eyebrow">{item.kind.replace(/-/g, ' ')}</p>
      <h3 id={`${item.itemId}-title`}>{item.title ?? item.itemId}</h3>
      {item.body && <p>{item.body}</p>}
      {item.status && <span className="status-pill info">{item.status.replace(/-/g, ' ')}</span>}
      {item.surfaceId && (
        <button type="button" className="link-button" onClick={() => onOpenSurface?.(item.surfaceId!)}>
          Open structured surface
        </button>
      )}
      <TraceLinkList traceIds={item.traceIds} traceLinks={item.traceLinks} />
    </article>
  );
}
