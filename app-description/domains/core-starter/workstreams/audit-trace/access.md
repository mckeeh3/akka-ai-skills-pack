# Access: Audit/Trace

## Authorized roles and scopes

Audit/Trace is default-deny. Every route, surface action, chat plan, agent tool call, API call, projection read, consumer/internal call, export workflow, and runtime-validation evidence read requires backend-owned `AuthContext`, active membership or service provenance, tenant scope, role/capability grants, and redaction policy evaluation.

| Principal / worker | Scope | Allowed visibility |
|---|---|---|
| `tenant-admin` / Organization admin | selected tenant/Organization; optional customer/account filter within that tenant | Tenant audit/work traces, denials, agent/tool/policy evidence, support-access events involving the tenant, and runtime-validation evidence for the tenant; sensitive fields redacted by default. |
| `saas-support` or SaaS owner support operator | active support-access grant for tenant, or platform support scope recorded in `AuthContext` | Support diagnostics, trace-gap investigation, provider/config fail-closed summaries, and tenant traces only inside support-access scope; tenant payloads redacted unless `trace.sensitive.read` is explicitly granted. |
| `audit-trace-agent` | selected user `AuthContext` plus tool-boundary grants | Read-only search/correlation/summary/denial investigation through confirmed `human_chat_tool_plan` or bounded `agent_tool_call`; no authority to widen scope, approve support access, approve export, or reveal hidden targets. |
| `audit-trace-system-worker` | service provenance plus tenant/correlation context | Trace ingestion, projection/correlation, retention, runtime-validation evidence links, redaction, denial tracing, and export workflow bookkeeping. |

Customer admins, ordinary tenant members, disabled users, inactive memberships, cross-tenant callers, unsupported support sessions, and agents without explicit tool grants are denied.

## Visibility categories

- Tenant admins can view tenant-scoped trace summaries and authorized detail. Full payload sections use progressive disclosure and redaction; secrets, provider credentials, bearer/session tokens, frontend-secret material, hidden cross-tenant identifiers, and raw implementation internals never appear in browser or agent payloads.
- SaaS support can view tenant evidence only inside support-access scope and with stronger redaction defaults. Support-access grant, use, expiry, and denial traces are visible to authorized tenant admins and SaaS owners for review.
- Trace read access does not imply access to the underlying domain object if that object is otherwise forbidden; the trace surface may show safe summary, redacted evidence, or `not_found_or_redacted`.
- Runtime-validation evidence linked into Audit/Trace is visible only to roles allowed to see the affected tenant/workstream evidence.

## Actor-adapter authorization rules

- `surface_action`: tenant admins and scoped SaaS support operators may use structured surfaces when the backend authorizes the selected scope and action.
- `api_call`: protected browser/API calls re-evaluate `AuthContext`; route visibility and deep links are never authorization.
- `human_chat_tool_plan`: read-only investigation plans require explicit confirmation of the proposed plan, selected scope, and redaction level before any tool executes. Export/support-access actions require the separate approval gate described below.
- `agent_tool_call`: allowed only for bounded read/search/correlation/summary operations named in `tools/governed-tools.md`; the model receives redacted results and safe handles, not raw secrets or hidden records.
- `consumer_reaction`, `projection_update`, `internal_call`, and `timer_invocation`: system adapters require service provenance, tenant/correlation context, and trace emission.

## Export and support-access approval

- Redacted tenant export requests are allowed only where policy grants the caller `trace.export.redacted.request` for the selected tenant. Export preparation is asynchronous and produces an approval-required, denied, or redacted-result surface.
- Sensitive/raw payload export is not a default tenant-admin capability. It requires an explicit approval policy and `trace.sensitive.export` grant; otherwise it is denied or deferred as not-current.
- SaaS support access must be granted, scoped, time-bounded, reviewable, and audit-traced. Support operators cannot approve their own support access or use Audit/Trace to bypass tenant/support-access policy.

## Denials

Disabled users, inactive memberships, missing selected context, non-authorized roles, expired support access, missing capability grants, cross-tenant/customer filters, hidden/expired trace references, unsupported export attempts, unconfirmed chat plans, missing agent tool-boundary grants, and unsupported internal callers are denied server-side.

Denied requests return safe feedback and emit audit/work trace evidence without exposing protected data, hidden counts, hidden ids, or internal policy implementation details. Authorized denial investigation surfaces may show denial reason, policy reference, actor-adapter source, and remediation path when visible under the caller's scope.

## Retention and immutability

Default audit/work trace retention is 90 days unless a governed tenant policy sets another value. Trace records are immutable until retention expiry. Retention changes, export requests, support-access review, trace-gap findings, and runtime-validation evidence links are themselves audit-traced.
