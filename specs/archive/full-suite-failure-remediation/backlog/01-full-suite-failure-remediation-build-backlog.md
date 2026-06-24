# Full Suite Failure Remediation Build Backlog

## Backlog goal

Make the project's normal frontend/backend full-suite checks reliable by resolving pre-existing failures documented during recent terminal verification.

## Implementation order

### FSFR-01: Reproduce and classify current failures

- Run frontend and backend suites or targeted equivalent commands.
- Produce `failure-inventory.md` with exact current failures, reproduction commands, suspected root cause, owner cluster, and repair task mapping.

### FSFR-02: Repair frontend surface intent routing contract

- Fix the frontend contract failure around destructive/approval-gated prompts staying on safe fallback.
- Preserve deterministic router-first semantics and no-mutation guarantees.

### FSFR-03: Repair Governance/Policy lifecycle and attention cluster

- Reconcile GovernancePolicyService state transitions with tests and app-description/current intent.
- Fix browser/workstream smoke and attention producer failures if they share the same lifecycle-state root cause.
- Split if the cluster proves too broad.

### FSFR-04: Repair Agent Admin artifact read/redaction mismatch

- Fix `agentAdminCatalogDetailAndArtifactReadsAreBackendAuthoritativeAndRedacted` by aligning implementation/test/current intent around backend-authoritative artifact reads and redacted surface ids.

### FSFR-05: Repair User Admin status/support-access cluster

- Fix member status disable/reactivate no-op vs accepted behavior.
- Fix User Admin browser smoke failures around support-access grant and system-message coverage.
- Preserve self-action and last-admin safeguards.

### FSFR-06: Repair MeService bootstrap audit capability mismatch

- Reconcile `saas_owner.audit.read` vs `audit.trace.read` role/capability expectations for bootstrap admin links.
- Preserve local authorization semantics and `/api/me` frontend-safe output.

### FSFR-07: Repair runtime seam/autonomous/browser smoke cluster

- Address concrete Akka workstream runtime agent seam test.
- Address Audit/Trace summary autonomous runtime fail-closed test.
- Address My Account browser smoke TestKit runtime errors.
- Split into follow-up tasks if the real runtime integration scope is too large.

### FSFR-08: Terminal full-suite verification

- Run final checks.
- Append follow-up tasks and another terminal verification if failures remain.

## Design guardrails

- Prefer fixing implementation over weakening tests.
- If tests are stale, cite current intent and update tests to the accepted behavior.
- Do not replace real runtime expectations with deterministic fake runtime behavior.
- Do not expand scope into unrelated product features.
