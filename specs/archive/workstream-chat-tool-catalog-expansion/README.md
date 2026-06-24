# Workstream Chat Tool Catalog Expansion

## Purpose

Deepen confirmed workstream chat tool execution beyond the representative first-pass paths completed by `specs/workstream-chat-tool-execution/`.

The goal is to expand the root SaaS Foundation App's bounded `human_chat_tool_plan` catalog so more existing governed surface actions can be safely requested through workstream chat, proposed as exact plan snapshots, explicitly confirmed by the human, and executed through the same backend-authorized tool/action paths already used by structured surfaces.

## Source discussion / trigger

Manual testing of the completed confirmed chat tool execution path looked good. The next desired step is to deepen chat-tool coverage beyond the initial representative paths.

The previous mini-project closed with `runtime-ready` representative coverage for:

- My Account: theme/settings update;
- User Admin: create Organization and invite Organization Admin;
- Agent Admin: approval-gated prompt-risk review;
- Audit/Trace: investigation note append;
- Governance/Policy: inert policy proposal draft.

This mini-project expands coverage while preserving the same safety model:

```text
workstream chat prompt
→ deterministic no-mutation surface routing first
→ governed human_chat_tool_plan proposal when prompt is execution-oriented
→ exact plan-snapshot confirmation
→ per-step governed action dispatcher
→ result/recovery surface
→ audit/work trace evidence
```

## Scope

Root app-facing assets:

- `app-description/domains/core-starter/workstreams/**`
- `src/main/java/ai/first/application/coreapp/workstream/**`
- `src/main/java/ai/first/application/coreapp/**` service paths only when needed for cataloged actions
- `src/main/java/ai/first/application/foundation/agent/**` only when seed/runtime trace integration needs small updates
- `src/test/java/ai/first/**`
- `frontend/src/workstream/**`
- `frontend/src/api/**`
- `frontend/src/**/*.contract.test.mjs`
- `src/main/resources/agent-behavior-seeds/starter-v1/**` when seed familiarity changes
- `specs/workstream-chat-tool-catalog-expansion/**`

## Done state

This mini-project is complete when the SaaS Foundation App has a broader, documented, tested, and runtime-verified `human_chat_tool_plan` catalog at the stated scope:

- all existing foundation workstream surface actions are inventoried and classified as one of: `chat-executable-now`, `chat-proposal-only`, `approval-gated`, `surface-only`, `router-only`, `internal-only`, `blocked-pending-design`, or `out-of-scope`;
- app-description current intent records the expanded chat tool catalog and explicit non-executable/blocked actions for all five foundation workstreams;
- backend catalog entries exist for each newly accepted `chat-executable-now`, `chat-proposal-only`, or `approval-gated` action, with governed tool id, capability id, input schema, idempotency, confirmation/approval behavior, transaction boundary, and trace requirements;
- prompt classification recognizes useful natural-language requests for the expanded catalog without stealing deterministic no-mutation surface-routing prompts;
- confirmation still requires exact plan snapshot acknowledgement and selected AuthContext validation;
- each executed step continues to use existing backend-authorized action/service paths where possible;
- high-impact actions such as policy activation/rollback, managed-agent activation/rollback, support-access grants, role grants/removals, account disabling, trace export delivery, and destructive lifecycle changes are either fully modeled with the right approval/confirmation safeguards or explicitly remain approval-gated/surface-only/blocked;
- frontend plan proposal/result surfaces remain usable for larger catalogs, including multi-step plans, approval-gated steps, validation repair, partial failure, and recovery;
- starter agent seed material can explain the expanded catalog and the difference between deterministic surface routing, proposal-only plans, approval-gated plans, and executable confirmed plans;
- tests cover no-mutation before confirmation, exact confirmation, out-of-catalog denial, selected-context/capability denial, approval-gated behavior, idempotency, partial failure/recovery, and trace evidence across the expanded catalog;
- terminal verification records API/UI/manual-smoke evidence for representative expanded paths and appends follow-up tasks if coverage gaps remain.

## Non-goals

- Do not expose every possible action as chat-executable just because it exists as a surface action.
- Do not weaken surface-first UX. Deterministic no-mutation routing and structured surfaces remain first-class.
- Do not grant autonomous AI mutation authority. This remains human-requested, human-confirmed execution.
- Do not bypass backend authorization, selected `AuthContext`, tenant/customer scoping, tool catalog membership, tool boundary, idempotency, provider fail-closed behavior, audit/work traces, or frontend secret boundaries.
- Do not count provider-unavailable fail-closed behavior as successful model-backed planning.
- Do not silently expand the scope to business-specific domains beyond the five foundation workstreams.

## Execution model

Execute one task per fresh harness context. Each task must update `pending-tasks.md`, run required checks or mark blocked with a precise reason, and make one focused git commit before being marked `done`.

The terminal verification task must verify both the current task group and the overall done state. If material gaps remain, it must append more bounded tasks plus a new terminal verification task, then leave the mini-project open for another sequential pass.

## Read order for future task sessions

1. `AGENTS.md`
2. `specs/AGENTS.md`
3. `specs/workstream-chat-tool-catalog-expansion/README.md`
4. `specs/workstream-chat-tool-catalog-expansion/conversation-capture.md`
5. `specs/workstream-chat-tool-catalog-expansion/pending-tasks.md`
6. selected sprint/backlog/task brief
7. prior completed mini-project evidence:
   - `specs/workstream-chat-tool-execution/verification-notes.md`
   - `specs/workstream-chat-tool-execution/source-and-design-map.md`
   - `specs/workstream-surface-intent-routing/verification-notes.md`
8. task-specific backend/frontend/app-description/test files

## Sprint sequence

1. Sprint 01: Inventory and current-intent expansion.
2. Sprint 02: Shared catalog/contract expansion and prompt classification guardrails.
3. Sprint 03: Per-workstream catalog implementation slices.
4. Sprint 04: Frontend UX, seeds, traceability, and regression coverage.
5. Sprint 05: Runtime verification and follow-up loop.

## Open concerns

- Some existing surface actions may be intentionally surface-only because the UI provides required context or evidence. Inventory should classify them explicitly instead of forcing chat execution.
- Some high-impact actions may need a separate approval/decision-card mini-project before they can become executable chat tools.
- Existing static frontend build files in `src/main/resources/static-resources/**` may be dirty from manual testing/builds; future tasks must avoid committing unrelated generated assets unless the selected task explicitly requires frontend production build output.
