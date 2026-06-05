# Java validation guide for skills-pack reference code

This directory contains executable Java reference code for the skills pack. Treat it as examples and fixtures, not as downstream product source.

## Source sets

Validate source-controlled Java where it lives now:

- root `src/main/java`, `src/test/java` — canonical runnable core app source and tests
- `skills-pack/examples/akka-components/src/main`, `skills-pack/examples/akka-components/src/test` — focused Java reference examples exported by the pack

Do not validate generated build output as source:

- `target/`

## Default fast gate

Use this before and after focused root Java edits:

```bash
mvn -q -DskipTests compile
```

For skills-pack Java examples, run from `skills-pack/examples/akka-components` when that example project is independently buildable, or validate them through the root build when they are included there.

## Focused test gate

Prefer focused tests for the slice you changed. Examples from the root app:

```bash
mvn -q -Dtest='*ValidatorTest,*CommandHandlerTest,*BusinessLogicTest' test
mvn -q -Dtest='*EntityTest' test
mvn -q -Dtest='*Workflow*Test' test
mvn -q -Dtest='*View*Test,*Consumer*Test' test
mvn -q -Dtest='*EndpointIntegrationTest' test
mvn -q -Dtest='*AgentTest,*AgentEndpointIntegrationTest' test
mvn -q -Dtest='*security*Test,*Security*Test,*Admin*Test,*Me*Test' test
```

Adjust patterns to the files actually changed.

## Long-running confidence gate

A full root app test run is useful for runtime-backed changes:

```bash
mvn -q test
```

Use a timeout above 120 seconds when running in an agent harness.

## Stream and realtime tests

SSE, WebSocket, topic, and polling tests can be slower or noisier because they start runtime infrastructure and wait for asynchronous delivery. Run them separately when changed code affects streams:

```bash
mvn -q -Dtest='*Stream*Test,*WebSocket*Test,*Sse*Test,*Topic*Test' test
```

When editing stream tests, prefer deterministic Awaitility-style conditions over fixed `Thread.sleep(...)` calls.

## Core app validation

The core app is source-controlled at the repository root and uses the fixed default package `ai.first`. It is directly buildable; do not treat it as an unrendered template.

Typical checks:

```bash
mvn test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
```

## Removal validation checklist

Before deleting Java reference code:

1. Search for class/file references in `skills-pack/skills/`, `skills-pack/docs/`, active `specs/`, root tests, and installed `.agents/` mirrors when in scope.
2. Check Akka annotation discovery: endpoints/components may be used by route tests without direct Java imports.
3. Remove or update matching tests, resources, protobuf files, static assets, and docs in the same change.
4. Run the focused gate for the owning slice plus `git diff --check`.
5. If deleting a public reference example, update the relevant inventory and pack docs.
