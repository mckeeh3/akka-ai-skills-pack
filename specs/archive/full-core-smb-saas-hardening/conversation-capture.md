# Conversation Capture

## Source discussion

After completing release-readiness for five-core v0, the user asked to begin full-core hardening. The goal is to drive implementation of the core workstreams to be as functionally complete as possible for a real small/medium business SaaS app, while also using the implementation process to discover and fix skills-pack gaps.

The assistant recommended an umbrella mini-project followed by child mini-projects for:

1. full-core product baseline and UX standard;
2. My Account;
3. User Admin;
4. Agent Admin;
5. Audit/Trace;
6. Governance/Policy;
7. cross-workstream polish and release validation.

The user agreed with the concerns and recommendations and answered:

1. the starter template is the main executable baseline;
2. SMB is the target, not enterprise;
3. the workstream and surface UX/UI concept is the one and only app architecture;
4. real model/provider behavior is critical, and the implementation must push both request/response workstream agents and internal worker agents that get tedious work done for humans.

## Accepted decisions

- Create an umbrella planning mini-project before coding.
- Target `templates/ai-first-saas-starter/` as the executable baseline.
- Target SMB functional completeness, not enterprise scope.
- Preserve workstream/surface architecture as the only product architecture.
- Push AI-first behavior strongly: both request/response agents and internal worker agents.
- Use Akka `AutonomousAgent` for durable task-oriented internal/background work when lifecycle semantics justify it.
- Keep deterministic non-AI services for mechanical policy, validation, projections, lifecycle, redaction, and audit behavior.
- Use many mini-projects and sprint waves as needed.
- Learn from each wave and plan subsequent waves based on implementation discoveries.
- Capture skills-pack gaps as explicit fixes or follow-up tasks.

## Risks and concerns accepted by user

- Scope can explode; keep the target SMB-complete, not enterprise-complete.
- Do not implement five huge workstreams in parallel.
- Visual quality needs explicit acceptance criteria.
- Skills-pack gaps should be surfaced and fixed, not hidden.
- Runtime completion doctrine remains mandatory.

## Unresolved questions

No blocking questions are needed to start the umbrella planning. Later tasks may create pending questions for specific product/UX/runtime decisions discovered during full-core capability planning.
