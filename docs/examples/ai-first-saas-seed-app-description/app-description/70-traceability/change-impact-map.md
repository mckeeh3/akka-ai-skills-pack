# Change Impact Map

- auth provider or identity claims change:
  - update `40-auth-security/`, frontend API contract, endpoint tests, and readiness assumptions
- tenant model change:
  - update capabilities, tenant state model, authorization rules, UI tenant switcher, tenant isolation tests
- agent authority change:
  - update operating model, agent rules, policy gates, decision tests, audit trace requirements
- new UI surface:
  - update UI index, frontend API contracts, behavior flow if new actions exist, acceptance tests
- new Akka component demonstration:
  - update component mapping rules, generation scope, output surfaces, and skills-pack reference map when implemented
