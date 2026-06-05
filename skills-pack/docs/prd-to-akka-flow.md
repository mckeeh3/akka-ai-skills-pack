# PRD-to-Akka flow

Use this doc as a concise bridge from broad requirements to an Akka implementation plan. For generated SaaS apps, the starting point is always the secure AI-first SaaS workstream model, not a conventional resource/API/component list.

## Canonical planning sequence

1. **Read the PRD or input artifact completely.**
   Preserve user domain language and identify explicit constraints, roles, outcomes, data boundaries, integrations, and delivery expectations.

2. **Apply the mandatory generated-SaaS foundation.**
   Include WorkOS/AuthKit user auth, local authorization, tenant/customer boundaries, `/api/me`, invitations/email, audit, managed-agent runtime governance, and the workstream UI shell unless the task is explicitly non-SaaS or repository-maintenance-only.

3. **Decide one-workstream vs multi-workstream shape.**
   Large PRDs must explicitly decide whether they describe one workstream, multiple workstreams, or shared foundation/cross-workstream work. Incremental inputs must name affected existing workstream graph nodes, role-specific dashboard attention items, surface edges, internal workstream agent graph delegations/results, governed-tools, expertise bundles, and task/backlog entries instead of replanning from scratch.

4. **Identify workstreams and agents.**
   Define core and domain-specific workstreams, each backed by one functional/context-area agent for authenticated consequential work. Evaluate internal/background agents only where durable delegated work is justified.

5. **Break down attention and role-specific dashboards.**
   For each workstream and actor, answer `what needs my attention?` and describe role-specific dashboard/briefing surfaces, attention sources, system-message surfaces, detail/decision/evidence surfaces, left-rail indicators, and My Account aggregation.

6. **Build the surface graph and internal agent graph.**
   Treat the dashboard as the surface graph trunk. Record surface nodes, surface-request/action edges, edge effects, result surfaces, trace links, realtime/refresh behavior, and the internal workstream agent graph: virtual dashboard agent, worker agents, delegation edges, stop/escalation rules, and result/proposal surfaces.

7. **Map actions to governed capabilities and governed-tools.**
   Every browser action, surface request, prompt suggestion, request-based Agent tool, AutonomousAgent task operation, workflow step, timer, consumer reaction, and API endpoint must map to a capability contract and governed-tool id with AuthContext, scope, schemas, side effects, idempotency, approval/policy, audit/work traces, qualified exposure (`browser-tool`, `agent-tool`, `internal-tool`, workflow/timer/consumer/MCP exposure), and tests.

8. **Plan workstream expertise.**
   For each new or materially changed LLM-backed functional agent, plan prompt intent, skill/reference document families, compact manifests, tool boundaries, authorized loaders, denied-load behavior, seed/import expectations, user-help examples, governance surfaces, and tests.

9. **Select Akka substrate after capability contracts.**
   Choose Entities, Workflows, Views, Consumers, Timed Actions, HTTP/gRPC/MCP endpoints, request-based Agents, and AutonomousAgents based on capability semantics and participant lifecycle.

10. **Produce an implementation handoff.**
   The plan should include workstream decomposition decision, role-dashboard and attention model, human surface graph, internal workstream agent graph, workstream expertise plan, capability/governed-tool inventory, selected components, why each component exists, skill routing, implementation order, required tests, UI/API/realtime exposure, audit/work traces, and local runtime validation.

11. **Materialize durable follow-on work when needed.**
   Use pending questions for blockers and pending tasks for implementation only after vertical workstream/surface/internal-agent/governed-tool/capability contracts are present. Queue entries for incremental changes must state whether they add, modify, reuse, split, deprecate, or verify existing graph nodes, edges, governed-tools, or expertise bundles.

## Preferred generated-SaaS example

Start with:
- `examples/requirements-to-workstream-mini-example.md`

That compact example shows the required chain:

```text
input/PRD
→ one-workstream vs multi-workstream decision
→ workstreams and functional agents
→ attention and role-specific dashboards
→ human surface graph and actions
→ internal workstream agent graph
→ governed capabilities/governed-tools/APIs
→ Akka substrate
→ Agent/AutonomousAgent choices
→ events, projections, traces
→ task shape
```

Use it with:
- `requirements-to-workstream-development-process.md`
- `ai-first-saas-application-architecture.md`
- `capability-first-backend-architecture.md`
- the target project `app-description/README.md` plus `../docs/core-ai-first-saas-foundation.md`

## Focused examples

Use the current examples in `docs/examples/` for generated-SaaS planning mechanics and core foundation inputs. Keep examples domain-neutral or directly tied to current focused pack examples; do not reintroduce historical domain-specific planning examples as generic guidance.

## Prompt pattern for a direct solution plan

```text
Read <requirements-file> and produce an Akka solution plan that starts from:
- mandatory secure AI-first SaaS foundation and five-core starter implications where applicable
- one-workstream vs multi-workstream decision, affected workstreams, functional agents, attention categories, and role-specific dashboard surfaces
- human surface graph nodes/edges, internal workstream agent graph, and governed capabilities/governed-tools with AuthContext, policy, audit/work traces, and tests
- selected Akka substrate only after capability contracts are clear
- request-based Agent vs AutonomousAgent choices where model-backed work is in scope
- implementation order, focused skill routing, and local runtime/API/UI validation
```

## Coding handoff rule

Once accepted, the solution plan is the implementation contract. Downstream backlog and task entries must preserve workstream id, role-dashboard attention category, surface graph node/edge or surface/action, internal workstream agent graph responsibility when applicable, governed-tool id/class, capability id/class, workstream expertise changes, AuthContext/scope, selected substrate, API/frontend/realtime exposure, events/projections, audit/work traces, and validation path. If a task loses that context, repair the plan or queue before coding.
