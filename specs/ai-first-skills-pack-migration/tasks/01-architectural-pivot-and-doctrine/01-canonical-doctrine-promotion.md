# Task Brief: Canonical AI-First Doctrine Promotion

## Task ID

`TASK-01-001`

## Objective

Create the canonical AI-first SaaS architecture/doctrine document for this skills pack by distilling temporary concept material, primarily `skills/inbox/docs/ai-first-saas-coding-agent-framework.md`.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `skills/inbox/docs/ai-first-saas-coding-agent-framework.md`
- `skills/inbox/docs/skills-pack-tech-stack.md`
- `skills/inbox/docs/ai-first-saas-ui-patterns.md` only for cross-reference awareness
- `specs/ai-first-skills-pack-migration/sprints/01-architectural-pivot-and-doctrine-sprint.md`
- `specs/ai-first-skills-pack-migration/backlog/01-architectural-pivot-and-doctrine-build-backlog.md`

## Scope

Create a canonical document under `docs/`, suggested path:

```text
docs/ai-first-saas-application-architecture.md
```

The document should explain:

- AI-first SaaS as the new default target architecture for generated apps.
- Difference between AI-first SaaS and CRUD apps with AI/chatbot features.
- Human roles: intent author, supervisor, reviewer/approver, exception handler, policy owner/coach, auditor, outcome owner.
- Durable substrate objects: goals, plans, agents, policies, decisions, evidence, traces, learning, outcomes.
- Required surfaces: goal-to-execution workbench, command center, decision cards, policy/governance center, async digest, audit/work trace.
- Akka + React/Vite/TypeScript as the implementation substrate.
- Anti-patterns and minimal checklist.

## Non-goals

- Do not create new skills yet.
- Do not update `AGENTS.md` or `skills/README.md`; later Sprint 1 tasks do that.
- Do not delete or archive inbox docs.
- Do not implement application code.

## Expected outputs

- `docs/ai-first-saas-application-architecture.md`

## Required checks

- Verify links in the new document point only to existing files or are clearly labeled as planned/future.
- Search for accidental broken relative links to non-existent skills.

## Done criteria

- The new doc is concise enough for future routing tasks to use.
- It clearly states the AI-first default and target stack.
- It treats inbox content as source material, not authority.
- `specs/ai-first-skills-pack-migration/pending-tasks.md` is updated from `pending` to `done` for `TASK-01-001` after completion.
