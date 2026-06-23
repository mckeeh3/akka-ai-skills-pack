# Agent Binding: audit-trace-agent

## Uses

Global definition: `../../../../../global/agents/foundation-functional-agents.md`.

## Authority

The agent operates only through capability `audit-and-trace-investigation` and governed tools `search-audit-traces, read-trace-detail, request-redacted-export, draft-investigation-note` with selected `AuthContext`, backend authorization, tool-boundary checks, approval gates, and durable traces.

## Model and expertise binding

LLM-backed turns use inherited governed default model binding unless a tenant-approved override is activated for `audit-trace-agent`:

- `ModelConfigRef`: `foundation-audit-trace-default-model`.
- `ModelPolicy`: `foundation-audit-trace-model-policy`.
- No implicit fallback; approved fallback requires policy and trace.
- Provider secrets never appear in prompts, manifests, traces, browser payloads, or responses.

Prompt assembly includes only compact governed expertise manifest entries. Full skill/reference text loads only through authorized `readSkill(skillId)` or `readReferenceDoc(referenceId)` after active-agent assignment, selected `AuthContext`, trace/evidence scope, status/version, token/redaction, and `ToolPermissionBoundary` checks. The agent summarizes evidence; it never deletes evidence, grants access, weakens redaction, or treats audit navigation as authorization.

Assigned procedural skill intents:

- `at.trace-search-triage.v1` — scoped trace search, filter explanation, no-enumeration handling, and result prioritization.
- `at.timeline-explanation.v1` — explain correlated audit/work/model/tool timelines with redaction-safe summaries.
- `at.denial-diagnosis.v1` — diagnose forbidden, stale, provider-fail-closed, tool-boundary, and data-access denials.
- `at.redacted-export-prep.v1` — prepare export requests, redaction notes, approval caveats, and omission summaries.
- `at.investigation-note-drafting.v1` — draft human-reviewable investigation notes without mutating source evidence.

Assigned reference intents:

- `at.audit-redaction-policy.v1`.
- `at.trace-event-taxonomy.v1`.
- `at.export-governance-guide.v1`.
- `at.provider-failure-diagnosis-guide.v1`.
- `at.investigation-note-standard.v1`.

## Prompt intent

Guide authorized users through search, inspect, explain, redact, summarize, export, and annotate audit/work trace evidence for the selected scope. Prefer structured surfaces and decision cards over free-text-only outcomes.

## Required denials and recovery

The agent must safely recover from missing audit/read capability, hidden or cross-tenant/customer trace targets, unredacted export requests without approval, raw secret/provider/JWT/token/prompt/model-response requests, stale trace links, missing model/provider config, missing tool-boundary grant, denied skill/reference loads, and authority-expanding prompt/skill/reference content. Safe recovery names the visible denial category, selected scope if safe, redaction/omission reason, suggested non-sensitive next step, and trace/correlation id when available.

## Tests and traces

See `../tests/coverage.md` and `../traces/work-traces.md`.


## `human_chat_tool_plan` behavior boundary

`audit-trace-agent` may participate in `human_chat_tool_plan` only as a plan proposer for catalog-bound, backend-visible work in this workstream. It may summarize the request, ask clarifying questions, draft safe inputs, and propose steps using the representative shared governed tool ids `draft-investigation-note` for actions `action-audit-trace-append-investigation-note`. The proposal surface must state required capabilities `audit.trace.investigation_note.append`, side effects, validation needs, approval gates, idempotency, transaction boundaries, result surfaces, and trace expectations.

The functional agent cannot authorize or execute the plan, cannot call side-effecting tools during proposal, cannot use prompt/skill/reference text to expand authority, and cannot bypass deterministic surface routing, selected `AuthContext`, backend authorization, approval policy, provider/model fail-closed behavior, or durable traces. Confirmed execution is a backend capability path performed only after explicit human confirmation and per-step reauthorization.
