# Workstream Manifest Schema

Use a workstream manifest as the machine-readable index for a workstream-centered app-description tree. In the current-intent graph, keep the manifest mapped to the owning domain/workstream artifacts; in the existing reusable template/validator compatibility tree, the manifest path remains `app-description/12-workstreams/workstream-manifest.json`. Markdown files remain the human-readable contracts; the manifest is the lightweight referential-integrity spine used by validators and implementation tasks. The JSON Schema file is `./workstream-manifest.schema.json`; the repository validator adds cross-file checks beyond JSON Schema validation.

Validate it with:

```bash
tools/validate-workstream-manifest.py app-description
```

`tools/validate-workstream-contracts.sh app-description` also invokes this manifest validator. Surface contracts have separate process/app modes:

```bash
tools/validate-surface-contracts.sh --mode template app-description
tools/validate-surface-contracts.sh --mode implementation app-description
```

Implementation mode is readiness-aware. Workstreams at `capability-ready` and above must have complete `workstreamToolCatalog` entries and `surfaceActionMappings`, and must not rely on unresolved deferred result surfaces for their claimed scope.

## Required top-level shape

```json
{
  "manifestVersion": "workstream-manifest/v1",
  "idConventions": { "workstreamId": "..." },
  "workstreams": []
}
```

## Required workstream fields

| Field | Meaning |
| --- | --- |
| `workstreamId` | Stable kebab-case product vertical id, for example `user-admin`. |
| `displayName` | Human-readable launcher/dashboard name. |
| `classification` | `foundation` or `domain-specific`. |
| `functionalAgentId` | Exactly-one owner, kebab-case and ending in `-agent`, for example `user-admin-agent`. |
| `managedAgentDefinitionId` | Tenant-governed managed-agent record id; may differ from `functionalAgentId` but must be explicit. |
| `defaultSurfaceId` | Initial dashboard/briefing/system-message surface. |
| `readiness` | One of the readiness labels from `./workstream-contract.md`. |
| `instanceScope` | Runtime instance key semantics. |
| `authorizedActors` | Non-empty role/capability/AuthContext summary. |
| `icon` | `iconId`, `visualHint`, `accentColorToken`, `tooltip`, and `ariaLabel`. |
| `attentionCategories` | Workstream-local category ids, or `[]` only with an explicit non-attention explanation in markdown. Each local id must be mapped in `attention-and-dashboards.md` producer contracts to a canonical `AttentionItem.category`, severity rules, producer, and lifecycle behavior. |
| `surfaces` | Surface ids owned/reused by this workstream; must include `defaultSurfaceId`. |
| `capabilities` | Capability family ids used by this workstream. |
| `workstreamToolCatalog` | Lightweight governed-tool catalog for implementability. Optional below `capability-ready`; non-empty and required at `capability-ready`, `expertise-ready`, `runtime-ready`, and `production-ready`. |
| `surfaceActionMappings` | Lightweight surface action/governed-tool mappings for implementability. Optional below `capability-ready`; non-empty and required at `capability-ready`, `expertise-ready`, `runtime-ready`, and `production-ready`. |
| `surfaceIntentRoutes` | Optional lightweight composer-to-surface route catalog. Recommended for every composer-enabled workstream at `surface-ready` and above; entries open/refresh/prepopulate surfaces and must declare no-mutation behavior. |
| `readinessEvidence` | Explicit runtime evidence. Required at `runtime-ready` and `production-ready`; optional and usually omitted below those levels. |
| `expertiseBundle` | Optional functional-agent expertise artifact path/name; in the compatibility template this is a file name under `12-workstreams/workstream-expertise/`; required when LLM-backed behavior claims expertise readiness. |
| `internalWorkers` | Optional structured internal worker entries. Omit or use `[]` when no internal/background worker behavior is claimed; string-only worker ids are not valid. |
| `traceability` | Markdown traceability map paths that mention this workstream and surfaces. |
| `localValidation` | Commands/runtime-validation smoke needed before raising readiness. |

