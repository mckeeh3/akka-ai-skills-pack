# Backlog: Agent Admin behavior-profile realization

## Source current-intent nodes

- `app-description/domains/core-starter/workstreams/agent-admin/**`
- `app-description/domains/core-starter/capabilities/agent-doc-administration.md`
- `app-description/domains/core-starter/data-state/managed-agent-behavior-state.md`
- `app-description/domains/core-starter/workstreams/agent-admin/realization/source-alignment.md`

## Ordered build slices

### 1. Implementation map and stale-test triage

Produce a focused implementation map from current app-description to current code/tests. Split stale assertions into keep/replace/delete/compat buckets before changing broad behavior.

Suggested task: `AABP-01-001`.

### 2. Proposal lifecycle domain/service foundation

Add records/statuses/repository seams for behavior proposals, risk classification, authority-expansion flags, base version, transcript summary, approval/rejection/activation metadata, and proposal-to-active activation. Update service tests so Save Draft is non-active and activation is separate.

Suggested task: `AABP-01-002`.

### 3. Prompt/skill/reference proposal flows and restore/deprecate semantics

Reconcile direct save/restore/create/delete paths. Restore creates proposal. Create skill/reference creates proposal. Delete defaults to deprecation unless policy permits hard delete. Historical versions remain read-only.

Suggested task: `AABP-01-003`.

### 4. Behavior-profile version and assignment seams

Introduce tenant-scoped behavior profile version contracts for model config reference, prompt version, skill assignment, and generated tool assignment. Do not edit generated tool code. Activation creates profile versions.

Suggested task: `AABP-01-004`.

### 5. Runtime loader and trace alignment

Wire runtime resolution to active behavior profile/docs only, enforce assigned skill/reference/generated-tool boundaries, and emit trace metadata for profile, prompt, skill/reference, model-policy, generated-tool, and tool-boundary decisions.

Suggested task: `AABP-02-001`.

### 6. Workstream/API action and surface wiring

Expose protected Agent Admin dashboard/catalog/detail/doc/proposal/profile/assignment/trace surfaces and actions. Deny non-SaaS-admin callers safely. Remove current product exposure of stale whole-agent profile/lifecycle mutation.

Suggested task: `AABP-03-001`.

### 7. Frontend surfaces and fixture/contract realignment

Update React surfaces, types, fixtures, and contract tests for current Agent Admin inventory: proposal review, version history, profile assignments, generated tool assignment, model config ref summaries, runtime traces, SaaS-admin denials, and no provider-secret/tool-code leakage.

Suggested task: `AABP-04-001`.

### 8. Full-stack closure verification

Run backend/frontend validations, update source-alignment and verification notes, and either close the mini-project or append bounded follow-up tasks plus another terminal verification task.

Suggested task: `AABP-05-001`.

## Cross-cutting requirements for all implementation tasks

- Preserve SaaS Owner/Admin-only access and browser-safe denials.
- Preserve tenant/customer scope and reserved `saas-app-owner` behavior scope.
- Do not count fixture/model-less success as normal runtime provider success.
- Keep provider secrets, hidden platform instructions, generated tool internals, and unapproved tool-boundary internals out of browser payloads.
- Update stale tests rather than using them as product authority.
- Run the smallest checks that prove the selected slice plus `git diff --check`.
