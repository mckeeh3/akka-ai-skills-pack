# Akka Component Skills

This directory contains AI-focused skills for generating and reviewing Akka Java SDK component code.

Current local suites:
- Agents
- Event Sourced Entities
- Key Value Entities
- Workflows
- Timed Actions
- Consumers
- Views
- HTTP Endpoints
- gRPC Endpoints
- MCP Endpoints

If you have requirements but have not yet chosen the entity type, start with:
- `akka-entity-type-selection`

You can also consult the comparison/reference files:
- `references/akka-entity-comparison.md`
- `references/akka-grpc-jwt-patterns.md`
- `../docs/workflow-endpoint-pattern.md`
- `../docs/timer-pattern-selection.md`

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
Use when the agent should call function tools, Akka component tools, or MCP tools.
- `akka-agent-tools`

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

### Static content endpoints
Use when the endpoint serves packaged HTML, CSS, OpenAPI files, or other assets.
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

### First decide between ESE and KVE
Load:
- `akka-entity-type-selection`

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
