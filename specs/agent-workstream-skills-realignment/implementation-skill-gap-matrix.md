# Implementation Skill Gap Matrix

## Scope

Audit target: focused implementation skills that can be loaded after planning for generated full-stack AI-first SaaS work.

Standard input contract from Sprint 03:

```text
functional agent:
internal agent, if any:
workstream:
surface id/type/version:
surface action or workstream event:
capability id/class:
AuthContext and roles/capabilities:
input/output DTOs and redaction:
side effects and idempotency:
policy/approval/escalation:
audit/work traces:
selected Akka substrate:
frontend/API/realtime exposure:
required tests:
```

## Audit method

- Read Sprint 02 review, Sprint 03 plan, and `docs/agent-workstream-design-review-checklist.md`.
- Reviewed top-level implementation skills for web UI, agents, HTTP/gRPC/MCP endpoints, entities, workflows, views, consumers, and timed actions.
- Searched all focused companion skills for presence of workstream/surface/capability/AuthContext/trace contract language and for existing `Capability-first ... rule` sections.
- Classified gaps by whether a skill can plausibly be used from component/page/tool mechanics without preserving the standard input contract.

## Summary

| Area | Current alignment | Main drift | Priority | Follow-up task |
|---|---|---|---|---|
| Web UI | Top-level `akka-web-ui-apps`, UX, frontend-project, and state-rendering are mostly aligned. | Focused implementation skills for API client, forms, realtime, accessibility, and testing can still operate on UI mechanics without requiring surface/action/capability/AuthContext/trace inputs. | High | `TASK-AWSR-03-002` |
| Agents | Top-level `akka-agents` and several governance/tool skills distinguish functional vs internal agents and capability-backed tools. | Focused model mechanics skills can still start from prompt/memory/streaming/guardrail/evaluation mechanics without checking functional/internal placement, authority, tool/data boundaries, or trace obligations. | High | `TASK-AWSR-03-002` |
| HTTP endpoints | Top-level and most focused HTTP endpoint skills have capability-first exposure rules. | Component-client and testing skills should be strengthened to require UI/surface action or agent/tool provenance plus shared denial/audit/trace tests; current review checklists are narrower than the standard contract. | Medium | `TASK-AWSR-03-003` |
| gRPC endpoints | Top-level and focused gRPC endpoint skills generally have capability-first exposure rules. | `akka-grpc-proto-design` lacks the standard contract and can design RPCs without authority, tenant/customer, trace, redaction, and surface parity. | Medium | `TASK-AWSR-03-003` |
| MCP endpoints | Top-level and focused MCP endpoint skills generally treat tools/resources/prompts as capability exposure surfaces. | Need small consistency pass so resources/prompts/testing preserve same AuthContext, tool permission, trace, and redaction contract as UI/API/agent-tool surfaces. | Medium | `TASK-AWSR-03-003` |
| Entities | Top-level ESE/KVE and domain/application/edge-flow skills are mostly capability-aware. | TTL, notifications, replication, unit/integration testing, and doc snippet skills can be used as feature mechanics without carrying capability id, scope, idempotency, audit/trace, or exposure-surface context. | High | `TASK-AWSR-03-003` |
| Workflows | Top-level workflow and component/pausing/testing skills are mostly aligned. | Compensation and notifications need the standard contract for approval/escalation, surface updates, idempotent side effects, and trace obligations. | Medium | `TASK-AWSR-03-003` |
| Views | Top-level views and query/stream/testing skills are capability-aware. | Source-specific view skills can still create projections from source mechanics without requiring read/evidence capability, AuthContext filters, structured-surface/API/tool consumers, redaction, and data-access trace tests. | High | `TASK-AWSR-03-003` |
| Consumers | Top-level consumers skill is well aligned. | All source-specific consumer and testing skills need the standard contract so event reactions preserve provenance, system principal, tenant/customer scope, idempotency, side effects, audit/work traces, and exposure outcomes. | High | `TASK-AWSR-03-003` |
| Timed actions | Top-level timed-actions skill is aligned. | Timed-action component, timer scheduling, and testing skills can start from scheduler mechanics without requiring scheduler authority, tenant/customer scope, policy/approval reference, idempotency, no-op/denial behavior, and audit/work trace tests. | High | `TASK-AWSR-03-003` |
| Testing skills | Several top-level/focused testing skills mention some auth or trace cases. | Testing guidance is inconsistent: many focused tests do not require forbidden/cross-tenant, idempotency/no-op, audit/trace, surface rendering/API/realtime, or agent tool denial checks derived from the standard contract. | High | `TASK-AWSR-03-003` |

## Standard remediation pattern

For each affected implementation skill, add a compact section near the top:

