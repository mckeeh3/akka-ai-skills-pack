# Sprint 9 Build Backlog: AI-First Packaging and Terminology Finalization

## Purpose

Finalize high-level AI-first migration consistency before moving into later Akka component-skill revisions.

## Delivery goal

The pack should present AI-first SaaS consistently in source and installed contexts, include all referenced high-level docs, avoid stale future/planned wording, and use `ai-first` terminology consistently in active files and paths.

## Suggested harness task breakdown

### 1. Refresh canonical doctrine skill references

- task ID: `TASK-09-001`
- output: updated `docs/ai-first-saas-application-architecture.md`.
- scope: replace stale “planned future AI-first skill files” wording with direct references to the existing AI-first entry and companion skills.

### 2. Remove source-only archive paths from installed-facing docs

- task ID: `TASK-09-002`
- output: updated docs that currently mention `specs/ai-first-skills-pack-migration/archive/inbox/...` in pack-facing contexts.
- scope: keep source provenance where useful for repository maintainers, but do not make installed-pack docs rely on source-only migration archive paths.

### 3. Package docs referenced by installed skills

- task ID: `TASK-09-003`
- output: updated `install.sh`, `pack/README.md`, and any packaging docs/tests needed.
- scope: ensure installer includes docs referenced by installed skills, including module sprint planning, security docs, frontend project integration, and UX pattern docs.

### 4. Regenerate or update release/dist metadata

- task ID: `TASK-09-004`
- output: updated pack version/release metadata and regenerated dist artifacts if this repository tracks them as current release output.
- scope: ensure `pack/manifest.yaml`, `dist/`, and any release notes/build info no longer represent pre-AI-first content as current.

### 5. Rename active `agent-first` files and directories to `ai-first`

- task ID: `TASK-09-005`
- output: renamed active files/directories whose names contain `agent-first`, plus reference updates.
- scope: likely includes `docs/examples/agent-first-dca-app-description/` to `docs/examples/ai-first-dca-app-description/`; do not rename archived provenance files unless explicitly needed.

### 6. Scan active content for `agent-first` wording and replace with `ai-first`

- task ID: `TASK-09-006`
- output: active docs/skills/specs updated for terminology consistency.
- scope: search all active files for `agent-first`, `Agent-first`, and `Agent-First`; replace with `ai-first`, `AI-first`, or `AI-First` as grammatically appropriate. Preserve historical wording only inside archived provenance/source material when intentionally retained.

## Implementation order

Run in listed order. Complete path renames before broad text replacement so references can be updated once.

## Done criteria

- All six tasks are represented in `pending-tasks.md`.
- Active pack-facing guidance uses AI-first consistently.
- Installed pack packaging includes referenced docs and current high-level migration content.
