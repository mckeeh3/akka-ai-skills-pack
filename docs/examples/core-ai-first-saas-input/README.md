# Core AI-First SaaS Input Documents

This directory contains canonical example **input documents** for planning and generating a progressive secure AI-first SaaS core app with the Akka skills pack.

These files are source assets for the skills pack. They are intended to be used as realistic PRD/spec inputs that can be fed into the pack to produce solution plans, module specs, sprint specs, backlogs, and implementation tasks. They are not a replacement for the maintained app-description seed structure; use `../ai-first-saas-seed-app-description/README.md` as the preferred current generated-SaaS app-description reference.

Start with:

1. `00-document-development-process-context.md`
2. `01-core-seed-progression-plan.md`
3. `10-canonical-core-app-prd.md`

Use `10-canonical-core-app-prd.md` as the hard PRD target for full core generation. Full core scope requires the agent workstream shell plus Access/Profile, User Admin, Agent Admin, Audit/Trace, and Governance/Policy functional agents. If User Admin or Agent Admin are deferred, the selected scope must be recorded as `Module 1-only / not full core` before generation.

Then create and refine the module PRDs one document at a time.

The seed app is delivered progressively. Because the core UI is workstream-agent-backed, a narrow agent runtime bootstrap follows basic auth before full User Admin:

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
