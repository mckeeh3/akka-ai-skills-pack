# Sprint 2 Build Backlog: AI-First Routing Skill Family

## Purpose

Create the first executable routing layer for AI-first SaaS application design.

## Delivery goal

High-level product prompts route through AI-first SaaS interpretation before app-description maintenance, solution decomposition, or Akka component implementation.

## Suggested harness task breakdown

### 1. Create top-level `ai-first-saas` skill

- task ID: `TASK-02-001`
- output: `skills/ai-first-saas/SKILL.md`.
- scope: when to use, when not to use, required reads, core operating model, routing to app-description/decomposition/component skills, anti-chatbot rule.
- dependencies: Sprint 1 complete.

### 2. Create object-model and agent-team companion skills

- task ID: `TASK-02-002`
- output: `skills/ai-first-saas-object-model/SKILL.md`, `skills/ai-first-saas-agent-team-design/SKILL.md`.
- scope: durable goals/plans/policies/decisions/traces/outcomes; coordinator/specialist agent responsibilities and boundaries.

### 3. Create governance, decision-card, and audit companion skills

- task ID: `TASK-02-003`
- output: `skills/ai-first-saas-policy-governance/SKILL.md`, `skills/ai-first-saas-decision-cards/SKILL.md`, `skills/ai-first-saas-audit-trace/SKILL.md`.
- scope: policies/clauses/versioning, approvals/exceptions, evidence/provenance/work traces.

### 4. Create UI-surfaces and outcomes companion skills

- task ID: `TASK-02-004`
- output: `skills/ai-first-saas-ui-surfaces/SKILL.md`, `skills/ai-first-saas-outcomes-metrics/SKILL.md`.
- scope: command center, decision card, policy/governance center, digest, audit trace, outcome validation.

### 5. Wire new skills into skill routing

- task ID: `TASK-02-005`
- output: `skills/README.md` updates and any local reference updates needed for discoverability.
- scope: ensure no broken links to non-created skills.

## Done criteria

- New skills are concise and route into existing implementation families rather than duplicating them.
- `skills/README.md` clearly identifies AI-first SaaS as the default high-level product architecture path.
