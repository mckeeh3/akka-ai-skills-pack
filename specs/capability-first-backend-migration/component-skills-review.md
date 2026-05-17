# Component Skill Review

## Scope

Task: `TASK-05-003`

Reviewed focused Stage 3 component skill routing for stale CRUD-first, entity-first, endpoint-first, tool-first, and broad product-to-implementation language after the capability-first backend migration:

- `skills/akka-agents/SKILL.md`
- `skills/akka-event-sourced-entities/SKILL.md`
- `skills/akka-key-value-entities/SKILL.md`
- `skills/akka-workflows/SKILL.md`
- `skills/akka-views/SKILL.md`
- `skills/akka-consumers/SKILL.md`
- `skills/akka-timed-actions/SKILL.md`
- `skills/akka-http-endpoints/SKILL.md`
- `skills/akka-grpc-endpoints/SKILL.md`
- `skills/akka-mcp-endpoints/SKILL.md`
- `skills/akka-web-ui-apps/SKILL.md`
- related focused companion skills surfaced by repository search

## Findings

### Resolved in this task

1. Several top-level Stage 3 skills opened with unqualified "Use this as the top-level skill" language. Their later sections were already capability-aware, but the opening could still be read by a future harness as permission to route broad product input directly to component implementation.
   - Fix: qualified the opening routing sentence in agent, workflow, view, consumer, timer, HTTP, gRPC, MCP, and web UI top-level skills.
   - Each updated opening now says the component skill is for implementation/review only after the relevant capability contract, exposure surface, or component role is already selected.
   - Each updated opening now routes broad product/PRD/feature requests back through `capability-first-backend` and `akka-solution-decomposition` before coding.

2. Endpoint and remote exposure skills had strong capability-first sections, but their first routing sentence did not explicitly prevent endpoint-path, RPC-method, or MCP-tool-list design from becoming the starting point.
   - Fix: `akka-http-endpoints`, `akka-grpc-endpoints`, and `akka-mcp-endpoints` now explicitly say HTTP/gRPC/MCP are selected exposure surfaces and should not be the root abstraction when authority, scope, side effects, approval, or audit semantics are unclear.

3. Read, async, scheduled, and UI component families had capability-aware body text but lacked first-paragraph broad-input guards.
   - Fix: `akka-views`, `akka-consumers`, `akka-timed-actions`, and `akka-web-ui-apps` now explicitly reject raw projections, event glue, background jobs, or CRUD navigation as the starting point for broad product work.

4. `skills/akka-workflows/SKILL.md` had one malformed example-test link in the required-reading list.
   - Fix: restored the missing closing backtick.

### No cleanup required in this task

- Event Sourced Entity and Key Value Entity top-level skills already contained explicit capability-contract guards and broad-product routing back to `capability-first-backend` and `akka-solution-decomposition`.
- Focused entity edge/flow skills already choose interaction patterns from capability contracts rather than CRUD convenience.
- Agent tool and component-tool skills already treat tools as selected capability exposure surfaces and reject arbitrary component-internal tool exposure.
- View, endpoint, MCP, workflow, consumer, and timer companion skills found by search already preserve AuthContext/scope, idempotency, approval, audit/trace, denial/no-op, redaction, or exposure-boundary semantics where relevant; no broad rewrite was needed.

## Checks performed

- Searched `skills/**/SKILL.md` for `CRUD`, `component implementation`, broad-product, entity-first, endpoint-first, tool-first, direct-routing, and agent-tool language.
- Verified top-level Stage 3 skills no longer route broad product input directly to component implementation in their opening routing text.
- Verified updated endpoint/MCP language treats HTTP, gRPC, and MCP as selected capability exposure surfaces rather than root backend design objects.
- Verified entity top-level skills already retained capability-contract prerequisites and broad-product routing guards.
- Ran `git diff --check` after edits.

## Residual work

No new residual component-skill cleanup tasks are required from this review. Remaining Sprint 5 work should continue with the queued examples/tests review for unsafe tool patterns, raw state leakage, auth bypass, or unbounded authority.
