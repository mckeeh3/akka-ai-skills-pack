# Skills Review and Cleanup Plan

## Purpose

Review the current `skills/` tree after multiple major revisions and bring the source skills into tighter alignment with the pack's current goals:

- secure AI-first SaaS by default;
- mandatory core SaaS foundation;
- agent workstream application model;
- capability-first backend modeling;
- description-first app maintenance path;
- intent-driven Akka decomposition path;
- focused Stage 3 implementation skills;
- low-token, agent-optimized routing guidance.

This plan is for maintaining this repository as the source project for the `akka-ai-skills-pack`. It is not an end-user application build plan.

## Operating model

- Execute one task per fresh harness session.
- Each task must be self-contained and must leave the repository in a coherent state.
- Each completed task must make one git commit containing only the task's intended changes and its queue-status update.
- Do not perform broad rewrites outside the task scope.
- Treat source files under `skills/` and `docs/` as authoritative for edits.
- Treat `.agents/` as installed-pack validation material only when a task explicitly asks for installed-pack parity checks.
- Preserve natural-language routing: users should not need to know the skill taxonomy.

## Recommended execution order

1. Routing map audit
2. Planning/spec/backlog audit
3. Agent governance audit
4. App-description boundary audit
5. Web UI/auth/foundation audit
6. Akka component family audit
7. Reference/package wording cleanup
8. Final consistency, installed-pack parity, routing smoke checks, and completion summary

The durable queue is `pending-tasks.md` in this directory.

## Family findings baseline

| Family | Current assessment | Main risk | Cleanup intent |
|---|---|---|---|
| Routing map and top-level doctrine | Mostly aligned | Dense and duplicative front-door guidance | Clarify canonical routing and reduce ambiguity |
| Planning/spec/backlog/queues | Aligned but heavy | Procedural drift and repeated doctrine | Preserve lifecycle and tighten boundaries |
| Agent governance | Strong but overlapping | Too many adjacent governance skills without a crisp decision matrix | Add/clarify routing matrix and handoffs |
| App-description | Strong and coherent | Boundary drift among intake/change/readiness/UI/security/test skills | Clarify ownership and routing between layers |
| Web UI/auth/foundation | Strongly aligned | Possible overlap with HTTP/core foundation and legacy page-first mechanics | Keep workstream-first and security-first guidance explicit |
| Akka component skills | Mostly aligned | Low-level skills may drift from capability-first framing | Ensure orchestrators carry capability/auth/test context without overloading focused skills |
| Reference examples/package policy | Useful but potentially confusing | `com.example` reference paths could be mistaken for generated package guidance | Normalize wording around examples and generated package selection |

## Review status labels

Use these labels in task notes when summarizing a reviewed skill or family:

- `keep-as-is`
- `minor-alignment-edit`
- `routing-clarification-needed`
- `merge-split-candidate`
- `deprecation-candidate`
- `reference-path-cleanup-needed`

## Completion definition

This cleanup plan is complete when:

- all seven ordered cleanup tasks plus the closure tasks are `done` or explicitly `superseded` with rationale;
- each completed task has a corresponding git commit;
- top-level routing, planning, agent governance, app-description, web UI/foundation, component, and reference/package guidance tell one consistent story;
- installed-pack parity and representative routing smoke checks have been completed or explicitly deferred with rationale;
- `completion-summary.md` records the outcome and release-readiness recommendation;
- no task has introduced generated-application implementation work unrelated to skills-pack maintenance.
