import { FormEvent, useMemo, useState } from 'react';
import type { AuthContext, ComposerRequest, FunctionalAgentSummary, MeResponse } from '../types';
import { ComposerCommandHints } from './ComposerCommandHints';
import { buildComposerRequest, canSubmitComposer, composerAvailability } from './composerState';

type WorkstreamComposerProps = {
  me: MeResponse;
  authContext: AuthContext;
  selectedAgent?: FunctionalAgentSummary;
  attachedSurfaceId?: string;
  hints?: string[];
  onSubmit?: (request: ComposerRequest) => void;
};

export function WorkstreamComposer({ me, authContext, selectedAgent, attachedSurfaceId, hints, onSubmit }: WorkstreamComposerProps) {
  const [draft, setDraft] = useState('');
  const availability = useMemo(() => composerAvailability(me, selectedAgent), [me, selectedAgent]);
  const disabledReason = availability.status === 'disabled' ? availability.reason : undefined;
  const submitDisabled = !selectedAgent || !canSubmitComposer(draft, availability);
  const helperId = 'workstream-composer-helper';

  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!selectedAgent || !canSubmitComposer(draft, availability)) return;
    onSubmit?.(buildComposerRequest(authContext, selectedAgent, draft, attachedSurfaceId));
    setDraft('');
  }

  return (
    <form className="command-strip workstream-composer" aria-label="Persistent workstream composer" onSubmit={submit}>
      <span className="ai-mark" aria-hidden="true">AI</span>
      <div>
        <label htmlFor="workstream-composer-input" className="eyebrow">Ask {selectedAgent?.label ?? 'a functional agent'}</label>
        <textarea
          id="workstream-composer-input"
          autoFocus
          value={draft}
          onChange={(event) => setDraft(event.currentTarget.value)}
          aria-describedby={helperId}
          disabled={Boolean(disabledReason)}
          placeholder={disabledReason ?? 'Describe the outcome you want. Consequential actions will remain capability-backed.'}
        />
        <p id={helperId} className="field-helper">
          {disabledReason ?? `Selected context ${authContext.selectedContextId}; requests are scoped to ${selectedAgent?.label ?? 'the selected agent'}.`}
        </p>
        <ComposerCommandHints hints={hints} onUseHint={setDraft} />
      </div>
      <button type="submit" className="ds-button primary" disabled={submitDisabled}>
        Send to workstream
      </button>
    </form>
  );
}
