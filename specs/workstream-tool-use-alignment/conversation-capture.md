# Conversation Capture: Workstream Tool Use Alignment

## Trigger

The user asked to review and refine the concept of tool use in `skills-pack/` for the secure AI-first SaaS app architecture.

The user observed that the architecture currently defines two workers, human and agent. Humans use workstreams and surfaces to do things with the app. Agents use tools. The underdefined concept is that **tools and tool use matter for both worker types**.

## Initial framing

The discussion established this shared model:

```text
Capability
→ governed tool contract
→ exposed to humans through surfaces and chat-mediated plans
→ exposed to AI through agent-tool bindings
→ authorized, validated, traced, and executed by backend/runtime boundaries
```

The important distinction is that surfaces should not be treated as merely screens. Surfaces are human-facing tool interfaces. Agent tools are AI-facing tool interfaces. Both should adapt the same governed operation when they perform the same business action.

## Human chat tool-use goal

The user clarified that human workers must be able to invoke tools directly through the workstream shell chat, for example:

```text
create org "Org 1", and invite mckee.hugh@gmail.com as an org admin
```

The user noted that humans can do these things via structured surfaces, but should also be able to do work through direct chat requests. Previous guidance avoided this due to safety concerns. The new architectural direction is that if tools are clearly the governed and secure app boundary, the access path should not matter for authorization, validation, side effects, trace, or policy enforcement.

## Refined workstream-agent model

The user proposed thinking of a given workstream as controlled by two reasoning models:

- human reasoning;
- AI/model reasoning.

The workstream is the agent/harness. It has a specific purpose expressed by prompt, skills, and a specific set of tools. Both human and AI reasoning can make mistakes, but their only access to the app is through the specific subset of tools provided by the workstream's agent.

When a human submits a chat request, the request is processed by the workstream agent's AI model. That is the main distinction from a direct surface action: the AI helps interpret and plan the human request, but authority still comes from the human, selected workstream, and governed tool boundary.

## Decisions captured

### 1. Tool access scope

Access is only to the selected workstream agent's tools. A chat request cannot invoke arbitrary app tools outside the selected workstream's purpose and tool catalog.

### 2. Human confirmation requirement

For a human chat request that would execute consequential tools, the workstream agent must respond with a description of how the request will be processed in sufficient detail and ask for permission to proceed. The human must respond with confirmation before execution.

Confirmation should bind to the specific proposed plan. If the plan changes materially, confirmation should be requested again.

### 3. Workstream help behavior

Workstream agents remain limited to the narrow bounds of the workstream. They should provide help and assistance, including answering “how do I ...” questions, explaining surfaces, and guiding the user to allowed tools. Help/explanation does not require confirmation unless it becomes tool execution.

### 4. Human authority remains bounded

The human is limited to what is allowed in the selected workstream. Chat does not expand the user's authority beyond their AuthContext, tenant/customer scope, role/capabilities, membership state, and the selected workstream agent tool catalog.

### 5. Tool transaction boundary

Each tool is a transaction boundary. Failure of one step in a multi-step plan should not leave the system in an inconsistent state. Multi-step chat plans should execute as sequences of individually authorized, validated, idempotent, and traced tool invocations.

### 6. Shared tool contract

Tool definitions should become the common contract behind surfaces, workstream chat, agent tools, APIs, workflows, MCP tools, and other exposure paths. This is important for consistency between surface tool use and workstream agent tool use.

## Architectural principle to integrate

Use this spine consistently:

```text
Worker
→ selected workstream agent
→ actor adapter / access path
→ governed workstream tool
→ capability
→ Akka/API/UI/runtime realization
→ audit/work trace and result surface
```

Workers can be human-backed or AI-backed. Access paths include structured surfaces, human chat requests with confirmed tool plans, model-facing agent-tool calls, internal workflow/timer/consumer paths, APIs, and MCP tools. The governed tool and capability contract remain the boundary.

## Concerns to encode instead of using as blockers

- The model must not become “chat can mutate anything.” Chat can only propose and execute tools in the selected workstream catalog after explicit confirmation and deterministic runtime checks.
- The AI model is not the security boundary. It may interpret, propose, and explain, but backend/runtime checks enforce authority.
- Prompt injection and model mistakes are constrained by tool catalog membership, schemas, tool permission boundaries, approval policy, and traces.
- Existing deterministic surface routing remains valuable as a safe, fast adapter for opening/prepopulating surfaces. It should coexist with confirmed chat-driven tool execution instead of forbidding it globally.
- Multi-step plans need clear result reporting, partial-failure reporting, and trace correlation.

## Planning decision

The user requested a mini-project that fully integrates this concept into `skills-pack/`, likely across many areas. The queue should support a multi-pass loop: after a series of tasks, a terminal verification task checks whether the skills-pack is aligned; if not, it appends more tasks and another terminal verification task, then repeats.
