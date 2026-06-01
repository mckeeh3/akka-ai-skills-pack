# Scaffold Rules: AI-First SaaS Starter

## Required package prompt

Before writing Java source files, ask:

> What Java base package should I use for generated code? Press Enter to use `ai.first`.

Use `ai.first` only when the user accepts or defers the choice.

## Rendering rules

1. Replace placeholders in file paths before copying.
2. Replace placeholders in file contents before writing.
3. Derive `{{JAVA_PACKAGE_PATH}}` by replacing `.` with `/` in `{{JAVA_BASE_PACKAGE}}`.
4. Render backend files into the target root, not into a nested `backend/` directory, unless the scaffold command explicitly supports a monorepo layout.
5. Refuse to overwrite existing application files unless the user selects an explicit force mode.

## Backend package layout after rendering

```text
src/main/java/<base package>/domain
src/main/java/<base package>/application
src/main/java/<base package>/api
src/test/java/<base package>
```

The starter must not render `com.example` unless explicitly selected by the user.

## Scaffold readiness target

Rendered starter guidance must identify the initial runnable target as the five core v0 workstream set: My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy. Each starts text-first with `markdown_response` v1, a bounded functional agent, selected `AuthContext`, backend capability boundary, and audit/work trace substrate.

Rendered guidance must also preserve the workstream graph vocabulary: role-specific dashboard attention, human surface graph nodes/edges, internal workstream agent graph follow-up where applicable, workstream expertise, governed-tool ids, and qualified browser-tool, agent-tool, and internal-tool exposures. Do not describe protected surface actions as generic tools; tie them to governed capabilities, authorization, audit/work trace, and runtime validation.

Scaffolded app-description and specs placeholders must keep full-core follow-up work explicit. Five-core-v0 readiness is not full-core readiness; richer structured surfaces, support access, full governed agent document lifecycle, searchable audit/trace views, deeper policy/governance workflows, security hardening, and app-specific domain workstreams remain follow-up until implemented and tested.

## Attention backbone, producers, and event backbone

The starter includes v1 shared backend-owned attention, v2 bounded producers, and a bounded v3 governed workstream event backbone. Guidance for generated apps must distinguish:

- **implemented v1 starter scope:** actionable `AttentionItem` lifecycle state, scoped workstream dashboard reads, My Account aggregate attention, left-rail summaries from backend projections, redaction, and audit/work traces;
- **implemented v2 starter scope:** producer ids/idempotency for invitation delivery, governance approval state, timed invitation checks, worker/task blocked or review-needed states, and backend-derived refresh after producer-affecting actions;
- **implemented v3 starter scope:** typed browser-safe `WorkstreamEventEnvelope`/`WorkstreamEventSourceRef`, Akka-backed event repository seam, bounded invitation and access-review lifecycle event publication, an idempotent allow-listed event-to-attention consumer, and backend-derived `projection.refresh.available` update hints that require clients to reload backend-owned projections;
- **future work unless explicitly implemented:** broad AutonomousAgent durable task lifecycle integration over v3 events, event coverage for arbitrary generated-app domains, provider readiness restored events without a real readiness source, broad notification streams, digest infrastructure, enterprise notification preferences, and arbitrary AutonomousAgent task notifications.

Do not describe frontend-only badges, fixture/demo data, or event-stream hints as authoritative attention behavior. Normal runtime attention and event-backed projection claims must be backed by backend attention/event state and local validation through the rendered starter paths.
