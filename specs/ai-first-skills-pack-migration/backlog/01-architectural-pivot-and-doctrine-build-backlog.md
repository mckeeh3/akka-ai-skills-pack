# Sprint 1 Build Backlog: Architectural Pivot and Doctrine

## Purpose

Make the AI-first target architecture explicit and authoritative enough that subsequent skill refactors can proceed consistently.

## Delivery goal

Future agents should understand that this repository now evolves toward AI-first SaaS application generation by default, while preserving existing Akka implementation skills as substrate capabilities.

## Suggested harness task breakdown

### 1. Canonical doctrine promotion

- task ID: `TASK-01-001`
- task brief: `../tasks/01-architectural-pivot-and-doctrine/01-canonical-doctrine-promotion.md`
- output: canonical `docs/` architecture/doctrine document distilled from inbox material.
- skills: none required beyond repo guidance; consult `akka-context` only if Akka semantics are asserted.

### 2. Repository guidance pivot

- task ID: `TASK-01-002`
- task brief: `../tasks/01-architectural-pivot-and-doctrine/02-repository-guidance-pivot.md`
- output: updates to repository-level guidance such as `AGENTS.md` and possibly `README.md` declaring the AI-first default.

### 3. Routing map AI-first entry update

- task ID: `TASK-01-003`
- task brief: `../tasks/01-architectural-pivot-and-doctrine/03-routing-map-ai-first-entry.md`
- output: `skills/README.md` updated with an AI-first SaaS routing section and transition notes.

### 4. Inbox provenance and cleanup plan

- task ID: `TASK-01-004`
- task brief: `../tasks/01-architectural-pivot-and-doctrine/04-inbox-provenance-cleanup-plan.md`
- output: documented inventory and disposition plan for `skills/inbox/docs/*`.

## Implementation order

Run tasks in listed order. The routing map should reference the canonical doctrine created by Task 1. The inbox cleanup plan should reference what was promoted or deferred.

## Done criteria

- All Sprint 1 task outputs exist.
- No application implementation code is changed.
- Temporary inbox files remain temporary unless explicitly promoted.
