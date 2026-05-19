import type { WorkstreamItem as WorkstreamItemContract } from '../types';
import { WorkstreamItemCard } from './WorkstreamItem';

type WorkstreamStreamProps = {
  items: WorkstreamItemContract[];
  selectedItemId?: string;
  onOpenSurface?: (surfaceId: string) => void;
};

export function WorkstreamStream({ items, selectedItemId, onOpenSurface }: WorkstreamStreamProps) {
  if (items.length === 0) {
    return (
      <section className="flow-stack workstream-stream empty" aria-label="Workstream items">
        <article className="ds-card workstream-item empty">
          <p className="eyebrow">Empty workstream</p>
          <h3>No workstream items yet</h3>
          <p>Use the persistent composer to ask the selected functional agent for help.</p>
        </article>
      </section>
    );
  }

  return (
    <section className="flow-stack workstream-stream" aria-label="Workstream items" data-selected-item-id={selectedItemId}>
      {items.map((item) => (
        <WorkstreamItemCard key={item.itemId} item={item} onOpenSurface={onOpenSurface} />
      ))}
    </section>
  );
}
