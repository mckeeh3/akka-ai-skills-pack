import { useMemo, useRef, useState } from 'react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import type { AgentAdminDocumentDetail, AgentAdminReferenceDocSummary, AgentAdminRuntimeTraceRow, AgentAdminSkillDocSummary, AgentAdminSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type Props = {
  envelope: SurfaceEnvelope<AgentAdminSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

type AgentRow = {
  agentDefinitionId?: string;
  agentName?: string;
  shortPurpose?: string;
  purpose?: string;
  workstreamDomain?: string;
  lastEditTime?: string;
  actionId?: string;
  targetSurfaceId?: string;
};

type VersionRow = {
  version?: number;
  currentVersion?: boolean;
  createdAt?: string;
  label?: string;
};

type EditSessionInstruction = {
  actorAccountId?: string;
  instructions?: string;
  createdAt?: string;
};

type EditSessionView = {
  sessionId?: string;
  agentDefinitionId?: string;
  kind?: string;
  documentId?: string;
  baseVersion?: number;
  status?: string;
  instructions?: EditSessionInstruction[];
  proposedContent?: string;
  changeSummary?: string;
  clarifyingQuestion?: string | null;
  warnings?: string[];
  startedAt?: string;
  endedAt?: string | null;
};

type FieldError = { field: string; message: string };
type FormError = { message: string; fields: FieldError[] };

const agentAdminDocEditingContracts = new Set([
  'agent_admin.blank.v1',
  'agent_admin.dashboard.v1',
  'agent_admin.agent_list.v1',
  'agent_admin.agent_detail.v1',
  'agent_admin.agent_profile_history.v1',
  'agent_admin.prompt_doc.v1',
  'agent_admin.skill_library.v1',
  'agent_admin.skill_doc.v1',
  'agent_admin.skill_assignment.v1',
  'agent_admin.tool_assignment.v1',
  'agent_admin.model_config_ref.v1',
  'agent_admin.skill_reference_doc.v1',
  'agent_admin.edit_session.v1',
  'agent_admin.proposal_review.v1',
  'agent_admin.version_history.v1',
  'agent_admin.version_diff.v1',
  'agent_admin.create_skill.v1',
  'agent_admin.delete_skill_confirmation.v1',
  'agent_admin.create_reference_doc.v1',
  'agent_admin.delete_reference_doc_confirmation.v1',
  'agent_admin.runtime_traces.v1',
  'agent_admin.system_message.v1'
]);

export function AgentAdminDocEditingSurface({ envelope, onAction }: Props) {
  const contract = envelope.data.surfaceContract;
  switch (contract) {
    case 'agent_admin.blank.v1':
      return <AgentAdminBlankSurface envelope={envelope} onAction={onAction} />;
    case 'agent_admin.dashboard.v1':
      return <AgentAdminDashboardSurface envelope={envelope} onAction={onAction} />;
    case 'agent_admin.agent_list.v1':
      return <AgentAdminAgentListSurface envelope={envelope} onAction={onAction} />;
    case 'agent_admin.agent_detail.v1':
      return <AgentAdminAgentDetailSurface envelope={envelope} onAction={onAction} />;
    case 'agent_admin.agent_profile_history.v1':
      return <AgentAdminProfileHistorySurface envelope={envelope} onAction={onAction} />;
    case 'agent_admin.skill_library.v1':
      return <AgentAdminSkillLibrarySurface envelope={envelope} onAction={onAction} />;
    case 'agent_admin.skill_assignment.v1':
    case 'agent_admin.tool_assignment.v1':
    case 'agent_admin.model_config_ref.v1':
      return <AgentAdminAssignmentSurface envelope={envelope} onAction={onAction} />;
    case 'agent_admin.prompt_doc.v1':
    case 'agent_admin.skill_doc.v1':
    case 'agent_admin.skill_reference_doc.v1':
      return <AgentAdminDocumentSurface envelope={envelope} onAction={onAction} />;
    case 'agent_admin.edit_session.v1':
      return <AgentAdminEditSessionSurface envelope={envelope} onAction={onAction} />;
    case 'agent_admin.proposal_review.v1':
      return <AgentAdminProposalReviewSurface envelope={envelope} onAction={onAction} />;
    case 'agent_admin.version_history.v1':
      return <AgentAdminVersionHistorySurface envelope={envelope} onAction={onAction} />;
    case 'agent_admin.version_diff.v1':
      return <AgentAdminVersionDiffSurface envelope={envelope} onAction={onAction} />;
    case 'agent_admin.create_skill.v1':
      return <AgentAdminCreateSkillSurface envelope={envelope} onAction={onAction} />;
    case 'agent_admin.delete_skill_confirmation.v1':
      return <AgentAdminDeleteSkillSurface envelope={envelope} onAction={onAction} />;
    case 'agent_admin.create_reference_doc.v1':
      return <AgentAdminCreateReferenceDocSurface envelope={envelope} onAction={onAction} />;
    case 'agent_admin.delete_reference_doc_confirmation.v1':
      return <AgentAdminDeleteReferenceDocSurface envelope={envelope} onAction={onAction} />;
    case 'agent_admin.runtime_traces.v1':
      return <AgentAdminRuntimeTracesSurface envelope={envelope} onAction={onAction} />;
    default:
      return null;
  }
}

export function isAgentAdminDocEditingSurface(envelope: SurfaceEnvelope<unknown>) {
  const contract = (envelope.data as { surfaceContract?: string } | undefined)?.surfaceContract;
  return Boolean(contract && agentAdminDocEditingContracts.has(contract));
}

function AgentAdminBlankSurface({ envelope, onAction }: Props) {
  const clearState = envelope.data.clearWorkstream;
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="agent-admin-doc-surface agent-admin-blank" aria-labelledby={`${envelope.surfaceId}-blank-heading`}>
        <p className="eyebrow">Agent Admin · no surface selected</p>
        <h4 id={`${envelope.surfaceId}-blank-heading`}>Choose how to begin</h4>
        <p className="surface-empty-copy">{envelope.data.emptyCopy ?? 'Show the dashboard, show agents, or use the composer to find an agent document to improve.'}</p>
        {envelope.data.composerAvailable && <p className="surface-state-inline" role="status">Composer is available for SaaS-admin doc-editing requests.</p>}
        {clearState && <p className="surface-state-inline no-op" role="status">Clear workstream: {clearState.enabled ? 'available' : clearState.state}</p>}
        <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
      </section>
    </SurfaceStateFrame>
  );
}

function AgentAdminDashboardSurface({ envelope, onAction }: Props) {
  const actions = actionMap(envelope.actions);
  const cards = envelope.data.thingsYouCanDo ?? [];
  const recentAgents = (envelope.data.recentlyChangedAgents ?? []).slice(0, 5).map(asAgentRow);
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="agent-admin-doc-surface agent-admin-dashboard" aria-label="Agent Admin document editing dashboard">
        <div className="surface-section-heading">
          <div>
            <p className="eyebrow">Agent Admin · things you can do</p>
            <h4>Open agent documents</h4>
          </div>
          <p>Dashboard objects are action routers. Open the agent list or a recently changed agent to inspect prompt, skill, and reference docs.</p>
        </div>
        <div className="surface-dashboard-grid my-account-workstream-grid" aria-label="Agent Admin actions">
          {cards.map((card) => {
            const action = actions.get(card.actionId);
            const body = <><p>{card.label}</p><strong>{card.count}</strong><span>Open {card.targetSurfaceId.replace('surface-agent-admin-', '').replace(/-/g, ' ')}</span></>;
            return action ? (
              <button key={card.cardId} type="button" className="ds-card dashboard-card clickable info" disabled={Boolean(action.disabled)} onClick={() => !action.disabled && onAction?.(action, envelope.surfaceId, stringRecord({ targetSurfaceId: card.targetSurfaceId, cardId: card.cardId }))} aria-label={`Open ${card.label}: ${card.count} agents`}>
                {body}
              </button>
            ) : <article key={card.cardId} className="ds-card dashboard-card info">{body}</article>;
          })}
        </div>
        <section className="user-admin-section" aria-labelledby={`${envelope.surfaceId}-recent-heading`}>
          <div className="surface-section-heading compact"><div><p className="eyebrow">Recently changed</p><h4 id={`${envelope.surfaceId}-recent-heading`}>Top five recently changed agents</h4></div><p>Rows open agent detail through backend authorization.</p></div>
          {recentAgents.length === 0 ? <p className="surface-empty-copy">No agents have recent document changes.</p> : (
            <div className="user-admin-clean-list agent-admin-doc-list" role="list" aria-label="Recently changed agents">
              {recentAgents.map((agent) => <AgentRowButton key={agent.agentDefinitionId ?? agent.agentName} agent={agent} actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />)}
            </div>
          )}
        </section>
        {envelope.data.thingsNeedAttention && envelope.data.thingsNeedAttention.length === 0 && <p className="surface-state-inline" role="status">No Agent Admin attention queue is defined for this workstream.</p>}
      </section>
    </SurfaceStateFrame>
  );
}

