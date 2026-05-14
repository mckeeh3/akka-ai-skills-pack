# Seed frontend quality and packaging handoff

This localized React/Vite frontend validates the AI-first SaaS seed app UI design. Source of record lives under `frontend/src/**`.

## Checks

Run from `frontend/`:

```bash
npm run typecheck
npm test
npm run build
```

## Akka static hosting output

`npm run build` writes the Vite production build to:

```text
src/main/resources/static-resources/
```

The Akka endpoint serves `index.html`, `/favicon.ico`, and `/assets/**` from that directory. The generated `index.html` references the active hashed CSS/JS assets. Other static reference examples under `src/main/resources/static-resources/**` are intentionally preserved by the build command; do not hand-edit generated Vite assets.

## Current route smoke scope

Frontend contract tests cover shell wiring for:

- Mission Control: `/ui/briefing`
- Goal Workbench: `/ui/goals/new`
- Decision Queue and detail flow: `/ui/decisions`
- Governance Center: `/ui/governance/policies`
- Audit Trace Explorer: `/ui/audit/traces`
- Admin Users and Invitations: `/ui/admin/users`
- Profile Preferences: `/ui/profile`

## Explicit defers

The fixture frontend intentionally defers real authenticated backend integration, admin authorization enforcement, policy commit execution, trace export, and durable Akka state. Screen components use typed client seams so later slices can replace fixture clients with real `/api/...` implementations.
