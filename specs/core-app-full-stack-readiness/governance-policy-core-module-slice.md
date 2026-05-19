# Governance/Policy Core Module Slice

## Purpose

Define the implementation-ready full-core Governance/Policy module for generated secure AI-first SaaS applications. This slice closes the loop from evaluator findings and behavior-edit proposals to human review, approval or rejection, activation through governed artifact paths, rollback, trace evidence, outcome monitoring, and workstream decision cards.

This is a specification slice only. Follow-up code tasks should implement components, APIs, views, frontend surfaces, and tests without re-deciding authority boundaries, proposal lifecycle, decision-card evidence, activation semantics, rollback behavior, or audit/trace obligations.

## Scope

Included:

- Tenant-scoped `EvaluationRubric`, `EvaluationRun`, `EvaluationFinding`, `ImprovementProposal`, `ReplaySimulationResult`, and `OutcomeObservation` contracts.
- Human-governed approval, rejection, request-changes, activation, rollback, and outcome-monitoring lifecycle.
- Decision-card surfaces for improvement review, authority expansion denial, stale-baseline denial, rollback consideration, and bounded auto-approval review if enabled.
- Activation-through-governance integration with Agent Admin prompt, skill, agent definition, skill manifest, tool-boundary, model-policy, and rubric commands.
- Trace evidence links into the Audit/Trace module for evaluation, proposal, approval, activation, rollback, outcome, denial, prompt assembly, skill load, tool, model, and data-access events.
- Workstream surface, route/deep-link, API, view, authorization, redaction, tenant-isolation, and tests for Governance/Policy.

Excluded and deferred:

- Fully autonomous self-modifying production agents.
- Large-scale dataset/benchmark management, A/B experimentation, statistical significance, canary/shadow traffic, enterprise model-risk workflows, SIEM/GRC integrations, and cross-tenant/global learning.
- Concrete React components; this slice defines API and structured surface contracts for later UI implementation.

## Core safety rule

Evaluator agents and behavior editor agents may analyze, recommend, classify, and draft proposals. They do not approve, activate, expand authority, grant tools, weaken policy, or roll back consequential production behavior by themselves.

Production prompt, skill, manifest, tool-boundary, model, agent-definition, rubric, or policy changes require human approval by default. Bounded auto-approval is optional, must be explicit, mechanically enforced, audited, and limited to low-risk non-production or authority-reducing changes unless a later accepted spec says otherwise.

## Capability contracts

### `evaluations.read`

- type: read/evidence capability.
- actors/callers: Evaluation Reviewer, Improvement Approver, Agent Steward for owned artifacts, Tenant Admin, Auditor, Governance/Policy workstream UI, proposal creation flow.
- AuthContext: selected tenant/customer, active account, role/capability basis, correlation id.
- outputs: evaluation run lists/details, findings, rubric references, evidence summaries, redacted trace/artifact links.
- denials: missing capability, disabled actor, cross-tenant target, artifact read not authorized, sensitive trace detail without `trace.sensitive.read`.

### `evaluations.run`

- type: command/workflow capability.
- actors/callers: Evaluation Reviewer, Agent Steward for owned artifacts, Tenant Admin, deterministic evaluator adapter, evaluator agent through a bounded internal workflow.
- AuthContext: selected tenant/customer, target artifact/trace authorization, active rubric version, correlation id, idempotency key.
- side effects: create `EvaluationRun`, invoke deterministic or evaluator-agent scoring, create findings, emit `EVALUATION_RUN_*` and `EVALUATION_FINDING_CREATED` trace events, update evaluation views.
- idempotency: start commands use stable command id or idempotency key; duplicate starts return the existing run.
- denials: missing target permission, no active rubric, cross-tenant target, evaluator would receive unauthorized or secret input, unsupported target type.

### `evaluations.rubrics.manage`

- type: governed document command capability.
- actors/callers: Tenant Admin, Evaluation Reviewer with rubric-management scope, Governance/Policy Admin.
- AuthContext: selected tenant, `evaluations.rubrics.manage`, correlation id, expected version for updates.
- side effects: create/edit/submit/activate/deprecate rubric versions; emit audit/trace facts; update rubric catalog/history views.
- denials: invalid criteria, secret-like instructions, cross-tenant refs, activation without review when review is required.

### `improvements.create`

