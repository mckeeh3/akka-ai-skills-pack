# Build Backlog: App-description Intent-Compiler Migration

## Source current intent

- `specs/app-description-intent-compiler-migration/conversation-capture.md`
- `.agents/skills/docs/intent-compiler.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/intent-to-realization-flow.md`
- `.agents/skills/docs/intent-compiler-skill-contracts.md`

## Backlog items

### ADICM-01: Archive legacy app-description as temporary migration input

Create a temporary migration archive of the current `app-description/` tree and a source manifest describing that the archive is not active authority.

Suggested task: `TASK-ADICM-01-001`.

### ADICM-02: Inventory implementation and source evidence

Inventory root backend, frontend, tests, docs, active specs, and legacy app-description evidence. Classify each finding as one of:

- reusable foundation doctrine to reference from skills-pack docs;
- core starter current intent to capture in root app-description;
- stale/demo/legacy content to exclude;
- drift requiring a pending question or future runtime/spec repair task.

Suggested task: `TASK-ADICM-01-002`.

### ADICM-03: Define target current-intent graph skeleton

Replace or reconstruct `app-description/` around the current intent model's app/global/domain/workstream graph shape. Establish stable domain/workstream ids and graph conventions.

Suggested task: `TASK-ADICM-02-001`.

### ADICM-04: Populate core starter workstream graph

Populate the secure multi-tenant AI-first SaaS core starter domain and five core workstreams:

- My Account;
- User Admin;
- Agent Admin;
- Audit/Trace;
- Governance/Policy.

Each workstream should bind access, behavior, surfaces, agents, tools, policies, traces, tests, and realization notes.

Suggested task: `TASK-ADICM-02-002`.

### ADICM-05: Populate realization and traceability mappings

Map current-intent nodes to known Akka/frontend/API/test artifacts at a description/spec level. Do not implement runtime gaps in this task. Queue follow-up implementation work when material gaps are found.

Suggested task: `TASK-ADICM-02-003`.

### ADICM-06: Reconcile active specs/readiness/backlogs with new graph

Update active specs, readiness docs, and relevant queues to reference the new app-description graph shape. Supersede stale path references and preserve active pending-task semantics.

Suggested task: `TASK-ADICM-03-001`.

### ADICM-07: Scrub legacy archive dependencies and remove temporary archive

Remove or isolate the temporary archive and scrub active content so archived docs are not treated as product authority.

Suggested task: `TASK-ADICM-04-001`.

### ADICM-08: Terminal migration verification

Verify current task group and overall mini-project done state. Append new bounded tasks plus a new terminal verification task if material gaps remain.

Suggested task: `TASK-ADICM-04-002`.