## ID taxonomy

Use distinct ids and map them when they differ:

| ID | Example | Owner |
| --- | --- | --- |
| `workstreamId` | `user-admin` | Workstream product contract. |
| `functionalAgentId` | `user-admin-agent` | Left-rail/workstream owner. |
| `managedAgentDefinitionId` | `user-admin-agent` or `agent.user-admin` | Governed runtime agent behavior record. |
| Akka component class | `WorkstreamRuntimeAgent` | Java runtime implementation detail. |
| `surfaceId` | `user-admin-dashboard` | Structured surface contract. |
| `capabilityId` | `secure-tenant-user-foundation` | Backend authority family. |
| `governedToolId` | `useradmin.invitation.create` | Executable semantic operation inside a capability. |

Do not silently substitute one id family for another. If Java/frontend examples use adapter-specific ids, map them back to the manifest ids in app-description or traceability files.

## Workstream tool catalog template

`workstreamToolCatalog` is the lightweight machine-readable bridge from a workstream to its governed backend operations. It does not replace the full capability/governed-tool contract; it gives validators and implementation tasks a compact list of executable semantic operations, actor adapters, confirmation/approval policy, idempotency, transaction boundaries, result surfaces, partial-failure behavior, and trace sources.

Required for `capability-ready`, `expertise-ready`, `runtime-ready`, and `production-ready` workstreams:

```json
{
  "governedToolId": "useradmin.invitation.create",
  "capabilityId": "secure-tenant-user-foundation",
  "actorAdapters": ["surface_action", "human_chat_tool_plan"],
  "confirmationRequired": true,
  "approvalPolicy": "Organization Admin or Customer Admin within selected AuthContext scope; policy approval when invite risk exceeds threshold",
  "idempotency": "client-generated invitation request id",
  "transactionBoundary": "invitation creation, outbox enqueue, attention/audit updates are one governed operation with retry-safe idempotency",
  "resultSurfaceId": "system_message",
  "partialFailureBehavior": "return a safe partial-failure system_message and preserve audit/correlation evidence",
  "traceSources": ["surface_action", "human_chat_tool_plan"]
}
```

Allowed `actorAdapters` and `traceSources` values are `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, `api`, `workflow`, `timer`, `consumer`, `mcp`, and `internal`.

## Surface action mapping template

`surfaceActionMappings` is the lightweight machine-readable bridge from manifest surfaces to governed backend authority. It intentionally does not duplicate full surface contracts from `./structured-surface-contracts.md`; it exists so validators and implementation tasks can see that consequential edges have an explicit capability/governed-tool path.

Required for `capability-ready`, `expertise-ready`, `runtime-ready`, and `production-ready` workstreams:

```json
{
  "surfaceId": "user-admin-dashboard",
  "actionId": "invite-user",
  "capabilityId": "secure-tenant-user-foundation",
  "governedToolId": "useradmin.invitation.create",
  "exposureChannel": "browser-tool",
  "authBasis": "Organization Admin or Customer Admin within selected AuthContext scope",
  "actorAdapter": "surface_action",
  "confirmationRequired": true,
  "approvalPolicy": "approval required for risky or policy-conflicting invitations",
  "idempotency": "client-generated invitation request id",
  "transactionBoundary": "one authorized governed-tool transaction per invitation request id",
  "resultSurfaceId": "system_message",
  "partialFailureBehavior": "show safe recovery copy and preserve audit/correlation evidence",
  "traceSource": "surface_action",
  "traceRequired": true
}
```

Allowed `exposureChannel` values are `browser-tool`, `agent-tool`, `workflow-tool`, `timer-tool`, `consumer-tool`, `MCP-tool`, `internal-tool`, `api`, `surface-request`, and `human_chat_tool_plan`. Allowed `actorAdapter` values are `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, `api`, `workflow`, `timer`, `consumer`, `mcp`, and `internal`.

