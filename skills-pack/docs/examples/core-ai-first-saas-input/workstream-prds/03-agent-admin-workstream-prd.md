# PRD 3: Agent Admin Workstream PRD

## 1. PRD identity

- **PRD name:** Agent Admin Functional Agent Workstream PRD
- **Scope:** Governed administration of runtime agents, agent definitions, prompts, skills, skill manifests, tool permission boundaries, deterministic prompt assembly, `readSkill(skillId)`, PromptAssemblyTrace, SkillLoadTrace, AgentWorkTrace, and audit.
- **Functional agent workstream:** `functional_agent.agent_admin`.
- **Goals:** Allow authorized admins to inspect, draft, propose, approve, activate, disable, and audit tenant-scoped agent behavior without letting prompt/skill text grant authority.
- **Non-goals:** Arbitrary model-provider management beyond model config references; ungoverned prompt editing; autonomous approval of authority expansion; app-domain agent behavior beyond foundation admin agents.
- **Dependencies on other PRDs:** Requires Main/Foundation auth/audit/shell and User Admin roles/capabilities. Audit PRD consumes traces and audit events.

## 2. Actors and authority

- **User roles:** `TENANT_ADMIN` with `agent_admin.*`; `SAAS_OWNER_ADMIN` only for seed/default package metadata and support-safe investigation; `AUDITOR` read-only; specialized `AGENT_STEWARD` role may edit/propose but not approve.
- **System/internal actors:** `AgentRuntime`, `PromptAssembler`, `SkillLoader`, `ToolBoundaryEnforcer`, `SeedDocumentImporter`, `AuditEventWriter`.
- **Functional agent:** `functional_agent.agent_admin` helps explain agent configuration, draft prompt/skill changes, summarize diffs, and prepare proposals.
- **Internal agents:** optional `AgentBehaviorEditorAgent` drafts proposed diffs and risk notes; optional evaluator agents run simulations before activation.
- **AuthContext:** tenant-scoped context required for tenant agents; SaaS Owner seed updates cannot overwrite tenant-customized active records.
- **Tenant/customer scope:** all AgentDefinition, PromptDocument, SkillDocument, AgentSkillManifest, ToolPermissionBoundary, traces are tenant-scoped unless explicitly platform seed metadata.
- **Capabilities:** `agent_admin.dashboard.view`, `agent_admin.agents.search`, `agent_admin.agents.view`, `agent_admin.agents.edit`, `agent_admin.agents.lifecycle`, `agent_admin.prompts.edit_draft`, `agent_admin.prompts.propose_change`, `agent_admin.prompts.approve_activate`, `agent_admin.skills.search`, `agent_admin.skills.edit_draft`, `agent_admin.skills.propose_change`, `agent_admin.skills.approve_activate`, `agent_admin.manifests.assign`, `agent_admin.tools.manage_boundary`, `agent_runtime.read_skill`.
- **Approval/escalation:** activating prompt/skill changes, expanding tool/data authority, enabling side-effecting tools, changing approval policy, or replacing active manifests requires approver authority. Draft saves do not.
- **Forbidden behavior:** prompt/skill content cannot grant permissions; cannot activate unapproved versions; cannot load skill text not in active manifest; cannot bypass ToolPermissionBoundary; cannot edit archived agent except restore workflow; cannot cross-tenant inspect traces.

## 3. Workstream model

- **Purpose:** govern runtime AI behavior as versioned, traceable business configuration.
- **Default entry:** `surface.agent_admin.dashboard.v1` with agent status summary, pending proposals, failed prompt assembly/skill load denials, recent agent work traces, and seed drift notices.
- **Persistent composer:** accepts scoped requests such as “show disabled agents”, “draft safer prompt for UserAdminAgent”, “why was skill read denied”. The composer creates draft/proposal surfaces; activation still uses explicit approval actions.
- **Items/events:** `AgentDefinitionViewed`, `AgentDefinitionUpdated`, `AgentDisabled`, `AgentArchived`, `PromptDraftSaved`, `AgentPromptChangeProposed`, `AgentPromptChangeApproved`, `AgentPromptActivated`, `SkillDraftSaved`, `AgentSkillChangeProposed`, `AgentSkillChangeApproved`, `AgentSkillActivated`, `SkillManifestAssigned`, `ToolBoundaryChanged`, `PromptAssemblyTraceRecorded`, `SkillLoadTraceRecorded`, `AgentWorkTraceRecorded`, `CapabilityDenied`.
- **Trace links:** prompt/skill/agent surfaces link to active version snapshots, traces, related audit events, and work-trace timelines.
- **Realtime/stale/reconnect:** edit surfaces use version/checksum conflict detection; active status and pending proposals refresh; stale editors cannot activate until reloaded.