- type: proposal drafting capability.
- actors/callers: Evaluation Reviewer, Agent Steward, Tenant Admin, behavior editor agent, evaluator agent through controlled proposal-draft path.
- AuthContext: selected tenant, target artifact read permission, source evaluation/finding authorization, correlation id.
- side effects: create draft `ImprovementProposal`, link findings/evidence/trace refs, create decision-card draft, emit proposal-created trace.
- denials: missing target access, proposal attempts authority expansion without marking approval requirement, cross-tenant finding/target, raw secret content in diff.

### `improvements.review`

- type: human decision capability.
- actors/callers: Improvement Approver, Tenant Admin, delegated reviewer role.
- AuthContext: selected tenant, reviewer account, role/capability basis, decision rationale, correlation id.
- side effects: approve, reject, or request changes; persist durable decision fact; emit `IMPROVEMENT_PROPOSAL_APPROVED`, `IMPROVEMENT_PROPOSAL_REJECTED`, or request-changes trace; update decision/proposal queues.
- denials: actor lacks reviewer authority, proposal not in review, stale or missing evidence when required, self-approval disallowed by tenant policy if configured.

### `improvements.activate`

- type: consequential command/workflow capability.
- actors/callers: Improvement Approver, Tenant Admin, authorized activation workflow after approval.
- AuthContext: selected tenant, `improvements.activate`, target-artifact governance capability such as `prompts.activate`, `skills.activate`, `agent.definitions.manage`, `agent.tool_boundaries.manage`, or `evaluations.rubrics.manage`, expected baseline version/checksum, correlation id, idempotency key.
- side effects: call target governance command, persist activation metadata, create outcome monitoring expectation, emit activation audit/work traces.
- denials: proposal not approved, actor lacks target governance capability, baseline mismatch/stale proposal, target inactive/deleted/cross-tenant, simulation/evidence missing when required, evaluator-agent self-activation.

### `improvements.rollback`

- type: consequential command/workflow capability.
- actors/callers: Improvement Approver, Tenant Admin, authorized rollback workflow.
- AuthContext: selected tenant, `improvements.rollback`, target artifact rollback capability, rollback reason/evidence, correlation id, idempotency key.
- side effects: activate previous safe version or restore prior state through target governance path, mark proposal rolled back, emit rollback trace, update outcome watch state.
- denials: no safe rollback target, missing rollback capability, target no longer valid, cross-tenant target, rollback would activate invalid/deprecated/forbidden artifact.

### `improvements.auto_approval.manage`

- type: optional policy configuration capability.
- actors/callers: Tenant Admin or Governance/Policy Admin only.
- AuthContext: selected tenant, explicit approval-policy capability, correlation id.
- side effects: create/update/deactivate bounded auto-approval rules and emit audit/trace facts.
- denials: production authority expansion, side-effecting tool grants, role/membership/security/billing changes, external side effects, missing rollback path, missing tests, unsupported risk tier.

### `outcomes.read` and `outcomes.note`

- type: read/observation capability.
- actors/callers: Improvement Approver, Evaluation Reviewer, Agent Steward for owned artifacts, Tenant Admin, Auditor read-only.
- AuthContext: selected tenant, proposal/artifact authorization, correlation id.
- side effects: `outcomes.read` is read-only; `outcomes.note` creates manual `OutcomeObservation` and emits `OUTCOME_OBSERVED`.
- denials: missing capability, cross-tenant proposal/artifact, sensitive trace detail without matching trace capability.

## Durable object contracts

### `EvaluationRubricEntity`

Recommended substrate: Event Sourced Entity for lifecycle; immutable snapshot records for activated rubric versions.

State fields:

| Field | Notes |
|---|---|
| `tenantId`, `rubricId` | Required scope and id. |
| `name`, `purpose`, `targetTypes` | Browser-safe summary and supported target kinds. |
| `criteria[]` | Weighted or pass/fail criteria with ids. |
| `severityMapping`, `failureCategories` | Classification taxonomy. |
| `evaluatorInstructions` | Governed content; no secrets. |
| `status` | `DRAFT`, `IN_REVIEW`, `ACTIVE`, `DEPRECATED`, `ARCHIVED`. |
| `latestVersion`, `activeVersion`, `checksum` | Repeatability and concurrency. |
| `createdBy`, `updatedBy`, `reviewedBy`, `activatedBy`, timestamps | Audit metadata. |

Commands/events:

- `createRubric`, `editDraftRubric`, `submitRubric`, `approveRubric`, `activateRubric`, `deprecateRubric`, `archiveRubric`.
- Events: `EvaluationRubricCreated`, `EvaluationRubricUpdated`, `EvaluationRubricSubmitted`, `EvaluationRubricApproved`, `EvaluationRubricActivated`, `EvaluationRubricDeprecated`, `EvaluationRubricArchived`.

