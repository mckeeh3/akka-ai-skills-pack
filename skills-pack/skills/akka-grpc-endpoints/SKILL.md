---
name: akka-grpc-endpoints
description: Orchestrate Akka Java SDK gRPC endpoint work across protobuf contracts, unary methods, component calls, request context, streaming, and integration testing. Use when the task spans more than one gRPC endpoint concern.
---

# Akka gRPC Endpoints

Use this as the top-level skill for Akka Java SDK gRPC endpoint work when gRPC is already selected as an exposure surface for one or more backend capabilities.

For broad product, PRD, feature, or service-contract requests, route through `capability-first-backend` and `akka-solution-decomposition` before implementing protobuf services. Do not start from RPC methods when capability authority, scope, side effects, approval, and audit semantics are still unclear.

## Goal

Generate or review gRPC endpoint code that is:
- correct for Akka SDK 3.6.x
- explicit about protobuf contracts in `src/main/proto`
- safe at the edge with clear gRPC status mapping
- easy for AI agents to extend without reading unrelated files
- backed by integration tests that use `getGrpcEndpointClient(...)`

## AI-first substrate role

In AI-first SaaS implementations, use gRPC endpoints for typed service contracts around goal execution, plan control, approvals, exceptions, policy-governance APIs, audit/search APIs, streaming status, or cross-service outcome reporting when protobuf-first integration is preferred.

Keep gRPC endpoints as contract adapters. Validate and map protobuf messages, identity, metadata, and status codes at the edge, but persist consequential AI-first facts through entities or workflows and project query/reporting shapes through views. Use explicit protobuf messages for evidence, risk, confidence, approval, trace, and outcome fields when those are part of the service boundary.

Pair AI-first gRPC endpoints with:
- `akka-grpc-proto-design` for stable contracts that expose authority, evidence, trace, and outcome fields deliberately
- `akka-grpc-endpoint-request-context` or `akka-grpc-endpoint-jwt` when caller identity or service authority matters
- `akka-grpc-endpoint-streaming` for live plan progress, supervision feeds, or audit/event streams
- component-client endpoint skills for typed access to workflows, views, agents, and entities


## Capability-first exposure rule

Treat every gRPC method as an `api_call`, service, or `internal_call` actor adapter for a named governed tool inside a backend capability, not as the governed tool or capability itself. Before adding or changing a method, identify the responsible worker/harness, actor adapter, governed tool id, capability id, allowed actors/callers, `AuthContext`, tenant/customer scope, protobuf input/output schema, side effects, idempotency, approval policy, audit/trace obligations, selected Akka implementation path, and tests.

For protected services, preserve the capability contract at the edge: authenticate the caller or service identity, resolve or receive the selected tenant/customer context, authorize the required role/scope/capability, validate protobuf messages, redact replies, map denials to explicit gRPC statuses such as `UNAUTHENTICATED` or `PERMISSION_DENIED`, and record required audit/work-trace events before calling components. Metadata, service names, and method names are not authorization controls.

When the same capability is also exposed through UI, HTTP, agent tools, workflows, MCP, timers, or consumers, keep authority, validation, idempotency, approval, and audit semantics identical across surfaces. Consequential gRPC actions should call the workflow/entity/approval substrate that enforces policy instead of committing side effects only in endpoint code.

## Required reading before coding

