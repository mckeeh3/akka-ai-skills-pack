# Conversation Capture

## Source discussion

The user asked whether the skills pack is strong at selecting Akka `AutonomousAgent` as part of design and implementation. The answer identified strong existing routing and coverage, with remaining opportunities for focused executable examples.

The discussion converged on a larger reference-runtime direction: fully implement the five core workstreams as a v0 secure AI-first SaaS app, using those workstreams to demonstrate three distinct internal agent categories:

1. request/response AI agents for user-facing workstream turns;
2. durable Akka `AutonomousAgent` components for background/internal investigations, reviews, batches, or improvement loops;
3. non-AI deterministic backend agents/services for policy evaluation, authorization, projection, validation, outbox, trace normalization, and lifecycle enforcement.

The user then chose a planning structure:

- first mini-project: plan all five workstreams together at v0 scope;
- then one mini-project for each of the five workstreams:
  - My Account;
  - User Admin;
  - Agent Admin;
  - Audit/Trace;
  - Governance/Policy.

## Accepted decisions

- Treat this as source-repository work for the skills-pack starter/template/reference assets, not as an unrelated end-user application.
- Plan all five together before implementing individual verticals.
- Implement individual workstreams one mini-project at a time, each with its own queue and verification loop.
- Keep user-facing functional workstream turns request-based by default.
- Use Akka `AutonomousAgent` only for durable task-oriented internal/background work when lifecycle, progress, notifications, cancellation/failure, dependencies, handoff, or investigation semantics justify it.
- Use deterministic non-AI services/agents for mechanical authorization, policy checks, projections, trace normalization, lifecycle processors, and other non-model behavior.
- Preserve runtime completion doctrine: real local Akka runtime/API/UI validation is required for runtime claims; mocks/fixtures/test doubles are not normal user-facing runtime substitutes.

## Non-goals

- Do not implement any workstream runtime code in the planning session.
- Do not collapse all five workstreams into one mega-implementation task.
- Do not replace request-based workstream agents with `AutonomousAgent` for normal composer turns.
- Do not add app-specific/domain-specific workstreams before the five core v0 workstreams are planned and validated.
- Do not weaken existing WorkOS/AuthKit, AuthContext, tenant/customer, authorization, audit/trace, model-provider, governed-agent, or frontend secret-boundary requirements.

## Risks

- Existing starter queues already completed five-core v0 and production-ready v0 baseline work. These new mini-projects must build on that baseline and should not duplicate or regress it.
- Workstream-specific queues can become too broad if they try to reach full-core SaaS readiness in one task group.
- AutonomousAgent examples must be justified by durable task lifecycle semantics, not added just to demonstrate technology.
- Deterministic internal services must not be mislabeled as AI agents or allowed to bypass authorization/audit.

## Unresolved questions

No blocking product questions were identified for planning. Workstream task sessions may add pending questions if implementation discovers missing authority, lifecycle, approval, retention, UI style, or provider/runtime decisions.
