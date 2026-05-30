# Task: Decide Audit/Trace summary worker readiness

## Objective

Decide whether the scheduled audit-summary worker is justified after deterministic Audit/Trace foundations and AuditTraceAgent evidence tooling land; implement only the bounded blocked/readiness path that is supported by source reality.

## Required reads

- AGENTS.md
- specs/full-core-smb-audit-trace/README.md
- specs/full-core-smb-audit-trace/audit-trace-implementation-map.md
- specs/full-core-smb-saas-hardening/agent-worker-opportunities.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- specs/full-core-smb-user-admin-access-review-worker/access-review-worker-implementation-map.md
- docs/agent-component-selection-guide.md

## In scope

- Inspect the implemented deterministic Audit/Trace foundations and User Admin access-review worker seam.
- Record a bounded readiness decision in code/tests or queue notes.
- If no real worker is justified, keep `audit.trace.summaryTask.*` as blocked/provider-runtime surfaces with explicit recovery copy and traces.
- If a minimal deterministic task record is justified, implement only start/read/cancel/accept-result scaffolding with provider-blocked state; do not claim model-backed summary completion without a concrete provider-backed worker.
- Update tests/fixtures for whichever bounded decision lands.

## Out of scope

- Do not implement enterprise SIEM/anomaly detection.
- Do not let worker output mutate traces, policy, users, behavior records, or authorization state.
- Do not mark model-backed audit summary complete via canned deterministic output.

## Expected outputs

- Source/test changes only for the bounded readiness/blocked/task-record path selected.
- Queue notes if a separate future mini-project is required for a real AutonomousAgent audit-summary worker.
- Updated queue status.

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=WorkstreamServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-audit-trace-vertical.contract.test.mjs src/workstream-surfaces.contract.test.mjs
rg -n "audit\.trace\.summaryTask|audit summary|AutonomousAgent|blocked_provider_or_runtime|worker|no direct mutation|provider" templates/ai-first-saas-starter --glob '!**/node_modules/**'
git diff --check
```

## Done criteria

- The audit-summary worker path is either safely blocked/deferred or has a bounded deterministic task-record foundation with provider-blocked semantics.
- No user-facing worker success path is claimed unless it uses a real governed model-backed runtime.
- Follow-up work is queued or noted if a future AutonomousAgent implementation remains needed.

## Commit message

- `full-core-smb: decide audit trace worker readiness`
