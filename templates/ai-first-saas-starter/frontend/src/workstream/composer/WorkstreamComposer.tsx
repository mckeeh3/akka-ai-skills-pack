import { FormEvent, useLayoutEffect, useMemo, useRef, useState } from 'react';
import type { AuthContext, ComposerRequest, FunctionalAgentSummary, MeResponse } from '../types';
import { buildComposerRequest, canSubmitComposer, composerAvailability } from './composerState';

type WorkstreamComposerProps = {
  me: MeResponse;
  authContext: AuthContext;
  selectedAgent?: FunctionalAgentSummary;
  attachedSurfaceId?: string;
  onSubmit?: (request: ComposerRequest) => void;
};

export function WorkstreamComposer({ me, authContext, selectedAgent, attachedSurfaceId, onSubmit }: WorkstreamComposerProps) {
  const [draft, setDraft] = useState('');
  const inputRef = useRef<HTMLTextAreaElement>(null);
  const availability = useMemo(() => composerAvailability(me, selectedAgent), [me, selectedAgent]);
  const disabledReason = availability.status === 'disabled' ? availability.reason : undefined;
  const submitDisabled = !selectedAgent || !canSubmitComposer(draft, availability);
  const helperId = 'workstream-composer-helper';

  useLayoutEffect(() => {
    const input = inputRef.current;
    if (!input) return;
    input.style.height = 'auto';
    input.style.height = `${input.scrollHeight}px`;
  }, [draft]);

  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!selectedAgent || !canSubmitComposer(draft, availability)) return;
    onSubmit?.(buildComposerRequest(authContext, selectedAgent, draft, attachedSurfaceId));
    setDraft('');
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
          disabled={Boolean(disabledReason)}
          placeholder={disabledReason ?? 'Ask for an outcome…'}
        />
        <p id={helperId} className="sr-only">
          {disabledReason ?? `Selected context ${authContext.selectedContextId}; requests are scoped to ${selectedAgent?.label ?? 'the selected agent'}.`}
        </p>
      </div>
      <button type="submit" className="ds-button primary icon-button send-prompt-button" disabled={submitDisabled} aria-label="Send prompt" title="Send prompt">
        <span aria-hidden="true">↑</span>
      </button>
    </form>
  );
}
