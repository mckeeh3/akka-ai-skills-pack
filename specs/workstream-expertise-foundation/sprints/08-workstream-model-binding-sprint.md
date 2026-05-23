# Sprint 08: Workstream Model Binding Readiness

## Objective

Close the final readiness gap for the claim: every generated workstream is backed by a workstream-specific governed agent backed by a specific approved AI model binding.

The existing workstream expertise model covers agents, prompts, skills, references, manifests, tools, traces, and tests. This sprint makes model binding equally explicit and test-backed through `ModelConfigRef`, `ModelPolicy`, provider secret boundaries, model-use traces, and generation/readiness requirements.

## Scope

Likely source files:

- `docs/workstream-expertise-model.md`
- `docs/agent-runtime-invocation-pattern.md`
- `docs/agent-coverage-matrix.md`
- `skills/akka-agent-model-governance/SKILL.md`
- `skills/akka-agent-behavior-profiles/SKILL.md`
- `skills/app-description-functional-agent-modeling/SKILL.md`
- `skills/app-description-readiness-assessment/SKILL.md`
- `skills/akka-prd-to-specs-backlog/SKILL.md`
- `skills/app-generate-app/SKILL.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/workstream-expertise/**`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/**`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/**`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/**`

## Deliverables

- Per-workstream expert bundle contract includes model binding: `ModelConfigRef`, `ModelPolicy`, allowed modes, fallback policy, provider secret boundary, and model-use trace requirements.
- Seed app-description bundles either name a model binding or explicitly inherit a governed foundation default model binding.
- Starter backend includes executable governed `ModelConfigRef` / `ModelPolicy` seed state and runtime validation before agent invocation.
- Tests cover active model success, disabled/unknown model denial, policy-denied model/provider, no provider secret leakage, fallback/no-fallback behavior where in scope, and model refs in traces.
- Planning/readiness/generation guidance requires each generated workstream agent to have a specific approved model binding or explicit inherited default.
- Coverage matrix no longer lists governed model config resolver coverage as missing after executable tests exist.

## Checks

- `git diff --check`
- Targeted starter backend tests for agentfoundation model governance.
- Full starter validation when starter template runtime changes are broad enough to warrant it.
