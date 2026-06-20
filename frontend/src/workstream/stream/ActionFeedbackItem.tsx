import type { WorkstreamItem } from '../types';
import { TraceLinkList } from './TraceLinkList';

type ActionFeedbackItemProps = {
  item: WorkstreamItem;
  onOpenSurface?: (surfaceId: string) => void;
};

export function ActionFeedbackItem({ item, onOpenSurface }: ActionFeedbackItemProps) {
  return (
    <article id={item.itemId} className="ds-card workstream-item action-feedback" aria-labelledby={`${item.itemId}-title`} tabIndex={-1}>
      <p className="eyebrow">Action feedback</p>
      <h3 id={`${item.itemId}-title`}>{item.title ?? 'Action result'}</h3>
      {item.body && <p>{item.body}</p>}
      {item.surfaceId && (
        <button type="button" className="link-button" onClick={() => onOpenSurface?.(item.surfaceId!)}>
          Open result view
        </button>
      )}
      <TraceLinkList traceIds={item.traceIds} traceLinks={item.traceLinks} />
    </article>
  );
}
