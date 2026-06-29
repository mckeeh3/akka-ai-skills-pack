# Capability: Governance policy lifecycle

## Purpose

Let authorized SaaS owners, tenant admins, policy operators, auditors, and scoped support users govern foundation policy lifecycle for AI-first SaaS behavior: policy catalog visibility, effective-policy reads, draft proposals, simulation evidence, human decision-card approval/denial, activation, exceptions, rollback, history, and runtime policy-decision evidence.

Governance/Policy is the human-governed control plane for behavior-shaping policy. Policy changes that affect runtime behavior are versioned, simulated where consequential, reviewed on decision cards, activated only through authorized human decisions, and traceable after rollback or exception handling. It is not a prompt-only guardrail, enterprise rule-language engine, autonomous policy-commit path, legal compliance suite, or place to override platform security.

## Actors and scope

- `saas-owner-admin`: manages SaaS/default policy versions, approves SaaS-wide changes, activates approved defaults, grants/revokes SaaS-scope exceptions, and authorizes SaaS-wide rollback decisions.
- `tenant-admin`: reads effective policies for the selected tenant, drafts tenant-scoped business-governance changes, approves/activates tenant-owned changes where policy allows, requests/reviews tenant exceptions, and initiates tenant rollback decisions.
- `policy-operator`: prepares drafts, runs simulations, assembles decision-card evidence, requests approvals, and executes approved activation/rollback/exception actions only when the selected `AuthContext` grants matching capability.
- `auditor`: reads authorized policy versions, drafts, simulations, decisions, exceptions, rollback records, runtime-decision evidence, history, and trace links; audit access alone does not allow mutation or approval.
- `support`: reads scoped tenant policy state and history only under active support access and backend-scoped context.
- `governance-policy-agent`: explains, searches, summarizes, drafts proposals, prepares simulations, assembles decision-card evidence, and drafts exception/rollback plans under bounded authority. It cannot approve, activate, roll back, grant exceptions, mutate active policy state, or override hard platform controls.
- `governance-policy-system-worker`: deterministically validates policy lifecycle commands, resolves effective policy, executes authorized draft/simulation/decision/activation/rollback/exception operations, publishes runtime policy state, and emits traces.

All reads, writes, agent turns, surface actions, confirmed chat plans, bounded agent tool calls, workflow steps, runtime internal checks, and governed-tool invocations use backend-owned selected `AuthContext`, tenant/customer/account ids where applicable, membership status, role/capability grants, support-access state, and hard platform-security checks. Browser-provided policy ids, tenant/customer/account ids, filters, scopes, and route params are untrusted hints.

## Governed tools and exposure

Canonical capability id: `governance-policy-lifecycle`.

- `governance.policy.search` (`surface_action`, bounded `agent_tool_call`, `api_call`, `internal_call` read): read visible policy catalog, active/draft/rolled-back versions, pending approvals, simulation findings, exceptions, rollback availability, and history summary.
- `governance.policy.read` (`surface_action`, bounded `agent_tool_call`, `api_call`, `internal_call` read/evaluate): read active version, drafts, clauses/values, scope precedence, exception state, runtime decision semantics, rollback targets, and decision evidence.
- `governance.policy.draft` (`surface_action`, bounded `agent_tool_call`, confirmed `human_chat_tool_plan`, `api_call`, `workflow_step` proposal): create or update a versioned policy proposal without active runtime mutation.
- `governance.policy.simulate` (`surface_action`, bounded `agent_tool_call`, confirmed `human_chat_tool_plan`, `api_call`, `workflow_step`, `internal_call` simulation): evaluate draft/rollback/exception candidates and produce evidence, changed outcomes, risk/impact/confidence, approval-gate requirements, and partial-failure findings without activation.
- `governance.policy.submit_for_approval` (`surface_action`, confirmed `human_chat_tool_plan`, `api_call`, `workflow_step` workflow): move a valid draft and evidence bundle into human review with a decision-card item.
- `governance.policy.approve` (`surface_action`, `api_call`, `workflow_step` approval decision): record approve, reject, request-evidence, modify, defer, escalate, or exception-required decisions; activation remains separate.
- `governance.policy.activate` (`surface_action`, confirmed `human_chat_tool_plan`, `api_call`, `workflow_step`, `internal_call` commit): activate an approved policy version and publish runtime enforcement state.
- `governance.policy.rollback` (`surface_action`, confirmed `human_chat_tool_plan`, `api_call`, `workflow_step`, `internal_call` rollback): restore a prior approved version or revoke a problematic exception through a rollback decision.
- `governance.policy.review_exception` (`surface_action`, confirmed `human_chat_tool_plan`, `api_call`, `workflow_step` exception review): grant, deny, revoke, expire, or request evidence for scoped policy exceptions.
- `governance.policy.read_history` (`surface_action`, bounded `agent_tool_call`, `api_call`, `internal_call` read): read authorized policy changes, decisions, simulations, exceptions, rollback records, and runtime outcome links.

Side-effecting lifecycle tools are never exposed as autonomous `agent_tool_call`s. Confirmed chat-plan execution requires exact plan-snapshot confirmation, selected `AuthContext`, backend reauthorization, catalog membership, required reason, approval state when needed, idempotency, freshness, and trace emission. Activation, rollback, and consequential exception commits additionally require matching decision-card approval state.

Legacy simple-settings ids `governance.policy.list`, `governance.policy.read_effective`, `governance.policy.set_default`, `governance.policy.set_override`, and `governance.policy.reset_override` are aliases only when mapped into the lifecycle tools above. They are not separate direct-commit authority.

## Foundation policy categories

Current foundation policy categories are:

