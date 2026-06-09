# Root App Tooling Guidance

This directory is part of the default app-realization mode. Edit here for tooling that supports the runnable SaaS app, app-description/spec validation, development workflows, and root app checks.

## Scope

- Root app scripts, validators, developer utilities, and app-facing automation under `tools/**`.
- Skills-pack installer, release, packaging, and reusable installed-tool payloads belong under `skills-pack/**`, not here.

## Rules

- Keep tool behavior aligned with the root app runtime and validation paths.
- Prefer fail-closed validation with actionable errors for security/provider/configuration checks.
- Do not move skills-pack maintenance tools into root `tools/**` unless the user explicitly asks for a root compatibility wrapper.
- Do not edit `skills-pack/**` from this mode unless the user explicitly requests skills-pack maintenance.

## Checks

Use the smallest checks that prove the touched tooling, commonly:

```bash
git diff --check
```