## Surface intent route template

`surfaceIntentRoutes` is the lightweight machine-readable bridge from composer prompt patterns to safe structured surface opens/prefills. It does not submit the surface action; it documents deterministic routing before model fallback.

Recommended for composer-enabled workstreams:

```json
{
  "intentId": "user-admin.organization.create.open",
  "promptExamples": ["create organization \"Org 1\"", "new organization Org 1"],
  "targetSurfaceId": "surface-user-admin-organization-create",
  "requiredCapabilityId": "saas_owner.tenant.manage",
  "prefillFields": ["organizationName", "reasonHint"],
  "ambiguityBehavior": "open blank create surface or ask for clarification; never guess hidden targets",
  "forbiddenEffects": "does not create, invite, send, approve, archive, activate, or mutate before human submit",
  "traceRequired": true
}
```

## Runtime readiness evidence template

`readinessEvidence` is required only for `runtime-ready` and `production-ready` workstreams. Do not fill it with planned checks; lower readiness levels should keep evidence in `localValidation` until the real path works.

```json
{
  "localCommands": ["mvn test", "npm --prefix frontend run build"],
  "apiUiSmokePath": "signed-in Organization Admin opens User Admin and completes invite denial/success smoke through /api and UI",
  "providerSecurityFailClosedCheck": "missing provider/security config returns actionable fail-closed system_message and audit trace",
  "traceEvidence": "AdminAuditEvent and AgentWorkTrace ids visible from audit-trace-explorer"
}
```

## Internal worker template

When `internalWorkers` is non-empty, each manifest entry is structured and the current-intent workstream worker/agent artifacts (or compatibility `12-workstreams/internal-agents.md`) should include matching detail for the worker:

```json
{
  "workerId": "access-review-triage-worker",
  "substrate": "AutonomousAgent task",
  "trigger": "user-admin-dashboard access review action or scheduled stale-access signal",
  "authorityBasis": "service actor constrained to secure-tenant-user-foundation within selected tenant/context",
  "behaviorProfile": "workers/access-review-triage-worker.md#behavior-profile",
  "reasoningEngine": "model",
  "capabilityId": "secure-tenant-user-foundation",
  "governedToolId": "useradmin.access_review.triage",
  "actorAdapter": "internal",
  "idempotency": "worker id + trigger id + selected tenant/context scope",
  "transactionBoundary": "each governed-tool invocation is authorized, idempotent, and traced independently",
  "traceSource": "internal",
  "progressSurfaceId": "system_message",
  "resultSurfaceId": "decision-card",
  "failureSurfaceId": "system_message"
}
```

`behaviorProfile` and `reasoningEngine` are optional compatibility fields in the compact manifest, but the matching worker artifact should define them before implementation scope. Omit `internalWorkers` or use `[]` when the workstream has no internal/background worker behavior. A string-only worker id is not enough once internal work is claimed.

## Attention producer template

When `attentionCategories` is non-empty, the current-intent workstream dashboard/attention artifact (or compatibility `12-workstreams/attention-and-dashboards.md`) should include producer rows with:

```text
producerId and version
owning workstream and functional agent
source family
local manifest category id mapped to canonical AttentionItem.category
severity/lifecycle rules using info | warning | urgent | blocked
idempotency key strategy
source/evidence refs
visible roles/capabilities and redaction profile
My Account / left rail effect
trace records
tests for replay, duplicate, forbidden, stale, and tenant isolation
```

## Validator scope

The validator is intentionally lightweight. It checks required fields, id syntax, uniqueness, conditional mapping/evidence requirements, `workstreamToolCatalog`/`surfaceActionMappings`, structured internal worker entries, expertise bundle presence, and references into the core markdown maps. It does not prove runtime readiness; runtime readiness still requires the real Akka/API/UI evidence named by `readinessEvidence` at `runtime-ready` and above.
