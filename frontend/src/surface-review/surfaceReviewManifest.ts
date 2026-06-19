import { fullCoreDemoSurfaceEnvelopes } from '../__tests__/fixtures/workstream/surfaces';
import type { SurfaceEnvelope } from '../workstream/types';
import { workstreamDescriptions } from './surfaceReviewDescriptions';

export type SurfaceReviewEntry = {
  workstreamId: string;
  workstreamLabel: string;
  surfaceId: string;
  title: string;
  surfaceType: string;
  contract?: string;
  ownerFunctionalAgentId: string;
  sourcePath: string;
  envelope: SurfaceEnvelope<unknown>;
};

export type SurfaceReviewWorkstream = {
  workstreamId: string;
  label: string;
  sourcePath: string;
  surfaces: SurfaceReviewEntry[];
};

const workstreamByFunctionalAgentId: Record<string, string> = {
  'user-admin-agent': 'user-admin',
  'agent-admin-agent': 'agent-admin',
  'agent-audit-trace': 'audit-trace',
  'agent-governance-policy': 'governance-policy',
  'agent-my-account': 'my-account'
};

export const surfaceReviewEntries: SurfaceReviewEntry[] = Array.from(
  new Map(fullCoreDemoSurfaceEnvelopes.map((envelope) => [envelope.surfaceId, envelope as SurfaceEnvelope<unknown>])).values()
).map((envelope) => {
  const workstreamId = workstreamByFunctionalAgentId[envelope.ownerFunctionalAgentId] ?? 'unknown';
  const description = workstreamDescriptions[workstreamId] ?? {
    label: workstreamId,
    sourcePath: 'app-description',
    workstreamId,
    markdown: ''
  };
  const data = envelope.data && typeof envelope.data === 'object' ? envelope.data as { surfaceContract?: unknown } : {};
  return {
    workstreamId,
    workstreamLabel: description.label,
    surfaceId: envelope.surfaceId,
    title: envelope.title,
    surfaceType: envelope.surfaceType,
    contract: typeof data.surfaceContract === 'string' ? data.surfaceContract : undefined,
    ownerFunctionalAgentId: envelope.ownerFunctionalAgentId,
    sourcePath: description.sourcePath,
    envelope
  };
}).sort((left, right) => left.workstreamLabel.localeCompare(right.workstreamLabel) || left.surfaceId.localeCompare(right.surfaceId));

export const surfaceReviewWorkstreams: SurfaceReviewWorkstream[] = Object.values(
  surfaceReviewEntries.reduce<Record<string, SurfaceReviewWorkstream>>((groups, entry) => {
    groups[entry.workstreamId] ??= {
      workstreamId: entry.workstreamId,
      label: entry.workstreamLabel,
      sourcePath: entry.sourcePath,
      surfaces: []
    };
    groups[entry.workstreamId].surfaces.push(entry);
    return groups;
  }, {})
).sort((left, right) => left.label.localeCompare(right.label));

export function findSurfaceReviewEntry(workstreamId?: string | null, surfaceId?: string | null): SurfaceReviewEntry {
  return surfaceReviewEntries.find((entry) => entry.workstreamId === workstreamId && entry.surfaceId === surfaceId)
    ?? surfaceReviewEntries.find((entry) => entry.surfaceId === surfaceId)
    ?? surfaceReviewEntries[0];
}

export function surfaceReviewHref(entry: Pick<SurfaceReviewEntry, 'workstreamId' | 'surfaceId'>): string {
  return `/surface-review?workstream=${encodeURIComponent(entry.workstreamId)}&surface=${encodeURIComponent(entry.surfaceId)}`;
}
