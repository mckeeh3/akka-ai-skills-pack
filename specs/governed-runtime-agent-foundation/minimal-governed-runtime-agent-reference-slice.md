# Minimal governed runtime agent reference slice

## Purpose

Add the smallest executable reference that proves the managed runtime agent foundation can be implemented without turning the example layer into a full SaaS product.

This task intentionally records a follow-on implementation plan instead of adding Java code immediately. The full executable slice touches behavior-profile state, prompt versions, skill manifests, tool-boundary enforcement, trace emission, and deterministic agent tests; implementing all of that safely is larger than one fresh harness task.

## Slice goal

Create a compact reference path for `agent-runtime.invoke-managed-agent`:

```text
ManagedAgentEndpoint or test fixture
→ AuthContext fixture
→ AgentRuntimeResolver
→ active AgentDefinition
→ active PromptDocument / PromptVersion
→ active AgentSkillManifest compact prompt context
→ ToolPermissionBoundary snapshot
→ Java Agent invocation
→ readSkill(skillId) tool authorization
→ PromptAssemblyTrace + SkillLoadTrace + AgentWorkTrace facts
```

The slice should be executable, deterministic, and local-test friendly. It should not depend on WorkOS, real LLM providers, real SaaS billing, or a full frontend.

## Capability contract

### `agent-runtime.invoke-managed-agent.reference`

- callers: deterministic test fixture, optional protected HTTP endpoint.
- actors: active tenant admin or runtime actor represented by a minimal `AuthContext` fixture.
- scope: one tenant, one active `AgentDefinition`, one active prompt, one active manifest, one assigned skill, one denied skill.
- authorization: runtime call requires tenant match, active agent, active prompt version, active manifest, active tool boundary, and granted `readSkill` tool.
- side effects: trace facts only; no external side effects.
- traces: `PromptAssemblyTrace`, `SkillLoadTrace`, and `AgentWorkTrace` as in-memory/domain records or minimal Akka-owned records.
- tests: success, disabled-agent denial, cross-tenant denial, unassigned-skill denial, missing-tool-grant denial, and trace assertions.

## Proposed package shape

Use names that make the example easy to find without implying these are production-ready generated types.

### Domain/reference records

Add small immutable records under `src/main/java/com/example/domain/agentfoundation/`:

- `ReferenceAuthContext`
- `ReferenceAgentDefinition`
- `ReferencePromptDocument`
- `ReferencePromptVersion`
- `ReferenceSkillDocument`
- `ReferenceSkillVersion`
- `ReferenceAgentSkillManifest`
- `ReferenceToolPermissionBoundary`
- `ReferencePromptAssemblyTrace`
- `ReferenceSkillLoadTrace`
- `ReferenceAgentWorkTrace`
- `ReferenceResolvedAgentRuntime`

Keep these records narrow: ids, tenant id, lifecycle/status, version/checksum, short content, grants, mode, authorization decision, and correlation id.

### Application helpers

Add deterministic helpers under `src/main/java/com/example/application/agentfoundation/`:

- `ReferenceAgentRuntimeResolver`
  - validates tenant, active agent, active prompt, active manifest, active tool boundary, runtime mode, and correlation id;
  - assembles the prompt with compact manifest entries only;
  - returns `ReferenceResolvedAgentRuntime` or a safe denial result;
  - records `ReferencePromptAssemblyTrace` for allowed and denied attempts.
- `ReferencePromptAssembler`
  - composes platform instruction, active prompt content, compact manifest, tool rules, and request context;
  - computes deterministic checksum.
- `ReferenceSkillReadAuthorizer`
  - authorizes `readSkill(skillId)` against tenant, agent, manifest entry, active skill version, runtime mode, and `ToolPermissionBoundary`;
  - records `ReferenceSkillLoadTrace` for allowed and denied attempts.
- `ReferenceTraceSink`
  - in-memory test sink for prompt assembly, skill load, and work traces.

These helpers may be ordinary Java classes. They should not become hidden authorization stores; all authorization inputs must come from explicit fixture/state records.

### Agent/tool example

Add the smallest Java Agent and tool wiring necessary to demonstrate invocation:

