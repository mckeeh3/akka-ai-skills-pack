# Audit/Trace Summary AutonomousAgent Handoff

## Status

This mini-project defines the bounded Audit/Trace Summary AutonomousAgent vertical and validates the currently visible starter/reference assets. The implemented vertical evidence in this checkout is limited to:

- `specs/audit-trace-summary-autonomous-agent/audit-trace-summary-autonomous-agent-contract.md` for the governed task, evidence, event, attention, and surface contract;
- Audit/Trace frontend fixture/contract coverage for `audit.trace.summaryProgress.v1` and `audit.trace.summaryReview.v1` blocked/review surfaces;
- validation evidence in `specs/audit-trace-summary-autonomous-agent/validation/01-runtime-path-validation.md`.

Do **not** claim a completed backend runtime path from this state. Validation did not find backend classes named `AuditTraceSummaryAutonomousAgent` or `AuditTraceSummaryAutonomousAgentRuntime` in `src/main` or `src/test`.

## Runtime completion guardrails

A completed runtime must invoke a concrete Akka `AutonomousAgent` task through `ComponentClient.forAutonomousAgent(AuditTraceSummaryAutonomousAgent.class, ...).runSingleTask(...)` and read task state/results through `ComponentClient.forTask(...).get(...)`. Direct provider calls, canned summaries, deterministic service summaries, fixture-only output, simulated findings, or model-less successful results are not complete runtime behavior.

Provider fail-closed behavior is required. Missing governed model provider/profile, `AuditTraceSummaryAutonomousAgent` binding, `ToolPermissionBoundary` grants, governed loader tools such as `readSkill`/`readReferenceDoc`, or `auditTraceSummaryEvidence.read` must produce blocked provider/runtime state with actionable recovery, v3 events, and attention. It must not produce fake success.

## Evidence, redaction, and authority

Audit/Trace summary evidence must be tenant/customer scoped, AuthContext-authorized, browser-safe, and model-safe before it is used in prompts, tool results, result surfaces, or citations. Redaction must remove or mask raw JWTs, provider credentials, API keys, raw/hidden prompts, raw tool payloads, invitation tokens, support-only data, and cross-tenant/customer data.

Summary output is advisory. Human accept/reject records reviewer disposition only. The worker must not mutate audit records, traces, users, memberships, roles, provider configuration, prompt/skill/reference records, policies, redaction rules, or attention except through backend-derived task-state projections/events.

## Relationship to future digest work

This vertical is a bounded Audit/Trace summary worker, not a future digest platform. It can inform later scheduled digest/platform work only after the concrete AutonomousAgent runtime, scoped evidence loader, provider fail-closed path, v3 event/attention mapping, redaction rules, and surface validation are complete for this narrow worker.

Future digest platform work should create a separate contract and queue rather than broadening this mini-project into cross-workstream digest orchestration.

## Verification notes for future agents

Use these sources before changing status claims:

- contract: `specs/audit-trace-summary-autonomous-agent/audit-trace-summary-autonomous-agent-contract.md`
- validation: `specs/audit-trace-summary-autonomous-agent/validation/01-runtime-path-validation.md`
- queue: `specs/audit-trace-summary-autonomous-agent/pending-tasks.md`
- worker pattern: `docs/autonomous-agent-worker-runtime-pattern.md`
- example index: `specs/autonomous-agent-worker-pattern-extraction/examples-and-next-workers.md`

The next verification task should either confirm backend runtime evidence exists or append bounded follow-up implementation tasks before marking the mini-project complete.
