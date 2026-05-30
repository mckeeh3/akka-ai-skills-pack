# Conversation Capture

## Source discussion

After all pending `full-core-smb-*` tasks were completed, the assistant inspected the SMB full-core outline and recommended Agent Admin as the next workstream. The user agreed and asked to proceed.

## Accepted decisions

- Create `specs/full-core-smb-agent-admin/` as the next mini-project.
- Sequence Agent Admin after User Admin because User Admin now provides real authority/access-change evidence and a durable worker pattern.
- Sequence Agent Admin before Audit/Trace and Governance/Policy because those later workstreams need richer behavior-change and managed-agent runtime traces.
- Preserve SMB scope; avoid enterprise model marketplaces, arbitrary tool/plugin management, and page-first CRUD prompt editors.
- Keep deterministic services responsible for lifecycle, validation, authorization, ToolPermissionBoundary enforcement, provider readiness, redaction, seed idempotency, and audit/trace.
- Use governed request/response Akka Agent behavior for AgentAdminAgent guidance, with provider fail-closed behavior.
- Introduce a behavior-review/prompt-risk worker only after deterministic Agent Admin foundations exist.

## Constraints

- Target is `templates/ai-first-saas-starter/` as the executable baseline.
- Workstream + structured surface UX remains the only product architecture.
- Missing provider/model config must fail closed with actionable surfaces/traces.
- No deterministic/model-less successful normal responses for model-backed Agent Admin guidance or workers.
- Visual quality and runtime validation are first-class acceptance criteria.
- One task per fresh harness context.

## Risks

- Agent Admin can easily become too broad. Keep the first source-map task focused on an SMB vertical slice sequence.
- Behavior artifact visibility can leak sensitive prompts or secrets. Require redaction, authority checks, and browser-safe previews.
- Tool-boundary changes can expand authority. Deterministic proposal/review/activation must own authority-changing transitions.
- AgentAdminAgent guidance can accidentally imply it changed behavior. It may draft or explain, but deterministic lifecycle capabilities must commit changes.

## Unresolved questions

No blocking product question is required to start. The implementation-map task must determine the current starter's Agent Admin source shape and whether existing behavior-change lifecycle primitives are sufficient or need a deterministic foundation task first.
