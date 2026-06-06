# Maintainer skill routing summary

This is the current compact routing surface for pack maintainers. Use it before opening the long historical `skill-routing-reference.md`; update both only when routing semantics change.

## First decision

1. **Repository/runtime app work**: stay in the root app (`src/**`, `frontend/**`, `app-description/**`, `specs/**`) and use root `AGENTS.md` plus focused implementation skills.
2. **Installable skills-pack work**: stay under `skills-pack/**` and use `skills-pack/AGENTS.md`.
3. **Planning from requirements**: route through `core-saas-foundation`, `ai-first-saas`, `agent-workstream-apps`, app-description skills, then Akka decomposition/backlog skills.
4. **Queued implementation**: use `akka-do-next-pending-task` and execute one runnable task per fresh session.
5. **Open decisions**: use `akka-do-next-pending-question` before task generation or blocked implementation.

## Core suites

- SaaS/foundation: `core-saas-foundation`, `akka-workos-user-auth`, `akka-basic-user-admin`, `akka-saas-invitation-onboarding`, `akka-resend-email-service`.
- AI-first/workstream modeling: `ai-first-saas`, `agent-workstream-apps`, `ai-first-saas-*` design skills.
- App description: `app-descriptions` plus capability, behavior, auth/security, tests, observability, UI, surface, functional-agent, readiness, and change-impact skills.
- Akka substrate: `akka-key-value-entities`, `akka-event-sourced-entities`, `akka-workflows`, `akka-views`, `akka-consumers`, `akka-timed-actions`, `akka-http-endpoints`, `akka-grpc-endpoints`, `akka-mcp-endpoints`.
- Agents: `akka-agents`, governed behavior-document skills, `akka-agent-component`, tools, memory, streaming, guardrails, evaluation, autonomous-agent skills, and testing skills.
- Frontend: `akka-web-ui-apps`, frontend-project, API-client, state-rendering, forms-validation, realtime, accessibility, UX, and testing skills.

## Hard routing rules

- Do not treat `.agents/skills` as app source, docs storage, examples storage, or a duplicate app baseline.
- Do not satisfy runtime features through deterministic/demo/model-less substitutes outside tests or explicitly named fixtures.
- Generated full-stack SaaS tasks must preserve functional-agent/workstream/surface/action/capability/AuthContext/substrate/API/frontend/realtime/audit/test contracts unless explicitly internal-only or docs-only.
- App-specific code should use `domain-specific` or the user's actual domain name, not historical placeholder verticals.

## When to open the long reference

Open `skill-routing-reference.md` only for detailed maintainer audits, release reviews, or resolving routing conflicts that are not answered by `skills/README.md`, this summary, or the selected skill's front matter.
