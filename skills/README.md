# Akka Skill Routing Map

This directory contains AI-focused skills for turning high-level requirements into the right Akka Java SDK solution, then turning that solution plan into the downstream implementation phase for concrete component code, tests, and related delivery assets.

These skills are primarily an **internal routing layer for the harness**.
Users should be able to describe intent in natural language; the harness should infer the right path and load the smallest relevant skill set.

## Intent-driven usage flow

Use the skills in this order:

1. read the requirements, PRD, spec, prompt, API sketch, UI brief, feature request, or change request
2. use Stage 1 decomposition via `akka-solution-decomposition` when the solution shape is still unclear
3. use Stage 2 only if one structural decision is still unresolved, such as `akka-entity-type-selection`
4. move to Stage 3 to load only the focused implementation skills for the chosen components
5. use the accepted solution plan as the implementation contract for the downstream coding phase
6. generate code and tests only after decomposition and structural selection are done

Short reusable version:
- `../docs/intent-driven-usage-flow.md`
- `../docs/prd-to-akka-flow.md`

## Visible 3-stage skill model

Use the skill library as a visible 3-stage hierarchy:

### Stage 1: Intent and architecture
Start here when you have a PRD, requirements doc, user story, process description, API sketch, UI brief, or similar high-level input and still need to derive the Akka solution shape.

Primary Stage 1 entry skills:
- `akka-solution-decomposition`
- `akka-prd-to-specs-backlog` — use when the user wants repo-ready planning artifacts under `specs/`, not just a solution plan

### Stage 2: Structural decisions
Use this stage when you already know part of the architecture, but still need to resolve a focused design choice before coding.
This is a narrower follow-on stage, not the default front door for broad requirements.

Primary Stage 2 skill currently available:
- `akka-entity-type-selection` — choose between Event Sourced Entity and Key Value Entity when you know you need state but not which state model

### Stage 3: Focused component implementation
Use this stage only after the solution shape is already clear enough to generate code.
This is the downstream implementation phase driven by the accepted solution plan.

Stage 3 is the family of focused implementation skills for peer building blocks such as:
- workflows
- views
- consumers
- timed actions
- endpoints and web UI delivery
- agents
- entities

Entities are one Stage 3 family among several peers.
Not every task starts at Stage 3.
If all you have is a requirements artifact or other broad specification input, start at Stage 1.
Even if the problem sounds stateful, use Stage 1 first when the overall component set is still unknown.
Use Stage 2 only when the task is already narrowed to a stateful core and you still need to choose the entity style.
Move to Stage 3 when the architecture is settled enough to write code and tests.

Primary flow:
1. start from a PRD, requirements doc, user story, process description, API sketch, UI brief, or similar high-level input
2. use Stage 1 to decompose that input into the right Akka component set
3. use Stage 2 when a focused architecture decision is still unresolved
4. use Stage 3 to load only the implementation skills needed for the chosen components

Current local Stage 3 suites:
- Agents
- Workflows
- Views
- Consumers
- Timed Actions
- HTTP Endpoints and web UI patterns
- gRPC Endpoints
- MCP Endpoints
- Event Sourced Entities
- Key Value Entities

If you have high-level requirements, a prompt, or a specification file and do not yet know the Akka component set, start with Stage 1:
- `akka-solution-decomposition`

If the task is already narrowed to a stateful component and you have not yet chosen the entity type, start with Stage 2:
- `akka-entity-type-selection`

You can also consult the comparison/reference files:
- `references/akka-entity-comparison.md`
- `references/akka-grpc-jwt-patterns.md`
- `../docs/agent-coverage-matrix.md`
- `../docs/agent-runtime-state-reference.md`
- `../docs/workflow-endpoint-pattern.md`
- `../docs/timer-pattern-selection.md`

## PRD planning entry skills

### Solution decomposition skill

Start with:
- `akka-solution-decomposition`

Use when the goal is to derive the Akka component set and implementation handoff, but not necessarily to materialize a full repository planning package.

### PRD to specs/backlog skill

Start with:
- `akka-prd-to-specs-backlog`

Use when the task starts from a PRD or high-level requirements and the user wants the result written as a repository planning structure under `specs/`, including:
- `specs/akka-solution-plan.md`
- cross-cutting specs
- numbered slice specs
- numbered build backlogs
- execution-order readmes

This skill builds on `akka-solution-decomposition` and continues into harness-friendly file generation for downstream implementation.

### Slice spec to backlog skill

Start with:
- `akka-slice-spec-to-backlog`

Use when a `specs/slices/*.md` file already exists and the next task is to generate or refine only the matching `specs/backlog/*-build-backlog.md` file.

This is the narrow follow-on planning skill for turning one slice into an implementation-ready backlog without redoing the full PRD decomposition.

