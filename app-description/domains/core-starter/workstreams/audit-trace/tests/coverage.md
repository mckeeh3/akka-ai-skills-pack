# Tests: Audit/Trace

## Acceptance

- Given an authorized tenant admin with selected `AuthContext`, when they open the Audit/Trace dashboard, then investigation, denial, trace-gap, support-access review, runtime-validation evidence, and export attention items are scoped to their tenant and link to protected surfaces.
- Given an authorized SaaS support operator with active support-access scope, when they open Audit/Trace, then search/detail/timeline results are limited to the granted support scope, redacted by default, and support-access use is traced.
- Given an authorized user searches audit or work traces, when the search is submitted, then the graph path worker → `surface_action`/`api_call` or confirmed `human_chat_tool_plan` → governed tool (`search-audit-traces` or `search-work-traces`) → capability `audit-and-trace-investigation` → result surface is traceable.
- Given an authorized user opens trace/work-trace detail, then default detail shows safe summary, actor/worker/adapter/source, authorization basis, governed tool/capability, redaction state, related traces, and sensitive-detail state without leaking secrets.
- Given an authorized user opens a correlation timeline, then related surface actions, chat plans, agent tool calls, workflows, consumers, API/internal events, policy/approval refs, support-access events, and runtime-validation evidence are ordered with causation links and explicit trace gaps.
- Given an authorized user opens a denial investigation, then denial reason, policy reference, actor adapter, governed tool/capability, selected AuthContext/support scope, redaction status, and safe remediation are visible when allowed.
- Given an authorized user requests an investigation summary, then the summary cites selected evidence refs, correlation chain, denial/trace-gap/support-access/export refs, redaction disclaimer, and unresolved unknowns.
- Given export is allowed by policy, when a redacted export is requested, then an approval-required/queued/redacted-result/denied/expired state is returned and the request/result is idempotently traced.

## Regression and explicit exclusions

- Given keyword search, then matches come only from deterministic metadata/summary fields and never full request/response/tool payload text.
- Given browser list/search/timeline payloads, then full payloads, secrets, provider credentials, bearer/session tokens, hidden cross-tenant identifiers, and frontend-secret material are absent.
- Given the Audit/Trace agent is asked to expand scope, approve export/support access, reveal hidden traces, perform full-payload keyword search, delete/edit traces, or expose secrets, then it refuses and emits safe work-trace evidence.
- Given sensitive/raw export is requested without explicit approval policy/grant, then the request is denied or approval-required; it is not silently prepared.

## Security and negative

- Given a non-admin tenant member, customer admin, disabled user, inactive membership, missing selected context, or expired support-access grant, when they attempt any Audit/Trace protected action, then the action is denied server-side and emits denial trace evidence without hidden target enumeration.
- Given tenant admin A requests tenant B traces or uses a cross-tenant customer filter, then the request is denied or returns no authorized rows without revealing protected existence.
- Given a hidden, expired, malformed, or cross-scope trace reference, when detail/timeline/summary/export is requested, then the result is `not_found_or_redacted`, validation-error, approval-required, or forbidden as appropriate.
- Given `audit-trace-agent` lacks a ToolPermissionBoundary grant for a requested `agent_tool_call`, then no tool executes, a denial trace is recorded, and the user receives safe recovery text.
- Given a human chat investigation plan is unconfirmed or stale, then no governed tool executes and the denial/unconfirmed state is traced with `requestedBy` but no `confirmedBy`.
- Given support operator access, then support-access grant/use/expiry is visible to authorized reviewers and the support operator cannot approve their own access/export.
- Given frontend/API payloads, then provider/server secrets, bearer/session tokens, raw prompt/model payloads, and frontend-secret material are never emitted.

## Idempotency and result behavior

- Given the same read-only search/detail/correlation request is repeated, then current authorized results are returned without mutating trace facts other than read evidence.
- Given the same confirmed chat plan is replayed with the same confirmation/idempotency context, then read tools do not create duplicate side effects and result traces link to the same plan/correlation context.
- Given the same redacted export request/scope/idempotency key is repeated, then the existing approval/result state is returned without duplicate export preparation.
- Given a multi-tool investigation summary has a partial failure, then the result surface shows per-tool outcomes and safe trace refs without hiding completed reads.

## Observability and trace verification

- Search, detail read, work-trace read, timeline/correlation lookup, denied trace read, denial investigation, support-access review, summary generation, export request/result, runtime-validation evidence link, trace-gap detection, retention expiry, assistant refusal, and partial failure each emit durable trace evidence with worker id/type, adapter/source, tenant/support scope, governed tool/capability id where applicable, authorization decision, redaction class, result surface, and correlation id.
- Human chat plan traces include proposed plan, `requestedBy`, `confirmedBy`, confirmation id, governed tool ids, per-tool execution outcomes, denials before confirmation, and partial-failure/result-surface refs.
- Agent work traces include AgentDefinition id/version, prompt/skill/reference/model refs, ToolPermissionBoundary decision, governed tool id, actor adapter source `agent_tool_call`, authorization decision, requestedBy where applicable, and model-safe output summary.
- Runtime-validation evidence links record workstream, scenario id, run status, evidence refs, source-alignment impact, and any trace-gap findings.
- Derived search/timeline views are not the only audit truth; immutable trace facts remain the source of record until retention expiry.

## UI and accessibility verification

- Dashboard cards, search filters, rows, pagination, detail links, timeline events, denial/support-access review controls, summary/export actions, and confirmation surfaces are keyboard-operable with visible focus.
- Status, denial, approval, and redaction states are not color-only.
- Error, empty, forbidden, approval-required, validation-error, stale/reconnect, trace-gap, partial-failure, and `not_found_or_redacted` states provide safe recovery text without exposing hidden data.
- Sensitive-detail warnings appear anywhere sensitive payload sections are rendered.

## Runtime-validation references

Runtime-validation scenarios to link into Audit/Trace as evidence:

- trace search/detail success and forbidden trace read;
- redaction and frontend secret-boundary validation;
- confirmed human chat read-only plan trace chain;
- bounded agent tool call denial and allowed model-safe result;
- support-access grant/use/expiry review;
- redacted export approval/denial/idempotency;
- cross-workstream correlation and trace-gap detection;
- provider/config fail-closed and runtime-loader denial traces;
- source-alignment evidence for refreshed workstream paths.
