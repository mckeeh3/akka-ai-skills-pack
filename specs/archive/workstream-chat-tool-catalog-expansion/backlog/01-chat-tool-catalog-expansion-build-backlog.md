# Workstream Chat Tool Catalog Expansion Build Backlog

## Backlog goal

Expand the SaaS Foundation App's confirmed `human_chat_tool_plan` catalog from representative coverage to a broader, classified, tested foundation catalog.

## Implementation order

### WCTC-01: Inventory and classify existing actions

- Produce `specs/workstream-chat-tool-catalog-expansion/catalog-inventory.md`.
- Inventory existing `SurfaceAction`/`runAction` paths, app-description tools, frontend surfaces, and tests for all five foundation workstreams.
- Classify each action as executable/proposal/approval/surface-only/router/internal/blocked/out-of-scope.
- Recommend the first expansion set for each workstream.

### WCTC-02: Update current intent and catalog coverage map

- Update app-description workstream artifacts with expanded chat catalog intent and blocked/surface-only rationale.
- Add `catalog-coverage-map.md` under this mini-project, tying app-description, backend ids, frontend surfaces, tests, and smoke paths together.

### WCTC-03: Shared catalog classification and prompt guardrails

- Add/extend backend catalog metadata for classification, risk, approval requirement, surface-only rationale, and natural-language prompt examples.
- Tighten prompt classification so no-mutation surface routing remains first and unsupported/high-risk prompts are not silently executed.
- Add tests for unsafe exposure prevention.

### WCTC-04: Expand My Account chat tools

- Add safe confirmed chat plan entries for profile/settings/context/notification preference actions selected by the inventory.
- Cover denied context and exact confirmation behavior.

### WCTC-05: Expand User Admin chat tools

- Add safe confirmed chat plan entries for more User Admin actions selected by the inventory.
- Likely candidates include invite/resend/revoke invitation, user detail read/open, membership status/lifecycle where safely modeled, organization/customer lifecycle surfaces/actions, and support access only if approval/confirmation prerequisites are complete.
- Keep role grants/removals, account disabling, last-admin-risk actions, and support access grants approval-gated or surface-only unless fully modeled.

### WCTC-06: Expand Agent Admin chat tools

- Add safe confirmed chat plan entries for behavior proposal drafts, prompt/skill/reference/manifest/tool-boundary review surfaces, simulations, test runs, seed import where safe, and approval-gated lifecycle actions.
- Keep activation/rollback/deactivation execution blocked or approval-gated unless exact lifecycle prerequisites are fully modeled.

### WCTC-07: Expand Audit/Trace chat tools

- Add safe confirmed chat plan entries for trace search/detail/timeline/failure evidence/investigation guide and note append selected by inventory.
- Keep export delivery or raw evidence access approval-gated/surface-only unless export policy and redaction are fully modeled.

### WCTC-08: Expand Governance/Policy chat tools

- Add safe confirmed chat plan entries for policy proposal drafts, simulations, impact-analysis starts/reviews, and decision-card review paths selected by inventory.
- Keep policy activation/rollback or live authority changes approval-gated or blocked unless fully modeled.

### WCTC-09: Frontend UX and result-state polish

- Ensure plan surfaces handle expanded catalog metadata, blocked/surface-only rationale, proposal-only/approval-gated steps, multi-step result/recovery, and validation repair.
- Add/extend frontend contract tests.

### WCTC-10: Seed and traceability updates

- Update starter managed-agent seed material so each workstream agent can explain the expanded catalog and denied/blocked classifications accurately.
- Update app-description/spec traceability maps.

### WCTC-11: Expanded regression and runtime smoke tests

- Add focused backend/API/frontend tests across the expanded catalog.
- Cover no mutation before confirmation, exact confirmation, out-of-catalog denial, selected-context denial, provider fail-closed, approval-gated behavior, idempotency, partial failure, and trace evidence.

### WCTC-99: Terminal verification

- Verify README done state.
- Run checks and smoke paths.
- Append follow-up tasks plus a new terminal verification task if material gaps remain.

## Design guardrails

- Every catalog entry must have a classification and a rationale.
- `chat-executable-now` requires existing backend-authorized execution path, input schema, idempotency, exact confirmation, trace, and tests.
- `chat-proposal-only` may draft or prepare a proposal but must not commit final side effects.
- `approval-gated` may prepare or route an approval surface but must not complete the high-impact operation without the existing approval gate.
- `surface-only` remains valid when the browser surface provides necessary evidence/context that chat should not compress.
- `blocked-pending-design` must name the missing design or runtime prerequisite.
