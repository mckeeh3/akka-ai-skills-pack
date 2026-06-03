# AI-first SaaS Core App Description

This `app-description/` tree is the root app-owned source of truth for the AI-first SaaS core app. It is maintained alongside the runnable backend and frontend, not inside `.agents/`.

## Current core scope

The core app description covers the five baseline workstreams:

1. My Account
2. User Admin
3. Agent Admin
4. Audit/Trace
5. Governance/Policy

It captures:

- secure SaaS identity, tenancy, membership, role, permission, and `/api/me` foundation;
- governed runtime-agent foundation with managed prompts, skills, references, manifests, tool permission boundaries, and traces;
- functional-agent workstreams, structured surfaces, dashboards, composer behavior, and workstream expertise;
- capability-first backend contracts and governed tools;
- behavior, tests, auth/security, observability, UI, generation, and traceability maps;
- readiness distinctions between the baseline core app, full-core SaaS readiness, and later domain-specific expansion.

## Growth model

Add domain-specific features as extension slices under this same tree:

```text
new domain request
→ affected core workstream or new domain workstream
→ functional agent responsibilities
→ structured surfaces and actions
→ governed capabilities and governed tools
→ behavior/rules/state
→ tests/security/observability/UI
→ traceability maps
→ implementation plan
```

Recommended path:

```text
app-description/extensions/<domain>/
```

Do not create a separate app-description root per domain. Keep this tree as the single authoritative application description for the core app and product extensions.