Validation rules:

- Active rubric versions are immutable and same-tenant.
- Criteria ids are stable across versions where possible.
- Secret-like evaluator instructions, cross-tenant refs, and prompt text that attempts authority expansion block activation.

### `EvaluationRunWorkflow`

Recommended substrate: Workflow when evaluator calls, retries, and result collection are asynchronous; Event Sourced Entity only for a simpler synchronous implementation.

State fields:

- `tenantId`, `evaluationRunId`, target type/id/version/checksum, rubric id/version, evaluator type (`DETERMINISTIC`, `EVALUATOR_AGENT`, `MANUAL`), status (`QUEUED`, `RUNNING`, `COMPLETED`, `FAILED`, `CANCELED`), input refs, redaction summary, findings summary, score/pass-fail, highest severity, createdBy, startedAt, completedAt, correlation id, error summary.

Workflow steps:

1. Validate target ownership and caller capability.
2. Resolve active rubric version and target artifact/trace snapshot.
3. Redact and package evaluator input.
4. Invoke deterministic evaluator or bounded evaluator agent.
5. Validate structured result and map findings.
6. Persist findings and run result.
7. Emit trace/audit events and update views.

Validation rules:

- Evaluator input excludes secrets and unauthorized linked-resource details.
- Failed evaluator calls produce safe failed run state, not partial invisible errors.
- Canceled runs cannot create new findings after cancellation.

### `EvaluationFindingRecord`

Recommended substrate: immutable child record, Key Value Entity, or event-derived records associated with an `EvaluationRun`.

Fields:

- `tenantId`, `findingId`, `evaluationRunId`, category (`CORRECTNESS`, `SAFETY`, `HALLUCINATION`, `POLICY`, `TONE`, `TOOL_USE`, `DATA_ACCESS`, `PROMPT_ADHERENCE`, `SKILL_USE`, `LATENCY`, `USER_VALUE`), severity, confidence, evidence refs, linked trace event ids, explanation, recommended action, createdAt.

Rules:

- Finding evidence refs must resolve inside the same tenant.
- Browser DTOs return safe summaries unless caller has detail permissions for the linked artifact/trace.

### `ImprovementProposalEntity`

Recommended substrate: Event Sourced Entity because proposal lifecycle, review, approval, activation, and rollback are consequential.

State fields:

| Field | Notes |
|---|---|
| `tenantId`, `proposalId` | Required scope and id. |
| `targetArtifactRef` | Prompt, skill, agent definition, manifest, tool boundary, model policy, rubric, or policy placeholder. |
| `proposalType` | `EDIT`, `ACTIVATION`, `ROLLBACK`, `DISABLE`, `AUTHORITY_REDUCTION`, `TOOL_BOUNDARY_CHANGE`, `MODEL_POLICY_CHANGE`, `RUBRIC_CHANGE`, `POLICY_NOTE`. |
| `status` | `DRAFT`, `IN_REVIEW`, `CHANGES_REQUESTED`, `APPROVED`, `REJECTED`, `ACTIVATED`, `ROLLED_BACK`, `CANCELED`, `SUPERSEDED`. |
| `sourceEvaluationRunId`, `sourceFindingIds[]` | Optional but required when proposal comes from evaluation. |
| `proposedDiff`, `changeSummary` | Redacted browser-safe diff plus content refs as needed. |
| `evidenceSummary`, `traceRefs[]` | Evidence basis available at review time. |
| `riskImpact`, `confidence`, `expectedOutcome` | Decision-card fields. |
| `baselineVersion`, `baselineChecksum` | Stale-baseline protection. |
| `simulationRefs[]` | Replay/simulation evidence. |
| `reviewDecision`, `reviewerAccountId`, `reviewedAt` | Human decision record. |
| `activationMetadata` | Activated target version/checksum and command ids. |
| `rollbackTarget`, `rollbackMetadata` | Safe rollback version/state and rollback facts. |
| `createdByActor`, `createdByAgentDefinitionId`, timestamps, correlationId | Provenance. |

Commands/events:

- `createDraftProposal`, `attachDiff`, `attachEvidence`, `submitForReview`, `requestChanges`, `approveProposal`, `rejectProposal`, `runSimulation`, `activateApprovedProposal`, `markActivationFailed`, `rollbackActivatedProposal`, `cancelProposal`, `supersedeProposal`.
- Events: `ImprovementProposalCreated`, `ImprovementProposalUpdated`, `ImprovementProposalSubmitted`, `ImprovementProposalChangesRequested`, `ImprovementProposalApproved`, `ImprovementProposalRejected`, `ImprovementSimulationRecorded`, `ImprovementActivated`, `ImprovementActivationDenied`, `ImprovementRolledBack`, `ImprovementCanceled`, `ImprovementSuperseded`.

