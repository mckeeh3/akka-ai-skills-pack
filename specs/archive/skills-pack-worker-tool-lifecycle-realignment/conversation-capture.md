# Conversation Capture: Skills-Pack Worker/Tool/Lifecycle Realignment

## Source discussion

This mini-project captures the discussion about significantly realigning `skills-pack/` so it can better support a never-ending stream of app features, specs, tweaks, adjustments, fixes, issues, manual test observations, and implementation requests.

## Accepted framing

The skills-pack should model application development as a continuous loop, not a one-time generation pass:

```text
incoming request
  -> interview / intent reconciliation
  -> build-compile / implementation
  -> manual runtime test / reconciliation
  -> back to interview
```

The app-description is a living graph of current intent. It should describe the app from high-level purpose down through domains, workstreams, workers, surfaces, agents, tools, capabilities, Akka components, tests, security, and observability.

## Key clarification: app workers

The app should model work as performed by workers. A worker may be:

- a human worker using human judgment;
- a software agent worker using an AI model;
- a deterministic system worker using predefined logic.

Human and software workers should be modeled with structural symmetry, while preserving important differences in authority, behavior, and control.

The working analogy:

| Concept | Human worker | Software agent worker |
|---|---|---|
| Worker | Authenticated person / role | AI-backed agent |
| Reasoning engine | Human judgment | AI model |
| Harness | Surface / browser shell | Akka Agent / AutonomousAgent runtime |
| Context | Surface payload, evidence, labels, validation | Prompt, memory, references, tool results |
| Tools | Surface actions, forms, confirmations, browser-mediated actions | Function tools, component tools, MCP tools |
| Constraints | Auth, visible fields, disabled states, confirmations | Tool boundaries, prompts, policies, guardrails |
| Trace | Surface action trace, audit, work trace | Prompt/tool/model/work trace |

Avoid over-literalizing “human as model.” The safer formulation is: human workers use human judgment through governed surfaces; software workers use AI models through governed agent runtimes.

## Key clarification: tools as first-class app building blocks

Tools should become a central app-description component type.

Tools are the semantic abstraction layer between workers/adapters and backend capabilities. They should not be treated as thin wrappers around Akka components.

Canonical chain:

```text
worker -> actor adapter / harness -> governed tool -> capability -> Akka implementation
```

Actor adapters include, at minimum:

- `surface_action`
- `human_chat_tool_plan`
- `agent_tool_call`
- `workflow_step`
- `timer_invocation`
- `consumer_reaction`
- `mcp_tool_call`
- `api_call`
- `internal_call`

A single governed tool may be exposed through multiple actor adapters. Human surface availability must not automatically grant AI tool availability.

Example:

```text
Tool: inviteOrganizationMember
Capability: manage organization invitations
Implementation:
  - InvitationEntity
  - InvitationWorkflow
  - EmailOutboxEntity
  - InvitationView
  - AdminAuditConsumer
  - HTTP endpoint
```

The same operation may be used by a human admin surface, a confirmed human chat plan, an AI admin agent, or an internal workflow, but each exposure must declare authority, confirmation/approval behavior, idempotency, result surfaces, and traces separately.

## Desired skills-pack direction

Realign the pack around:

1. a three-phase continuous development lifecycle;
2. a worker/tool/capability model;
3. an app-description graph contract that makes workers and tools first-class;
4. a compile contract from app-description deltas to implementation slices;
5. skill metadata/classification by phase, family, kind, inputs, outputs, and routes;
6. doctrine compression so broad skills become routers and focused skills keep only focused mechanics;
7. family-by-family skill migration and verification.

## Non-goals captured in discussion

- Do not immediately delete many skills. Skill names are a public routing surface.
- Do not edit the installed `.agents/skills` mirror as source of truth.
- Do not create several independent mini-projects before the doctrine is stable.
- Do not collapse tools into endpoints or Akka components.
- Do not let UI visibility or labels become authorization.
- Do not make AI agents inherit human permissions implicitly.

## Recommended implementation approach

Create one umbrella mini-project first: `skills-pack-worker-tool-lifecycle-realignment`.

Execute it sequentially, one fresh-context task at a time. Start with doctrine and contracts, pilot them on representative skills, then migrate skill families in waves, then verify and decide whether follow-on mini-projects are needed.