## 4. Structured surfaces

### `surface.agent_admin.dashboard.v1`
- **Type:** governance dashboard.
- **Purpose:** summarize agent estate health, pending changes, trace anomalies, and tool-boundary risks.
- **Placement:** default Agent Admin workstream entry.
- **Payload:** `agentCountsByStatus`, `pendingPromptProposals`, `pendingSkillProposals`, `toolBoundaryWarnings`, `recentPromptAssemblyTraces`, `recentSkillLoadDenials`, `recentAgentWorkTraces`, `seedDriftItems`, `allowedActions`.
- **States:** loading; empty no agents; ready; validation-error invalid filters; forbidden; stale when projections lag; success/failure as workstream events.
- **Trace/audit links:** dashboard cards deep-link to traces/audit detail.
- **A11y/responsive:** summary cards with accessible labels; high-risk warnings not color-only.

### `surface.agent_admin.agent_list.v1`
- **Type:** searchable governance table.
- **Purpose:** find agents by lifecycle, owner, authority, model config, prompt, manifest, tool boundary, status.
- **Payload:** `filters`, `rows[] {agentId, displayName, lifecycleStatus, owner, authorityLevel, promptRef, manifestRef, toolBoundaryRef, modelConfigRef, lastAssemblyAt, riskFlags}`, `page`, `allowedActions`.
- **States:** loading/empty/ready/validation-error/forbidden/stale; sensitive config values redacted.

### `surface.agent_admin.agent_edit.v1`
- **Type:** detail/edit form.
- **Purpose:** view/edit AgentDefinition metadata, lifecycle, owner/steward, model config ref, prompt refs, manifest refs, tool boundary refs.
- **Payload:** `agentDefinition`, `activePromptVersion`, `activeManifest`, `toolBoundary`, `version`, `allowedActions`, `traceLinks`.
- **States:** validation errors for invalid refs/authority expansion; stale version conflict; archive/disable success/failure.

### `surface.agent_admin.prompt_edit.v1`
- **Type:** versioned document editor + diff/proposal card.
- **Purpose:** edit prompt drafts, propose changes, review/approve/activate.
- **Payload:** `promptDocument`, `activeVersion`, `draftVersion`, `proposedVersion`, `diff`, `riskNotes`, `simulationResults`, `approvalState`, `checksum`, `allowedActions`.
- **States:** empty no draft; ready editor; validation-error forbidden instructions/oversized content/missing rationale; stale checksum; forbidden if missing edit/approve capability.

### `surface.agent_admin.skills_list.v1`
- **Type:** searchable table.
- **Purpose:** find governed skills and manifest assignments.
- **Payload:** `filters`, `rows[] {skillId, title, status, activeVersion, assignedAgents[], checksum, lastLoadedAt, riskFlags}`, `page`, `allowedActions`.

### `surface.agent_admin.skills_edit.v1`
- **Type:** versioned skill editor + manifest assignment panel.
- **Purpose:** edit skill drafts, propose/approve/activate, assign compact manifest entries, inspect load traces.
- **Payload:** `skillDocument`, `activeVersion`, `draftVersion`, `diff`, `manifestAssignments`, `loadTraceSummary`, `allowedActions`.
- **States:** validation errors for manifest mismatch, forbidden authority statements, stale checksum, denied assignment.

## 5. Surface actions