Validation rules:

- Activation requires approved status, target governance capability, expected baseline match, and required evidence.
- Authority expansion, side-effecting tool grants, model-provider expansion, approval-boundary expansion, and security/billing/membership changes always require human approval.
- Behavior editor/evaluator agents can draft, not approve or activate.
- Applying a proposal calls the same target component commands as direct admin actions; proposals do not mutate target records directly.

### `ReplaySimulationResultRecord`

Recommended substrate: Key Value Entity or immutable child record linked to the proposal.

Fields:

- `tenantId`, `simulationId`, `proposalId`, input trace/test-case refs, target baseline version/checksum, candidate artifact version/checksum, baseline result summary, candidate result summary, evaluator score comparison, pass/fail, safe error summary, createdBy, createdAt, correlationId.

Rules:

- Missing replay support is represented explicitly as manual evidence requirement.
- Simulation result cannot include raw secrets, provider credentials, JWTs, invitation tokens, or unauthorized trace data.

### `OutcomeObservationRecord`

Recommended substrate: Key Value Entity or event-derived records projected into outcome views.

Fields:

- `tenantId`, `observationId`, proposalId, target artifact ref/version/checksum, observation window, measured value/status, expected vs actual summary, linked evaluation run ids, regression flag, rollback recommendation flag, manual note, createdBy, createdAt, correlationId.

Rules:

- Outcome observations link back to the activated proposal and trace evidence.
- Regression flags recommend review or rollback; they do not roll back automatically unless an explicit future policy supports it.

### `AutoApprovalRuleEntity` (optional)

Recommended substrate: Event Sourced Entity if included; otherwise document as deferred.

Fields:

- `tenantId`, rule id/version, target types, allowed proposal types, max severity/risk, required score threshold, required simulation pass, allowed environments/modes, side-effect allowance, rollback requirement, status, reviewer/activation metadata.

MVP default:

- Deferred or inactive. If implemented, it only allows low-risk non-production draft/test artifact changes or authority reductions with successful simulation and explicit rollback target.

## Decision-card contract

Every proposal review surface is a decision card, not just an edit form.

Required fields:

```text
Decision subject: proposal id and target artifact ref
Goal / plan / task link: source evaluation run, finding ids, or behavior-edit request
Recommended action: approve, reject, request changes, activate, rollback, defer
Decision authority: human role/capability or explicit bounded auto-approval rule
Evidence considered: findings, trace refs, simulation results, linked artifact versions
Policy clauses / guardrails triggered: authority expansion, tool boundary, model policy, redaction, tenant scope
Confidence: evaluator confidence or reviewer-assigned confidence
Risk and impact: severity, affected users/agents/tools/data, side effects
Alternatives considered: reject, smaller change, authority reduction, rollback, manual monitoring
Known gaps / uncertainty: missing replay, partial evidence, stale baseline risk
Available actions: approve | reject | request changes | defer | escalate | activate | rollback
Decision deadline or SLA: optional review due date or stale proposal warning
Trace links: audit/work trace ids and correlation id
Outcome follow-up: monitoring expectation, regression criteria, rollback target
```

State effects:

- `approve` moves `IN_REVIEW` to `APPROVED` and records reviewer decision.
- `reject` moves to `REJECTED` with rationale.
- `request changes` moves to `CHANGES_REQUESTED` and preserves evidence/diff history.
- `activate` calls target governance command and moves to `ACTIVATED` only after success.
- `rollback` calls target rollback/activation command and moves to `ROLLED_BACK` only after success.
- `defer` leaves status unchanged but records decision note/deadline.
- `escalate` records trace/decision metadata and can update assigned reviewer role.

## Integration with Agent Admin and Audit/Trace

### Agent Admin activation targets

Governance/Policy uses the Agent Admin slice as the authoritative implementation path for behavior-changing targets:

