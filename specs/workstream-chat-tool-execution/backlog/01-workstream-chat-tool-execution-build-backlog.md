# Workstream Chat Tool Execution Build Backlog

## Backlog goal

Implement confirmed human-chat tool execution for the SaaS Foundation App workstreams using one shared substrate and per-workstream slices.

## Implementation order

### WCTE-01: Audit and implementation design

- Inventory current WorkstreamService message/action paths, surface intent routing, frontend composer/surface contracts, managed-agent runtime invocation, tool boundary/trace support, and app-description surface catalogs.
- Select first-pass representative chat-executable tools for all five foundation workstreams.
- Produce `specs/workstream-chat-tool-execution/source-and-design-map.md`.

### WCTE-02: App-description current-intent updates

- Update core-starter workstream app-description artifacts to include `human_chat_tool_plan` as an actor adapter/exposure channel.
- Add or update tool catalog bindings, confirmation/approval, transaction/idempotency, trace, and test expectations for all five workstreams.

### WCTE-03: Backend plan/confirmation DTOs and surfaces

- Add Java DTOs/records and surface envelopes for `chat_tool_plan_proposal`, `chat_tool_plan_confirmation`, `chat_tool_plan_result`, and `chat_tool_plan_system_message` or equivalent typed surfaces.
- Persist/display proposal records as workstream entries without executing tools.
- Cover no-mutation and idempotency behavior in tests.

### WCTE-04: Governed runtime plan proposal

- Extend the governed workstream agent runtime path to request structured plan proposals for human-chat tool execution.
- Use real Akka Agent runtime in normal path with active AgentDefinition, prompt assembly, tool boundary, registered tools, and traces.
- Fail closed with typed system-message/plan-unavailable surfaces when provider/runtime/tool-boundary configuration is missing.
- Tests may use deterministic test provider/model behavior.

### WCTE-05: Chat tool catalog and dispatcher

- Add backend-owned workstream chat tool catalog entries mapping plan step ids to governed tool ids, existing surface/browser actions or service methods, capability ids, schemas, idempotency rules, policy/approval, and trace fields.
- Add dispatcher that executes confirmed steps one transaction at a time through existing authorized paths where possible.
- Reject plans whose steps are not in the selected workstream catalog or whose inputs do not match the confirmed plan snapshot.

### WCTE-06: User Admin plan proposal proof

- Implement the User Admin proposal path for `create org "Org 1", and invite mckee.hugh@gmail.com as an org admin`.
- It must produce a detailed plan and require explicit confirmation.
- It must not create the organization or invitation before confirmation.

### WCTE-07: User Admin confirmed execution proof

- Execute the confirmed User Admin plan step-by-step.
- Step 1 creates Organization `Org 1` through the existing governed Organization create path.
- Step 2 invites `mckee.hugh@gmail.com` as Organization Admin for the created Organization through the existing invitation path.
- Record per-step results, traces, idempotency, and partial failure behavior.

### WCTE-08: Frontend plan confirmation/result surfaces

- Add typed frontend surfaces/components and API client support for chat tool plan proposal, confirmation, execution progress/result, partial failure, denial, and recovery.
- Ensure explicit confirmation is accessible and plan-bound.
- Do not expose secrets, hidden capabilities, provider payloads, or raw prompts/tool internals.

### WCTE-09: User Admin runtime/API/UI tests

- Add backend, frontend contract, and where possible endpoint/API tests for the User Admin proof path.
- Prove no mutation before confirmation, authorized execution after confirmation, safe denial, idempotency, partial failure, traces, and frontend rendering.

### WCTE-10: Representative all-workstream expansion

- Add one representative confirmed chat tool-plan path for My Account, Agent Admin, Audit/Trace, and Governance/Policy, plus retain User Admin coverage.
- Prefer lower-risk read/evidence, proposal, settings, or review actions for the first pass.
- Keep high-impact activation/export/destructive actions approval-gated or blocked unless existing surface semantics already support safe confirmation.

### WCTE-11: Agent seeds, docs, and app-description traceability updates

- Update starter seed prompt/skill/reference material so each workstream agent can explain confirmed chat tool plans and distinguish them from deterministic surface routing.
- Update traceability docs/specs if new IDs or surfaces were introduced.

### WCTE-99: Terminal verification

- Verify README done state.
- Run backend/frontend checks and API/UI/manual smoke path as far as local configuration allows.
- Append follow-up tasks plus a new terminal verification task if material gaps remain.

## Design guardrails

- Deterministic surface intent routing stays first for high-confidence no-mutation surface-open/prefill prompts.
- Model-backed plan proposal must use the governed Akka Agent runtime path in normal runtime.
- The model can propose but cannot authorize.
- Execution requires explicit human confirmation of the exact plan snapshot.
- Tool execution uses backend authorization and the selected AuthContext on every step.
- Plan step failure should stop dependent steps unless the plan explicitly declares safe independent continuation.
- Every plan and step must be traceable to source prompt, selected workstream, requestedBy, confirmedBy, capability id, governed tool id, idempotency key, and result surface.
