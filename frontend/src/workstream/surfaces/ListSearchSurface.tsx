import type { ListSearchSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type ListSearchSurfaceProps = {
  envelope: SurfaceEnvelope<ListSearchSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

export function ListSearchSurface({ envelope, onAction }: ListSearchSurfaceProps) {
  const columns = Array.from(new Set(envelope.data.rows.flatMap((row) => Object.keys(row))));
  const queryValue = typeof envelope.data.query === 'string' ? envelope.data.query : JSON.stringify(envelope.data.query);
  return (
    <SurfaceStateFrame envelope={envelope}>
      <form className="surface-search-form" role="search">
        <label htmlFor={`${envelope.surfaceId}-query`}>Search</label>
        <input className="designed-control surface-search-control" id={`${envelope.surfaceId}-query`} name="query" defaultValue={queryValue} />
      </form>
      {envelope.data.partial && <p className="surface-state-inline partial" role="status">Partial results: unauthorized or redacted evidence is omitted.</p>}
      {envelope.data.redaction && <p className="redaction-note">Redaction: {renderSurfaceValue(envelope.data.redaction)}</p>}
      {envelope.data.rows.length === 0 ? <p>No results match the current search.</p> : (
        <table>
          <caption>{envelope.title} results</caption>
          <thead><tr>{columns.map((column) => <th key={column} scope="col">{column}</th>)}</tr></thead>
          <tbody>{envelope.data.rows.map((row, index) => <tr key={String(row.id ?? row.userId ?? index)}>{columns.map((column) => <td key={column}>{String(row[column] ?? '')}</td>)}</tr>)}</tbody>
        </table>
      )}
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

function renderSurfaceValue(value: unknown): string | undefined {
  if (value == null) return undefined;
  if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') return String(value);
  if (Array.isArray(value)) return value.map(renderSurfaceValue).filter(Boolean).join(' · ');
  if (typeof value === 'object') return Object.entries(value as Record<string, unknown>).map(([key, entry]) => `${key}: ${renderSurfaceValue(entry) ?? 'n/a'}`).join(' · ');
  return String(value);
}
