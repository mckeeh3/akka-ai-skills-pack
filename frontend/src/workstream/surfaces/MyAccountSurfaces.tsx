import { useMemo, useState } from 'react';
import type { SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type AnyData = Record<string, any>;

type Props = {
  envelope: SurfaceEnvelope<AnyData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
  onFieldValueChange?: (fieldId: string, value: string, surfaceId: string) => void;
};

export function isMyAccountRebuiltSurface(envelope: SurfaceEnvelope<unknown>) {
  return envelope.surfaceId === 'surface-my-profile'
    || envelope.surfaceId === 'surface-my-settings'
    || envelope.surfaceId === 'surface-my-context'
    || envelope.surfaceId === 'surface-my-account-personal-attention-digest-progress'
    || envelope.surfaceId === 'surface-my-account-personal-attention-digest-result'
    || envelope.surfaceId === 'surface-my-account-personal-attention-digest-blocked'
    || envelope.surfaceId === 'surface-my-account-open-denied'
    || String((envelope.data as { surfaceContract?: string } | undefined)?.surfaceContract ?? '').startsWith('my_account.profile.')
    || String((envelope.data as { surfaceContract?: string } | undefined)?.surfaceContract ?? '').startsWith('my_account.preferences.')
    || String((envelope.data as { surfaceContract?: string } | undefined)?.surfaceContract ?? '').startsWith('my_account.context_')
    || String((envelope.data as { surfaceContract?: string } | undefined)?.surfaceContract ?? '').startsWith('my_account.personal_attention_digest.')
    || String((envelope.data as { surfaceContract?: string } | undefined)?.surfaceContract ?? '').startsWith('my_account.open_denied');
}

export function MyAccountSurface({ envelope, onAction, onFieldValueChange }: Props) {
  if (envelope.surfaceId === 'surface-my-account-personal-attention-digest-progress') return <MyAccountDigestProgressSurface envelope={envelope} onAction={onAction} />;
  if (envelope.surfaceId === 'surface-my-account-personal-attention-digest-result') return <MyAccountDigestResultSurface envelope={envelope} onAction={onAction} />;
  if (envelope.surfaceId === 'surface-my-account-personal-attention-digest-blocked' || envelope.surfaceId === 'surface-my-account-open-denied') return <MyAccountRecoverySurface envelope={envelope} onAction={onAction} />;
  if (envelope.surfaceId === 'surface-my-context') return <MyAccountContextSurface envelope={envelope} onAction={onAction} />;
  return <MyAccountSelfServiceSurface envelope={envelope} onAction={onAction} onFieldValueChange={onFieldValueChange} />;
}

function MyAccountSelfServiceSurface({ envelope, onAction, onFieldValueChange }: Props) {
  const fields = Array.isArray(envelope.data.fields) ? envelope.data.fields : [];
  const initialValues = useMemo(() => Object.fromEntries(fields.map((field: AnyData) => [field.fieldId, String(field.value ?? '')])), [fields]);
  const [fieldValues, setFieldValues] = useState<Record<string, string>>(initialValues);
  const actionInput = Object.fromEntries(fields.filter((field: AnyData) => field.editable).map((field: AnyData) => [field.fieldId, fieldValues[field.fieldId] ?? String(field.value ?? '')]));
  const isSettings = envelope.surfaceId === 'surface-my-settings';

  function updateField(fieldId: string, value: string) {
    setFieldValues((current) => ({ ...current, [fieldId]: value }));
    onFieldValueChange?.(fieldId, value, envelope.surfaceId);
  }

  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="my-account-detail-hero" aria-label={isSettings ? 'My Account settings self-service view' : 'My Account profile self-service view'}>
        <div>
          <p className="eyebrow">{isSettings ? 'Personal preferences' : 'Self-service profile'}</p>
          <h3>{envelope.title}</h3>
          <p>{envelope.data.summary ?? (isSettings ? 'Preview and save personal preferences through governed backend actions.' : 'Inspect safe identity facts and edit only allowed self-service fields.')}</p>
        </div>
        <dl className="authority-summary-grid" aria-label="My Account self-service authority">
          <div><dt>View</dt><dd>{envelope.data.surfaceContract ?? envelope.surfaceId}</dd></div>
          <div><dt>Authority</dt><dd>{envelope.data.permissionState?.authoritativeCapabilityId ?? 'my_account.update_profile_settings'}</dd></div>
          <div><dt>Provider facts</dt><dd>{stringify(envelope.data.providerBoundarySummary ?? 'immutable provider-backed fields stay read-only')}</dd></div>
          <div><dt>Redaction</dt><dd>{stringify(envelope.data.redaction ?? 'browser-safe profile only')}</dd></div>
        </dl>
      </section>

      <form className="surface-detail-edit-form my-account-self-service-form" aria-label={`${envelope.title} governed self-service form`} onSubmit={(event) => event.preventDefault()}>
        {fields.map((field: AnyData) => <MyAccountField key={field.fieldId} field={field} value={fieldValues[field.fieldId] ?? String(field.value ?? '')} onChange={updateField} surfaceId={envelope.surfaceId} />)}
      </form>
      <p className="form-status" aria-live="polite">Field changes are local preview only until a governed save action succeeds; backend validation, idempotency, authorization, and audit remain authoritative.</p>
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} actionInput={actionInput} onAction={onAction} />
      <TraceRefs refs={envelope.data.traceRefs ?? envelope.traceIds} />
    </SurfaceStateFrame>
  );
}

