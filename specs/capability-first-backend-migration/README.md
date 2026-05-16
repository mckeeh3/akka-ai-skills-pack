# Capability-First Backend Migration Plan

This planning package coordinates the next migration phase of this repository: from a secure AI-first SaaS Akka skills pack into a skills pack that treats backend behavior as **capability-first architecture for agentic systems**.

The migration is planning-only until individual pending tasks are executed in fresh harness sessions. Each task should be small enough to complete, validate, and git commit in one session.

## Target architecture shift

The AI-first migration established secure SaaS, durable goals/plans, bounded agents, governance, audit, and outcomes as the default generated-application model.

This migration adds the backend substrate doctrine:

```text
product intent
→ secure SaaS foundation
→ capability inventory
→ authority, scope, schemas, side effects, audit, and approval rules
→ Akka component realization
→ selected exposure surfaces: agent tools, UI actions, workflow steps, APIs, MCP tools, timers, consumers
```

The source abstraction is **Capability**, not "agent tool". Agent tools are one exposure path for governed capabilities.

## Execution model

- Execute one task per fresh harness context.
- Use `pending-tasks.md` as the durable queue.
- Select the first `pending` task whose dependencies are satisfied.
- Start with Sprint 1 tasks. Later tasks may be refined by review sprints.
- Do not edit implementation/source guidance outside the selected task scope.
- Each task must end by making a git commit containing only that task's intended changes and the corresponding `pending-tasks.md` status update.
- Record the commit hash in the task notes when practical. If the hash cannot be embedded without amending the same commit, reference the commit message instead.
- Preserve task IDs; supersede obsolete tasks instead of renumbering or deleting them.

## Read order for future sessions

1. `AGENTS.md`
2. `skills/README.md`
3. `docs/ai-first-saas-application-architecture.md`
4. This file
5. The relevant sprint spec under `sprints/`
6. The matching backlog under `backlog/`
7. The selected task entry in `pending-tasks.md`
8. The task brief under `tasks/` when present
9. The smallest relevant skills/docs listed by the task

## Sprint sequence

1. `sprints/01-capability-first-doctrine-and-routing-sprint.md` — define the canonical capability-first doctrine, vocabulary, and top-level routing entry.
2. `sprints/02-description-and-decomposition-integration-sprint.md` — make app descriptions, PRD/spec intake, and Akka decomposition produce capability models before component selection.
3. `sprints/03-component-skill-reframing-sprint.md` — reframe entities, workflows, views, agents, endpoints, MCP, consumers, and timers as capability carriers/exposure surfaces.
4. `sprints/04-reference-examples-and-tests-sprint.md` — add or revise examples/tests that demonstrate read, consequential, workflow-backed, view-backed, MCP-exposed, and UI-reused capabilities.
5. `sprints/05-review-and-stale-content-cleanup-sprint.md` — review prior sprint progress, identify stale CRUD/component-first/tool-only content, and produce cleanup tasks.
6. `sprints/06-final-consistency-review-sprint.md` — verify the pack presents one coherent secure AI-first + capability-first doctrine.

## Backlog alignment

Each sprint has a matching backlog:

- `backlog/01-capability-first-doctrine-and-routing-build-backlog.md`
- `backlog/02-description-and-decomposition-integration-build-backlog.md`
- `backlog/03-component-skill-reframing-build-backlog.md`
- `backlog/04-reference-examples-and-tests-build-backlog.md`
- `backlog/05-review-and-stale-content-cleanup-build-backlog.md`
- `backlog/06-final-consistency-review-build-backlog.md`

## Migration principles

- Secure SaaS foundation remains mandatory.
- Capability-first does not mean exposing everything to agents.
- Tools are governed exposure surfaces, not authorization boundaries.
- Prompt instructions are not security controls.
- Every protected capability must mechanically enforce auth context, tenant/customer scope, permissions/capabilities, policy/approval gates, and audit/work traces.
- Prefer read-only tool exposure unless mutation is explicitly justified.
- Consequential capabilities should default to proposal/approval workflows unless policy grants autonomous authority.
- Capability shape should drive Akka component selection, not the reverse.

## Initial definition

A backend capability is a named, intentional domain operation or query with explicit:

- purpose and business meaning
- input and output schema
- actor/AuthContext requirements
- tenant/customer scope
- permission/capability requirements
- data access boundaries
- side effects and idempotency behavior
- audit/work-trace behavior
- approval/escalation policy
- autonomous-vs-human-supervised use rules
- exposure surfaces: agent tool, UI action, workflow step, HTTP/gRPC API, MCP tool/resource/prompt, timer, consumer, or internal-only component method

## Done state for this migration

The migration is complete when future agents consistently derive, document, implement, test, and expose backend behavior as governed capabilities before treating Akka components or agent tools as the primary design object.
