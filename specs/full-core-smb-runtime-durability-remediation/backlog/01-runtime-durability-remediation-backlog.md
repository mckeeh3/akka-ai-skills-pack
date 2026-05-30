# Backlog: Runtime Durability Remediation

## Goal

Make the full-core SMB starter honest and production-like at the stated scope by removing, replacing, or gating in-memory/fixture/demo normal runtime paths.

## Suggested harness task breakdown

1. Inspect all in-memory/mock/fixture/demo paths and produce `runtime-durability-remediation-map.md`.
2. Correct release-readiness handoff/docs to mark the no-in-memory remediation as a blocker if needed.
3. Replace or gate backend in-memory normal runtime repositories in bounded groups.
4. Replace or gate in-memory trace/agent behavior stores and runtime trace sinks.
5. Gate frontend fixture/demo inspection and clean stale generated static assets.
6. Re-run integrated validation and produce updated release handoff.
7. Terminal verification.

## Required check categories

- `rg` inventory scans for `InMemory`, `mock`, `fake`, `fixture`, `demo`, `canned`, `model-less`, `fallback`, and static generated assets.
- targeted backend tests for durable/fail-closed runtime behavior.
- targeted frontend tests for fixture gating and normal API path behavior.
- `tools/validate-ai-first-saas-starter-fullstack.sh` after source remediation.
- `git diff --check`.

## Acceptance criteria

- Future source-edit tasks can run without guessing source paths or validation commands.
- Release-readiness status is corrected until the stronger durability bar is met.
