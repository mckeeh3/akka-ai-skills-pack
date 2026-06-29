# Realization: Akka components for Audit/Trace

Capability: `audit-and-trace-investigation`.

This map is docs-only. It states component responsibilities implied by current intent and does not prove implementation alignment.

## Required component responsibilities

| Intent binding | Runtime responsibility |
|---|---|
| Immutable audit/work trace store | Persist tenant-scoped human surface actions, API calls, chat-plan lifecycle, agent tool calls, workflow/consumer/internal/timer events, denials, policy/approval refs, support-access events, export events, runtime-validation evidence links, and trace-gap findings as immutable records until retention expiry. |
| Trace normalization consumers | Normalize source workstream events from My Account, User Admin, Agent Admin, Governance/Policy, Audit/Trace, and future business workstreams with worker id/type, actor adapter/source, governed tool/capability, correlation/causation ids, redaction class, and safe summaries. |
| Audit/work trace projections/views | Support tenant/support-scoped dashboard, search, detail, timeline/correlation, denial investigation, support-access review, summary evidence, export state, and runtime-validation evidence views without indexing full payloads. |
| Correlation/timeline builder | Preserve parent/child and causation links across `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, workflow, consumer, API, projection, internal, and runtime-validation events; emit trace-gap findings when links are missing. |
| Support-access and export workflow state | Track support-access grant/use/expiry/denial and redacted export request/approval/result/idempotency states with reviewable trace refs. |
| Agent work-trace producer | Record AgentDefinition, prompt, skill, reference, model, tool-boundary, data access, tool invocation, authorization, confirmation, approval, and output summaries for `audit-trace-agent` and other managed agents. |
| Runtime-validation evidence linker | Attach runtime-validation run status/evidence refs to Audit/Trace timelines and source-alignment evidence without storing secrets. |
| Authorization/redaction boundary | Enforce selected `AuthContext`, active membership, tenant/support scope, role/capability grants, support-access expiry, sensitive-read/export approvals, no hidden enumeration, and secret-never-store rules for every read/action. |
| Retention expiry process | Remove records only through retention expiry and leave diagnosable retention-expiry evidence that does not reveal expired payloads. |

## Validation evidence required before build completion

- Component/API tests for immutable trace creation from surface action, chat plan, agent tool call, workflow/consumer/API/internal, denial, support-access, export, runtime-validation, and trace-gap producers.
- Component/API tests for tenant/support-scoped dashboard/search/detail/timeline/correlation projections and no full-payload keyword indexing.
- Component/API tests for denial investigation, support-access review, redacted export request/approval/denial/idempotency, and sensitive-detail redaction.
- Agent work-trace tests for prompt/skill/reference/model/tool-boundary refs, allowed/denied `agent_tool_call`, confirmed `human_chat_tool_plan`, requestedBy/confirmedBy, and partial-failure result refs.
- Runtime-validation evidence-link tests for safe refs, source-alignment impact, and frontend secret-boundary preservation.

## Explicit component exclusions

Do not implement autonomous trace remediation, support-access self-approval, raw sensitive export by default, trace edit/delete, full-payload keyword search, or prompt-based authority expansion unless later current intent adds those capabilities and policies.