### Solution decomposition details

Start with:
- `akka-solution-decomposition`

Use when the task begins from a product requirement, user story, process description, API sketch, UI brief, or a filename containing specifications and you need to decide the Akka component set before coding.

The output of this skill is not the final answer by itself.
It is the implementation contract for downstream work:
- it identifies the chosen components
- it defines implementation order
- it maps each component to the exact code-generation and test-generation skills to load next
- it can also route endpoint generation, web UI generation, and documentation/snippet generation when those are part of the task

This skill routes to:
- `akka-workflows` for durable multi-step orchestration
- `akka-views` for list/search/reporting read models
- `akka-consumers` for async reactions, integrations, and republishing
- `akka-timed-actions` for deadlines, reminders, and expiry
- `akka-http-endpoints` for REST, SSE, WebSocket, static content, and browser-hosted UI
- `akka-grpc-endpoints` for protobuf-first service APIs
- `akka-mcp-endpoints` for LLM-facing tools, resources, and prompts
- `akka-agents` when the solution genuinely needs LLM-driven behavior
- `akka-entity-type-selection` for EventSourcedEntity vs KeyValueEntity decisions when the plan includes a stateful core but the entity style is still undecided

## Planning-to-implementation handoff

Once a solution plan is accepted, treat it as the work queue for coding:
1. take the chosen components in implementation order
2. load only the Stage 3 skills named for the next component
3. generate that component's code and its corresponding tests
4. repeat for each remaining component
5. finish any downstream endpoint, web UI, or documentation/snippet work called out by the plan

Decomposition is complete only when it enables focused implementation work with low ambiguity.
For a lightweight template, see `../docs/solution-plan-to-implementation-queue.md`.

## Agent skills

Start with:
- `akka-agents`

Then load the focused skill that matches the current task:

### Component structure
Use when writing the agent class itself.
- `akka-agent-component`

### Structured responses
Use when the agent should return typed JSON-mapped output.
- `akka-agent-structured-responses`

### Tools
Use when the agent should call local or external function tools.
- `akka-agent-tools`

### Component tools
Use when the agent should call Akka Views, entities, or workflows as tools.
- `akka-agent-component-tools`

### MCP tools
Use when the agent should call remote MCP-hosted tools.
- `akka-agent-mcp-tools`

### Multimodal
Use when the agent should send images or PDFs, or needs a custom content loader.
- `akka-agent-multimodal`

### Memory
Use when the main concern is session ids, bounded history, or filtered memory.
- `akka-agent-memory`

### Streaming
Use when the agent should stream tokens to HTTP or notifications.
- `akka-agent-streaming`

### Orchestration
Use when workflows or other components should call agents reliably.
- `akka-agent-orchestration`

### Guardrails
Use when runtime safety controls are the main concern.
- `akka-agent-guardrails`

### Evaluation
Use when LLM-as-judge or evaluator agents are the main concern.
- `akka-agent-evaluation`

### Runtime state
Use when the task involves built-in PromptTemplate or SessionMemoryEntity state, including views, endpoints, analytics, or compaction flows.
- `akka-agent-runtime-state`

### Testing
Use:
- `akka-agent-testing`

## Event Sourced Entity skills

Start with:
- `akka-event-sourced-entities`

Then load the focused skill that matches the current task:

### Domain modeling
Use when working on state, events, commands, validators, command-to-event logic, or pure replay logic.
- `akka-ese-domain-modeling`

### Application entity core
Use when writing the `EventSourcedEntity` class itself.
- `akka-ese-application-entity`

### Application entity feature skills
Load these only when the task needs the feature:
- `akka-ese-ttl` — `expireAfter(...)` and automatic expiry
- `akka-ese-notifications` — `NotificationPublisher`, `NotificationStream`, SSE mapping
- `akka-ese-replication` — strong reads, replication filters, `@EnableReplicationFilter`

### Flow selection
Use when deciding how the entity participates in endpoint or internal flows.
- `akka-ese-edge-and-flow-patterns`

### Documentation snippets
Use when writing or replacing docs with focused ESE examples.
- `akka-ese-doc-snippets`

### Testing
Use:
- `akka-ese-unit-testing`
- `akka-ese-integration-testing`

## Key Value Entity skills

Start with:
- `akka-key-value-entities`

Then load the focused skill that matches the current task:

### Domain modeling
Use when working on state, commands, validators, command-to-state logic, or pure business-decision helpers.
- `akka-kve-domain-modeling`

### Application entity core
Use when writing the `KeyValueEntity` class itself.
- `akka-kve-application-entity`

