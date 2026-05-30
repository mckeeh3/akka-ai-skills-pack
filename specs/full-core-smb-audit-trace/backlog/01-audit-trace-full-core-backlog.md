# Backlog: Audit/Trace Full-Core SMB

## Goal

Make Audit/Trace useful and safe for SMB operators investigating denials, admin changes, behavior changes, model/tool/provider failures, worker task results, and related traces.

## Suggested harness task breakdown

1. Inspect source boundaries and produce `audit-trace-implementation-map.md` with vertical slice contracts and bounded source-edit tasks.
2. Implement audit dashboard and trace search/detail/timeline foundations.
3. Implement redacted evidence/failure cards and cross-workstream trace links.
4. Implement AuditTraceAgent request/response explanation guidance with provider fail-closed behavior.
5. Implement scheduled audit-summary/anomaly worker candidate if lifecycle foundations justify it.
6. Run targeted plus broad fullstack validation and terminal verification.

## Dependencies

- `specs/full-core-smb-saas-hardening/smb-full-core-baseline.md`
- `specs/full-core-smb-saas-hardening/workstream-full-core-outline.md`
- `specs/full-core-smb-saas-hardening/agent-worker-opportunities.md`
- `specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md`
- `specs/audit-trace-workstream-v0/workstream-contract.md`
- User Admin and Agent Admin predecessor implementation maps for trace producers and link patterns

## Required check categories

- `git diff --check`
- source-boundary proof searches with `find`/`rg`
- backend tests for search/detail/timeline reads, authorization, tenant isolation, redaction, correlation, provider blocked states, and trace links
- frontend tests/typecheck/build for workstream surfaces when UI changes
- broad `tools/validate-ai-first-saas-starter-fullstack.sh` before completion

## Acceptance criteria

- Future implementation tasks can run without guessing source paths or validation commands.
- The final implemented Audit/Trace vertical is governed, scoped, trace-linked, redacted, visually polished, provider-fail-closed, and SMB-bounded.
