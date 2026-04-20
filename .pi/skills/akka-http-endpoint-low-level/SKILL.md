---
name: akka-http-endpoint-low-level
description: Implement Akka Java SDK HTTP endpoints using low-level request and response APIs such as HttpResponse, HttpEntity.Strict, and advanced request handling. Use when content types, raw payloads, or fine-grained HTTP control are the main concerns.
---

# Akka HTTP Endpoint Low-Level APIs

Use this skill when an endpoint needs lower-level HTTP handling.

## Required reading

Read these first if present:
- `akka-context/sdk/http-endpoints.html.md`
- `../../../src/main/java/com/example/api/LowLevelHttpEndpoint.java`
- `../../../src/test/java/com/example/application/LowLevelHttpEndpointIntegrationTest.java`

## Use this pattern when

- the endpoint must return a custom `HttpResponse`
- the endpoint must inspect raw content types or raw bytes
- the endpoint accepts `HttpEntity.Strict`
- an advanced use case may require `HttpRequest` or manual request-body handling
- the official API surface includes async `CompletionStage` responses, but the local repository prefers synchronous endpoint handlers unless a low-level streaming case truly requires otherwise

## Core pattern

1. Return `akka.http.javadsl.model.HttpResponse` when you need explicit status/entity control.
2. Use `HttpResponses.*` helpers when they are enough.
3. Drop to the Akka HTTP model only when helper APIs are not enough.
4. Accept `HttpEntity.Strict` for custom payload handling with content-type checks.
5. Keep advanced low-level request handling small and isolated.

## Repository example

- `LowLevelHttpEndpoint`
  - lower-level `HttpResponse.create()` response construction
  - manual JSON bytes via `JsonSupport.encodeToAkkaByteString(...)`
  - `HttpEntity.Strict` request handling with content-type validation

## Important note about full `HttpRequest` handlers

The Akka SDK also supports endpoint methods that accept `akka.http.javadsl.model.HttpRequest` for streaming request handling, typically together with an injected `Materializer`.

This repository does not use that as a canonical executable example because the local endpoint conventions prefer synchronous endpoint handlers whenever possible.

When you truly need that feature:
- read `akka-context/sdk/http-endpoints.html.md`
- handle or discard the streaming entity explicitly
- keep the async/streaming logic tightly scoped to the endpoint

## Anti-patterns

Avoid:
- reaching for raw `HttpResponse.create()` when `HttpResponses.ok(...)` or `badRequest(...)` is enough
- accepting low-level request types for normal JSON APIs
- ignoring content types on binary or text payload endpoints
- leaving streaming request entities unconsumed in advanced handlers

## Review checklist

Before finishing, verify:
- low-level APIs are used only when justified
- content-type checks are explicit where relevant
- response status and entity are both intentional
- manual JSON serialization is limited to the advanced response path