### Application entity feature skills
Load these only when the task needs the feature:
- `akka-kve-ttl` — `expireAfter(...)` and automatic expiry
- `akka-kve-notifications` — `NotificationPublisher`, `NotificationStream`, SSE mapping
- `akka-kve-replication` — strong reads, replication filters, `@EnableReplicationFilter`

### Flow selection
Use when deciding how the entity participates in endpoint or internal flows.
- `akka-kve-edge-and-flow-patterns`

### Documentation snippets
Use when writing or replacing docs with focused KVE examples.
- `akka-kve-doc-snippets`

### Testing
Use:
- `akka-kve-unit-testing`
- `akka-kve-integration-testing`

## Workflow skills

Start with:
- `akka-workflows`

Then load the focused skill that matches the current task:

### Component structure
Use when writing the workflow class, state transitions, and `WorkflowSettings`.
- `akka-workflow-component`

### Compensation
Use when a later step must undo earlier work.
- `akka-workflow-compensation`

### Notifications
Use when clients should subscribe to workflow progress.
- `akka-workflow-notifications`

### Pause/resume
Use when the workflow must wait for an approval or later input.
- `akka-workflow-pausing`

### Testing
Use:
- `akka-workflow-testing`

## Timed action skills

Start with:
- `akka-timed-actions`

Then load the focused skill that matches the current task:

### Component structure
Use when writing the `TimedAction` class itself.
- `akka-timed-action-component`

### Timer scheduling
Use when the main work is `TimerScheduler.createSingleTimer(...)`, timer naming, replacement, or deletion.
- `akka-timers-scheduling`

### Testing
Use:
- `akka-timed-action-testing`

## Consumer skills

Start with:
- `akka-consumers`

Then load the focused skill that matches the current task:

### Source selection
Use the source-specific skill for the upstream you are consuming.
- `akka-consumer-from-event-sourced-entity`
- `akka-consumer-from-key-value-entity`
- `akka-consumer-from-workflow`
- `akka-consumer-from-topic`
- `akka-consumer-from-service-stream`

### Producing
Use when the consumer republishes or transforms messages into a topic or service stream.
- `akka-consumer-producing`

### Testing
Use when validating consumer flows with TestKit incoming or outgoing eventing hooks.
- `akka-consumer-testing`

## View skills

Start with:
- `akka-views`

Then load the focused skill that matches the current task:

### Source selection
Use the source-specific skill for the updater type you are implementing.
- `akka-view-from-event-sourced-entity`
- `akka-view-from-key-value-entity`
- `akka-view-from-workflow`
- `akka-view-from-topic`
- `akka-view-from-service-stream`

### Query design
Use when designing wrapper records, aliases, or pagination.
- `akka-view-query-patterns`

### Streaming
Use when the view query should stream current rows or live updates.
- `akka-view-streaming`

### Testing
Use when validating projections with mocked incoming messages.
- `akka-view-testing`

## HTTP endpoint skills

Start with:
- `akka-http-endpoints`

Then load the focused skill that matches the current task:

### Component-calling endpoints
Use when the endpoint maps HTTP requests to Akka component calls.
- `akka-http-endpoint-component-client`

### Request-context endpoints
Use when the endpoint depends on query params, headers, principals, or other request metadata.
- `akka-http-endpoint-request-context`

### Web UI endpoints
Use when the service should host a packaged browser UI, especially with co-hosted JSON APIs, SSE pages, or WebSocket pages.
- `akka-http-endpoint-web-ui`

### Static content endpoints
Use when the endpoint serves packaged HTML, CSS, OpenAPI files, or other assets without broader interactive browser behavior.
- `akka-http-endpoint-static-content`

### Low-level HTTP endpoints
Use when the endpoint needs `HttpResponse`, `HttpEntity.Strict`, or other lower-level HTTP model APIs.
- `akka-http-endpoint-low-level`

### HTTP client provider endpoints
Use when the endpoint calls another HTTP service through `HttpClientProvider`.
- `akka-http-endpoint-http-client-provider`

### SSE endpoints
Use when the endpoint streams server-sent events or must support reconnects.
- `akka-http-endpoint-sse`

### WebSocket endpoints
Use when the endpoint needs bidirectional streaming over `@WebSocket`.
- `akka-http-endpoint-websocket`

### JWT-secured endpoints
Use when the endpoint validates bearer tokens and reads claims.
- `akka-http-endpoint-jwt`

### Internal-only ACL endpoints
Use when the endpoint should only be callable by services or needs method-level ACL overrides.
- `akka-http-endpoint-acl-internal`

### Testing
Use:
- `akka-http-endpoint-testing`

## gRPC endpoint skills

Start with:
- `akka-grpc-endpoints`

Then load the focused skill that matches the current task:

