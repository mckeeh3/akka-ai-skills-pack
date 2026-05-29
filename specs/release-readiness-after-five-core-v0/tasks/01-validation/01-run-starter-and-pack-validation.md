# Task: Run starter and pack validation

## Objective

Run the core release validation commands and record pass/fail/skip evidence.

## Required commands

- `tools/validate-ai-first-saas-starter-fullstack.sh`
- `bash tools/check-version-consistency.sh`
- `bash tools/build-pack.sh --clean --no-archive`
- `git diff --check`

## Guardrails

- Do not commit generated `dist/` artifacts.
- Fix only direct release blockers or append bounded blocker tasks.
