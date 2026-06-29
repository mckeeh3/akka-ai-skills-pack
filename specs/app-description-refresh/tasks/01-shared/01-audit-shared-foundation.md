# TASK-ADR-01-001: Audit shared foundation contracts

## Summary

Audit shared root app-description artifacts against the current installed skills-pack graph contract and replace `shared-foundation-audit.md` placeholders with concrete findings.

## Scope

Read and assess shared artifacts only:

- `app-description/app.md`
- `app-description/global/**`
- `app-description/domains/core-starter/domain.md`
- `app-description/domains/core-starter/capabilities/**`
- `app-description/domains/core-starter/data-state/**`

Do not perform broad workstream rewrites in this task.

## Required reads

- `AGENTS.md`
- `app-description/AGENTS.md`
- `specs/app-description-refresh/README.md`
- `specs/app-description-refresh/shared-foundation-audit.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/app-description-component-graph.md`
- `.agents/skills/docs/app-description-source-alignment.md`
- `.agents/skills/docs/intent-compiler-skill-contracts.md`
- `.agents/skills/core-saas-foundation/SKILL.md`

## Skills

- `app-descriptions`
- `app-description-change-impact`
- `core-saas-foundation`

## Expected outputs

- Updated `specs/app-description-refresh/shared-foundation-audit.md` with concrete findings.
- Shared decisions/follow-up notes for the refresh sequence.
- Pending questions or blockers if shared semantics cannot be safely inferred.

## Required checks

- `git diff --check`
- Proof command showing shared files were considered, such as `find app-description/global app-description/domains/core-starter/capabilities app-description/domains/core-starter/data-state -type f | sort`.

## Done criteria

- Shared refresh gaps are explicit enough for `TASK-ADR-01-002` to edit shared app-description files without guessing.
- Queue is updated and committed.

## Vertical workstream contract

- Lifecycle / readiness target: description audit, non-runtime.
- Workstream / functional agent: cross-cutting shared foundation scope.
- Governed-tool id and exposure: none implemented; audit must identify shared governed-tool contract gaps.
- Capability id: shared foundation capabilities.
- AuthContext / roles / tenant scope: audit only.
- Akka substrate: docs/app-description only.
- Audit/work trace requirements: audit only; identify trace contract gaps.
- Local validation path: `git diff --check` plus coverage proof.
