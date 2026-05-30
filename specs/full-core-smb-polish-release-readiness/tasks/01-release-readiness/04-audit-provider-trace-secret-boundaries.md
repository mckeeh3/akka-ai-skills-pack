# Task: Audit provider, trace, navigation, and secret boundaries

## Objective

Verify that provider fail-closed behavior, trace/action/navigation semantics, and browser-visible secret/hidden-prompt boundaries remain coherent across the five-core SMB starter.

## Required reads

- `AGENTS.md`
- `specs/full-core-smb-polish-release-readiness/README.md`
- `specs/full-core-smb-polish-release-readiness/integrated-release-readiness-map.md`
- `specs/full-core-smb-polish-release-readiness/sprints/01-polish-release-readiness-sprint.md`
- `specs/full-core-smb-polish-release-readiness/backlog/01-polish-release-readiness-backlog.md`
- `specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md`
- `templates/ai-first-saas-starter/README.md`

## In scope

- Static scans for backend secrets, provider credentials, hidden prompt text, raw token fields, cross-tenant evidence, provider fallback claims, and model-less success paths.
- Inspect/request focused tests for `system_message`, provider-blocked, `ToolPermissionBoundary`, evidence tools, trace ids, and authorized workstream navigation.
- Record findings and append bounded blocker tasks when needed.

## Out of scope

- Do not expose real secrets or require provider credentials.
- Do not implement broad workstream fixes in this audit task.

## Expected outputs

- `specs/full-core-smb-polish-release-readiness/provider-trace-secret-audit.md`
- updated `pending-tasks.md` if bounded audit/fix tasks are needed

## Required checks

- targeted `rg` scans from `integrated-release-readiness-map.md`
- `env -u OPENAI_API_KEY tools/smoke-ai-first-saas-starter-real-model.sh`
- focused backend/frontend tests if a finding needs confirmation
- `git diff --check`

## Done criteria

- Audit covers provider fail-closed behavior, no deterministic/model-less normal runtime substitution, trace links, workstream navigation, evidence tools, browser static assets, frontend env files, and denial copy.
- Findings distinguish release blockers, environmental skips, non-blocking recommendations, and intentional deferrals.
- Task changes and queue update are committed.

## Commit message

- `full-core-smb: audit provider trace secret boundaries`
