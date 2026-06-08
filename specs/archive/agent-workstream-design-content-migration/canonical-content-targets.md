# Canonical Content Targets for Agent Workstream Design Migration

## Purpose

This target summary gives later migration tasks compact rules to apply without rereading the full drift inventory. It is scoped to `specs/agent-workstream-design-content-migration/` and should be used with `docs/agent-workstream-design-review-checklist.md`.

## Target model

All generated full-stack AI-first SaaS design content should preserve this ordering:

```text
secure SaaS foundation
→ functional/context-area agents
→ durable workstreams
→ typed structured surfaces
→ governed backend capabilities
→ horizontal Akka implementation
```

Do not make page trees, CRUD screens, route names, frontend folders, agent tools, or Akka components the root design abstraction.

## Canonical ownership rules

### `12-workstreams/` owns application meaning

Use `12-workstreams/` for:
- functional/context-area agents and their role/capability authorization;
- internal agents that support workflows, tools, timers, consumers, evaluation, routing, summarization, governance, or extraction;
- durable workstream semantics, retention, replay, summarization, trace links, and follow-up behavior;
- `surfaces-index.md` with surface ids, type/version, owning and reusable functional agents, payload contract location, allowed actions, linked capabilities, and rendering/action tests;
- `surface-contracts/**` with typed payloads, redaction, AuthContext assumptions, state model, actions, action-to-capability mappings, audit/work traces, and tests.

### `55-ui/` owns browser realization

Use `55-ui/` for:
- workstream shell rendering, functional-agent rail, main panel, persistent composer, context/authority indicators, and denial/recovery states;
- structured surface rendering and update behavior;
- routes and deep links as implementation details that open shell, functional agent, stream item, or surface state;
- interactions, forms, frontend API contracts, realtime behavior, accessibility/responsive rules, and selected visual style guide.

`55-ui/` must link back to the authoritative functional agents, surfaces, capabilities, auth/security, observability, and tests. It must not replace `12-workstreams/` as the application model.

### Backend capabilities remain authoritative

Every workstream action, surface action, browser action, agent tool, workflow step, timer, consumer reaction, API, MCP tool/resource, or internal call maps to a governed backend capability with backend-enforced auth, tenant/customer scope, schemas, side effects, idempotency, policy/approval, audit/trace, exposure surfaces, and tests.

Frontend controls, prompt text, route guards, and tool descriptions are never authorization controls.

## File-set target rules for later tasks

- Later doctrine/bootstrap/UI alignment should define one canonical full-core `55-ui/` file set and allowed minimal/deferred subsets.
- Full core generated SaaS scope must include managed-agent UI description files for agent catalog/detail, prompt/skill governance, skill manifests/tool permissions, edit-agent proposals/traces, and style guide unless a narrower scope explicitly defers them.
- Very small generated SaaS app descriptions may be compact, but they must still include enough `12-workstreams/` and `55-ui/` artifacts to preserve the workstream model, surface contracts, backend capability mappings, and browser realization requirements.

## Routing target rules

- Natural-language UI/dashboard/admin requests should route through functional-agent modeling, surface modeling, capability modeling, and then UI realization; not directly to page trees.
- AI-first UI surface selection must name owning/reusable functional agents and workstream placement before route/API/component planning.
- Web UI implementation/generation should point generated SaaS UI to `frontend/src/workstream/**` and the User Admin reference vertical contract tests.
- `frontend/src/screens/**`, standalone static resources, and endpoint-only UI examples are mechanics or legacy references unless explicitly migrated.

## Legacy quarantine rules

- Legacy examples may remain only when clearly labeled as mechanics, compatibility, historical, or non-target generated-SaaS references.
- Consolidated historical files such as `55-ui/ui-surfaces.md` should either be migrated into the canonical `12-workstreams/` + split `55-ui/` structure or labeled as reference-specific and non-canonical.
- Purchase-request and conventional examples should be linked for description mechanics only, not as current generated AI-first SaaS target architecture.

## Review entry point

Use `docs/agent-workstream-design-review-checklist.md` for future edits. A file passes only when it keeps application meaning in functional/context-area agents, workstreams, structured surfaces, governed capabilities, and mandatory security, while treating browser routes and frontend implementation as downstream realization details.