### Component-calling endpoints
Use when the endpoint maps protobuf requests to Akka component calls.
- `akka-grpc-endpoint-component-client`

### Request-context endpoints
Use when the endpoint depends on principals, gRPC metadata, JWT claims, or tracing.
- `akka-grpc-endpoint-request-context`

### Streaming endpoints
Use when the endpoint returns server-streamed protobuf replies.
- `akka-grpc-endpoint-streaming`

### JWT-secured endpoints
Use when the endpoint validates bearer tokens and reads claims.
- `akka-grpc-endpoint-jwt`

### Proto design
Use when the main task is `.proto` structure, schema evolution, or common/external protobuf types.
- `akka-grpc-proto-design`

### Testing
Use:
- `akka-grpc-endpoint-testing`

## MCP endpoint skills

Start with:
- `akka-mcp-endpoints`

Then load the focused skill that matches the current task:

### Component-calling MCP endpoints
Use when MCP tools or resources need current Akka component state.
- `akka-mcp-endpoint-component-client`

### Request-context MCP endpoints
Use when the MCP endpoint depends on headers, principals, JWT claims, or tracing.
- `akka-mcp-endpoint-request-context`

### MCP resources and prompts
Use when the task is mainly about resource URIs, URI templates, packaged resources, or prompt templates.
- `akka-mcp-endpoint-resources-prompts`

### Testing
Use:
- `akka-mcp-endpoint-testing`

## Practical combinations

### New single-purpose agent
Load:
- `akka-agents`
- `akka-agent-component`
- `akka-agent-structured-responses`
- `akka-agent-testing`

### New tool-using agent
Load:
- `akka-agents`
- `akka-agent-component`
- `akka-agent-tools`
- `akka-agent-testing`

### New streaming agent exposed through HTTP
Load:
- `akka-agents`
- `akka-agent-streaming`
- `akka-http-endpoints`
- `akka-http-endpoint-component-client`
- `akka-agent-testing`
- `akka-http-endpoint-testing`

### New workflow-supervised agent flow
Load:
- `akka-agents`
- `akka-agent-orchestration`
- `akka-workflows`
- `akka-workflow-component`
- `akka-agent-testing`
- `akka-workflow-testing`

### Add guardrails to an agent
Load:
- `akka-agents`
- `akka-agent-guardrails`

### Add evaluator agents or LLM-as-judge checks
Load:
- `akka-agents`
- `akka-agent-evaluation`
- `akka-agent-testing`

### Work with prompt templates or session-memory runtime state
Load:
- `akka-agents`
- `akka-agent-runtime-state`

### Stateful core is already known; now decide between ESE and KVE
Load:
- `akka-entity-type-selection`

Use this only when the broader Akka component set is already clear enough that the remaining question is ESE vs KVE.
Then continue with either the ESE or KVE suite.

### New endpoint-facing event sourced entity
Load:
- `akka-event-sourced-entities`
- `akka-ese-domain-modeling`
- `akka-ese-application-entity`
- `akka-ese-edge-and-flow-patterns`
- `akka-ese-unit-testing`
- `akka-ese-integration-testing`

### New endpoint-facing key value entity
Load:
- `akka-key-value-entities`
- `akka-kve-domain-modeling`
- `akka-kve-application-entity`
- `akka-kve-edge-and-flow-patterns`
- `akka-kve-unit-testing`
- `akka-kve-integration-testing`

### Add TTL to an entity
Load either:
- `akka-ese-application-entity` + `akka-ese-ttl`
- `akka-kve-application-entity` + `akka-kve-ttl`

### Add live notifications
Load either:
- `akka-ese-application-entity` + `akka-ese-notifications`
- `akka-kve-application-entity` + `akka-kve-notifications`

### Add replication support
Load either:
- `akka-ese-application-entity` + `akka-ese-replication`
- `akka-kve-application-entity` + `akka-kve-replication`

### New workflow component
Load:
- `akka-workflows`
- `akka-workflow-component`
- `akka-workflow-testing`

### New workflow with compensation
Load:
- `akka-workflows`
- `akka-workflow-component`
- `akka-workflow-compensation`
- `akka-workflow-testing`

### New workflow with notifications
Load:
- `akka-workflows`
- `akka-workflow-component`
- `akka-workflow-notifications`
- `akka-workflow-testing`

### New workflow with pause/resume behavior
Load:
- `akka-workflows`
- `akka-workflow-component`
- `akka-workflow-pausing`
- `akka-workflow-testing`

### New timer-backed expiry or reminder flow
Load:
- `akka-timed-actions`
- `akka-timers-scheduling`
- `akka-timed-action-component`
- `akka-timed-action-testing`

Add one of these if the timer targets broader component work:
- `akka-http-endpoint-component-client`
- `akka-key-value-entities`
- `akka-event-sourced-entities`
- `akka-workflows`

