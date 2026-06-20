import type { WorkstreamItem } from '../types';
import { TraceLinkList } from './TraceLinkList';

type StreamStatusItemProps = {
  item: WorkstreamItem;
};

const statusTone: Record<string, string> = {
  working: 'info',
  'waiting-for-human': 'warning',
  blocked: 'critical',
  ready: 'success',
  failed: 'critical',
  stale: 'warning'
};

export function StreamStatusItem({ item }: StreamStatusItemProps) {
  const tone = item.status ? statusTone[item.status] ?? 'info' : 'info';
  return (
    <article id={item.itemId} className={`ds-card workstream-item ${item.kind}`} aria-labelledby={`${item.itemId}-title`} tabIndex={-1}>
      <p className="eyebrow">{item.kind.replace(/-/g, ' ')}</p>
      <h3 id={`${item.itemId}-title`}>{item.title ?? item.itemId}</h3>
      {item.body && <p>{item.body}</p>}
      {item.status && <span className={`status-pill ${tone}`}>{item.status.replace(/-/g, ' ')}</span>}
      {item.surfaceId && <a href={`/ui?surfaceId=${encodeURIComponent(item.surfaceId)}`}>Open view</a>}
      <TraceLinkList traceIds={item.traceIds} traceLinks={item.traceLinks} />
    </article>
  );
}
