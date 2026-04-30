---
name: akka-solution-decomposition
description: Decompose high-level requirements, prompts, or specification files into the minimal Akka Java SDK component set, then route to the focused skills needed to implement the design.
---

# Akka Solution Decomposition

Use this as the top-level starting skill when the task begins from high-level intent rather than from a fixed Akka component type.

## Goal

Generate or review an Akka solution plan that:
- maps user-facing capabilities to concrete Akka components
- chooses the simplest component set that preserves required business semantics
- makes write model, read model, orchestration, timing, integration, and edge concerns explicit
- routes to the smallest relevant local skill set
- gives a safe implementation order before code generation starts
- acts as the implementation contract for the downstream coding phase

## Supported inputs

For a small canonical example of the intended output shape, see:
- `../../../docs/prd-to-akka-flow.md`
- `../../../docs/examples/purchase-request-prd.md`
- `../../../docs/examples/purchase-request-solution-plan.md`


Use this skill when the input is one or more of:
- a high-level prompt
- a product requirement
- a user-story list
- a business process description
- an API sketch or contract draft
- a UI brief
- a feature request or change request
- a filename or path containing requirements or specifications

If the user provides a filename or path:
1. read the file completely before selecting components
2. extract capabilities, constraints, actors, inputs, outputs, and integration points
3. then produce the component plan

## Required reading

Read these first if present:
- `../../../CONTEXT-WARMUP.md` for repo mental model and session bootstrap
- `../../../AGENTS.md` for authoritative project rules and Akka coding constraints
- `../../../skills/README.md` for local routing across skill families
- `../../../docs/agent-coverage-matrix.md` when the task is agent-related
- `../references/akka-entity-comparison.md`
- `../../../docs/workflow-endpoint-pattern.md`
- `../../../docs/timer-pattern-selection.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`

