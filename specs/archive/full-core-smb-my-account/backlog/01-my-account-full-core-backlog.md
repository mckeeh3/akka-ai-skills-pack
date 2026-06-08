# Backlog: My Account Full-Core SMB

## Goal

Make My Account useful and safe for signed-in SMB users who need to understand their account, selected context, authority, personal attention, and safe next steps.

## Suggested harness task breakdown

1. Inspect source boundaries and produce `my-account-implementation-map.md` with vertical slice contracts and bounded source-edit tasks.
2. Implement `/api/me`, selected context, authority summary, and lower-left user tile launch refinements.
3. Implement profile/settings surfaces and update lifecycle.
4. Implement personal attention aggregation, own trace refs, and safe workstream navigation.
5. Implement MyAccountAgent request/response guidance with provider fail-closed behavior.
6. Decide personal digest worker readiness if lifecycle foundations justify it.
7. Run targeted plus broad fullstack validation and terminal verification.

## Dependencies

- `specs/full-core-smb-saas-hardening/smb-full-core-baseline.md`
- `specs/full-core-smb-saas-hardening/workstream-full-core-outline.md`
- `specs/full-core-smb-saas-hardening/agent-worker-opportunities.md`
- `specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md`
- `specs/my-account-workstream-v0/workstream-contract.md`
- predecessor full-core implementation maps for User Admin, Agent Admin, Audit/Trace, and Governance/Policy attention/trace patterns

## Required check categories

- `git diff --check`
- source-boundary proof searches with `find`/`rg`
- backend tests for `/api/me`, context, settings updates, attention aggregation, navigation, authorization, tenant isolation, idempotency, provider blocked states, and traces
- frontend tests/typecheck/build for user tile, My Account surfaces, context indicator, denials, and no duplicate top-rail launcher
- broad `tools/validate-ai-first-saas-starter-fullstack.sh` before completion

## Acceptance criteria

- Future implementation tasks can run without guessing source paths or validation commands.
- The final implemented My Account vertical is governed, scoped, trace-linked, redacted, visually polished, provider-fail-closed, and SMB-bounded.
