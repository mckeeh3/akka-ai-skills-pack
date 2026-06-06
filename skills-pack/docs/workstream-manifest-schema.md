# Workstream Manifest Schema

Use `app-description/12-workstreams/workstream-manifest.json` as the machine-readable index for a workstream-centered app-description tree. Markdown files remain the human-readable contracts; the manifest is the lightweight referential-integrity spine used by validators and implementation tasks. The JSON Schema file is `./workstream-manifest.schema.json`; the repository validator adds cross-file checks beyond JSON Schema validation.

Validate it with:

```bash
tools/validate-workstream-manifest.py app-description
```

`tools/validate-workstream-contracts.sh app-description` also invokes this manifest validator.

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
| `expertiseBundle` | Optional file name under `12-workstreams/workstream-expertise/`; required when LLM-backed behavior claims expertise readiness. |
| `internalWorkers` | Optional internal worker ids supporting this workstream. |
| `traceability` | Markdown traceability map paths that mention this workstream and surfaces. |
| `localValidation` | Commands/manual smoke needed before raising readiness. |

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

## Internal worker template

When `internalWorkers` is non-empty, `12-workstreams/internal-agents.md` should include a row or section for each worker with:

```text
workerId
owningWorkstreamId
trigger/source surface or event
selected substrate, usually AutonomousAgent for durable model work
input/output schema
capabilityId and governedToolId
service or AuthContext authority basis
model config/prompt/skill/reference/tool boundary when model-backed
progress/result/failure/stale surfaces
attention effects
audit/work trace fields
tests and local validation
```

## Attention producer template

When `attentionCategories` is non-empty, `12-workstreams/attention-and-dashboards.md` should include producer rows with:

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

The validator is intentionally lightweight. It checks required fields, id syntax, uniqueness, expertise bundle presence, and references into the core markdown maps. It does not prove runtime readiness; runtime readiness still requires the local Akka/API/UI validation named by `localValidation`.
