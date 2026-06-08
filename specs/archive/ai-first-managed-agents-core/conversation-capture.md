# Conversation Capture: AI-first Managed Agents Core

## User decision

The user confirmed the rule of thumb:

> If an agent answers a user in the workstream shell, it must already be configuration-driven. If an admin edits how agents behave, that belongs to Agent Admin.

The user requested a pending task queue to make the necessary core-app changes, with self-sufficient tasks suitable for fresh harness sessions and one git commit per completed task.

## Architectural decision

Configuration-driven agents are part of the core app runtime substrate, not deferred to the Agent Admin PRD.

Agent Admin is the management/governance surface over this substrate. The core app must already provide:

- seeded managed `AgentDefinition` records for core functional agents;
- governed prompt, skill, reference, model, and tool-boundary records;
- runtime profile resolution;
- deterministic prompt assembly with compact manifests;
- runtime tool-list resolution;
- Akka `effects().tools(runtimeTools)` invocation;
- `readSkill(skillId)` and `readReferenceDoc(referenceId)` as real Akka function tools;
- traces and fail-closed denials.

## Current repository finding summary

Already present:

- doctrine and skill guidance for governed runtime agents;
- starter domain records for agent definitions, prompts, skills, references, manifests, model config, tool boundaries, and traces;
- starter seed import and repository seam;
- starter `WorkstreamRuntimeAgent` using a real Akka Agent model path;
- `AgentRuntimeService` methods for prompt assembly, `readSkill`, `readReferenceDoc`, model policy, and traces;
- tests for seed import, prompt assembly, skill/reference load denial, model policy, disabled-agent denial, and Akka Agent path.

Key remaining gaps:

- `WorkstreamRuntimeAgent` does not yet call `.tools(runtimeTools)`;
- `readSkill` and `readReferenceDoc` are service methods but not registered `@FunctionTool` loader classes in the starter runtime path;
- no starter `ToolRegistry` / `AgentRuntimeToolResolver` maps stable tool grants to Java tool instances/classes;
- no tests prove the model can call governed tools through the real Akka Agent path;
- durable governed-agent storage is a starter KVE seam rather than first-class entity/view slices;
- docs need to elevate AI-first managed agents as a primary core app pillar alongside workstreams and surfaces.
