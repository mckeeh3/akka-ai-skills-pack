# Module 7 PRD: Evaluation and Closed-Loop Improvement

## Status

Detailed PRD for the seventh MVP module in the progressive core AI-first SaaS seed app.

Read first:

- `00-document-development-process-context.md`
- `01-core-seed-progression-plan.md`
- `02-persistent-discussion-capture.md`
- `03-module-auth-app-access-prd.md`
- `04-module-user-admin-prd.md`
- `05-module-agent-definition-prd.md`
- `06-module-prompt-governance-prd.md`
- `07-module-skill-governance-prd.md`
- `08-module-audit-work-trace-prd.md`


## Workstream architecture alignment

This module PRD is interpreted under `10-canonical-core-app-prd.md` and `../../agent-workstream-application-architecture.md`. Any legacy references to pages, screens, navigation, or route inventory mean structured workstream surfaces, surface actions, and route/deep-link implementation details inside the agent workstream shell. They must not be used to generate a page-first admin console or chatbot-bolt-on app.

## 1. Module purpose

Module 7 completes the core AI-first SaaS seed loop by turning agent activity and trace evidence into governed evaluation and improvement.

The module lets authorized users define evaluation rubrics, run evaluator agents or deterministic checks against agent outputs and work traces, classify failures, create improvement proposals, review evidence, approve or reject behavior changes, activate approved changes safely, monitor outcomes, and roll back when needed.

This module must preserve the core safety principle from the planning discussion: agents may draft recommendations and improvement proposals, but consequential changes to active behavior remain human-governed unless an explicit bounded auto-approval rule is defined and tested.

## 2. User-visible outcome

At completion, an authorized reviewer can:

1. open an Evaluation and Improvement area;
2. inspect recent agent/test outputs and work traces that need evaluation;
3. create or select an evaluator rubric;
4. run an evaluation against a prompt/skill/agent output or trace;
5. see evaluator findings, scores, failure categories, evidence, and recommended changes;
6. convert findings into an improvement proposal for a prompt, skill, agent definition, or policy placeholder;
7. review proposal diff, replay/simulation evidence, risk, and expected impact;
8. approve, reject, or request changes;
9. activate an approved improvement through the existing prompt/skill/agent governance path;
10. monitor post-activation outcomes and roll back when necessary;
11. verify through tests that evaluation, proposal, approval, activation, monitoring, rollback, audit, and tenant isolation are enforced.

## 3. MVP boundaries

### In scope

- Evaluation run model for evaluating agent outputs, prompt tests, skill-load tests, and work traces from Module 6.
- Evaluator rubric documents with versioning sufficient for repeatable evaluation.
- Evaluator agent or deterministic evaluator adapter for MVP scoring/classification.
- Failure/issue classification taxonomy.
- Improvement proposal lifecycle: draft, in review, approved, rejected, activated, rolled back.
- Proposal targets: PromptDocument/PromptVersion, SkillDocument/SkillVersion, AgentDefinition, and policy placeholder notes.
- Replay/simulation evidence record for proposed changes, even if limited to test-console replay.
- Human approval workflow for consequential changes.
- Optional tightly bounded auto-approval policy only for low-risk changes if explicitly accepted.
- Outcome monitoring basics after activation.
- Rollback path to previous active prompt/skill/agent configuration.
- Audit/work trace events for evaluation and improvement actions.
- UI for evaluation runs, findings, proposals, approvals, activation, monitoring, and rollback.
- Tests for evaluation lifecycle, proposal governance, activation boundaries, rollback, audit, tenant isolation, and security.

### Out of scope for Module 7

- Fully autonomous self-modifying agents without human governance.
- Sophisticated experiment platform, multi-variant A/B testing, or statistical significance engine.
- Large-scale offline evaluation datasets and benchmark management beyond a small rubric/test-case set.
- Enterprise model risk management workflow.
- Cross-tenant learning or global shared improvements.
- Complex policy language and policy engine beyond basic approval/auto-approval boundary records.
- Full production business outcome analytics beyond seed outcome links and basic monitoring.

## 4. Actors