function MyAccountContextSurface({ envelope, onAction }: Props) {
  const contexts = Array.isArray(envelope.data.availableContexts) ? envelope.data.availableContexts : [];
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="my-account-detail-hero" aria-label="My Account context and authority view">
        <div><p className="eyebrow">Context & authority</p><h3>{envelope.title}</h3><p>{envelope.data.summary ?? 'Backend-owned selected context governs workstream visibility, actions, traces, and agent behavior.'}</p></div>
        <dl className="authority-summary-grid" aria-label="Selected backend AuthContext">
          <div><dt>Selected context</dt><dd>{stringify(envelope.data.selectedContext?.contextId ?? envelope.authContext.selectedContextId)}</dd></div>
          <div><dt>Scope</dt><dd>{stringify(envelope.data.selectedContext?.contextType ?? envelope.authContext.customerId ?? 'tenant')}</dd></div>
          <div><dt>Roles</dt><dd>{stringify(envelope.data.roleSummary ?? envelope.data.authorityBasis ?? 'backend selected')}</dd></div>
          <div><dt>Capabilities</dt><dd>{stringify(envelope.data.visibleCapabilitySummary ?? `${envelope.authContext.visibleCapabilityIds.length} visible`)}</dd></div>
        </dl>
      </section>
      <section className="my-account-section" aria-labelledby={`${envelope.surfaceId}-contexts-heading`}>
        <div className="surface-section-heading"><div><p className="eyebrow">Available contexts</p><h4 id={`${envelope.surfaceId}-contexts-heading`}>Switch only to backend-authorized contexts</h4></div><p>Client context ids are advisory request inputs; backend decides visibility and marks stale views after a switch.</p></div>
        <div className="my-account-control-panels" aria-label="Authorized context choices">
          {contexts.map((context: AnyData) => <article key={context.contextId ?? context.label} className={`my-account-control-panel ${context.selected ? 'success' : context.selectable === false ? 'warning' : 'info'}`}><p className="eyebrow">{context.selected ? 'Selected' : context.selectable === false ? 'Unavailable' : 'Available'}</p><h4>{context.label ?? context.contextId}</h4><p>{context.denialHint ?? context.contextType ?? 'Backend-authorized context option'}</p></article>)}
        </div>
      </section>
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
      <TraceRefs refs={envelope.data.traceRefs ?? envelope.traceIds} />
    </SurfaceStateFrame>
  );
}

