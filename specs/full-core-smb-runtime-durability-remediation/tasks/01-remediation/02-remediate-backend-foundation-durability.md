# Task: Remediate backend foundation runtime durability

## Objective

Replace or fail-closed gate normal backend runtime use of Akka component-backed foundation repositories that currently back identity/account/membership, workstream log, audit trace, governance policy, and access-review task behavior.

## Required reads

- AGENTS.md
- specs/full-core-smb-runtime-durability-remediation/README.md
- specs/full-core-smb-runtime-durability-remediation/runtime-durability-remediation-map.md
- templates/ai-first-saas-starter/README.md
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/StarterSecurityComponents.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java

## In scope

- Normal runtime wiring in `StarterSecurityComponents` and `WorkstreamService` for:
  - `SubstituteIdentityRepository`
  - `SubstituteWorkstreamLogRepository`
  - `SubstituteAuditTraceRepository`
  - `SubstituteGovernancePolicyRepository`
  - `SubstituteAccessReviewTaskRepository`
- Tests and docs needed to prove durable or fail-closed behavior at the stated starter scope.
- Keep explicitly named test/local-demo adapters only where they cannot be mistaken for normal runtime completion.

## Out of scope

- Invitation and governed-agent behavior repository rebinding; use the separate backend seam task.
- Frontend fixture gating.
- Broad release handoff rewrite beyond queue notes if this task is incomplete.

## Expected outputs

- Backend source changes under `templates/ai-first-saas-starter/backend/src/main/java/`.
- Backend tests under `templates/ai-first-saas-starter/backend/src/test/java/`.
- README/doc copy updates if runtime behavior changes.
- Updated queue status.

## Required checks

- `git diff --check`
- Targeted source scan for remaining normal-runtime Akka component-backed foundation wiring:
  - `rg -n "new Substitute(Identity|WorkstreamLog|AuditTrace|GovernancePolicy|AccessReviewTask)|Substitute(Identity|WorkstreamLog|AuditTrace|GovernancePolicy|AccessReviewTask)Repository" templates/ai-first-saas-starter/backend/src/main/java`
- Rendered or targeted backend tests appropriate to changed paths; prefer kept scaffold command if template placeholders block Maven directly.

## Done criteria

- Normal generated backend runtime no longer silently relies on the listed Akka component-backed foundation repositories for completed behavior, or fails closed with actionable copy/traces where durable state is not yet implemented.
- Test-only/local-demo use is explicitly named and gated.
- Checks pass and changes are committed.

## Commit message

- `full-core-smb: remediate backend foundation durability`
