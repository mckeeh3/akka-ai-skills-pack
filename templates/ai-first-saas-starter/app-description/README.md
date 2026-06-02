# {{APP_NAME}} App Description

This scaffolded `app-description/` tree is the project-owned source of truth for the core AI-first SaaS starter app created by the scaffold flow.

It is created in the target project workspace, not inside `.agents/`, so future harness sessions can evolve the application by updating this tree before implementation changes.

## Current core scope

The initial scaffold describes the five core v0 workstreams:

1. My Account
2. User Admin
3. Agent Admin
4. Audit/Trace
5. Governance/Policy

The core app description captures:

- secure SaaS identity, tenancy, membership, role, permission, and `/api/me` foundation;
- governed runtime agent foundation with managed prompts, skills, references, manifests, tool permission boundaries, and traces;
- functional-agent workstreams, structured surfaces, dashboards, composer behavior, and workstream expertise;
- capability-first backend contracts and governed-tools;
- behavior, tests, auth/security, observability, UI, generation, and traceability maps;
- explicit readiness distinction between the five-core starter, full-core SaaS readiness, and later domain-specific expansion.

## Growth model

Domain-specific features are added as new slices under the same tree:

```text
new domain request
→ affected functional agent/workstream or new domain functional agent
→ surfaces and actions
→ governed capabilities and governed-tools
→ behavior/rules/state
→ tests/security/observability/UI
→ traceability maps
→ implementation/regeneration plan
```

Do not create a separate app-description root per domain. Use this root as the single authoritative application description.