When requirements already suggest a likely component, also read the official Akka doc for that area before coding:
- `akka-context/sdk/event-sourced-entities.html.md`
- `akka-context/sdk/key-value-entities.html.md`
- `akka-context/sdk/workflows.html.md`
- `akka-context/sdk/views.html.md`
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/sdk/timed-actions.html.md`
- `akka-context/sdk/http-endpoints.html.md`
- `akka-context/sdk/grpc-endpoints.html.md`
- `akka-context/sdk/agents.html.md`

In this repository, prefer these cross-component examples:
- `../../../src/main/java/com/example/application/ShoppingCartEntity.java`
- `../../../src/main/java/com/example/application/DraftCartEntity.java`
- `../../../src/main/java/com/example/application/TransferWorkflow.java`
- `../../../src/main/java/com/example/application/ApprovalWorkflow.java`
- `../../../src/main/java/com/example/application/TicketReservationTimedAction.java`
- `../../../src/main/java/com/example/application/ShoppingCartCheckoutConsumer.java`
- `../../../src/main/java/com/example/application/ReviewRequestsByStatusView.java`
- `../../../src/main/java/com/example/application/ActivityAgent.java`
- `../../../src/main/java/com/example/api/ShoppingCartEndpoint.java`
- `../../../src/main/java/com/example/api/TransferWorkflowEndpoint.java`
- `../../../src/main/java/com/example/api/WebUiHomeEndpoint.java`
- `../../../src/main/java/com/example/api/ShoppingCartGrpcEndpointImpl.java`
- `../../../src/main/java/com/example/api/ShoppingCartMcpEndpoint.java`

## What this skill must produce

Before any coding, produce a component plan with these sections:
1. Inputs
2. Capability summary
3. Chosen components
4. Why each component exists
5. Skill routing
6. Open questions and assumptions
7. Recommended implementation order
8. Required tests

Treat sections 5, 7, and 8 as the implementation handoff.
The plan is not complete if it only names components.
It must also tell the downstream implementation phase:
- which skills to load for code generation
- which skills to load for test generation
- what order to implement the components
- whether endpoint generation, web UI generation, or documentation/snippet generation belong downstream

## Decomposition workflow

### 1. Extract capabilities

List:
- actors
- commands and mutations
- queries, search, and reporting needs
- long-running processes
- time-based behavior
- async integrations
- human approval or pause points
- edge and API channels
- browser UI needs, including screens, navigation, forms, frontend state, realtime behavior, accessibility, and responsive requirements
- AI and LLM needs
- security constraints

### 2. Identify the write model

Ask:
- what state must be durable?
- is there one aggregate or several?
- does history matter or only latest state?
- are durable facts or events part of the business language?

If a stateful core exists but entity type is not yet fixed, route to:
- `akka-entity-type-selection`

### 3. Add orchestration only when required

Choose a `Workflow` when:
- the use case is multi-step and durable
- retries or restarts must not lose progress
- compensation or approval is required
- several components or integrations must be coordinated

Do not add a workflow for a simple single-entity command flow.

### 4. Add read models only when query needs justify them

Choose a `View` when:
- the user needs list, search, filter, or reporting queries
- query shape differs from write-model shape
- data must be projected from events, updates, workflow state, topics, or service streams
- streaming query results or live updates are required

Do not add a view for simple direct single-entity lookups unless the query pattern truly needs projection.

### 5. Add async reactions only when something must react after the write

Choose a `Consumer` when:
- one component must react asynchronously to another component's updates
- messages come from topics or service streams
- side effects should happen outside the entity command handler
- events need republishing to topics or service streams

### 6. Add time-based components only when deadlines or reminders exist

Choose a `TimedAction` when:
- a timeout, expiry, reminder, retry delay, or scheduled callback is required
- the schedule must call back into an entity or workflow safely
- obsolete timer executions must be normalized to no-op or done behavior

### 7. Add AI components only when the requirement is genuinely LLM-driven

Choose an `Agent` when:
- the behavior depends on prompt-driven generation, extraction, classification, evaluation, or summarization
- structured LLM output is needed
- tools, memory, guardrails, or multi-agent orchestration are required

Do not introduce an agent for deterministic business rules that should stay in code.

### 8. Choose edge and API surfaces

Choose:
- `HTTP endpoint` for REST, browser integration, SSE, WebSocket, static assets, or co-hosted web UI
- `Akka-hosted web UI app` for full browser applications with screens, typed API clients, forms, state, selected frontend project shape, and frontend quality requirements
- `gRPC endpoint` for protobuf-first service APIs
- `MCP endpoint` for LLM-oriented tools, resources, or prompts

A single solution may expose more than one edge surface.

### 9. Add security and delivery concerns explicitly

Check whether the requirements imply:
- JWT-protected endpoints
- internal-only ACL endpoints
- SSE reconnect support
- WebSocket interaction
- packaged browser UI assets
- notifications or service streams

### 10. Generate the implementation order

Prefer this order unless requirements force another:
1. domain records and invariants
2. stateful core components: entities and workflows
3. views
4. consumers and timed actions
5. endpoints and web UI
6. tests for each component family
7. docs or snippets if the task includes repository guidance

## Component selection guide

### Stateful core

Choose one or more of:
- `EventSourcedEntity` — event history, facts, replay, audit
- `KeyValueEntity` — latest state only, simpler snapshot-style model
- `Workflow` — durable multi-step orchestration across components or integrations

### Query and read model

Choose:
- direct entity or workflow read when a simple point lookup is enough
- `View` when list, search, reporting, or projection is required

### Async and integration

Choose one or more of:
- `Consumer` — react to updates, topics, or service streams
- `TimedAction` — deadlines, reminders, retries, expiries
- notifications, topic production, or service streams when clients or downstream services must observe progress

### Edge surfaces

Choose one or more of:
- `HTTP endpoint`
- `gRPC endpoint`
- `MCP endpoint`
- HTTP-hosted web UI via `akka-http-endpoint-web-ui`
- fully capable frontend app via `akka-web-ui-apps`, usually with `akka-web-ui-frontend-project` for full React/Vite-style apps
- SSE or WebSocket companions when live browser updates are required

### AI layer

Choose one or more of:
- `Agent`
- workflow-supervised multi-agent orchestration
- prompt-template or session-memory runtime state
- evaluator or guardrail patterns

## Routing rules

After decomposition, load the minimal next skill set.
The routing output should feed code generation directly, not serve as a purely informational appendix.
For every chosen component, list the implementation skills and the corresponding testing skill when one exists.

### If the core decision is still entity type

Load:
- `akka-entity-type-selection`

This is still part of the planning handoff, not the final coding step.
Only move into entity code generation after this decision is resolved.

### If the plan includes event-sourced state

Load:
- `akka-event-sourced-entities`
- `akka-ese-domain-modeling`
- `akka-ese-application-entity`

Then add only what is needed:
- `akka-ese-edge-and-flow-patterns`
- `akka-ese-ttl`
- `akka-ese-notifications`
- `akka-ese-replication`
- `akka-ese-unit-testing`
- `akka-ese-integration-testing`

### If the plan includes key-value state

Load:
- `akka-key-value-entities`
- `akka-kve-domain-modeling`
- `akka-kve-application-entity`

Then add only what is needed:
- `akka-kve-edge-and-flow-patterns`
- `akka-kve-ttl`
- `akka-kve-notifications`
- `akka-kve-replication`
- `akka-kve-unit-testing`
- `akka-kve-integration-testing`

### If the plan includes orchestration

Load:
- `akka-workflows`
- `akka-workflow-component`

Then add only what is needed:
- `akka-workflow-compensation`
- `akka-workflow-notifications`
- `akka-workflow-pausing`
- `akka-workflow-testing`

### If the plan includes projections or reporting queries

Load:
- `akka-views`

Then load the source-specific skill:
- `akka-view-from-event-sourced-entity`
- `akka-view-from-key-value-entity`
- `akka-view-from-workflow`
- `akka-view-from-topic`
- `akka-view-from-service-stream`

Then add:
- `akka-view-query-patterns`
- `akka-view-streaming` when live query updates are needed
- `akka-view-testing`

### If the plan includes async reactions or integration bridges

Load:
- `akka-consumers`

Then add the source-specific skill:
- `akka-consumer-from-event-sourced-entity`
- `akka-consumer-from-key-value-entity`
- `akka-consumer-from-workflow`
- `akka-consumer-from-topic`
- `akka-consumer-from-service-stream`

Then add:
- `akka-consumer-producing` when the consumer republishes or exposes outputs
- `akka-consumer-testing`

### If the plan includes deadlines, reminders, or expiry

Load:
- `akka-timed-actions`
- `akka-timed-action-component`
- `akka-timers-scheduling`
- `akka-timed-action-testing`

### If the plan includes a complete browser app

Load:
- `akka-web-ui-apps`
- `akka-http-endpoints`
- `akka-http-endpoint-web-ui`

Then add only what is needed:
- `akka-web-ui-frontend-project` for standard frontend projects
- `akka-web-ui-lightweight-typescript` for deliberately framework-free apps
- `akka-web-ui-api-client`
- `akka-web-ui-state-rendering`
- `akka-web-ui-forms-validation`
- `akka-web-ui-realtime`
- `akka-web-ui-accessibility-responsive`
- `akka-web-ui-testing`
- `akka-http-endpoint-component-client`
- `akka-http-endpoint-sse`
- `akka-http-endpoint-websocket`
- `akka-http-endpoint-jwt` only when security implementation is in scope
- `akka-http-endpoint-testing`

The implementation handoff must include frontend screens, frontend project shape, state model, API contracts, loading/empty/error states, accessibility/responsive requirements, static asset route plan, SPA routing choice, and tests.

### If the plan includes HTTP APIs or simple browser UI delivery

Load:
- `akka-http-endpoints`

Then add only what is needed:
- `akka-http-endpoint-component-client`
- `akka-http-endpoint-request-context`
- `akka-http-endpoint-web-ui`
- `akka-http-endpoint-static-content`
- `akka-http-endpoint-low-level`
- `akka-http-endpoint-http-client-provider`
- `akka-http-endpoint-sse`
- `akka-http-endpoint-websocket`
- `akka-http-endpoint-jwt`
- `akka-http-endpoint-acl-internal`
- `akka-http-endpoint-testing`

### If the plan includes protobuf-first APIs

Load:
- `akka-grpc-endpoints`

Then add only what is needed:
- `akka-grpc-endpoint-component-client`
- `akka-grpc-endpoint-request-context`
- `akka-grpc-endpoint-streaming`
- `akka-grpc-endpoint-jwt`
- `akka-grpc-proto-design`
- `akka-grpc-endpoint-testing`

### If the plan includes LLM-facing tools, resources, or prompts

Load:
- `akka-mcp-endpoints`

Then add only what is needed:
- `akka-mcp-endpoint-component-client`
- `akka-mcp-endpoint-request-context`
- `akka-mcp-endpoint-resources-prompts`
- `akka-mcp-endpoint-testing`

### If the plan includes AI and LLM behavior

Load:
- `akka-agents`

Then add only what is needed:
- `akka-agent-component`
- `akka-agent-structured-responses`
- `akka-agent-tools`
- `akka-agent-component-tools`
- `akka-agent-mcp-tools`
- `akka-agent-multimodal`
- `akka-agent-memory`
- `akka-agent-streaming`
- `akka-agent-orchestration`
- `akka-agent-guardrails`
- `akka-agent-evaluation`
- `akka-agent-runtime-state`
- `akka-agent-testing`

## Planning-to-implementation handoff

After producing the solution plan, convert it into a concrete work queue:
1. take the recommended implementation order
2. for each component in that order, load only the named implementation skills
3. generate that component's code before moving to the next major component
4. generate the corresponding tests for that component family
5. generate endpoints, web UI, or documentation/snippets when the plan explicitly includes them

Decomposition has succeeded only when a future agent can follow the plan mechanically into focused implementation work.

## Standard output template

Use this exact response shape whenever the task starts from requirements:

```md
# Akka Solution Plan

