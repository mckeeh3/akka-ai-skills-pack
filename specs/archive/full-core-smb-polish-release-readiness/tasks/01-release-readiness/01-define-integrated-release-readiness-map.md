# Task: Define full-core SMB integrated release-readiness map

## Objective

Inspect completed full-core workstream artifacts, starter source/test/docs boundaries, validation scripts, and release guidance. Produce an integrated release-readiness map and append bounded validation, polish, documentation, and handoff tasks.

## In scope

- Identify exact validation commands and source/doc areas.
- Define cross-workstream UX/navigation/trace/provider/no-secret review scope.
- Distinguish release blockers from intentional deferrals.
- Append bounded tasks with task briefs.

## Out of scope

- Do not implement fixes in this inspection task.
- Do not add new feature scope unless it is a release blocker.

## Expected outputs

- `specs/full-core-smb-polish-release-readiness/integrated-release-readiness-map.md`
- updated `pending-tasks.md`
- task briefs for appended validation/polish/doc/handoff tasks

## Checks

- `git diff --check`
- targeted `find`/`rg` discovery commands

## Commit message

- `full-core-smb: map polish release readiness`
