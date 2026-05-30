# Backlog: Agent Admin Full-Core SMB

## Goal

Make Agent Admin useful and safe for SMB operators who need to inspect, understand, and govern managed-agent behavior without hidden prompts, code edits, or unsafe authority expansion.

## Suggested harness task breakdown

1. Inspect source boundaries and produce `agent-admin-implementation-map.md` with vertical slice contracts and bounded source-edit tasks.
2. Implement Agent catalog/detail dashboard and governed configuration reads.
3. Implement prompt/skill/reference/manifest/model/tool-boundary visibility and redacted browser DTOs.
4. Implement behavior-change draft/review/activate/cancel/rollback lifecycle at SMB scope.
5. Implement AgentAdminAgent request/response guidance with provider fail-closed behavior.
6. Implement behavior-review/prompt-risk worker candidate if lifecycle foundations justify it.
7. Run targeted plus broad fullstack validation and terminal verification.

## Dependencies

- `specs/full-core-smb-saas-hardening/smb-full-core-baseline.md`
- `specs/full-core-smb-saas-hardening/workstream-full-core-outline.md`
- `specs/full-core-smb-saas-hardening/agent-worker-opportunities.md`
- `specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md`
- `specs/agent-admin-workstream-v0/workstream-contract.md`
- User Admin predecessor implementation maps for evidence/worker patterns where useful

## Required check categories

- `git diff --check`
- source-boundary proof searches with `find`/`rg`
- backend tests for reads, proposals, lifecycle, authorization, tenant isolation, provider blocked states, traces, redaction, and tool-boundary enforcement
- frontend tests/typecheck/build for workstream surfaces when UI changes
- broad `tools/validate-ai-first-saas-starter-fullstack.sh` before completion

## Acceptance criteria

- Future implementation tasks can run without guessing source paths or validation commands.
- The final implemented Agent Admin vertical is governed, scoped, trace-linked, visually polished, provider-fail-closed, and SMB-bounded.
