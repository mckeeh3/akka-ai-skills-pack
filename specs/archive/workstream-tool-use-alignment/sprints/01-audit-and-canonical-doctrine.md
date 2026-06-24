# Sprint 01: Audit and Canonical Doctrine

## Goal

Identify where `skills-pack/` currently defines tools, workstreams, surfaces, agents, capabilities, and direct chat/surface routing semantics, then update canonical doctrine so every later skill-family edit has one accepted source of truth.

## Scope

- Audit `skills-pack/docs/**` and `skills-pack/skills/**` for conflicting or incomplete tool-use guidance.
- Produce a source map and gap list under this mini-project.
- Update canonical docs such as:
  - `skills-pack/docs/ai-first-saas-application-architecture.md`
  - `skills-pack/docs/agent-workstream-application-architecture.md`
  - `skills-pack/docs/workstream-contract.md`
  - `skills-pack/docs/structured-surface-contracts.md`
  - `skills-pack/docs/capability-first-backend-architecture.md`
  - `skills-pack/docs/workstream-surface-intent-routing.md`
  - intent compiler docs where traceability or current-intent graph wording needs refinement

## Expected doctrine outcome

Canonical docs should state that:

- governed tools are capability-backed executable operations and the common contract behind surface actions, human chat plans, agent tools, APIs, workflows, timers, consumers, MCP tools, and internal operations;
- a workstream agent/harness has a bounded purpose and tool catalog;
- human-backed and AI-backed actor adapters can expose the same governed tool through different mediation and trace source;
- human chat requests may execute tools only through a proposed plan, explicit confirmation, backend-enforced authorization, transactional tool execution, and durable traces;
- deterministic surface routing remains a safe no-mutation adapter but is not a global prohibition against confirmed chat tool execution.

## Completion signal

Sprint 01 is complete when later tasks can use a canonical source map and updated doctrine as required reads without having to infer the intended architecture from this conversation alone.
