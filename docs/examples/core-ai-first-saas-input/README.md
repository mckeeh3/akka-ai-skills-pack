# Core AI-First SaaS Input Documents

This directory contains canonical example input documents for planning and generating a progressive secure AI-first SaaS core app with the Akka skills pack.

These files are source assets for the skills pack. They are intended to be used as realistic PRD/spec inputs that can be fed into the pack to produce solution plans, module specs, sprint specs, backlogs, and implementation tasks.

Start with:

1. `00-document-development-process-context.md`
2. `01-core-seed-progression-plan.md`
3. `10-canonical-core-app-prd.md`

Use `10-canonical-core-app-prd.md` as the hard PRD target for full core generation. Full core scope requires the agent workstream shell plus Access/Profile, User Admin, Agent Admin, Audit/Trace, and Governance/Policy functional agents. If User Admin or Agent Admin are deferred, the selected scope must be recorded as `Module 1-only / not full core` before generation.

Then create and refine the module PRDs one document at a time.

The seed app is delivered progressively:

1. minimal auth and app access MVP;
2. user administration;
3. agent definition foundation;
4. prompt governance;
5. skill governance;
6. audit and work trace;
7. evaluation and closed-loop improvement.

Each module should be implemented through visible, demonstrable full-stack sprints, not as a large backend-only foundation phase.
