# Post-Completion Review: Workstream Expertise Foundation

## Review outcome

The completed workstream expertise content establishes the right doctrine, routing, app-description ownership, skill/reference governance vocabulary, User Admin expert-bundle example, starter seed fixtures, and planning guidance. The model is coherent and should remain the canonical direction.

Two material gaps remain worth planning as follow-up work:

1. **Executable first-class reference governance is still incomplete in the starter backend.**
   The docs and seed fixtures describe `ReferenceDocument`, `AgentReferenceManifest`, `readReferenceDoc(referenceId)`, and `ReferenceLoadTrace`, but the starter backend runtime still stores only `SkillDocument` and `AgentSkillManifest` records and implements only `readSkill(skillId)`. The frontend contract test checks fixture text for reference behavior, but backend runtime tests do not yet prove assigned reference loads, unassigned reference denials, missing `read_reference` boundary denial, redaction/oversize denial, or reference load traces.
2. **Only User Admin has a concrete workstream expert bundle.**
   The starter core app-description lists multiple foundation functional agents (`my-account-agent`, `agent-admin-agent`, `mission-control-agent`, `governance-policy-agent`, `audit-trace-agent`), but `12-workstreams/workstream-expertise/` currently has only `user-admin-agent.md`. For LLM-backed foundation agents, each should either have a bundle or an explicit readiness-impacting deferral. Agent Admin and Audit/Trace are especially important because they govern and investigate the expertise system itself.

## Evidence

Reviewed files included:

- `docs/workstream-expertise-model.md`
- `docs/agent-runtime-invocation-pattern.md`
- `skills/akka-agent-reference-governance/SKILL.md`
- `docs/agent-coverage-matrix.md`
- `templates/ai-first-saas-starter/app-description/app-description/12-workstreams/functional-agents.md`
- `templates/ai-first-saas-starter/app-description/app-description/12-workstreams/workstream-expertise/user-admin-agent.md`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/AgentDefinition.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/AgentBehaviorRepositoryState.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/SkillDocument.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/AgentSkillManifest.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorSeedLoader.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeServiceTest.java`
- `templates/ai-first-saas-starter/frontend/src/workstream-user-admin-expertise.contract.test.mjs`

## Gap matrix

| Area | Current state | Gap | Recommended follow-up |
|---|---|---|---|
| Reference governance skill | Strong conceptual guidance for first-class references. | No executable first-class backend example yet. | Add starter backend `ReferenceDocument`, `AgentReferenceManifest`, repository state, seed import, runtime loader, and tests. |
| Runtime prompt assembly | Starter prompt assembly renders `# Compact skill manifest`. | It does not render a separate compact reference manifest even though seed YAML declares one. | Extend `AgentDefinition`/runtime resolver path to include reference manifest id/version and render separate skill/reference sections. |
| `readReferenceDoc` runtime | Tool boundary includes `READ_REFERENCE` grant and fixtures mention `readReferenceDoc`. | No `readReferenceDoc` method or backend authorization path exists. | Implement request/result types, authorizer logic, safe denials, and `REFERENCE_LOAD`/`ReferenceLoadTrace` traces. |
| Seed loader | Seed resources include reference markdown and expertise YAML. | Loader validates/imports skill seeds only; references are not governed records. | Import `ReferenceDocument` records and `AgentReferenceManifest` with provenance, checksums, idempotency, and customization-preserving behavior. |
| Backend tests | Tests cover prompt assembly, `readSkill`, disabled agent denial, behavior proposal authority checks. | Missing assigned/unassigned reference load tests and missing `read_reference` boundary denial tests. | Add focused backend tests mirroring `readSkill` coverage for references. |
| Coverage matrix | Records the executable reference gap. | The gap remains after the completed plan. | Close the matrix item after starter backend runtime tests exist. |
| Foundation workstream coverage | User Admin has a detailed expert bundle. | Other foundation functional agents have no bundle or explicit deferral. | Add bundle stubs/details for Agent Admin and Audit/Trace first; then Governance/Policy, Mission Control, and My Account as appropriate. |

## New follow-up plan

Add two follow-up sprints:

- **Sprint 06: Executable reference-governance coverage** — close the backend starter/runtime gap for first-class references.
- **Sprint 07: Foundation workstream expertise expansion** — ensure all foundation functional agents have expert bundles or explicit deferrals, starting with Agent Admin and Audit/Trace.

These are not blockers for the doctrine already completed, but they are important to make the vision fully real and test-backed.
