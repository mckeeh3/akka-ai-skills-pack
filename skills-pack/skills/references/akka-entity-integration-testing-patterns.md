# Akka entity integration testing shared patterns

Use these patterns from `akka-ese-integration-testing` and `akka-kve-integration-testing`.

## Test harness rules

Integration tests should:

- extend `TestKitSupport`;
- use `httpClient` for endpoint tests;
- use `componentClient` for internal component-to-component checks;
- verify externally visible behavior, not only internal implementation details.

## Endpoint test pattern

Use when validating HTTP behavior:

1. Invoke endpoint routes with `httpClient`.
2. Send request bodies as API request records.
3. Deserialize responses as API response records.
4. Assert HTTP success or expected failure behavior.

Curated reference shape:

- `../examples/akka-components/src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`
- `../examples/akka-components/src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`

## Consumer-driven flow pattern

Use when an entity event or state change triggers work in another component:

1. Drive the flow through the edge component or endpoint.
2. Wait for downstream state to appear.
3. Assert final state using `componentClient` or endpoint reads.
4. Account for asynchronous propagation.

Curated reference shape:

- `../examples/akka-components/src/main/java/ai/first/application/foundation/workstream/WorkstreamEventAttentionConsumer.java`
- `../examples/akka-components/src/test/java/ai/first/application/foundation/workstream/WorkstreamEventBackboneServiceTest.java`

## Waiting strategy

When the flow is asynchronous:

- do not assume immediate consistency;
- poll or await until expected state is visible;
- keep timeouts explicit and reasonable.

## Generated SaaS test set

Derive tests from the capability contract, not only from entity mechanics:

- authorized success with tenant/customer scoped identifiers or state;
- validation failure and safe reply shape;
- no-op/idempotent duplicate command behavior;
- forbidden or cross-tenant attempts when the entity method is directly exposed or called by endpoints/tools/workflows;
- audit/work-trace expectations for consequential commands, denials, and data access;
- exposure parity for HTTP/gRPC/MCP/tool/surface flows when the entity backs those surfaces.

## Anti-patterns

Avoid:

- using `componentClient` to test endpoint HTTP contracts;
- assuming consumer-driven flows are synchronous;
- exposing domain records as external API test payloads when endpoint-specific types exist;
- testing only happy paths.
