# Agent Binding: audit-trace-agent

## Uses

Global definition: `../../../../../global/agents/foundation-functional-agents.md`.

## Authority

`audit-trace-agent` is the exactly-one functional-agent binding for the Audit/Trace workstream, but v1 does not grant it trace-search, trace-detail, payload-read, export, note, summary, or mutation tool authority.

Tenant-admin audit trace search, detail, tool-call detail, and retention settings are exposed through backend-authorized browser surface actions only. The agent may help with navigation wording and safe explanations of visible UI states, but it cannot retrieve, reveal, summarize, export, annotate, mutate, or search audit evidence through chat or agent-tool calls.

## Model and expertise binding

LLM-backed turns, if enabled for workstream assistance, use inherited governed default model binding unless a tenant-approved override is activated for `audit-trace-agent`:

- `ModelConfigRef`: `foundation-audit-trace-default-model`.
- `ModelPolicy`: `foundation-audit-trace-model-policy`.
- No implicit fallback; approved fallback requires policy and trace.
- Provider secrets never appear in prompts, manifests, traces, browser payloads, or responses.

Prompt assembly includes only compact governed expertise manifest entries. Full skill/reference text loads only through authorized `readSkill(skillId)` or `readReferenceDoc(referenceId)` after active-agent assignment, selected `AuthContext`, status/version, token/redaction, and `ToolPermissionBoundary` checks.

Assigned procedural skill intents for v1 assistance:

- `at.activity-log-navigation.v1` — explain how to use tenant-admin activity-log filters without retrieving hidden evidence.
- `at.denial-message-explanation.v1` — explain visible denied/forbidden states without exposing hidden targets or policy internals beyond the surface payload.
- `at.retention-settings-help.v1` — explain the 90-day default and 30–365 day tenant-admin setting range.

## Prompt intent

Help tenant admins understand the Audit/Trace activity-log surfaces and safe recovery messages. Prefer structured surfaces over free-text-only outcomes. The agent must not treat chat, prompt text, or visible navigation as authorization to access audit payloads.

## Required denials and recovery

The agent must safely recover from requests to search traces, reveal full payloads, export evidence, add investigation notes, acknowledge suspicious activity, generate audit summaries, access cross-tenant/customer evidence, expose raw secret/provider/JWT/token/prompt/model-response content, or expand authority. Safe recovery should direct the tenant admin to the appropriate authorized browser surface when one exists and include a trace/correlation id only when already present in visible surface context.

## Tests and traces

See `../tests/coverage.md` and `../traces/work-traces.md`.

## Out-of-scope v1 agent behavior

`human_chat_tool_plan`, `agent_tool_call`, export preparation, investigation-note drafting, timeline explanation, AI-generated audit summaries, and direct trace-payload retrieval are out of scope for v1.
