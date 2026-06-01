# Audit/Trace Summary AutonomousAgent Handoff

## Status

This mini-project now has a bounded concrete Audit/Trace Summary AutonomousAgent reference vertical at the local starter/example scope. Current evidence:

- contract: `specs/audit-trace-summary-autonomous-agent/audit-trace-summary-autonomous-agent-contract.md`;
- concrete Akka component: `src/main/java/com/example/application/AuditTraceSummaryAutonomousAgent.java`;
- runtime adapter: `src/main/java/com/example/application/ComponentClientAuditTraceSummaryAutonomousAgentRuntime.java`;
- fail-closed runtime: `src/main/java/com/example/application/FailClosedAuditTraceSummaryAutonomousAgentRuntime.java`;
- typed task/result/rule/projection/evidence tool records under `src/main/java/com/example/application/AuditTraceSummary*`;
- backend-derived events, attention, and progress/review surface mapping: `src/main/java/com/example/application/AuditTraceSummarySurfaces.java`;
- targeted backend tests: `AuditTraceSummaryRuntimeGuardrailTest`, `AuditTraceSummaryAutonomousAgentIntegrationTest`, and `AuditTraceSummarySurfaceMappingTest`;
- validation evidence: `specs/audit-trace-summary-autonomous-agent/validation/03-concrete-runtime-path-validation.md`.

The bounded worker is ready for terminal verification. Do not broaden this status into a general scheduled digest platform or enterprise audit export capability.

## Runtime completion guardrails

Normal success invokes a concrete Akka `AutonomousAgent` task through `ComponentClient.forAutonomousAgent(AuditTraceSummaryAutonomousAgent.class, ...).runSingleTask(...)` and reads task state/results through `ComponentClient.forTask(...).get(...)`. Direct provider calls, canned summaries, deterministic service summaries, fixture-only output, simulated findings, or model-less successful results remain invalid completion evidence.

Provider fail-closed behavior remains required. Missing governed model provider/profile, `AuditTraceSummaryAutonomousAgent` binding, `ToolPermissionBoundary` grants, governed loader tools such as `readSkill`/`readReferenceDoc`, or `auditTraceSummaryEvidence.read` must produce blocked provider/runtime state with actionable recovery, v3 events, and attention. It must not produce fake success.

## Evidence, redaction, and authority

Audit/Trace summary evidence is tenant/customer scoped, AuthContext-authorized by contract, browser-safe, and model-safe before it is used in task instructions, tool results, result surfaces, or citations. Redaction omits or masks raw JWTs, provider credentials, API keys, raw/hidden prompts, raw tool payloads, invitation tokens, support-only data, and cross-tenant/customer data. Hidden or unauthorized refs use `not_found_or_redacted`.

Summary output is advisory. Human accept/reject records reviewer disposition only. The worker must not mutate audit records, traces, users, memberships, roles, provider configuration, prompt/skill/reference records, policies, redaction rules, or attention except through backend-derived task-state projections/events.

## Validation notes

Latest validation (`TASK-ATSA-08-001`) passed:

- `mvn -Dtest=AuditTraceSummaryRuntimeGuardrailTest,AuditTraceSummaryAutonomousAgentIntegrationTest,AuditTraceSummarySurfaceMappingTest test`;
- `cd frontend && node --test src/workstream-audit-trace-vertical.contract.test.mjs`;
- `cd frontend && npm run typecheck`;
- `cd frontend && npm run build`;
- focused `rg` evidence for concrete runtime, `ComponentClient` task invocation/read, provider fail-closed, scoped redaction, events, attention, surfaces, and no fake success.

No browser-driven manual smoke was run; the local runtime validation is Akka TestKit-backed for this bounded reference scope. Runtime startup still logs the pre-existing `AdminUserBootstrap` `TENANT_ADMIN` enum parsing exception during targeted tests, but Maven exits with `BUILD SUCCESS` and the issue is outside this Audit/Trace worker.

## Relationship to future digest work

This vertical is a bounded Audit/Trace summary worker, not a future digest platform. It can inform later scheduled digest/platform work only as a pattern source for concrete AutonomousAgent runtime, scoped evidence loading, provider fail-closed behavior, v3 event/attention mapping, redaction rules, and surface validation.

Future digest platform work should create a separate contract and queue rather than broadening this mini-project into cross-workstream digest orchestration.

## Verification notes for future agents

Use these sources before changing status claims:

- contract: `specs/audit-trace-summary-autonomous-agent/audit-trace-summary-autonomous-agent-contract.md`
- latest validation: `specs/audit-trace-summary-autonomous-agent/validation/03-concrete-runtime-path-validation.md`
- queue: `specs/audit-trace-summary-autonomous-agent/pending-tasks.md`
- worker pattern: `docs/autonomous-agent-worker-runtime-pattern.md`
- backend runtime/tests listed in the status section above

The next runnable task is terminal verification: `TASK-ATSA-99-002`.
