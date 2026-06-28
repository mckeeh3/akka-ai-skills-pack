# Agent Admin Doc Editing Realization

## Initiative

Revise the core app frontend and backend so Agent Admin matches the current app-description: a SaaS-admin-only AI-assisted agent-document editing workspace for prompts, skills, and skill reference docs.

## Current intent source

The authoritative product intent is the app-description committed in `4f6ddfb4 Reframe Agent Admin as AI-assisted doc editing`, especially:

- `app-description/domains/core-starter/capabilities/agent-doc-administration.md`
- `app-description/domains/core-starter/workstreams/agent-admin/**`
- `app-description/domains/core-starter/data-state/managed-agent-behavior-state.md`
- `app-description/global/tools/foundation-governed-tools.md`

## Problem

The existing code and tests were previously oriented around a governance-heavy Agent Admin model: behavior proposals, prompt-risk review, seed imports, model refs, tool boundaries, activation, rollback, and tenant/org scoped governance. That no longer matches current intent.

The implementation must be revised to support the simpler but runtime-real path:

1. SaaS admin lists agents.
2. SaaS admin opens an agent.
3. SaaS admin opens a prompt, skill, or skill reference doc.
4. SaaS admin gives free-form edit instructions.
5. An editing agent proposes Markdown-preserving full document content.
6. SaaS admin iterates, then Save or Cancel.
7. Save creates a new current immutable version immediately used at runtime.
8. Runtime agents load current prompt + skill descriptions and may call `readSkill` / `readReferenceDoc`.
9. Runtime doc reads are traced and visible in Agent Admin.

## Done state

This mini-project is complete when local code, tests, and app-description realization maps prove the Agent Admin doc-editing workstream through the real app paths at the stated scope:

- backend authorization denies non-SaaS-admin Agent Admin access;
- backend stores and retrieves agent prompt, skill, and skill reference docs with simple integer versions;
- current-version-only editing, Save, Cancel, version browsing, adjacent-version diff, and restore work;
- skill create/delete and reference-doc create/delete work with the required permanence semantics;
- editing agent flow is integrated through a concrete model-backed/fail-closed runtime path and test provider path, not as normal-runtime deterministic fake success;
- runtime agent loading uses current prompt + skill names/descriptions and supports audited `readSkill` / `readReferenceDoc`;
- frontend renders blank state, optional dashboard, agent list, agent detail, prompt doc, skill doc, reference doc, edit session, version history/diff, create/delete confirmations, and runtime traces;
- existing stale governance/prompt-risk/seed/tool-boundary Agent Admin UI and tests are removed, hidden, or explicitly not treated as current intent;
- unit/contract/integration checks pass for the implemented scope;
- final verification confirms no material gaps remain or appends bounded follow-up tasks plus a new terminal verification task.

## Non-goals

- Do not implement tenant/org/customer-scoped Agent Admin.
- Do not create or delete whole agents.
- Do not add model settings administration.
- Do not add tool permission administration.
- Do not keep separate activation/publish/rollback lifecycle in Agent Admin.
- Do not treat prompt-risk approval gates, seed import, or old behavior proposal queues as current Agent Admin user-facing behavior.
- Do not edit `skills-pack/**` for this mini-project.

## Execution model

- Execute one task per fresh harness context.
- Use `pending-tasks.md` as the durable queue.
- Future task sessions must mark exactly one task `in-progress`, perform only that task, run required checks, mark `done` only if checks pass, and commit the implementation plus queue update together.
- If a task discovers material gaps, mark it blocked or append follow-up tasks according to the terminal verification loop.
- The parent orchestrator should use `pi-subagents` to launch exactly one fresh-context worker subagent at a time.

## Suggested implementation order

1. Backend document administration service and domain contract.
2. Durable versioned prompt/skill/reference-doc state and lifecycle.
3. Editing-agent draft/revise/save/cancel flow.
4. Runtime prompt/skill/reference loading and read traces.
5. Workstream/API action wiring.
6. Frontend types, fixtures, surfaces, and router updates.
7. Stale governance UI/test cleanup.
8. Full-stack validation and terminal verification.

## Required checks by area

Choose the smallest checks that prove each task. Common checks include:

```bash
git diff --check
mvn test
mvn -Dtest='*Agent*Doc*,*AgentAdmin*' test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
```

A task brief may narrow checks to targeted tests when appropriate.