```md
## Generated SaaS input contract

For generated full-stack AI-first SaaS work, implement only after the selected task, app-description, spec, or backlog supplies or explicitly defers:
- functional agent or explicit internal-only/foundation scope;
- workstream, structured surface id/type/version, and surface action or workstream event when user-facing;
- capability id/class, selected Akka substrate, and exposure surfaces;
- AuthContext, tenant/customer scope, roles/capabilities, and backend authorization boundary;
- input/output DTOs, redaction, side effects, idempotency, policy/approval/escalation, audit/work traces, and required tests.

If these are absent and the work is generated SaaS implementation, route back to `agent-workstream-apps` + `capability-first-backend` or block for task-brief repair instead of guessing.
```

Then tailor the skill-specific checklist so it consumes the relevant fields instead of restating the entire architecture.

## Detailed gaps

### Web UI skills

| Skill(s) | Gap | Needed update | Priority |
|---|---|---|---|
| `akka-web-ui-api-client` | Can define fetch wrappers from endpoint DTOs without mapping each browser action to a structured surface action and governed capability. | Require surface id/action, capability id, AuthContext propagation, denial/error DTOs, redaction, idempotency/correlation ids, and trace links in API client contracts. | High |
| `akka-web-ui-forms-validation` | Form mechanics can be implemented without surface state/action contract or capability-backed validation semantics. | Require form surface id/version, action id, input DTO, server validation/denial shape, idempotency, approval-needed state, trace/correlation fields, and rendering tests. | High |
| `akka-web-ui-realtime` | Realtime handling is focused on SSE/WebSocket lifecycle but does not require surface/workstream event semantics. | Require workstream event types, structured surface update semantics, AuthContext/tenant scope, reconnect/stale states, trace ids, and forbidden-stream behavior. | High |
| `akka-web-ui-accessibility-responsive` | Accessibility/responsive guidance can be applied to generic regions without functional-agent shell semantics. | Anchor checks to functional-agent rail, workstream panel, composer, structured surfaces/actions, decision/approval controls, and authority indicators. | Medium |
| `akka-web-ui-testing` | Testing can stop at route/assets/API smoke checks. | Require rendering/API/realtime tests derived from surface contracts plus `/api/me`, forbidden, tenant-isolation, disabled action, stale/reconnect, and trace-link assertions. | High |

### Agent skills

| Skill(s) | Gap | Needed update | Priority |
|---|---|---|---|
| `akka-agent-component`, `akka-agent-structured-responses`, `akka-agent-memory`, `akka-agent-streaming`, `akka-agent-multimodal`, `akka-agent-guardrails`, `akka-agent-evaluation` | Mechanics can start from one model interaction without checking functional/context-area vs internal-agent placement, authority, capability surface, or trace obligations. | Add generated SaaS input contract gate; require placement, caller AuthContext, allowed data/tools, policy/approval boundaries, trace fields, and tests before coding. | High |
| `akka-agent-tools`, `akka-agent-component-tools`, `akka-agent-mcp-tools`, `akka-agent-tool-boundaries` | Mostly capability-aware, but tool guidance should consistently tie every tool to the shared surface/capability contract and denial/audit tests. | Add explicit cross-surface parity check for UI/API/MCP/tool exposure, idempotency, approval-required side effects, and ToolPermissionBoundary trace outcomes. | Medium |
| `akka-agent-prompt-governance`, `akka-agent-skill-governance`, `akka-agent-governed-documents`, `akka-agent-behavior-editing`, `akka-agent-work-trace`, `akka-agent-closed-loop-improvement` | Governance concepts are strong but not all reference functional-agent workstream ownership or structured surfaces where user-facing. | Require admin/governance surfaces, decision cards, capability ids, AuthContext, trace visibility/redaction, and tests for protected behavior-document changes. | Medium |
| `akka-agent-testing` | Strong managed-agent checks, but lacks a single standard test contract that connects functional/internal placement, surface actions, tools, denials, traces, and approval gates. | Add test matrix driven by the standard input contract. | High |

### Endpoint skills

| Skill(s) | Gap | Needed update | Priority |
|---|---|---|---|
| HTTP endpoint family | Existing capability-first exposure rule is good. | Add short generated SaaS input contract to top-level or focused skills where missing details include source functional agent/surface action, `/api/me` selected context, shared denial DTOs, and trace ids. | Medium |
| `akka-grpc-proto-design` | Can design protobuf contracts as API mechanics without capability/AuthContext/redaction/audit semantics. | Require capability id/class, actor/service principal, tenant/customer scope fields, redaction, status-denial shape, idempotency/correlation ids, trace/outcome fields, and stream resume semantics where applicable. | High |
| MCP endpoint family | Existing capability-first exposure rule is good. | Ensure resources/prompts/testing preserve caller-filtered resource visibility, redaction, tool permission boundaries, and audit/work-trace outcomes. | Medium |

