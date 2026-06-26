# Worker: Agent Admin Functional Agent Worker

## Responsibility

User-facing functional-agent worker bound to `agent-admin-agent`. It helps SaaS admins navigate Agent Admin, understand safe profile summaries, prepare edit requests, interpret proposals, and route review/activation surfaces.

## Execution harness and adapters

- Workstream functional-agent shell.
- Deterministic no-mutation surface intent routing for open/list/read/preflight requests.
- `human_chat_tool_plan` proposal path for explicit, catalog-bound human-confirmed actions.
- `agent_tool_call` exposure only for read/proposal tools allowed by the active `ToolPermissionBoundary`.

## Authority

Uses the same governed tool ids as browser surfaces but only through adapter-specific grants. It cannot activate behavior, grant authority, or perform side effects without backend authorization and required human review/confirmation.

## Traces

Must emit workstream log traces, agent work traces, denied-tool traces, and links to proposal/review traces.