### New workflow-triggered timer flow
Load:
- `akka-workflows`
- `akka-workflow-component`
- `akka-timed-actions`
- `akka-timers-scheduling`
- `akka-timed-action-testing`

### New consumer reacting to event sourced events
Load:
- `akka-consumers`
- `akka-consumer-from-event-sourced-entity`
- `akka-consumer-testing`

### New consumer reacting to key value updates
Load:
- `akka-consumers`
- `akka-consumer-from-key-value-entity`
- `akka-consumer-testing`

### New consumer reacting to workflow updates
Load:
- `akka-consumers`
- `akka-consumer-from-workflow`
- `akka-consumer-producing`
- `akka-consumer-testing`

### New topic-ingesting consumer
Load:
- `akka-consumers`
- `akka-consumer-from-topic`
- `akka-consumer-testing`

### New service-to-service subscriber consumer
Load:
- `akka-consumers`
- `akka-consumer-from-service-stream`
- `akka-consumer-producing`

### New topic or service-stream producer consumer
Load:
- `akka-consumers`
- `akka-consumer-producing`
- `akka-consumer-testing`

### New HTTP endpoint that calls components
Load:
- `akka-http-endpoints`
- `akka-http-endpoint-component-client`
- `akka-http-endpoint-testing`

### New HTTP endpoint using request context only
Load:
- `akka-http-endpoints`
- `akka-http-endpoint-request-context`
- `akka-http-endpoint-testing`

### New Akka-served web UI
Load:
- `akka-http-endpoints`
- `akka-http-endpoint-web-ui`
- `akka-http-endpoint-testing`

Then add one or more focused companions as needed:
- `akka-http-endpoint-static-content`
- `akka-http-endpoint-sse`
- `akka-http-endpoint-websocket`
- `akka-http-endpoint-jwt`
- `akka-http-endpoint-acl-internal`

### New HTTP endpoint serving static content
Load:
- `akka-http-endpoints`
- `akka-http-endpoint-static-content`
- `akka-http-endpoint-testing`

### New low-level HTTP endpoint
Load:
- `akka-http-endpoints`
- `akka-http-endpoint-low-level`
- `akka-http-endpoint-testing`

### New HTTP endpoint calling another HTTP service
Load:
- `akka-http-endpoints`
- `akka-http-endpoint-http-client-provider`
- `akka-http-endpoint-testing`

### New HTTP endpoint streaming SSE
Load:
- `akka-http-endpoints`
- `akka-http-endpoint-sse`
- `akka-http-endpoint-testing`

### New WebSocket endpoint
Load:
- `akka-http-endpoints`
- `akka-http-endpoint-websocket`
- `akka-http-endpoint-testing`

### New HTTP endpoint secured with JWTs
Load:
- `akka-http-endpoints`
- `akka-http-endpoint-jwt`
- `akka-http-endpoint-testing`

### New internal-only HTTP endpoint
Load:
- `akka-http-endpoints`
- `akka-http-endpoint-acl-internal`
- `akka-http-endpoint-testing`

### New gRPC endpoint that calls components
Load:
- `akka-grpc-endpoints`
- `akka-grpc-endpoint-component-client`
- `akka-grpc-endpoint-testing`

### New gRPC endpoint using request context or ACLs
Load:
- `akka-grpc-endpoints`
- `akka-grpc-endpoint-request-context`
- `akka-grpc-endpoint-testing`

### New streaming gRPC endpoint
Load:
- `akka-grpc-endpoints`
- `akka-grpc-endpoint-streaming`
- `akka-grpc-endpoint-testing`

### New JWT-secured gRPC endpoint
Load:
- `akka-grpc-endpoints`
- `akka-grpc-endpoint-jwt`
- `akka-grpc-endpoint-testing`

### New gRPC protobuf contract
Load:
- `akka-grpc-endpoints`
- `akka-grpc-proto-design`

### New MCP endpoint that calls components
Load:
- `akka-mcp-endpoints`
- `akka-mcp-endpoint-component-client`
- `akka-mcp-endpoint-testing`

### New MCP endpoint using request context or JWTs
Load:
- `akka-mcp-endpoints`
- `akka-mcp-endpoint-request-context`
- `akka-mcp-endpoint-testing`

### New MCP resource or prompt endpoint
Load:
- `akka-mcp-endpoints`
- `akka-mcp-endpoint-resources-prompts`
- `akka-mcp-endpoint-testing`

### Create a view from an event sourced entity
Load:
- `akka-views`
- `akka-view-from-event-sourced-entity`
- `akka-view-query-patterns`
- `akka-view-testing`

