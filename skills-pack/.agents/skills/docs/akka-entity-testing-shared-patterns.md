# Akka Entity Testing Shared Patterns

Use this note to keep Event Sourced Entity and Key Value Entity testing skills compact. Load the entity-specific skill for SDK names and state/event mechanics, then apply the common patterns below.

## Common test levels

- **Unit/component mechanics**: use the entity testkit when the behavior under test is a command handler, read handler, no-op, delete, TTL, or state/event transition.
- **Endpoint round trip**: use `TestKitSupport` plus `httpClient` when HTTP status mapping, request/response DTOs, JWT/request context, authorization, validation, audit, or browser-facing behavior matters.
- **Component flow**: use `componentClient` when testing an internal capability path or workflow/consumer interaction without HTTP routing concerns.
- **Consumer/view propagation**: trigger the upstream component, await projected/downstream state, and assert the externally visible query/read result. Account for eventual consistency.

## Generated SaaS coverage

For generated SaaS work, tests must prove the capability contract, not only Akka mechanics:

- authorized success with tenant/customer scoped state;
- validation failure with safe caller-visible errors;
- forbidden/wrong-tenant behavior with no leakage;
- idempotent no-op behavior for retried or already-applied internal messages;
- audit/work-trace emission where required;
- provider/configuration failures fail closed rather than silently using fixture behavior.

## Curated examples

Current curated examples live under `examples/akka-components/**`. They are read-only source references, not an independently buildable app and not a source tree to copy wholesale. When a focused skill needs a test example, prefer concrete files that exist in that tree, such as:

- `src/test/java/ai/first/application/foundation/agent/AgentDefinitionEntityTest.java`
- `src/test/java/ai/first/application/foundation/agent/GovernedDocumentEntityTest.java`
- `src/test/java/ai/first/application/foundation/agent/ManifestBoundaryEntityTest.java`
- `src/test/java/ai/first/application/foundation/workstream/WorkstreamEventBackboneServiceTest.java`
- `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`

If the current curated tree has no direct example for a pattern, label the guidance as a target-project pattern instead of naming a nonexistent repository class.