function AgentAdminAgentListSurface({ envelope, onAction }: Props) {
  const rows = (envelope.data.rows ?? []).map(asAgentRow);
  const initialName = String(envelope.data.filters?.agentName ?? '');
  const initialDomain = String(envelope.data.filters?.workstreamOrDomain ?? envelope.data.filters?.workstreamDomain ?? '');
  const [agentName, setAgentName] = useState(initialName);
  const [workstreamOrDomain, setWorkstreamOrDomain] = useState(initialDomain);
  const listAction = envelope.actions.find((action) => action.actionId === 'action-agent-admin-list-agents' || action.actionId === 'action-agent-admin-show-agents');
  const filteredRows = useMemo(() => rows.filter((row) => {
    const nameMatch = !agentName || String(row.agentName ?? '').toLowerCase().includes(agentName.toLowerCase());
    const domainMatch = !workstreamOrDomain || String(row.workstreamDomain ?? '').toLowerCase().includes(workstreamOrDomain.toLowerCase());
    return nameMatch && domainMatch;
  }), [rows, agentName, workstreamOrDomain]);
  const emptyCopy = rows.length === 0 ? 'No agents are available in this SaaS-admin context.' : 'No agents match the current filters.';
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="agent-admin-doc-surface agent-admin-agent-list" aria-labelledby={`${envelope.surfaceId}-list-heading`}>
        <div className="surface-section-heading">
          <div><p className="eyebrow">Agent Admin · browse</p><h4 id={`${envelope.surfaceId}-list-heading`}>Find an agent to improve</h4></div>
          <p>{filteredRows.length} of {rows.length} visible agents shown. Backend authorization still controls row visibility and opening.</p>
        </div>
        <form className="surface-search-form user-admin-clean-search" role="search" onSubmit={(event) => { event.preventDefault(); if (listAction) onAction?.(listAction, envelope.surfaceId, stringRecord({ agentName, workstreamOrDomain })); }}>
          <label htmlFor={`${envelope.surfaceId}-agent-name`}>Agent name</label>
          <input className="designed-control surface-search-control" id={`${envelope.surfaceId}-agent-name`} name="agentName" value={agentName} onChange={(event) => setAgentName(event.currentTarget.value)} />
          <label htmlFor={`${envelope.surfaceId}-domain`}>Workstream or domain</label>
          <input className="designed-control surface-search-control" id={`${envelope.surfaceId}-domain`} name="workstreamOrDomain" value={workstreamOrDomain} onChange={(event) => setWorkstreamOrDomain(event.currentTarget.value)} />
          <button type="submit" className="surface-action-link secondary" disabled={!listAction}>Apply filters</button>
        </form>
        {filteredRows.length === 0 ? <p className="surface-empty-copy">{emptyCopy}</p> : (
          <div className="user-admin-clean-list agent-admin-doc-list" role="list" aria-label="Agent rows">
            {filteredRows.map((agent) => <AgentRowButton key={agent.agentDefinitionId ?? agent.agentName} agent={agent} actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />)}
          </div>
        )}
        <SurfaceActionBar actions={envelope.actions.filter((action) => action.actionId !== listAction?.actionId && action.actionId !== 'action-agent-admin-open-agent-detail')} surfaceId={envelope.surfaceId} onAction={onAction} />
      </section>
    </SurfaceStateFrame>
  );
}

function AgentAdminAgentDetailSurface({ envelope, onAction }: Props) {
  const agent = envelope.data.agent;
  const promptAction = actionById(envelope.actions, 'action-agent-admin-open-prompt-doc');
  const runtimeAction = actionById(envelope.actions, 'action-agent-admin-open-runtime-traces');
  const skills = envelope.data.skills ?? [];
  const profile = envelope.data.profile;
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="agent-admin-doc-surface agent-admin-agent-detail" aria-labelledby={`${envelope.surfaceId}-detail-heading`}>
        <div className="surface-section-heading">
          <div><p className="eyebrow">Agent Admin · generated identity read-only</p><h4 id={`${envelope.surfaceId}-detail-heading`}>{agent?.agentName ?? envelope.title}</h4></div>
          <p>Generated agent identity, lifecycle, placement, and generated tool code are read-only. Use behavior-profile proposal and assignment surfaces for runtime behavior changes.</p>
        </div>
        <dl className="authority-summary-grid" aria-label="Generated agent and behavior profile summary">
          <div><dt>Purpose</dt><dd>{agent?.purpose ?? 'No purpose provided'}</dd></div>
          <div><dt>Placement</dt><dd>{agent?.workstreamDomain ?? 'placement unavailable'}</dd></div>
          <div><dt>Profile scope</dt><dd>{profile?.scopeProvenance ?? profile?.scope ?? 'global defaults until tenant-specific change'}</dd></div>
          <div><dt>Safe model alias</dt><dd>{profile?.safeModelAlias ?? envelope.data.safeModelAlias ?? 'model alias unavailable'}</dd></div>
          <div><dt>Prompt version</dt><dd>{profile?.activePromptVersion ?? envelope.data.prompt?.currentVersion ?? 'unavailable'}</dd></div>
          <div><dt>Generated identity</dt><dd>Read-only app-description/code-generated default</dd></div>
        </dl>
        <section className="user-admin-section" aria-labelledby={`${envelope.surfaceId}-docs-heading`}>
          <div className="surface-section-heading compact"><div><p className="eyebrow">Behavior profile</p><h4 id={`${envelope.surfaceId}-docs-heading`}>Prompt, skills, references, and assignments</h4></div><p>Rows open separate backend-authorized surfaces; changes save proposals or create behavior-profile versions after activation.</p></div>
          <div className="user-admin-clean-list agent-admin-doc-list" role="list" aria-label="Agent behavior documents and assignments">
            {envelope.data.prompt && <DocumentSummaryButton summary={envelope.data.prompt} action={promptAction} agentDefinitionId={agent?.agentDefinitionId} surfaceId={envelope.surfaceId} onAction={onAction} />}
            {skills.map((skill) => <SkillSummary key={skill.documentId} skill={skill} actions={envelope.actions} agentDefinitionId={agent?.agentDefinitionId} surfaceId={envelope.surfaceId} onAction={onAction} />)}
          </div>
        </section>
        {Array.isArray(envelope.data.allowedGeneratedTools) && envelope.data.allowedGeneratedTools.length > 0 && <p className="surface-state-inline" role="status">Allowed generated tools are assignment metadata only; no action on this surface creates, edits, or deletes generated tool code.</p>}
        {runtimeAction && <button type="button" className="surface-action-link secondary" onClick={() => onAction?.(runtimeAction, envelope.surfaceId, stringRecord({ agentDefinitionId: agent?.agentDefinitionId }))}>Runtime reads</button>}
        <SurfaceActionBar actions={envelope.actions.filter((action) => !['action-agent-admin-open-prompt-doc', 'action-agent-admin-open-skill-doc', 'action-agent-admin-open-reference-doc', 'action-agent-admin-open-create-reference-doc', 'action-agent-admin-open-delete-reference-doc', 'action-agent-admin-open-delete-skill', 'action-agent-admin-open-runtime-traces'].includes(action.actionId))} surfaceId={envelope.surfaceId} onAction={onAction} />
      </section>
    </SurfaceStateFrame>
  );
}

