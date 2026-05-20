# Sprint 07: Starter Fullstack Gap Closure

## Objective

Move `templates/ai-first-saas-starter/` from a scaffoldable foundation/reference toward a fully functioning fullstack Akka + React/Vite/TypeScript starter app.

This sprint closes the review gaps found after the frontend was embedded into the starter template:

1. stale acceptance docs still describe the frontend as not scaffolded;
2. no single scaffolded fullstack smoke command proves backend + frontend + static hosting together;
3. fixture-era UI language remains visible in canonical frontend paths;
4. local WorkOS/AuthKit and first-admin bootstrap are not turnkey enough;
5. invitation onboarding lacks a complete browser/API acceptance path;
6. Resend remains a production seam/stub instead of an adapter boundary with testable behavior;
7. core foundation persistence is still in-memory and should be incrementally replaced by durable Akka components;
8. governed agent/runtime records need durable component-backed lifecycle paths;
9. admin/governance/audit capability APIs need stronger concrete contracts and HTTP/integration tests;
10. final acceptance must be rerun against the updated fullstack starter.

## Delivery rules

- Each task is intended for a fresh harness session.
- Each task must update `specs/ai-first-saas-starter-app-template/pending-tasks.md` before completion.
- Each task must run its required checks or document why a check could not run.
- Each task must make one git commit before marking itself `done`.
- Preserve scaffold safety: default installs remain skills-only; starter code is materialized only through explicit scaffold/init.

## Task sequence

1. Refresh acceptance docs and current gap baseline.
2. Add scaffolded fullstack smoke validation.
3. Make canonical frontend copy production-first while retaining explicit fixture mode.
4. Make local AuthKit/first-admin bootstrap path turnkey and documented.
5. Implement browser/API invitation acceptance flow.
6. Replace Resend stub with a production adapter boundary and captured local/test outbox checks.
7. Introduce durable Akka identity/invitation/audit slices behind existing ports.
8. Introduce durable Akka governed-agent behavior slices behind existing ports.
9. Expand concrete admin/governance/audit APIs and integration tests.
10. Rerun final acceptance and publish updated completion summary.
