import type { AuthContext, ComposerRequest, FunctionalAgentSummary, MeResponse } from '../types';
import { hasRequiredCapabilities } from '../rail/railState';

export type ComposerAvailability =
  | { status: 'ready' }
  | { status: 'disabled'; reason: string };

export function composerAvailability(me: MeResponse, selectedAgent: FunctionalAgentSummary | undefined): ComposerAvailability {
  if (me.account.status === 'disabled') return { status: 'disabled', reason: 'The signed-in account is disabled.' };
  if (me.memberships.length === 0) return { status: 'disabled', reason: 'No active membership is available for this tenant.' };
  if (!selectedAgent) return { status: 'disabled', reason: 'Choose a functional agent before sending a request.' };
  if (selectedAgent.availability !== 'visible') return { status: 'disabled', reason: selectedAgent.deniedReason ?? 'The selected functional agent is not available.' };
  if (!hasRequiredCapabilities(selectedAgent, me.visibleCapabilityIds)) return { status: 'disabled', reason: `Missing required capability: ${selectedAgent.requiredCapabilityIds.join(', ')}` };
  return { status: 'ready' };
}

export function canSubmitComposer(draft: string, availability: ComposerAvailability): boolean {
  return availability.status === 'ready' && draft.trim().length > 0;
}

export function buildComposerRequest(authContext: AuthContext, selectedAgent: FunctionalAgentSummary, prompt: string, attachedSurfaceId?: string): ComposerRequest {
  return {
    functionalAgentId: selectedAgent.functionalAgentId,
    selectedContextId: authContext.selectedContextId,
    prompt: prompt.trim(),
    attachedSurfaceId,
    idempotencyKey: `composer:${authContext.selectedContextId}:${selectedAgent.functionalAgentId}:${Date.now()}`
  };
}