### Component skills

| Skill(s) | Gap | Needed update | Priority |
|---|---|---|---|
| ESE/KVE TTL, notifications, replication | Feature skills can be applied as component mechanics. | Require capability reason, scoped state keys, exposure surfaces, stale/expired/replicated denial semantics, trace/audit obligations, and tests. | High |
| ESE/KVE unit/integration testing | Testing skills are mechanics-focused. | Require tests for capability success, validation, no-op/idempotency, forbidden/cross-tenant, audit/trace, and endpoint/tool/surface parity when exposed. | High |
| Workflow compensation/notifications | Companion skills do not fully consume approval/escalation/surface/trace contract. | Require capability id, approval or policy basis, compensation authority, idempotent downstream side effects, notification surface event schema, and audit/work-trace tests. | Medium |
| View source-specific skills | Source mechanics dominate. | Require read/evidence capability, AuthContext filters, tenant/customer scoped row fields, redaction, selected UI/API/tool consumer, data-access trace, and forbidden query tests. | High |
| Consumer source-specific/producing/testing skills | Source mechanics dominate. | Require reactive capability id, event provenance, system principal/authorization or approval basis, tenant/customer scope, idempotency key, side effects, denial/retry behavior, and audit/work trace tests. | High |
| Timed-action component/scheduling/testing | Scheduler mechanics dominate. | Require scheduled capability id, scheduler authority, target scope, system principal, idempotency/no-op strategy, policy/approval reference, retry budget, and audit/trace tests. | High |

## Prioritized follow-up updates

### For `TASK-AWSR-03-002`

1. Add the generated SaaS input contract gate to focused web UI skills that currently lack it:
   - `skills/akka-web-ui-api-client/SKILL.md`
   - `skills/akka-web-ui-forms-validation/SKILL.md`
   - `skills/akka-web-ui-realtime/SKILL.md`
   - `skills/akka-web-ui-accessibility-responsive/SKILL.md`
   - `skills/akka-web-ui-testing/SKILL.md`
2. Add or normalize the same gate in focused agent mechanics skills:
   - `skills/akka-agent-component/SKILL.md`
   - `skills/akka-agent-structured-responses/SKILL.md`
   - `skills/akka-agent-memory/SKILL.md`
   - `skills/akka-agent-streaming/SKILL.md`
   - `skills/akka-agent-multimodal/SKILL.md`
   - `skills/akka-agent-guardrails/SKILL.md`
   - `skills/akka-agent-evaluation/SKILL.md`
   - `skills/akka-agent-testing/SKILL.md`
3. Add lighter cross-surface parity/checklist updates to tool and governance agent skills when touched:
   - `skills/akka-agent-tools/SKILL.md`
   - `skills/akka-agent-component-tools/SKILL.md`
   - `skills/akka-agent-mcp-tools/SKILL.md`
   - `skills/akka-agent-tool-boundaries/SKILL.md`
   - governance skills that expose admin surfaces or decision cards.

### For `TASK-AWSR-03-003`

1. Add the generated SaaS input contract gate to endpoint gaps:
   - `skills/akka-grpc-proto-design/SKILL.md`
   - any HTTP/gRPC/MCP focused skill whose checklist lacks source surface/action, denial DTO/status, trace, and tenant/customer tests.
2. Add component companion gates to:
   - ESE/KVE TTL, notifications, replication, unit testing, integration testing.
   - Workflow compensation and notifications.
   - View source-specific skills.
   - Consumer source-specific, producing, and testing skills.
   - Timed-action component, timer scheduling, and timed-action testing skills.
3. Normalize testing skills so each component family asks for the standard generated SaaS test set where applicable:
   - success;
   - validation;
   - forbidden/disabled-user/role-scope denial;
   - tenant/customer isolation;
   - idempotency/no-op/retry safety;
   - audit/work-trace emission;
   - surface/API/tool/realtime behavior when exposed.

## Non-gaps / keep as is

- Top-level routing skills now generally force generated SaaS work through functional agents, structured surfaces, governed capabilities, and then Akka components.
- Top-level `akka-web-ui-apps`, `akka-agents`, `akka-http-endpoints`, `akka-grpc-endpoints`, `akka-mcp-endpoints`, `akka-event-sourced-entities`, `akka-key-value-entities`, `akka-workflows`, `akka-views`, `akka-consumers`, and `akka-timed-actions` already provide enough high-level alignment to serve as family entry points.
- Focused doc-snippet skills are lower priority unless they are used to generate canonical generated-SaaS implementation examples; keep them mechanics-focused but avoid presenting legacy/page-first examples as canonical SaaS architecture.