function AgentAdminDocumentSurface({ envelope, onAction }: Props) {
  const doc = envelope.data.doc;
  const [instructions, setInstructions] = useState('');
  if (!doc) {
    return <SurfaceStateFrame envelope={envelope}><p className="surface-empty-copy">Document content is unavailable or redacted.</p></SurfaceStateFrame>;
  }
  const isCurrent = doc.currentVersion && doc.editable && envelope.data.editInputEnabled === true;
  const editAction = actionById(envelope.actions, 'action-agent-doc-edit-start');
  const historyAction = actionById(envelope.actions, 'action-agent-doc-version-history');
  const diffAction = actionById(envelope.actions, 'action-agent-doc-version-diff');
  const restoreAction = !doc.currentVersion ? actionById(envelope.actions, 'action-agent-doc-version-restore') : undefined;
  const runtimeAction = actionById(envelope.actions, 'action-agent-admin-open-runtime-traces');
  const deleteSkillAction = doc.kind === 'skill' ? actionById(envelope.actions, 'action-agent-admin-open-delete-skill') : undefined;
  const deleteReferenceAction = doc.kind === 'reference' ? actionById(envelope.actions, 'action-agent-admin-open-delete-reference-doc') : undefined;
  const createReferenceAction = doc.kind === 'skill' ? actionById(envelope.actions, 'action-agent-admin-open-create-reference-doc') : undefined;
  return (
    <SurfaceStateFrame envelope={envelope}>
      <article className="agent-admin-doc-surface agent-admin-document" aria-labelledby={`${envelope.surfaceId}-doc-heading`}>
        {!isCurrent && <p className="surface-state-inline forbidden" role="status">{envelope.data.readOnlyBanner ?? 'Historical version: read-only.'}</p>}
        <div className="surface-section-heading">
          <div><p className="eyebrow">Agent Admin · {doc.kind} document</p><h4 id={`${envelope.surfaceId}-doc-heading`}>{doc.title}</h4></div>
          <p>{doc.description}</p>
        </div>
        <DocMetadata doc={doc} />
        {envelope.data.referenceDocs && envelope.data.referenceDocs.length > 0 && <ReferenceDocLinks referenceDocs={envelope.data.referenceDocs} actions={envelope.actions} agentDefinitionId={doc.agentDefinitionId} skillDocumentId={doc.documentId} surfaceId={envelope.surfaceId} onAction={onAction} />}
        <section className="agent-admin-markdown-document" aria-labelledby={`${envelope.surfaceId}-content-heading`}>
          <h5 id={`${envelope.surfaceId}-content-heading`}>Current Markdown content</h5>
          <div className="markdown-response-content"><ReactMarkdown remarkPlugins={[remarkGfm]} skipHtml>{doc.contentBody}</ReactMarkdown></div>
        </section>
        <section className="surface-section-list" aria-label="Document actions">
          <div className="surface-action-bar">
            {historyAction && <button type="button" onClick={() => onAction?.(historyAction, envelope.surfaceId, documentInput(doc))}>{historyAction.label}</button>}
            {diffAction && <button type="button" onClick={() => onAction?.(diffAction, envelope.surfaceId, documentInput(doc))}>{diffAction.label}</button>}
            {runtimeAction && <button type="button" onClick={() => onAction?.(runtimeAction, envelope.surfaceId, documentInput(doc))}>{runtimeAction.label}</button>}
            {createReferenceAction && <button type="button" onClick={() => onAction?.(createReferenceAction, envelope.surfaceId, stringRecord({ agentDefinitionId: doc.agentDefinitionId, skillDocumentId: doc.documentId }))}>{createReferenceAction.label}</button>}
            {deleteSkillAction && <button type="button" onClick={() => onAction?.(deleteSkillAction, envelope.surfaceId, stringRecord({ agentDefinitionId: doc.agentDefinitionId, skillDocumentId: doc.documentId }))}>{deleteSkillAction.label}</button>}
            {deleteReferenceAction && <button type="button" onClick={() => onAction?.(deleteReferenceAction, envelope.surfaceId, stringRecord({ agentDefinitionId: doc.agentDefinitionId, referenceDocumentId: doc.documentId, documentId: doc.documentId }))}>{deleteReferenceAction.label}</button>}
            {restoreAction && <button type="button" onClick={() => onAction?.(restoreAction, envelope.surfaceId, documentInput(doc))}>{restoreAction.label}{restoreAction.requiresConfirmation ? ' · confirm' : ''}</button>}
          </div>
        </section>
        <section className="detail-edit-form-section" aria-labelledby={`${envelope.surfaceId}-improve-heading`}>
          <div className="surface-section-heading compact"><div><p className="eyebrow">Improve behavior</p><h4 id={`${envelope.surfaceId}-improve-heading`}>Free-form edit instructions</h4></div><p>{isCurrent ? 'Instructions are reviewed by the editing agent before any save. The browser does not mutate document content directly.' : 'Editing is disabled for historical versions. Restore creates a non-active proposal copied from this content.'}</p></div>
          {isCurrent ? (
            <form className="surface-detail-edit-form" onSubmit={(event) => { event.preventDefault(); if (editAction) onAction?.(editAction, envelope.surfaceId, { ...documentInput(doc), instructions }); }}>
              <div className="surface-detail-field">
                <label htmlFor={`${envelope.surfaceId}-instructions`}>Describe the behavior improvement</label>
                <textarea className="designed-control surface-detail-control" id={`${envelope.surfaceId}-instructions`} name="instructions" value={instructions} onChange={(event) => setInstructions(event.currentTarget.value)} />
                <p className="field-helper">Editable advisory input. Submitting opens an AI-assisted edit session; it does not save a version.</p>
              </div>
              <button type="submit" className="surface-action-link primary" disabled={!editAction || Boolean(editAction.disabled)}>{editAction?.label ?? 'Improve behavior'}</button>
            </form>
          ) : (
            <p className="surface-state-inline no-op" role="status">Edit input disabled: selected version {doc.version} is read-only and immutable.</p>
          )}
        </section>
      </article>
    </SurfaceStateFrame>
  );
}