| Proposal target | Activation path |
|---|---|
| Prompt change | `PromptDocumentEntity` submit/review/activate or rollback commands. |
| Skill change | `SkillDocumentEntity` submit/review/activate or rollback commands. |
| Agent definition change | `AgentDefinitionEntity` metadata/status/authority/ref commands with approval refs. |
| Skill manifest change | `AgentSkillManifestEntity` review/activate commands. |
| Tool-boundary change | `ToolPermissionBoundaryEntity` review/simulate/activate commands. |
| Model-policy/config change | `ModelConfigRefEntity` / `ModelPolicyEntity` manage commands when tenant model management is in scope. |
| Behavior-edit proposal | Existing `BehaviorEditProposalEntity` can be folded into or linked to `ImprovementProposalEntity`; future implementation should avoid two competing activation paths. |
| Rubric change | `EvaluationRubricEntity` review/activate commands. |
| Policy placeholder | Reviewed note/proposal only; no enforcement unless a later policy engine implements it. |

### Audit/Trace events consumed and emitted

Governance/Policy reads trace evidence from the Audit/Trace module and emits trace events back to it.

Required event types:

- `EVALUATION_RUBRIC_CREATED`, `EVALUATION_RUBRIC_UPDATED`, `EVALUATION_RUBRIC_ACTIVATED`.
- `EVALUATION_RUN_STARTED`, `EVALUATION_RUN_COMPLETED`, `EVALUATION_RUN_FAILED`, `EVALUATION_RUN_CANCELED`.
- `EVALUATION_FINDING_CREATED`.
- `IMPROVEMENT_PROPOSAL_CREATED`, `IMPROVEMENT_PROPOSAL_UPDATED`, `IMPROVEMENT_PROPOSAL_SUBMITTED`.
- `IMPROVEMENT_PROPOSAL_APPROVED`, `IMPROVEMENT_PROPOSAL_REJECTED`, `IMPROVEMENT_PROPOSAL_CHANGES_REQUESTED`.
- `IMPROVEMENT_SIMULATION_RUN`.
- `IMPROVEMENT_ACTIVATED`, `IMPROVEMENT_ACTIVATION_DENIED`, `IMPROVEMENT_ROLLED_BACK`.
- `OUTCOME_OBSERVED`.
- `GOVERNANCE_AUTH_DENIED`, `EVALUATION_AUTH_DENIED`.

Trace fields include tenant id, actor account or agent id, target artifact ids, rubric id/version, evaluation run id, finding ids, proposal id, previous/new artifact version, reviewer decision, risk/severity, authorization decision, denial reason, correlation id, and safe metadata only.

## View contracts

All views are tenant-scoped and authorization-filtered at endpoint/query boundary. Sensitive prompt/skill content and trace details are returned only by detail endpoints with matching capabilities.

| View | Source | Purpose | Key filters |
|---|---|---|---|
| `EvaluationRunListView` | EvaluationRun workflow/entity events | run queue and history | tenant, status, target type, target id, rubric, severity, pass/fail, createdBy, time range |
| `EvaluationRunDetailView` | EvaluationRun + finding records | run detail and evidence summary | tenant, evaluationRunId |
| `EvaluationFindingView` | finding records/events | finding lists and proposal creation context | tenant, run id, category, severity, target, proposal-linked status |
| `EvaluationRubricCatalogView` | rubric events/snapshots | rubric list/history | tenant, status, target type, active version |
| `ImprovementProposalQueueView` | proposal events | proposal/review/activation queues | tenant, status, target type, risk, reviewer, source run |
| `ImprovementProposalDetailView` | proposal + simulations + outcome refs | decision-card payload | tenant, proposalId |
| `SimulationEvidenceView` | simulation records | replay/simulation history | tenant, proposalId, pass/fail |
| `OutcomeObservationView` | outcome records | outcome dashboard/watch list | tenant, proposalId, target artifact, rollback recommendation |
| `GovernanceDecisionCardView` | proposal + trace/evidence projections | reviewer decision worklist | tenant, status, risk, authority expansion, stale baseline |
| `GovernanceAuditLinkView` | AuditTraceEvent refs | trace links from governance surfaces | tenant, proposal/run/artifact/correlation |

Query rules:

- Server derives tenant/customer from `AuthContext`; frontend ids are never authority.
- Avoid optional-filter OR query patterns; implement separate query variants for dominant access paths.
- List views return summaries; detail endpoints enforce artifact and trace detail authorization.

## Protected HTTP API contracts

All APIs require authenticated browser/user context, backend authorization, selected tenant/customer scope, correlation id, server-side tenant filtering, redaction, denial auditing, and browser-safe DTOs.

### Evaluations

