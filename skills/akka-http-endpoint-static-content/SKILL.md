---
name: akka-http-endpoint-static-content
description: Serve packaged HTML, CSS, OpenAPI files, and other static resources from Akka Java SDK HTTP endpoints. Use when the task is file serving, not a larger interactive browser UI.
---

# Akka HTTP Endpoint Static Content

Use this skill when an HTTP endpoint serves packaged static resources.

If the task is an interactive browser page with JSON, SSE, or WebSocket behavior, start with:
- `../akka-http-endpoint-web-ui/SKILL.md`

## Required reading

Read these first if present:
- `akka-context/sdk/http-endpoints.html.md`
- `../akka-http-endpoint-web-ui/SKILL.md`
- `../../../src/main/java/com/example/api/StaticContentEndpoint.java`
- `../../../src/main/resources/static-resources/http-endpoint/index.html`
- `../../../src/main/resources/static-resources/http-endpoint/app.css`
- `../../../src/main/resources/static-resources/http-endpoint/help.txt`
- `../../../src/main/resources/static-resources/http-endpoint/guide/index.html`
- `../../../src/main/resources/static-resources/http-endpoint/openapi.yaml`
- `../../../src/test/java/com/example/application/StaticContentEndpointIntegrationTest.java`

## Use this pattern when

- the service needs a lightweight documentation page
- the service bundles a small non-interactive HTML page
- you need a narrow example of `HttpResponses.staticResource(...)`
- the endpoint should serve a static subtree with `HttpRequest`
- the endpoint should serve a directory index from packaged resources
- the task includes publication of a packaged `openapi.yaml`

## Do not use this skill when

Move to the broader web-ui skill when the main concern is:

- browser `fetch(...)` calls to JSON endpoints
- browser-side SSE consumption through `EventSource`
- browser-side WebSocket behavior
- TypeScript-authored interactive UI logic

## Core pattern

1. Put files under `src/main/resources/static-resources`.
2. Add a normal `@HttpEndpoint` with `@Acl`.
3. Return specific files with `HttpResponses.staticResource("...")`.
4. Return a static subtree with `HttpResponses.staticResource(request, prefix)` when the route should cover many files.
5. Keep paths stable and obvious so future agents can find assets cheaply.
6. Keep pure file serving separate from interactive browser examples.

## Repository example

- `StaticContentEndpoint`
  - serves a packaged HTML page
  - serves a packaged CSS asset
  - serves a static subtree containing `help.txt`
  - demonstrates directory-index resolution with `guide/index.html`
  - serves a packaged `openapi.yaml`

## File layout guidance

Prefer paths like:
- `src/main/resources/static-resources/http-endpoint/index.html`
- `src/main/resources/static-resources/http-endpoint/app.css`
- `src/main/resources/static-resources/http-endpoint/help.txt`
- `src/main/resources/static-resources/http-endpoint/guide/index.html`
- `src/main/resources/static-resources/http-endpoint/openapi.yaml`

This keeps the example topic-local and easy to route to.

## TypeScript note

Pure static/file-serving examples do not need TypeScript.

Only move to TypeScript when the browser page has interactive logic that is better shown in the broader web-ui family.

## OpenAPI note

When the task includes OpenAPI publication:
- generate the file with the OpenAPI Maven plugin as described in `akka-context/sdk/http-endpoints.html.md`
- serve the generated or packaged `openapi.yaml` as a static resource

## Anti-patterns

Avoid:
- turning the static-content example into a catch-all browser UI example
- mixing unrelated assets into one large resource tree
- hiding files in deep directories without a matching endpoint example
- introducing TypeScript when the task is only file serving

## Review checklist

Before finishing, verify:
- files live under `static-resources`
- endpoint methods use `HttpResponses.staticResource(...)`
- static subtree routes pass the correct request prefix
- the example covers a direct file, a subtree file, a directory index, and a packaged OpenAPI file
- tests fetch the assets through `httpClient`