function MyAccountDigestProgressSurface({ envelope, onAction }: Props) {
  const events = Array.isArray(envelope.data.progressEvents) ? envelope.data.progressEvents : Array.isArray(envelope.data.steps) ? envelope.data.steps : [];
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="my-account-digest-progress" aria-label="Personal attention digest progress">
        <div><p className="eyebrow">Advisory digest progress</p><h3>{envelope.title}</h3><p>{envelope.data.summary ?? 'Digest work is backend-governed, provider-aware, redacted, and does not mutate source attention.'}</p></div>
        <dl className="authority-summary-grid"><div><dt>Status</dt><dd>{stringify(envelope.data.status ?? envelope.data.phase ?? 'not-started')}</dd></div><div><dt>No direct mutation</dt><dd>{String(envelope.data.noDirectMutation ?? true)}</dd></div><div><dt>Evidence window</dt><dd>{stringify(envelope.data.evidenceWindow ?? 'authorized personal attention only')}</dd></div><div><dt>Provider/runtime blocker</dt><dd>{stringify(envelope.data.blockedReason ?? envelope.data.providerReadiness ?? envelope.data.runtimeReadiness ?? 'none visible')}</dd></div></dl>
        {envelope.data.noFakeSuccess && <p className="surface-state-inline forbidden">Provider/runtime status is fail-closed; no fixture, deterministic, or model-less digest success is shown.</p>}
      </section>
      <section className="surface-timeline" aria-label="Durable digest progress events">{events.map((event: AnyData, index: number) => <article key={event.eventId ?? event.stepId ?? index} className={`surface-timeline-event ${event.status ?? 'ready'}`}><strong>{event.label ?? event.phase ?? event.stepId ?? `Event ${index + 1}`}</strong><p>{event.summary ?? event.explanation ?? event.status}</p></article>)}</section>
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} actionInput={digestInput(envelope)} onAction={onAction} />
      <TraceRefs refs={envelope.data.traceRefs ?? envelope.traceIds} />
    </SurfaceStateFrame>
  );
}

function MyAccountDigestResultSurface({ envelope, onAction }: Props) {
  const evidence = Array.isArray(envelope.data.evidenceRefs) ? envelope.data.evidenceRefs : Array.isArray(envelope.data.sourceRefs) ? envelope.data.sourceRefs : [];
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="my-account-detail-hero" aria-label="Personal attention digest advisory result">
        <div><p className="eyebrow">Advisory result</p><h3>{envelope.title}</h3><p>{envelope.data.summary ?? envelope.data.resultSummary ?? 'Review authorized evidence, omissions, and advisory recommendations without resolving source work.'}</p></div>
        <dl className="authority-summary-grid"><div><dt>Review state</dt><dd>{stringify(envelope.data.reviewState ?? envelope.data.status ?? 'completed-review-required')}</dd></div><div><dt>Advisory only</dt><dd>{String(envelope.data.advisoryOnly ?? true)}</dd></div><div><dt>Omissions</dt><dd>{stringify(envelope.data.omissionSummary ?? envelope.data.redaction ?? 'browser-safe evidence only')}</dd></div><div><dt>Source mutation</dt><dd>{String(envelope.data.noDirectMutation ?? true)}</dd></div></dl>
      </section>
      <section className="surface-section-list" aria-label="Digest evidence and authorized source links">{evidence.map((ref: AnyData, index: number) => <article key={ref.refId ?? ref.traceId ?? index} className="surface-section-card"><h4>{ref.label ?? ref.refId ?? `Evidence ${index + 1}`}</h4><p>{ref.summary ?? ref.refType ?? 'Browser-safe evidence reference'}</p></article>)}</section>
      {Array.isArray(envelope.data.recommendations) && envelope.data.recommendations.length > 0 && <section className="recommendation-list" aria-label="Digest advisory recommendations"><h4>Advisory recommendations</h4><ul>{envelope.data.recommendations.map((item: AnyData, index: number) => <li key={item.recommendationId ?? item.label ?? index}>{stringify(item.label ?? item.title ?? item)}</li>)}</ul></section>}
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} actionInput={digestInput(envelope)} onAction={onAction} />
      <TraceRefs refs={envelope.data.traceRefs ?? envelope.traceIds} />
    </SurfaceStateFrame>
  );
}

