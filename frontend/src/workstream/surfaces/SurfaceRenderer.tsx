import type { SurfaceAction, SurfaceEnvelope } from '../types';
import { AuditTimelineSurface } from './AuditTimelineSurface';
import { DashboardSurface } from './DashboardSurface';
import { DecisionSurface } from './DecisionSurface';
import { DetailEditSurface } from './DetailEditSurface';
import { GovernanceDiffSurface } from './GovernanceDiffSurface';
import { ListSearchSurface } from './ListSearchSurface';
import { MarkdownResponseSurface } from './MarkdownResponseSurface';
import { OutcomeSurface } from './OutcomeSurface';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';
import { WorkflowStatusSurface } from './WorkflowStatusSurface';

type StructuredSurfaceRendererProps = {
  envelope?: SurfaceEnvelope<unknown>;
  envelopes?: SurfaceEnvelope<unknown>[];
  selectedSurfaceId?: string;
  onAction?: (action: SurfaceAction, surfaceId: string) => void;
};

export function StructuredSurfaceRenderer({ envelope, envelopes = [], selectedSurfaceId, onAction }: StructuredSurfaceRendererProps) {
  const selectedEnvelope = envelope ?? envelopes.find((candidate) => candidate.surfaceId === selectedSurfaceId) ?? envelopes[0];

  if (!selectedEnvelope) {
    return <SurfaceStateFrame />;
  }

  switch (selectedEnvelope.surfaceType) {
    case 'markdown_response':
      return <MarkdownResponseSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    case 'dashboard':
      return <DashboardSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    case 'list-search':
      return <ListSearchSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    case 'detail-edit':
      return <DetailEditSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    case 'decision':
      return <DecisionSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    case 'audit-timeline':
      return <AuditTimelineSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    case 'workflow-status':
      return <WorkflowStatusSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    case 'governance-diff':
      return <GovernanceDiffSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    case 'outcome':
      return <OutcomeSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    default:
      return (
        <SurfaceStateFrame envelope={selectedEnvelope}>
          <pre>{JSON.stringify(selectedEnvelope.data, null, 2)}</pre>
          <SurfaceActionBar actions={selectedEnvelope.actions} surfaceId={selectedEnvelope.surfaceId} onAction={onAction} />
        </SurfaceStateFrame>
      );
  }
}

export { StructuredSurfaceRenderer as SurfaceRenderer };
