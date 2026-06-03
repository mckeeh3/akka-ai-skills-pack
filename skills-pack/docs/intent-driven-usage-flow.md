# Intent-driven usage flow

Use this flow when a user provides a PRD, requirements doc, feature request, fix, adjustment, API sketch, UI brief, or other broad product input.

## Default sequence

1. **Read the input completely**
   - Preserve the user's domain terms.
   - Do not make the user name skills, stages, files, or architecture patterns.

2. **Apply the secure AI-first SaaS default**
   - For generated applications, begin from the mandatory secure SaaS foundation unless the user explicitly asks for repository-maintenance-only or non-SaaS reference material.
   - Minimum/basic/starter/chatbot-like generated SaaS requests mean the five core workstream v0 starter: My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy.
   - Use `../docs/minimum-ai-first-saas-app.md`, `../docs/ai-first-saas-application-architecture.md`, and `../docs/agent-workstream-application-architecture.md` for foundation doctrine.

3. **Model the application as workstreams before pages or components**
   - Identify functional/context-area agents, core vs domain-specific workstreams, internal/background agents when justified, attention categories, role-specific dashboard surfaces, and retained human authority.
   - For incremental input, reconcile against the existing workstream graph: affected workstreams, changed dashboards/attention items, surface nodes/edges, internal agent graph delegations, governed-tools, expertise bundles, and pending tasks.
   - Do not start from page trees, CRUD modules, resource APIs, navigation bars, database tables, or Akka component lists.
   - Use `../docs/requirements-to-workstream-development-process.md` for the canonical process.

4. **Define the human surface graph and internal agent graph**
   - Treat role-specific dashboards as graph trunks that answer what requires attention for each actor.
   - Capture surface nodes, system-message surfaces, detail/decision/evidence surfaces, surface-request/action edges, edge effects, states, trace links, accessibility expectations, and realtime/refresh behavior.
   - Capture the internal workstream agent graph where relevant: virtual dashboard-agent view, worker agents, delegation edges, stop/escalation rules, result/proposal surfaces, and human attention items.
   - Map every protected button, link, prompt suggestion, browser action, agent-tool, workflow step, timer, or consumer reaction to a governed backend capability and governed-tool.

5. **Model governed backend capabilities and governed-tools**
   - Record capability id/class, callers, AuthContext, tenant/customer scope, schemas, validation, idempotency, approval/policy, side effects, audit/work traces, exposure surfaces, and tests.
   - Use `../docs/capability-first-backend-architecture.md` before selecting Akka components.

6. **Choose the planning path**
   - Use `../skills/app-descriptions/SKILL.md` when the user is maintaining or reviewing an authoritative app description before realization.
   - Use `../skills/akka-solution-decomposition/SKILL.md` when the user wants direct Akka solution shaping and the component set is not yet known.
   - Use `../skills/akka-prd-to-specs-backlog/SKILL.md` when the user wants durable `specs/`, backlog, and pending-task artifacts.

7. **Queue unresolved decisions when needed**
   - Use `../skills/akka-pending-question-generation/SKILL.md` when missing security, workstream, surface, capability, approval, audit, runtime, or delivery decisions would force guessing.
   - Use `../skills/akka-do-next-pending-question/SKILL.md` and `../skills/akka-pending-question-queue-maintenance/SKILL.md` for one-at-a-time question handling and queue repair.

8. **Generate implementation tasks only after vertical contracts exist**
   - Backlogs and pending tasks must preserve workstream, attention/surface/action, capability id/class, AuthContext/scope, selected substrate, API/frontend/realtime exposure, audit/work traces, and local validation path.
   - Execute one implementation task per fresh context with `../skills/akka-do-next-pending-task/SKILL.md`.

9. **Load focused implementation skills last**
   - Use `../skills/README.md` to select only the Stage 3 skills needed for the accepted capability-aware plan.
   - Implement component by component, but keep the workstream/surface/capability contract as the source of meaning.

10. **Validate through the intended local runtime path**
    - Named generated-app features are complete only when the intended local runtime/API/UI path works at the stated scope.
    - Do not count deterministic/demo/mock/model-less normal runtime behavior as completion for auth, agents, tools, capabilities, traces, provider calls, or protected UI actions.

## Quick example references

Preferred generated-SaaS planning example:
- `examples/requirements-to-workstream-mini-example.md`

Conventional planning mechanics only:
- `prd-to-akka-flow.md`
- purchase-request examples linked from that doc

Purchase-request examples are not the canonical generated AI-first SaaS target architecture. Use them only for narrow mechanics such as solution-plan or queue shape after the secure AI-first SaaS workstream model is clear.

## Rule of thumb

Broad input becomes implementation-ready only after this chain is explicit:

```text
secure SaaS foundation
→ affected workstreams and functional agents
→ role-specific dashboards and attention categories
→ human surface graph and actions
→ internal workstream agent graph when delegated work exists
→ governed capabilities and governed-tools
→ qualified exposure channels (browser-tool, agent-tool, internal-tool, workflow/timer/consumer/MCP)
→ Akka substrate
→ tests and local runtime/API/UI validation
```

If any link is missing, ask a bounded question, update the app description/specs, or repair the backlog before coding.
