# Release Readiness After Five-Core v0

## Purpose

Prepare the repository for a release after completing the five-core v0 workstream series and reconciling the older core PRD input set.

The goal is to verify that the installable skills pack, starter scaffold, docs, examples, version references, and release handoff are coherent after the recent five-core v0 and source-of-truth work.

## Background

Completed prerequisite mini-projects:

- `specs/five-core-workstreams-v0-plan/`
- `specs/my-account-workstream-v0/`
- `specs/user-admin-workstream-v0/`
- `specs/agent-admin-workstream-v0/`
- `specs/audit-trace-workstream-v0/`
- `specs/governance-policy-workstream-v0/`
- `specs/core-prd-workstream-reconciliation/`

Those projects established the five-core v0 starter/reference runtime and clarified that `docs/examples/core-ai-first-saas-input/` is older module-sequenced full-core/detail provenance rather than the preferred current v0/starter rollout path.

## Scope

- Run pack-level release validation commands.
- Validate starter scaffold and installed-pack behavior in disposable targets.
- Check version consistency and packaging output.
- Review release-facing docs for stale guidance caused by the five-core v0 changes.
- Produce a release-readiness handoff that says whether the repository is ready to cut a new version or what blocks release.

## Non-goals

- Do not implement full-core hardening features in this queue.
- Do not change release version or create tags unless a task explicitly scopes version bump/release execution and the user approves it.
- Do not broaden into a whole-repository cleanup unless validation finds a concrete release blocker.

## Affected repository areas

- `README.md`
- `docs/skills-pack-user-guide.md`
- `docs/skills-pack-developer-guide.md`
- `docs/examples/**`
- `pack/**`
- `templates/ai-first-saas-starter/**`
- `frontend/**` mirror/source sync as needed
- `tools/**`
- `dist/` only as generated validation output; do not commit generated dist artifacts unless release policy explicitly changes

## Execution model

Execute one task per fresh harness session. Every completed task should make one focused commit with queue status updates. Validation tasks should record exact commands and whether they passed, skipped, or failed.

## Read order for future task sessions

1. `AGENTS.md`
2. `skills/README.md`
3. this mini-project's `README.md`
4. this mini-project's `conversation-capture.md`
5. this mini-project's `pending-tasks.md`
6. selected sprint/backlog/task brief
7. only the exact docs/tools/source files listed by the task

## Done state

This mini-project is complete when:

- starter fullstack validation passes or a concrete blocker task exists;
- version consistency passes or a concrete blocker task exists;
- pack build succeeds without committing generated `dist/` artifacts;
- source install and installed scaffold dry-run/smoke behavior are validated in disposable targets;
- release-facing docs have been reviewed for stale five-core v0/core PRD guidance;
- a release-readiness handoff exists with pass/fail/skip evidence and recommended next action.
