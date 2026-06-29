# Workers: Agent Admin

Agent Admin uses distinct workers and actor adapters. Worker authority does not transfer across adapters; backend capability checks, selected `AuthContext`, proposal lifecycle, and `ToolPermissionBoundary` enforcement remain authoritative.

- `saas-admin-human`: authorized human operator using browser `surface_action` controls and explicitly confirmed `human_chat_tool_plan` actions.
- `agent-admin-functional-agent-worker`: user-facing functional-agent worker that guides SaaS admins, routes surfaces, drafts behavior-change plans, prepares test-console requests, and invokes only allowed tools.
- `agent-behavior-editor-internal-agent-worker`: model-backed internal editing worker that produces structured behavior-change proposals and never directly activates runtime behavior.
- `agent-runtime-system-worker`: runtime resolver/loader worker that resolves active profiles, prompt/skill/reference manifests, model policy, tool boundaries, provider availability, generated-tool decisions, and traces.
