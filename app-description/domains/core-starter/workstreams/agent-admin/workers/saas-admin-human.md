# Worker: SaaS Admin Human

## Responsibility

Authorized SaaS Owner/Admin user who improves managed-agent behavior through Agent Admin browser surfaces, confirmed chat plans, proposal review, approvals, and safe test-console workflows.

## Execution harness and adapters

- Browser workstream shell and structured surfaces.
- `surface_action` adapters for catalog/detail reads, governance edits, proposal review, approval, activation, assignment, confirmation, test-console, and trace actions.
- Confirmed `human_chat_tool_plan` only for catalog-bound Agent Admin actions, with explicit confirmation bound to the exact proposed plan before any consequential governed tool executes.

## Authority

Authority comes from selected SaaS Owner/Admin `AuthContext` and explicit managed-agent-governance / `agent_admin.*` capabilities. Surface visibility, chat text, prompt text, skill/reference text, or model output does not grant authority.

## Boundaries

May inspect and activate permitted model config reference, prompt, skill, reference, manifest, skill-assignment, generated-tool-assignment, tool-boundary reference, and test-console changes through versioned proposal/document/profile flows. May not create/delete whole agents, create/edit/delete generated tool code, expose provider secrets, mutate raw model settings or backend tool-boundary implementations directly, approve authority expansion without the required review route, or expand tenant/customer scope through behavior text.
