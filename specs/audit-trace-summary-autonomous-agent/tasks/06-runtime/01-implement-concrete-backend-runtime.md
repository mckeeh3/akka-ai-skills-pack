# TASK-ATSA-06-001: Implement concrete Audit/Trace summary backend runtime

## Objective

Implement the missing backend runtime path for the bounded Audit/Trace Summary AutonomousAgent vertical.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/audit-trace-summary-autonomous-agent/README.md`
- `specs/audit-trace-summary-autonomous-agent/audit-trace-summary-autonomous-agent-contract.md`
- `specs/audit-trace-summary-autonomous-agent/audit-trace-summary-handoff.md`
- `specs/audit-trace-summary-autonomous-agent/validation/02-completion-verification.md`
- `specs/audit-trace-summary-autonomous-agent/tasks/06-runtime/01-implement-concrete-backend-runtime.md`
- `docs/autonomous-agent-worker-runtime-pattern.md`
- existing AutonomousAgent worker examples under `src/main/java/com/example/application` and matching tests

## Skills

- `akka-autonomous-agents`

## In scope

- `AuditTraceSummaryAutonomousAgent` component extending Akka `AutonomousAgent` with typed task/result records and result validation.
- `AuditTraceSummaryAutonomousAgentRuntime` or equivalent backend adapter using `ComponentClient.forAutonomousAgent(...).runSingleTask(...)` and `ComponentClient.forTask(...).get(...)`.
- Durable/browser-safe task projection for queued/running/blocked/failed/completed-review-required/cancelled/accepted/rejected states.
- Scoped/redacted evidence loader for `auditTraceSummaryEvidence.read` with tenant/customer/AuthContext checks and `not_found_or_redacted` behavior.
- Provider/model/profile/runtime/tool-boundary/evidence-read missing config maps to `BLOCKED_PROVIDER_OR_RUNTIME`, never deterministic/model-less success.
- Backend tests for start/read/cancel/accept/reject auth, idempotency, tenant isolation, redaction, fail-closed behavior, result validation, and no fake success.

## Non-goals

- Do not build a general digest platform.
- Do not mutate audit records, users, roles, memberships, provider configuration, policies, prompts, skills, or references from summary output.
- Do not satisfy normal success with canned, deterministic, fixture, simulated, or direct-provider output.

## Required checks

- `git diff --check`
- targeted backend Maven tests for the new runtime/fail-closed/redaction/idempotency behavior
- focused `rg` proving concrete `AuditTraceSummaryAutonomousAgent`, runtime adapter, `ComponentClient.forAutonomousAgent`, `ComponentClient.forTask`, fail-closed, redaction, and no fake success guardrails

## Commit message

`audit-summary-agent: implement concrete runtime`
