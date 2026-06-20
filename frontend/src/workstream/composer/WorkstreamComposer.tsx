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
  onClearScreen?: (functionalAgentId: string) => void | Promise<void>;
};

export function WorkstreamComposer({ me, authContext, selectedAgent, attachedSurfaceId, isSubmitting = false, onSubmit, onShowDashboard, onClearScreen }: WorkstreamComposerProps) {
  const [draft, setDraft] = useState('');
  const inputRef = useRef<HTMLTextAreaElement>(null);
  const availability = useMemo(() => composerAvailability(me, selectedAgent), [me, selectedAgent]);
  const disabledReason = availability.status === 'disabled' ? availability.reason : undefined;
  const submitDisabled = isSubmitting || !selectedAgent || !canSubmitComposer(draft, availability);
  const showDashboardDisabled = !selectedAgent || availability.status === 'disabled';
  const clearScreenDisabled = !selectedAgent;
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
    const request = buildComposerRequest(authContext, selectedAgent, draft, attachedSurfaceId);
    setDraft('');
    await onSubmit?.(request);
    window.requestAnimationFrame(focusComposerInput);
  }

  async function showDashboard() {
    if (!selectedAgent || showDashboardDisabled) return;
    await onShowDashboard?.(selectedAgent.functionalAgentId);
    window.requestAnimationFrame(focusComposerInput);
  }

  async function clearScreen() {
    if (!selectedAgent || clearScreenDisabled) return;
    await onClearScreen?.(selectedAgent.functionalAgentId);
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
      <button type="button" className="ds-button ghost icon-button clear-screen-button" disabled={clearScreenDisabled} aria-label="Clear screen for current workstream" onClick={clearScreen}>
        <CleanScreenIcon />
        <span className="workstream-clear-screen-tooltip" role="tooltip">Clear screen</span>
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

function CleanScreenIcon() {
  return (
    <svg className="clear-screen-button-icon" aria-hidden="true" viewBox="0 0 24 24" focusable="false">
      <ellipse className="clean-screen-base-fill" cx="12" cy="15.4" rx="7.1" ry="2.45" />
      <ellipse className="clean-screen-base-ring" cx="12" cy="15.15" rx="7.65" ry="2.95" />
      <path className="clean-screen-shine clean-screen-shine-large" d="M10.05 4.9c.55 2.05 1.65 3.15 3.7 3.7-2.05.55-3.15 1.65-3.7 3.7-.55-2.05-1.65-3.15-3.7-3.7 2.05-.55 3.15-1.65 3.7-3.7Z" />
      <path className="clean-screen-shine" d="M16.95 3.85c.28 1.02.83 1.57 1.85 1.85-1.02.28-1.57.83-1.85 1.85-.28-1.02-.83-1.57-1.85-1.85 1.02-.28 1.57-.83 1.85-1.85Z" />
      <path className="clean-screen-shine" d="M16.75 11.15c.22.78.64 1.2 1.42 1.42-.78.22-1.2.64-1.42 1.42-.22-.78-.64-1.2-1.42-1.42.78-.22 1.2-.64 1.42-1.42Z" />
      <path className="clean-screen-sweep" d="M6.05 15.1c2.05-.85 4.2-.95 6.05-.3 1.78.62 3.82.5 5.84-.48" />
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
