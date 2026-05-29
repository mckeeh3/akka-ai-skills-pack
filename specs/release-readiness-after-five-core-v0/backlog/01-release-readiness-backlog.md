# Backlog: Release Readiness After Five-Core v0

## Goal

Turn the completed five-core v0/reference-runtime work into a release-ready repository state.

## Suggested harness task breakdown

1. Create the release-readiness planning scaffold.
2. Run starter and pack validation commands; fix only release-blocking issues discovered by those commands or append blocker tasks.
3. Test source install and installed scaffold dry-run/smoke behavior in disposable targets.
4. Review release-facing docs and write release-readiness handoff.
5. Verify the mini-project completion state.

## Required checks

- `tools/validate-ai-first-saas-starter-fullstack.sh`
- `bash tools/check-version-consistency.sh`
- `bash tools/build-pack.sh --clean --no-archive`
- source install into a disposable target
- installed scaffold dry-run, and non-dry-run only in a disposable empty target when safe
- `git diff --check`

## Acceptance criteria

- A maintainer can decide whether to run the actual release flow or what blocker must be fixed first.
