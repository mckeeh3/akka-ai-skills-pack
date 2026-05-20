# Conversation Capture: AI-First SaaS Starter App Template

## User intent

The user asked whether web UI implementation guidance is sufficient for generating fully functional AI-first SaaS apps. After review, we agreed that the UI layer is strong, but the next confidence upgrade is an end-to-end full-core reference implementation wired to real backend capabilities.

The user then proposed that the skills pack should include the initial fully functional AI-first SaaS app so downstream users can focus on extension rather than starting from a blank project. We agreed with the refinement that installation should support both:

- skills-only install for global installs and existing projects; and
- explicit starter-app scaffold/init mode for empty/new projects.

## Agreed direction

Create a canonical installable starter app template that proves the whole pack:

- secure SaaS foundation;
- agent workstream UI;
- capability-first backend;
- governed runtime agents;
- Akka component implementation;
- React/Vite/TypeScript frontend;
- tests and packaging;
- app-specific extension workflow.

## Planning instruction

Create a migration plan similar to the other `specs/` migrations. Define self-contained tasks that can each be completed in a fresh harness session. Each completed task must update queue status and end with a git commit.