## Inputs
- source:
- assumptions:

## Capability summary
- ...

## Chosen components
- <ComponentType>: <ComponentName> — <purpose>

## Why each component exists
- <ComponentName>: ...

## Skill routing
- <skill-name>
- ...

## Open questions and assumptions
- question:
- assumption:

## Recommended implementation order
1. ...
2. ...

## Required tests
- <test type> — <component>
```

If requirements are incomplete, still produce the best provisional plan, but separate assumptions from confirmed facts.

## Open questions to ask when the requirements are underspecified

Ask only the smallest set needed to avoid architectural mistakes:
- Does the business need audit, history, or replay, or is latest state enough?
- Is there a multi-step process that must survive retries and restarts?
- Are approvals or human wait states required?
- Are timeout, reminder, or expiry behaviors required?
- What list, search, or reporting queries are needed?
- Which external interfaces are required: HTTP, gRPC, MCP, browser UI?
- Are live updates needed via SSE, WebSocket, or notifications?
- Are there downstream integrations via topics or service streams?
- Are JWT or internal-only ACL constraints required?
- Is any part of the behavior genuinely LLM-driven?

## Anti-patterns

Avoid:
- starting with endpoint code before identifying the write model and process model
- choosing only one component family because it is familiar
- adding a workflow for simple one-step entity operations
- adding an agent for deterministic rules that belong in domain code
- exposing query-heavy use cases directly from write models when a view is the better fit
- performing side effects inside entity command handlers instead of routing through consumers or other supported patterns
- generating code before listing unresolved assumptions

## Final review checklist

Before moving from planning to coding, verify:
- every user-facing capability maps to at least one concrete component or an explicit decision not to add one
- each chosen component has a clear purpose and owning package
- entity type decisions are justified
- workflow usage is justified
- view needs are explicit
- timer and consumer needs are explicit
- edge and API surfaces are explicit
- required tests are listed for each component family
- the next skills to load are listed in implementation order
- the plan clearly feeds the downstream implementation phase instead of stopping at decomposition
- open questions and assumptions are called out separately

## Response style

When answering:
- start with a short capability summary
- then list the proposed Akka components
- justify each component in one line
- list the exact next skills to load
- make the implementation order read like a downstream coding handoff
- state open questions before coding
- do not jump into code until the component plan is explicit
