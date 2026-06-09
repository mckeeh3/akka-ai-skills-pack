# Root App Documentation Guidance

This directory is part of the default app-realization mode. Edit here for documentation about the runnable secure AI-first SMB SaaS core app.

## Scope

- App-facing architecture, setup, operation, extension, security, workstream, and domain documentation.
- Domain-specific docs should live under `docs/extensions/<domain>/` when possible.

## Rules

- Keep root app docs distinct from reusable skills-pack docs.
- Do not document deterministic/demo/mock/model-less paths as completed production behavior for agents, auth, durability, provider calls, protected capabilities, authorization denials, audit traces, or work traces.
- Link docs to real app paths, commands, and validation evidence when possible.
- Do not edit `skills-pack/**` from this mode unless the user explicitly requests skills-pack maintenance.

## Checks

Use the smallest checks that prove consistency for the touched docs, commonly:

```bash
git diff --check
```
