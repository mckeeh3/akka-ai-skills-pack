# My Account Workstream v0 Contract

## Purpose

`My Account Agent` is the signed-in user's self-service workstream for understanding and safely acting on their current account, selected `AuthContext`, profile/settings, authority, and personal next steps across the five-core v0 shell.

This contract inherits `specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md` and must not weaken its runtime, authorization, trace, model-provider, or UI validation gates.

## Workstream identity and launch semantics

- Workstream id: `my_account`
- Functional agent: `My Account Agent`
- Primary launcher: signed-in user tile/email only; do not duplicate My Account as a normal top-rail workstream launcher.
- User-facing mode: continuous request/response composer plus structured system-message surfaces.
- Required selected context: authenticated account with selected tenant/customer `AuthContext` for tenant/customer-scoped details; account-global safe profile/settings reads may render with an explicit no-selected-context blocked state for scoped actions.
- Primary v0 audience: the signed-in account holder acting for themselves.

## Responsibility boundary

### In scope

- Show browser-safe account, profile, settings, selected context, membership, role/capability summary, and current authority basis.
- Explain what the user can do next without granting authority by UI copy or prompt text.
- Open authorized sibling workstreams from aggregate next-step links or safe shell requests.
- Update the user's own allowed profile/settings preferences when permitted.
- Return safe denials, validation errors, blocked-provider states, and trace/correlation references.
- Record durable workstream request/response entries and audit/work traces for protected reads, self-service writes, denials, prompt assembly, skill/reference loads, tool calls, and model/provider failures.

### Out of scope

- Creating users, invitations, roles, capabilities, tenants, customers, policies, governed agent definitions, prompts, skills, references, model refs, or tool boundaries.
- Cross-user or administrative mutation except by opening an authorized sibling workstream.
- Treating frontend affordances, prompt instructions, hidden fields, or deterministic canned responses as authorization or completed model-backed runtime behavior.
- Introducing Akka `AutonomousAgent` work unless a later task adds a durable personal digest/review task with task lifecycle semantics.

## Agent-type and service choices

| Need | Selection | Rationale |
|---|---|---|
| Normal My Account composer turn and `markdown_response` help | Request-based Akka `Agent` | The user expects an immediate/streamed answer for the selected account/context. Completion requires the governed managed-agent runtime path and concrete Akka Agent invocation. |
| Mechanical `/api/me`, context, authority, profile/settings, validation, redaction, trace normalization | Deterministic non-AI services/components | These are policy and data operations; they must be backend-enforced and audited, not model-decided. |
| Durable personal access/audit digest, if added later | Optional Akka `AutonomousAgent` | Only justified if it has task identity, progress snapshots/results, cancellation/failure, notifications, or long-running investigation semantics. Not part of this v0 contract unless a follow-up task explicitly adds it. |

## Governed runtime requirements for model-backed turns

A model-backed My Account response is complete only when the normal message path:

1. authenticates the account and resolves the selected `AuthContext`;
2. resolves the active governed managed-agent `AgentDefinition` for `my_account`;
3. assembles approved prompt, compact skill manifest, compact reference manifest, and model config;
4. checks `ToolPermissionBoundary` before registering tools;
5. exposes only assigned and authorized `readSkill(skillId)`, `readReferenceDoc(referenceId)`, and selected My Account read/proposal tools;
6. invokes a concrete request-based Akka `Agent` through the governed runtime path with `effects().tools(runtimeTools)`;
7. calls the configured provider boundary;
8. emits `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, model/tool/provider traces, and `AgentWorkTrace`;
9. returns either sanitized `markdown_response` or a safe blocked/error surface.

Missing, blank, or invalid provider/security/tool-boundary configuration must fail closed with an actionable blocked surface and trace. It must not fall back to deterministic normal-runtime prose.

## Structured surfaces and actions

| Surface/action | Type | Capability mapping | Notes |
|---|---|---|---|
| User tile launcher | shell launcher | `my_account.view_summary` | Only visible from the signed-in user tile/email. |
| Account summary card | structured surface | `my_account.view_summary` | Browser-safe account/profile/settings/context/authority summary. |
| Current context panel | structured surface | `my_account.view_context` | Selected tenant/customer, membership, role/capability basis, missing-context blocked state. |
| Personal settings form | structured surface/action | `my_account.update_profile_settings` | Only allowed self-service fields; backend validates and audits. |
| Next steps panel | structured surface | `my_account.list_next_steps` | Aggregates authorized sibling-workstream links and blocked/recovery guidance. |
| Open sibling workstream | surface action / shell request | `my_account.open_authorized_workstream` | Resolves capability visibility server-side; never grants access. |
| Ask My Account | composer action | `my_account.ask_agent` | Request/response Agent turn with governed tools and traces. |
| Trace link | surface link | `my_account.view_own_trace_refs` | Opens/redacts only authorized trace references or delegates to Audit/Trace when available. |

## Authority and exposure rules

- Backend authorization is required for every protected API route, component call, view query, workstream action, agent tool, and trace read.
- Required authority is derived from `AuthContext`, active membership, selected tenant/customer, membership status, and named capabilities; frontend visibility is advisory only.
- The My Account Agent may read self-scoped profile/context/authority evidence and may propose or execute only explicitly allowed self-service updates.
- Sibling-workstream links must be generated from backend-visible capabilities and return safe denial/system-message surfaces when authority is absent or context is invalid.
- Provider secrets and backend-only policy/tool-boundary data must never appear in `/api/me`, frontend source, static assets, responses, logs, or trace payloads.

## Trace and audit obligations

Every capability in `capability-inventory.md` must define trace/audit fields. At minimum, My Account v0 records or preserves:

- correlation id and workstream request id;
- account id, selected tenant/customer ids where applicable, membership id, role/capability basis, and denial reason;
- protected data categories read or mutated, with redaction markers rather than secrets;
- profile/settings change summaries and idempotency keys;
- shell request origin and target workstream for open-workstream actions;
- prompt assembly, skill/reference load, model invocation, tool invocation/denial, provider failure, and `AgentWorkTrace` references for agent turns.

## Validation path

Implementation tasks must prove the intended local runtime path for their scope. Minimum validation expectations:

- backend tests for success, validation, forbidden/missing-context/disabled-user, tenant isolation, idempotency/no-op, audit/work trace, and provider fail-closed behavior where model-backed behavior is in scope;
- frontend tests/typecheck for signed-in tile launch, safe rendering, denials/blocked states, trace links, and no duplicate top-rail My Account launcher;
- `git diff --check` for every task;
- terminal verification compares completed runtime/API/UI behavior against this contract, the capability inventory, and the shared five-core v0 contract.
