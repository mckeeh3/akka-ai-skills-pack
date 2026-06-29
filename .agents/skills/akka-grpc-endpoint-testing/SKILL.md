---
name: akka-grpc-endpoint-testing
description: Write Akka Java SDK gRPC endpoint integration tests using TestKitSupport and getGrpcEndpointClient(...). Use for unary behavior, status-code mapping, ACL tests, and streamed reply verification.
---

# Akka gRPC Endpoint Testing

Use this skill for gRPC endpoint integration tests.


## Lifecycle and compile boundary

Testing work belongs to the build/compile phase unless the selected task is explicitly runtime verification or runtime-validation reconciliation. Use this skill to prove the component-specific mechanics and the declared worker/harness/actor-adapter/governed-tool/capability path; do not widen a component-testing task into unrelated planning, product repair, or runtime-validation failure triage. For feature-bearing generated SaaS work, passing component tests can support `manual-ready`; `runtime-ready` still requires the real local API/UI/agent path, provider/fail-closed evidence where relevant, and reconciliation of runtime-validation findings through `../docs/runtime-validation-reconciliation.md`.

## Capability-first exposure rule

Treat every gRPC method as an `api_call`, service, or `internal_call` actor adapter for a named governed tool inside a backend capability, not as the governed tool or capability itself. Before adding or changing a method, identify the responsible worker/harness, actor adapter, governed tool id, capability id, allowed actors/callers, `AuthContext`, tenant/customer scope, protobuf input/output schema, side effects, idempotency, approval policy, audit/trace obligations, selected Akka implementation path, and tests.

For protected services, preserve the capability contract at the edge: authenticate the caller or service identity, resolve or receive the selected tenant/customer context, authorize the required role/scope/capability, validate protobuf messages, redact replies, map denials to explicit gRPC statuses such as `UNAUTHENTICATED` or `PERMISSION_DENIED`, and record required audit/work-trace events before calling components. Metadata, service names, and method names are not authorization controls.

When the same capability is also exposed through UI, HTTP, agent tools, workflows, MCP, timers, or consumers, keep authority, validation, idempotency, approval, and audit semantics identical across surfaces. Consequential gRPC actions should call the workflow/entity/approval substrate that enforces policy instead of committing side effects only in endpoint code.

## Required reading

Read these first if present:
- `../docs/app-development-lifecycle.md`
- `../docs/app-description-to-code-compile-contract.md`
- `../docs/runtime-validation-reconciliation.md` when tests are part of a runtime-validation readiness claim or remediation loop
- `akka-context/sdk/grpc-endpoints.html.md`

## Test harness rules

gRPC endpoint tests should:
- extend `TestKitSupport`
- use `getGrpcEndpointClient(GeneratedClient.class)` for normal calls
- use `getGrpcEndpointClient(GeneratedClient.class, Principal)` when caller identity matters
- assert protobuf reply content directly
- assert failure behavior through `StatusRuntimeException`

## Target-project patterns

The current curated SaaS Foundation App examples do not include gRPC runtime fixtures. Do not cite retired workstream-event gRPC classes as current repository examples. Implement these shapes in the target project when gRPC is in scope.

### Unary endpoint tests
- protobuf request-to-command mapping
- `INVALID_ARGUMENT` assertion for rejected requests

### Streaming endpoint tests
- collect stream replies with `Sink.seq()` and `testKit.getMaterializer()`
- wait for view consistency before consuming the gRPC stream

### ACL/request-context tests
- allowed service principal via `Principal.localService(...)`
- denied internet principal via `Principal.INTERNET`

## What to cover

Prefer these categories:
1. full worker/harness/actor-adapter/governed-tool/capability path for generated SaaS methods, including shared authorization, trace, and result semantics with any HTTP, UI, MCP, workflow, timer, consumer, or internal adapter for the same governed tool
2. successful unary invocation
3. protobuf field mapping and response shape
4. explicit gRPC status behavior for expected failures
5. service-only vs public ACL behavior when relevant
6. streamed reply behavior when relevant
7. eventual-consistency waits when a view backs the gRPC stream

## Anti-patterns

Avoid:
- using `componentClient` to test the gRPC API contract
- testing only happy paths
- skipping status-code assertions on failures
- leaving stream tests uncollected or unasserted

## Review checklist

Before finishing, verify:
- the test extends `TestKitSupport`
- route calls go through a generated gRPC client
- caller-principal-sensitive tests use the `Principal` overload
- failure behavior is asserted with `StatusRuntimeException`
- stream tests use `Sink.seq()` with the test materializer
