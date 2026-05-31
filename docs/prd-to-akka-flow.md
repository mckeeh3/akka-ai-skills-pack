# PRD-to-Akka flow

Use this doc as a concise bridge from broad requirements to an Akka implementation plan. For generated SaaS apps, the starting point is always the secure AI-first SaaS workstream model, not a conventional resource/API/component list.

## Canonical planning sequence

1. **Read the PRD or input artifact completely.**
   Preserve user domain language and identify explicit constraints, roles, outcomes, data boundaries, integrations, and delivery expectations.

2. **Apply the mandatory generated-SaaS foundation.**
   Include WorkOS/AuthKit user auth, local authorization, tenant/customer boundaries, `/api/me`, invitations/email, audit, managed-agent runtime governance, and the workstream UI shell unless the task is explicitly non-SaaS or repository-maintenance-only.

3. **Identify workstreams and agents.**
   Define core and domain-specific workstreams, each backed by one functional/context-area agent for authenticated consequential work. Evaluate internal/background agents only where durable delegated work is justified.

4. **Break down attention and default surfaces.**
   For each workstream, answer `what needs my attention?` and describe dashboard/briefing surfaces, system-message surfaces, detail/decision/evidence surfaces, left-rail indicators, and My Account aggregation.

5. **Map actions to governed capabilities.**
   Every browser action, surface request, prompt suggestion, request-based Agent tool, AutonomousAgent task operation, workflow step, timer, consumer reaction, and API endpoint must map to a capability contract with AuthContext, scope, schemas, side effects, idempotency, approval/policy, audit/work traces, and tests.

6. **Select Akka substrate after capability contracts.**
   Choose Entities, Workflows, Views, Consumers, Timed Actions, HTTP/gRPC/MCP endpoints, request-based Agents, and AutonomousAgents based on capability semantics and participant lifecycle.

7. **Produce an implementation handoff.**
   The plan should include capability summary, selected components, why each component exists, skill routing, implementation order, required tests, UI/API/realtime exposure, audit/work traces, and local runtime validation.

8. **Materialize durable follow-on work when needed.**
   Use pending questions for blockers and pending tasks for implementation only after vertical workstream/surface/capability contracts are present.

## Preferred generated-SaaS example

Start with:
- `examples/requirements-to-workstream-mini-example.md`

That compact example shows the required chain:

```text
input/PRD
→ workstreams and functional agents
→ attention and dashboards
→ surfaces/actions
→ governed capabilities/APIs
→ Akka substrate
→ Agent/AutonomousAgent choices
→ events, projections, traces
→ task shape
```

Use it with:
- `requirements-to-workstream-development-process.md`
- `ai-first-saas-application-architecture.md`
- `capability-first-backend-architecture.md`
- `examples/ai-first-saas-seed-app-description/README.md`

## Conventional mechanics references

These files remain useful only for conventional planning mechanics such as comparing an input artifact to a solution plan, seeing queue formatting, or reviewing module/sprint file shape:

- `examples/purchase-request-prd.md`
- `examples/purchase-request-solution-plan.md`
- `examples/purchase-request-pending-tasks.md`
- `examples/purchase-request-module-sprint-plan.md`

They are **not** the canonical generated AI-first SaaS target architecture. Do not copy their page/resource/CRUD decomposition as the primary model for a generated app. If you use them, first apply the secure SaaS foundation, workstream/surface/capability chain, and runtime completion standard above.

## Prompt pattern for a direct solution plan

```text
Read <requirements-file> and produce an Akka solution plan that starts from:
- mandatory secure AI-first SaaS foundation and five-core starter implications where applicable
- workstreams, functional agents, attention categories, and default surfaces
- structured surfaces/actions and governed capabilities with AuthContext, policy, audit/work traces, and tests
- selected Akka substrate only after capability contracts are clear
- request-based Agent vs AutonomousAgent choices where model-backed work is in scope
- implementation order, focused skill routing, and local runtime/API/UI validation
```

## Coding handoff rule

Once accepted, the solution plan is the implementation contract. Downstream backlog and task entries must preserve workstream id, attention category or surface/action, capability id/class, AuthContext/scope, selected substrate, API/frontend/realtime exposure, events/projections, audit/work traces, and validation path. If a task loses that context, repair the plan or queue before coding.