| Actor | Description | Module 7 expectations |
|---|---|---|
| Evaluation Reviewer | User responsible for evaluating agent quality and safety. | Can inspect evaluation runs, findings, and evidence. |
| Improvement Approver | Human authority for behavior changes. | Can approve/reject proposals and authorize activation/rollback. |
| Agent Steward | Owner of agent definitions/prompts/skills. | Can draft proposals and respond to findings for owned agents. |
| Tenant Admin | Tenant-wide admin. | Can manage evaluation capabilities and inspect audit. |
| Evaluator Agent | Bounded agent or adapter that scores outputs against rubric. | Can recommend findings and proposals but cannot activate consequential changes by default. |
| Auditor | Read-only investigator. | Can inspect evaluation/proposal/audit traces. |
| Tenant Member | Normal user. | No evaluation governance access by default. |

## 5. Authorization and capability model

Required capabilities:

- `evaluations.read` — view evaluation runs and findings.
- `evaluations.run` — start evaluation runs.
- `evaluations.rubrics.read` — view evaluator rubrics.
- `evaluations.rubrics.manage` — create/update evaluator rubrics.
- `improvements.read` — view improvement proposals.
- `improvements.create` — create proposals from findings or manually.
- `improvements.review` — approve, reject, or request changes.
- `improvements.activate` — activate approved proposals through governance paths.
- `improvements.rollback` — roll back activated improvements.
- `improvements.auto_approval.manage` — manage bounded auto-approval rules if included.
- `outcomes.read` — view outcome monitoring summaries.
- `evaluations.audit.read` — inspect evaluation/improvement audit and traces.

Recommended initial role mapping:

| Role | Module 7 capabilities |
|---|---|
| Tenant Admin | All capabilities, unless approval is separated. |
| Evaluation Reviewer | `evaluations.read`, `evaluations.run`, `evaluations.rubrics.read`, `improvements.read`, `improvements.create`. |
| Improvement Approver | `improvements.read`, `improvements.review`, `improvements.activate`, `improvements.rollback`, `outcomes.read`. |
| Agent Steward | Read/run/create capabilities scoped to owned agents. |
| Auditor | Read-only evaluation, improvement, outcome, and audit capabilities. |
| Member | No capabilities by default. |

Rules:

- Backend authorization is authoritative.
- Evaluation and improvement records are tenant-scoped.
- Evaluator agents cannot approve or activate consequential changes unless a bounded auto-approval rule explicitly permits it.
- A user cannot activate a change to a prompt/skill/agent artifact unless they also have the required governance capability for that artifact.
- Rollback requires explicit capability and is audited.

## 6. Durable objects and state ownership

### EvaluationRubric

A governed document defining how to evaluate an output or trace.

Required fields:

- `rubricId`
- `tenantId`
- name
- purpose
- target type: prompt output, skill usage, agent behavior, trace, safety, quality, policy adherence
- criteria with weights or pass/fail rules
- severity mapping
- failure categories
- evaluator instructions
- lifecycle status: draft, active, deprecated, archived
- version
- created/updated/reviewed metadata

State owner expectation: Event Sourced Entity for current rubric state, with immutable version snapshots if rubric history is included in MVP.

### EvaluationRun

Represents one evaluation execution.

Required fields:

- `evaluationRunId`
- `tenantId`
- target type/id: agentDefinitionId, promptVersion, skillVersion, workTraceId, or test output id
- rubric id/version
- evaluator type: deterministic, evaluator agent, manual
- status: queued, running, completed, failed, canceled
- input references and redaction summary
- findings summary
- score/pass-fail
- risk/severity
- created by / started at / completed at
- correlation id

State owner expectation: Workflow or Event Sourced Entity. Workflow is preferred if evaluation includes asynchronous evaluator calls, retries, and result collection.

### EvaluationFinding

Represents a specific issue or success finding.

Required fields:

- `findingId`
- `evaluationRunId`
- `tenantId`
- category: correctness, safety, hallucination, policy, tone, tool use, data access, prompt adherence, skill use, latency, user value
- severity
- confidence
- evidence references
- explanation
- recommended action
- linked trace event ids

