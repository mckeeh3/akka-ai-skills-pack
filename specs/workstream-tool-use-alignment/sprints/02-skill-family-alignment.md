# Sprint 02: Skill Family Alignment

## Goal

Propagate canonical workstream tool-use doctrine through focused skills so future harness sessions model, plan, implement, test, and review generated SaaS features consistently.

## Skill families in scope

### App-description and intent skills

Skills that capture current intent must model workstream tool catalogs, governed tool ids, actor adapters, confirmation behavior, approval/denial behavior, traces, and tests without duplicating semantics between surfaces and agent tools.

Likely touched skills include:

- `app-descriptions`
- `app-description-functional-agent-modeling`
- `app-description-surface-modeling`
- `app-description-capability-modeling`
- `app-description-behavior-specification`
- `app-description-auth-security`
- `app-description-observability`
- `app-description-test-specification`
- `app-description-ui`
- `app-description-readiness-assessment`
- `app-generate-app`

### Agent, tool, trace, and governance skills

Agent implementation guidance must distinguish governed workstream tools from Akka `@FunctionTool` exposure and explain the confirmed human chat plan path.

Likely touched skills include:

- `akka-agents`
- `akka-agent-tools`
- `akka-agent-component-tools`
- `akka-agent-mcp-tools`
- `akka-agent-tool-boundaries`
- `akka-agent-work-trace`
- `akka-agent-behavior-profiles`
- `akka-agent-prompt-governance`
- `akka-agent-skill-governance`
- `akka-agent-reference-governance`
- `akka-agent-orchestration`
- `akka-agent-testing`

### Workstream, SaaS, UI, and planning skills

Workstream and UI skills must treat surfaces as human tool adapters and chat as a natural-language tool-plan adapter, while retaining surface-routing, confirmation, and accessibility expectations.

Likely touched skills include:

- `ai-first-saas`
- `agent-workstream-apps`
- `ai-first-saas-worker-decomposition`
- `ai-first-saas-ui-surfaces`
- `ai-first-saas-audit-trace`
- `core-saas-foundation`
- `capability-first-backend`
- `akka-web-ui-apps`
- `akka-web-ui-api-client`
- `akka-web-ui-forms-validation`
- planning/queue skills that generate specs, backlogs, task briefs, and pending tasks

## Completion signal

Sprint 02 is complete when affected skills consistently tell future harnesses to preserve the shared governed tool contract and use adapter-specific guidance rather than treating surfaces, agent tools, and chat commands as separate business semantics.
