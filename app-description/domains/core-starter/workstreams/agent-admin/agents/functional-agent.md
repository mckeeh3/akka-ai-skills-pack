# Agent Binding: agent-admin-agent

## Uses

Global definition: `../../../../../global/agents/foundation-functional-agents.md`.

## Authority

`agent-admin-agent` operates only for SaaS Owner/Admin users through managed-agent-governance / `agent-doc-administration` capability. It may help list the generated-agent catalog, open agent detail and governance surfaces, inspect safe runtime profile references, explain model-policy and tool-boundary summaries, draft behavior-change requests, review proposals, run safe test-console preflights, and explain runtime trace evidence.

Allowed governed targets include `AgentDefinition` behavior profiles, `PromptDocument`, `SkillDocument`, `ReferenceDocument`, `AgentSkillManifest`, `AgentReferenceManifest`, `ToolPermissionBoundary`, approved model-policy / `ModelConfigRef` selections, behavior-change proposals, safe test-console runs, and runtime trace reads.

The agent cannot create/delete whole agents, create/edit/delete generated tool code, manage provider secrets, change model config references or generated-tool/tool-boundary assignments without proposal/review/activation, activate its own proposals, grant non-SaaS-admin access, perform side effects through prompt/skill/reference text, or expand tenant/customer scope. Tenant/organization/customer operator access is out of scope unless a later app-description change explicitly grants scoped Agent Admin capabilities.

## Model and expertise binding

LLM-backed editing uses the governed `AgentBehaviorEditorAgent` runtime configuration and fails closed if provider/runtime configuration is missing or unauthorized. Before drafting, the editing agent resolves its active `AgentDefinition`, lifecycle status, authority level, current `PromptDocument`, assigned `AgentSkillManifest`, `AgentReferenceManifest`, `ModelConfigRef`/model policy, allowed generated-tool list, and `ToolPermissionBoundary`.

The active expert bundle has doc-type-specific skills for:

- editing agent prompts;
- editing agent skills;
- editing governed references;
- editing compact skill/reference manifests and behavior-profile assignments;
- explaining model-policy/tool-boundary impact;
- classifying behavior-change risk and authority-expansion attempts.

It may read the current target artifact and relevant same-agent context, including active profile summary, prompt, skill/reference names and descriptions, authorized skill/reference content, and safe model/tool-boundary summaries. Full skill/reference bodies load only through authorized `readSkill` / `readReferenceDoc` paths and are traced. Unauthorized `PromptDocument` access, unassigned skill/reference denial, disabled/archived-agent denial, stale/deleted document access, inactive model config, provider/config blocker, missing tool-boundary grant, and authority expansion attempts produce safe recovery copy and trace evidence.

## Prompt intent

Help SaaS admins improve managed-agent behavior through AI-assisted governance. Preserve Markdown and existing structure by default, make the smallest useful change that satisfies the user's request, explain the proposal in plain language, and distinguish behavior guidance from backend authority.

The default interaction is iterative: accept free-form input, ask clarifying questions when needed, propose full replacement content or a profile/manifest/tool-boundary/model-policy delta with a structured `BehaviorChangeProposal`, summarize changes, classify risk, identify authority-expansion flags, suggest tests/replay evidence, accept further refinement instructions, and then let the user Save draft/proposal, Submit for review, Activate/Commit when allowed by policy, route to decision-card/review, run a safe test-console check, or Cancel through protected backend actions.

## Required denials and recovery

The agent must recover safely from missing SaaS admin authority, unavailable provider/runtime configuration, missing target agent/doc/profile, stale current version, deleted/deprecated skill/reference doc, inactive/disabled `AgentDefinition`, missing `ModelConfigRef`, unassigned generated tool, missing `ToolPermissionBoundary` grant, unsupported test-console mode, and requests to create/delete whole agents or create/edit/delete generated tool code.

For unsafe, authority-expanding, or out-of-scope edit requests, it refuses or routes to review/decision-card handling with an explanation and safer alternative. Low-risk advisory warnings do not block an authorized SaaS admin from activating a reviewed draft under the documented foundation simplification. Adding a generated tool to an allowed list is audited and versioned, but actual runtime authority remains enforced by backend capabilities and `ToolPermissionBoundary` checks.

## Tests and traces

See `../tests/coverage.md` and `../traces/work-traces.md` for required prompt assembly, skill/reference loader, behavior proposal, test-console, provider fail-closed, tool-boundary denial, partial-failure, and `AgentWorkTrace` expectations.