| Action id | Label | Intent | Inputs | Capability id | Required authority | Idempotency | Side effects | Audit events | Success | Failure/denial | Approval |
|---|---|---|---|---|---|---|---|---|---|---|---|
| `action.agent_admin.refresh_dashboard` | Refresh | Load governance summary | filters | `agent_admin.dashboard.summary` | `agent_admin.dashboard.view` | read-only | sensitive read audit | `AgentAdminDashboardRead` | summary DTO | safe denial | no |
| `action.agent_admin.search_agents` | Search agents | Find agents | query/filters/page | `agent_admin.agents.search` | `agent_admin.agents.search` | read-only | read audit | `AgentDefinitionsSearched` | rows | validation/denial | no |
| `action.agent_admin.open_agent` | Open agent | View details | agentId | `agent_admin.agents.view` | `agent_admin.agents.view` | read-only | sensitive read audit | `AgentDefinitionRead` | detail | forbidden | no |
| `action.agent_admin.save_agent_definition` | Save agent | Edit metadata/refs | agent fields/version | `agent_admin.agents.update_definition` | `agent_admin.agents.edit` | same values no-op | state update | `AgentDefinitionUpdated` | updated detail | stale/denied | authority expansion may require approval |
| `action.agent_admin.disable_agent` | Disable | Stop runtime use | agentId/reason/version | `agent_admin.agents.change_lifecycle` | `agent_admin.agents.lifecycle` | same status no-op | runtime rejects future use | `AgentDisabled` | disabled | denied | maybe |
| `action.agent_admin.archive_agent` | Archive | Retire agent | agentId/reason/version | `agent_admin.agents.change_lifecycle` | `agent_admin.agents.lifecycle` | same status no-op | archived status | `AgentArchived` | archived | denied | yes if active side-effecting tools |
| `action.agent_admin.save_prompt_draft` | Save prompt draft | Save draft text | promptId/content/rationale/checksum | `agent_admin.prompts.save_draft` | `agent_admin.prompts.edit_draft` | checksum/idempotency | draft version | `PromptDraftSaved` | draft saved | validation/denied | no |
| `action.agent_admin.propose_prompt_change` | Propose prompt | Submit draft for review | promptId/draftVersion/rationale | `agent_admin.prompts.propose_change` | `agent_admin.prompts.propose_change` | duplicate proposal no-op | proposal created | `AgentPromptChangeProposed` | proposal | denied | no |
| `action.agent_admin.approve_activate_prompt` | Approve/activate prompt | Make prompt active | proposalId/decision/rationale | `agent_admin.prompts.approve_activate` | `agent_admin.prompts.approve_activate` | decision idempotent | active version changed | `AgentPromptChangeApproved`, `AgentPromptActivated` | active snapshot | denied/stale | yes |
| `action.agent_admin.save_skill_draft` | Save skill draft | Save governed skill text | skillId/content/rationale/checksum | `agent_admin.skills.save_draft` | `agent_admin.skills.edit_draft` | checksum/idempotency | draft | `SkillDraftSaved` | draft saved | validation/denied | no |
| `action.agent_admin.propose_skill_change` | Propose skill | Submit skill draft | skillId/draftVersion/rationale | `agent_admin.skills.propose_change` | `agent_admin.skills.propose_change` | duplicate no-op | proposal | `AgentSkillChangeProposed` | proposal | denied | no |
| `action.agent_admin.approve_activate_skill` | Approve/activate skill | Make skill active | proposalId/decision/rationale | `agent_admin.skills.approve_activate` | `agent_admin.skills.approve_activate` | idempotent decision | active skill snapshot | `AgentSkillChangeApproved`, `AgentSkillActivated` | active snapshot | denied/stale | yes |
| `action.agent_admin.assign_skill_manifest` | Assign manifest | Update agent skill allowlist | agentId/manifestEntries/version | `agent_admin.manifests.assign` | `agent_admin.manifests.assign` | same manifest no-op | manifest active | `SkillManifestAssigned` | updated manifest | denied | yes if new authority |
| `action.agent_admin.update_tool_boundary` | Update tool boundary | Change allowed tools/data/side effects | agentId/boundary/version | `agent_admin.tools.update_boundary` | `agent_admin.tools.manage_boundary` | same boundary no-op | boundary active | `ToolBoundaryChanged` | updated boundary | denied | yes |

## 6. Governed backend capabilities

### `agent_admin.dashboard.summary`
- **Class:** read/evidence.
- **Actors:** Agent Admin UI/agent.
- **Scope:** selected tenant.
- **DTOs:** filters -> summary counts, warnings, trace snippets.
- **Data:** AgentDefinitionView, Prompt/Skill proposal views, trace views, audit.
- **Audit:** `AgentAdminDashboardRead`.
- **Tests:** redaction, forbidden, tenant isolation.

### `agent_admin.agents.search` / `agent_admin.agents.view`
- **Class:** read/evidence.
- **DTOs:** search filters/page; detail by agent id.
- **Validation:** tenant scope; redact model config secrets.
- **Data:** AgentDefinition, Prompt refs, SkillManifest, ToolBoundary, trace summaries.
- **Audit:** `AgentDefinitionsSearched`, `AgentDefinitionRead`.
- **Tests:** search by status/owner, cross-tenant denial, allowed actions.

### `agent_admin.agents.update_definition`
- **Class:** command/governance.
- **DTOs:** `{agentId, displayName, owner, steward, lifecycle?, authorityLevel?, modelConfigRef?, promptRef?, manifestRef?, toolBoundaryRef?, version, reason}` -> AgentDefinition.
- **Validation:** refs active/approved where required; authority expansion approval; no archived edit unless restore policy.
- **Data:** writes AgentDefinition current state/history.
- **Idempotency:** unchanged update no-op.
- **Audit:** `AgentDefinitionUpdated`.
- **Tests:** invalid ref, stale version, authority expansion denial.

