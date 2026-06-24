# Conversation Capture: Workstream Chat Tool Catalog Expansion

## Background

The root app now has a confirmed workstream chat tool execution substrate from `specs/workstream-chat-tool-execution/`. Terminal verification recorded `runtime-ready` first-pass representative coverage across all five foundation workstreams.

Representative paths completed there:

- My Account: update settings/theme;
- User Admin: create Organization and invite Organization Admin;
- Agent Admin: prompt-risk review as an approval-gated plan;
- Audit/Trace: append investigation note;
- Governance/Policy: draft inert policy proposal.

Manual testing after completion looked good.

## User request

The user asked to proceed with the recommended next option:

> Deepen chat-tool coverage beyond representative paths.

## Accepted direction

Create a new durable mini-project under:

```text
specs/workstream-chat-tool-catalog-expansion/
```

The mini-project should expand the confirmed `human_chat_tool_plan` catalog across the five foundation workstreams while keeping the safety posture from the previous implementation:

- deterministic surface routing remains first for no-mutation open/prefill prompts;
- execution-oriented prompts may produce plan-bound confirmation surfaces;
- no mutation happens before exact plan snapshot confirmation;
- backend catalog membership, selected AuthContext, human authority, capability checks, tool boundary, idempotency, approval policy, and traces remain authoritative;
- high-impact or under-modeled actions must be marked approval-gated, surface-only, blocked, or out-of-scope rather than exposed unsafely.

## Planning decision

Use one mini-project for the whole foundation app with per-workstream slices. This keeps the expanded catalog coherent and avoids duplicating the shared prompt-classification, catalog, dispatcher, trace, frontend, and verification rules.

## Manual testing signal

The user's manual testing is positive supporting evidence for proceeding, but this mini-project still requires automated and runtime verification before marking expanded coverage complete.
