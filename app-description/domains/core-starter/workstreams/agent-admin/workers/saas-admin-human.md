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

May inspect and activate permitted prompt/skill/reference changes. May not create/delete whole agents, expose provider secrets, mutate model settings/tool boundaries directly, or expand tenant/customer scope through behavior text.
