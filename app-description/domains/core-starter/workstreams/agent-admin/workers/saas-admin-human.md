# Worker: SaaS Admin Human

## Responsibility

Authorized SaaS Owner/Admin user who improves managed-agent behavior through Agent Admin browser surfaces.

## Execution harness and adapters

- Browser workstream shell and structured surfaces.
- `surface_action` adapters for list/read/edit/review/activate actions.
- Confirmed `human_chat_tool_plan` only for catalog-bound actions represented by Agent Admin surfaces.

## Authority

Authority comes from selected SaaS Owner/Admin `AuthContext` and explicit `agent-doc-administration` / `agent_admin.*` capabilities. Surface visibility, chat text, prompt text, or model output does not grant authority.

## Boundaries

May inspect and activate permitted model config reference, prompt, skill, reference, skill-assignment, and generated-tool-assignment changes through versioned behavior-profile/document flows. May not create/delete whole agents, create/edit/delete generated tool code, expose provider secrets, mutate raw model settings or backend tool-boundary implementations directly, or expand tenant/customer scope through behavior text.
