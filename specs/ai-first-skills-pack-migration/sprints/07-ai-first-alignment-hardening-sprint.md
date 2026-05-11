# Sprint 7: AI-First Alignment Hardening

## Sprint goal

Close post-migration alignment gaps so the source repository, installed pack, routing docs, planning skills, app-description lifecycle skills, examples, and packaging all consistently support AI-first SaaS as the default target architecture.

## Dependencies

- Sprints 1 through 6 complete.
- `docs/ai-first-saas-application-architecture.md` exists as canonical doctrine.
- AI-first skill family exists under `skills/ai-first-saas*`.
- DCA worked example exists under `docs/examples/agent-first-dca-app-description/`.

## Scope

This sprint is a hardening and consistency sprint. It should not introduce broad new doctrine unless a gap requires it. Prefer targeted fixes that make the already-created AI-first concepts usable from both the source repository and installed pack.

Primary alignment areas:

1. Packaging and installed-pack guidance.
2. Skill/doc path correctness.
3. Core flow docs.
4. App-description lifecycle skills.
5. Leaf planning and queue-maintenance skills.
6. DCA example and gap-document consistency.
7. Planning the first executable AI-first implementation slice.

## Acceptance behavior

- Installing the pack includes the AI-first doctrine, AI-first skill family, and worked example references needed by installed skills.
- Required-read paths in skill files resolve correctly from skill directories in source and after install-time rewriting.
- Core flow docs teach AI-first interpretation before CRUD/component decomposition.
- Readiness, change-impact, generation, and summary skills check AI-first operating-model completeness when applicable.
- Slice/backlog/task/question follow-on skills preserve AI-first constraints rather than stripping them.
- DCA example/gap docs agree on current coverage and app-description structure.
- A future sprint can implement the first executable AI-first reference slice from a concrete plan.

## Done criteria

- All Sprint 7 pending tasks are `done` or explicitly superseded/deferred with rationale.
- Each task has a git commit as required by the queue rules.
- No unrelated application implementation code is changed.
- Remaining AI-first gaps are explicitly documented rather than hidden.

## Explicit defer list

- Implementing the executable AI-first reference slice itself, unless the final planning task explicitly creates a separate future sprint/task queue for that work.
- Broad redesign of the skill taxonomy beyond targeted alignment.
- Removing low-agentic purchase-request examples; they remain useful contrast/reference material.
