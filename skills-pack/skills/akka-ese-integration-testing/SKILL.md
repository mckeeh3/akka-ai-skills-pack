---
name: akka-ese-integration-testing
description: Write integration tests for Akka event sourced entity flows using TestKitSupport, httpClient, and componentClient. Use for endpoint round trips, consumer-driven flows, and end-to-end validation of EventSourcedEntity interactions.
---

# Akka ESE Integration Testing

Use this skill for service-level tests involving event sourced entities.

## Generated SaaS input contract

For generated full-stack AI-first SaaS implementation work, apply `../references/generated-saas-input-contract.md` before coding. If the selected task lacks the required workstream/capability/AuthContext/surface/trace/test contract and does not explicitly defer it, route back to `agent-workstream-apps` + `capability-first-backend` or block for task-brief repair instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/event-sourced-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`

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

Pattern references:
- `WorkstreamEndpointIntegrationTest`
- `AdminEndpointIntegrationTest`

## Consumer-driven flow pattern

Use when an event from one entity triggers work in another component:
- drive the flow through the edge component or endpoint
- wait for the downstream state to appear
- assert final state using `componentClient` or endpoint reads
- account for asynchronous propagation

Pattern reference:
- `WorkstreamEventAttentionConsumerIntegrationTest`

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

## Generated SaaS test set

For generated SaaS entity work, derive tests from the capability contract, not only from entity mechanics:
- authorized success with tenant/customer scoped identifiers or state;
- validation failure and safe reply shape;
- no-op/idempotent duplicate command behavior;
- forbidden or cross-tenant attempts when the entity method is directly exposed or called by endpoints/tools/workflows;
- audit/work-trace expectations for consequential commands, denials, and data access;
- exposure parity for HTTP/gRPC/MCP/tool/surface flows when the entity backs those surfaces.


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