function AgentAdminEditSessionSurface({ envelope, onAction }: Props) {
  const session = (envelope.data.session ?? {}) as EditSessionView;
  const target = envelope.data.target;
  const [refinementInstructions, setRefinementInstructions] = useState('');
  const [showDiff, setShowDiff] = useState(false);
  const reviseAction = actionById(envelope.actions, 'action-agent-doc-edit-revise');
  const saveAction = actionById(envelope.actions, 'action-agent-doc-edit-save');
  const cancelAction = actionById(envelope.actions, 'action-agent-doc-edit-cancel');
  const diffAction = actionById(envelope.actions, 'action-agent-doc-version-diff');
  const sessionInput = stringRecord({
    sessionId: session.sessionId,
    agentDefinitionId: session.agentDefinitionId ?? target?.agentDefinitionId,
    kind: session.kind ?? target?.kind,
    documentId: session.documentId ?? target?.documentId,
    baseVersion: session.baseVersion ?? target?.baseVersion
  });
  const warnings = session.warnings ?? [];
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="agent-admin-doc-surface agent-admin-edit-session" aria-labelledby={`${envelope.surfaceId}-edit-heading`}>
        <div className="surface-section-heading">
          <div><p className="eyebrow">Agent Admin · editing session</p><h4 id={`${envelope.surfaceId}-edit-heading`}>Review proposed document</h4></div>
          <p>Target {session.kind ?? target?.kind ?? 'document'} {session.documentId ?? target?.documentId ?? ''} from base version {session.baseVersion ?? target?.baseVersion ?? 'current'}.</p>
        </div>
        <dl className="authority-summary-grid" aria-label="Edit session metadata">
          <div><dt>Status</dt><dd>{session.status ?? envelope.data.state ?? 'proposed'}</dd></div>
          <div><dt>Session</dt><dd>{session.sessionId ?? 'pending'}</dd></div>
          <div><dt>Started</dt><dd>{session.startedAt ?? envelope.generatedAt}</dd></div>
          <div><dt>Save behavior</dt><dd>{envelope.data.saveCreatesNonActiveProposal || envelope.data.saveCreatesNewCurrentVersion === false ? 'Save draft creates a non-active proposal. Activate separately from proposal review.' : 'Save behavior unavailable.'}</dd></div>
        </dl>
        <section className="agent-admin-edit-transcript" aria-labelledby={`${envelope.surfaceId}-transcript-heading`}>
          <h5 id={`${envelope.surfaceId}-transcript-heading`}>User instruction transcript</h5>
          {(session.instructions ?? []).length === 0 ? <p className="surface-empty-copy">No instructions are recorded yet.</p> : (
            <ol className="surface-section-list">
              {(session.instructions ?? []).map((entry, index) => <li key={`${entry.actorAccountId ?? 'actor'}-${index}`}><strong>{entry.actorAccountId ?? 'SaaS admin'}:</strong> {entry.instructions ?? ''}</li>)}
            </ol>
          )}
        </section>
        {session.clarifyingQuestion && <p className="surface-state-inline" role="status">Clarifying question: {session.clarifyingQuestion}</p>}
        <section className="agent-admin-markdown-document" aria-labelledby={`${envelope.surfaceId}-proposal-heading`}>
          <h5 id={`${envelope.surfaceId}-proposal-heading`}>Full proposed Markdown document</h5>
          <div className="markdown-response-content"><ReactMarkdown remarkPlugins={[remarkGfm]} skipHtml>{session.proposedContent ?? 'No proposed content is available.'}</ReactMarkdown></div>
        </section>
        <section className="agent-admin-edit-summary" aria-labelledby={`${envelope.surfaceId}-summary-heading`}>
          <h5 id={`${envelope.surfaceId}-summary-heading`}>Summary and advisory warnings</h5>
          <p>{session.changeSummary ?? 'No change summary is available.'}</p>
          {warnings.length > 0 && <ul className="surface-section-list" aria-label="Advisory warnings and risks">{warnings.map((warning) => <li key={warning}>{warning}</li>)}</ul>}
          {envelope.data.warningsAdvisoryOnly && <p className="surface-state-inline no-op" role="status">Warnings are advisory only; Save draft does not activate behavior.</p>}
        </section>
        <div className="surface-action-bar" aria-label="Proposal diff actions">
          <button type="button" onClick={() => setShowDiff((value) => !value)}>Show diff</button>
          {diffAction && <button type="button" onClick={() => onAction?.(diffAction, envelope.surfaceId, sessionInput)}>{diffAction.label} in backend</button>}
        </div>
        {showDiff && <pre className="agent-admin-unified-diff" aria-label="Proposed document diff preview">Base version {session.baseVersion ?? target?.baseVersion ?? 'current'} → proposed document\n\n{session.changeSummary ?? 'Diff preview requested. Use the backend Show diff action for authoritative comparison.'}</pre>}
        <form className="surface-detail-edit-form" onSubmit={(event) => { event.preventDefault(); if (reviseAction) onAction?.(reviseAction, envelope.surfaceId, { ...sessionInput, instructions: refinementInstructions }); }}>
          <div className="surface-detail-field">
            <label htmlFor={`${envelope.surfaceId}-refinement`}>Additional refinement instructions</label>
            <textarea className="designed-control surface-detail-control" id={`${envelope.surfaceId}-refinement`} name="refinementInstructions" value={refinementInstructions} onChange={(event) => setRefinementInstructions(event.currentTarget.value)} />
            <p className="field-helper">More input refines the proposal; it does not save the document.</p>
          </div>
          <button type="submit" className="surface-action-link secondary" disabled={!reviseAction || Boolean(reviseAction.disabled)}>{reviseAction?.label ?? 'Revise proposal'}</button>
        </form>
        <div className="surface-action-bar" aria-label="Edit session save or cancel">
          {saveAction && <button type="button" className="surface-action-link primary" onClick={() => onAction?.(saveAction, envelope.surfaceId, sessionInput)} disabled={Boolean(saveAction.disabled)}>{saveAction.label}{saveAction.requiresConfirmation ? ' · confirm' : ''}</button>}
          {cancelAction && <button type="button" className="surface-action-link secondary" onClick={() => onAction?.(cancelAction, envelope.surfaceId, sessionInput)} disabled={Boolean(cancelAction.disabled)}>{cancelAction.label}</button>}
        </div>
      </section>
    </SurfaceStateFrame>
  );
}

function AgentAdminProfileHistorySurface({ envelope, onAction }: Props) {
  const rows = (envelope.data.profileHistory ?? envelope.data.rows ?? []) as Array<Record<string, unknown>>;
  const agent = envelope.data.agent;
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="agent-admin-doc-surface agent-admin-profile-history" aria-labelledby={`${envelope.surfaceId}-profile-history-heading`}>
        <div className="surface-section-heading"><div><p className="eyebrow">Agent Admin · behavior profile versions</p><h4 id={`${envelope.surfaceId}-profile-history-heading`}>Behavior profile history</h4></div><p>Historical profile versions are immutable. Restore creates a profile restore proposal; activation creates a new active behavior-profile version.</p></div>
        <dl className="authority-summary-grid" aria-label="Current behavior profile">
          <div><dt>Agent</dt><dd>{agent?.agentName ?? 'Selected agent'}</dd></div>
          <div><dt>Current scope</dt><dd>{envelope.data.currentProfile?.scopeProvenance ?? envelope.data.currentProfile?.scope ?? 'global'}</dd></div>
          <div><dt>Model config reference</dt><dd>{envelope.data.currentProfile?.modelRefId ?? 'unavailable'}</dd></div>
          <div><dt>Safe model alias</dt><dd>{envelope.data.currentProfile?.safeModelAlias ?? 'unavailable'}</dd></div>
        </dl>
        {rows.length === 0 ? <p className="surface-empty-copy">No behavior-profile versions are visible for this scope.</p> : <div className="user-admin-clean-list agent-admin-profile-history-list" role="list" aria-label="Behavior profile versions">{rows.map((row, index) => <article key={String(row.profileVersion ?? row.version ?? index)} className="user-admin-clean-row" role="listitem"><span className="user-admin-person"><strong>Profile version {String(row.profileVersion ?? row.version ?? index + 1)}</strong><small>{String(row.scopeProvenance ?? row.scope ?? 'scope unavailable')}</small></span><span className="user-admin-role">Prompt v{String(row.activePromptVersion ?? '—')} · {String(row.modelRefId ?? 'model ref unavailable')}</span><small>Skills: {Array.isArray(row.assignedSkillDocumentIds) ? row.assignedSkillDocumentIds.join(', ') : 'none'} · Generated tools: {Array.isArray(row.assignedGeneratedToolIds) ? row.assignedGeneratedToolIds.join(', ') : 'none'}</small></article>)}</div>}
        <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} actionInput={stringRecord({ agentDefinitionId: agent?.agentDefinitionId ?? envelope.data.agentDefinitionId })} />
      </section>
    </SurfaceStateFrame>
  );
}

function AgentAdminSkillLibrarySurface({ envelope, onAction }: Props) {
  const rows = (envelope.data.rows ?? envelope.data.skills ?? []) as Array<Record<string, unknown>>;
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="agent-admin-doc-surface agent-admin-skill-library" aria-labelledby={`${envelope.surfaceId}-skill-library-heading`}>
        <div className="surface-section-heading"><div><p className="eyebrow">Agent Admin · skill library</p><h4 id={`${envelope.surfaceId}-skill-library-heading`}>Tenant-scoped governed skills</h4></div><p>Creating or editing a skill saves a non-active proposal. Assigning skills to agents creates behavior-profile versions and does not mutate skill document versions.</p></div>
        {rows.length === 0 ? <p className="surface-empty-copy">No governed skills are visible for this scope.</p> : <div className="user-admin-clean-list agent-admin-doc-list" role="list" aria-label="Skill library rows">{rows.map((row) => <button key={String(row.documentId ?? row.stableSkillId ?? row.name)} type="button" role="listitem" className="user-admin-clean-row" onClick={() => { const action = actionById(envelope.actions, String(row.actionId ?? 'action-agent-admin-open-skill-doc')); if (action) onAction?.(action, envelope.surfaceId, stringRecord({ agentDefinitionId: envelope.data.agentDefinitionId, skillDocumentId: row.documentId, documentId: row.documentId, kind: 'skill' })); }}><span className="user-admin-person"><strong>{String(row.name ?? row.title ?? 'Skill')}</strong><small>{String(row.purpose ?? row.description ?? 'No purpose provided')}</small></span><span className="status-pill info">{String(row.lifecycleStatus ?? envelope.data.lifecycleDefault ?? 'active')}</span><small>Assigned agent count: {String(row.assignedAgentCount ?? 'available after backend read')}</small></button>)}</div>}
        <SurfaceActionBar actions={envelope.actions.filter((action) => action.actionId !== 'action-agent-admin-open-skill-doc')} surfaceId={envelope.surfaceId} onAction={onAction} actionInput={stringRecord({ agentDefinitionId: envelope.data.agentDefinitionId })} />
      </section>
    </SurfaceStateFrame>
  );
}

