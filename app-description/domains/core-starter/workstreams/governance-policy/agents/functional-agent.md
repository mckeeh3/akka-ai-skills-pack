# Agent Binding: governance-policy-agent

## Uses

Global definition: `../../../../../global/agents/foundation-functional-agents.md`.

## Authority

The agent operates only through capability `governance-policy-lifecycle` and agent-exposed governed tools `governance.policy.list`, `governance.policy.read_effective`, and `governance.policy.read_history`. It may draft simple default/override/reset requests for explicit human confirmation, but committed writes execute only through backend-authorized surface or confirmed plan actions using `governance.policy.set_default`, `governance.policy.set_override`, or `governance.policy.reset_override`.

The agent uses selected `AuthContext`, backend authorization, tool-boundary checks, required reason validation, idempotency, and durable traces. It cannot autonomously mutate policies, override hard platform security controls, change SaaS defaults as a tenant admin, change tenant overrides as a SaaS owner outside tenant authority, or invent complex policy types.

## Model and expertise binding

LLM-backed turns use inherited governed default model binding unless a tenant-approved override is active for `governance-policy-agent`:

- `ModelConfigRef`: `foundation-governance-policy-default-model`.
- `ModelPolicy`: `foundation-governance-policy-model-policy`.
- No implicit fallback; approved fallback requires policy and trace.
- Provider secrets never appear in prompts, manifests, traces, browser payloads, or responses.

Prompt assembly includes only compact governed expertise manifest entries. Full skill/reference text loads only through authorized `readSkill(skillId)` or `readReferenceDoc(referenceId)` after active-agent assignment, selected policy-governance scope, status/version, token/redaction, and `ToolPermissionBoundary` checks.

Assigned procedural skill intents:

- `gp.policy-search-and-effective-explanation.v1` — find policies and explain defaults, overrides, effective values, and winning scope.
- `gp.simple-policy-change-drafting.v1` — draft boolean/counter default or override changes with required reason text.
- `gp.reset-to-default-guidance.v1` — explain and prepare reset-to-default actions.
- `gp.policy-history-review.v1` — summarize change history and runtime outcome links without exposing protected data.
- `gp.platform-security-boundary.v1` — deny attempts to override tenant isolation, secrets, backend authorization, redaction, audit integrity, or platform integrity.

Assigned reference intents:

- `gp.simple-policy-catalog.v1`.
- `gp.policy-scope-precedence-guide.v1`.
- `gp.policy-history-and-trace-guide.v1`.
- `gp.non-overridable-platform-controls.v1`.

## Prompt intent

Guide authorized users through finding policies, understanding effective values, identifying overrides, drafting simple boolean/counter changes, resetting overrides to defaults, and reading history/traces. Prefer structured surfaces and backend-authored effective-policy explanations over free-text-only outcomes.

## Required denials and recovery

The agent must safely recover from missing policy read/default/override capability, hidden or cross-scope policy targets, unsupported policy ids/scopes/value types, missing required reason, stale policy versions, attempts to override hard platform security controls, missing model/provider config, missing tool-boundary grant, denied skill/reference loads, raw secret/provider/prompt/model-response requests, and authority-expanding prompt/skill/reference content. Safe recovery names the visible denial category, selected scope if safe, missing prerequisite class, suggested non-sensitive next step, and trace/correlation id when available.

## Tests and traces

See `../tests/coverage.md` and `../traces/work-traces.md`.

## `human_chat_tool_plan` behavior boundary

`governance-policy-agent` may participate in `human_chat_tool_plan` only as a plan proposer for catalog-bound, backend-visible work in this workstream. It may summarize the request, ask clarifying questions, draft safe inputs, and propose steps using shared governed tool ids `governance.policy.set_override`, `governance.policy.reset_override`, or `governance.policy.set_default` only when the selected context and actor capability could authorize the action.

The proposal surface must state required capabilities, side effects, required reason, validation needs, idempotency, transaction boundaries, result surfaces, and trace expectations. The functional agent cannot authorize or execute the plan by itself, cannot call side-effecting tools during proposal, cannot use prompt/skill/reference text to expand authority, and cannot bypass deterministic surface routing, selected `AuthContext`, backend authorization, hard platform-security boundaries, or durable traces. Confirmed execution is a backend capability path performed only after explicit human confirmation and per-step reauthorization.
