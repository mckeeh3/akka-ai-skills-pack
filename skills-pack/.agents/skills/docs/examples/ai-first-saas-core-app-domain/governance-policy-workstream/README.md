# Governance / Policy Workstream PRD

## PRD identity

- **Workstream id:** `governance_policy`
- **Backing functional agent:** `functional_agent.governance_policy`
- **Domain:** `ai_first_saas_core_app`
- **Purpose:** manage policies, approval rules, thresholds, simulations, behavior-change proposals, learning, and governed activation/rollback
- **Primary users:** policy owners, organization admins with governance capability, agent behavior stewards, approvers, auditors with read-only authority

## Invariants

```text
This workstream is backed by exactly one functional/context-area agent.
Surfaces are the only renderable workstream artifacts.
System messages are typed surfaces.
Every surface action, including read/query and surface-request actions, maps to a governed backend capability.
The workstream agent may request surfaces and guide users, but backend capabilities enforce authority.
```

Policies, thresholds, approval rules, prompt/skill governance rules, and authority changes are runtime business logic. Activation requires backend-enforced approval unless a narrow safe autonomous boundary is explicitly defined.

## User intents

The workstream agent must handle:

- `dashboard`, `show governance dashboard`
- `show policies`, `find approval policy`, `show active thresholds`
- `draft policy change`, `compare versions`, `explain this policy`
- `simulate policy`, `what would this change affect`, `run replay`
- `show proposals`, `approve proposal`, `reject proposal`, `request changes`
- `show deviations`, `show exceptions`, `why did this require approval`
- `rollback policy`, `activate version`
- help/how-to questions for governance, approvals, simulations, and learning loops

The agent may draft and analyze governance changes but cannot activate high-impact changes without explicit authorized approval.

## Required surfaces

| Surface id | Type | Purpose | Producing capability | Primary actions |
|---|---|---|---|---|
| `surface.governance_policy.dashboard.v1` | dashboard | pending proposals, active policies, exceptions, simulations, risky changes | `governance_policy.dashboard.view` | open policies, open proposal queue, open simulations, open exceptions |
| `surface.governance_policy.policy_list.v1` | data_table | policy/rule/threshold catalog | `governance_policy.policies.search` | open policy, filter, create draft |
| `surface.governance_policy.policy_detail.v1` | detail_card/version_card | active policy with clauses, scope, owner, authority, history | `governance_policy.policies.view` | draft change, compare, simulate, open audit |
| `surface.governance_policy.policy_diff.v1` | diff_review | proposed policy/threshold/approval change | `governance_policy.proposals.diff` | approve, reject, request changes, simulate |
| `surface.governance_policy.proposal_queue.v1` | data_table/decision_queue | pending governance proposals and approvals | `governance_policy.proposals.search` | open proposal, approve, reject |
| `surface.governance_policy.decision_card.v1` | decision_card | recommendation/evidence/risk/confidence/impact/alternatives/actions | `governance_policy.decisions.view` | approve, reject, request evidence, escalate |
| `surface.governance_policy.simulation_result.v1` | metric_panel/evidence_bundle | replay/simulation output for proposed change | `governance_policy.simulations.view` | rerun, approve proposal, open traces |
| `surface.governance_policy.exception_review.v1` | exception_card/audit_timeline | policy deviations, exceptions, override requests | `governance_policy.exceptions.search` | open exception, resolve, create learned rule proposal |
| `surface.governance_policy.learning_center.v1` | dashboard/list | feedback, learned rules, examples, proposed durable changes | `governance_policy.learning.view` | draft policy/skill proposal, dismiss, simulate |
| `surface.governance_policy.system_message.v1` | system_message | approval-required, denial, simulation started, activation result | capability-specific | retry, open trace, request approval |

## Surface style expectations

These surfaces inherit `ai-first-workstream-enterprise` from `../../../web-ui-style-guide.md`: calm enterprise workstream styling, named-theme tokens, neutral layered surfaces, blue/indigo AI accent, sparse semantic status colors, accessible focus states, strong version/table hierarchy, and prominent governance, authority, evidence, simulation, and decision cues. Style is a UI realization layer only; it must not change policy semantics, approval authority, capability mappings, activation/rollback rules, routes, simulations, or audit behavior.

- Dashboard: render as a governance command center with KPI cards for pending proposals, active policies, exceptions, simulations, risky changes, and approval SLA; place proposal/exception attention queues and high-impact change alerts above routine catalog summaries.
- Policy list and detail: use dense catalog rows/cards and layered version detail panels showing scope, owner, clauses, authority boundary, active/draft status, history, simulation readiness, and audit links with explicit read-only or unauthorized states.
- Policy diff and proposal queue: use enterprise diff-review and decision-queue layouts with version metadata, clause-level changes, risk/impact, required approvers, conflict-of-interest warnings, approval eligibility, and approve/reject/request-changes actions tied to traceable capabilities.
- Decision cards and exception reviews: render recommendation/evidence/risk/confidence/impact/alternatives as decision-card anatomy, with policy trigger badges, human-authority controls, learning options, escalation actions, and trace/outcome links.
- Simulation results: use metric/evidence panels with before/after impact summaries, replay scope, affected policies/prompts/tools, failures/uncertainty, trace links, rerun controls, and clear distinction between simulated evidence and activated policy state.
- Learning center: render feedback, examples, learned-rule candidates, and proposed durable changes as governed queues/cards with provenance, evidence, dismissal/proposal actions, and simulation-required status.
- System-message surfaces: use typed cards for approval required, denied authority, simulation started/complete, activation success/failure, rollback result, stale/reconnect, and trace-unavailable states with semantic icon/color plus text, recovery actions, and request-approval/open-trace affordances when authorized.

