# Example and test safety review

Task: `TASK-05-004`

Scope reviewed:
- `src/main/java/com/example/application/*Agent*.java`
- `src/main/java/com/example/application/*Tools*.java`
- `src/main/java/com/example/application/*Entity.java` component-tool examples
- `src/main/java/com/example/api/*McpEndpoint.java`
- agent/MCP/tool tests under `src/test/java/com/example/application/`
- app-description examples under `docs/examples/`

Review focus:
- examples should not normalize authorization bypass;
- examples should not expose raw entity state, event history, JWTs, headers, secrets, or unredacted tenant data to agents;
- examples should not grant unbounded tool or MCP authority;
- tests should reinforce deterministic, bounded tool invocation rather than prompt-only security.

## Findings

### Safe existing patterns

- `CartInspectorAgent`, `ShoppingCartEntity.inspectCartSummary`, `RemoteShoppingCartAgent`, and `ShoppingCartToolsMcpEndpoint` already frame cart tool access as a selected read-only evidence capability with curated summary output.
- `RemoteShoppingCartAgent` uses `withAllowedToolNames("getCartSummary")`, avoiding broad remote MCP tool authority.
- `RefundApprovalAgent`, `RefundProposalTools`, `RefundApprovalWorkflow`, and `RefundApprovalCapabilityTest` demonstrate the desired consequential-capability pattern: the agent can draft a proposal, but refund side effects are committed only by workflow approval or by an explicitly bounded autonomous policy grant.
- The secure SaaS and DCA app-description examples consistently require tenant/customer scope, backend authorization, audit traces, scoped evidence, and human approval for high-impact agent actions.
- Agent tests use deterministic `TestModelProvider`/tool invocation patterns rather than real model calls.

### Cleanup completed

- Tightened `ShoppingCartMcpEndpoint` so the older broad MCP example no longer presents an `ALL` ACL or generic "inspect cart state" language. It now names the selected read-only capabilities, uses a service ACL, and says production SaaS variants must enforce AuthContext, tenant/customer scope, and data-access audit before loading protected carts.
- Tightened `SecureSupportMcpEndpoint` wording so request-context access is described as redacted caller-context routing, not raw bearer-token/header exposure, and so JWT validation is not confused with tenant authorization.
- Updated `SecureSupportMcpEndpointTest` to assert the safe shape: header count may be returned, but individual request-header values are not exposed.

## Residual notes

- Some examples remain intentionally small reference examples rather than complete secure SaaS implementations. Where retained, comments now need to make the boundary explicit instead of implying prompt text, MCP instructions, or frontend routing is authorization.
- No follow-up task is required from this review; the remaining full-pack stale-content sweep is covered by `TASK-05-005` and Sprint 6 review tasks.
