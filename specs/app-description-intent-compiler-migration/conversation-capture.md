# Conversation Capture: App-description Intent-Compiler Migration

## Trigger

The user identified a chicken-and-egg problem for the secure multi-tenant AI-first SaaS core starter:

- the initial rough core workstreams were created before the intent compiler skills and documentation were fully developed;
- the current `app-description/` tree appears to contain an older structured description that mixes reusable foundation concepts with starter-app workstreams;
- the skills pack now carries the reusable foundation understanding, so root `app-description/` should not duplicate that foundation implementation doctrine;
- the current starter implementation in `src/`, `frontend/`, tests, docs, and specs is a second important source of truth for reconstructing the starter app description.

## Accepted observations

- The intent compiler captures user intent into both non-code current-intent artifacts and generated/runtime implementation artifacts.
- The root core starter needs intent-compiler-aligned app-description/spec layers before further full implementation can be reliably driven.
- The current `app-description/` should be treated as legacy/provenance input, not final authority.
- Reusable foundation knowledge should live in `skills-pack/` and installed skill docs; root `app-description/` should reference selected foundation capabilities and commitments rather than re-document all foundation implementation knowledge.
- The reconstructed app-description should describe the secure multi-tenant AI-first SaaS core starter's selected capabilities, domains, workstreams, surfaces, agents, tools, policies, traces, tests, and realization mapping.
- Temporary traceability to archived legacy app-description docs is acceptable during migration, but active content must be scrubbed of archive dependencies once the migration is complete and the temporary archive should be removed.

## Recommended approach to implement

1. Archive the current `app-description/` tree as temporary migration input.
2. Inventory the current starter implementation in `src/`, `frontend/`, tests, docs, and specs.
3. Rebuild `app-description/` using the current intent compiler graph structure.
4. Keep reusable foundation doctrine in skills-pack docs and only reference/apply it in root app-description artifacts.
5. Establish traceability from legacy description and implementation sources during migration.
6. Reconcile specs/backlogs/tasks against the new current-intent graph.
7. Remove the temporary archive and scrub active content so the final app-description is current-state authority, not a historical ledger.

## Important constraint

The archive is temporary migration scaffolding only. Future tasks must not leave active app-description/spec content depending on archived legacy files after the migration verification task completes.
