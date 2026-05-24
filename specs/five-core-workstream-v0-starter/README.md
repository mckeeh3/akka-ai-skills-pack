# Five Core Workstream v0 Starter

## Goal

Realign the initial AI-first SaaS starter from a single User Admin v0 slice to a functioning authenticated workstream shell that shows the five core workstreams in the left rail from the first run:

1. My Account
2. User Admin
3. Agent Admin
4. Audit/Trace
5. Governance/Policy

Each core workstream initially works as a text-first v0 workstream with one structured surface type, `markdown_response`, a bounded functional-agent prompt/skill/model configuration, backend authorization, durable/request-response workstream entries, and audit/work trace semantics. Richer surfaces and capabilities are then implemented one workstream at a time.

## Non-goals

- Do not claim full-core SaaS readiness from the five v0 text-first workstreams.
- Do not remove richer full-core surface examples unless a task explicitly supersedes them.
- Do not add app-specific domain workstreams in this migration.
- Do not weaken WorkOS/AuthKit, AuthContext, capability, tenant isolation, audit, or frontend secret-boundary rules.

## Growth model

The migration should make the starter demonstrate the repeatable extension loop:

```text
new PRD
→ new functional agent/workstream v0
→ markdown_response shell
→ prompt/skills/model refs
→ governed capabilities
→ richer structured surfaces
→ Akka components/tests
```

## Task execution

Use `pending-tasks.md`. Each task is intended for a fresh harness session and must make exactly one focused git commit when complete.
