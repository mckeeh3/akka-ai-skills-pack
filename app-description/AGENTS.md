# App-Description Realization Guidance

This directory is part of the default app-realization mode. Edit here when compiling user intent into the authoritative current-intent graph for the runnable SaaS app.

## Scope

- App/global/domain/workstream intent, capabilities, behavior, tests, security, observability, UI surfaces, and readiness artifacts.
- Domain-specific app-description extensions should live under `app-description/extensions/<domain>/` when possible.

## Rules

- Treat app-description artifacts as app-facing source of truth for intended behavior, not skills-pack documentation.
- Keep security, authorization, traces, tests, and realization impact linked to changed behavior.
- If an input is ambiguous, normalize the smallest safe intent delta and ask or record blocking questions rather than silently inventing critical policy.
- Do not edit `skills-pack/**` from this mode unless the user explicitly requests skills-pack maintenance.

## Checks

Use the smallest checks that prove consistency for the touched artifacts, commonly:

```bash
git diff --check
```