function AgentAdminAssignmentSurface({ envelope, onAction }: Props) {
  const contract = envelope.data.surfaceContract;
  const agent = envelope.data.agent;
  const isToolAssignment = contract === 'agent_admin.tool_assignment.v1';
  const isModelConfig = contract === 'agent_admin.model_config_ref.v1';
  const rows = isToolAssignment ? (envelope.data.availableGeneratedTools ?? []) : (envelope.data.availableSkills ?? []);
  const assigned = isToolAssignment ? (envelope.data.currentlyAssignedGeneratedToolIds ?? []) : (envelope.data.currentlyAssignedSkillDocumentIds ?? []);
  const saveAction = actionById(envelope.actions, isToolAssignment ? 'action-agent-admin-assign-generated-tools' : isModelConfig ? 'action-agent-admin-update-model-config-ref' : 'action-agent-admin-assign-skills');
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="agent-admin-doc-surface agent-admin-assignment" aria-labelledby={`${envelope.surfaceId}-assignment-heading`}>
        <div className="surface-section-heading"><div><p className="eyebrow">Agent Admin · behavior-profile assignment</p><h4 id={`${envelope.surfaceId}-assignment-heading`}>{envelope.title}</h4></div><p>Saving this surface is backend-authorized and creates a new behavior-profile version for the selected scope. It does not edit generated code or skill document versions.</p></div>
        <dl className="authority-summary-grid" aria-label="Assignment profile impact">
          <div><dt>Agent</dt><dd>{agent?.agentName ?? 'Selected agent'}</dd></div>
          <div><dt>Profile scope</dt><dd>{envelope.data.profile?.scopeProvenance ?? envelope.data.profile?.scope ?? 'tenant/global scope resolved by backend'}</dd></div>
          <div><dt>Activation impact</dt><dd>{envelope.data.activationCreatesBehaviorProfileVersion ? 'Creates behavior-profile version' : 'Backend determines impact'}</dd></div>
          <div><dt>Secrets/code boundary</dt><dd>{isModelConfig ? 'Provider secrets are not exposed.' : isToolAssignment ? 'Generated tool implementation is read-only.' : 'Skill document versions are unchanged.'}</dd></div>
        </dl>
        {isModelConfig ? <p className="surface-state-inline" role="status">Current model config reference: {envelope.data.currentModelRefId ?? envelope.data.profile?.modelRefId ?? 'unavailable'} · safe alias {envelope.data.safeModelAlias ?? envelope.data.profile?.safeModelAlias ?? 'unavailable'}.</p> : <div className="user-admin-clean-list agent-admin-assignment-list" role="list" aria-label="Available assignment rows">{rows.map((row) => { const id = 'generatedToolId' in row ? row.generatedToolId : row.documentId; return <article key={String(id)} className="user-admin-clean-row" role="listitem"><span className="user-admin-person"><strong>{String('generatedToolId' in row ? row.generatedToolId : row.name)}</strong><small>{String('purpose' in row ? row.purpose : row.source ?? 'app-description/code-generated')}</small></span><span className={`status-pill ${assigned.includes(String(id)) ? 'success' : 'info'}`}>{assigned.includes(String(id)) ? 'assigned' : 'available'}</span></article>; })}</div>}
        {saveAction && <button type="button" className="surface-action-link primary" onClick={() => onAction?.(saveAction, envelope.surfaceId, stringRecord({ agentDefinitionId: agent?.agentDefinitionId ?? envelope.data.agentDefinitionId, assignedIds: assigned.join(',') }))} disabled={Boolean(saveAction.disabled)}>{saveAction.label}{saveAction.requiresConfirmation ? ' · confirm' : ''}</button>}
        <SurfaceActionBar actions={envelope.actions.filter((action) => action.actionId !== saveAction?.actionId)} surfaceId={envelope.surfaceId} onAction={onAction} actionInput={stringRecord({ agentDefinitionId: agent?.agentDefinitionId ?? envelope.data.agentDefinitionId })} />
      </section>
    </SurfaceStateFrame>
  );
}

function AgentAdminProposalReviewSurface({ envelope, onAction }: Props) {
  const proposal = (envelope.data.proposal ?? {}) as Record<string, unknown>;
  const proposalId = String(proposal.proposalId ?? envelope.data.target?.documentId ?? 'proposal');
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="agent-admin-doc-surface agent-admin-proposal-review" aria-labelledby={`${envelope.surfaceId}-proposal-heading`}>
        <div className="surface-section-heading"><div><p className="eyebrow">Agent Admin · proposal review</p><h4 id={`${envelope.surfaceId}-proposal-heading`}>Review behavior-change proposal {proposalId}</h4></div><p>Approval, rejection, and activation are separate backend-authorized actions. Rejection leaves active behavior unchanged; high-risk or authority-expanding changes route to decision-card review.</p></div>
        <dl className="authority-summary-grid" aria-label="Proposal review metadata">
          <div><dt>Status</dt><dd>{String(proposal.status ?? envelope.data.state ?? 'draft')}</dd></div>
          <div><dt>Risk</dt><dd>{String(proposal.riskClassification ?? 'low')}</dd></div>
          <div><dt>Authority expansion</dt><dd>{proposal.authorityExpansion ? 'requires decision-card route' : 'not indicated'}</dd></div>
          <div><dt>Active behavior changed</dt><dd>{envelope.data.activeBehaviorChanged ? 'yes after activation' : 'no'}</dd></div>
        </dl>
        <section className="agent-admin-edit-summary" aria-labelledby={`${envelope.surfaceId}-proposal-summary-heading`}><h5 id={`${envelope.surfaceId}-proposal-summary-heading`}>Summary/rationale</h5><p>{String(proposal.summary ?? proposal.rationale ?? 'No proposal summary is available.')}</p>{Array.isArray(proposal.suggestedTests) && <ul className="surface-section-list" aria-label="Suggested tests">{proposal.suggestedTests.map((item) => <li key={String(item)}>{String(item)}</li>)}</ul>}</section>
        <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} actionInput={stringRecord({ proposalId, agentDefinitionId: envelope.data.target?.agentDefinitionId })} />
      </section>
    </SurfaceStateFrame>
  );
}

