import type { GovernanceDiffSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type GovernanceDiffSurfaceProps = {
  envelope: SurfaceEnvelope<GovernanceDiffSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string) => void;
};

export function GovernanceDiffSurface({ envelope, onAction }: GovernanceDiffSurfaceProps) {
  return (
    <SurfaceStateFrame envelope={envelope}>
      <div className="governance-diff-summary">
        <section><h4>Before</h4><p>{envelope.data.beforeSummary}</p></section>
        <section><h4>After</h4><p>{envelope.data.afterSummary}</p></section>
      </div>
      <table>
        <caption>Policy proposal changes</caption>
        <thead><tr><th scope="col">Path</th><th scope="col">Before</th><th scope="col">After</th><th scope="col">Impact</th></tr></thead>
        <tbody>{envelope.data.changes.map((change) => <tr key={change.path}><th scope="row">{change.path}</th><td>{change.before ?? ''}</td><td>{change.after ?? ''}</td><td>{change.impact}</td></tr>)}</tbody>
      </table>
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}
