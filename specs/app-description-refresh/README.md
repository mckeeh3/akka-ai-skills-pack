# App-description Refresh Mini-project

## Purpose

Refresh the root `app-description/` for the runnable SaaS Foundation App so it reflects the current installed Akka AI skills-pack doctrine rather than the older skills-pack version that originally shaped the description.

This is a description-first semantic migration. It preserves accepted foundation app intent while revising the current-intent graph to the newer worker/tool/capability/source-alignment/runtime-validation model.

## Target scope

App-facing root assets:

- `app-description/**`
- `specs/app-description-refresh/**`
- `specs/runtime-validation/**` only when adding or updating scenario definitions for the refreshed description
- other `specs/**` only when needed to reconcile references or queue follow-up work

Implementation source, frontend source, and tests are read-only evidence for this mini-project unless a later task explicitly creates a separate implementation/remediation mini-project.

## Current-intent target

The refreshed app-description should preserve this graph chain for each feature-bearing foundation workstream:

```text
app -> domain -> workstream -> worker -> execution harness
  -> actor adapter -> governed tool -> capability
  -> Akka/frontend/API realization -> tests
  -> runtime-validation scenario -> traces/evidence
```

Each affected workstream should have explicit lifecycle/source-alignment state. Because the description will change materially, implementation alignment should default to `stale-description-changed` unless the task records a specific no-code-impact alignment review.

## Workstream migration strategy

Use a hybrid plan:

1. One shared umbrella refresh owns global/domain decisions and cross-workstream conventions.
2. One independent migration plan per foundation workstream owns local bindings and scoped revisions.
3. A cross-workstream consistency pass verifies shared roles, AuthContext, governed-tool ids, capability ids, traces, UI shell, and runtime-validation conventions.

Per-workstream migration plans live under `workstreams/`:

- `my-account-migration-plan.md`
- `user-admin-migration-plan.md`
- `agent-admin-migration-plan.md`
- `governance-policy-migration-plan.md`
- `audit-trace-migration-plan.md`

## Non-goals

- Do not rewrite `skills-pack/**`.
- Do not edit installed `.agents/skills/**` as project source.
- Do not implement runtime code while refreshing description files.
- Do not claim `runtime-ready` from description changes.
- Do not regenerate a separate app or parallel app-description root.
- Do not duplicate reusable skills-pack doctrine in app-description files; reference it and describe this app's commitments.

## Execution model

Execute one queued task per fresh harness context. Each task must:

1. mark exactly one task `in-progress` before edits;
2. execute only that task;
3. run required checks or block with a precise reason;
4. mark `done` only when checks and done criteria pass;
5. commit the task changes and queue update together;
6. report the next runnable task.

Parent orchestration should run queued tasks sequentially through `pi-subagents`; do not run workstream refresh tasks in parallel because they share global/domain conventions.

## Read order for future task sessions

1. `AGENTS.md`
2. `app-description/AGENTS.md`
3. `specs/AGENTS.md`
4. `.agents/skills/README.md`
5. `.agents/skills/docs/current-intent-model.md`
6. `.agents/skills/docs/app-description-component-graph.md`
7. `.agents/skills/docs/app-description-source-alignment.md`
8. `.agents/skills/docs/intent-to-realization-flow.md`
9. `.agents/skills/docs/intent-compiler-skill-contracts.md`
10. `.agents/skills/docs/runtime-validation.md`
11. `specs/app-description-refresh/README.md`
12. `specs/app-description-refresh/conversation-capture.md`
13. `specs/app-description-refresh/pending-tasks.md`
14. selected task brief and workstream migration plan

## Done state

This mini-project is complete when:

- shared app/global/domain app-description artifacts are refreshed to the current skills-pack graph contract;
- each of the five foundation workstreams is revised through its independent migration plan;
- workstream lifecycle/source-alignment files honestly mark implementation alignment and readiness;
- governed-tool, actor-adapter, capability, worker, trace, test, UI, API, and runtime-validation links are present or explicitly deferred;
- runtime-validation scenarios or scenario gaps exist for the refreshed foundation workstream behavior;
- active specs that depend on app-description semantics are reconciled or follow-up tasks are queued;
- a terminal verification task confirms no material refresh gaps remain, or appends bounded follow-up tasks plus a new terminal verification task.

## Current open concerns

- The existing implementation may lag the refreshed description; do not hide that drift. Mark affected workstreams `stale-description-changed` and queue alignment/compile/runtime-validation follow-up work when needed.
- Some older app-description files may already contain current concepts. Preserve useful accepted intent while replacing obsolete structure or vocabulary.
- Runtime-validation coverage may be incomplete. Description refresh tasks should add scenario definitions or queue scenario-authoring work rather than claiming runtime completion.
