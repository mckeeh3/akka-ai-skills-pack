# Backlog 05: Agent implementation hardening

## Purpose

Solidify and clarify implementation of agents in generated AI-first SaaS apps now that the governed runtime agent foundation is mandatory.

## Delivery goal

Add focused, reusable guidance and reference coverage so a future harness can implement managed runtime agents with low ambiguity: resolve active `AgentDefinition`, assemble governed prompts, load assigned governed skills, enforce tool boundaries, trace execution, use behavior-editing agents for document changes, test all denials, and choose between one skilled agent and multiple specialized agents deliberately.

## Capability contracts

### `agent-runtime.invoke-managed-agent`

- actors/callers: browser API, workflow step, timer, consumer, internal service, test console.
- auth/scope: active `AuthContext`, tenant/customer scope, active membership, caller capability, active `AgentDefinition`.
- semantics: resolve profile, prompt, manifest, tool boundary, and runtime mode before invoking Java `Agent` code.
- traces: `PromptAssemblyTrace`, `SkillLoadTrace`, `AgentWorkTrace`.
- tests: disabled/archived denial, cross-tenant denial, unapproved prompt/skill denial, unassigned skill denial, trace creation.

### `agent-behavior.edit-through-agent`

- actors/callers: steward, policy owner, admin, `AgentBehaviorEditorAgent`.
- semantics: natural-language behavior change request becomes proposed diff/draft version/manifest or tool-boundary proposal, not direct active mutation.
- approval: authority expansion, tool/data expansion, cross-scope changes, prompt/policy conflicts require review or decision card.
- tests: proposal success, forbidden proposal, authority-expansion escalation, activation requires approval.

### `agent-tool-boundary.enforce`

- actors/callers: runtime agent tools, MCP tools, component tools, workflow tools, test console.
- semantics: prompt/skill text never grants tool authority; tool registry and boundary decide allowed tools/data/side effects.
- tests: denied tool, allowed read-only tool, side-effect approval required, trace for allowed/denied tool use.

### `agent-responsibility.shape`

- actors/callers: solution decomposition, app-description maintenance, PRD planning.
- semantics: decide one governed skilled agent vs specialized agents vs workflow-supervised team from responsibility, authority, model, lifecycle, stewardship, and risk boundaries.
- tests/checks: planning guidance preserves responsibility boundaries and does not create unnecessary agent sprawl.

## Suggested harness task breakdown

1. Add runtime invocation resolver guidance.
2. Add dedicated behavior-editing agent skill and routing.
3. Add tool permission boundary implementation guidance.
4. Add governed-agent testing guidance.
5. Add model configuration governance guidance.
6. Add one-agent vs agent-team responsibility-shaping guidance.
7. Add or plan a minimal governed runtime agent reference slice and update coverage matrix.
8. Final verification and audit.

## Done criteria

- Each task is completed in its own fresh harness session and committed.
- Agent guidance clearly distinguishes doctrine, planning, implementation, UI, tests, and examples.
- The coverage matrix accurately reports remaining gaps.
- Verification passes.
