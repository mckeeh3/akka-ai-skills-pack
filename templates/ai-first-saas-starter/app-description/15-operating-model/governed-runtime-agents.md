# Governed Runtime Agents

The core app uses configuration-driven, governed runtime agents for the five user-facing workstreams.

## Required managed records

Each core functional agent must resolve active records before claiming model-backed readiness:

- `AgentDefinition`
- `PromptDocument` / `PromptVersion`
- `SkillDocument` / `SkillVersion`
- `ReferenceDocument` / `ReferenceVersion`
- `AgentSkillManifest`
- `AgentReferenceManifest`
- `ToolPermissionBoundary`
- `PromptAssemblyTrace`
- `SkillLoadTrace`
- `ReferenceLoadTrace`
- `AgentWorkTrace`

## Runtime path requirement

Normal workstream message submission must:

1. resolve the selected AuthContext and authorized functional agent;
2. load the active `AgentDefinition` and active prompt/manifest/tool-boundary records;
3. assemble the prompt deterministically with compact skill/reference manifest entries;
4. register authorized loader tools such as `readSkill(skillId)` and `readReferenceDoc(referenceId)` plus any capability tools allowed by the `ToolPermissionBoundary`;
5. invoke the concrete Akka request-based `Agent` component through `effects().tools(runtimeTools)`;
6. emit prompt assembly, skill/reference load, tool allow/deny, model invocation, response, denial, and work traces;
7. persist durable workstream log entries and surface payload references.

Direct service/provider calls that bypass the Akka `Agent` component are not a completed user-facing workstream runtime.

## Core functional agents

- `my-account-agent`
- `user-admin-agent`
- `agent-admin-agent`
- `audit-trace-agent`
- `governance-policy-agent`

Internal/background workers may use Akka `AutonomousAgent` only when the task has governed capabilities, typed lifecycle state, fail-closed provider/runtime behavior, events, attention, structured surfaces, and tests.

## Fail-closed provider behavior

If provider credentials, model configuration, or security setup is missing, the normal runtime must return a safe blocked/system-message surface with trace evidence. It must not return deterministic, canned, mock, model-less, or fixture-backed success.