### Create a view from a key value entity
Load:
- `akka-views`
- `akka-view-from-key-value-entity`
- `akka-view-query-patterns`
- `akka-view-testing`

### Create a view from a workflow
Load:
- `akka-views`
- `akka-view-from-workflow`
- `akka-view-query-patterns`
- `akka-view-testing`

### Create a view from a topic
Load:
- `akka-views`
- `akka-view-from-topic`
- `akka-view-query-patterns`
- `akka-view-testing`

### Create a view from another Akka service stream
Load:
- `akka-views`
- `akka-view-from-service-stream`
- `akka-view-query-patterns`

### Add view streaming
Load:
- `akka-views`
- `akka-view-streaming`
- `akka-view-testing`

## Repository reference examples

### Event sourced entities
Core entities:
- `../src/main/java/com/example/application/ShoppingCartEntity.java`
- `../src/main/java/com/example/application/OrderEntity.java`
- `../src/main/java/com/example/application/ExpiringShoppingCartEntity.java`

Domain examples:
- `../src/main/java/com/example/domain/ShoppingCart.java`
- `../src/main/java/com/example/domain/Order.java`
- `../src/main/java/com/example/domain/ExpiringShoppingCart.java`

Testing examples:
- `../src/test/java/com/example/application/ShoppingCartEntityTest.java`
- `../src/test/java/com/example/application/OrderEntityTest.java`
- `../src/test/java/com/example/application/ExpiringShoppingCartEntityTest.java`

### Key value entities
Core entities:
- `../src/main/java/com/example/application/DraftCartEntity.java`
- `../src/main/java/com/example/application/PurchaseOrderEntity.java`
- `../src/main/java/com/example/application/ExpiringDraftCartSessionEntity.java`

Domain examples:
- `../src/main/java/com/example/domain/DraftCart.java`
- `../src/main/java/com/example/domain/PurchaseOrder.java`
- `../src/main/java/com/example/domain/ExpiringDraftCartSession.java`

Testing examples:
- `../src/test/java/com/example/application/DraftCartEntityTest.java`
- `../src/test/java/com/example/application/PurchaseOrderEntityTest.java`
- `../src/test/java/com/example/application/ExpiringDraftCartSessionEntityTest.java`

### Agents
Core agent examples:
- `../src/main/java/com/example/application/ActivityAgent.java`
- `../src/main/java/com/example/application/TemplateBackedActivityAgent.java`
- `../src/main/java/com/example/application/WeatherAgent.java`
- `../src/main/java/com/example/application/WeatherForecastTools.java`
- `../src/main/java/com/example/application/StreamingActivityAgent.java`
- `../src/main/java/com/example/application/AgentTeamWorkflow.java`
- `../src/main/java/com/example/application/DynamicAgentTeamWorkflow.java`
- `../src/main/java/com/example/application/SelectorAgent.java`
- `../src/main/java/com/example/application/PlannerAgent.java`
- `../src/main/java/com/example/application/SummarizerAgent.java`
- `../src/main/java/com/example/application/SessionMemoryAlertsConsumer.java`
- `../src/main/java/com/example/application/SessionMemoryByComponentView.java`
- `../src/main/java/com/example/application/SessionMemoryAlertView.java`
- `../src/main/java/com/example/application/SessionMemoryCompactionAgent.java`
- `../src/main/java/com/example/application/SessionMemoryCompactionConsumer.java`
- `../src/main/java/com/example/application/SessionMemoryCompactionAuditConsumer.java`
- `../src/main/java/com/example/application/PromptTemplateHistoryView.java`
- `../src/main/java/com/example/application/ActivityAnswerEvaluatorAgent.java`
- `../src/main/java/com/example/application/CompetitorMentionGuard.java`
- `../src/main/java/com/example/api/ActivityAgentEndpoint.java`
- `../src/main/java/com/example/api/ActivityPromptEndpoint.java`
- `../src/main/java/com/example/api/PromptTemplateHistoryEndpoint.java`
- `../src/main/java/com/example/api/SessionMemoryViewEndpoint.java`
- `../src/main/java/com/example/api/SessionMemoryAlertStreamEndpoint.java`
- `../src/main/java/com/example/api/DynamicAgentTeamWorkflowEndpoint.java`
- `../src/main/resources/application.conf`