### `agent_admin.agents.change_lifecycle`
- **Class:** command/approval.
- **DTOs:** `{agentId, targetStatus, reason, version, approvalId?}`.
- **Validation:** allowed transition enabled/disabled/archived; active work safe shutdown if needed.
- **Audit:** `AgentEnabled`, `AgentDisabled`, `AgentArchived`.
- **Tests:** disabled agent runtime rejects, archive active side-effecting requires approval.

### `agent_admin.prompts.save_draft`
- **Class:** governance command.
- **DTOs:** `{promptDocumentId, baseVersionId, content, rationale, checksum, idempotencyKey}` -> draft version.
- **Validation:** max size, forbidden authority claims flagged, no secrets, base version exists.
- **Audit:** `PromptDraftSaved`.
- **Tests:** checksum conflict, validation, no activation.

### `agent_admin.prompts.propose_change`
- **Class:** proposal.
- **DTOs:** `{promptDocumentId, draftVersionId, rationale, riskNotes?, requestedActivation?}` -> proposal.
- **Data:** marks draft proposed, creates diff snapshot.
- **Audit:** `AgentPromptChangeProposed`.
- **Tests:** duplicate proposal, forbidden draft ownership if policy.

### `agent_admin.prompts.approve_activate`
- **Class:** approval/governance.
- **DTOs:** `{proposalId, decision: APPROVE|REJECT|REQUEST_CHANGES, rationale, approvalId?}` -> active/rejected status.
- **Validation:** approver cannot approve own proposal if separation policy; simulations present if required; no authority expansion via text.
- **Side effects:** active prompt ref changes; prompt assembly cache invalidated.
- **Audit:** `AgentPromptChangeApproved`, `AgentPromptActivated`, or rejected events.
- **Tests:** approval separation, active snapshot immutability, runtime uses active version.

### `agent_admin.skills.save_draft` / `agent_admin.skills.propose_change` / `agent_admin.skills.approve_activate`
- **Class:** governance command/proposal/approval.
- **DTOs:** analogous to prompt capabilities, with `skillId`, `title`, `whenToUse`, `body`, `checksum`.
- **Validation:** skill text cannot declare authority grants; manifest compatibility; no secrets.
- **Audit:** `SkillDraftSaved`, `AgentSkillChangeProposed`, `AgentSkillActivated`.
- **Tests:** draft/propose/approve path, cross-tenant denial, immutable active version.

### `agent_admin.manifests.assign`
- **Class:** governance/approval.
- **DTOs:** `{agentId, manifestEntries[{skillId, versionPolicy, purpose, whenToUse}], version, reason, approvalId?}`.
- **Validation:** skills active/approved; entries compact; authority expansion approval; tool boundary compatibility.
- **Audit:** `SkillManifestAssigned`.
- **Tests:** assigned skill appears compact in prompt assembly, unassigned skill read denied.

### `agent_admin.tools.update_boundary`
- **Class:** governance/approval.
- **DTOs:** `{agentId, allowedTools[], dataScopes[], sideEffectPolicy, approvalRequirements, version, reason}`.
- **Validation:** caller can grant boundary; side effects approval-gated; no broad wildcard without approval.
- **Audit:** `ToolBoundaryChanged`, denials.
- **Tests:** denied tool invocation, allowed tool invocation, approval for expansion.

### `agent_runtime.prompts.assemble`
- **Class:** read/evidence/internal trace.
- **Actors:** AgentRuntime.
- **DTOs:** `{agentId, mode, authContext, correlationId}` -> `{systemPrompt, compactManifest, modelConfigRef, traceId}`.
- **Validation:** agent enabled; refs active; AuthContext authorized.
- **Data:** reads AgentDefinition, active PromptVersion, AgentSkillManifest, ToolBoundary.
- **Audit/trace:** `PromptAssemblyTrace` with prompt/skill/manifest/tool refs.
- **Tests:** deterministic assembly, disabled agent, cross-tenant denial, trace recorded.

### `agent_runtime.skills.read`
- **Class:** read/evidence/tool.
- **Actors:** AgentRuntime, authorized agent tool `readSkill(skillId)`.
- **DTOs:** `{agentId, skillId, requestedVersion?, mode, authContext}` -> `{skillText, version, checksum}`.
- **Validation:** skill assigned in active manifest; active version; tenant match; ToolPermissionBoundary allows; mode allowed.
- **Audit/trace:** `SkillLoadTrace` allowed/denied.
- **Tests:** authorized read, unassigned denied, inactive denied, trace redaction.

