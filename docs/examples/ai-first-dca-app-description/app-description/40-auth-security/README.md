# 40 Auth Security

Current files:
- `identity-and-trust.md` — WorkOS/AuthKit authentication, JWT-protected APIs, local Akka account authority, `/api/me`, startup admin bootstrap, and trust boundaries.
- `authorization-rules.md` — DCA seed roles, tenant/customer scopes, backend enforcement rules, denial behavior, admin APIs, and optional impersonation guardrails.
- `agent-permissions.md` — mechanical authority boundaries for agents, tools, workflows, action-boundary judging, supplies autopilot, and governed policy/prompt changes.
- `data-protection.md` — sensitive data classes, frontend/backend secret separation, response/log minimization, audit retention, and deny-by-default cases.
- `boundary-and-surface-rules.md` — public static routes versus protected `/api/...` routes, frontend UX boundaries, backend enforcement boundaries, and integration boundaries.

Purpose: define human roles, tenant/customer boundaries, WorkOS/local-account trust, agent authority enforcement, sensitive data rules, and approval permissions for the authenticated DCA seed app.

Source reference: these files adapt the working `examples/poc-user-auth-onboarding/` proof-of-concept as implementation guidance, not as a drop-in production security system.