Testing examples:
- `../src/test/java/com/example/application/ActivityAgentTest.java`
- `../src/test/java/com/example/application/AgentTeamWorkflowIntegrationTest.java`
- `../src/test/java/com/example/application/DynamicAgentTeamWorkflowIntegrationTest.java`
- `../src/test/java/com/example/application/ActivityAgentEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/ActivityPromptEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/DynamicAgentTeamWorkflowEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/PromptTemplateHistoryViewIntegrationTest.java`
- `../src/test/java/com/example/application/PromptTemplateHistoryEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/SessionMemoryViewEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/SessionMemoryAlertStreamEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/SessionMemoryAlertsConsumerIntegrationTest.java`
- `../src/test/java/com/example/application/SessionMemoryByComponentViewIntegrationTest.java`
- `../src/test/java/com/example/application/SessionMemoryCompactionConsumerIntegrationTest.java`
- `../src/test/java/com/example/application/SessionMemoryCompactionAuditConsumerIntegrationTest.java`

### Workflows
Core workflow examples:
- `../src/main/java/com/example/application/TransferWorkflow.java`
- `../src/main/java/com/example/application/ApprovalWorkflow.java`
- `../src/main/java/com/example/application/ReviewWorkflow.java`
- `../src/main/java/com/example/application/WalletEntity.java`
- `../src/main/java/com/example/api/TransferWorkflowEndpoint.java`
- `../src/main/java/com/example/api/ApprovalWorkflowEndpoint.java`
- `../src/main/java/com/example/domain/TransferState.java`
- `../src/main/java/com/example/domain/ApprovalState.java`
- `../src/main/java/com/example/domain/Wallet.java`

Testing examples:
- `../src/test/java/com/example/application/TransferWorkflowIntegrationTest.java`
- `../src/test/java/com/example/application/TransferWorkflowEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/ApprovalWorkflowIntegrationTest.java`
- `../src/test/java/com/example/application/ApprovalWorkflowEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/WalletEntityTest.java`
- `../src/test/java/com/example/application/ReviewWorkflowTopicConsumerIntegrationTest.java`
- `../src/test/java/com/example/application/ReviewRequestsByStatusViewIntegrationTest.java`

### Timed actions
Core timer examples:
- `../src/main/java/com/example/domain/TicketReservation.java`
- `../src/main/java/com/example/application/TicketReservationEntity.java`
- `../src/main/java/com/example/application/TicketReservationTimedAction.java`
- `../src/main/java/com/example/api/TicketReservationEndpoint.java`
- `../src/main/java/com/example/domain/ReminderJob.java`
- `../src/main/java/com/example/application/ReminderJobEntity.java`
- `../src/main/java/com/example/application/ReminderJobTimedAction.java`
- `../src/main/java/com/example/api/ReminderJobEndpoint.java`
- `../src/main/java/com/example/domain/ApprovalDeadlineState.java`
- `../src/main/java/com/example/application/ApprovalDeadlineWorkflow.java`
- `../src/main/java/com/example/application/ApprovalDeadlineTimedAction.java`
- `../src/main/java/com/example/api/ApprovalDeadlineWorkflowEndpoint.java`

Testing examples:
- `../src/test/java/com/example/application/TicketReservationEntityTest.java`
- `../src/test/java/com/example/application/TicketReservationTimedActionTest.java`
- `../src/test/java/com/example/application/TicketReservationEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/ReminderJobEntityTest.java`
- `../src/test/java/com/example/application/ReminderJobTimedActionTest.java`
- `../src/test/java/com/example/application/ReminderJobEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/ApprovalDeadlineWorkflowIntegrationTest.java`
- `../src/test/java/com/example/application/ApprovalDeadlineTimedActionTest.java`
- `../src/test/java/com/example/application/ApprovalDeadlineWorkflowEndpointIntegrationTest.java`

### Consumers
Core consumer examples:
- `../src/main/java/com/example/application/ShoppingCartCheckoutConsumer.java`
- `../src/main/java/com/example/application/DraftCartCheckoutConsumer.java`
- `../src/main/java/com/example/application/ShoppingCartCommandsTopicConsumer.java`
- `../src/main/java/com/example/application/ShoppingCartEventsToTopicConsumer.java`
- `../src/main/java/com/example/application/ShoppingCartPublicEventsConsumer.java`
- `../src/main/java/com/example/application/ReviewWorkflowTopicConsumer.java`
- `../docs/consumer-reference.md`
- `../docs/service-to-service-consumers.md`

Testing examples:
- `../src/test/java/com/example/application/ShoppingCartCheckoutConsumerIntegrationTest.java`
- `../src/test/java/com/example/application/DraftCartCheckoutConsumerIntegrationTest.java`
- `../src/test/java/com/example/application/ShoppingCartCommandsTopicConsumerIntegrationTest.java`
- `../src/test/java/com/example/application/ReviewWorkflowTopicConsumerIntegrationTest.java`

