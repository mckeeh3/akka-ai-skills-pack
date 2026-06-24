import type { WorkstreamItem } from '../types';
import { TraceLinkList } from './TraceLinkList';
import { hasWorkstreamText, renderWorkstreamText } from './renderWorkstreamText';

type ActionFeedbackItemProps = {
  item: WorkstreamItem;
  onOpenSurface?: (surfaceId: string) => void;
};

export function ActionFeedbackItem({ item, onOpenSurface }: ActionFeedbackItemProps) {
  const title = renderWorkstreamText(item.title);
  const body = renderWorkstreamText(item.body);

  return (
    <article id={item.itemId} className="ds-card workstream-item action-feedback" aria-labelledby={`${item.itemId}-title`} tabIndex={-1}>
      <p className="eyebrow">Action feedback</p>
      <h3 id={`${item.itemId}-title`}>{title || 'Action result'}</h3>
      {hasWorkstreamText(item.body) && <p>{body}</p>}
      {item.surfaceId && (
        <button type="button" className="link-button" onClick={() => onOpenSurface?.(item.surfaceId!)}>
          Open result view
        </button>
      )}
      <TraceLinkList traceIds={item.traceIds} traceLinks={item.traceLinks} />
    </article>
  );
}
