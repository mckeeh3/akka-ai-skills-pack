# Task: Run integrated full-core SMB validation

## Objective

Run broad and focused validation for `templates/ai-first-saas-starter/`, record reproducible results, and classify any failures as release blockers, environmental blockers, or non-blocking follow-ups.

## Required reads

- `AGENTS.md`
- `specs/full-core-smb-polish-release-readiness/README.md`
- `specs/full-core-smb-polish-release-readiness/conversation-capture.md`
- `specs/full-core-smb-polish-release-readiness/integrated-release-readiness-map.md`
- `specs/full-core-smb-polish-release-readiness/sprints/01-polish-release-readiness-sprint.md`
- `specs/full-core-smb-polish-release-readiness/backlog/01-polish-release-readiness-backlog.md`
- `templates/ai-first-saas-starter/README.md`

## In scope

- Run or explicitly block on the mapped validation commands.
- Capture command, result, provider-env mode, and useful failure summary.
- Append bounded fix tasks before the terminal verification task if a release blocker is found.

## Out of scope

- Do not implement broad fixes in this validation task unless they are tiny documentation corrections needed to record the result.
- Do not weaken validation to pass.

## Expected outputs

- `specs/full-core-smb-polish-release-readiness/validation-results.md`
- updated `specs/full-core-smb-polish-release-readiness/pending-tasks.md` if blockers are found

## Required checks

- `tools/validate-ai-first-saas-starter-fullstack.sh`
- `tools/prove-workstream-icons-v0.sh`
- `env -u OPENAI_API_KEY tools/smoke-ai-first-saas-starter-real-model.sh`
- targeted backend/frontend commands from `integrated-release-readiness-map.md` as needed to isolate failures
- `git diff --check`

## Done criteria

- Validation results are reproducible from the recorded commands.
- Any failed command has a release-blocker/environmental/non-blocking classification.
- Queue is updated with bounded blocker tasks when needed.
- Task changes and queue update are committed.

## Commit message

- `full-core-smb: run integrated validation`