State owner expectation: KV Entity or event-derived view associated with EvaluationRun.

### ImprovementProposal

A governed proposal to change behavior.

Required fields:

- `proposalId`
- `tenantId`
- target artifact type/id/version: prompt, skill, agent definition, rubric, policy placeholder
- proposal type: edit, activation, rollback, disable, authority reduction, tool-boundary change
- status: draft, in review, approved, rejected, activated, rolled back, canceled
- source evaluationRunId/findingIds
- proposed diff or change summary
- evidence summary
- risk/impact assessment
- expected outcome
- replay/simulation results
- approver/reviewer metadata
- activation metadata
- rollback target
- correlation id

State owner expectation: Event Sourced Entity because proposal lifecycle and approvals are consequential.

### ReplaySimulationResult

Evidence from testing a proposed change before activation.

Required fields:

- `simulationId`
- `tenantId`
- proposalId
- input trace/test case references
- candidate artifact versions/checksums
- baseline result summary
- candidate result summary
- evaluator score comparison
- pass/fail
- created timestamp

State owner expectation: KV Entity or child records of ImprovementProposal.

### OutcomeMetric / OutcomeObservation

Basic post-activation monitoring record.

Required fields:

- metric/observation id
- tenant id
- linked proposal id
- linked artifact version
- observation window
- measured value or status
- expected vs actual summary
- rollback recommendation flag

State owner expectation: View or KV Entity for MVP.

## 7. Capabilities

### 7.1 Evaluation queue and run list

Authorized users can view evaluation runs.

Filters:

- status;
- target type;
- agentDefinitionId;
- rubric;
- severity;
- pass/fail;
- created by;
- time range.

List rows:

- target summary;
- rubric;
- status;
- score/pass-fail;
- highest severity;
- created/completed timestamps;
- linked proposal count.

### 7.2 Create/run evaluation

Authorized users can run an evaluation against:

- prompt test output;
- skill-loading test output;
- agent test output;
- work trace from Module 6;
- active prompt/skill/agent configuration snapshot.

Required behavior:

- verify target belongs to selected tenant;
- verify user can evaluate target;
- select active rubric version;
- create EvaluationRun;
- call deterministic evaluator or evaluator agent;
- store findings and score;
- emit trace/audit events.

### 7.3 Evaluator rubrics

Authorized users can create and manage rubrics.

MVP rubric capabilities:

- create draft rubric;
- edit criteria and categories;
- activate rubric version;
- deprecate old rubric;
- view version history if included;
- use active rubric for evaluations.

### 7.4 Findings review

Evaluation run detail must show:

- target artifact/trace;
- rubric version;
- overall score/pass-fail;
- findings grouped by category/severity;
- evidence references to traces, prompt versions, skill versions, and outputs;
- evaluator explanation;
- recommended actions;
- create proposal action.

### 7.5 Improvement proposal creation

Authorized users or evaluator agents can draft proposals from findings.

Proposal targets:

- prompt change draft;
- skill change draft;
- agent definition status/authority/tool-boundary change;
- rubric update;
- policy placeholder note for future governance module.

Required behavior:

- link proposal to findings/evidence;
- create proposed diff or structured change;
- include risk/impact and expected outcome;
- require human review before activation unless auto-approval applies;
- emit audit event.

### 7.6 Replay/simulation evidence

Before activation, proposal should include at least minimal evidence.

MVP replay/simulation can:

- rerun prompt/skill test console with candidate change;
- compare candidate to baseline output;
- run rubric against candidate result;
- record pass/fail and score difference.

If replay is not available for a target, proposal must state why and require manual evidence.

### 7.7 Human review and approval

Reviewers can approve, reject, or request changes.

Review surface must show:

- source findings;
- proposed diff;
- affected artifact and current active version;
- replay/simulation evidence;
- risk/impact assessment;
- approval requirements;
- activation and rollback plan.

### 7.8 Activation

Approved proposals can be activated by authorized users.

Activation behavior depends on target:

