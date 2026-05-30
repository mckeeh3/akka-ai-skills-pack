# Backlog: Full-Core SMB Polish and Release Readiness

## Goal

Prepare the full-core SMB starter for release with coherent product quality, validation evidence, documentation, and handoff.

## Suggested harness task breakdown

1. Inspect current full-core specs/source/validation and produce `integrated-release-readiness-map.md` with bounded fix/review/doc tasks.
2. Execute integrated validation and append/fix blockers.
3. Review visual UX and cross-workstream shell/surface consistency; fix bounded issues.
4. Verify provider fail-closed, trace links, no-secret/no-hidden-prompt browser boundaries, and docs claims.
5. Update README/template/user guidance and release handoff.
6. Terminal verification.

## Required check categories

- `tools/validate-ai-first-saas-starter-fullstack.sh`
- targeted backend/frontend checks from affected workstreams
- `rg`/static scans for provider secrets, hidden prompts, model-less fallback, trace links, system messages, and deferred workers
- `git diff --check`

## Acceptance criteria

- Future tasks can run without guessing source paths or validation commands.
- Release handoff gives a clear ship/no-ship recommendation with evidence.
