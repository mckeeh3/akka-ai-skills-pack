import type { SurfaceAction, SurfaceActionInput, SurfaceEnvelope } from '../types';
import { AgentAdminDocEditingSurface, isAgentAdminDocEditingSurface } from './AgentAdminDocEditingSurface';
import { AgentAdminTaskSurface, isAgentAdminTaskSurface } from './AgentAdminTaskSurface';
import { AuditTimelineSurface } from './AuditTimelineSurface';
import { ChatToolPlanSurface } from './ChatToolPlanSurface';
import { DashboardSurface } from './DashboardSurface';
import { DecisionSurface } from './DecisionSurface';
import { DetailEditSurface } from './DetailEditSurface';
import { GovernanceDiffSurface } from './GovernanceDiffSurface';
import { ListSearchSurface } from './ListSearchSurface';
import { MarkdownResponseSurface } from './MarkdownResponseSurface';
import { OutcomeSurface } from './OutcomeSurface';
import { NotificationCenterSurface } from './NotificationCenterSurface';
import { MyAccountSurface, isMyAccountRebuiltSurface } from './MyAccountSurfaces';
import { OrganizationAdminSurface } from './OrganizationAdminSurface';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';
import { SystemMessageSurface } from './SystemMessageSurface';
import { UserAdminRoleChangePreviewSurface, isUserAdminRoleChangePreviewSurface } from './UserAdminRoleChangePreviewSurface';
import { UserAdminScopedAdminSurface, isUserAdminScopedAdminSurface } from './UserAdminScopedAdminSurface';
import { UserAdminTaskSurface, isUserAdminTaskSurface } from './UserAdminTaskSurface';
import { WorkflowStatusSurface } from './WorkflowStatusSurface';

type StructuredSurfaceRendererProps = {
  envelope?: SurfaceEnvelope<unknown>;
  envelopes?: SurfaceEnvelope<unknown>[];
  selectedSurfaceId?: string;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: SurfaceActionInput) => void | Promise<void>;
  onFieldValueChange?: (fieldId: string, value: string, surfaceId: string) => void;
  onSignOut?: () => void;
};

export function StructuredSurfaceRenderer({ envelope, envelopes = [], selectedSurfaceId, onAction, onFieldValueChange, onSignOut }: StructuredSurfaceRendererProps) {
  const selectedEnvelope = envelope ?? envelopes.find((candidate) => candidate.surfaceId === selectedSurfaceId) ?? envelopes[0];

  if (!selectedEnvelope) {
    return <SurfaceStateFrame />;
  }

  if (isMyAccountRebuiltSurface(selectedEnvelope)) {
    return <MyAccountSurface envelope={selectedEnvelope as never} onAction={onAction} onFieldValueChange={onFieldValueChange} />;
  }

  if (isUserAdminScopedAdminSurface(selectedEnvelope)) {
    return <UserAdminScopedAdminSurface envelope={selectedEnvelope as never} onAction={onAction} />;
  }

  if (isOrganizationLifecycleSurface(selectedEnvelope)) {
    return <OrganizationAdminSurface envelope={selectedEnvelope as never} onAction={onAction} />;
  }

  if (isUserAdminRoleChangePreviewSurface(selectedEnvelope)) {
    return <UserAdminRoleChangePreviewSurface envelope={selectedEnvelope as never} onAction={onAction} />;
  }

  if (isAgentAdminDocEditingSurface(selectedEnvelope)) {
    return <AgentAdminDocEditingSurface envelope={selectedEnvelope as never} onAction={onAction} />;
  }

  if (isAgentAdminTaskSurface(selectedEnvelope)) {
    return <AgentAdminTaskSurface envelope={selectedEnvelope as never} onAction={onAction} />;
  }

  if (isUserAdminTaskSurface(selectedEnvelope)) {
    return <UserAdminTaskSurface envelope={selectedEnvelope as never} onAction={onAction} />;
  }

  switch (selectedEnvelope.surfaceType) {
    case 'markdown_response':
      return <MarkdownResponseSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    case 'system_message':
      return <SystemMessageSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    case 'system-message':
      return <SystemMessageSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    case 'chat_tool_plan_proposal':
    case 'chat_tool_plan_confirmation':
    case 'chat_tool_plan_result':
    case 'chat_tool_plan_system_message':
      return <ChatToolPlanSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    case 'dashboard':
      return <DashboardSurface envelope={selectedEnvelope as never} onAction={onAction} onSignOut={onSignOut} />;
    case 'list-search':
      return <ListSearchSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    case 'show-inspection':
    case 'edit-form':
    case 'detail-edit':
      return <DetailEditSurface envelope={selectedEnvelope as never} onAction={onAction} onFieldValueChange={onFieldValueChange} />;
    case 'create-form':
    case 'lifecycle-confirmation':
    case 'destructive-lifecycle-confirmation':
      return <UserAdminTaskSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    case 'decision-card':
    case 'decision':
      return <DecisionSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    case 'audit-timeline':
      return <AuditTimelineSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    case 'workflow-status':
      return <WorkflowStatusSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    case 'diff':
    case 'governance-diff':
      return <GovernanceDiffSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    case 'outcome':
    case 'outcome-panel':
      return <OutcomeSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    case 'notification-center':
      return <NotificationCenterSurface envelope={selectedEnvelope as never} onAction={onAction} />;
    default:
      return (
        <SurfaceStateFrame envelope={selectedEnvelope}>
          <pre>{JSON.stringify(selectedEnvelope.data, null, 2)}</pre>
          <SurfaceActionBar actions={selectedEnvelope.actions} surfaceId={selectedEnvelope.surfaceId} onAction={onAction} />
        </SurfaceStateFrame>
      );
  }
}

function isOrganizationLifecycleSurface(envelope: SurfaceEnvelope<unknown>) {
  const contract = (envelope.data as { surfaceContract?: string } | undefined)?.surfaceContract ?? '';
  return envelope.surfaceId === 'surface-user-admin-organization-directory'
    || envelope.surfaceId === 'surface-user-admin-organization-detail'
    || envelope.surfaceId === 'surface-user-admin-organization-create'
    || envelope.surfaceId === 'surface-user-admin-organization-rename'
    || envelope.surfaceId === 'surface-user-admin-organization-suspend-confirmation'
    || envelope.surfaceId === 'surface-user-admin-organization-reactivate-confirmation'
    || contract === 'user_admin.organization_directory.v1'
    || contract === 'user_admin.organization_detail.v1'
    || contract === 'user_admin.organization_create.v1'
    || contract === 'user_admin.organization_rename.v1'
    || contract === 'user_admin.organization_suspend_confirmation.v1'
    || contract === 'user_admin.organization_reactivate_confirmation.v1';
}

export { StructuredSurfaceRenderer as SurfaceRenderer };
