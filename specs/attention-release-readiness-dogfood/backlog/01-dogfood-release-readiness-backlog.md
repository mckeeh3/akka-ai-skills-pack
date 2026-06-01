# Backlog: Dogfood Release Readiness

## Goal

Validate the current attention-backed starter behavior as release-ready at its claimed scope, using both user dogfood observations and targeted repeatable checks.

## Task breakdown

1. Capture evidence and define smoke checklist.
2. Run fresh scaffold backend/frontend validation.
3. Run or document manual/runtime smoke checks for attention flows and edge cases.
4. Update release-readiness handoff/docs with current status and future-work boundaries.
5. Verify completion and append bounded blockers if needed.

## Required checks

Use targeted subsets of:

- `git diff --check`
- scaffolded backend Maven tests for attention services/producers/workstreams
- frontend tests/typecheck/build
- focused `rg` checks for backend-derived attention and stale docs
- manual smoke notes where browser/runtime checks are performed
