# Backend App Realization Guidance

This directory is part of the default app-realization mode. Edit here when implementing the runnable Akka Java SDK backend for the secure AI-first SMB SaaS core app.

## Scope

- Runtime source under `src/main/java/ai/first/**`.
- Tests under `src/test/java/ai/first/**`.
- Resources under `src/main/resources/**`.
- Platform/security/identity/managed-agent runtime code belongs under `foundation`.
- Built-in five-core-workstream code belongs under `coreapp`.
- User-owned product/domain extensions belong under `business.<domain>` paths.

## Rules

- Preserve tenant/customer scoping, backend authorization, audit/work traces, provider fail-closed behavior, and durable runtime paths.
- Do not implement normal runtime behavior with deterministic/demo/mock/model-less shortcuts. Use mocks only in tests or explicitly named fixture modes.
- Use the relevant Akka skills as implementation guidance for entities, workflows, views, consumers, agents, endpoints, timers, tests, and web UI hosting.
- Do not edit `skills-pack/**` from this mode unless the user explicitly requests skills-pack maintenance.

## Checks

Choose the smallest proof for the change. Common backend checks:

```bash
mvn test
git diff --check
```
