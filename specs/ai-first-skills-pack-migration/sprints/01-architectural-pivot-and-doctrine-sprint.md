# Sprint 1: Architectural Pivot and Canonical Doctrine

## Sprint goal

Declare the repository's target architecture shift: applications produced by this skills pack are now AI-first SaaS applications by default, implemented on the Akka + React/Vite/TypeScript substrate.

## Dependencies

- User-approved direction to evolve this repository in place.
- Temporary concept input in `skills/inbox/docs/`.

## Scope

- Promote a canonical AI-first SaaS architecture/doctrine document from the inbox framework.
- Update repository-level maintainer guidance and routing docs so future work interprets the pack as AI-first by default.
- Document inbox provenance and cleanup policy without deleting source material yet.
- Keep all changes planning/docs/routing focused; do not implement new application code.

## Key inputs

- `AGENTS.md`
- `skills/README.md`
- `skills/inbox/docs/ai-first-saas-coding-agent-framework.md`
- `skills/inbox/docs/skills-pack-tech-stack.md`
- `skills/inbox/docs/ai-first-saas-ui-patterns.md`

## Expected outputs

- Canonical architecture/doctrine doc under `docs/`.
- Repo guidance updates identifying AI-first as the default target application model.
- Routing-map update that introduces the future AI-first entry path without requiring all companion skills to exist yet.
- Inbox provenance/cleanup plan.

## Implementation task groups

1. Canonical doctrine promotion.
2. Repo guidance pivot.
3. Routing map update.
4. Inbox provenance and cleanup plan.

## Acceptance behavior

- A future agent reading `AGENTS.md` and `skills/README.md` understands that AI-first SaaS is now the default target architecture.
- The canonical doc distinguishes AI-first SaaS from chatbot-enhanced CRUD.
- Inbox docs are clearly treated as temporary reference material until promoted, merged, archived, or removed by specific tasks.

## Done criteria

- Sprint 1 pending tasks are completed and marked done.
- No non-planning source files are modified except approved docs/skill routing guidance.
- No inbox file is silently treated as authoritative without promotion.

## Defer list

- Creating the full AI-first skill family.
- Refactoring app-description skills.
- Refactoring PRD/spec/backlog planning skills.
- Reframing component implementation skills.
- Creating the DCA worked example.
