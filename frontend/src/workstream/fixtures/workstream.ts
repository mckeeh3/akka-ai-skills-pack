import type { WorkstreamItem } from '../types';

const createdAt = '2026-05-19T12:00:00.000Z';

export const initialWorkstreamItems: WorkstreamItem[] = [
  { itemId: 'item-user-request', functionalAgentId: 'agent-user-admin', kind: 'user-request', createdAt, correlationId: 'corr-user-request', traceIds: ['trace-user-request'], title: 'Invite a reviewer', body: 'Invite robin@example.test as a reviewer.', status: 'ready' },
  { itemId: 'item-agent-response', functionalAgentId: 'agent-user-admin', kind: 'agent-response', createdAt, correlationId: 'corr-agent-response', traceIds: ['trace-agent-response'], title: 'Invitation plan drafted', body: 'I can invite the reviewer after confirmation.', status: 'waiting-for-human' },
  { itemId: 'item-surface', functionalAgentId: 'agent-user-admin', kind: 'surface', createdAt, correlationId: 'corr-surface', traceIds: ['trace-surface'], surfaceId: 'surface-user-list', title: 'Users and invitations', status: 'ready' },
  { itemId: 'item-capability-result', functionalAgentId: 'agent-user-admin', kind: 'capability-result', createdAt, correlationId: 'corr-capability-result', traceIds: ['trace-capability-result'], title: 'Invitation accepted', body: 'Capability admin.users.invite accepted the request.', status: 'ready' },
  { itemId: 'item-workflow-status', functionalAgentId: 'agent-user-admin', kind: 'workflow-status', createdAt, correlationId: 'corr-workflow', traceIds: ['trace-workflow'], surfaceId: 'surface-workflow-status', title: 'Invitation workflow waiting', status: 'waiting-for-human' },
  { itemId: 'item-decision', functionalAgentId: 'agent-governance-policy', kind: 'decision', createdAt, correlationId: 'corr-decision', traceIds: ['trace-decision'], surfaceId: 'surface-decision-card', title: 'Approve bounded outreach plan', status: 'waiting-for-human' },
  { itemId: 'item-audit-trace', functionalAgentId: 'agent-audit-trace', kind: 'audit-trace', createdAt, correlationId: 'corr-audit', traceIds: ['trace-invite'], surfaceId: 'surface-audit-timeline', title: 'Admin audit event recorded', status: 'ready', traceLinks: [{ traceId: 'trace-invite', label: 'Invitation trace', href: '/ui?surfaceId=surface-audit-timeline' }] },
  { itemId: 'item-action-feedback', functionalAgentId: 'agent-user-admin', kind: 'action-feedback', createdAt, correlationId: 'corr-feedback', traceIds: ['trace-feedback'], title: 'Opened result surface', body: 'The workflow status surface was appended to the workstream.', surfaceId: 'surface-workflow-status', status: 'ready' },
  { itemId: 'item-system-status', functionalAgentId: 'agent-access-profile', kind: 'system-status', createdAt, correlationId: 'corr-system', traceIds: [], title: 'Realtime reconnected', body: 'Events resumed from the last known event id.', status: 'ready' }
];
