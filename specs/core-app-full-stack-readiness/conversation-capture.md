# Conversation Capture: Core App Full-Stack Readiness

## User prompt

The user asked whether the current skills pack is clear about what is needed for a core app. The stated target includes:

- fully functioning workstream-agent-backed UIs;
- full user onboarding via invites;
- full user administration at SaaS Owner and SaaS Tenant levels;
- full agent administration;
- integration of prompts and skills with Akka agents;
- a hybrid Akka agent implementation that is understood at high level but may not be fully worked out at code level.

The follow-up request was to create an implementation plan, similar to `specs/workstream-ui-implementation-migration/`, as a series of one-session tasks where each task commits its changes.

## Gap assessment summary

The current pack is clear at the doctrine/routing/app-description level, especially through:

- `docs/examples/core-ai-first-saas-input/10-canonical-core-app-prd.md`
- `docs/examples/core-ai-first-saas-input/01-core-seed-progression-plan.md`
- `templates/ai-first-saas-starter/app-description/`
- `docs/core-ai-first-saas-foundation.md`
- `docs/core-saas-identity-tenancy-admin.md`
- `docs/agent-runtime-invocation-pattern.md`
- `skills/core-saas-foundation/SKILL.md`

The remaining readiness gaps are mostly realization gaps:

1. no single complete runnable full-core reference app;
2. invitation onboarding and full user administration are strongly specified but not fully executable/reference-proven end to end;
3. governed agent runtime has good reference slices, but Agent Admin and runtime governance are not fully durable/API/UI/componentized;
4. workstream UI reference exists, but realistic backend-backed core verticals need strengthening;
5. hybrid Akka agent runtime needs a canonical production-shaped implementation contract;
6. end-to-end generation/readiness path needs a golden path from PRD/app-description to module specs, backlog, tasks, code, and tests.

## Planning decision

Create `specs/core-app-full-stack-readiness/` with:

- README and conversation capture;
- sprint specs;
- build backlogs;
- one-session task briefs;
- `pending-tasks.md` queue;
- an initial planning task marked done and all implementation tasks pending.

Each future task should be executed in a fresh harness session and committed independently.