- `GET /api/evaluations?status=&targetType=&agentDefinitionId=&rubricId=&severity=&passFail=&from=&to=` → `EvaluationRunListResponse`.
- `POST /api/evaluations` with `CreateEvaluationRunRequest` → starts `EvaluationRunWorkflow` and returns run summary.
- `GET /api/evaluations/{evaluationRunId}` → `EvaluationRunDetailDto` with findings summary and evidence links.
- `GET /api/evaluations/{evaluationRunId}/findings` → `EvaluationFindingListResponse`.
- `POST /api/evaluations/{evaluationRunId}/cancel` → cancel when running and supported.

### Rubrics

- `GET /api/evaluation-rubrics?status=&targetType=` → rubric catalog.
- `POST /api/evaluation-rubrics` → create draft rubric.
- `GET /api/evaluation-rubrics/{rubricId}` → rubric detail/history summary.
- `PATCH /api/evaluation-rubrics/{rubricId}/draft` → edit draft criteria/instructions.
- `POST /api/evaluation-rubrics/{rubricId}/submit` → submit for review if review is enabled.
- `POST /api/evaluation-rubrics/{rubricId}/review` → approve/reject/request changes.
- `POST /api/evaluation-rubrics/{rubricId}/activate` → activate approved version.
- `POST /api/evaluation-rubrics/{rubricId}/deprecate` → deprecate active or old version.

### Improvement proposals

- `GET /api/improvements?status=&targetType=&risk=&reviewer=&sourceEvaluationRunId=` → proposal queue.
- `POST /api/improvements` → create proposal from finding, behavior-edit request, or manual draft.
- `GET /api/improvements/{proposalId}` → decision-card detail.
- `PATCH /api/improvements/{proposalId}/draft` → update draft diff/evidence/risk.
- `POST /api/improvements/{proposalId}/submit-review` → submit for review.
- `POST /api/improvements/{proposalId}/approve` → approve with rationale.
- `POST /api/improvements/{proposalId}/reject` → reject with rationale.
- `POST /api/improvements/{proposalId}/request-changes` → request changes.
- `POST /api/improvements/{proposalId}/simulate` → create replay/simulation evidence.
- `POST /api/improvements/{proposalId}/activate` → activate approved proposal through target governance component.
- `POST /api/improvements/{proposalId}/rollback` → roll back activated proposal through target governance component.
- `POST /api/improvements/{proposalId}/cancel` → cancel draft/in-review proposal when authorized.

### Outcomes

- `GET /api/outcomes/improvements?targetType=&rollbackRecommended=&from=&to=` → outcome observation list/dashboard.
- `GET /api/outcomes/improvements/{proposalId}` → outcome detail for one proposal.
- `POST /api/outcomes/improvements/{proposalId}/note` → add manual outcome observation.

### Optional auto-approval rules

- `GET /api/governance/auto-approval-rules` → list effective rules if enabled.
- `POST /api/governance/auto-approval-rules` → create draft rule.
- `PATCH /api/governance/auto-approval-rules/{ruleId}/draft` → edit draft rule.
- `POST /api/governance/auto-approval-rules/{ruleId}/activate` → activate rule after approval.
- `POST /api/governance/auto-approval-rules/{ruleId}/deactivate` → deactivate rule.

MVP may omit these endpoints and return a documented human-approval-required policy.

## Workstream UI surface contracts

Functional agent: `Governance/Policy` or `Evaluation & Improvement`.

Minimum deep links:

- `/app/evaluations` evaluation landing/run list.
- `/app/evaluations/new` create evaluation run.
- `/app/evaluations/:evaluationRunId` run detail/findings.
- `/app/evaluations/rubrics` rubric list.
- `/app/evaluations/rubrics/:rubricId` rubric detail/history.
- `/app/improvements` proposal queue.
- `/app/improvements/:proposalId` decision-card review detail.
- `/app/improvements/:proposalId/simulation` replay/simulation evidence.
- `/app/improvements/:proposalId/activate` activation confirmation.
- `/app/improvements/:proposalId/rollback` rollback confirmation.
- `/app/outcomes` outcome monitoring dashboard.

Structured surfaces:

