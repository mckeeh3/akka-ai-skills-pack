---
name: akka-http-endpoint-http-client-provider
description: Implement Akka Java SDK HTTP endpoints that call other HTTP services through HttpClientProvider. Use when endpoint-to-HTTP-service delegation or HTTP proxy patterns are the main concern.
---

# Akka HTTP Endpoint HttpClientProvider

Use this skill when an endpoint calls another HTTP service.

## Required reading

Read these first if present:
- `akka-context/sdk/http-endpoints.html.md`
- `akka-context/sdk/component-and-service-calls.html.md`
- `../../../src/main/java/com/example/api/ProxyGreetingEndpoint.java`
- `../../../src/test/java/com/example/application/ProxyGreetingEndpointIntegrationTest.java`

## Use this pattern when

- the endpoint delegates part of its work to another HTTP service
- the target may be another Akka service or an arbitrary base URL
- the endpoint must translate upstream responses into its own API contract

## Core pattern

1. Inject `HttpClientProvider`.
2. Create a client with `httpClientFor(serviceNameOrBaseUrl)`.
3. Make an HTTP request with `GET`, `POST`, `PUT`, `PATCH`, or `DELETE`.
4. Deserialize upstream responses as endpoint-local API records.
5. Return your own API response or HTTP error.

## Repository example

- `ProxyGreetingEndpoint`
  - uses `HttpClientProvider`
  - reads a base URL from a request header for a self-contained local example
  - delegates to another HTTP route and remaps the response

## Note on deployed services

In deployed Akka projects, prefer `httpClientFor("service-name")` when calling another Akka service in the same project.

Use absolute URLs only for arbitrary external services or for specialized local examples.

## Review checklist

Before finishing, verify:
- `HttpClientProvider` is injected where needed
- the target service name or base URL is explicit
- upstream response parsing uses endpoint-local records
- the endpoint exposes its own stable API contract