- prompt proposal creates/activates a new PromptVersion through Module 4 governance path;
- skill proposal creates/activates a new SkillVersion through Module 5 governance path;
- agent definition proposal updates status/authority/tool boundary through Module 3 path;
- rubric proposal activates a new rubric version;
- policy placeholder creates a reviewed note but does not enforce policy unless later module implements it.

Activation must:

- verify current artifact version still matches expected baseline or require rebase;
- record activated version/checksum;
- emit audit/work trace;
- create outcome monitoring expectation.

### 7.9 Rollback

Authorized users can roll back an activated improvement.

Required behavior:

- identify previous active version or safe rollback target;
- verify rollback permissions;
- create rollback event and update target artifact through its governance path;
- record reason and evidence;
- emit audit/work trace;
- mark proposal rolled back.

### 7.10 Bounded auto-approval policy placeholder

If included, auto-approval must be conservative.

Allowed MVP shape:

- policy records specify target type, max severity, required evaluator score, no external side effects, and rollback availability;
- auto-approval applies only to low-risk draft/test artifacts by default;
- activation of production active prompt/skill/agent behavior remains human-approved unless explicitly accepted;
- all auto-approvals are audited and reviewable.

It is acceptable to defer auto-approval entirely and document human approval as mandatory.

### 7.11 Outcome monitoring

After activation, the system records basic outcome observations.

MVP monitoring:

- track linked proposal and activated artifact version;
- compare subsequent evaluation scores or failure counts;
- show whether expected improvement was observed;
- flag rollback consideration if regressions appear;
- provide manual outcome note.

## 8. UI requirements

### 8.1 Workstream surfaces and route/deep-link inventory

Minimum routes:

- `/app/evaluations` evaluation landing/run list;
- `/app/evaluations/new` create evaluation run;
- `/app/evaluations/:evaluationRunId` evaluation detail/findings;
- `/app/evaluations/rubrics` rubric list;
- `/app/evaluations/rubrics/new` create rubric;
- `/app/evaluations/rubrics/:rubricId` rubric detail;
- `/app/improvements` proposal list;
- `/app/improvements/:proposalId` proposal detail/review;
- `/app/improvements/:proposalId/simulation` replay/simulation evidence;
- `/app/improvements/:proposalId/activate` activation confirmation;
- `/app/improvements/:proposalId/rollback` rollback confirmation;
- `/app/outcomes` basic outcome monitoring list/dashboard surface.

### 8.2 Evaluation landing

Landing should summarize:

- recent failed evaluations;
- open high-severity findings;
- proposals awaiting review;
- recently activated improvements;
- rollback watch items;
- quick action to run evaluation.

### 8.3 Evaluation detail UI

Evaluation detail should show:

- status and score;
- target artifact/trace links;
- rubric version;
- findings by severity/category;
- evidence snippets and trace links;
- evaluator explanation;
- create proposal action.

### 8.4 Proposal review UI

Proposal review should show:

- source findings and trace evidence;
- proposed changes/diff;
- affected prompt/skill/agent/rubric artifact;
- baseline and candidate versions;
- simulation results;
- risk and impact;
- expected outcome;
- approval/rejection/request-changes controls;
- activation and rollback plan.

### 8.5 Outcome UI

Outcome monitoring should show:

- activated proposal;
- artifact/version affected;
- expected outcome;
- observed evaluation trend or manual observation;
- rollback recommendation flag;
- action to open proposal or rollback.

### 8.6 Accessibility and responsive behavior

- Findings and proposal diffs must be keyboard accessible.
- Severity/status indicators must include text labels.
- Review actions must require clear confirmation.
- Evidence panels should collapse/expand for long traces.

## 9. API requirements

Exact endpoint names may be adjusted during implementation planning, but the module must cover these contracts.

### Evaluations

- `GET /api/evaluations` — list evaluation runs.
- `POST /api/evaluations` — create/start evaluation run.
- `GET /api/evaluations/{evaluationRunId}` — run detail.
- `GET /api/evaluations/{evaluationRunId}/findings` — findings list.
- `POST /api/evaluations/{evaluationRunId}/cancel` — cancel if running and supported.

### Rubrics

