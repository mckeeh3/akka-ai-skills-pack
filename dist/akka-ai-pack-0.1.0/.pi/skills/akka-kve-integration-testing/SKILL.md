---
name: akka-kve-integration-testing
description: Write integration tests for Akka key value entity flows using TestKitSupport, httpClient, and componentClient. Use for endpoint round trips, consumer-driven flows, and end-to-end validation of KeyValueEntity interactions.
---

# Akka KVE Integration Testing

Use this skill for service-level tests involving key value entities.

## Required reading

Read these first if present:
- `akka-context/sdk/key-value-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../../../src/test/java/com/example/application/DraftCartIntegrationTest.java`
- `../../../src/test/java/com/example/application/DraftCartCheckoutConsumerIntegrationTest.java`
- `../../../src/test/java/com/example/application/PurchaseOrderEndpointIntegrationTest.java`

## Test harness rules

Integration tests should:
- extend `TestKitSupport`
- use `httpClient` for endpoint tests
- use `componentClient` for internal component-to-component calls
- verify externally visible behavior, not internal implementation details only

## Endpoint test pattern

Use when validating HTTP behavior:
- invoke endpoint routes with `httpClient`
- send request bodies as API request records
- deserialize responses as API response records
- assert HTTP success or expected failure behavior

Repository examples:
- `DraftCartIntegrationTest`
- `PurchaseOrderEndpointIntegrationTest`

## Consumer-driven flow pattern

Use when a state change from one entity triggers work in another component:
- drive the flow through the edge component or endpoint
- wait for the downstream state to appear
- assert final state using `componentClient` or endpoint reads
- account for asynchronous propagation

Repository example:
- `DraftCartCheckoutConsumerIntegrationTest`

## Waiting strategy

When the flow is asynchronous:
- do not assume immediate consistency
- poll or await until the expected state is visible
- keep timeouts explicit and reasonable

## What to cover

Prefer these categories:
1. endpoint round trip success
2. validation failure mapped to HTTP behavior
3. update flow changing persistent state
4. end-to-end consumer or workflow driven interaction
5. idempotent or repeated command behavior when important

## Anti-patterns

Avoid:
- using `componentClient` to test endpoint HTTP contracts
- assuming consumer-driven flows are synchronous
- exposing domain records as external API test payloads when endpoint-specific types exist
- testing only happy paths

## Review checklist

Before finishing, verify:
- test extends `TestKitSupport`
- endpoint tests use `httpClient`
- internal follow-up checks use `componentClient` where appropriate
- async flows wait explicitly
- validation failures are asserted, not ignored
