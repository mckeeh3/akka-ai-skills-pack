# Conversation Capture: App-description Refresh

## Discussed idea

The current foundation `app-description/` was implemented using older versions of the Akka AI skills pack. The currently installed skills pack has significant improvements that warrant a complete revision of the current app-description.

## Accepted direction

Use a controlled app-description re-baseline, not a blind delete/regenerate:

- preserve accepted foundation app intent;
- refresh to the current file-backed current-intent graph model;
- explicitly model workers, execution harnesses, actor adapters, governed tools, capabilities, traces, tests, realization, source alignment, and runtime-validation;
- revise app-description first, then compile/align implementation in later work;
- mark implementation alignment honestly when description changes make code stale.

## Workstream planning decision

The best approach is a hybrid:

- one umbrella plan for shared foundation/global/domain contracts;
- one independent migration plan per foundation workstream;
- one cross-workstream consistency/readiness pass.

Fully independent workstream migrations without a shared umbrella were rejected because shared actors, roles, policies, tools, capabilities, UI shell conventions, traces, AuthContext semantics, and runtime-validation setup could diverge.

## Foundation workstreams in scope

- My Account
- User Admin
- Agent Admin
- Governance/Policy
- Audit/Trace

## Explicit non-goals

- No skills-pack source maintenance in this mini-project.
- No runtime source implementation in the description-refresh tasks.
- No `runtime-ready` claims from description-only work.
- No parallel queued task execution.

## Planning outcome

Create `specs/app-description-refresh/` with:

- umbrella docs;
- per-workstream migration plans;
- backlog/task briefs;
- a sequential pending-task queue;
- a terminal verification task that can append follow-up tasks if gaps remain.