function AgentAdminVersionHistorySurface({ envelope, onAction }: Props) {
  const rows = (envelope.data.rows ?? []).map(asVersionRow);
  const selected = typeof envelope.data.selectedVersion === 'object' ? envelope.data.selectedVersion as AgentAdminDocumentDetail : undefined;
  const historyAction = actionById(envelope.actions, 'action-agent-doc-version-history');
  const diffAction = actionById(envelope.actions, 'action-agent-doc-version-diff');
  const restoreAction = selected && !selected.currentVersion ? actionById(envelope.actions, 'action-agent-doc-version-restore') : undefined;
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="agent-admin-doc-surface agent-admin-version-history" aria-labelledby={`${envelope.surfaceId}-history-heading`}>
        <div className="surface-section-heading"><div><p className="eyebrow">Agent Admin · immutable versions</p><h4 id={`${envelope.surfaceId}-history-heading`}>Version history</h4></div><p>Rows show simple integer version numbers. Selecting a version reopens this surface through backend authorization.</p></div>
        {rows.length === 0 ? <p className="surface-empty-copy">No saved versions are available.</p> : (
          <div className="version-row-list" role="list" aria-label="Document versions">
            {rows.map((row) => <button key={row.version ?? row.label} type="button" role="listitem" className="user-admin-clean-row agent-admin-version-row" disabled={!historyAction} onClick={() => historyAction && onAction?.(historyAction, envelope.surfaceId, versionInput(envelope, row.version))} aria-label={`Open version ${row.version}${row.currentVersion ? ', current version' : ', historical read-only version'}`}><span className="user-admin-person"><strong>Version {row.version}</strong><small>{row.createdAt ?? 'Created time unavailable'}</small></span><span className={`status-pill ${row.currentVersion ? 'success' : 'info'}`}>{row.currentVersion ? 'current' : 'read-only'}</span></button>)}
          </div>
        )}
        {selected && <article className="agent-admin-version-selected" aria-labelledby={`${envelope.surfaceId}-selected-heading`}>
          <p className="surface-state-inline forbidden" role="status">{envelope.data.readOnlyBanner ?? 'Historical version: read-only.'}</p>
          <div className="surface-section-heading compact"><div><p className="eyebrow">Selected version</p><h4 id={`${envelope.surfaceId}-selected-heading`}>{selected.title} · version {selected.version}</h4></div><p>{selected.editSessionTranscriptSummary}</p></div>
          <DocMetadata doc={selected} />
          <div className="markdown-response-content"><ReactMarkdown remarkPlugins={[remarkGfm]} skipHtml>{selected.contentBody}</ReactMarkdown></div>
          <div className="surface-action-bar" aria-label="Selected version actions">
            {diffAction && <button type="button" onClick={() => onAction?.(diffAction, envelope.surfaceId, documentInput(selected))}>{diffAction.label}</button>}
            {restoreAction && <button type="button" onClick={() => onAction?.(restoreAction, envelope.surfaceId, documentInput(selected))}>{restoreAction.label}{restoreAction.requiresConfirmation ? ' · confirm' : ''}</button>}
          </div>
        </article>}
      </section>
    </SurfaceStateFrame>
  );
}

function AgentAdminVersionDiffSurface({ envelope, onAction }: Props) {
  const noPrior = envelope.data.priorVersion == null || envelope.data.state === 'no-prior-version' || (envelope.data as { status?: string }).status === 'no-prior-version';
  const historyAction = actionById(envelope.actions, 'action-agent-doc-version-history');
  const restoreAction = actionById(envelope.actions, 'action-agent-doc-version-restore');
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="agent-admin-doc-surface agent-admin-version-diff" aria-labelledby={`${envelope.surfaceId}-diff-heading`}>
        <div className="surface-section-heading"><div><p className="eyebrow">Agent Admin · adjacent diff</p><h4 id={`${envelope.surfaceId}-diff-heading`}>Version {String(envelope.data.selectedVersion ?? '')} vs {String(envelope.data.priorVersion ?? 'no predecessor')}</h4></div><p>{envelope.data.diffRule ?? 'selected version N is compared only with N-1'}</p></div>
        {noPrior ? <p className="surface-empty-copy">Version 1 has no predecessor, so no adjacent diff is available.</p> : <pre className="agent-admin-unified-diff" aria-label="Unified diff">{envelope.data.unifiedDiff}</pre>}
        <div className="surface-action-bar" aria-label="Diff actions">
          {historyAction && <button type="button" onClick={() => onAction?.(historyAction, envelope.surfaceId, versionInput(envelope, Number(envelope.data.selectedVersion ?? 0)))}>{historyAction.label}</button>}
          {restoreAction && <button type="button" onClick={() => onAction?.(restoreAction, envelope.surfaceId, versionInput(envelope, Number(envelope.data.selectedVersion ?? 0)))}>{restoreAction.label}{restoreAction.requiresConfirmation ? ' · confirm' : ''}</button>}
        </div>
      </section>
    </SurfaceStateFrame>
  );
}

function AgentAdminCreateSkillSurface({ envelope, onAction }: Props) {
  const createAction = actionById(envelope.actions, 'action-agent-admin-create-skill');
  const cancelAction = actionById(envelope.actions, 'action-agent-admin-open-agent-detail');
  return <AgentAdminCreateDocForm envelope={envelope} action={createAction} cancelAction={cancelAction} fields={[['skillName', 'Skill name'], ['purpose', 'Purpose/description'], ['initialContentRequest', 'Free-form initial content request']]} heading="Create skill" helper="The editing agent drafts the first Markdown version from your request." onAction={onAction} />;
}

function AgentAdminCreateReferenceDocSurface({ envelope, onAction }: Props) {
  const createAction = actionById(envelope.actions, 'action-agent-admin-create-reference-doc');
  const cancelAction = actionById(envelope.actions, 'action-agent-admin-open-skill-doc');
  return <AgentAdminCreateDocForm envelope={envelope} action={createAction} cancelAction={cancelAction} fields={[['referenceDocName', 'Reference doc name'], ['description', 'Short description'], ['initialContentRequest', 'Free-form initial content request']]} heading="Create reference doc" helper="The short description helps the model decide whether to read this reference doc at runtime." onAction={onAction} />;
}

function AgentAdminCreateDocForm({ envelope, action, cancelAction, fields, heading, helper, onAction }: Props & { action?: SurfaceAction; cancelAction?: SurfaceAction; fields: Array<[string, string]>; heading: string; helper: string }) {
  const [values, setValues] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<FormError | null>(null);
  const firstFieldRef = useRef<HTMLInputElement | null>(null);
  const validate = () => {
    const errors = fields.filter(([field]) => !values[field]?.trim()).map(([field, label]) => ({ field, message: `${label} is required.` }));
    return errors.length > 0 ? { message: 'Please complete the required fields.', fields: errors } : null;
  };
  const submit = () => {
    const error = validate();
    setFormError(error);
    if (error) {
      firstFieldRef.current?.focus();
      return;
    }
    if (action) onAction?.(action, envelope.surfaceId, stringRecord({ agentDefinitionId: envelope.data.agentDefinitionId, skillDocumentId: envelope.data.skillDocumentId, ...values }));
  };
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="agent-admin-doc-surface agent-admin-create-doc" aria-labelledby={`${envelope.surfaceId}-heading`}>
        <div className="surface-section-heading"><div><p className="eyebrow">Agent Admin · create</p><h4 id={`${envelope.surfaceId}-heading`}>{heading}</h4></div><p>{helper}</p></div>
        {formError && <div className="surface-state-inline forbidden" role="alert">{formError.message}</div>}
        <form className="surface-detail-edit-form" onSubmit={(event) => { event.preventDefault(); submit(); }} noValidate>
          {fields.map(([field, label], index) => {
            const fieldError = formError?.fields.find((candidate) => candidate.field === field);
            const controlId = `${envelope.surfaceId}-${field}`;
            const errorId = `${controlId}-error`;
            const isTextarea = field === 'initialContentRequest' || field === 'description' || field === 'purpose';
            return (
              <div className="surface-detail-field" key={field}>
                <label htmlFor={controlId}>{label}</label>
                {isTextarea ? <textarea className="designed-control surface-detail-control" id={controlId} name={field} value={values[field] ?? ''} aria-describedby={fieldError ? errorId : undefined} aria-invalid={Boolean(fieldError)} onChange={(event) => setValues((current) => ({ ...current, [field]: event.currentTarget.value }))} /> : <input ref={index === 0 ? firstFieldRef : undefined} className="designed-control surface-detail-control" id={controlId} name={field} value={values[field] ?? ''} aria-describedby={fieldError ? errorId : undefined} aria-invalid={Boolean(fieldError)} onChange={(event) => setValues((current) => ({ ...current, [field]: event.currentTarget.value }))} />}
                {fieldError && <p className="field-helper error" id={errorId}>{fieldError.message}</p>}
              </div>
            );
          })}
          <div className="surface-action-bar">
            <button type="submit" className="surface-action-link primary" disabled={!action || Boolean(action.disabled)}>{action?.label ?? heading}{action?.requiresConfirmation ? ' · confirm' : ''}</button>
            {cancelAction && <button type="button" className="surface-action-link secondary" onClick={() => onAction?.(cancelAction, envelope.surfaceId, stringRecord({ agentDefinitionId: envelope.data.agentDefinitionId, skillDocumentId: envelope.data.skillDocumentId, kind: 'skill', documentId: envelope.data.skillDocumentId }))}>Cancel</button>}
          </div>
        </form>
      </section>
    </SurfaceStateFrame>
  );
}

