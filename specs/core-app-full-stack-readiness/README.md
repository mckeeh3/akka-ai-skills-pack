# Core App Full-Stack Readiness Plan

This planning package turns the current skills-pack gap assessment into an executable migration plan for making the pack ready to generate and/or host a **full core AI-first SaaS app**.

The migration is planning-only until individual pending tasks are executed in fresh harness sessions. Each task must be self-contained, update `pending-tasks.md`, and end with a git commit.

## Minimum-first relationship

Full-core readiness remains stricter than the minimum starter. For minimum/starter/basic/chatbot-like generated SaaS requests, the first implementation slice is **User Admin workstream v0** as defined by `../../docs/minimum-ai-first-saas-app.md`: bootstrap authorization, selected `AuthContext`, bounded `UserAdminAgent`, durable workstream log, `markdown_response`, backend capability boundary, and audit/work trace substrate.

That first slice must carry explicit follow-up gates to reach full-core readiness: fuller User Admin structured capabilities, Agent Admin and governed behavior documents, Audit/Trace search/UI, invitation onboarding with email/outbox, support access, security completeness, and only then app-specific domain workstreams. A plan or implementation that stops at the minimum starter must be labeled narrower than full core.

## Why this migration exists

The repository now clearly defines the full core app target:

```text
WorkOS/AuthKit secure SaaS foundation
→ invite-based onboarding and full user administration
→ role-authorized functional-agent workstream shell
→ Agent Admin for governed AgentDefinition, prompts, skills, manifests, tool boundaries, model refs, and tests
→ hybrid Akka agent runtime with governed prompt/skill/tool execution
→ Audit/Trace and Governance/Policy surfaces
→ full-stack acceptance and security tests
```

The gap is not primarily doctrine. The gap is that the pack needs a reliable realization path and executable/reference assets proving the target at code, UI, API, and test level.

## Target outcome

The migration is complete when the skills pack has:

- a canonical full-core app realization plan derived from `docs/examples/core-ai-first-saas-input/10-canonical-core-app-prd.md`;
- clear generated artifacts for module specs, sprint specs, backlog items, and implementation task briefs;
- reference/backend guidance and examples for complete invitation onboarding and full user administration;
- reference/backend guidance and examples for full Agent Admin and the hybrid governed Akka agent runtime;
- workstream UI reference surfaces backed by realistic core API contracts for User Admin, Agent Admin, Audit/Trace, and Governance/Policy;
- full-stack acceptance/security test expectations and, where practical, executable reference tests;
- final consistency review proving that future harness sessions can follow the queue from PRD/app-description to runnable full-core increments.

## Execution model

- Execute one task per fresh harness context.
- Use `pending-tasks.md` as the durable queue.
- Select the first `pending` task whose dependencies are satisfied.
- Read this README, `conversation-capture.md`, the selected sprint, backlog, queue entry, and task brief before editing.
- Do not perform broad opportunistic rewrites outside the selected task scope.
- Each task must end with one git commit containing only that task's intended changes and queue-status update.
- Record the commit message in task notes; record commit hash only when practical without amending the same commit.
- Preserve task IDs. Supersede obsolete tasks instead of renumbering or deleting them.

## Read order for future sessions

1. `AGENTS.md`
2. `skills/README.md`
3. `docs/ai-first-saas-application-architecture.md`
4. `docs/capability-first-backend-architecture.md`
5. `docs/core-ai-first-saas-foundation.md`
6. `docs/core-saas-identity-tenancy-admin.md`
7. `docs/agent-workstream-application-architecture.md`
8. `docs/agent-runtime-invocation-pattern.md`
9. `docs/examples/core-ai-first-saas-input/10-canonical-core-app-prd.md`
10. this file
11. selected sprint spec under `sprints/`
12. selected backlog under `backlog/`
13. selected task entry in `pending-tasks.md`
14. selected task brief under `tasks/`

## Sprint sequence

1. `sprints/01-scope-and-generation-sprint.md` — convert the full-core PRD into a concrete realization map, readiness rubric, and module/sprint/task generation path.
2. `sprints/02-auth-user-admin-sprint.md` — close executable/reference gaps for WorkOS/AuthKit, invite onboarding, full user/membership/role administration, support access, and admin views.
3. `sprints/03-agent-admin-runtime-sprint.md` — close executable/reference gaps for Agent Admin, governed documents, seed loading, model refs, tool boundaries, and hybrid Akka agent runtime.
4. `sprints/04-workstream-ui-sprint.md` — back the workstream UI reference with realistic User Admin, Agent Admin, Audit/Trace, Governance/Policy API contracts and fixture-driven verticals.
5. `sprints/05-audit-governance-sprint.md` — complete durable audit/work-trace, governance proposal, evaluation, approval, rollback, and trace explorer expectations.
6. `sprints/06-e2e-generation-review-sprint.md` — add full-stack acceptance/security test matrix, run final consistency review, and publish completion summary.

## Done state

The migration is done when the pack can take the canonical core PRD and guide a fresh harness through a sequence of committed, demonstrable full-stack increments that produce the full core app or an explicitly labeled narrower module scope. Full core may not omit User Admin, Agent Admin, workstream-backed UI, invitation onboarding, governed prompt/skill/runtime agent behavior, or tenant-isolation/security tests.
