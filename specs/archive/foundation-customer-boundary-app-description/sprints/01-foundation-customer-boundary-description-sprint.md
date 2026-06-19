# Sprint 01: Foundation Customer Boundary Description

## Goal

Make the foundation customer boundary unambiguous in the active `app-description/` current-intent graph.

## Scope

- Inventory existing app-description and implementation evidence for foundation customer boundary concepts.
- Update app-description nodes for domain boundary, capabilities, data/state, workstream bindings, surfaces, agents, tools, policies, traces, tests, and realization mapping.
- Verify sufficiency and append follow-up tasks if any ambiguity remains.

## Done criteria

- Current-intent graph clearly says what the foundation customer boundary owns and does not own.
- User Admin/Tenant Customer Admin workstream bindings include customer lifecycle and Customer Admin branch behavior.
- Surface, agent, tool, policy, trace, and test expectations are capability-backed and security-preserving.
- Realization mapping names the existing backend Akka/application/API/frontend component families at the right level of detail.
- Verification answers: “Is the description sufficiently unambiguous for future realization?” and either records yes with evidence or appends more bounded tasks plus a replacement terminal verification task.

## Required validation

- `git diff --check`
- Targeted search proof that active `app-description/` contains foundation customer boundary coverage for domain, capabilities, data-state, workstream, surfaces, agents/tools, traces/tests, and realization.

## Non-runtime reason

This sprint is app-description/docs-only. Runtime behavior is used as evidence but not changed.
