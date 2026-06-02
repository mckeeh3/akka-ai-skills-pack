# Model Binding Readiness Review: Workstream Expertise

## Question reviewed

Is the workstream expertise foundation fully ready for skills-pack use such that, when an app is created, every workstream is backed by a workstream-specific agent backed by a specific AI model?

## Short answer

Not fully yet. The workstream expertise layer is ready for workstream-specific agents, expert bundles, governed skills, governed reference documents, manifests, boundaries, loaders, traces, and starter core app-description coverage. The remaining gap is the **specific AI model binding** part.

The pack already has the right conceptual model:

- each managed agent has an `AgentDefinition`;
- `AgentDefinition` includes `modelConfigRefId` and `modelPolicyRefId`;
- `akka-agent-model-governance` defines `ModelConfigRef`, `ModelPolicy`, fallback policy, provider secret boundaries, model-use traces, and tests;
- `docs/agent-runtime-invocation-pattern.md` requires model config resolution before invocation.

But the executable/reference coverage still shows model governance as partial:

- `docs/agent-coverage-matrix.md` records no executable governed `ModelConfigRef` resolver example yet;
- starter backend `AgentDefinition` stores model refs, but the starter runtime does not yet prove active `ModelConfigRef` lookup, disabled/cross-tenant model denial, fallback policy, or model-use trace behavior;
- workstream expert bundles mention model policy in some places, but not every bundle has an explicit per-workstream model binding contract;
- app generation/planning guidance should more explicitly require each generated workstream agent to receive an approved `ModelConfigRef`/`ModelPolicy` or a deliberate inherited default.

## Readiness matrix

| Requirement | Status | Evidence / gap |
|---|---|---|
| Every workstream has a functional agent concept | Ready | Agent workstream architecture and starter core app-description functional-agent catalog. |
| Every foundation workstream has an expert bundle | Ready at app-description level | Sprint 07 review confirms bundles for My Account, User Admin, Agent Admin, Mission Control, Governance/Policy, and Audit/Trace. |
| Workstream expert bundles include skills/references/manifests/loaders/boundaries/traces | Ready | `docs/workstream-expertise-model.md`, `skills/akka-agent-skill-governance`, `skills/akka-agent-reference-governance`, Sprint 06 executable reference coverage. |
| Starter proves User Admin skill/reference runtime loading | Ready | Sprint 06 completed executable first-class reference-governance coverage. |
| AgentDefinition stores model refs | Present | Starter `AgentDefinition` has `modelConfigRefId` and `modelPolicyRefId`. |
| Specific model binding is governed and tested | Partial | `akka-agent-model-governance` documents it, but coverage matrix says no executable governed `ModelConfigRef` resolver example yet. |
| App generation requires model binding per workstream | Needs polish | Guidance mentions model refs/policies, but should make per-workstream model binding a readiness/generation requirement alongside expertise bundles. |

## Recommendation

Add one focused follow-up sprint before calling the feature fully ready:

- **Sprint 08: Workstream Model Binding Readiness**
  - audit current model-binding guidance;
  - make per-workstream `ModelConfigRef`/`ModelPolicy` part of the expert bundle contract;
  - add executable starter model config seed/resolution/denial/trace tests;
  - align planning/generation/readiness guidance so every generated workstream agent gets a specific approved model binding or explicit inherited default;
  - update the coverage matrix.

After Sprint 08, the answer should become: yes, the skills pack is ready to create apps where every generated workstream is backed by a workstream-specific governed agent with a specific governed model binding, expert bundle, skills, references, tool boundary, traces, and tests.
