# Policies: Audit/Trace

## Uses

Global policies: `../../../../../global/policies/foundation-security-and-governance.md`.

## Active bindings

Applies to every Audit/Trace route, surface action, chat plan, agent tool call, API/internal call, projection read, consumer/timer action, export request, support-access review, and runtime-validation evidence link:

- tenant-customer-isolation;
- backend-authorization-default-deny;
- frontend-secret-boundary;
- trace-sensitive-data-redaction;
- support-access-least-privilege-and-review;
- immutable-audit-records-until-retention-expiry;
- deterministic-summary-search-only;
- no-hidden-target-enumeration;
- confirmed-human-chat-tool-plan-for-read-only-investigation;
- tool-permission-boundary-required-for-agent-tool-calls;
- export-redacted-by-default-and-approval-gated-for-sensitive-payloads;
- runtime-validation-evidence-linking-without-secret-disclosure;
- trace-gap-detection-and-diagnosability.

## Role and scope policy

- Tenant admins may inspect tenant-scoped traces and support-access evidence for their tenant, with redaction defaults and sensitive-detail grants enforced by backend policy.
- SaaS support operators may inspect tenant traces only under active support-access or platform support scope; support-access use is visible for later review.
- Agents may use only explicitly granted model-safe tools. Prompt/skill/reference/model text cannot grant roles, support scope, tenant scope, export approval, or sensitive read authority.

## Denial evidence

Denied actions must produce durable trace evidence. Denied trace records visible to an authorized investigator include safe denial reason, actor adapter, governed tool/capability, policy reference, selected AuthContext/support scope summary, redaction class, and correlation id. User-facing denial responses must not expose protected data, hidden trace existence, hidden cross-tenant/customer ids, raw policy internals, secrets, or provider credentials.

## Export and support-access policy

- Redacted exports require explicit export request authority and produce approval-required, queued, ready, denied, expired, or failed states.
- Sensitive/raw payload export requires explicit `trace.sensitive.export` policy approval; otherwise it is denied or deferred as unavailable in the current scope.
- Support operators cannot approve their own support access or export requests.
- Support-access grant/use/revoke/expiry and trace reads performed under support access are reviewable by authorized tenant/SaaS administrators.

## Runtime-validation and trace-gap policy

Runtime-validation runs may link evidence into Audit/Trace only with safe refs, tenant/workstream scope, validation status, and source-alignment impact. Missing, malformed, delayed, or uncorrelated source events produce trace-gap attention and diagnostics instead of fabricated evidence.
