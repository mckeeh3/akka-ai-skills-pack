# Backlog: Governance/Policy Full-Core SMB

## Goal

Make Governance/Policy useful and safe for SMB operators managing policy posture, authority-changing proposals, simulations, approvals, exceptions, and decision evidence.

## Suggested harness task breakdown

1. Inspect source boundaries and produce `governance-policy-implementation-map.md` with vertical slice contracts and bounded source-edit tasks.
2. Implement governance dashboard and policy inventory/detail foundations.
3. Implement proposal draft/submit/read lifecycle.
4. Implement simulation/replay evidence and approve/reject/activate/rollback decision cards.
5. Implement GovernancePolicyAgent request/response guidance with provider fail-closed behavior.
6. Implement policy-impact analysis worker candidate if lifecycle foundations justify it.
7. Run targeted plus broad fullstack validation and terminal verification.

## Dependencies

- `specs/full-core-smb-saas-hardening/smb-full-core-baseline.md`
- `specs/full-core-smb-saas-hardening/workstream-full-core-outline.md`
- `specs/full-core-smb-saas-hardening/agent-worker-opportunities.md`
- `specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md`
- `specs/governance-policy-workstream-v0/workstream-contract.md`
- User Admin, Agent Admin, and Audit/Trace predecessor implementation maps for evidence, authority, behavior-change, and trace-link patterns

## Required check categories

- `git diff --check`
- source-boundary proof searches with `find`/`rg`
- backend tests for dashboard/inventory/proposal/simulation/decision reads and commands, authorization, tenant isolation, idempotency, provider blocked states, and traces
- frontend tests/typecheck/build for workstream surfaces when UI changes
- broad `tools/validate-ai-first-saas-starter-fullstack.sh` before completion

## Acceptance criteria

- Future implementation tasks can run without guessing source paths or validation commands.
- The final implemented Governance/Policy vertical is governed, scoped, trace-linked, redacted, visually polished, provider-fail-closed, and SMB-bounded.
