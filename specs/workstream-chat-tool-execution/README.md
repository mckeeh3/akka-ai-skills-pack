# Workstream Chat Tool Execution

## Recommendation: one mini-project, five workstream slices

Use **one mini-project** for all five foundation workstreams, not one mini-project per workstream.

Rationale:

- the hard part is shared substrate: tool-plan proposal, plan-bound confirmation, governed tool catalog/dispatcher, per-step transaction/idempotency, traces, result surfaces, and frontend confirmation UX;
- splitting by workstream first would duplicate the substrate design and create drift in tool semantics;
- the safer sequence is shared contract first, one User Admin vertical proof for the example request, then small per-workstream slices that reuse the same substrate;
- verification can still check each of the five foundation workstreams individually and append follow-up work if a workstream remains undercovered.

## Purpose

Implement confirmed workstream chat tool execution in the runnable secure AI-first SaaS core app.

The target runtime behavior is:

```text
human workstream chat request
→ selected workstream agent interprets within its bounded purpose/tool catalog
→ agent proposes a detailed tool plan
→ human confirms the specific plan
→ backend executes each governed tool as an independent transaction boundary
→ result/partial-failure surfaces are rendered
→ audit/work traces preserve actor adapter, requestedBy, confirmation, authorization, and per-tool outcomes
```

Example target request in the User Admin workstream:

```text
create org "Org 1", and invite mckee.hugh@gmail.com as an org admin
```

Expected behavior:

1. The request is recognized as a User Admin human-chat tool-plan request, not executed immediately.
2. The workstream agent/runtime proposes a plan such as:
   - create Organization `Org 1`;
   - invite `mckee.hugh@gmail.com` as Organization Admin for the created Organization.
3. The plan response explains inputs, side effects, required capabilities, idempotency, traces, possible provider/email fail-closed behavior, and what will not happen.
4. The human must explicitly confirm the plan.
5. Only after confirmation does the backend execute the steps through governed tools/action paths with tenant/customer scope, authorization, idempotency, and audit/work traces.
6. If a later step fails, prior committed tool transactions remain valid and the result surface reports completed, failed, skipped, and recovery steps.

## Source discussion / trigger

The skills-pack alignment mini-project `specs/workstream-tool-use-alignment/` completed the doctrine update that tools are the shared app boundary for both human-backed and AI-backed workers. Its verification explicitly left root app runtime implementation out of scope.

The earlier root app mini-project `specs/workstream-surface-intent-routing/` implemented deterministic no-mutation composer routing to prefilled surfaces. That remains valid and should continue to run before general model-backed chat. This mini-project adds the separately governed path for confirmed human chat tool plans.

## Scope

Root app-facing assets:

- `app-description/domains/core-starter/workstreams/**`
- `src/main/java/ai/first/application/coreapp/workstream/**`
- `src/main/java/ai/first/application/foundation/agent/**`
- `src/main/java/ai/first/domain/foundation/**` or `src/main/java/ai/first/domain/coreapp/**` only if durable plan state requires it
- `src/test/java/ai/first/**`
- `frontend/src/workstream/**`
- `frontend/src/api/**`
- `frontend/src/**/*.contract.test.mjs`
- `src/main/resources/agent-behavior-seeds/starter-v1/**` when workstream agent seed/familiarity text needs update
- `specs/workstream-chat-tool-execution/**`

## Done state

This mini-project is complete when the runnable core app supports confirmed human-chat tool execution at the stated scope:

- the app-description and implementation identify workstream tool catalogs and `human_chat_tool_plan` exposure for all five foundation workstreams;
- deterministic surface routing remains the first no-mutation path for high-confidence surface-open/prefill prompts;
- prompts that require execution can produce a plan-bound confirmation surface instead of directly mutating or falling through to advisory markdown;
- the User Admin example `create org "Org 1", and invite mckee.hugh@gmail.com as an org admin` works through the intended local backend/API/UI path with no mutation before confirmation and with execution after confirmation when authorized;
- the plan proposal path invokes the governed workstream agent runtime for model-backed interpretation when provider/runtime configuration is available and fails closed with a typed system-message/plan-unavailable surface when not available;
- tests may use deterministic model/test-provider behavior, but normal runtime does not fake model-backed planning success without configured provider/runtime;
- every executable plan step maps to a governed tool id, capability id, actor adapter/exposure channel, input schema, idempotency key, authorization policy, transaction boundary, and trace requirement;
- confirmed execution uses the intersection of human authority, selected AuthContext, selected workstream agent tool catalog, tool boundary, and tool policy;
- multi-step execution reports completed, failed, skipped, and recovery states without leaving inconsistent state;
- all five foundation workstreams have at least one representative confirmed chat tool-plan path, with deeper coverage queued if verification finds gaps;
- frontend surfaces render plan proposal, confirmation, execution progress/result, partial failure, denial, and recovery states accessibly and without exposing secrets or hidden capabilities;
- audit/work traces distinguish direct surface action from `human_chat_tool_plan`, include `requestedBy`, `confirmedBy`, correlation/idempotency, per-step results, denials, and provider fail-closed evidence;
- local validation evidence records readiness level and commands/manual smoke for the claimed path.

## Non-goals

- Do not make chat an unrestricted command shell.
- Do not bypass surfaces, backend capability authorization, selected `AuthContext`, idempotency, provider fail-closed behavior, audit/work traces, or frontend secret boundaries.
- Do not remove deterministic surface intent routing; it remains a preferred no-mutation path for opening/prepopulating surfaces.
- Do not expose every existing surface action as a chat-executable tool in the first pass.
- Do not grant AI autonomous mutation authority. This mini-project is about human-requested, human-confirmed tool execution.
- Do not count mock/model-less normal runtime as `runtime-ready` for model-backed plan proposal.

## Execution model

Execute one task per fresh harness context. Each task must update `pending-tasks.md`, run required checks or mark blocked with a precise reason, and make one focused git commit before being marked `done`.

The terminal verification task must verify both the current task group and the overall done state. If gaps remain, it must append more bounded tasks plus a new terminal verification task, then leave the mini-project open for another sequential pass.

## Read order for future task sessions

1. `AGENTS.md`
2. `specs/AGENTS.md`
3. `specs/workstream-chat-tool-execution/README.md`
4. `specs/workstream-chat-tool-execution/conversation-capture.md`
5. `specs/workstream-chat-tool-execution/pending-tasks.md`
6. selected sprint/backlog/task brief
7. related prior mini-project notes from:
   - `specs/workstream-tool-use-alignment/verification-notes.md`
   - `specs/workstream-surface-intent-routing/verification-notes.md`
8. task-specific backend/frontend/app-description/test files

## Sprint sequence

1. Sprint 01: Source audit, current-intent/app-description contracts, and implementation design.
2. Sprint 02: Shared backend plan proposal, confirmation, tool catalog, dispatcher, and trace substrate.
3. Sprint 03: User Admin proof for Organization creation plus Organization Admin invitation.
4. Sprint 04: Frontend confirmation/result surfaces and all-workstream representative expansion.
5. Sprint 05: Seed material, runtime verification, and follow-up loop.

## Open concerns

- Exact first-pass representative tools for My Account, Agent Admin, Audit/Trace, and Governance/Policy should be selected during the audit/design task based on existing governed actions and tests.
- Local runtime `runtime-ready` depends on provider/model configuration. Missing provider config must produce fail-closed evidence rather than successful fake planning.
- A future mini-project may deepen coverage to every important surface action after the shared substrate and first representative paths prove safe.
