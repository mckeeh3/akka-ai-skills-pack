# Java validation guide for skills-pack reference code

This repository contains executable Java reference code for the skills pack. Treat it as examples and fixtures, not as the product being generated.

## Source sets

Validate source-controlled Java only:

- `src/main/java`, `src/test/java` — executable reference app and tests.
- `templates/ai-first-saas-starter/backend/src/**` — rendered starter template source.

Do not validate generated or packaged output as source:

- `target/`
- `dist/`

## Default fast gate

Use this before and after focused Java edits:

```bash
mvn -q -DskipTests compile
```

For changes that affect tests or test-only helpers, also run focused test compilation:

```bash
mvn -q -DskipTests test-compile
```

## Focused test gate

Prefer focused tests for the slice you changed. Examples:

```bash
# Domain / pure logic
mvn -q -Dtest='*ValidatorTest,*CommandHandlerTest,*BusinessLogicTest' test

# Entity unit tests
mvn -q -Dtest='*EntityTest' test

# Workflow slice
mvn -q -Dtest='*Workflow*Test' test

# Views / consumers
mvn -q -Dtest='*View*Test,*Consumer*Test' test

# HTTP endpoint slice
mvn -q -Dtest='*EndpointIntegrationTest' test

# Agent slice
mvn -q -Dtest='*AgentTest,*AgentEndpointIntegrationTest' test

# Security/admin slice
mvn -q -Dtest='*security*Test,*Security*Test,*Admin*Test,*Me*Test' test
```

Adjust patterns to the files actually changed.

## Long-running confidence gate

The root project intentionally includes many Akka runtime-backed `TestKitSupport` and `*IntegrationTest` classes in the normal Maven test phase. A full test run is useful, but it is not the right first command for every harness session.

Run this only with a generous timeout:

```bash
mvn -q test
```

Use a timeout above 120 seconds when running in an agent harness.

## Stream and realtime tests

SSE, WebSocket, topic, and polling tests can be slower or noisier because they start runtime infrastructure and wait for asynchronous delivery. Run them separately when the changed code affects streams:

```bash
mvn -q -Dtest='*Stream*Test,*WebSocket*Test,*Sse*Test,*Topic*Test' test
```

When editing stream tests, prefer deterministic Awaitility-style conditions over fixed `Thread.sleep(...)` calls.

## Starter template validation

The starter template contains placeholders and must be rendered before Maven can compile it. For template Java changes:

1. render `templates/ai-first-saas-starter/backend` into a temporary directory with concrete values for package, group id, app slug, and app name;
2. run:

```bash
mvn -q -DskipTests test-compile
mvn -q test
```

Do not treat unrendered template Java as directly buildable project source.

## Removal validation checklist

Before deleting Java reference code:

1. Search for class/file references in `skills/`, `docs/`, `specs/`, `src/test/`, `templates/`, and `.agents/` if the installed pack mirror is in scope.
2. Check for Akka annotation discovery: endpoints/components may be used by route tests without direct Java imports.
3. Remove or update matching tests, resources, protobuf files, static assets, and docs in the same change.
4. Run the focused gate for the owning slice plus `mvn -q -DskipTests compile`.
5. If deleting a public reference example, update the inventory and cleanup plan.
