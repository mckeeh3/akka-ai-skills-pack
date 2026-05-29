import type { ListSearchSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type ListSearchSurfaceProps = {
  envelope: SurfaceEnvelope<ListSearchSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string) => void;
};

export function ListSearchSurface({ envelope, onAction }: ListSearchSurfaceProps) {
  const columns = Array.from(new Set(envelope.data.rows.flatMap((row) => Object.keys(row))));
  const queryValue = typeof envelope.data.query === 'string' ? envelope.data.query : JSON.stringify(envelope.data.query);
  const extra = envelope.data as ListSearchSurfaceData & { emptyCopy?: string; emptyMessage?: string; systemStates?: string[]; mobileFallback?: string; surfaceContract?: string; surfaceContracts?: string[] };
  const emptyMessage = extra.emptyCopy ?? extra.emptyMessage ?? 'No results match the current search.';
  return (
    <SurfaceStateFrame envelope={envelope}>
      {(extra.surfaceContract || extra.surfaceContracts) && <p className="capability-basis">Surface contract: {extra.surfaceContract ?? extra.surfaceContracts?.join(', ')}</p>}
      <form className="surface-search-form" role="search">
        <label htmlFor={`${envelope.surfaceId}-query`}>Search</label>
        <input id={`${envelope.surfaceId}-query`} name="query" defaultValue={queryValue} />
      </form>
      {envelope.data.partial && <p className="surface-state-inline partial" role="status">Partial results: unauthorized or redacted evidence is omitted.</p>}
      {envelope.data.redaction && <p className="redaction-note">Redaction: {envelope.data.redaction}</p>}
      {extra.systemStates && <p className="surface-state-inline" role="status">Safe system states: {extra.systemStates.join(', ')}</p>}
      {extra.mobileFallback && <p className="surface-state-inline">Responsive fallback: {extra.mobileFallback}</p>}
      {envelope.data.rows.length === 0 ? <p>{emptyMessage}</p> : (
        <>
          <table>
            <caption>{envelope.title} results</caption>
            <thead><tr>{columns.map((column) => <th key={column} scope="col">{column}</th>)}</tr></thead>
            <tbody>{envelope.data.rows.map((row, index) => <tr key={String(row.id ?? row.userId ?? row.invitationId ?? index)}>{columns.map((column) => <td key={column}>{renderCell(column, row[column])}</td>)}</tr>)}</tbody>
          </table>
          <section className="surface-card-list" aria-label={`${envelope.title} card results`}>
            {envelope.data.rows.map((row, index) => (
              <article key={String(row.id ?? row.userId ?? row.invitationId ?? index)} className={`surface-row-card ${String(row.rowType ?? 'row')}`}>
                {columns.map((column) => <p key={column}><span>{column}</span><strong>{renderCell(column, row[column])}</strong></p>)}
              </article>
            ))}
          </section>
        </>
      )}
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

function renderCell(column: string, value: unknown) {
  if (value == null) return '';
  const text = String(value);
  if (/trace/i.test(column) && text) return <a href={`/ui?surfaceId=surface-audit-timeline#${encodeURIComponent(text)}`}>{text}</a>;
  return text;
}
