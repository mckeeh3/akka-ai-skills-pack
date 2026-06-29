# Audit/Trace system worker

workerId: audit-trace-system-worker
workerType: system
reasoningEngine: deterministic
scope: local-workstream
owningDomain: core-starter
owningWorkstream: audit-trace
runtimeReadiness: description-ready

## Purpose

The Audit/Trace system worker represents deterministic backend/API/projection/consumer/runtime-validation participants that ingest immutable audit/work trace facts, execute authorized search/read/correlation/export workflows, enforce redaction and support-access policy, detect trace gaps, and emit evidence for every read, denial, summary, support-access, export, and runtime-validation path.

## Responsibility

- Owns/does:
  - Persist human, chat-plan, agent-tool, workflow, consumer, API, internal, policy, support-access, export, runtime-validation, denial, and trace-gap events as durable trace facts until retention expiry.
  - Build tenant/support-scoped search, timeline, correlation, denial-investigation, support-access review, and runtime-validation evidence projections.
  - Reauthorize every read/export/support-access action server-side.
  - Apply redaction, secret-never-store, sensitive-detail, retention, and no-enumeration rules.
  - Execute asynchronous redacted export preparation where policy permits.
  - Emit read, denial, partial-failure, trace-gap, retention-expiry, and runtime-validation evidence traces.
- Does not own/do:
  - Let frontend state or agent text authorize access, index full payload keyword text, expose secrets/tokens/provider credentials, manually edit/delete audit records, or approve support/export decisions.

## Authority and scope

- authorityLevel: deterministic backend execution under authenticated/service authority; no discretionary support/admin authority beyond policy.
- AuthContext scope: selected tenant/Organization, optional customer/account, support-access grant, service provenance for ingestion/projection/retention/runtime-validation.
- Allowed decisions: authorize/deny, validate filters, redact fields, detect trace gaps, link runtime-validation evidence, classify retention-expired/not-found/redacted, return idempotent export/read states.
- Requires approval when: export/support-access/sensitive detail policy requires it.
- Denied/hidden behavior: safe forbidden/not-found-redacted/approval-required/validation surfaces and denial traces without protected existence leakage.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| Protected HTTP/workstream endpoint | `api_call` | browser/API | `api_call` | Resolves selected tenant/AuthContext/support scope server-side. |
| Internal audit/work trace service/store | `internal_call` | backend | `internal_call` | Records immutable events and reads scoped DTOs. |
| Consumer/projection | `consumer_reaction` / `projection_update` | event/entity/topic stream | `consumer_reaction` | Normalizes source workstream events into searchable projections. |
| Timer/timed action | `timer_invocation` | scheduled backend | `timer_invocation` | Applies retention expiry and trace-gap checks. |
| Runtime-validation linker | `internal_call` | validation evidence ingestion | `runtime_validation` | Links validation runs/evidence to trace/search/source-alignment views. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation | Idempotency/transaction boundary |
|---|---|---|---|---|---|
| `search-audit-traces` | `audit-and-trace-investigation` | `api_call`, `internal_call` | read scoped audit summaries | none | read-only; search trace emitted |
| `search-work-traces` | `audit-and-trace-investigation` | `api_call`, `internal_call` | read scoped work traces | none | read-only |
| `read-audit-trace-detail` | `audit-and-trace-investigation` | `api_call`, `internal_call` | read authorized detail | sensitive warning/redaction | read-only detail trace emitted |
| `read-work-trace-detail` | `audit-and-trace-investigation` | `api_call`, `internal_call` | read authorized work trace | sensitive warning/redaction | read-only |
| `lookup-trace-correlation` | `audit-and-trace-investigation` | `api_call`, `internal_call`, `projection_update` | read correlation timeline | none | read-only projection query |
| `investigate-denied-trace-access` | `audit-and-trace-investigation` | `api_call`, `internal_call` | read denial evidence | none | read-only denial trace emitted |
| `summarize-investigation-evidence` | `audit-and-trace-investigation` | `api_call`, `internal_call` | assemble authorized summary evidence | confirmation/approval inherited from caller adapter | summary result fact |
| `request-redacted-trace-export` | `audit-and-trace-investigation` | `api_call`, `internal_call` | prepare/request redacted export | approval gate where policy requires | idempotent export workflow |
| `review-support-access-traces` | `audit-and-trace-investigation` | `api_call`, `internal_call` | read support-access evidence | none for read | read-only review trace emitted |
| trace ingestion / projection / retention / gap internals | `audit-and-trace-investigation` | `internal_call`, `consumer_reaction`, `projection_update`, `timer_invocation` | system bookkeeping | service provenance/policy | append immutable record, update projection, expire by retention, or emit trace-gap fact |
| runtime-validation evidence link internals | `audit-and-trace-investigation` | `internal_call` | link validation evidence | service provenance/policy | append validation evidence fact |

## Policies, constraints, and fail-closed behavior

- Tenant/customer isolation and support-access checks are server-side and default-deny.
- Redaction strips secrets/tokens/provider credentials/frontend-secret material from stored/displayed summaries and exports.
- Projection lag or missing source evidence emits trace-gap diagnostics rather than fabricated timeline entries.
- Export requests are idempotent; repeated request/scope/correlation returns existing state.
- Retention expiry is the only normal removal path for immutable trace records.

## Audit and work traces

Record worker id/type, adapter/source, tenant/admin/support/service identity, selected AuthContext/support grant, filters/safe handles, governed tool/capability id, authorization decision, redaction/sensitive flags, support-access/export/runtime-validation refs, status/error, validation/denial reason, idempotency/no-op outcome, trace-gap classification, and result surface.

## Tests and manual runtime scenarios

- API/surface/chat/agent trigger → system worker adapter → governed tool/internal trace operation → `audit-and-trace-investigation` → typed surface/event/trace evidence.
- Consumer/projection event missing correlation → trace-gap attention and diagnostic evidence.
- Runtime-validation result links to source-alignment evidence without exposing secrets.
- Export request approval/denial/result emits full trace chain.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Agents: `../agents/functional-agent.md`
- Tools: `../tools/governed-tools.md`
- Capabilities: `../../../capabilities/audit-and-trace-investigation.md`
- Policies: `../policies/policy-bindings.md`
- Traces: `../traces/work-traces.md`
- Tests: `../tests/coverage.md`
- Akka components/API/frontend source-alignment: `../realization/source-alignment.md`