- `ManagedReferenceActivityAgent`
  - similar in responsibility to `ActivityAgent`, but receives an assembled governed system prompt from the resolver rather than hard-coding the complete behavior.
  - uses deterministic `TestModelProvider` in tests.
- `ReferenceAgentSkillTools`
  - exposes `readSkill(skillId)` as the governed skill-loading tool shape;
  - delegates all checks to `ReferenceSkillReadAuthorizer`;
  - returns a safe denial string instead of leaking cross-tenant or missing-skill details.

If Java SDK tool injection makes the minimal example too coupled for the first implementation pass, keep `ReferenceAgentSkillTools` as a direct unit-tested tool class and add a follow-up task for SDK `@FunctionTool` integration.

## Test plan

Add tests under `src/test/java/com/example/application/agentfoundation/`:

1. `ReferenceAgentRuntimeResolverTest`
   - active profile resolves and assembled prompt contains compact `AgentSkillManifest` only;
   - disabled agent is denied before model invocation;
   - cross-tenant prompt or manifest is denied;
   - allowed and denied assembly attempts record `PromptAssemblyTrace`.
2. `ReferenceSkillReadAuthorizerTest`
   - assigned active skill loads successfully;
   - unassigned skill is denied with safe message;
   - inactive or cross-tenant skill is denied;
   - missing `readSkill` tool grant is denied;
   - each result records `SkillLoadTrace`.
3. `ManagedReferenceActivityAgentTest`
   - deterministic `TestModelProvider` call succeeds with the assembled prompt path;
   - work trace correlates prompt assembly, skill load when used, and final response summary;
   - no full skill text is present in the initial prompt fixture.

Run at least the focused Maven/JUnit tests for the new package. If the first implementation pass remains helper-only, run those helper tests and explain why the SDK agent/tool integration is deferred.

## Suggested follow-on pending tasks

These tasks are intentionally smaller than this planning task and should be materialized into `pending-tasks.md` only if the project decides to continue executable hardening after Sprint 05 final audit.

### REF-AGENT-001: Add reference governed-agent domain records and fixtures

- outputs: immutable reference records plus fixture factory for one active tenant, agent, prompt, manifest, skill, boundary, and trace sink.
- checks: unit tests for fixture consistency and tenant mismatch helpers.

### REF-AGENT-002: Add `ReferenceAgentRuntimeResolver` and prompt assembly tests

- outputs: resolver, prompt assembler, prompt assembly trace sink integration.
- checks: active success, disabled denial, cross-tenant denial, compact manifest-only prompt, `PromptAssemblyTrace` creation.

### REF-AGENT-003: Add `ReferenceSkillReadAuthorizer` and `readSkill` tests

- outputs: skill read authorizer and minimal tool wrapper.
- checks: assigned active skill success, unassigned denial, inactive denial, cross-tenant denial, missing `ToolPermissionBoundary` grant denial, `SkillLoadTrace` creation.

### REF-AGENT-004: Add minimal managed reference agent invocation test

- outputs: `ManagedReferenceActivityAgent`, deterministic `TestModelProvider` test, `AgentWorkTrace` fixture assertions.
- checks: assembled prompt invocation succeeds, no full skill text in initial prompt, response/work trace correlation.

### REF-AGENT-005: Add optional HTTP/test-console reference surface

- outputs: narrow protected endpoint or test-console-like application boundary that calls the resolver.
- checks: safe denial responses, correlation id propagation, no prompt/skill/provider secrets exposed.

## Non-goals

- Full SaaS identity, WorkOS, invitation, billing, or browser UI implementation.
- Complete Event Sourced Entity implementations for every governed record.
- A production-grade policy engine.
- Real provider secrets or external tool calls.
- Replacing the existing `ActivityAgent`, `TemplateBackedActivityAgent`, or PromptTemplate examples.

## Done criteria for the executable follow-up

- The slice demonstrates active `AgentDefinition` resolution, governed prompt assembly, compact manifest rendering, authorized `readSkill(skillId)`, tool-boundary denial, and trace creation.
- Tests prove success and the key denial paths without real model/provider calls.
- `docs/agent-coverage-matrix.md` is updated from planned reference to executable reference for the rows covered by the code.
