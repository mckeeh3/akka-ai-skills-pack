export type WorkstreamEventType =
  | 'workstream.item.appended'
  | 'workstream.item.updated'
  | 'surface.created'
  | 'surface.updated'
  | 'surface.action.accepted'
  | 'surface.action.denied'
  | 'surface.workflow.progressed'
  | 'surface.stale'
  | 'surface.reconnected';

export type WorkstreamEvent<TPatch = unknown> = {
  eventId: string;
  eventType: WorkstreamEventType;
  tenantId: string;
  customerId?: string;
  functionalAgentId: string;
  surfaceId?: string;
  surfaceType?: string;
  surfaceVersion?: string;
  correlationId: string;
  traceIds: string[];
  occurredAt: string;
  sequence?: number;
  patch?: TPatch;
};

export type SurfaceEvent<TPatch = unknown> = WorkstreamEvent<TPatch> & {
  surfaceId: string;
  surfaceType: string;
  surfaceVersion: string;
};

export type RealtimeConnectionState =
  | { status: 'connecting' }
  | { status: 'connected'; lastEventId?: string }
  | { status: 'reconnecting'; lastEventId?: string }
  | { status: 'stale'; lastEventId?: string; reason: string }
  | { status: 'disconnected'; reason?: string };

export type MalformedSafeEvent = {
  eventId?: string;
  eventType?: string;
  malformed: true;
  reason: string;
  raw: unknown;
};
