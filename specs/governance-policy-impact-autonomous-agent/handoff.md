# Governance/Policy Impact AutonomousAgent Handoff

## Implemented vertical

The starter/reference template now includes a bounded Governance/Policy policy-change impact `AutonomousAgent` vertical. It is an internal/background worker for proposed governance or policy changes, not the request/response Governance/Policy workstream chat path and not a policy activation engine.

Implemented starter scope:

- governed capabilities: `governance.policy.impact_analysis.start`, `read`, `cancel`, `accept_result`, `reject_result`, and `request_changes`;
- concrete Akka worker path: `GovernancePolicyImpactAutonomousAgent`, `GovernancePolicyImpactTasks`, `GovernancePolicyImpactAutonomousAgentResult`, `ComponentClientGovernancePolicyImpactAutonomousAgentRuntime`, and durable task projection via `GovernancePolicyImpactTask`/repository/entity/service;
- v3 events: `workflow.governance_policy.impact_analysis.*` and shared `worker.task.*` lifecycle events with impact task, proposal, capability, trace, and optional `autonomous_task` source refs;
- attention: `attention:worker-task:<impactTaskId>:task-state` for blocked, failed, review-required, request-changes/rejected, accepted, and cancelled states;
- structured surfaces: `surface-governance-policy-impact-analysis-task` and `surface-governance-policy-impact-analysis-result` with backend-derived progress, blocker, result, review, redaction, evidence, trace, and action state;
- scaffold validation evidence in `validation.md` for targeted backend tests, frontend contract tests, typecheck/build, and focused guardrail scans.

## Boundaries

This vertical is advisory. Accepting, rejecting, or requesting changes on the impact result records a human review disposition only. It must not approve, reject, activate, roll back, mutate role/capability boundaries, change provider settings, or expand `ToolPermissionBoundary` authority. Consequential policy lifecycle actions remain separate governed backend capabilities with their own authorization, idempotency, audit/work traces, and approval gates.

## Runtime guardrails

Normal successful impact findings require the real Akka `AutonomousAgent` task path through the governed runtime adapter. Missing provider/model configuration, managed-agent profile, `ComponentClient` binding, evidence access, tool grant, `readSkill`, `readReferenceDoc`, authorization, or tenant/customer scope must fail closed with actionable blocked/provider-runtime state, v3 events, attention, and safe surfaces.

Do not mark policy impact analysis complete by using deterministic, canned, simulated, fixture, fake, or model-less successful findings. Deterministic governance simulation output may be cited only as scoped evidence; it is not a substitute for a model-backed impact result.

## Redaction and evidence

Evidence is scoped to the selected `AuthContext`, tenant, and optional customer. Browser-visible task and result surfaces must preserve redaction of raw prompt bodies, hidden prompt text, provider credentials, API keys, JWTs, raw tool payloads, support-only data, and cross-tenant/customer data. Findings should cite safe evidence/source/trace refs rather than exposing raw artifacts.

## Future work: policy simulation platform

A fuller policy simulation platform remains future work. It may add broader replay/evaluation loops, policy-as-code authoring, multi-policy scenario comparison, activation/rollback workflows, legal/compliance review packs, or long-running governance evaluation campaigns. Those features must be modeled as separate governed capabilities and workers; they are not implied by this bounded impact-analysis vertical.