### Views
Core view examples:
- `../src/main/java/com/example/application/ShoppingCartsByCheckedOutView.java`
- `../src/main/java/com/example/application/ShoppingCartAuditView.java`
- `../src/main/java/com/example/application/DraftCartsByCheckedOutView.java`
- `../src/main/java/com/example/application/DraftCartLifecycleView.java`
- `../src/main/java/com/example/application/ReviewRequestsByStatusView.java`
- `../src/main/java/com/example/application/ShoppingCartTopicView.java`
- `../docs/service-to-service-views.md`

Testing examples:
- `../src/test/java/com/example/application/ShoppingCartsByCheckedOutViewIntegrationTest.java`
- `../src/test/java/com/example/application/ShoppingCartAuditViewIntegrationTest.java`
- `../src/test/java/com/example/application/DraftCartsByCheckedOutViewIntegrationTest.java`
- `../src/test/java/com/example/application/DraftCartLifecycleViewIntegrationTest.java`
- `../src/test/java/com/example/application/ReviewRequestsByStatusViewIntegrationTest.java`
- `../src/test/java/com/example/application/ShoppingCartTopicViewIntegrationTest.java`

### HTTP endpoints
Core endpoint examples:
- `../src/main/java/com/example/api/GreetingEndpoint.java`
- `../src/main/java/com/example/api/StaticContentEndpoint.java`
- `../src/main/java/com/example/api/WebUiHomeEndpoint.java`
- `../src/main/java/com/example/api/WebUiDataEndpoint.java`
- `../src/main/java/com/example/api/WebUiSsePageEndpoint.java`
- `../src/main/java/com/example/api/WebUiWebSocketPageEndpoint.java`
- `../src/main/java/com/example/api/LowLevelHttpEndpoint.java`
- `../src/main/java/com/example/api/ProxyGreetingEndpoint.java`
- `../src/main/java/com/example/api/PingWebSocketEndpoint.java`
- `../src/main/java/com/example/api/CounterStreamEndpoint.java`
- `../src/main/java/com/example/api/DraftCartViewStreamEndpoint.java`
- `../src/main/java/com/example/api/RequestHeadersEndpoint.java`
- `../src/main/java/com/example/api/SecureGreetingEndpoint.java`
- `../src/main/java/com/example/api/InternalStatusEndpoint.java`
- `../src/main/java/com/example/api/ShoppingCartEndpoint.java`
- `../src/main/java/com/example/api/DraftCartEndpoint.java`
- `../src/main/java/com/example/api/OrderEndpoint.java`
- `../src/main/java/com/example/api/PurchaseOrderEndpoint.java`
- `../src/main/java/com/example/api/TransferWorkflowEndpoint.java`
- `../src/main/java/com/example/api/ApprovalWorkflowEndpoint.java`

Testing examples:
- `../src/test/java/com/example/application/GreetingEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/StaticContentEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/WebUiHomeEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/WebUiDataEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/WebUiSsePageEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/WebUiWebSocketPageEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/LowLevelHttpEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/ProxyGreetingEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/PingWebSocketEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/CounterStreamEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/DraftCartViewStreamEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/RequestHeadersEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/SecureGreetingEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/InternalStatusEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/ShoppingCartIntegrationTest.java`
- `../src/test/java/com/example/application/OrderEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/PurchaseOrderEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/TransferWorkflowEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/ApprovalWorkflowEndpointIntegrationTest.java`

### gRPC endpoints
Core endpoint examples:
- `../src/main/proto/com/example/api/grpc/shopping_cart_grpc_endpoint.proto`
- `../src/main/proto/com/example/api/grpc/internal_status_grpc_endpoint.proto`
- `../src/main/proto/com/example/api/grpc/secure_greeting_grpc_endpoint.proto`
- `../src/main/proto/com/example/api/grpc/pattern_secure_greeting_grpc_endpoint.proto`
- `../src/main/java/com/example/api/ShoppingCartGrpcEndpointImpl.java`
- `../src/main/java/com/example/api/InternalStatusGrpcEndpointImpl.java`
- `../src/main/java/com/example/api/SecureGreetingGrpcEndpointImpl.java`
- `../src/main/java/com/example/api/PatternSecureGreetingGrpcEndpointImpl.java`

Testing examples:
- `../src/test/java/com/example/application/ShoppingCartGrpcEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/InternalStatusGrpcEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/SecureGreetingGrpcEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/PatternSecureGreetingGrpcEndpointIntegrationTest.java`

### MCP endpoints
Core endpoint examples:
- `../src/main/java/com/example/api/ShoppingCartMcpEndpoint.java`
- `../src/main/java/com/example/api/SecureSupportMcpEndpoint.java`
- `../src/main/resources/mcp/checkout-guidelines.md`

Testing examples:
- `../src/test/java/com/example/application/ShoppingCartMcpEndpointTest.java`
- `../src/test/java/com/example/application/SecureSupportMcpEndpointTest.java`
