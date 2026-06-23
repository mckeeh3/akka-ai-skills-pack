# Workstream Tool Use Alignment

## Purpose

Create a durable skills-pack maintenance mini-project to make **tool use** a first-class architectural primitive across `skills-pack/`.

The goal is to align the skills, docs, templates, examples, and validation guidance around this model:

```text
Workstream agent
→ bounded purpose/prompt, skills/references, policies, and governed tool catalog
→ human-backed actor adapter: structured surfaces and confirmed chat tool plans
→ AI-backed actor adapter: model-mediated tool planning and agent-tool calls
→ shared capability-backed governed tools
→ backend-enforced authorization, transaction boundary, audit/work trace, and result surfaces
```

A workstream is controlled by two possible reasoning engines:

- an authenticated human worker reasoning through surfaces or direct chat requests;
- an AI model reasoning through the configured workstream agent runtime.

Both can affect the app only through the specific subset of governed tools provided by the selected workstream agent. Surfaces are human-facing tool interfaces. Agent tools are AI-facing tool interfaces. Human chat requests are natural-language requests to the selected workstream agent that may produce a detailed tool plan and then require explicit human confirmation before execution.

## Source discussion / trigger

The user identified that the current skills-pack architecture emphasizes human workers using workstreams/surfaces and agents using tools, but does not consistently define **tools** as the shared app boundary for both worker types. The user clarified that humans should be able to do work through direct workstream shell chat requests such as:

```text
create org "Org 1", and invite mckee.hugh@gmail.com as an org admin
```

The desired behavior is not ungoverned chat mutation. The selected workstream agent should parse the request within its narrow workstream purpose, respond with a sufficiently detailed execution plan, ask for permission to proceed, and execute only after the human confirms. The human remains limited to the selected workstream agent's tool catalog and the user's own authorization.

## Scope

This mini-project targets skills-pack source assets only:

- `skills-pack/docs/**`
- `skills-pack/skills/**`
- `skills-pack/templates/**`
- `skills-pack/examples/**`
- `skills-pack/tools/**` when validation support needs updates
- pack maintainer checks and references needed to keep installed skill guidance coherent

Root app runtime code, frontend code, and app-description content are out of scope unless a verification task explicitly recommends a separate root-app mini-project.

## Done state

This mini-project is complete when the skills-pack consistently teaches and validates the following across canonical docs, routing skills, app-description skills, agent/tool skills, surface/UI skills, planning queues, templates, and examples:

- **Tools are architectural building blocks**: a governed tool is the executable semantic operation inside a capability boundary, not merely an Akka `@FunctionTool` or a UI button.
- **Workstream agents own a bounded tool catalog**: each workstream agent has a purpose, expertise, policies, and a specific set of governed tools available to its human-backed and/or AI-backed actor adapters.
- **Humans and AI both use tools through adapters**: surfaces are structured human tool-use adapters; human chat is a natural-language tool-plan adapter; agent-tools are model-facing adapters.
- **Human chat tool use is allowed when governed**: a human chat request may become a proposed tool plan, but the workstream agent must explain the plan in sufficient detail and receive explicit confirmation before executing consequential tools.
- **The model is not the security boundary**: tool catalog membership, AuthContext, tenant/customer scope, role/capability checks, approval policy, schemas, idempotency, and audit/work traces are enforced deterministically by backend/runtime guidance.
- **Each tool is a transaction boundary**: multi-step plans execute as a sequence of individually authorized and traced tool invocations; failure of one step must not leave the system inconsistent, and results/partial failures must be reported clearly.
- **Surface and chat tool use share semantics**: if the same operation is exposed through a surface and workstream chat, both point to the same governed tool id and capability contract with adapter-specific input mediation, confirmation UX, and trace source.
- **Prior no-direct-command guidance is reconciled**: deterministic surface routing remains a safe default and useful UX, but it is no longer the only acceptable path; confirmed chat-driven tool execution is acceptable when the workstream tool boundary, confirmation, authorization, transactional semantics, and traces are modeled.
- **Queue/task guidance carries the new vertical contract**: generated-app planning tasks require governed tool ids, actor adapters, exposure channels, confirmation/approval behavior, transaction/idempotency semantics, traces, and validation evidence.
- **Installed skills validate cleanly**: relevant pack checks pass after the alignment changes.

## Non-goals

- Do not implement root SaaS Foundation App runtime chat-tool execution in this mini-project.
- Do not grant unrestricted agent autonomy or treat prompt text as authority.
- Do not remove deterministic surface routing; instead reposition it as one human-backed tool adapter path.
- Do not collapse capabilities, surfaces, APIs, workflows, and agent tools into one implementation mechanism.
- Do not weaken tenant isolation, authorization, provider fail-closed behavior, audit/work traces, frontend secret boundaries, or runtime completion doctrine.

## Execution model

Execute one task per fresh harness context. Each task must update `pending-tasks.md`, run required checks or mark blocked with a precise reason, and make one focused git commit before being marked `done`.

At the end of the initial task group, the terminal verification task must check whether the whole skills-pack is aligned. If material gaps remain, it must append new bounded tasks and a new terminal verification task, leaving the mini-project open for another sequential pass.

## Read order for future task sessions

1. `AGENTS.md`
2. `skills-pack/AGENTS.md`
3. `specs/workstream-tool-use-alignment/README.md`
4. `specs/workstream-tool-use-alignment/conversation-capture.md`
5. `specs/workstream-tool-use-alignment/pending-tasks.md`
6. selected sprint/backlog/task brief
7. task-specific `skills-pack/docs/**`, `skills-pack/skills/**`, templates, examples, or tools

## Initial task group

1. Sprint 01: Audit and canonical doctrine.
2. Sprint 02: Skill family alignment.
3. Sprint 03: Planning, templates, validation, and consistency repair.
4. Sprint 04: Terminal verification and follow-up task generation when needed.

## Open concerns

- The exact runtime substrate for confirmed chat tool execution is intentionally left to future implementation tasks in target apps; this mini-project aligns guidance and contracts rather than writing runtime code.
- Some existing guidance strongly favors surface routing and no direct mutation. The alignment should preserve that as a conservative default while allowing confirmed chat tool execution when the governed tool contract is complete.
- Verification must avoid a superficial terminology-only pass. It should search for conflicting guidance, check skill-routing implications, and append follow-up work when gaps remain.
