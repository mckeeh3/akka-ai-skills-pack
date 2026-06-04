import type { WorkstreamItem } from '../../../workstream/types';

// Bootstrap starts with no synthetic workstream entries; user prompts, surface requests,
// capability actions, and realtime/backend events append items through the normal runtime path.
export const initialWorkstreamItems: WorkstreamItem[] = [];
