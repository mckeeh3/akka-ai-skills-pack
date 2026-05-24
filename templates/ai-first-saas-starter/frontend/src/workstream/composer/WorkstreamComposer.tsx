import { FormEvent, useLayoutEffect, useMemo, useRef, useState } from 'react';
import type { AuthContext, ComposerRequest, FunctionalAgentSummary, MeResponse } from '../types';
import { buildComposerRequest, canSubmitComposer, composerAvailability } from './composerState';

type WorkstreamComposerProps = {
  me: MeResponse;
  authContext: AuthContext;
  selectedAgent?: FunctionalAgentSummary;
  attachedSurfaceId?: string;
  isSubmitting?: boolean;
  onSubmit?: (request: ComposerRequest) => void | Promise<boolean | void>;
};

export function WorkstreamComposer({ me, authContext, selectedAgent, attachedSurfaceId, isSubmitting = false, onSubmit }: WorkstreamComposerProps) {
  const [draft, setDraft] = useState('');
  const inputRef = useRef<HTMLTextAreaElement>(null);
  const availability = useMemo(() => composerAvailability(me, selectedAgent), [me, selectedAgent]);
  const disabledReason = availability.status === 'disabled' ? availability.reason : undefined;
  const submitDisabled = isSubmitting || !selectedAgent || !canSubmitComposer(draft, availability);
  const helperId = 'workstream-composer-helper';

  useLayoutEffect(() => {
    const input = inputRef.current;
    if (!input) return;
    input.style.height = 'auto';
    input.style.height = `${input.scrollHeight}px`;
  }, [draft]);

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (isSubmitting || !selectedAgent || !canSubmitComposer(draft, availability)) return;
    const accepted = await onSubmit?.(buildComposerRequest(authContext, selectedAgent, draft, attachedSurfaceId));
    if (accepted !== false) setDraft('');
  }

  return (
    <form className="command-strip workstream-composer" aria-label="Persistent workstream composer" onSubmit={submit}>
      <div className="composer-input-wrap">
        <label htmlFor="workstream-composer-input" className="sr-only">Ask {selectedAgent?.label ?? 'a functional agent'}</label>
        <textarea
          id="workstream-composer-input"
          ref={inputRef}
          rows={1}
          autoFocus
          value={draft}
          onChange={(event) => setDraft(event.currentTarget.value)}
          aria-describedby={helperId}
          disabled={isSubmitting || Boolean(disabledReason)}
          placeholder={isSubmitting ? 'Model-backed agent is responding…' : disabledReason ?? "What's next..."}
        />
        <p id={helperId} className="sr-only">
          {isSubmitting ? 'Submitting prompt to the governed model-backed runtime; selected workstream context is preserved.' : disabledReason ?? `Selected context ${authContext.selectedContextId}; requests are scoped to ${selectedAgent?.label ?? 'the selected agent'}.`}
        </p>
      </div>
      <button type="submit" className="ds-button primary icon-button send-prompt-button" disabled={submitDisabled} aria-label={isSubmitting ? 'Submitting prompt' : 'Send prompt'} title={isSubmitting ? 'Submitting prompt to model-backed agent' : 'Send prompt'}>
        <span aria-hidden="true">↑</span>
      </button>
    </form>
  );
}
