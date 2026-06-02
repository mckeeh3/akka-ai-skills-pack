# Workstream Expertise Contracts

This directory is the authoritative app-description location for per-functional-agent workstream expert bundles in the scaffolded core app description.

One file should exist for each LLM-enabled functional agent when its bundle is in scope:

```text
12-workstreams/workstream-expertise/
  <functional-agent-id>.md
```

A workstream expert bundle defines the governed behavior and knowledge that make a functional agent competent for its workstream. It complements, but does not replace:

- `12-workstreams/functional-agents.md` for the functional-agent catalog and shell placement;
- `12-workstreams/surfaces-index.md` and `surface-contracts/**` for structured surface contracts;
- `10-capabilities/**` for backend capability contracts;
- `15-operating-model/governed-runtime-agents.md` for runtime agent lifecycle and governance rules;
- `40-auth-security/**` for authorization and tenant/customer isolation;
- `50-observability/**` for audit and trace schemas;
- `30-tests/**` and `70-traceability/**` for verification and relationship maps.

## Required bundle contract

Each `<functional-agent-id>.md` should capture:

- bundle id and owning functional agent id;
- foundation or domain-specific scope;
- tenant/customer/AuthContext assumptions;
- authorized roles, permissions, and named capability grants;
- prompt intent, refusal behavior, clarification behavior, and escalation behavior;
- governed prompt references;
- governed model binding: explicit `ModelConfigRef`/`ModelPolicy` or explicit inherited governed default model binding, allowed modes, fallback or `noFallback` policy, provider secret boundary, and model-use trace facts;
- governed procedural skill document ids, versions/status expectations, and when-to-use hints;
- governed reference document ids, versions/status expectations, and what they may be used to cite or consult;
- compact expertise manifest entries assembled into prompt context without full document bodies;
- authorized `readSkill(skillId)` and reference-loader behavior, including denied loads;
- mapped backend capability ids and whether agent use is read-only, proposal-only, approval-gated, or bounded autonomous;
- `ToolPermissionBoundary` references and model-facing tool allow/deny rules;
- owned/reused structured surfaces that show evidence, decisions, denials, traces, or governance state;
- required `PromptAssemblyTrace`, model binding/model-use trace facts, `SkillLoadTrace`, reference-load trace, `AgentWorkTrace`, data-access, decision, and audit events;
- governance owner/steward, reviewer roles, and approval requirements for expertise changes;
- seed/import provenance, checksum, idempotency, and customization-preserving upgrade expectations;
- tests for model binding resolution/denial/fallback and provider secret non-exposure, authorized and denied skill/reference loads, tool-boundary denial, no authority expansion from text, tenant isolation, surface rendering, and trace emission.

## Seed status

The scaffolded core app description materializes expertise examples for every foundation functional agent:

- `my-account-agent.md` — own-account, selected-context, profile/settings, sign-out, inherited governed default model binding, safe self-service, disabled/forbidden context denials, traces, seed policy, and tests.
- `user-admin-agent.md` — canonical User Admin prompt intent, skills, references, compact manifests, boundaries, denied loads, traces, governance ownership, seed policy, and tests.
- `agent-admin-agent.md` — Agent Admin governed behavior artifact proposals, prompt/skill/reference governance, manifest and tool-boundary governance, seed upgrades, approval gates, denials, traces, and tests.
- `governance-policy-agent.md` — policy clauses, approval gates, proposals, simulations, replay evidence, activation/rollback, authority-expansion denials, traces, seed policy, and tests.
- `audit-trace-agent.md` — Audit/Trace search and explanation behavior, redaction-preserving evidence summaries, support-access audit, export limits, denied loader/tool traces, decision-card escalation, and tests.

Later starter-resource and test tasks should realize the governed records described in these bundles without bypassing app-managed governed storage.
