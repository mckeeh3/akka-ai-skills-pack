# `src/main/java` inventory by skills-pack family

Status: current inventory after removing the legacy PoC, the unused `BootstrapAdmin` record, and the DCA/supplies domain-specific vertical slice.

## Keep as canonical or directly useful executable fixtures

| Skill family | Main files / packages | Why kept |
| --- | --- | --- |
| Event sourced entities | `ShoppingCart*`, `Order*`, `ExpiringShoppingCart*` | ESE domain modeling, application entity, edge/internal flow, TTL, replication, notifications, endpoint/view/consumer fixtures. |
| Key value entities | `DraftCart*`, `PurchaseOrder*`, `ExpiringDraftCartSession*` | KVE domain modeling, application entity, edge/internal flow, TTL, replication, notifications, ESE/KVE comparison. |
| Workflows | `TransferWorkflow`, `ApprovalWorkflow`, `ReviewWorkflow`, `WalletEntity`, related state/endpoints | Workflow component, compensation, pause/resume, notifications, workflow views/consumers. |
| Timed actions / timers | `TicketReservation*`, `ReminderJob*`, `ApprovalDeadline*` | Timer scheduling, timed action handlers, obsolete timer/idempotency behavior, timer-backed tests. |
| Consumers | `ShoppingCartCheckoutConsumer`, `DraftCartCheckoutConsumer`, `ShoppingCartCommandsTopicConsumer`, `ShoppingCartEventsToTopicConsumer`, `ShoppingCartPublicEventsConsumer`, `ReviewWorkflowTopicConsumer` | Entity, workflow, topic, and producing-consumer patterns. |
| Views | `ShoppingCartsByCheckedOutView`, `ShoppingCartAuditView`, `DraftCartsByCheckedOutView`, `DraftCartLifecycleView`, `ReviewRequestsByStatusView`, `ShoppingCartTopicView` | View source selection, query patterns, streaming/projection test fixtures. |
| HTTP endpoints | `GreetingEndpoint`, cart/order endpoints, `LowLevelHttpEndpoint`, `ProxyGreetingEndpoint`, request-context/JWT/internal ACL endpoints, SSE/WebSocket/web UI endpoints | HTTP component client, low-level HTTP, request context, JWT, ACL, SSE, WebSocket, web UI hosting. |
| gRPC endpoints | `*GrpcEndpointImpl` | Protobuf mapping, component client, request context, JWT and pattern JWT validation. |
| MCP endpoints | `ShoppingCartMcpEndpoint`, `ShoppingCartToolsMcpEndpoint`, `SecureSupportMcpEndpoint` | MCP tools/resources/prompts, component client, request context, secure support endpoint tests. |
| Basic agents | `ActivityAgent`, `TemplateBackedActivityAgent`, `ConfiguredModelActivityAgent`, `StreamingActivityAgent`, `WeatherAgent`, `WeatherForecastTools` | Agent component, prompt template, model config, structured/streaming responses, function tools. |
| Agent orchestration/team | `AgentTeamWorkflow`, `DynamicAgentTeamWorkflow`, `SelectorAgent`, `PlannerAgent`, `SummarizerAgent`, `ActivityWorkerAgent`, `ActivityAnswerEvaluatorAgent`, related domain state | Workflow-backed agent orchestration, dynamic agent team selection, evaluator patterns. |
| Agent runtime state/memory | `SessionMemory*`, `PromptTemplateHistoryView`, `ActivityPromptEndpoint`, `PromptTemplateHistoryEndpoint`, `WorkerMemorySummaryAgent` | Built-in prompt template/session memory state, compaction, alerts, views, endpoints, streams. |
| Agent safety/tools/multimodal | `CompetitorMentionGuard`, `DocumentAnalysisAgent`, `CartInspectorAgent`, `RemoteShoppingCartAgent`, `RefundApproval*` | Guardrails, multimodal, component tools, remote MCP tools, decision/proposal workflow. |
| Governed runtime agent foundation | `application/agentfoundation/**`, `domain/agentfoundation/**`, `ManagedReferenceAgentEndpoint` | Durable agent definitions, governed prompt/skill docs, manifests, tool boundaries, prompt assembly, skill authorization, behavior editing, work traces, closed-loop improvement. |
| Core SaaS security/admin reference | `api/security/**`, `application/security/**`, `domain/security/**`, `security/**` | WorkOS-backed `/api/me`, local authorization, user/admin/tenant/customer entities, admin audit, invite email seam. This remains a richer executable reference alongside the generated starter template. |
| Capability-first supervised export | `SupervisedExportWorkflow`, `SupervisedExportEvidenceView`, `SupervisedExportState` | High-risk governed capability, approval pause, audit/evidence view, idempotency. |
| Web UI reference pair | `FrontendReferenceApiEndpoint`, `FrontendReferenceUiEndpoint` | Tested lightweight frontend/API fixture for web UI skills; keep unless replaced by a clearer canonical web UI example. |
| Package docs | `package-info.java` files | Low-cost package orientation for source examples. |

## Removed as not directly useful

| Removed path | Reason |
| --- | --- |
| `examples/poc-user-auth-onboarding/**` | Legacy portable PoC superseded by `templates/ai-first-saas-starter/**` and current root security/admin reference code. |
| `src/main/java/com/example/domain/security/BootstrapAdmin.java` | Unused record; active bootstrap code uses `AdminUserBootstrap.BootstrapAdminSpec`. |
| `docs/examples/ai-first-dca-app-description/**` | Domain-specific app-description vertical superseded by the canonical seed app-description and starter template guidance. |
| `src/main/java/com/example/api/supplies/**`, `src/main/java/com/example/application/supplies/**`, `src/main/java/com/example/domain/supplies/**` | Domain-specific supplies/DCA vertical no longer provides unique reusable skills-pack value. |
| `src/test/java/com/example/application/supplies/**`, `src/test/java/com/example/domain/supplies/**` | Tests covered removed DCA/supplies vertical only. |
| `src/main/resources/static-resources/supplies/**` | Static domain-specific supplies UI superseded by workstream UI reference and starter guidance. |
| `src/main/java/com/example/api/security/DcaSeedFrontendEndpoint.java` and matching DCA seed tests | DCA-named frontend/security bridge superseded by starter/workstream frontend references. |

## Cleanup candidates that require separate decisions

These are useful today, but should be reviewed in focused future sessions rather than deleted opportunistically.

1. **Core security/admin reference ownership**
   - Scope: `api/security/**`, `application/security/**`, `domain/security/**`, `security/**`.
   - Decision: keep as richer executable reference, move to a named example slice, or supersede with starter-template coverage.

2. **Frontend reference pair**
   - Scope: `FrontendReferenceApiEndpoint`, `FrontendReferenceUiEndpoint`, matching static resources and integration test.
   - Decision: document as canonical lightweight web UI reference or remove after replacement.

3. **Test phase cleanup**
   - Scope: `src/test/java` integration/runtime tests.
   - Decision: introduce Maven profiles/tags or documented slice commands; do not silently drop coverage.