- `agent_tool_authority`: which functional/internal agents may call governed tools through `agent_tool_call`, which tools require human confirmation, and which scopes are allowed.
- `approval_gate`: risk, confidence, impact, authority-expansion, and human-review thresholds.
- `exception_policy`: bounded temporary deviations with owner, scope, expiry, reason, evidence, reviewer, and trace links.
- `runtime_enforcement_policy`: simple boolean/counter/limit settings used by protected runtime actions.
- `model_and_governed_document_policy`: activation rules when prompts, skills, references, rubrics, or model policy changes affect runtime behavior or authority.
- `trace_retention_and_visibility`: audit/work trace visibility, redaction, retention, and downstream enforcement evidence, subordinate to hard platform controls and Audit/Trace permissions.

Domain-specific business policies remain extension-owned until a business-domain extension adds them to its domain app-description. Unsupported policy ids/categories/scopes/value types and complex policy scripts are denied or routed to extension guidance rather than invented.

## Lifecycle contract

1. **Catalog/read:** authorized actors inspect active policy versions, pending drafts, exceptions, simulation findings, rollback candidates, and decision history.
2. **Draft:** actors or the Governance/Policy agent create versioned `PolicyProposal` records with scope, clauses/values, rationale, risk, affected workstreams/tools/roles, required reason, idempotency key, and trace reference. Drafts do not affect runtime.
3. **Simulate:** simulation/replay evaluates draft/rollback/exception candidates against selected current-policy state, representative traces, affected action/tool/workstream mappings, known exceptions, and approval gates. Evidence gaps produce partial-failure findings rather than silent success.
4. **Decide:** reviewers act on decision cards to approve, reject, request evidence, modify/counterpropose, defer, escalate, mark exception-required, activate an approved version where allowed, or start rollback. Recommendation and final human decision are separate durable facts.
5. **Activate:** approved versions activate under a policy-version transaction boundary. Activation records old/new versions, approval ref, affected scopes, runtime publication status, history, trace evidence, and partial-publication state when needed.
6. **Exception:** authorized reviewers grant, deny, revoke, or expire scoped, time-bounded exceptions only where policy allows. Expired exceptions never authorize runtime behavior.
7. **Rollback:** authorized reviewers restore a prior approved version or revoke a problematic exception through a separate rollback decision card. Rollback appends history rather than deleting prior state.
8. **Runtime enforcement/evidence:** downstream workstreams cite policy-decision traces when policy affects action availability, tool execution, approval routing, denials, exception use, or rollback recovery.

## Authorization and denials

Tenant admins may manage only tenant-scoped business-governance policy where selected context grants it. SaaS owners may manage SaaS/default scope but must not silently overwrite tenant-owned approved versions or exceptions. Auditors and support users are read-only unless a separate selected role/capability grants mutation.

Disabled users, inactive memberships, missing context, missing capability, missing reviewer authority, separation-of-duty violation, cross-tenant/customer/account access, hidden scope targets, unsupported policy ids/types/scopes, missing required reason, stale policy version, failed simulation precondition, unapproved activation, expired/invalid exception, and hard-platform-security override attempts are denied server-side.

Denied writes and reads return safe `system_message`, result, or partial-failure surfaces with no hidden scope enumeration and emit policy-denial traces.

## Idempotency, transactions, and result behavior

Draft, approval request, decision, activation, rollback, and exception commands require idempotency keys. Replays return the existing result without duplicate drafts, decisions, commits, history, traces, runtime outcome links, or attention items.

Activation and rollback are single policy-version transaction boundaries. Result surfaces distinguish committed, not-committed, partial-publication, idempotent replay, stale/conflict, denied, and failed states. Exception grant/revoke/expire is an exception-state transaction boundary. Read operations are idempotent except for optional read/policy-decision trace evidence.

## Non-overridable controls

Governance/Policy cannot override tenant isolation, backend authorization, frontend secret boundaries, JWT/provider-key protection, raw prompt/model/provider payload protection, redaction boundaries, audit trace integrity, required human-governance gates for authority expansion, platform integrity checks, or secret-never-store behavior.

Browser payloads never return raw prompts, provider secrets, hidden authority state, JWTs, raw tool payloads, raw correlation/idempotency internals, cross-tenant evidence, or raw model/provider payloads.

## Outcomes

In scope:

- policy dashboard, catalog, detail/effective read, draft, simulation, decision-card, exception, rollback, history, result, partial-failure, and system-message surfaces;
- bounded functional-agent read/draft/simulation assistance and no-mutation plan proposals;
- confirmed human chat plans for catalog-bound lifecycle actions only after exact confirmation and backend checks;
- policy lifecycle, decision, denial, runtime enforcement, partial-failure, exception, rollback, and history traces;
- readiness for focused build/compile tasks while retaining `stale-description-changed` implementation alignment until runtime validation proves the real Akka/API/UI path.

Out of scope:

- complex policy scripting;
- legal compliance workflow suites;
- unbounded autonomous policy commits;
- enterprise delegation models not represented in app-description;
- any request to override hard platform security controls.

## Linked graph nodes

- Workstream: `../workstreams/governance-policy/workstream.md`
- Access: `../workstreams/governance-policy/access.md`
- Behavior: `../workstreams/governance-policy/behavior.md`
- Workers: `../workstreams/governance-policy/workers/`
- Agent binding: `../workstreams/governance-policy/agents/functional-agent.md`
- Tools: `../workstreams/governance-policy/tools/governed-tools.md`
- Surfaces: `../workstreams/governance-policy/surfaces/surfaces.md`
- Policies: `../workstreams/governance-policy/policies/policy-bindings.md`
- Traces: `../workstreams/governance-policy/traces/work-traces.md`
- Tests: `../workstreams/governance-policy/tests/coverage.md`
- Realization/source alignment: `../workstreams/governance-policy/realization/source-alignment.md`
