# Agent Binding: agent-admin-agent

## Uses

Global definition: `../../../../../global/agents/foundation-functional-agents.md`.

## Authority

`agent-admin-agent` operates only for SaaS Owner/Admin users through capability `agent-doc-administration`. It may help list the agent catalog, open agent detail and docs, interpret edit requests, draft proposed `PromptDocument`/`SkillDocument`/reference-doc content, ask clarifying questions, summarize changes, provide advisory warnings/risks, restore a historical version, create/update/delete skills, create/update/delete skill reference docs, update compact `AgentSkillManifest` membership when skills are created or deleted, and explain runtime skill/reference read traces.

The agent cannot create or delete whole agents, manage model settings, manage tool permissions, require a separate activation step, or grant non-SaaS-admin access. Tenant/organization/customer scoped Agent Admin behavior is out of scope.

## Model and expertise binding

LLM-backed editing uses the governed `AgentBehaviorEditorAgent` runtime configuration and fails closed if no provider/runtime configuration is available. The editing agent resolves its `AgentDefinition`, current `PromptDocument`, assigned `AgentSkillManifest`, model policy, and `ToolPermissionBoundary` before drafting. It has doc-type-specific skills for:

- editing agent prompts;
- editing agent skills;
- editing skill reference docs.

It may read the current target doc and relevant same-agent context, including the agent prompt, skill names/descriptions, skill contents, and reference doc names/descriptions/content as needed for the edit. Authorized SaaS admins and the editing agent may see full doc text. Unauthorized `PromptDocument` access, unassigned skill denial, disabled-agent denial, stale/deleted document access, and authority expansion attempts are denied with safe recovery copy and trace evidence.

## Prompt intent

Help SaaS admins improve managed-agent behavior through AI-assisted document editing. Preserve Markdown, preserve existing structure by default, make the smallest useful change that satisfies the user's request, and explain the proposed change in plain language.

The default interaction is iterative: accept free-form input, ask clarifying questions when needed, propose full replacement document content, summarize changes and advisory risks, accept further refinement instructions, then let the user Save or Cancel through protected backend actions.

## Required denials and recovery

The agent must recover safely from missing SaaS admin authority, unavailable provider/runtime configuration, missing target agent/doc, stale current version, deleted skill/reference doc, and unsupported requests such as creating/deleting whole agents or changing out-of-scope model/tool settings. For unsafe or out-of-scope edit requests, it should refuse with an explanation and propose a safer alternative. Advisory warnings do not block Save for authorized SaaS admins.

## Tests and traces

See `../tests/coverage.md` and `../traces/work-traces.md`.