| Surface | Payload contract | Actions |
|---|---|---|
| GovernanceLandingSurface | recent failed evaluations, high-severity findings, proposals awaiting review, stale-baseline warnings, recently activated improvements, rollback watch items. | run evaluation, open queue/detail, refresh. |
| EvaluationRunListSurface | filters, run rows, status/severity/pass-fail, target/rubric refs, pagination. | filter, open run, cancel when allowed. |
| EvaluationRunDetailSurface | target snapshot, rubric version, findings, score, evidence/trace refs, evaluator explanation. | create proposal, open trace, rerun when allowed. |
| RubricGovernanceSurface | rubric catalog, draft/editor summary, version/history/diff. | create, edit, submit, review, activate, deprecate. |
| ImprovementProposalQueueSurface | proposal rows by status/risk/target/reviewer, authority-expansion and stale-baseline badges. | filter, open decision card, assign/escalate if included. |
| ImprovementDecisionCardSurface | complete decision-card contract, proposed diff, evidence, risk, simulation, target baseline/current state. | approve, reject, request changes, defer, escalate, simulate, activate. |
| SimulationEvidenceSurface | baseline/candidate summaries, score comparison, pass/fail, missing replay explanation. | rerun simulation, attach manual evidence, open traces. |
| ActivationConfirmationSurface | approved proposal, target command preview, baseline check, rollback target, risk acknowledgement. | activate or cancel. |
| RollbackConfirmationSurface | active proposal, regression/outcome evidence, previous safe version, impact summary. | rollback or cancel. |
| OutcomeMonitoringSurface | activated proposals, expected vs actual outcomes, evaluation trend, rollback recommendation. | open proposal, add note, start rollback. |

UI requirements:

- Rail/action visibility derives from `/api/me` capabilities; backend remains authoritative.
- Decision cards must distinguish evaluator recommendation from human decision.
- Severity, status, and risk indicators include text labels and must not rely on color alone.
- Approval, activation, rollback, and auto-approval actions require explicit confirmation and clear consequence copy.
- States: loading, empty, filtered empty, populated, forbidden, error, stale-baseline, missing-evidence, simulation-failed.
- Diffs, evidence panels, and timelines are keyboard accessible and responsive; long trace excerpts collapse safely.
- Frontend payloads never include provider/model/backend secrets, JWTs, WorkOS secrets, Resend keys, raw invitation tokens, or unauthorized prompt/skill bodies.

## Akka substrate routing

| Concern | Recommended substrate | Skills to load for implementation |
|---|---|---|
| Rubric lifecycle | Event Sourced Entity plus immutable snapshots. | `akka-event-sourced-entities`, `akka-agent-governed-documents`. |
| Evaluation execution | Workflow with deterministic/evaluator-agent adapter. | `akka-workflows`, `akka-agent-evaluation`, `akka-agent-structured-responses`. |
| Findings | Immutable child records, KVE, or event-derived records. | `akka-key-value-entities` or `akka-views`. |
| Improvement proposal lifecycle | Event Sourced Entity. | `akka-event-sourced-entities`, `ai-first-saas-decision-cards`. |
| Human review and activation orchestration | Workflow with pause/resume around review/activation. | `akka-workflows`, `akka-workflow-pausing`, `akka-workflow-testing`. |
| Replay/simulation | Workflow step plus KVE/record evidence. | `akka-workflows`, `akka-agent-closed-loop-improvement`, `akka-agent-testing`. |
| Outcome observations | KVE or view-derived records; optional timed checks. | `akka-key-value-entities`, `akka-timed-actions`, `akka-views`. |
| Queues/lists/dashboards | Views. | `akka-views`, `akka-view-query-patterns`, `akka-view-testing`. |
| Trace emission and evidence links | Audit/Trace recorder/client. | `ai-first-saas-audit-trace`, `akka-agent-work-trace`, `akka-consumers`. |
| Browser APIs | HTTP endpoints with request context/component clients. | `akka-http-endpoints`, `akka-http-endpoint-request-context`, `akka-http-endpoint-component-client`, `akka-http-endpoint-testing`. |
| Workstream UI | React/Vite structured surfaces. | `akka-web-ui-apps`, `akka-web-ui-api-client`, `akka-web-ui-state-rendering`, `akka-web-ui-forms-validation`, `akka-web-ui-accessibility-responsive`, `akka-web-ui-testing`. |

## Security and authorization rules

- Resolve active account and selected tenant/customer AuthContext for every API, command, workflow step, view query, stream, and tool exposure.
- Require capability per action; frontend-visible capabilities are hints only.
- Verify target artifact, trace, evaluation, finding, proposal, simulation, and outcome records belong to selected tenant/customer.
- Require linked artifact permission in addition to trace/proposal permission before returning prompt/skill/agent details.
- Activation requires both `improvements.activate` and the target artifact governance capability.
- Rollback requires both `improvements.rollback` and target rollback/activation capability.
- Deny evaluator-agent or behavior-editor self-activation unless an explicit bounded auto-approval rule permits the exact action.
- Deny authority expansion, side-effecting tool grant expansion, model-provider expansion, approval-boundary expansion, and security/billing/role/membership changes without human approval.
- Deny stale-baseline activation until proposal is rebased or reapproved.
- Denial responses must not leak cross-tenant resource existence and must emit caller-tenant denial trace.
- Evaluation inputs/outputs, proposal diffs, simulations, and outcomes follow Audit/Trace redaction policy.
- Secret-never-store values are rejected or stripped before persistence.