- `GET /api/evaluation-rubrics` — list rubrics.
- `POST /api/evaluation-rubrics` — create rubric.
- `GET /api/evaluation-rubrics/{rubricId}` — rubric detail.
- `PUT /api/evaluation-rubrics/{rubricId}/draft` — update draft rubric.
- `POST /api/evaluation-rubrics/{rubricId}/activate` — activate rubric version.
- `POST /api/evaluation-rubrics/{rubricId}/deprecate` — deprecate rubric.

### Improvement proposals

- `GET /api/improvements` — list proposals.
- `POST /api/improvements` — create proposal.
- `GET /api/improvements/{proposalId}` — proposal detail.
- `PUT /api/improvements/{proposalId}/draft` — update draft proposal.
- `POST /api/improvements/{proposalId}/submit-review` — submit for review.
- `POST /api/improvements/{proposalId}/approve` — approve proposal.
- `POST /api/improvements/{proposalId}/reject` — reject proposal.
- `POST /api/improvements/{proposalId}/request-changes` — request changes.
- `POST /api/improvements/{proposalId}/simulate` — run replay/simulation.
- `POST /api/improvements/{proposalId}/activate` — activate approved proposal.
- `POST /api/improvements/{proposalId}/rollback` — roll back activated proposal.

### Outcomes

- `GET /api/outcomes/improvements` — list outcome observations for activated proposals.
- `GET /api/outcomes/improvements/{proposalId}` — outcome detail.
- `POST /api/outcomes/improvements/{proposalId}/note` — add manual outcome observation if included.

API rules:

- all endpoints require AuthContext and relevant capability;
- target artifacts/traces must belong to selected tenant;
- activation must route through target artifact governance rules;
- evaluator outputs and simulation results are redacted according to trace policy;
- responses must exclude secrets and cross-tenant data.

## 10. Authorization rules

Required backend authorization checks:

- resolve active account and selected tenant AuthContext;
- require capability for each evaluation/improvement action;
- verify target artifact or work trace belongs to selected tenant;
- enforce artifact-specific permissions for prompt/skill/agent changes;
- require reviewer/approver permission for approval actions;
- require activation permission and baseline version match for activation;
- require rollback permission for rollback;
- deny evaluator-agent activation unless bounded auto-approval policy explicitly permits;
- audit allowed consequential actions and denied attempts.

## 11. Evaluation and proposal safety requirements

Required safety rules:

- evaluator outputs are recommendations, not authority by themselves;
- every proposal must cite evidence or state why evidence is unavailable;
- activation requires approved proposal and target governance permissions;
- production active prompt/skill/agent behavior changes require human approval by default;
- rollback target must be known before activation whenever feasible;
- auto-approval, if included, is limited, explicit, tested, and auditable;
- proposed prompt/skill changes must still pass their own validation and secret-boundary checks;
- evaluation/test data must be redacted according to trace policy.

## 12. Audit and observability requirements

Required event types:

- `EVALUATION_RUBRIC_CREATED`
- `EVALUATION_RUBRIC_UPDATED`
- `EVALUATION_RUBRIC_ACTIVATED`
- `EVALUATION_RUN_STARTED`
- `EVALUATION_RUN_COMPLETED`
- `EVALUATION_RUN_FAILED`
- `EVALUATION_FINDING_CREATED`
- `IMPROVEMENT_PROPOSAL_CREATED`
- `IMPROVEMENT_PROPOSAL_SUBMITTED`
- `IMPROVEMENT_PROPOSAL_APPROVED`
- `IMPROVEMENT_PROPOSAL_REJECTED`
- `IMPROVEMENT_SIMULATION_RUN`
- `IMPROVEMENT_ACTIVATED`
- `IMPROVEMENT_ROLLED_BACK`
- `OUTCOME_OBSERVED`
- `EVALUATION_AUTH_DENIED`

Required audit fields:

- event id;
- timestamp;
- tenant id;
- actor account id or evaluator agent id;
- target artifact/trace ids;
- rubric id/version;
- evaluationRunId;
- proposalId when applicable;
- previous/new artifact version when applicable;
- decision and reviewer metadata;
- risk/severity;
- authorization decision;
- denial reason if denied;
- correlation id;
- safe metadata only.