function MyAccountRecoverySurface({ envelope, onAction }: Props) {
  const steps = envelope.data.recoverySteps ?? envelope.data.recoveryOptions ?? [];
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="my-account-detail-hero provider-fail-closed" aria-label="My Account recovery message">
        <div><p className="eyebrow">Safe recovery</p><h3>{envelope.data.title ?? envelope.title}</h3><p>{envelope.data.message ?? envelope.data.summary ?? 'The requested work is unavailable, denied, stale, or provider/runtime blocked in the selected context.'}</p></div>
        <dl className="authority-summary-grid"><div><dt>Reason</dt><dd>{stringify(envelope.data.safeReasonCode ?? envelope.data.blockerCode ?? envelope.data.status)}</dd></div><div><dt>No fake success</dt><dd>{String(envelope.data.noFakeSuccess ?? true)}</dd></div><div><dt>No enumeration</dt><dd>{String(envelope.data.noEnumeration ?? envelope.surfaceId === 'surface-my-account-open-denied')}</dd></div><div><dt>No direct mutation</dt><dd>{String(envelope.data.noDirectMutation ?? true)}</dd></div><div><dt>Provider readiness</dt><dd>{stringify(envelope.data.providerReadiness ?? 'browser-safe readiness only')}</dd></div><div><dt>Runtime readiness</dt><dd>{stringify(envelope.data.runtimeReadiness ?? 'browser-safe readiness only')}</dd></div></dl>
      </section>
      {steps.length > 0 && <section className="surface-section-list" aria-label="Recovery steps"><h4>Recovery steps</h4>{steps.map((step: any, index: number) => <article key={step.stepId ?? step.label ?? index} className="surface-section-card"><h4>{typeof step === 'string' ? step : step.label ?? `Step ${index + 1}`}</h4>{typeof step !== 'string' && <p>{step.description ?? step.disabledReason ?? step.expectedOutcome}</p>}</article>)}</section>}
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} actionInput={digestInput(envelope)} onAction={onAction} />
      <TraceRefs refs={envelope.data.traceRefs ?? envelope.traceIds} />
    </SurfaceStateFrame>
  );
}

function MyAccountField({ field, value, onChange, surfaceId }: { field: AnyData; value: string; onChange: (fieldId: string, value: string) => void; surfaceId: string }) {
  const inputId = `${surfaceId}-${field.fieldId}`;
  const options = Array.isArray(field.options) ? field.options : [];
  return <div className="surface-detail-field"><label htmlFor={inputId}>{field.label}</label>{field.inputType === 'select' && options.length > 0 ? <select className="designed-control surface-detail-control" id={inputId} value={value} disabled={!field.editable} onChange={(event) => onChange(field.fieldId, event.currentTarget.value)}>{options.map((option: AnyData) => <option key={option.value ?? option.label} value={option.value ?? option.label}>{option.label ?? option.value}</option>)}</select> : field.inputType === 'textarea' ? <textarea className="designed-control surface-detail-control" id={inputId} value={value} readOnly={!field.editable} onChange={(event) => onChange(field.fieldId, event.currentTarget.value)} /> : <input className="designed-control surface-detail-control" id={inputId} type={field.inputType ?? 'text'} value={value} readOnly={!field.editable} onChange={(event) => onChange(field.fieldId, event.currentTarget.value)} />}<p className="field-helper">{field.helperText ?? (field.editable ? 'Editable; save through governed action.' : field.disabledReason ?? 'Read-only provider or policy fact.')}</p></div>;
}

function TraceRefs({ refs }: { refs?: unknown[] }) {
  if (!refs?.length) return null;
  return <details className="dashboard-evidence-drawer"><summary>Role-gated trace details</summary><section className="trace-link-list" aria-label="My Account trace references">{refs.map((ref, index) => { const value = typeof ref === 'string' ? ref : stringify((ref as AnyData).traceId ?? (ref as AnyData).refId ?? ref); return <a key={`${value}-${index}`} href={`/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(value)}`}>{value}</a>; })}</section></details>;
}

function digestInput(envelope: SurfaceEnvelope<AnyData>): Record<string, string> {
  return Object.fromEntries(Object.entries({ digestTaskId: envelope.data.digestTaskId ?? envelope.data.taskId, correlationId: envelope.correlationId }).filter(([, value]) => value !== undefined).map(([key, value]) => [key, String(value)]));
}

function stringify(value: unknown): string {
  if (Array.isArray(value)) return value.map(stringify).join(', ');
  if (value && typeof value === 'object') return Object.entries(value as AnyData).map(([key, nested]) => `${key}: ${stringify(nested)}`).join('; ');
  return String(value ?? '');
}
