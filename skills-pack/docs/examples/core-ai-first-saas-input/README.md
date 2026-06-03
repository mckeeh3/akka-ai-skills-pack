# Core AI-First SaaS Input Documents

This directory contains older module-sequenced example **input documents** for planning and generating a progressive secure AI-first SaaS core app with the Akka skills pack.

These files are source assets for the skills pack. They are intended to be used as realistic PRD/spec inputs that can be fed into the pack to produce solution plans, module specs, sprint specs, backlogs, and implementation tasks. They remain useful as full-core/detail provenance and broader release-test input, but they are not the preferred current v0/starter rollout path. For the current workstream-oriented core-app domain input, use `../ai-first-saas-core-app-domain/README.md`; for the maintained starter core app-description structure, use the target project `app-description/README.md` plus `../../docs/core-ai-first-saas-foundation.md`.

Process rule: feed these PRDs through the requirements-to-workstream chain before implementation planning: workstreams → attention categories → dashboard contracts → structured surfaces/actions → governed capabilities/APIs → Akka substrate → request-based workstream Agents and durable AutonomousAgent candidates where appropriate → events/notifications/projections → audit/work traces. For the compact pattern, read `../requirements-to-workstream-mini-example.md`.

Start with:

1. `00-document-development-process-context.md`
2. `01-core-seed-progression-plan.md`
3. `10-canonical-core-app-prd.md`

Within this older module-sequenced sample, `10-canonical-core-app-prd.md` is the hard PRD target for full core generation. Do not treat it as evidence that the five-core v0 starter/workstream contracts already implement every full-core detail. Full core scope requires the agent workstream shell plus Access/Profile, User Admin, Agent Admin, Audit/Trace, and Governance/Policy functional agents. If User Admin or Agent Admin are deferred, the selected scope must be recorded as `Module 1-only / not full core` before generation.

Then create and refine the module PRDs one document at a time.

The starter core app is delivered progressively. Because the core UI is workstream-agent-backed, a narrow agent runtime bootstrap follows basic auth before full User Admin:

1. minimal auth and app access MVP;
2. agent workstream runtime bootstrap;
3. user administration;
4. agent definition foundation;
5. prompt governance;
6. skill governance;
7. audit and work trace;
8. evaluation and closed-loop improvement.

Each module should be implemented through visible, demonstrable full-stack sprints, not as a large backend-only foundation phase.

Review generated plans against `../../agent-workstream-design-review-checklist.md` so PRD-derived work preserves functional/context-area agents, structured surfaces, governed capabilities, mandatory authorization, traces, and workstream-first UI realization rather than drifting into page-first or CRUD-first decomposition.