## Acceptance and test matrix

Minimum tests for generation readiness:

| Area | Required tests |
|---|---|
| Rubrics | create/edit/submit/review/activate/deprecate; activation denies invalid criteria, secret-like instructions, missing capability, and cross-tenant refs. |
| Evaluation runs | deterministic success creates completed run, score, findings, trace events; evaluator failure creates failed state and safe error; cancel path when supported. |
| Findings | findings link target artifact/trace/rubric version; evidence DTO redaction; create proposal from finding. |
| Proposal lifecycle | draft/update/submit/review/approve/reject/request-changes/cancel/supersede with durable events and decision-card fields. |
| Simulation | baseline/candidate comparison recorded; missing replay requires manual evidence; failed simulation blocks activation when required. |
| Activation | approved prompt/skill/agent/rubric proposal activates through target governance path and records activated version/checksum. |
| Activation denials | unapproved proposal, missing activation capability, missing target governance capability, stale baseline, missing evidence, cross-tenant target, evaluator self-activation. |
| Authority expansion | prompt/skill/tool/model/agent authority expansion requires human approval; prompt or skill text alone cannot grant authority. |
| Rollback | activated proposal rolls back to previous safe version through target governance path; denial for missing rollback target/capability/cross-tenant target. |
| Outcomes | outcome observation creation, expected-vs-actual summary, regression flag, rollback recommendation, manual note. |
| Decision cards | review card includes evidence, risk, confidence, impact, alternatives, trace links, available actions, and state effects. |
| Audit/trace | evaluation, proposal, review, activation, rollback, outcome, and denial event families emitted with correlation ids and safe metadata. |
| Tenant isolation | Tenant A cannot list/detail/activate/rollback Tenant B evaluations, proposals, simulations, outcomes, or linked artifacts. |
| Redaction | sensitive trace/artifact fields masked without permission; secrets absent from records, DTOs, fixtures, and exports. |
| UI | landing/list/detail/rubric/proposal/simulation/activation/rollback/outcome states: loading, empty, filtered empty, populated, forbidden, error, stale-baseline, missing-evidence. |
| Frontend secret boundary | built assets and fixtures contain no WorkOS, Resend, model provider, invitation token, JWT, or backend secrets. |
| Security review | backend authorization exists on every route/view/workflow action; activation cannot bypass target artifact governance. |

## Generation-ready checklist

- [x] Evaluation/rubric/finding/proposal/simulation/outcome durable contracts are specified.
- [x] Evaluator findings and behavior-edit proposals connect to review, approval/rejection, activation, rollback, trace evidence, and outcome monitoring.
- [x] Human approval is the default for consequential behavior changes.
- [x] Bounded auto-approval is optional, conservative, explicit, audited, and safely deferrable.
- [x] Decision-card surface contract includes evidence, policy/guardrail triggers, confidence, risk, impact, alternatives, actions, trace links, and outcome follow-up.
- [x] Activation routes through Agent Admin/governed artifact commands instead of mutating targets directly.
- [x] Audit/Trace event families and evidence links are specified.
- [x] View, API, workstream UI, security, redaction, and tenant-isolation contracts are specified.
- [x] Acceptance/security test matrix is specified.

## Follow-up implementation order

1. Domain records/enums for rubrics, evaluation targets, findings, proposals, decision cards, simulations, outcomes, risk/severity/confidence, and artifact refs.
2. `EvaluationRubricEntity` plus rubric version/history views and endpoint tests.
3. `EvaluationRunWorkflow`, deterministic evaluator adapter, finding records, and evaluation views.
4. `ImprovementProposalEntity` with decision-card fields and proposal queue/detail views.
5. Replay/simulation records and simulation workflow step.
6. Activation orchestration through prompt/skill/agent/manifest/tool/model/rubric governance component clients.
7. Rollback orchestration and outcome observation records/views.
8. Audit/Trace recorder integration for evaluation, proposal, decision, activation, rollback, outcome, and denial events.
9. Protected HTTP APIs for evaluations, rubrics, improvements, simulations, activation, rollback, and outcomes.
10. Governance/Policy workstream frontend surfaces, typed API client, UI states, accessibility, and frontend secret-boundary tests.
