# Workers: Agent Admin

Agent Admin uses distinct workers and actor adapters. Worker authority does not transfer across adapters; backend capability checks and `ToolPermissionBoundary` enforcement remain authoritative.

- `saas-admin-human`: authorized human operator using browser `surface_action` controls and confirmed `human_chat_tool_plan` actions.
- `agent-admin-functional-agent-worker`: user-facing functional-agent worker that guides SaaS admins, routes surfaces, drafts behavior-change plans, and invokes only allowed tools.
- `agent-behavior-editor-internal-agent-worker`: model-backed internal editing worker that produces structured behavior-change proposals and never directly activates runtime behavior.
- `agent-runtime-system-worker`: runtime resolver/loader worker that resolves active profiles, prompt/skill/reference manifests, model policy, tool boundaries, and emits traces.
