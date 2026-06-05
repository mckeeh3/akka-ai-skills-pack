# Workstream-First Core AI-First SaaS PRDs

This directory contains implementation-ready PRDs for a full-stack core AI-first SaaS application built on Akka Java SDK and React/Vite/TypeScript.

Use these PRDs as source inputs for app-description updates, Akka solution planning, specs/backlogs/sprints, pending-task queues, and implementation task briefs.

## PRDs

1. `01-main-foundation-prd.md` — secure SaaS foundation, WorkOS/AuthKit, `/api/me`, AuthContext, Resend/outbox, authorization, audit foundation, shell, shared DTOs, static frontend hosting.
2. `02-user-admin-workstream-prd.md` — User Admin functional agent workstream, dashboard, users list, user account/edit, invitations, roles/capabilities, last-admin protection, audit.
3. `03-agent-admin-workstream-prd.md` — Agent Admin functional agent workstream, AgentDefinition, prompt/skill governance, manifests, tool boundaries, deterministic assembly, readSkill, traces.
4. `04-audit-workstream-prd.md` — Audit functional agent workstream, dashboard, audit search, audit detail, prompt/skill/tool/work traces, redaction, sensitive-read audit, export approval.

The PRDs intentionally use the sequence:

```text
secure SaaS foundation
→ role-authorized functional agent workstreams
→ structured surfaces and surface actions
→ governed backend capabilities
→ Akka components, APIs, frontend, tests
```

They avoid page-first and CRUD-first decomposition. Conventional routes may still be introduced during realization as deep links or HTTP/API implementation details.