Read these first if present:
- `akka-context/sdk/grpc-endpoints.html.md`
- `akka-context/sdk/access-control.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- existing project protobuf files under `src/main/proto/**`
- existing project gRPC endpoints under `src/main/java/**/api/*GrpcEndpointImpl.java`
- matching gRPC endpoint tests under `src/test/java/**`

## Companion skills

Load the companion skill that matches the current task:

- `akka-grpc-endpoint-component-client`
  - gRPC endpoints that call entities or views through `ComponentClient`
- `akka-grpc-endpoint-request-context`
  - endpoints extending `AbstractGrpcEndpoint` to read principals, metadata, JWT claims, or tracing
- `akka-grpc-endpoint-jwt`
  - bearer-token validation and claim-aware gRPC endpoint behavior
- `akka-grpc-endpoint-streaming`
  - server-streaming methods returning `Source<Reply, NotUsed>`
- `akka-grpc-endpoint-testing`
  - `TestKitSupport` integration tests using `getGrpcEndpointClient(...)`
- `akka-grpc-proto-design`
  - protocol design, schema evolution, common protobuf types, and external protobuf dependencies

Security and deployment topics are in scope, but some are primarily routed through official docs rather than large local examples:
- ACL design for public vs service-only endpoints
- JWT validation and claim-based behavior
- TLS certificates for production exposure

Use this top-level skill plus the official docs when those are the main concern.

## Default package layout

Use the fixed Java base package `ai.first` for this SaaS Foundation App repository and downstream generated code. Keep package declarations, imports, tests, and source paths under `ai.first`; do not infer package names from examples.

Typical layer paths are:
- `<base>.domain`
- `<base>.application`
- `<base>.api`
- `src/main/proto`

Rules:
- protobuf contracts live in `src/main/proto`
- gRPC endpoint implementation classes belong in `api`
- endpoint classes implement generated service interfaces
- endpoint class names end with `Impl`
- domain records stay in `domain`
- component orchestration stays in `application`, not in the endpoint

## Core rules

1. A gRPC endpoint has `@GrpcEndpoint` and must not have `@Component`.
2. Add `@Acl(...)` explicitly.
3. Define service and message contracts in `.proto` files under `src/main/proto`.
4. Implement the generated service interface in the endpoint class.
5. Extend `AbstractGrpcEndpoint` only when request context access is needed.
6. Return protobuf-generated message types from endpoint methods.
7. Convert internal domain or component types to protobuf with private `toApi(...)` helpers.
8. Signal expected client failures with `GrpcServiceException` or `StatusRuntimeException`.
9. Keep production endpoint code synchronous for unary calls.
10. For server streaming, return `Source<Reply, NotUsed>`.
11. Design streams so clients can resume explicitly if reconnects matter.
12. For generated SaaS services, extract or receive caller context and authorize every protected method against tenant/customer scope and required capability before calling components.
13. Map missing authentication, disabled users, forbidden roles/scopes, and cross-tenant/customer attempts to explicit gRPC denial statuses and required audit/work-trace records.
14. Test gRPC endpoints through `getGrpcEndpointClient(...)`, not `componentClient`.

## Decision guide

Choose one of these modes before coding:

### 1. Pure edge mapping endpoint
Use when the endpoint mainly maps protobuf requests to protobuf replies and does not need component calls.

Pattern to implement:
- a small service/internal status endpoint with no component dependency

### 2. Component-calling unary endpoint
Use when the endpoint translates protobuf requests into entity or view calls and maps replies back into protobuf.

Pattern to implement:
- a domain-specific endpoint that maps protobuf requests to component calls and protobuf replies

### 3. Request-context endpoint
Use when endpoint logic depends on gRPC metadata, principals, JWT claims, or tracing.

Pattern to implement:
- a service/internal endpoint that inspects principals, metadata, JWT claims, or trace headers through `requestContext()`

### 4. Streaming endpoint
Use when a gRPC method should stream multiple replies.

Pattern to implement:
- a domain-specific endpoint method that forwards a streamed view query

### 5. Protocol-design task
Use when the main work is protobuf layout, compatibility, common message types, or external proto imports.

Repository examples:
- `workstream_event_grpc_endpoint.proto`
- `internal_status_grpc_endpoint.proto`

## Final review checklist

Before finishing, verify:
- `@GrpcEndpoint` is present
- `@Acl` is present
- no `@Component` annotation is used on the endpoint
- endpoint class implements the generated interface
- endpoint class name ends with `Impl`
- protobuf contracts live in `src/main/proto`
- `AbstractGrpcEndpoint` is used only if request context is needed
- expected failures become explicit gRPC statuses
- streaming methods return `Source<Reply, NotUsed>`
- protected methods have JWT/request-context handling and backend authorization checks or are explicitly service-only through ACL
- tenant/customer ids are filtered or rejected server-side for every scoped command, query, stream, and component call
- forbidden, disabled-user, role/scope denial, cross-tenant/customer, and audit expectations are covered or explicitly delegated to foundation tests
- tests use `getGrpcEndpointClient(...)`

## Response style

When answering coding tasks:
- name the protobuf service explicitly
- call out whether the endpoint is pure edge logic or calls other components
- state how validation failures are mapped to gRPC status behavior
- list the concrete example files used as references
