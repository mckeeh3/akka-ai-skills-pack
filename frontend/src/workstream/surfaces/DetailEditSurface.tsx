import type { DetailEditSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type DetailEditSurfaceProps = {
  envelope: SurfaceEnvelope<DetailEditSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string) => void;
};

export function DetailEditSurface({ envelope, onAction }: DetailEditSurfaceProps) {
  return (
    <SurfaceStateFrame envelope={envelope}>
      <form className="surface-detail-edit-form">
        <input type="hidden" name="recordId" value={envelope.data.recordId} readOnly />
        {envelope.data.fields.map((field) => (
          <label key={field.fieldId} htmlFor={`${envelope.surfaceId}-${field.fieldId}`}>
            <span>{field.label}</span>
            <input id={`${envelope.surfaceId}-${field.fieldId}`} name={field.fieldId} defaultValue={field.value} readOnly={!field.editable} aria-readonly={!field.editable} />
          </label>
        ))}
        <p>Version {envelope.data.version}</p>
      </form>
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}
