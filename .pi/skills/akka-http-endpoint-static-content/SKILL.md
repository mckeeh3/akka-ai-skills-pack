---
name: akka-http-endpoint-static-content
description: Serve packaged HTML, CSS, JavaScript, and other static resources from Akka Java SDK HTTP endpoints. Use when adding a small UI, docs page, or static asset endpoint.
---

# Akka HTTP Endpoint Static Content

Use this skill when an HTTP endpoint serves static resources.

## Required reading

Read these first if present:
- `akka-context/sdk/http-endpoints.html.md`
- `../../../src/main/java/com/example/api/StaticContentEndpoint.java`
- `../../../src/main/resources/static-resources/http-endpoint/index.html`
- `../../../src/main/resources/static-resources/http-endpoint/app.css`
- `../../../src/main/resources/static-resources/http-endpoint/help.txt`
- `../../../src/main/resources/static-resources/http-endpoint/guide/index.html`
- `../../../src/main/resources/static-resources/http-endpoint/openapi.yaml`
- `../../../src/test/java/com/example/application/StaticContentEndpointIntegrationTest.java`

## Use this pattern when

- the service needs a lightweight documentation page
- the service bundles a tiny admin or demo UI
- you want an agent-friendly example of `HttpResponses.staticResource(...)`
- the endpoint should serve a subtree of static assets with `HttpRequest`
- the endpoint should serve a generated or packaged `openapi.yaml`
- the task includes the endpoint side of OpenAPI schema publication

## Core pattern

1. Put files under `src/main/resources/static-resources`.
2. Add a normal `@HttpEndpoint` with `@Acl`.
3. Return specific files with `HttpResponses.staticResource("...")`.
4. Return static subtrees with `HttpResponses.staticResource(request, prefix)` when needed.
5. Keep file names stable and obvious so future agents can find them cheaply.
6. Prefer small, self-contained examples.

## Repository example

- `StaticContentEndpoint`
  - serves a packaged HTML page
  - serves a packaged CSS file
  - serves a static subtree through `HttpRequest`
  - serves a packaged `openapi.yaml`
  - keeps the example small and explicit

## File layout guidance

Prefer paths like:
- `src/main/resources/static-resources/http-endpoint/index.html`
- `src/main/resources/static-resources/http-endpoint/app.css`
- `src/main/resources/static-resources/http-endpoint/guide/index.html`
- `src/main/resources/static-resources/http-endpoint/openapi.yaml`

This keeps the example topic-local and easy to route to.

## OpenAPI note

When the task includes OpenAPI publication:
- generate the file with the OpenAPI Maven plugin as described in `akka-context/sdk/http-endpoints.html.md`
- serve the generated `openapi.yaml` as a static resource

## Anti-patterns

Avoid:
- mixing unrelated assets into one large static example
- hiding files in deep resource trees without a matching endpoint example
- using static content examples to teach unrelated endpoint concerns

## Review checklist

Before finishing, verify:
- files live under `static-resources`
- endpoint methods use `HttpResponses.staticResource(...)`
- static subtree routes pass the correct request prefix
- response paths are stable and obvious
- tests fetch the assets through `httpClient`
