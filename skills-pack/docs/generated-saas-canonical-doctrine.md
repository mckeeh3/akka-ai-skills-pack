# Generated SaaS canonical doctrine

Use this as the short shared doctrine for generated secure AI-first SaaS work. Focused skills should link here instead of repeating global rules.

## Source and install model

- The repository root is the runnable secure SaaS Foundation App.
- The skills install creates a harness support library under `.agents/skills/**` or `~/.agents/skills/**`.
- Installed skills/docs/examples/templates/tools are read-only guidance for the harness; they are not target application source, app-description storage, specs, or a duplicate baseline app.
- Target app code, `app-description/**`, `specs/**`, `frontend/**`, and `src/**` stay in the current project workspace.
- Official Akka SDK docs remain external at top-level `akka-context/**` when present.

## Default generated-app shape

Generated full-stack apps are secure AI-first SaaS workstream applications by default:

- fixed Java base package `ai.first`;
- mandatory secure SaaS foundation: WorkOS/AuthKit, `/api/me`, tenant/customer scoping, roles/capabilities, invitations, Resend email boundary, user/admin surfaces, support access, admin audit, and backend authorization;
- role-authorized functional-agent workstreams with structured surfaces, attention, traces, and governed backend capabilities;
- governed runtime agents for model-backed behavior: active agent definitions, governed prompts/skills/references, manifests, tool boundaries, model config, traces, and safe failure surfaces.

## Implementation input gate

Before generated SaaS runtime implementation, apply `../references/generated-saas-input-contract.md`. The task/app-description/spec/backlog must identify or explicitly defer the functional agent or internal trigger, capability, selected Akka substrate, AuthContext/scope, DTOs, side effects, idempotency, audit/work traces, and tests.

If those inputs are absent, repair the task brief or route back to description/capability/decomposition skills instead of guessing from component mechanics alone.

## Completion gate

A feature is complete only when it works through the real local Akka/API/UI path at the stated scope. Apply `../references/generated-saas-runtime-completion.md` for provider fail-closed behavior, governed Akka `Agent` invocation, trace requirements, and fixture/mock boundaries.

## UI gate

Use `./workstream-ui-reference-architecture.md` as the canonical generated-SaaS UI architecture. Browser routes and pages are realization details; the primary model is authenticated workstream shell, functional-agent rail, continuous stream/composer, structured surfaces, backend-backed actions, realtime/stale state, and accessible responsive rendering.
