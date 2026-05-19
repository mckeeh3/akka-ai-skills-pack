# Change Impact Map

- WorkOS/AuthKit configuration or identity claims change:
  - update `40-auth-security/`, frontend API contract, endpoint tests, and readiness assumptions
- tenant model change:
  - update capabilities, tenant state model, authorization rules, UI tenant switcher, tenant isolation tests
- agent authority change:
  - update operating model, agent rules, policy gates, decision tests, audit trace requirements
- new or changed functional agent:
  - update `12-workstreams/functional-agents.md`, workstream retention rules if needed, rail rendering, `/api/me` capability exposure, authorization tests, and traceability maps
- new structured surface:
  - update `12-workstreams/surfaces-index.md`, surface contract, UI index, structured surface renderer, frontend API contracts, behavior flow if new actions exist, acceptance/rendering tests, and surface-to-capability map
- new Akka horizontal demonstration:
  - update capability-to-horizontal map, component mapping rules, generation scope, output surfaces, and skills-pack reference map when implemented
