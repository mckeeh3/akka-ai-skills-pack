# TASK-ADR-01-002: Refresh shared foundation app-description artifacts

## Summary

Apply the findings from `TASK-ADR-01-001` to shared app/global/domain app-description artifacts so per-workstream refresh tasks inherit stable definitions and conventions.

## Scope

May edit:

- `app-description/app.md`
- `app-description/global/**`
- `app-description/domains/core-starter/domain.md`
- `app-description/domains/core-starter/capabilities/**`
- `app-description/domains/core-starter/data-state/**`
- `specs/app-description-refresh/**` notes/queue

Do not rewrite individual workstream directories except to add a narrowly justified shared reference note if blocking.

## Required reads

- `AGENTS.md`
- `app-description/AGENTS.md`
- `specs/app-description-refresh/README.md`
- `specs/app-description-refresh/shared-foundation-audit.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/app-description-component-graph.md`
- `.agents/skills/docs/app-worker-tool-model.md`
- `.agents/skills/docs/intent-to-realization-flow.md`
- `.agents/skills/core-saas-foundation/SKILL.md`

## Skills

- `app-descriptions`
- `app-description-auth-security`
- `app-description-observability`
- `app-description-capability-modeling`
- `core-saas-foundation`

## Expected outputs

- Refreshed shared app/global/domain current-intent artifacts.
- Shared naming and graph conventions recorded in `shared-foundation-audit.md` or a linked note.
- Queue update.

## Required checks

- `git diff --check`
- `rg -n "worker|actor adapter|governed tool|capability|tenant|Organization|trace|runtime-validation|source-alignment" app-description/app.md app-description/global app-description/domains/core-starter`

## Done criteria

- Shared artifacts are ready for per-workstream tasks.
- Workstream-local details remain in workstream directories.
- Material description changes that affect implementation are flagged for stale alignment in downstream workstream tasks or a clear shared note.
- Queue is updated and committed.

## Vertical workstream contract

- Lifecycle / readiness target: shared current-intent refresh, description-ready for workstream tasks.
- Workstream / functional agent: cross-cutting shared foundation scope.
- Governed-tool id and exposure: shared definitions only; no runtime exposure implemented.
- Capability id: core-starter shared capabilities.
- AuthContext / roles / tenant scope: define shared semantics.
- Akka substrate: app-description docs only.
- Audit/work trace requirements: define shared trace patterns.
- Local validation path: `git diff --check` plus graph vocabulary proof.