function AgentAdminDeleteSkillSurface({ envelope, onAction }: Props) {
  const deleteAction = actionById(envelope.actions, 'action-agent-admin-delete-skill');
  const cancelAction = actionById(envelope.actions, 'action-agent-admin-open-agent-detail');
  const referenceDocs = envelope.data.referenceDocs ?? [];
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="agent-admin-doc-surface agent-admin-delete-skill" aria-labelledby={`${envelope.surfaceId}-heading`}>
        <div className="surface-section-heading"><div><p className="eyebrow">Agent Admin · deprecate/remove</p><h4 id={`${envelope.surfaceId}-heading`}>Deprecate or remove skill {envelope.data.skillName}</h4></div><p>{envelope.data.deprecationWarning ?? envelope.data.permanentDeletionWarning ?? 'Skill removal defaults to deprecation and manifest/loader access removal; hard delete is policy-gated.'}</p></div>
        <p className="surface-state-inline forbidden" role="alert">Default lifecycle action is deprecation. Permanent deletion is shown only when backend lifecycle policy permits it.</p>
        <section className="agent-admin-reference-list" aria-label="Reference docs that will also be deleted">
          <h5>Reference docs affected: {envelope.data.referenceDocCount ?? referenceDocs.length}</h5>
          {referenceDocs.length === 0 ? <p className="surface-empty-copy">No reference doc list was provided.</p> : referenceDocs.map((reference) => <p key={reference.documentId} className="user-admin-clean-row"><strong>{reference.name}</strong><span>{reference.description}</span></p>)}
        </section>
        <div className="surface-action-bar">
          {deleteAction && <button type="button" className="surface-action-link danger" onClick={() => onAction?.(deleteAction, envelope.surfaceId, stringRecord({ agentDefinitionId: envelope.data.agentDefinitionId, skillDocumentId: envelope.data.skillDocumentId }))} disabled={Boolean(deleteAction.disabled)}>{deleteAction.label}{deleteAction.requiresConfirmation ? ' · confirm' : ''}</button>}
          {cancelAction && <button type="button" className="surface-action-link secondary" onClick={() => onAction?.(cancelAction, envelope.surfaceId, stringRecord({ agentDefinitionId: envelope.data.agentDefinitionId }))}>Cancel</button>}
        </div>
      </section>
    </SurfaceStateFrame>
  );
}

function AgentAdminDeleteReferenceDocSurface({ envelope, onAction }: Props) {
  const deleteAction = actionById(envelope.actions, 'action-agent-admin-delete-reference-doc');
  const cancelAction = actionById(envelope.actions, 'action-agent-admin-open-agent-detail');
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="agent-admin-doc-surface agent-admin-delete-reference" aria-labelledby={`${envelope.surfaceId}-heading`}>
        <div className="surface-section-heading"><div><p className="eyebrow">Agent Admin · deprecate/remove</p><h4 id={`${envelope.surfaceId}-heading`}>Deprecate or remove reference doc {envelope.data.referenceDocName}</h4></div><p>{envelope.data.deprecationWarning ?? envelope.data.permanentDeletionWarning ?? 'Reference removal follows lifecycle policy; permanent deletion is explicit and policy-gated.'}</p></div>
        <p className="surface-state-inline forbidden" role="alert">Default lifecycle action is deprecation. Permanent deletion is shown only when backend lifecycle policy permits it.</p>
        <div className="surface-action-bar">
          {deleteAction && <button type="button" className="surface-action-link danger" onClick={() => onAction?.(deleteAction, envelope.surfaceId, stringRecord({ agentDefinitionId: envelope.data.agentDefinitionId, referenceDocumentId: envelope.data.referenceDocumentId, documentId: envelope.data.referenceDocumentId }))} disabled={Boolean(deleteAction.disabled)}>{deleteAction.label}{deleteAction.requiresConfirmation ? ' · confirm' : ''}</button>}
          {cancelAction && <button type="button" className="surface-action-link secondary" onClick={() => onAction?.(cancelAction, envelope.surfaceId, stringRecord({ agentDefinitionId: envelope.data.agentDefinitionId }))}>Cancel</button>}
        </div>
      </section>
    </SurfaceStateFrame>
  );
}

function AgentAdminRuntimeTracesSurface({ envelope, onAction }: Props) {
  const rows = (envelope.data.rows ?? []) as AgentAdminRuntimeTraceRow[];
  const initialFilters = envelope.data.filters ?? {};
  const [agentDefinitionId, setAgentDefinitionId] = useState(String(initialFilters.agentDefinitionId ?? ''));
  const [documentIdOrStableId, setDocumentIdOrStableId] = useState(String(initialFilters.documentIdOrStableId ?? ''));
  const [occurredAtFrom, setOccurredAtFrom] = useState(String(initialFilters.occurredAtFrom ?? ''));
  const [occurredAtTo, setOccurredAtTo] = useState(String(initialFilters.occurredAtTo ?? ''));
  const traceAction = actionById(envelope.actions, 'action-agent-admin-open-runtime-traces');
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="agent-admin-doc-surface agent-admin-runtime-traces" aria-labelledby={`${envelope.surfaceId}-heading`}>
        <div className="surface-section-heading"><div><p className="eyebrow">Agent Admin · runtime reads</p><h4 id={`${envelope.surfaceId}-heading`}>Runtime skill/reference read traces</h4></div><p>{envelope.data.contentRedaction ?? 'Trace rows show metadata only; full document content is not shown.'}</p></div>
        <form className="surface-search-form user-admin-clean-search agent-admin-trace-filter-form" role="search" onSubmit={(event) => { event.preventDefault(); if (traceAction) onAction?.(traceAction, envelope.surfaceId, stringRecord({ agentDefinitionId, documentIdOrStableId, occurredAtFrom, occurredAtTo })); }}>
          <label htmlFor={`${envelope.surfaceId}-agent-filter`}>Agent</label>
          <input className="designed-control surface-search-control" id={`${envelope.surfaceId}-agent-filter`} name="agentDefinitionId" value={agentDefinitionId} onChange={(event) => setAgentDefinitionId(event.currentTarget.value)} />
          <label htmlFor={`${envelope.surfaceId}-doc-filter`}>Skill or reference doc</label>
          <input className="designed-control surface-search-control" id={`${envelope.surfaceId}-doc-filter`} name="documentIdOrStableId" value={documentIdOrStableId} onChange={(event) => setDocumentIdOrStableId(event.currentTarget.value)} />
          <label htmlFor={`${envelope.surfaceId}-from-filter`}>From time</label>
          <input className="designed-control surface-search-control" id={`${envelope.surfaceId}-from-filter`} name="occurredAtFrom" value={occurredAtFrom} onChange={(event) => setOccurredAtFrom(event.currentTarget.value)} />
          <label htmlFor={`${envelope.surfaceId}-to-filter`}>To time</label>
          <input className="designed-control surface-search-control" id={`${envelope.surfaceId}-to-filter`} name="occurredAtTo" value={occurredAtTo} onChange={(event) => setOccurredAtTo(event.currentTarget.value)} />
          <button type="submit" className="surface-action-link secondary" disabled={!traceAction}>Apply trace filters</button>
        </form>
        {rows.length === 0 ? <p className="surface-empty-copy">No runtime read traces match the selected filters.</p> : (
          <div className="user-admin-clean-list agent-admin-trace-list" role="list" aria-label="Runtime read trace rows">
            {rows.map((row) => <article key={row.traceId} className="user-admin-clean-row agent-admin-trace-row" role="listitem"><span className="user-admin-person"><strong>{row.agentName}</strong><small>{row.documentRead} · {row.documentName}</small></span><span className="user-admin-role">{row.timestamp}</span><span className="status-pill info">{row.requestSessionId}</span><small>{row.userCustomerContext} · {row.decision} · {row.safeSummary}</small></article>)}
          </div>
        )}
      </section>
    </SurfaceStateFrame>
  );
}

