# Domain Extension Guide

Use this guide when extending the core app for a specific product domain.

## Extension principle

Keep the secure AI-first SaaS foundation stable and add product behavior through domain-specific extension zones. A domain extension should describe its intent, workstreams, surfaces, governed capabilities, runtime components, UI, tests, and audit/security expectations without rewriting core identity, tenancy, authorization, audit, or managed-agent foundations.

## Recommended paths

```text
src/main/java/ai/first/domain/business/<domain>/
src/main/java/ai/first/application/business/<domain>/
src/main/java/ai/first/api/business/<domain>/
src/test/java/ai/first/business/<domain>/
frontend/src/extensions/<domain>/
app-description/extensions/<domain>/
specs/extensions/<domain>/
docs/extensions/<domain>/
```

Use the user's actual domain name for `<domain>` (for example, `crm`, `erp`, `billing`, or `procurement`). Java code uses the `business.<domain>` package so it is visibly user-owned; frontend and planning assets continue to use extension folders.

## Backend rules

- Model backend behavior as governed capabilities before selecting Akka components.
- Keep domain commands and queries tenant/customer scoped.
- Enforce authorization in backend routes, component commands, view queries, workflow actions, timers, consumers, and agent tools.
- Emit audit/work traces for consequential actions, protected data access, denials, provider calls, decisions, and policy changes.
- Use idempotency keys and explicit side-effect semantics for mutating actions.
- If core behavior must be extended, add a small registry or hook in core code and keep Java domain implementation under the `business.<domain>` package path.
- Keep foundation code independent of `coreapp` and `business` packages; business code may depend on stable foundation contracts and approved core app extension hooks, while core app code must not depend on business packages.

## Frontend rules

- Add domain UI under `frontend/src/extensions/<domain>/` unless a small core shell registry is needed.
- Treat frontend visibility and disabled controls as UX hints only; backend authorization remains authoritative.
- Do not expose backend secrets in frontend code or non-`VITE_` environment variables.
- Keep fixture mode explicit and opt-in; do not make fixtures the normal production path.

## App-description and specs

Before implementation, update or create domain-specific description/spec files that record:

- affected functional agents and workstreams;
- structured surfaces and actions;
- governed capabilities and governed-tools;
- auth, tenant/customer scope, audit, trace, approval, and policy behavior;
- Akka component choices and runtime paths;
- backend/frontend/test acceptance criteria.

## Done standard

A domain extension is done only when its stated runtime scope works through the local Akka/API/UI path and the relevant checks pass. Fixture-only tests or deterministic model-less responses are not enough for runtime completion.
