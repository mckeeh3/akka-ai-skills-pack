# Generated SaaS runtime completion doctrine

A generated-app or SaaS Foundation App feature is complete only when the real local runtime path works at the stated scope.

For model-backed workstream behavior, normal message submission must invoke a concrete Akka `Agent` component through the governed runtime path:

- active `AgentDefinition` and model/provider configuration;
- governed prompt, skill, reference, and manifest resolution;
- authorized `readSkill(skillId)` and `readReferenceDoc(referenceId)` loader tools where assigned;
- `ToolPermissionBoundary` enforcement;
- resolved runtime tools registered with `effects().tools(runtimeTools)`;
- provider invocation from the Akka Agent path;
- durable prompt/skill/reference/model/tool/data/policy trace facts and user-visible safe failure surfaces.

Missing provider or security configuration should fail closed with actionable errors and traces.

Do not count deterministic/demo/mock/simulated/model-less normal runtime behavior as implemented for workstream agents, auth, durability, protected capabilities, authorization denials, provider calls, audit/work traces, or work traces. Fixtures, mocks, deterministic fakes, and test doubles are allowed only in tests, local-only harness checks, or explicitly named test adapters; they must not be wired as the default user-facing runtime path.
