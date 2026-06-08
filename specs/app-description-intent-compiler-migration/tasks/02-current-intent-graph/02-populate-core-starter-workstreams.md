# TASK-ADICM-02-002: Populate core starter workstream graph

## Purpose

Populate current-intent graph nodes for the secure multi-tenant AI-first SaaS core starter domain and its five core workstreams.

## Required reads

- `AGENTS.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/intent-to-realization-flow.md`
- `.agents/skills/docs/ai-first-saas-application-architecture.md`
- `.agents/skills/docs/core-ai-first-saas-foundation.md`
- `.agents/skills/docs/foundation-layer-coverage-matrix.md`
- `specs/app-description-intent-compiler-migration/README.md`
- `specs/app-description-intent-compiler-migration/source-inventory.md`
- `specs/app-description-intent-compiler-migration/sprints/02-current-intent-graph-sprint.md`
- current `app-description/app.md` and domain/global skeleton from `TASK-ADICM-02-001`

## Expected outputs

- domain capabilities for the core starter
- data-state nodes for selected durable app objects
- workstream nodes for My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy
- access/behavior/surface/agent/tool/policy/trace/test bindings for each workstream at current-intent level
- updated queue status and notes

## Required checks

- `git diff --check`
- focused `rg` proof that all five core workstreams have workstream, access, behavior, tests, and realization coverage
- focused `rg` proof that app-description references foundation docs/concepts without copying large reusable doctrine blocks

## Done criteria

- The five core workstreams are represented as current-state graph bindings.
- Workstream bindings link to capabilities, surfaces, agents/tools/policies/traces/tests where applicable.
- Known drift is recorded for follow-up instead of hidden in active intent.
- Changes and queue update are committed.

## Vertical workstream contract

- Workstream / functional agent: docs-only current-intent capture for My Account, User Admin, Agent Admin, Audit/Trace, Governance/Policy
- Attention category or non-attention reason: non-runtime description capture
- Role-specific dashboard / surface: workstream surface descriptions only
- Surface graph node/action edge: described in app-description nodes, no runtime change
- Governed-tool id and exposure: described in tool binding nodes where applicable
- Capability id: core starter capabilities
- AuthContext / roles / tenant scope: must capture role/tenant/customer scope and denial expectations
- Akka substrate: docs/specs only
- API / frontend / realtime path: realization notes only
- Audit/work trace requirements: must capture trace/audit bindings for each protected workstream
- Local validation path: `git diff --check` plus workstream coverage proof