### `agent_runtime.work_trace.record`
- **Class:** trace/audit.
- **Actors:** AgentRuntime/tools/workflows.
- **DTOs:** `{agentId, sessionId?, capabilityId, promptTraceId, skillTraceIds[], toolCalls[], dataReads[], outcome, status}` -> `AgentWorkTrace`.
- **Validation:** sensitive payload redaction.
- **Tests:** trace linked to prompt/skill/tool and audit search.

## 7. Akka realization expectations

- **Event Sourced Entities:** `AgentDefinitionEntity`, `PromptDocumentEntity`, `SkillDocumentEntity`, `AgentSkillManifestEntity`, `ToolPermissionBoundaryEntity`, immutable trace entities for PromptAssemblyTrace/SkillLoadTrace/AgentWorkTrace or audit-backed event entities.
- **Key Value Entities:** model config refs/current indexes if history not required; runtime cache metadata.
- **Workflows:** approval/activation workflows for prompt/skill/tool-boundary changes; seed import workflow.
- **Views:** AgentDefinitionView, PromptDocumentView, SkillDocumentView, ProposalQueueView, TraceSearchView, ToolBoundaryView.
- **Consumers:** projection updates and trace enrichment.
- **Timed Actions:** optional scheduled seed drift check, proposal expiry, evaluation reminders.
- **Agents:** `AgentBehaviorEditorAgent` and optional evaluator agents with governed prompts/skills.
- **HTTP endpoints:** Agent Admin dashboard/search/detail/edit, prompt/skill/proposal/manifest/tool-boundary endpoints; runtime internal endpoints if needed.
- **Frontend:** six structured surfaces, Monaco/plain editor, diff viewer, proposal cards, manifest assignment UI, trace links, stale checksum handling.

## 8. Internal agents, workflows, and event-driven processing

- **AgentBehaviorEditorAgent:** drafts prompt/skill diffs, rationale, risk, test suggestions. Tools limited to read config, save draft/proposal; cannot approve/activate or expand authority.
- **Model/tool boundaries:** all runtime agents resolve active AgentDefinition and ToolPermissionBoundary; side-effecting tools require explicit boundary and capability authorization.
- **Prompt/skill governance:** draft -> propose -> approve/reject/request changes -> activate; active versions immutable; rollback creates new activation event pointing to previous snapshot.
- **Workflows:** `PromptChangeApprovalWorkflow`, `SkillChangeApprovalWorkflow`, `ToolBoundaryApprovalWorkflow`, `SeedDocumentImportWorkflow`.
- **Events/retries:** proposal events update queues; activation idempotent; seed import never overwrites tenant customized active content.
- **Traces:** every assembly, skill load, tool call, denial, and consequential agent result links to audit.

## 9. Security, audit, and compliance

- Prompts/skills are behavior guidance only; backend authorization and ToolPermissionBoundary enforce authority.
- Tenant isolation for all governed documents and traces.
- Provider/API secrets never stored in prompt/skill text or sent to frontend.
- Sensitive traces redact prompts, user content, tool inputs, and outputs according to caller role.
- Denied skill loads/tool calls are logged.
- Activation requires approval for behavior-changing versions; high-risk changes need simulations/evidence if configured.
- Tests cover prompt-injection authority claims, unassigned `readSkill`, disabled agent runtime rejection, stale activation, redaction, and cross-tenant denial.

## 10. Acceptance criteria

- **Backend:** governed AgentDefinition, PromptDocument/Version, SkillDocument/Version, Manifest, ToolBoundary, prompt assembly, skill loading, and work traces are implemented with scoped auth and immutable active snapshots.
- **Frontend:** dashboard, agent list/edit, prompt editor, skill list/edit surfaces render all required states, diffs, approvals, stale conflicts, and trace links.
- **Auth/security:** unauthorized edits/activations/tool-boundary expansions are denied and audited; prompt/skill text cannot grant permissions.
- **Audit/trace:** all agent admin reads/actions plus runtime assembly/skill/work traces are searchable by Audit PRD.
- **Workflows/events/timers:** proposal/approval/activation workflows are idempotent and projection-safe; seed import is idempotent.
- **Fullstack:** authorized steward drafts prompt/skill change, proposes it, approver activates it, runtime uses deterministic active version, and audit/trace links prove what changed.
- **Tests:** unit/integration/UI tests for success, validation, forbidden, tenant isolation, stale checksum/version, approval, tool boundary, deterministic assembly, and readSkill authorization.

## 11. Open questions

- Is two-person approval required for all prompt/skill activations, or only high-risk/authority-adjacent changes?
- Which model config references are allowed in the first release, and are they tenant-admin managed or platform-seeded only?
