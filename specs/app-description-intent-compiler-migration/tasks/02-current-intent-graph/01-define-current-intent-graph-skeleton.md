# TASK-ADICM-02-001: Define current-intent graph skeleton

## Purpose

Reconstruct the root `app-description/` skeleton around the current intent compiler graph model.

## Required reads

- `AGENTS.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/intent-compiler-skill-contracts.md`
- `.agents/skills/docs/description-first-application-doctrine.md` if needed for description/current-state principles
- `specs/app-description-intent-compiler-migration/README.md`
- `specs/app-description-intent-compiler-migration/source-inventory.md`
- `specs/app-description-intent-compiler-migration/sprints/02-current-intent-graph-sprint.md`
- `specs/app-description-intent-compiler-migration/backlog/01-app-description-intent-compiler-migration-build-backlog.md`

## Expected outputs

- reconstructed `app-description/` directory skeleton using app/global/domain/workstream shape
- top-level `app-description/app.md`
- core starter domain directory and initial `domain.md`
- global directories and minimal index/README notes as needed
- migration note, if needed, that does not make archived legacy docs active authority

## Required checks

- `git diff --check`
- `find app-description -maxdepth 4 -type d | sort` review output showing graph shape
- focused `rg` proof that active graph names the core starter and does not duplicate foundation implementation doctrine wholesale

## Done criteria

- Future tasks can populate graph nodes without relying on old path taxonomy.
- The selected core starter domain/workstream naming is stable.
- Active docs describe current intent, not migration chronology.
- Changes and queue update are committed.

## Vertical workstream contract

- Workstream / functional agent: docs-only current-intent graph foundation for core starter
- Attention category or non-attention reason: non-runtime graph skeleton
- Role-specific dashboard / surface: none
- Surface graph node/action edge: none
- Governed-tool id and exposure: none
- Capability id: app-description graph shape
- AuthContext / roles / tenant scope: graph must preserve tenant/customer assumptions at app/domain level
- Akka substrate: docs/specs only
- API / frontend / realtime path: none
- Audit/work trace requirements: describe selected trace commitments at graph level where applicable
- Local validation path: `git diff --check` plus graph-shape proof
