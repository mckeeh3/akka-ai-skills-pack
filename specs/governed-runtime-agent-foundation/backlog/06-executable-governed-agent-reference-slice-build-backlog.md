# Backlog 06: Executable governed runtime agent reference slice

## Purpose

Add executable Java reference coverage for the governed runtime agent foundation so future harness runs have concrete examples and tests, not only doctrine and planning guidance.

## Delivery goal

Implement the smallest deterministic reference slice that demonstrates:

- active `AgentDefinition` / runtime profile resolution;
- governed prompt assembly with compact manifest only;
- `readSkill(skillId)` authorization against assigned active skills and `ToolPermissionBoundary` grants;
- denied disabled-agent, cross-tenant, unassigned-skill, inactive-skill, and missing-tool-grant cases;
- `PromptAssemblyTrace`, `SkillLoadTrace`, and `AgentWorkTrace` creation;
- optional Java Agent invocation using deterministic model tests;
- coverage matrix updates from planned-reference to executable-reference where warranted.

## Reference package

Prefer these package roots unless existing project conventions require small adjustments:

```text
src/main/java/com/example/domain/agentfoundation/
src/main/java/com/example/application/agentfoundation/
src/test/java/com/example/application/agentfoundation/
```

## Capability contracts

### `agent-runtime.reference-fixtures`

- Creates immutable records and fixture factory for tenant, auth context, active/disabled agent, prompt, skill, manifest, tool boundary, and trace sink.
- Must stay narrow and deterministic.

### `agent-runtime.resolve-reference-profile`

- Resolves active governed runtime from fixture records.
- Fails closed before model invocation for disabled agent, cross-tenant prompt/manifest/boundary, missing active references, or invalid mode.
- Emits prompt assembly trace for allowed and denied attempts.

### `agent-runtime.authorize-reference-skill-load`

- Authorizes assigned active skill load.
- Denies unassigned, inactive, cross-tenant, and missing tool-boundary grant cases.
- Emits skill load trace for allowed and denied attempts.

### `agent-runtime.invoke-reference-agent`

- Optional but preferred: invoke a minimal managed reference agent with deterministic test provider and correlate work trace.
- If Java SDK integration is too large, leave a focused follow-up task rather than over-expanding a single task.

## Suggested harness task breakdown

1. Add reference governed-agent domain records and fixtures.
2. Add runtime resolver and prompt assembly tests.
3. Add skill read authorizer, tool wrapper, and tests.
4. Add minimal managed reference agent invocation and work-trace test.
5. Add optional HTTP/test-console reference surface if still valuable.
6. Update coverage matrix and run final audit.

## Done criteria

- Executable tests cover success and key denial paths.
- Reference code is minimal, explicit, and safe for future agents to copy as a pattern.
- Coverage matrix accurately reports executable coverage and remaining gaps.