## Capability inventory and exposure channels

A capability is the governed backend contract. It may be exposed through one or more channels: surface action, browser API, workstream-agent tool, internal-agent tool, workflow step, timer, consumer, MCP tool, view, or internal method. Browser APIs and agent tools are exposure forms over the same capability; they do not redefine authorization, validation, idempotency, side effects, audit, approval, or denial behavior.

For this workstream, read/evidence, draft/proposal, simulation, and explanation capabilities may be exposed as workstream-agent tools so the Governance/Policy agent can answer conversational requests such as “draft a policy change” or “simulate this threshold”. Approval, activation, rollback, and authority-expanding capabilities require explicit backend-enforced approval and must not be silently invoked by agent conversation.

| Capability id | Class | Purpose | Side effects |
|---|---|---|---|
| `governance_policy.dashboard.view` | read/evidence | governance dashboard | read trace |
| `governance_policy.policies.search` | read/evidence | search policy catalog | read trace |
| `governance_policy.policies.view` | read/evidence | policy detail/version | read trace |
| `governance_policy.policies.draft` | proposal | draft policy/threshold/approval rule change | draft proposal, audit |
| `governance_policy.proposals.search` | read/evidence | proposal queue | read trace |
| `governance_policy.proposals.view` | read/evidence | proposal detail | read trace |
| `governance_policy.proposals.diff` | read/evidence | render diff/risk/impact | read trace |
| `governance_policy.proposals.approve` | approval | approve proposal | approval audit |
| `governance_policy.proposals.reject` | approval | reject proposal | rejection audit |
| `governance_policy.proposals.request_changes` | approval | return proposal for revision | audit/work trace |
| `governance_policy.proposals.activate` | governance/approval | activate approved policy/version | active version change, audit |
| `governance_policy.proposals.rollback` | governance/approval | rollback active policy/version | active version change, audit |
| `governance_policy.simulations.start` | workflow | run replay/simulation | simulation workflow/traces |
| `governance_policy.simulations.view` | read/evidence | view simulation result | read trace |
| `governance_policy.decisions.view` | read/evidence | view decision/approval card | read trace |
| `governance_policy.exceptions.search` | read/evidence | search exceptions/deviations | read audit |
| `governance_policy.exceptions.resolve` | command/approval | resolve exception | audit, possible learned-rule proposal |
| `governance_policy.learning.view` | read/evidence | feedback/learning candidates | read trace |
| `governance_policy.learning.propose_rule` | proposal | convert feedback into rule/policy proposal | proposal, audit |

## Authorization and policy

- Read-only governance visibility requires governance read or auditor capability.
- Draft proposals require governance edit/propose capability.
- Approval requires approver role/capability and conflict-of-interest checks where configured.
- Activation/rollback require approved proposal and activation capability.
- High-impact, security, billing, support-access, data-export, prompt/skill/tool-boundary, approval-rule, or autonomous-authority changes require human approval by default.
- Agent recommendations cannot self-approve expanded authority.
- All simulations and policy invocations are traceable.

## Workstream-agent prompt requirements

`workstream-agent/prompt.md` must define the agent as the governance and policy assistant. It must:

- explain active policies, approval rules, thresholds, exceptions, and simulation results;
- draft policy/proposal diffs with rationale, risk, impact, alternatives, tests, and rollback notes;
- identify required approvers and authority boundaries;
- recommend but not activate high-impact changes without approval;
- request surfaces for policy lists, detail, diffs, decisions, simulations, exceptions, and learning candidates;
- emit system-message surfaces for approval-required, denied authority, simulation-started, activation success/failure, and rollback results.

Runtime skills should cover policy authoring, approval gates, decision cards, simulation/replay, exceptions/deviations, learning loops, activation/rollback, and audit trace interpretation.

## Akka realization candidates

- ESE: `PolicyDocumentEntity`, `PolicyProposalEntity`, `ApprovalDecisionEntity`, `ExceptionEntity`, `LearnedRuleProposalEntity`.
- Workflow: proposal review/approval, simulation/replay, activation/rollback, exception resolution.
- Views: `PolicyCatalogView`, `ProposalQueueView`, `DecisionQueueView`, `SimulationResultView`, `ExceptionReviewView`, `LearningCandidateView`.
- Agent: `GovernancePolicyAgent`, optional policy reviewer/evaluator internal agent.
- Consumer: policy invocation trace enrichment, proposal/audit projection.
- Timed Action: periodic policy review reminders, stale proposal reminders, scheduled replay.
- HTTP: `/api/governance-policy/**` surface payload/action endpoints.

## Tests

Required:

- dashboard/policy list/detail/diff/proposal/decision/simulation/exception/learning surfaces render required states;
- draft policy change creates proposal, not active change;
- approval/activation required for high-impact changes;
- unauthorized user cannot approve/activate;
- conflict-of-interest denial where applicable;
- simulation workflow runs and produces trace-linked result;
- rollback restores prior active version and emits audit;
- exception resolution can produce learned-rule proposal;
- agent cannot self-approve authority expansion;
- all policy reads/actions/denials/activations emit audit/work traces;
- system-message surfaces redacted and actionable.

## Not ready if

- policies are static config without version/proposal/approval/audit lifecycle;
- prompt text is used to enforce policy instead of backend capabilities;
- activation bypasses approval;
- simulation/replay is missing for impactful changes;
- exception handling has no durable record;
- tests do not cover denied approval, activation, rollback, and traceability.
