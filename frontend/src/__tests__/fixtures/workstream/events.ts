import type { MalformedSafeEvent, WorkstreamEvent } from '../../../workstream/types';

const base = {
  tenantId: 'tenant-acme',
  customerId: 'customer-northwind',
  functionalAgentId: 'agent-user-admin',
  correlationId: 'corr-events',
  traceIds: ['trace-events'],
  occurredAt: '2026-05-19T12:00:00.000Z'
};

export const workstreamEvents: WorkstreamEvent[] = [
  { ...base, eventId: 'evt-item-appended-1', eventType: 'workstream.item.appended', sequence: 1, patch: { itemId: 'item-capability-result' } },
  { ...base, eventId: 'evt-item-updated-2', eventType: 'workstream.item.updated', sequence: 2, patch: { itemId: 'item-workflow-status', status: 'waiting-for-human' } },
  { ...base, eventId: 'evt-surface-created-3', eventType: 'surface.created', surfaceId: 'surface-user-list', surfaceType: 'list-search', surfaceVersion: 'v1', sequence: 3 },
  { ...base, eventId: 'evt-surface-updated-4', eventType: 'surface.updated', surfaceId: 'surface-user-list', surfaceType: 'list-search', surfaceVersion: 'v1', sequence: 4, patch: { rowCount: 2 } },
  { ...base, eventId: 'evt-action-accepted-5', eventType: 'surface.action.accepted', surfaceId: 'surface-user-list', surfaceType: 'list-search', surfaceVersion: 'v1', sequence: 5, patch: { actionId: 'action-invite-user' } },
  { ...base, eventId: 'evt-action-denied-6', eventType: 'surface.action.denied', surfaceId: 'surface-user-list', surfaceType: 'list-search', surfaceVersion: 'v1', sequence: 6, patch: { reasonCode: 'missing-capability' } },
  { ...base, eventId: 'evt-workflow-progressed-7', eventType: 'surface.workflow.progressed', surfaceId: 'surface-workflow-status', surfaceType: 'workflow-status', surfaceVersion: 'v1', sequence: 7, patch: { stepId: 'send-email', status: 'completed' } },
  { ...base, eventId: 'evt-surface-stale-8', eventType: 'surface.stale', surfaceId: 'surface-user-list', surfaceType: 'list-search', surfaceVersion: 'v1', sequence: 8, patch: { reason: 'Missed event during reconnect.' } },
  { ...base, eventId: 'evt-surface-reconnected-9', eventType: 'surface.reconnected', surfaceId: 'surface-user-list', surfaceType: 'list-search', surfaceVersion: 'v1', sequence: 9, patch: { lastEventId: 'evt-surface-stale-8' } }
];

export const duplicateReplayEvent = { ...workstreamEvents[4] };
export const outOfOrderEvent: WorkstreamEvent = { ...base, eventId: 'evt-out-of-order-0', eventType: 'surface.updated', surfaceId: 'surface-user-list', surfaceType: 'list-search', surfaceVersion: 'v1', sequence: 0, patch: { ignored: true } };
export const crossContextDeniedEvent: WorkstreamEvent = { ...base, eventId: 'evt-cross-context-denied', eventType: 'surface.updated', tenantId: 'tenant-other', surfaceId: 'surface-user-list', surfaceType: 'list-search', surfaceVersion: 'v1', sequence: 10, patch: { denied: true } };

export const malformedSafeEvent: MalformedSafeEvent = {
  eventId: 'evt-malformed-safe',
  eventType: 'surface.updated',
  malformed: true,
  reason: 'Missing tenantId and occurredAt in raw server event.',
  raw: { eventType: 'surface.updated', surfaceId: 'surface-user-list' }
};

export const realtimeFixtureCases = {
  created: workstreamEvents[2],
  updated: workstreamEvents[3],
  accepted: workstreamEvents[4],
  denied: workstreamEvents[5],
  workflowProgressed: workstreamEvents[6],
  stale: workstreamEvents[7],
  reconnected: workstreamEvents[8],
  duplicateReplay: duplicateReplayEvent,
  outOfOrder: outOfOrderEvent,
  malformedSafe: malformedSafeEvent,
  crossContextDenied: crossContextDeniedEvent
};
