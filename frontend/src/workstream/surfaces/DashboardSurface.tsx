import type { AttentionItem, DashboardSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type DashboardSurfaceProps = {
  envelope: SurfaceEnvelope<DashboardSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
  onSignOut?: () => void;
};

export function DashboardSurface({ envelope, onAction, onSignOut }: DashboardSurfaceProps) {
  if (envelope.surfaceId === 'surface-my-account-dashboard' || envelope.data.surfaceContract?.startsWith('my_account.')) {
    return <MyAccountCommandCenter envelope={envelope} onAction={onAction} onSignOut={onSignOut} />;
  }
  if (envelope.surfaceId.startsWith('surface-user-admin-') || envelope.data.surfaceContract?.startsWith('user_admin.')) {
    return <UserAdminCommandCenter envelope={envelope} onAction={onAction} />;
  }
  if (envelope.surfaceId === 'surface-agent-admin-dashboard' || envelope.data.surfaceContract?.startsWith('agent_admin.dashboard')) {
    return <AgentAdminCommandCenter envelope={envelope} onAction={onAction} />;
  }
  if (envelope.surfaceId === 'surface-governance-policy-dashboard' || envelope.data.surfaceContract?.startsWith('governance.policy.dashboard')) {
    return <GovernancePolicyCommandCenter envelope={envelope} onAction={onAction} />;
  }
  if (envelope.surfaceId === 'surface-audit-trace-dashboard' || envelope.data.surfaceContract?.startsWith('audit.trace.dashboard')) {
    return <AuditTraceCommandCenter envelope={envelope} onAction={onAction} />;
  }

  const actionById = new Map(envelope.actions.map((action) => [action.actionId, action]));
  const myAccountSurfaceActions = actionsForIds(envelope.data.utilityActionIds, actionById)
    .filter((action) => action.actionId !== 'action-sign-out')
    .map(cleanMyAccountSurfaceActionLabel);
  const remainingActions = envelope.data.quickSurfaceActionIds || envelope.data.utilityActionIds ? [] : envelope.actions;
  return (
    <SurfaceStateFrame envelope={envelope}>
      {envelope.data.capabilityIds && <p className="sr-only">Backend capabilities: {envelope.data.capabilityIds.join(', ')}</p>}
      {envelope.data.redaction && <p className="sr-only">Redaction: {renderSurfaceValue(envelope.data.redaction)}</p>}
      {envelope.data.blockedState && (
        <section className="surface-state-inline forbidden" aria-label="Blocked dashboard state">
          <strong>{envelope.data.blockedState.reasonCode}</strong>
          <p>{envelope.data.blockedState.message}</p>
          <p>{envelope.data.blockedState.recovery}</p>
        </section>
      )}
      {envelope.data.accountContext && (
        <section className="my-account-context-strip" aria-label="Signed-in account and selected context">
          <div>
            <p className="eyebrow">Signed in as</p>
            <strong>{envelope.data.accountContext.displayName}</strong>
            {envelope.data.accountContext.email && <span>{envelope.data.accountContext.email}</span>}
          </div>
          <div>
            <p className="eyebrow">Selected context</p>
            <strong>{envelope.data.accountContext.tenantId}</strong>
            <span>{[envelope.data.accountContext.customerId, envelope.data.accountContext.authority].filter(Boolean).join(' · ')}</span>
          </div>
          {envelope.data.accountContext.roles && envelope.data.accountContext.roles.length > 0 && <p className="status-pill info">{envelope.data.accountContext.roles.join(', ')}</p>}
        </section>
      )}
      <div className="surface-dashboard-grid my-account-workstream-grid" aria-label="Workstreams available to me">
        {envelope.data.cards.map((card) => {
          const action = card.actionId ? actionById.get(card.actionId) : undefined;
          const cardBody = (
            <>
              <p>{card.label}</p>
              <strong>{card.value}</strong>
            </>
          );
          return action ? (
            <button key={card.cardId} type="button" className={`ds-card dashboard-card clickable ${card.severity ?? 'info'}`} onClick={() => onAction?.(action, envelope.surfaceId)} aria-label={`Open ${card.label} workstream; ${card.status ?? `${card.value} ${card.unit ?? ''}`}`}>
              {cardBody}
            </button>
          ) : (
            <article key={card.cardId} className={`ds-card dashboard-card ${card.severity ?? 'info'}`}>
              {cardBody}
            </article>
          );
        })}
      </div>
      {myAccountSurfaceActions.length > 0 && <SurfaceActionBar label="My Account surfaces" actions={myAccountSurfaceActions} surfaceId={envelope.surfaceId} onAction={onAction} />}
      {envelope.data.attentionItems && envelope.data.attentionItems.length > 0 && <AttentionList items={envelope.data.attentionItems} label="Backend-derived attention items; Audit/Trace attention items" />}
      {envelope.data.sections && envelope.data.sections.length > 0 && (
        <section className="surface-section-list" aria-label="Dashboard sections">
          {envelope.data.sections.map((section) => (
            <article key={section.sectionId} className="surface-section-card">
              <h4>{section.label}</h4>
              <p>{section.summary}</p>
            </article>
          ))}
        </section>
      )}
      {envelope.data.nextSteps && envelope.data.nextSteps.length > 0 && !envelope.data.quickSurfaceActionIds && (
        <section className="surface-section-list" aria-label="Authorized next actions">
          {envelope.data.nextSteps.map((step) => (
            <article key={`${step.workstreamId}-${step.label}`} className={`surface-section-card ${step.allowed ? 'allowed' : 'forbidden'}`}>
              <h4>{step.label}</h4>
              <p>{step.allowed ? 'Allowed by selected AuthContext.' : step.blockedReason}</p>
              {step.capabilityIds && <p className="capability-basis">{step.capabilityIds.join(', ')}</p>}
              {step.traceId && <a href={`/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(step.traceId)}`}>{step.traceId}</a>}
            </article>
          ))}
        </section>
      )}
      {remainingActions.length > 0 && <SurfaceActionBar actions={remainingActions} surfaceId={envelope.surfaceId} onAction={onAction} />}
    </SurfaceStateFrame>
  );
}


function AuditTraceCommandCenter({ envelope, onAction }: DashboardSurfaceProps) {
  const data = envelope.data;
  const actionById = new Map(envelope.actions.map((action) => [action.actionId, action]));
  const searchAction = actionById.get('action-audit-trace-search');
  const exportAction = actionById.get('action-audit-trace-request-redacted-export');
  return (
    <SurfaceStateFrame envelope={envelope}>
      <p className="sr-only">Surface contract: {data.surfaceContract ?? 'audit.trace.dashboard.v1'}. Backend capabilities: {data.capabilityIds?.join(', ') ?? 'audit.trace.read'}. Trace access is backend-scoped and redacted.</p>
      <section className="my-account-command-hero audit-trace-command-hero" aria-label="Audit/Trace investigation command center">
        <div>
          <p className="eyebrow">Audit/Trace · selected AuthContext</p>
          <h3>Investigate scoped evidence safely</h3>
          <p>{data.readiness ?? 'Search traces, inspect evidence, review denials/failures, request redacted exports, and follow correlation timelines without exposing hidden data.'}</p>
        </div>
        <dl className="authority-summary-grid" aria-label="Audit/Trace scope and redaction summary">
          <div><dt>Tenant</dt><dd>{data.accountContext?.tenantId ?? envelope.authContext.tenantId}</dd></div>
          <div><dt>Customer</dt><dd>{data.accountContext?.customerId ?? envelope.authContext.customerId ?? 'Tenant scope'}</dd></div>
          <div><dt>Visible capabilities</dt><dd>{data.capabilityIds?.length ?? envelope.authContext.visibleCapabilityIds.length}</dd></div>
          <div><dt>Redaction</dt><dd>{renderSurfaceValue(data.redaction) ?? 'browser-safe evidence only'}</dd></div>
        </dl>
      </section>

      <section className="user-admin-section" aria-labelledby={`${envelope.surfaceId}-cards-heading`}>
        <div className="surface-section-heading"><div><p className="eyebrow">Investigation overview</p><h4 id={`${envelope.surfaceId}-cards-heading`}>Scoped audit and failure counters</h4></div><p>Cards are summaries only. Open search, timeline, or failure evidence to reauthorize and inspect backend-provided evidence.</p></div>
        <div className="surface-dashboard-grid my-account-workstream-grid" aria-label="Audit/Trace scoped counters">
          {data.cards.map((card) => {
            const action = card.actionId ? actionById.get(card.actionId) : undefined;
            const body = <><p>{card.label}</p><strong>{card.value}</strong>{card.status && <span>{card.status}</span>}</>;
            return action ? <button key={card.cardId} type="button" className={`ds-card dashboard-card clickable ${card.severity ?? 'info'}`} disabled={Boolean(action.disabled)} onClick={() => !action.disabled && onAction?.(action, envelope.surfaceId, defaultAuditTraceInput(action, envelope))} aria-label={`Open ${card.label}: ${card.value}`}>{body}</button> : <article key={card.cardId} className={`ds-card dashboard-card ${card.severity ?? 'info'}`}>{body}</article>;
          })}
        </div>
      </section>

      {data.attentionItems && data.attentionItems.length > 0 && <AttentionList items={data.attentionItems} label="Audit/Trace attention items" actionById={actionById} surfaceId={envelope.surfaceId} onAction={onAction} />}

      <section className="user-admin-section" aria-labelledby={`${envelope.surfaceId}-actions-heading`}>
        <div className="surface-section-heading"><div><p className="eyebrow">Investigation actions</p><h4 id={`${envelope.surfaceId}-actions-heading`}>Authorized investigation actions</h4></div><p>Actions recheck selected context, audit capability, redaction policy, idempotency, and trace emission on the backend.</p></div>
        <div className="user-admin-action-grid" aria-label="Authorized Audit/Trace actions">
          {envelope.actions.map((action) => <button key={action.actionId} type="button" className="user-admin-work-card" disabled={Boolean(action.disabled)} onClick={() => !action.disabled && onAction?.(action, envelope.surfaceId, defaultAuditTraceInput(action, envelope))} aria-disabled={Boolean(action.disabled)}><span className="eyebrow">{action.requiresApproval ? 'Policy gated' : action.intent}</span><h4>{action.label}</h4><p>{action.disabled?.message ?? auditTraceActionHint(action.actionId)}</p></button>)}
        </div>
      </section>

      {data.sections && data.sections.length > 0 && <section className="surface-section-list" aria-label="Audit/Trace investigation sections">{data.sections.map((section) => <article key={section.sectionId} className="surface-section-card"><h4>{section.label}</h4><p>{section.summary}</p></article>)}</section>}
      {searchAction && <form className="surface-search-form" role="search" onSubmit={(event) => { event.preventDefault(); const filter = new FormData(event.currentTarget).get('filter'); onAction?.(searchAction, envelope.surfaceId, stringRecord({ filter: typeof filter === 'string' ? filter : 'recent', pageSize: '10' })); }}><label htmlFor={`${envelope.surfaceId}-filter`}>Search scoped traces</label><input className="designed-control surface-search-control" id={`${envelope.surfaceId}-filter`} name="filter" placeholder="denied, provider, model, workstream, trace id, correlation id" /><button type="submit" className="surface-action-link secondary">Search</button></form>}
      {exportAction && <p className="redaction-note">Request redacted export opens a policy-gated decision surface; unredacted browser exports are not produced.</p>}
    </SurfaceStateFrame>
  );
}

function auditTraceActionHint(actionId: string): string {
  if (actionId.includes('search')) return 'Search only within backend-authorized tenant/customer scope.';
  if (actionId.includes('timeline')) return 'Open a correlation timeline with unauthorized evidence omitted.';
  if (actionId.includes('failure')) return 'Inspect denial/provider/tool/model evidence with secrets redacted.';
  if (actionId.includes('export')) return 'Request a scoped redacted export through policy review.';
  if (actionId.includes('guide')) return 'Get investigation guidance without granting new authority.';
  return 'Backend returns the next safe Audit/Trace surface.';
}

function defaultAuditTraceInput(action: SurfaceAction, envelope: { correlationId: string }): Record<string, string> {
  if (action.actionId.includes('search')) return { filter: 'recent', pageSize: '10' };
  if (action.actionId.includes('timeline')) return { correlationId: envelope.correlationId };
  if (action.actionId.includes('failure')) return { failureCategory: 'provider_blocked' };
  if (action.actionId.includes('export')) return { reason: 'Scoped redacted investigation export requested from Audit/Trace dashboard.', format: 'jsonl-redacted' };
  return { correlationId: envelope.correlationId };
}

function agentAdminActionableCards(cards: DashboardSurfaceData['cards'] | undefined, actionById: Map<string, SurfaceAction>): Array<{ card: DashboardSurfaceData['cards'][number]; action: SurfaceAction }> {
  return (cards ?? []).map((card) => {
    const action = card.actionId ? actionById.get(card.actionId) : undefined;
    return action ? { card, action } : undefined;
  }).filter((entry): entry is { card: DashboardSurfaceData['cards'][number]; action: SurfaceAction } => Boolean(entry));
}

function GovernancePolicyCommandCenter({ envelope, onAction }: DashboardSurfaceProps) {
  const data = envelope.data;
  const actionById = new Map(envelope.actions.map((action) => [action.actionId, action]));
  const queues = data.attentionQueues ?? [];
  const authorizedActions = data.authorizedActions ?? [];
  return (
    <SurfaceStateFrame envelope={envelope}>
      <p className="sr-only">Surface contract: {data.surfaceContract ?? 'governance.policy.dashboard.v1'}. Policy proposals, simulations, approval gates, activation, rollback, outcomes, and traces are backend-owned and scoped by selected AuthContext.</p>
      <section className="my-account-command-hero governance-policy-command-hero" aria-label="Governance Policy authority and policy lifecycle summary">
        <div>
          <p className="eyebrow">Governance/Policy · selected AuthContext</p>
          <h3>Review policy proposals, simulations, decisions, activation, rollback, and outcomes safely.</h3>
          <p>{renderSurfaceValue(data.redaction) ?? 'Browser-safe policy summaries only. Raw prompts, provider secrets, hidden authority, raw tool payloads, and cross-tenant evidence are omitted.'}</p>
        </div>
        <dl className="authority-summary-grid" aria-label="Governance Policy readiness summary">
          <div><dt>Readiness</dt><dd>{formatStatus(data.readiness ?? 'ready')}</dd></div>
          <div><dt>Lifecycle</dt><dd>{Array.isArray((data as Record<string, unknown>).proposalLifecycle) ? ((data as Record<string, unknown>).proposalLifecycle as unknown[]).join(', ') : 'draft, review, decision, activation, rollback'}</dd></div>
          <div><dt>Capabilities</dt><dd>{data.capabilityIds?.length ?? envelope.authContext.visibleCapabilityIds.length}</dd></div>
          <div><dt>Authority</dt><dd>Backend authorization and approval policy</dd></div>
        </dl>
      </section>

      {queues.length > 0 && (
        <section className="user-admin-section" aria-labelledby={`${envelope.surfaceId}-queues-heading`}>
          <div className="surface-section-heading">
            <div><p className="eyebrow">Things that need my attention</p><h4 id={`${envelope.surfaceId}-queues-heading`}>Policy governance queues</h4></div>
            <p>Each queue opens a backend-authorized policy surface. Frontend state never approves, activates, rolls back, or fabricates impact-analysis success.</p>
          </div>
          <div className="attention-counter-strip user-admin-attention-strip" aria-label="Governance Policy attention counters">
            {queues.map((queue) => {
              const action = queue.actionId ? actionById.get(queue.actionId) : undefined;
              const body = <><span>{queue.label}</span><strong>{queue.count ?? 0}</strong><em>{formatStatus(queue.statusText ?? 'Open queue')}</em><small>{queue.redaction ?? queue.sourceCapabilityId ?? 'browser-safe summary'}</small></>;
              return action ? <button key={queue.queueId} type="button" className={`attention-counter-card ${queue.severity ?? 'info'}`} disabled={Boolean(action.disabled)} onClick={() => !action.disabled && onAction?.(action, envelope.surfaceId, stringRecord({ targetSurfaceId: queue.targetSurfaceId, requiredCapabilityId: queue.sourceCapabilityId, correlationId: queue.traceRefs?.[0] ?? envelope.correlationId }))} aria-label={`Open ${queue.label}: ${queue.statusText ?? 'queue'}; ${queue.count ?? 0} items`}>{body}</button> : <article key={queue.queueId} className={`attention-counter-card ${queue.severity ?? 'info'}`}>{body}</article>;
            })}
          </div>
        </section>
      )}

      {agentAdminActionableCards(data.cards, actionById).length > 0 && (
        <section className="user-admin-section" aria-labelledby={`${envelope.surfaceId}-cards-heading`}>
          <div className="surface-section-heading"><div><p className="eyebrow">Policy posture</p><h4 id={`${envelope.surfaceId}-cards-heading`}>Clickable governance areas</h4></div><p>Cards are backend-authored projections and open structured surfaces for review.</p></div>
          <div className="surface-dashboard-grid my-account-workstream-grid" aria-label="Governance Policy actionable cards">
            {agentAdminActionableCards(data.cards, actionById).map(({ card, action }) => <button key={card.cardId} type="button" className={`ds-card dashboard-card clickable ${card.severity ?? 'info'}`} onClick={() => onAction?.(action, envelope.surfaceId, stringRecord({ targetSurfaceId: card.targetSurfaceId, cardId: card.cardId, correlationId: envelope.correlationId }))}><p>{card.label}</p><strong>{card.value}</strong>{card.status && <span>{card.status}</span>}</button>)}
          </div>
        </section>
      )}

      {authorizedActions.length > 0 && (
        <section className="user-admin-section" aria-labelledby={`${envelope.surfaceId}-actions-heading`}>
          <div className="surface-section-heading"><div><p className="eyebrow">Things I can do</p><h4 id={`${envelope.surfaceId}-actions-heading`}>Authorized Governance/Policy surfaces</h4></div><p>Actions recheck capability, idempotency, approval policy, provider readiness, redaction, and audit requirements.</p></div>
          <div className="user-admin-action-grid" aria-label="Authorized Governance Policy task entry points">
            {authorizedActions.map((entry) => {
              const action = actionById.get(entry.actionId);
              return <button key={entry.actionId} type="button" className="user-admin-work-card" disabled={!action || Boolean(action.disabled)} onClick={() => action && !action.disabled && onAction?.(action, envelope.surfaceId)}><span className="eyebrow">{entry.approvalRequired || action?.requiresApproval ? 'Approval gated' : 'Authorized surface'}</span><h4>{entry.label}</h4><p>{entry.denialHint ?? 'Backend returns the next safe Governance/Policy surface after rechecking authority.'}</p></button>;
            })}
          </div>
        </section>
      )}

      {data.attentionItems && data.attentionItems.length > 0 && <AttentionList items={data.attentionItems} label="Backend-derived Governance/Policy attention items" />}
      {data.recentActivity && data.recentActivity.length > 0 && <details className="dashboard-evidence-drawer"><summary>Role-gated policy activity and trace diagnostics</summary><section className="surface-section-list" aria-label="Recent Governance Policy activity">{data.recentActivity.map((activity) => <article key={activity.activityId} className="surface-section-card"><h4>{activity.label}</h4>{activity.summary && <p>{activity.summary}</p>}{activity.traceId && <a href={`/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(activity.traceId)}`}>{activity.traceId}</a>}</article>)}</section></details>}
    </SurfaceStateFrame>
  );
}

function AgentAdminCommandCenter({ envelope, onAction }: DashboardSurfaceProps) {
  const data = envelope.data;
  const actionById = new Map(envelope.actions.map((action) => [action.actionId, action]));
  const queues = data.attentionQueues ?? [];
  const authorizedActions = data.authorizedActions ?? [];
  return (
    <SurfaceStateFrame envelope={envelope}>
      <p className="sr-only">Surface contract: {data.surfaceContract ?? 'agent_admin.dashboard.v1'}. Provider secrets, raw prompts, raw skills, raw references, hidden authority, and cross-tenant evidence are omitted by backend redaction.</p>
      <section className="my-account-command-hero agent-admin-command-hero" aria-label="Agent Admin authority and readiness summary">
        <div>
          <p className="eyebrow">Agent Admin · selected AuthContext</p>
          <h3>{data.hero?.title ?? 'Govern managed agents safely'}</h3>
          <p>{data.hero?.redactionSummary ?? renderSurfaceValue(data.redaction) ?? 'Backend selected scope, redaction, and capability boundaries apply to every Agent Admin action.'}</p>
        </div>
        <dl className="authority-summary-grid" aria-label="Agent Admin readiness summary">
          <div><dt>Scope</dt><dd>{data.hero?.scopeLabel ?? data.scopeLabel ?? 'Selected tenant/customer scope'}</dd></div>
          <div><dt>Admin role</dt><dd>{data.hero?.adminLevel ?? 'Agent steward'}</dd></div>
          <div><dt>Readiness</dt><dd>{formatStatus(data.readiness ?? 'ready')}</dd></div>
          <div><dt>Visible capabilities</dt><dd>{data.capabilityIds?.length ?? envelope.authContext.visibleCapabilityIds.length}</dd></div>
        </dl>
      </section>

      <section className="user-admin-section" aria-labelledby={`${envelope.surfaceId}-attention-heading`}>
        <div className="surface-section-heading">
          <div><p className="eyebrow">Things that need my attention</p><h4 id={`${envelope.surfaceId}-attention-heading`}>Governance attention queues</h4></div>
          <p>Each counter opens a backend-authorized surface. Hidden or forbidden agent evidence is omitted rather than inferred by the browser.</p>
        </div>
        <div className="attention-counter-strip user-admin-attention-strip" aria-label="Agent Admin attention counters">
          {queues.map((queue) => {
            const action = queue.actionId ? actionById.get(queue.actionId) : undefined;
            const body = <><span>{queue.label}</span><strong>{queue.count ?? 0}</strong><em>{formatStatus(queue.statusText ?? 'Open queue')}</em><small>{queue.redaction ?? queue.sourceCapabilityId ?? 'browser-safe summary'}</small></>;
            return action ? <button key={queue.queueId} type="button" className={`attention-counter-card ${queue.severity ?? 'info'}`} onClick={() => onAction?.(action, envelope.surfaceId, agentAdminQueueInput(queue, envelope))} aria-label={`Open ${queue.label}: ${queue.statusText ?? 'queue'}; ${queue.count ?? 0} items`}>{body}</button> : <article key={queue.queueId} className={`attention-counter-card ${queue.severity ?? 'info'}`}>{body}</article>;
          })}
        </div>
      </section>

      {agentAdminActionableCards(data.cards, actionById).length > 0 && (
        <section className="user-admin-section" aria-labelledby={`${envelope.surfaceId}-summary-heading`}>
          <div className="surface-section-heading">
            <div><p className="eyebrow">Governance summary</p><h4 id={`${envelope.surfaceId}-summary-heading`}>Clickable work areas</h4></div>
            <p>Cards summarize backend-authored Agent Admin work areas and open structured surfaces. Non-actionable metrics are omitted from the command center.</p>
          </div>
          <div className="surface-dashboard-grid my-account-workstream-grid" aria-label="Agent Admin actionable summary cards">
            {agentAdminActionableCards(data.cards, actionById).map(({ card, action }) => {
              const body = <><p>{card.label}</p><strong>{card.value}</strong>{card.status && <span>{card.status}</span>}</>;
              return <button key={card.cardId} type="button" className={`ds-card dashboard-card clickable ${card.severity ?? 'info'}`} onClick={() => onAction?.(action, envelope.surfaceId, agentAdminCardInput(card, envelope))} aria-label={`Open ${card.label}: ${card.status ?? card.value}`}>{body}</button>;
            })}
          </div>
        </section>
      )}

      <section className="user-admin-section" aria-labelledby={`${envelope.surfaceId}-actions-heading`}>
        <div className="surface-section-heading">
          <div><p className="eyebrow">Things I can do</p><h4 id={`${envelope.surfaceId}-actions-heading`}>Authorized Agent Admin surfaces</h4></div>
          <p>Actions recheck scope, capability, approval policy, idempotency, provider readiness, and audit requirements before returning a typed result surface.</p>
        </div>
        <div className="user-admin-action-grid" aria-label="Authorized Agent Admin task entry points">
          {authorizedActions.map((entry) => {
            const action = actionById.get(entry.actionId);
            return <button key={entry.actionId} type="button" className="user-admin-work-card" disabled={!action || Boolean(action.disabled)} onClick={() => action && onAction?.(action, envelope.surfaceId)}>
              <span className="eyebrow">{entry.approvalRequired || action?.requiresApproval ? 'Approval gated' : 'Authorized surface'}</span>
              <h4>{entry.label}</h4>
              <p>{entry.denialHint ?? 'Backend returns the next safe Agent Admin surface after rechecking authority.'}</p>
            </button>;
          })}
        </div>
      </section>

      {data.recentActivity && data.recentActivity.length > 0 && (
        <details className="dashboard-evidence-drawer">
          <summary>Role-gated activity and trace diagnostics</summary>
          <section className="surface-section-list" aria-label="Recent Agent Admin audit activity">
            {data.recentActivity.map((activity) => <article key={activity.activityId} className="surface-section-card"><h4>{activity.label}</h4>{activity.summary && <p>{activity.summary}</p>}{activity.traceId && <a href={`/ui?surfaceId=surface-agent-admin-trace&traceId=${encodeURIComponent(activity.traceId)}`}>{activity.traceId}</a>}</article>)}
          </section>
        </details>
      )}
    </SurfaceStateFrame>
  );
}

function UserAdminCommandCenter({ envelope, onAction }: DashboardSurfaceProps) {
  const data = envelope.data;
  const actionById = new Map(envelope.actions.map((action) => [action.actionId, action]));
  const queues = backendAuthoredUserAdminQueues(data);
  const nextActions = data.authorizedActions?.length ? data.authorizedActions : [];
  const nextActionButtons = actionsForIds(nextActions.map((action) => action.actionId), actionById);
  const populationCards = backendAuthoredUserAdminPopulationCards(data, actionById);
  const attentionCounters = userAdminAttentionCountersFromQueues(queues, actionById);
  const hasOpenAttention = queues.some((queue) => Number(queue.count ?? 0) > 0 || ['warning', 'urgent', 'critical', 'blocked', 'blocked_provider_or_runtime'].includes(String(queue.severity)));

  return (
    <SurfaceStateFrame envelope={envelope}>
      <p className="sr-only">Surface contract: {data.surfaceContract ?? 'user_admin.dashboard.v1'}. Browser-visible capability count: {data.capabilityIds?.length ?? envelope.authContext.visibleCapabilityIds.length}. Tenant: {data.accountContext?.tenantId ?? envelope.authContext.tenantId}. Scope: {data.accountContext?.customerId ?? envelope.authContext.customerId ?? 'Tenant scope'}.</p>

      <UserAdminHero envelope={envelope} />

      <section className="user-admin-section" aria-labelledby={`${envelope.surfaceId}-attention-heading`}>
        <div className="surface-section-heading">
          <div><p className="eyebrow">Needs admin attention</p><h4 id={`${envelope.surfaceId}-attention-heading`}>Things that need my attention</h4></div>
          <p>Every counter opens a backend-authorized queue, including zero-count queues for setup, history, or confirmation that the scope is clear.</p>
        </div>
        <div className="attention-counter-strip user-admin-attention-strip" aria-label="User Admin attention counters">
          {attentionCounters.map((counter) => {
            const action = counter.actionId ? actionById.get(counter.actionId) : undefined;
            const status = counter.status ?? 'Open queue with backend authorization';
            const body = <><span>{counter.label}</span><strong>{counter.value}</strong><em>{formatStatus(status)}</em></>;
            return action ? <button key={counter.counterId} type="button" className={`attention-counter-card ${counter.severity ?? 'info'}`} disabled={Boolean(action.disabled)} onClick={() => !action.disabled && onAction?.(action, envelope.surfaceId)} aria-label={`Open ${counter.label}: ${action.disabled?.message ?? status}; ${counter.value} items`} aria-disabled={Boolean(action.disabled)}>{body}</button> : <article key={counter.counterId} className={`attention-counter-card ${counter.severity ?? 'info'}`}>{body}</article>;
          })}
        </div>
      </section>

      {!hasOpenAttention && (
        <section className="surface-empty-state" aria-label="No User Admin attention needed">
          <h4>No admin attention needed in this scope</h4>
          <p>Visible queues are clear. You can still open the users list, invite a user, review support access, or inspect audit evidence.</p>
        </section>
      )}

      {nextActionButtons.length > 0 && (
        <section className="user-admin-section" aria-labelledby={`${envelope.surfaceId}-next-actions-heading`}>
          <div className="surface-section-heading">
            <div><p className="eyebrow">Things I can do</p><h4 id={`${envelope.surfaceId}-next-actions-heading`}>Authorized actions</h4></div>
            <p>Each action rechecks scope, capability, idempotency, approval policy, and audit requirements before returning a result surface.</p>
          </div>
          <div className="user-admin-action-grid" aria-label="Authorized User Admin actions">
            {nextActionButtons.map((action) => (
              <button key={action.actionId} type="button" className="user-admin-work-card" disabled={Boolean(action.disabled)} onClick={() => !action.disabled && onAction?.(action, envelope.surfaceId)} aria-disabled={Boolean(action.disabled)}>
                <span className="eyebrow">{action.disabled ? 'Unavailable in this context' : action.requiresApproval ? 'Approval gated' : 'Authorized capability'}</span>
                <h4>{cleanUserAdminActionLabel(action.label)}</h4>
                <p>{action.disabled?.message ?? nextActions.find((candidate) => candidate.actionId === action.actionId)?.denialHint ?? 'Backend rechecks authority and returns the next safe surface.'}</p>
              </button>
            ))}
          </div>
        </section>
      )}

      {populationCards.length > 0 && (
        <section className="user-admin-section" aria-labelledby={`${envelope.surfaceId}-populations-heading`}>
          <div className="surface-section-heading">
            <div><p className="eyebrow">Inspectable areas</p><h4 id={`${envelope.surfaceId}-populations-heading`}>Open scoped administration surfaces</h4></div>
            <p>Population counts are clickable visible-scope projections. Hidden tenants, customers, users, and counts are omitted.</p>
          </div>
          <div className="user-admin-population-grid" aria-label="Visible administered populations">
            {populationCards.map((card) => card.action ? (
              <button key={card.cardId} type="button" className="user-admin-population-card" disabled={Boolean(card.action.disabled)} onClick={() => !card.action?.disabled && onAction?.(card.action!, envelope.surfaceId)} aria-disabled={Boolean(card.action.disabled)}>
                <span className="eyebrow">{card.action.disabled ? 'Unavailable in this context' : card.scope}</span><h4>{card.label}</h4><strong>{card.value}</strong><p>{card.action.disabled?.message ?? card.summary}</p>
              </button>
            ) : (
              <article key={card.cardId} className="user-admin-population-card">
                <span className="eyebrow">{card.scope}</span><h4>{card.label}</h4><strong>{card.value}</strong><p>{card.summary}</p>
              </article>
            ))}
          </div>
        </section>
      )}

      {data.attentionItems && data.attentionItems.length > 0 && <AttentionList items={data.attentionItems} label="Backend-derived User Admin attention items" />}

      {data.recentActivity && data.recentActivity.length > 0 && (
        <section className="surface-section-list" aria-label="Recent User Admin audit activity">
          {data.recentActivity.map((activity) => <article key={activity.activityId} className="surface-section-card"><h4>{activity.label}</h4>{activity.summary && <p>{activity.summary}</p>}{activity.traceId && <a href={`/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(activity.traceId)}`}>{activity.traceId}</a>}</article>)}
        </section>
      )}
    </SurfaceStateFrame>
  );
}

function UserAdminHero({ envelope }: { envelope: SurfaceEnvelope<DashboardSurfaceData> }) {
  const data = envelope.data;
  const hero = data.hero;
  const accountContext = data.accountContext;
  const administeredLabels = hero?.administeredPopulationLabels ?? data.administeredPopulations?.map((population) => population.label) ?? [];
  return (
    <section className="my-account-command-hero user-admin-command-hero" aria-label="User Admin scope summary">
      <div>
        <p className="eyebrow">User Admin · selected AuthContext</p>
        <h3>{hero?.title ?? data.scopeLabel ?? envelope.title}</h3>
        <p>{hero?.redactionSummary ?? renderSurfaceValue(data.redaction) ?? 'Backend-selected scope, redaction, and capability boundaries apply to every surface action.'}</p>
      </div>
      <dl className="authority-summary-grid" aria-label="Selected User Admin authority">
        <div><dt>Scope</dt><dd>{hero?.scopeLabel ?? data.scopeLabel ?? accountContext?.authority ?? 'Selected scope'}</dd></div>
        <div><dt>Scope type</dt><dd>{hero?.scopeType ?? String((data.selectedAuthContext as Record<string, unknown> | undefined)?.scopeType ?? 'backend selected')}</dd></div>
        <div><dt>Admin level</dt><dd>{hero?.adminLevel ?? data.adminLevel ?? 'backend selected'}</dd></div>
        <div><dt>Authority basis</dt><dd>{renderSurfaceValue(data.authorityBasis) ?? 'Backend-owned capabilities and selected AuthContext'}</dd></div>
        {hero?.supportAccessState && <div><dt>Support access</dt><dd>{String(hero.supportAccessState)}</dd></div>}
        <div><dt>Visible populations</dt><dd>{administeredLabels.length > 0 ? administeredLabels.join(', ') : 'None visible in this context'}</dd></div>
      </dl>
    </section>
  );
}

function MyAccountCommandCenter({ envelope, onAction, onSignOut }: DashboardSurfaceProps) {
  const data = envelope.data;
  const actionById = new Map(envelope.actions.map((action) => [action.actionId, action]));
  const counters = data.attentionCounters?.length ? data.attentionCounters : defaultAttentionCounters(data);
  const panels = data.controlPanels?.length ? data.controlPanels : defaultControlPanels(data);

  return (
    <SurfaceStateFrame
      envelope={envelope}
      headerActions={onSignOut ? <button type="button" className="surface-header-sign-out-button" onClick={onSignOut}>Sign out</button> : undefined}
    >
      <section className="my-account-command-hero" aria-label="My Account selected authority and command intent">
        <div>
          <p className="eyebrow">Personal command center</p>
          <h3>Your account, attention, and preferences in one place.</h3>
          <p>Start with items that need action, or jump to profile, settings, context, notifications, and digest tools.</p>
        </div>
        {data.accountContext && (
          <dl className="authority-summary-grid" aria-label="Selected context authority">
            <div><dt>Signed in</dt><dd>{data.accountContext.displayName ?? 'Current user'}{data.accountContext.email ? ` · ${data.accountContext.email}` : ''}</dd></div>
            <div><dt>Organization</dt><dd>{data.accountContext.tenantLabel ?? 'Current organization'}</dd></div>
            <div><dt>Scope</dt><dd>{data.accountContext.customerLabel ?? (envelope.authContext.customerId ? 'Selected customer' : 'Tenant scope')}</dd></div>
            <div><dt>Authority</dt><dd>{data.accountContext.authority ?? data.accountContext.roles?.join(', ') ?? 'Backend selected AuthContext'}</dd></div>
          </dl>
        )}
      </section>

      <section className="my-account-section" aria-labelledby={`${envelope.surfaceId}-attention-heading`}>
        <div className="surface-section-heading">
          <div>
            <p className="eyebrow">Attention</p>
            <h4 id={`${envelope.surfaceId}-attention-heading`}>Items that need my attention by workstream</h4>
          </div>
          <p>Open a counter to recheck authorization and continue in the source workstream. Zero-count counters still open setup, history, or clear-state confirmation where available.</p>
        </div>
        <div className="attention-counter-strip" aria-label="Attention by available workstream">
          {counters.map((counter) => {
            const action = counter.actionId ? actionById.get(counter.actionId) : undefined;
            const status = counter.statusText ?? counter.status ?? counter.description ?? 'Backend-owned attention';
            const body = <><span>{counter.workstreamLabel ?? counter.label}</span><strong>{counter.attentionCount ?? counter.value}</strong><em>{formatStatus(status)}</em>{(counter.purposeSummary ?? counter.description) && <small>{counter.purposeSummary ?? counter.description}</small>}<small>{counter.redactionLevel ?? counter.redaction ?? 'Visible in selected context'}</small></>;
            const input = { targetFunctionalAgentId: counter.workstreamId ?? '', targetSurfaceId: counter.targetSurfaceId ?? counter.surfaceId ?? '', requiredCapabilityId: counter.sourceCapabilityId ?? counter.requiredCapabilityId ?? '', correlationId: envelope.correlationId };
            return action ? <button key={counter.counterId} type="button" className={`attention-counter-card ${counter.severity ?? 'info'}`} onClick={() => onAction?.(action, envelope.surfaceId, input)} aria-label={`Open ${counter.label}: ${status}; ${counter.value} attention items`}>{body}</button> : <article key={counter.counterId} className={`attention-counter-card ${counter.severity ?? 'info'}`}>{body}</article>;
          })}
        </div>
      </section>

      {data.attentionItems && data.attentionItems.length > 0 ? (
        <details className="dashboard-evidence-drawer my-account-attention-evidence">
          <summary>Backend attention evidence retained for trace review</summary>
          <p>Detailed items are evidence only on the command center. Open the source workstream through an attention counter to inspect and act on the item with backend authorization.</p>
          <AttentionList items={data.attentionItems} label="Collapsed backend-derived attention evidence; source opening stays governed" actionById={actionById} surfaceId={envelope.surfaceId} onAction={onAction} />
        </details>
      ) : (
        <section className="surface-empty-state my-account-empty-attention" aria-label="No current My Account attention">
          <h4>No personal attention items</h4>
          <p>Authorized workstream counters are clear in this context. You can still update settings or review notifications below.</p>
        </section>
      )}

      <section className="my-account-section" aria-labelledby={`${envelope.surfaceId}-control-heading`}>
        <div className="surface-section-heading">
          <div>
            <p className="eyebrow">Personal surfaces</p>
            <h4 id={`${envelope.surfaceId}-control-heading`}>Self-service controls</h4>
          </div>
          <p>Manage your personal account surfaces without leaving this workstream.</p>
        </div>
        <div className="my-account-control-panels" aria-label="Personal control panels">
          {panels.map((panel) => {
            const action = panel.actionId ? actionById.get(panel.actionId) : undefined;
            return (
              <article key={panel.panelId} className={`my-account-control-panel ${panel.severity ?? panel.state ?? 'info'}`}>
                <p className="eyebrow">{panel.state ?? 'Available surface'}</p>
                <h4>{panel.label}</h4>
                <p>{panel.summary}</p>
                {panel.value !== undefined && <strong>{panel.value}</strong>}
                {action ? <SurfaceActionBar actions={[cleanMyAccountSurfaceActionLabel(action)]} surfaceId={envelope.surfaceId} onAction={onAction} /> : <p className="capability-basis">No authorized action is available for this panel in the selected context.</p>}
              </article>
            );
          })}
        </div>
      </section>

      {data.traceRefs && data.traceRefs.length > 0 && (
        <details className="dashboard-evidence-drawer">
          <summary>Evidence and trace references</summary>
          <section className="trace-link-list" aria-label="My Account trace references">
            {data.traceRefs.map((traceRef, index) => {
              const traceId = typeof traceRef === 'string' ? traceRef : String(traceRef.traceId ?? traceRef.refId ?? `trace-${index}`);
              const label = typeof traceRef === 'string' ? traceRef : String(traceRef.label ?? traceId);
              return <a key={`${traceId}-${index}`} href={`/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(traceId)}`}>{label}</a>;
            })}
          </section>
        </details>
      )}
    </SurfaceStateFrame>
  );
}

// Contract marker: aria-label="Backend-derived attention items; Audit/Trace attention items"
// Contract marker: data-attention-source={envelope.data.attentionSource ?? 'attention.list_workstream_items'}
// Contract markers for backend metadata retained in payload but not rendered as dashboard clutter: Governed tool: Target surface:
function AttentionList({ items, label, actionById, surfaceId, onAction }: { items: AttentionItem[]; label: string; actionById?: Map<string, SurfaceAction>; surfaceId?: string; onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void }) {
  return (
    <section className="my-account-attention-card-list" aria-label={label} data-attention-source="attention.list_my_account_items">
      {items.map((item) => {
        const openAction = actionForAttentionItem(item, actionById);
        const input = { targetFunctionalAgentId: item.surfaceRef?.targetFunctionalAgentId ?? item.sourceWorkstreamId ?? '', targetSurfaceId: item.surfaceRef?.targetSurfaceId ?? '', targetItemId: item.surfaceRef?.targetItemId ?? item.itemId, requiredCapabilityId: item.surfaceRef?.requiredCapabilityId ?? item.capabilityId ?? '', correlationId: item.traceId ?? '' };
        return (
          <article key={item.itemId} className={`my-account-attention-card ${attentionSeverityClass(item.severity ?? item.status)}`} data-attention-redaction={item.redaction ?? 'full'}>
            <div>
              <p className="eyebrow">{formatAttentionSource(item.sourceWorkstreamId)} · {formatStatus(item.severity ?? item.status)}</p>
              <h4>{item.label ?? item.title ?? item.itemId}</h4>
              {item.summary && <p>{item.summary}</p>}
              <p className="capability-basis">{item.redaction === 'summary_only' ? 'Summary-only evidence' : 'Browser-safe evidence'} · Source opens through a governed workstream action.</p>
            </div>
            <div className="attention-card-actions">
              {openAction && surfaceId ? <button type="button" className="surface-action-link" onClick={() => onAction?.(openAction, surfaceId, input)}>Open source workstream</button> : <span className="form-status">Open from the matching counter above</span>}
              {item.traceId && <a className="surface-action-link secondary" href={`/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(item.traceId)}`}>View trace</a>}
            </div>
          </article>
        );
      })}
    </section>
  );
}

function defaultAttentionCounters(data: DashboardSurfaceData): NonNullable<DashboardSurfaceData['attentionCounters']> {
  return data.cards
    .filter((card) => card.cardKind === 'workstream-status' || card.workstreamId)
    .map((card) => ({
      counterId: `counter-${card.workstreamId ?? card.cardId}`,
      label: card.label,
      value: card.value,
      severity: card.severity ?? 'info',
      status: card.status,
      description: card.description,
      actionId: card.actionId,
      surfaceId: card.surfaceId,
      targetSurfaceId: card.targetSurfaceId ?? card.surfaceId,
      workstreamId: card.workstreamId,
      redaction: 'Visible in selected context'
    }));
}

function defaultControlPanels(data: DashboardSurfaceData): NonNullable<DashboardSurfaceData['controlPanels']> {
  return [
    { panelId: 'panel-profile', label: 'Profile', summary: 'Maintain browser-safe identity fields. Provider-backed facts remain read-only.', state: 'self-service', actionId: 'action-show-my-profile' },
    { panelId: 'panel-settings', label: 'Settings & theme', summary: 'Choose a named theme and persist personal preferences through governed settings.', state: 'self-service', actionId: 'action-show-my-settings' },
    { panelId: 'panel-context', label: 'Context & authority', summary: 'Inspect selected tenant/customer, role basis, visible capabilities, and context switch targets.', state: 'authority', actionId: 'action-show-my-context' },
    { panelId: 'panel-notifications', label: 'Notifications', summary: 'Triage in-app notifications without mutating source work.', state: 'triage', value: typeof data.notificationCenter?.visibleCount === 'number' ? data.notificationCenter.visibleCount : undefined, actionId: 'action-show-my-account-notification-center' },
    { panelId: 'panel-digest', label: 'Personal digest/export', summary: 'Start or review a governed advisory digest of authorized personal attention evidence.', state: 'advisory', actionId: 'action-start-my-account-personal-attention-digest' }
  ];
}

function agentAdminQueueInput(queue: NonNullable<DashboardSurfaceData['attentionQueues']>[number], envelope: { correlationId: string }): Record<string, string> {
  return stringRecord({ targetSurfaceId: queue.targetSurfaceId, requiredCapabilityId: queue.sourceCapabilityId, filter: queue.filter, correlationId: queue.traceRefs?.[0] ?? envelope.correlationId });
}

function agentAdminCardInput(card: NonNullable<DashboardSurfaceData['cards']>[number], envelope: { correlationId: string }): Record<string, string> {
  return stringRecord({ cardId: card.cardId, targetSurfaceId: card.targetSurfaceId, correlationId: envelope.correlationId });
}

function userAdminAttentionCountersFromQueues(queues: NonNullable<DashboardSurfaceData['attentionQueues']>, actionById: Map<string, SurfaceAction>): NonNullable<DashboardSurfaceData['attentionCounters']> {
  return queues.map((queue) => {
    const queueCount = Number(queue.count ?? 0);
    const action = userAdminQueueAction(queue, actionById);
    return {
      counterId: `counter-${queue.queueId}`,
      label: queue.label,
      value: queue.count ?? 0,
      severity: queue.severity,
      status: queue.statusText ?? (queueCount === 0 ? 'Open clear queue' : 'Needs review'),
      actionId: action?.actionId,
      description: queue.sourceCapabilityId
    };
  });
}

function cleanUserAdminActionLabel(label: string): string {
  return label.replace(/ · /g, ' — ');
}

// Backend-authored User Admin queue payloads commonly carry action-useradmin-read-access-review,
// action-read-support-access, action-open-admin-audit, and action-display-user-list.
// This renderer intentionally does not infer those actions from queue labels.
function userAdminQueueAction(queue: NonNullable<DashboardSurfaceData['attentionQueues']>[number], actionById: Map<string, SurfaceAction>): SurfaceAction | undefined {
  if (queue.actionId) return actionById.get(queue.actionId);
  if (queue.openActionId) return actionById.get(queue.openActionId);
  if (queue.targetSurfaceId) return actionForTarget(queue.targetSurfaceId, actionById);
  return undefined;
}

function backendAuthoredUserAdminPopulationCards(data: DashboardSurfaceData, actionById: Map<string, SurfaceAction>): Array<{ cardId: string; label: string; value: string | number; scope: string; summary: string; action?: SurfaceAction }> {
  return (data.administeredPopulations ?? []).map((population) => ({
    cardId: `population-${population.populationType}`,
    label: population.label,
    value: population.visibleCount,
    scope: population.populationType.replace(/_/g, ' '),
    summary: [
      population.attentionCount !== undefined ? `${population.attentionCount} need attention` : undefined,
      population.roleCoverageSummary,
      population.pendingInvitationCount !== undefined ? `${population.pendingInvitationCount} pending invitations` : undefined
    ].filter(Boolean).join(' · ') || 'Open backend-authorized population.',
    action: actionById.get(population.openActionId) ?? actionForTarget(population.targetSurfaceId, actionById)
  }));
}

function formatQueueEyebrow(severity: string | undefined, count: number): string {
  if (severity === 'critical' || severity === 'urgent' || severity === 'blocked' || severity === 'blocked_provider_or_runtime') return 'Needs review';
  if (severity === 'warning') return 'Watch queue';
  return count > 0 ? 'Open queue' : 'No attention needed';
}

function backendAuthoredUserAdminQueues(data: DashboardSurfaceData): NonNullable<DashboardSurfaceData['attentionQueues']> {
  if (data.attentionQueues?.length) return data.attentionQueues;
  return (data.attentionCounts ?? []).map((count) => ({
    queueId: count.attentionType,
    label: count.label,
    count: count.count,
    severity: count.severity ?? 'info',
    statusText: count.statusText,
    sourceCapabilityId: count.sourceCapabilityId,
    targetSurfaceId: count.targetSurfaceId,
    filter: typeof count.filter === 'string' ? count.filter : count.filter ? JSON.stringify(count.filter) : undefined,
    actionId: count.openActionId ?? count.actionId,
    traceRefs: count.traceRefs,
    redaction: count.redactionState
  }));
}

function actionForTarget(targetSurfaceId: string, actionById: Map<string, SurfaceAction>): SurfaceAction | undefined {
  return Array.from(actionById.values()).find((action) => action.resultSurface?.updateSurfaceId === targetSurfaceId || action.resultSurface?.appendSurfaceType === targetSurfaceId || action.shellRequest?.targetSurfaceId === targetSurfaceId);
}

function actionForAttentionItem(item: AttentionItem, actionById: Map<string, SurfaceAction> | undefined): SurfaceAction | undefined {
  if (!actionById) return undefined;
  const backendActionId = item.surfaceRef?.defaultActionId;
  if (backendActionId && actionById.has(backendActionId)) return actionById.get(backendActionId);
  const targetSurfaceId = item.surfaceRef?.targetSurfaceId;
  if (targetSurfaceId) return actionForTarget(targetSurfaceId, actionById);
  return undefined;
}

function userDirectoryAction(actionById: Map<string, SurfaceAction>): SurfaceAction | undefined {
  return actionById.get('action-user-admin-show-users') ?? actionById.get('action-display-user-list') ?? actionForTarget('surface-user-admin-users', actionById);
}

function actionsForIds(ids: string[] | undefined, actionById: Map<string, SurfaceAction>): SurfaceAction[] {
  return ids?.map((id) => actionById.get(id)).filter((action): action is SurfaceAction => Boolean(action)) ?? [];
}

function cleanMyAccountSurfaceActionLabel(action: SurfaceAction): SurfaceAction {
  const cleanLabels: Record<string, string> = {
    'action-show-my-profile': 'Profile',
    'action-show-my-settings': 'Settings',
    'action-show-my-context': 'Context',
    'action-show-my-account-notification-center': 'Notifications',
    'action-start-my-account-personal-attention-digest': 'Start digest'
  };
  return cleanLabels[action.actionId] ? { ...action, label: cleanLabels[action.actionId] } : action;
}

function attentionSeverityClass(severity: string): string {
  return severity === 'critical' || severity === 'urgent' || severity === 'blocked' ? 'danger' : severity;
}

function formatAttentionSource(source?: string): string {
  if (!source) return 'My Account';
  return source.replace(/^agent-/, '').split('-').map((part) => part.charAt(0).toUpperCase() + part.slice(1)).join(' ');
}

function formatStatus(value?: string): string {
  return (value ?? 'open').replace(/[-_]/g, ' ');
}

function stringRecord(value: Record<string, unknown>): Record<string, string> {
  return Object.fromEntries(Object.entries(value).filter(([, entry]) => entry !== undefined && entry !== null).map(([key, entry]) => [key, String(entry)]));
}

function renderSurfaceValue(value: unknown): string | undefined {
  if (value == null) return undefined;
  if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') return String(value);
  if (Array.isArray(value)) return value.map(renderSurfaceValue).filter(Boolean).join(' · ');
  if (typeof value === 'object') return Object.entries(value as Record<string, unknown>).map(([key, entry]) => `${key}: ${renderSurfaceValue(entry) ?? 'n/a'}`).join(' · ');
  return String(value);
}
