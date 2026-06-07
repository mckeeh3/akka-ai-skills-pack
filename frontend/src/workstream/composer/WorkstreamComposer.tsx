import { FormEvent, KeyboardEvent, useEffect, useLayoutEffect, useMemo, useRef, useState } from 'react';
import type { AuthContext, ComposerRequest, FunctionalAgentSummary, MeResponse } from '../types';
import { buildComposerRequest, canSubmitComposer, composerAvailability } from './composerState';

type WorkstreamComposerProps = {
  me: MeResponse;
  authContext: AuthContext;
  selectedAgent?: FunctionalAgentSummary;
  attachedSurfaceId?: string;
  isSubmitting?: boolean;
  onSubmit?: (request: ComposerRequest) => void | Promise<boolean | void>;
  onShowDashboard?: (functionalAgentId: string) => void | Promise<void>;
};

export function WorkstreamComposer({ me, authContext, selectedAgent, attachedSurfaceId, isSubmitting = false, onSubmit, onShowDashboard }: WorkstreamComposerProps) {
  const [draft, setDraft] = useState('');
  const inputRef = useRef<HTMLTextAreaElement>(null);
  const availability = useMemo(() => composerAvailability(me, selectedAgent), [me, selectedAgent]);
  const disabledReason = availability.status === 'disabled' ? availability.reason : undefined;
  const submitDisabled = isSubmitting || !selectedAgent || !canSubmitComposer(draft, availability);
  const showDashboardDisabled = !selectedAgent || availability.status === 'disabled';
  const helperId = 'workstream-composer-helper';
  const pointerStartedInSelectableSurfaceRef = useRef(false);

  function focusComposerInput() {
    const input = inputRef.current;
    if (!input || input.disabled) return;
    input.focus({ preventScroll: true });
  }

  function shouldRestoreComposerFocus(activeElement: Element | null) {
    if (pointerStartedInSelectableSurfaceRef.current || hasActiveTextSelection()) return false;
    if (!(activeElement instanceof HTMLElement)) return true;
    const editableElement = activeElement.closest('input, textarea, select, [contenteditable="true"]');
    return !editableElement;
  }

  function refocusComposerAfterBlur() {
    window.requestAnimationFrame(() => {
      if (shouldRestoreComposerFocus(document.activeElement)) focusComposerInput();
    });
  }

  useLayoutEffect(() => {
    const input = inputRef.current;
    if (!input) return;
    input.style.height = 'auto';
    input.style.height = `${input.scrollHeight}px`;
  }, [draft]);

  useEffect(() => {
    focusComposerInput();
  }, [selectedAgent?.functionalAgentId, disabledReason, isSubmitting]);

  useEffect(() => {
    function refocusVisibleComposer() {
      if (document.visibilityState === 'visible' && shouldRestoreComposerFocus(document.activeElement)) focusComposerInput();
    }

    function recordSelectableSurfacePointer(event: PointerEvent) {
      pointerStartedInSelectableSurfaceRef.current = isWorkstreamSelectableSurfaceTarget(event.target);
    }

    function clearSelectableSurfacePointer() {
      pointerStartedInSelectableSurfaceRef.current = false;
    }

    function restoreComposerFocus() {
      if (shouldRestoreComposerFocus(document.activeElement)) focusComposerInput();
    }

    window.addEventListener('focus', restoreComposerFocus);
    document.addEventListener('visibilitychange', refocusVisibleComposer);
    document.addEventListener('pointerdown', recordSelectableSurfacePointer, true);
    document.addEventListener('pointerup', clearSelectableSurfacePointer, true);
    document.addEventListener('pointercancel', clearSelectableSurfacePointer, true);
    return () => {
      window.removeEventListener('focus', restoreComposerFocus);
      document.removeEventListener('visibilitychange', refocusVisibleComposer);
      document.removeEventListener('pointerdown', recordSelectableSurfacePointer, true);
      document.removeEventListener('pointerup', clearSelectableSurfacePointer, true);
      document.removeEventListener('pointercancel', clearSelectableSurfacePointer, true);
    };
  }, [disabledReason, isSubmitting, selectedAgent?.functionalAgentId]);

  function submitFromKeyboard(event: KeyboardEvent<HTMLTextAreaElement>) {
    if (event.key !== 'Enter' || event.shiftKey || event.nativeEvent.isComposing) return;
    event.preventDefault();
    event.currentTarget.form?.requestSubmit();
  }

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (isSubmitting || !selectedAgent || !canSubmitComposer(draft, availability)) return;
    const accepted = await onSubmit?.(buildComposerRequest(authContext, selectedAgent, draft, attachedSurfaceId));
    if (accepted !== false) setDraft('');
    window.requestAnimationFrame(focusComposerInput);
  }

  async function showDashboard() {
    if (!selectedAgent || showDashboardDisabled) return;
    await onShowDashboard?.(selectedAgent.functionalAgentId);
    window.requestAnimationFrame(focusComposerInput);
  }

  return (
    <form className="command-strip workstream-composer" aria-label="Persistent workstream composer" onSubmit={submit}>
      <div className="composer-input-wrap">
        <label htmlFor="workstream-composer-input" className="sr-only">Ask {selectedAgent?.label ?? 'a functional agent'}</label>
        <textarea
          id="workstream-composer-input"
          className="designed-control composer-textarea"
          ref={inputRef}
          rows={1}
          autoFocus
          value={draft}
          onChange={(event) => setDraft(event.currentTarget.value)}
          onKeyDown={submitFromKeyboard}
          onBlur={refocusComposerAfterBlur}
          aria-describedby={helperId}
          disabled={isSubmitting || Boolean(disabledReason)}
          placeholder={isSubmitting ? 'Model-backed agent is responding…' : disabledReason ?? "What's next..."}
        />
        <p id={helperId} className="sr-only">
          {isSubmitting ? 'Submitting prompt to the governed model-backed runtime; selected workstream context is preserved.' : disabledReason ?? `Selected context ${authContext.selectedContextId}; requests are scoped to ${selectedAgent?.label ?? 'the selected agent'}.`}
        </p>
      </div>
      <button type="submit" className="ds-button primary icon-button send-prompt-button" disabled={submitDisabled} aria-label={isSubmitting ? 'Submitting prompt' : 'Send prompt'}>
        <span aria-hidden="true">↑</span>
        <span className="workstream-send-prompt-tooltip" role="tooltip">{isSubmitting ? 'Submitting prompt to model-backed agent' : 'Send prompt'}</span>
      </button>
      <button type="button" className="ds-button secondary icon-button show-dashboard-button" disabled={showDashboardDisabled} aria-label="Show dashboard" onClick={showDashboard}>
        <DashboardIcon />
        <span className="workstream-show-dashboard-tooltip" role="tooltip">Show dashboard</span>
      </button>
    </form>
  );
}

function DashboardIcon() {
  return (
    <svg className="dashboard-button-icon" aria-hidden="true" viewBox="0 0 24 24" focusable="false">
      <rect x="4" y="5" width="16" height="14" rx="3" />
      <path d="M8 10h3M8 14h2M14 10h2M14 14h2" />
      <path d="M4 9h16" />
    </svg>
  );
}

function hasActiveTextSelection(): boolean {
  const selection = window.getSelection?.();
  return Boolean(selection && selection.type === 'Range' && selection.toString().length > 0);
}

function isWorkstreamSelectableSurfaceTarget(target: EventTarget | null): boolean {
  return target instanceof HTMLElement && Boolean(target.closest('.workstream-item, .structured-surface, .surface-frame, [data-surface-id]'));
}