Observability requirements:

- structured logs for evaluation runs, evaluator failures, proposal transitions, activation, rollback, and denials;
- correlation id through evaluation workflow, proposal, simulation, activation, and outcome observation;
- counters for evaluation pass/fail, high-severity findings, open proposals, activations, and rollbacks if metrics are available.

## 13. Security and privacy requirements

- Evaluation records, findings, proposals, simulations, and outcomes are tenant-scoped.
- Evaluation inputs and outputs may contain sensitive prompt, skill, trace, or user data and must follow redaction policy.
- Evaluator agents must not receive secrets or unauthorized cross-tenant data.
- Proposal diffs must not leak artifacts the user cannot read.
- Activation cannot bypass prompt/skill/agent governance authorization.
- Rollback cannot activate an artifact version that is invalid, deleted, cross-tenant, or forbidden.
- Denial errors must not leak cross-tenant resource existence.

## 14. Acceptance scenarios

### Scenario 1: Reviewer opens evaluation landing

Given a user has evaluation read capabilities, when they open Evaluations, then they see recent runs, open findings, pending proposals, and outcome watch items for the selected tenant.

### Scenario 2: Normal member is forbidden

Given a member lacks evaluation capabilities, when they open evaluation or improvement surfaces, then access is forbidden and audited.

### Scenario 3: Run evaluation on work trace

Given a work trace exists and an active rubric is available, when a reviewer starts an evaluation, then an EvaluationRun completes with score, findings, evidence references, and audit events.

### Scenario 4: Evaluator failure is captured

Given an evaluator agent/tool fails, when the evaluation run completes unsuccessfully, then status is failed, safe error summary is visible, and audit/trace records are emitted.

### Scenario 5: Create proposal from finding

Given an evaluation finding recommends a prompt change, when a steward creates a proposal, then the proposal links to the finding, includes proposed diff and risk/impact, and enters draft or review state.

### Scenario 6: Simulation compares baseline and candidate

Given a proposal includes a prompt or skill change, when simulation runs, then baseline and candidate summaries, rubric score comparison, and pass/fail are recorded.

### Scenario 7: Human approves and activates improvement

Given a proposal is approved by an authorized approver, when activation is requested, then the target prompt/skill/agent artifact is updated through its governance path, activation is audited, and outcome monitoring starts.

### Scenario 8: Activation rejects stale baseline

Given the target artifact changed after proposal creation, when activation is requested, then activation is blocked until the proposal is rebased or reapproved.

### Scenario 9: Evaluator cannot self-activate

Given an evaluator agent creates a proposal, when it attempts to activate the proposal without human approval, then activation is denied unless an explicit bounded auto-approval policy applies.

### Scenario 10: Rollback restores previous behavior

Given an activated improvement causes regression, when an authorized approver triggers rollback, then the previous safe artifact version is restored through the governance path and rollback is audited.

### Scenario 11: Outcome observation flags regression

Given post-activation evaluations degrade, when outcome monitoring updates, then the proposal shows rollback consideration and links to evidence.

### Scenario 12: Cross-tenant proposal access is denied

Given a Tenant A user requests a Tenant B proposal, when the API processes it, then no Tenant B data leaks and denial is audited.

## 15. Test requirements

Minimum test coverage:

- Evaluation landing/list/detail UI states: loading, empty, populated, forbidden, error.
- Rubric create/update/activate and capability denials.
- Evaluation run success with deterministic evaluator or test model.
- Evaluation run failure handling.
- Findings linked to target trace/artifact and rubric version.
- Proposal create/update/submit/review/approve/reject lifecycle.
- Simulation/replay record creation and comparison output.
- Activation success through prompt/skill/agent governance path.
- Activation denial for unapproved proposal, stale baseline, missing capability, and cross-tenant target.
- Evaluator-agent self-activation denial unless explicit bounded policy is tested.
- Rollback success and rollback denial cases.
- Outcome observation creation and regression flag.
- Tenant isolation for evaluations, findings, proposals, simulations, and outcomes.
- Redaction of sensitive evaluation inputs/outputs.
- Audit/work trace events for evaluation, proposal, activation, rollback, and denials.
- Frontend bundle/static asset test verifies no provider/model/backend secrets are exposed.