function AgentRowButton({ agent, actions, surfaceId, onAction }: { agent: AgentRow; actions: SurfaceAction[]; surfaceId: string; onAction?: Props['onAction'] }) {
  const action = actions.find((candidate) => candidate.actionId === (agent.actionId ?? 'action-agent-admin-open-agent-detail'));
  return (
    <button type="button" role="listitem" className="user-admin-clean-row agent-admin-agent-row" disabled={!action || Boolean(action.disabled)} onClick={() => action && onAction?.(action, surfaceId, stringRecord({ agentDefinitionId: agent.agentDefinitionId, targetSurfaceId: agent.targetSurfaceId }))} aria-label={`Open ${agent.agentName ?? 'agent'} detail`}>
      <span className="user-admin-person"><strong>{agent.agentName ?? 'Unnamed agent'}</strong><small>{agent.shortPurpose ?? agent.purpose ?? 'No purpose provided'}</small></span>
      <span className="user-admin-role">{agent.workstreamDomain ?? 'domain unavailable'}</span>
      <span className="status-pill info">{agent.lastEditTime ?? 'no edits recorded'}</span>
    </button>
  );
}

function DocumentSummaryButton({ summary, action, agentDefinitionId, surfaceId, onAction }: { summary: { kind: string; documentId: string; title: string; description: string; currentVersion: number }; action?: SurfaceAction; agentDefinitionId?: string; surfaceId: string; onAction?: Props['onAction'] }) {
  return (
    <button type="button" role="listitem" className="user-admin-clean-row agent-admin-doc-row" disabled={!action || Boolean(action.disabled)} onClick={() => action && onAction?.(action, surfaceId, stringRecord({ agentDefinitionId, kind: summary.kind, documentId: summary.documentId, version: summary.currentVersion }))} aria-label={`Open ${summary.title} version ${summary.currentVersion}`}>
      <span className="user-admin-person"><strong>{summary.title}</strong><small>{summary.description}</small></span>
      <span className="user-admin-role">{summary.kind}</span>
      <span className="status-pill success">v{summary.currentVersion}</span>
    </button>
  );
}

function SkillSummary({ skill, actions, agentDefinitionId, surfaceId, onAction }: { skill: AgentAdminSkillDocSummary; actions: SurfaceAction[]; agentDefinitionId?: string; surfaceId: string; onAction?: Props['onAction'] }) {
  const skillAction = actions.find((action) => action.actionId === (skill.actionId ?? 'action-agent-admin-open-skill-doc'));
  const referenceAction = actions.find((action) => action.actionId === 'action-agent-admin-open-reference-doc');
  const createReferenceAction = actions.find((action) => action.actionId === 'action-agent-admin-open-create-reference-doc');
  const deleteSkillAction = actions.find((action) => action.actionId === 'action-agent-admin-open-delete-skill');
  const deleteReferenceAction = actions.find((action) => action.actionId === 'action-agent-admin-open-delete-reference-doc');
  return (
    <section className="agent-admin-skill-group" role="listitem" aria-labelledby={`${surfaceId}-${skill.documentId}-heading`}>
      <DocumentSummaryButton summary={{ kind: 'skill', documentId: skill.documentId, title: skill.name, description: skill.purpose, currentVersion: skill.currentVersion }} action={skillAction} agentDefinitionId={agentDefinitionId} surfaceId={surfaceId} onAction={onAction} />
      <div className="surface-action-bar" aria-label={`Actions for ${skill.name}`}>
        {createReferenceAction && <button type="button" onClick={() => onAction?.(createReferenceAction, surfaceId, stringRecord({ agentDefinitionId, skillDocumentId: skill.documentId }))}>{createReferenceAction.label}</button>}
        {deleteSkillAction && <button type="button" onClick={() => onAction?.(deleteSkillAction, surfaceId, stringRecord({ agentDefinitionId, skillDocumentId: skill.documentId }))}>{deleteSkillAction.label}</button>}
      </div>
      {skill.referenceDocs.length > 0 && <div className="agent-admin-reference-list" role="list" aria-label={`Reference docs for ${skill.name}`}>
        {skill.referenceDocs.map((reference) => <div key={reference.documentId} className="agent-admin-reference-row-group"><ReferenceSummaryButton reference={reference} action={referenceAction} agentDefinitionId={agentDefinitionId} skillDocumentId={skill.documentId} surfaceId={surfaceId} onAction={onAction} />{deleteReferenceAction && <button type="button" className="surface-action-link secondary" onClick={() => onAction?.(deleteReferenceAction, surfaceId, stringRecord({ agentDefinitionId, skillDocumentId: skill.documentId, referenceDocumentId: reference.documentId, documentId: reference.documentId }))}>{deleteReferenceAction.label}</button>}</div>)}
      </div>}
    </section>
  );
}

function ReferenceDocLinks({ referenceDocs, actions, agentDefinitionId, skillDocumentId, surfaceId, onAction }: { referenceDocs: AgentAdminReferenceDocSummary[]; actions: SurfaceAction[]; agentDefinitionId?: string; skillDocumentId?: string; surfaceId: string; onAction?: Props['onAction'] }) {
  const action = actions.find((candidate) => candidate.actionId === 'action-agent-admin-open-reference-doc');
  return <section className="agent-admin-reference-list" role="list" aria-label="Reference docs">{referenceDocs.map((reference) => <ReferenceSummaryButton key={reference.documentId} reference={reference} action={action} agentDefinitionId={agentDefinitionId} skillDocumentId={skillDocumentId} surfaceId={surfaceId} onAction={onAction} />)}</section>;
}

function ReferenceSummaryButton({ reference, action, agentDefinitionId, skillDocumentId, surfaceId, onAction }: { reference: AgentAdminReferenceDocSummary; action?: SurfaceAction; agentDefinitionId?: string; skillDocumentId?: string; surfaceId: string; onAction?: Props['onAction'] }) {
  return (
    <button type="button" role="listitem" className="user-admin-clean-row agent-admin-reference-row" disabled={!action || Boolean(action.disabled)} onClick={() => action && onAction?.(action, surfaceId, stringRecord({ agentDefinitionId, skillDocumentId, documentId: reference.documentId, stableReferenceId: reference.stableReferenceId, kind: 'reference', version: reference.currentVersion }))} aria-label={`Open reference doc ${reference.name} version ${reference.currentVersion}`}>
      <span className="user-admin-person"><strong>{reference.name}</strong><small>{reference.description}</small></span>
      <span className="user-admin-role">reference</span>
      <span className="status-pill info">v{reference.currentVersion}</span>
    </button>
  );
}

function DocMetadata({ doc }: { doc: AgentAdminDocumentDetail }) {
  return (
    <dl className="authority-summary-grid" aria-label="Document version metadata">
      <div><dt>Version</dt><dd>{doc.version}{doc.currentVersion ? ' · current' : ' · historical'}</dd></div>
      <div><dt>Created</dt><dd>{doc.createdAt}</dd></div>
      <div><dt>Actor</dt><dd>{doc.actorAccountId}</dd></div>
      <div><dt>Checksum</dt><dd>{doc.contentChecksum}</dd></div>
      <div><dt>Edit summary</dt><dd>{doc.editSessionTranscriptSummary}</dd></div>
    </dl>
  );
}

function actionMap(actions: SurfaceAction[]) {
  return new Map(actions.map((action) => [action.actionId, action]));
}

function actionById(actions: SurfaceAction[], actionId: string) {
  return actions.find((action) => action.actionId === actionId);
}

function asAgentRow(row: Record<string, unknown>): AgentRow {
  return row as AgentRow;
}

function asVersionRow(row: Record<string, unknown>): VersionRow {
  return row as VersionRow;
}

function stringRecord(values: Record<string, unknown>): Record<string, string> {
  return Object.fromEntries(Object.entries(values).filter(([, value]) => value !== undefined && value !== null).map(([key, value]) => [key, String(value)]));
}

function documentInput(doc: AgentAdminDocumentDetail): Record<string, string> {
  return stringRecord({ agentDefinitionId: doc.agentDefinitionId, kind: doc.kind, documentId: doc.documentId, version: doc.version, currentVersion: doc.currentVersion });
}

function versionInput(envelope: SurfaceEnvelope<AgentAdminSurfaceData>, version?: number): Record<string, string> {
  return stringRecord({ agentDefinitionId: envelope.data.agentDefinitionId, kind: envelope.data.kind, documentId: envelope.data.documentId, version });
}
