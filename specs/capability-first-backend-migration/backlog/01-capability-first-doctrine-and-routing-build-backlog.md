# Sprint 1 Build Backlog: Capability-First Doctrine and Routing

## Purpose

Make capability-first backend architecture explicit and authoritative enough for later skill, example, and cleanup work.

## Delivery goal

Future agents should understand that backend functionality is first modeled as governed capabilities. Agent tools, UI actions, APIs, workflows, MCP tools, consumers, and timers are exposure/execution surfaces selected after capability semantics are clear.

## Suggested harness task breakdown

### 1. Canonical capability-first doctrine

- task ID: `TASK-01-001`
- output: `docs/capability-first-backend-architecture.md`
- focus: define capability, exposure surfaces, design rules, authorization/audit expectations, and relationship to Akka components and agent tools.

### 2. AI-first doctrine integration

- task ID: `TASK-01-002`
- output: `docs/ai-first-saas-application-architecture.md`
- focus: add capability-first backend substrate language without weakening secure SaaS foundation or AI-first operating model.

### 3. Routing map update

- task ID: `TASK-01-003`
- output: `skills/README.md`
- focus: route broad implementation/product input through capability modeling before component or agent-tool selection.

### 4. Top-level capability-first skill

- task ID: `TASK-01-004`
- output: `skills/capability-first-backend/SKILL.md`
- focus: concise internal routing skill for capability modeling and downstream skill selection.

### 5. Initial consistency review

- task ID: `TASK-01-005`
- output: small review notes in pending task notes or a short review file if needed
- focus: verify no broken links, no contradiction with secure foundation, and no premature component-skill rewrites.

## Implementation order

Run tasks in listed order. The top-level skill should reference the canonical doctrine created by Task 1.

## Done criteria

- All Sprint 1 task outputs exist.
- Capability-first doctrine distinguishes capability from agent tool.
- No application implementation code is changed.
- Each completed task has a focused git commit.