## 16. Akka decomposition notes

This section is input for later `akka-prd-to-specs-backlog` and implementation planning. It is not the final design.

Likely Akka components:

- Event Sourced Entity for `EvaluationRubric` lifecycle, with optional immutable version snapshots.
- Workflow for `EvaluationRun` orchestration, evaluator calls, retries, and result recording.
- Agent component for evaluator-agent scoring where LLM evaluation is used.
- Event Sourced Entity for `ImprovementProposal` lifecycle.
- Key Value Entity or child records for `ReplaySimulationResult` and `OutcomeObservation`.
- Consumers to emit/normalize audit and work trace events.
- Timed Action for post-activation monitoring checks if scheduled observations are included.
- Views for evaluations by tenant/status/target/severity, findings by tenant/category, proposals by tenant/status/target, and outcomes by proposal/artifact.
- HTTP endpoints for evaluations, rubrics, proposals, simulation, activation, rollback, and outcomes.
- React/Vite/TypeScript UI for evaluation queue/detail, rubric management, proposal review, simulation evidence, activation/rollback, and outcomes.

Implementation guidance:

- Reuse AuthContext, admin audit, AgentDefinition, PromptDocument, SkillDocument, SkillManifest, and WorkTrace models from Modules 1-6.
- Keep human approval as the default for consequential behavior changes.
- Route activation through existing artifact governance components rather than mutating artifacts directly from proposals.
- Store artifact version/checksum references for reproducibility.
- Use deterministic evaluator tests where possible; LLM evaluator behavior should be bounded and testable.
- Keep cross-tenant learning and autonomous production mutation deferred.

## 17. Demo flow

A successful Module 7 demo should run as follows:

1. Sign in as Evaluation Reviewer.
2. Open a work trace from a prompt/skill test run.
3. Run an evaluation using an active rubric.
4. Inspect findings and create an improvement proposal for the prompt or skill.
5. Run simulation comparing baseline and candidate behavior.
6. Sign in as Improvement Approver and approve the proposal.
7. Activate the improvement through the prompt/skill governance path.
8. View outcome monitoring for the activated proposal.
9. Trigger or simulate a regression and roll back to the previous version.
10. Open audit/work trace and inspect evaluation, proposal, activation, outcome, and rollback events.
11. Run tests proving governance, tenant isolation, redaction, stale-baseline denial, evaluator self-activation denial, rollback, and frontend secret boundary.

## 18. Explicit defers to later modules

Deferred to later product hardening:

- fully autonomous self-improvement;
- advanced auto-approval policy engine;
- canary/shadow traffic management;
- statistical experiment analysis;
- large evaluation dataset management;
- cross-tenant/global learning;
- enterprise model risk workflows;
- SIEM/GRC integrations;
- advanced business outcome analytics.

## 19. Readiness checklist

Module 7 is ready for decomposition when the following are true:

- [ ] Module 1-6 AuthContext, governance artifact, and WorkTrace assumptions are accepted.
- [ ] EvaluationRubric, EvaluationRun, EvaluationFinding, ImprovementProposal, ReplaySimulationResult, and OutcomeObservation fields are accepted.
- [ ] Human approval default and any bounded auto-approval policy are accepted.
- [ ] Proposal target types and activation-through-governance rule are accepted.
- [ ] Replay/simulation MVP scope is accepted.
- [ ] Rollback behavior and stale-baseline protection are accepted.
- [ ] Outcome monitoring MVP scope is accepted.
- [ ] Workstream surface, route/deep-link, evaluation/proposal/outcome states are accepted.
- [ ] Audit/work trace event coverage is accepted.
- [ ] Tenant isolation, capability denial, redaction, proposal lifecycle, activation, rollback, outcome, and frontend secret-boundary tests are accepted.
- [ ] Fully autonomous self-improvement, advanced experiments, cross-tenant learning, and enterprise risk features are confirmed as not part of Module 7.
