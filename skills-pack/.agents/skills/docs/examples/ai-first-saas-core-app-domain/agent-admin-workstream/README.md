# Agent Admin Workstream PRD

## PRD identity

- **Workstream id:** `agent_admin`
- **Backing functional agent:** `functional_agent.agent_admin`
- **Domain:** `ai_first_saas_core_app`
- **Purpose:** govern runtime agents, prompts, skills, references, manifests, tool boundaries, lifecycle, behavior proposals, approvals, deterministic prompt assembly, and agent work traces
- **Primary users:** organization admins, agent behavior stewards, governance admins, auditors with read-only authority

## Invariants

```text
This workstream is backed by exactly one functional/context-area agent.
Surfaces are the only renderable workstream artifacts.
System messages are typed surfaces.
Every surface action, including read/query and surface-request actions, maps to a governed backend capability.
The workstream agent may request surfaces and guide users, but backend capabilities enforce authority.
```

Prompt/skill/reference content is behavior guidance only. It cannot grant tool authority, data access, tenant scope, approval authority, or role capabilities.

## User intents

The workstream agent must handle:

- `dashboard`, `show agents`, `show disabled agents`
- `view UserAdminAgent`, `edit prompt`, `show active prompt version`
- `show skills`, `add skill draft`, `compare skill versions`
- `show references`, `open manifest`, `why can this agent read this doc`
- `show tool boundaries`, `can this agent send email`, `deny this tool`
- `draft behavior change`, `simulate change`, `request approval`, `activate version`, `rollback`
- `show prompt assembly trace`, `show skill load trace`, `show agent work trace`
- help/how-to questions for agent behavior governance

## Required surfaces

| Surface id | Type | Purpose | Producing capability | Primary actions |
|---|---|---|---|---|
| `surface.agent_admin.dashboard.v1` | dashboard | action router for agent-governance attention and available admin work: pending behavior proposals, failed tests, disabled agents needing review, authority-expansion risks, trace investigations, agent catalog and draft/proposal actions | `agent_admin.dashboard.view` | open proposals, open failed tests, open disabled-agent review, open authority-risk review, open agents, open traces |
| `surface.agent_admin.agents_list.v1` | data_table | list AgentDefinitions | `agent_admin.agents.search` | open agent, create draft, filter |
| `surface.agent_admin.agent_detail.v1` | detail_card | lifecycle, owner, model policy, prompt/skill/reference manifests, tool boundary | `agent_admin.agents.view` | edit metadata, open prompt, open manifests, open tool boundary, disable/enable |
| `surface.agent_admin.prompt_versions.v1` | data_table/diff_review | PromptDocument/PromptVersion history and diffs | `agent_admin.prompts.view` | draft prompt, compare, request approval, activate, rollback |
| `surface.agent_admin.skill_versions.v1` | data_table/diff_review | SkillDocument/SkillVersion history and diffs | `agent_admin.skills.view` | draft skill, compare, request approval, activate, rollback |
| `surface.agent_admin.reference_versions.v1` | data_table/diff_review | ReferenceDocument/ReferenceVersion lifecycle and access notes | `agent_admin.references.view` | draft reference, approve, activate, rollback |
| `surface.agent_admin.manifest_editor.v1` | detail/form/diff_review | assigned skill/reference manifests and compact expertise manifest | `agent_admin.manifests.view` | propose manifest change, simulate, request approval |
| `surface.agent_admin.tool_boundary.v1` | detail/form/diff_review | ToolPermissionBoundary allowed/denied tools, data, side effects | `agent_admin.tool_boundaries.view` | propose boundary change, request approval |
| `surface.agent_admin.behavior_proposal.v1` | decision_card/diff_review | proposed behavior change with rationale, risk, tests, replay/simulation | `agent_admin.proposals.view` | approve, reject, request changes, simulate, activate |
| `surface.agent_admin.prompt_assembly_trace.v1` | audit_timeline | assembly inputs, versions, manifests, tool list, authorization decisions | `agent_admin.traces.prompt_assembly.view` | open source doc/version, open agent work trace |
| `surface.agent_admin.system_message.v1` | system_message | denials, validation, approval required, activation success/failure | capability-specific | retry, open trace, request approval |

## Surface style expectations

These surfaces inherit `ai-first-workstream-enterprise` from `../../../web-ui-style-guide.md`: calm enterprise workstream styling, named-theme tokens, neutral layered surfaces, blue/indigo AI accent, sparse semantic status colors, accessible focus states, strong version/table hierarchy, and prominent evidence/authority/trust cues. Style is a UI realization layer only; it must not change functional-agent semantics, managed-agent governance, prompt/skill/reference authority, tool-boundary enforcement, capability mappings, approval rules, routes, or trace behavior.

- Dashboard: render as an agent-governance action router, not a passive briefing. Put clickable attention indicators for pending proposals, failed tests, authority-expansion risk, disabled-agent review, and trace investigations first; put clickable next-action indicators for open agent catalog, create draft, review manifests, and open tool boundaries second. Active/disabled counts and recent-trace indicators are allowed only when the whole card opens the matching filtered queue/detail/history surface, including useful `0` states; passive health metrics belong in agent detail, trace, or report surfaces.
- Agent catalog and detail: use dense catalog rows/cards and layered detail panels showing lifecycle, owner/steward, model policy, prompt, manifests, tool boundary, status, and recent trace links with explicit disabled/read-only states.
- Prompt, skill, and reference version surfaces: use enterprise diff-review layouts with version metadata, checksums/status, reviewer state, activation/rollback eligibility, simulation/test evidence, redaction notes, and side-by-side or inline diffs as appropriate.
- Manifest and tool-boundary editors: emphasize compact expertise manifests, assigned/unassigned artifacts, allowed/denied tools, data/side-effect boundaries, and authority-expansion warnings through governance/trust control panels and approval-required decision cards.
- Behavior proposal surfaces: render as decision cards plus diff review with rationale, risk/impact, expected tests/replays, approve/reject/request-changes actions, and trace/audit links.
- Prompt assembly and work-trace surfaces: render as audit timelines with ordered inputs, versions, manifest entries, tool list, authorization decisions, allowed/denied loads, correlation ids, and links back to source documents and agent work traces.
- System-message surfaces: use typed cards for unsafe requests, validation, denied authority, pending approval, activation success/failure, and trace-unavailable states with semantic icon/color plus text, recovery actions, and request-approval/open-trace affordances when authorized.

## Capability inventory and exposure channels

A capability is the governed backend contract. It may be exposed through one or more channels: surface action, browser API, workstream-agent tool, internal-agent tool, workflow step, timer, consumer, MCP tool, view, or internal method. Browser APIs and agent tools are exposure forms over the same capability; they do not redefine authorization, validation, idempotency, side effects, audit, approval, or denial behavior.

For this workstream, read/evidence capabilities may be exposed as tools so the Agent Admin workstream agent can answer conversational requests such as “show the active prompt for UserAdminAgent” or “why can this agent read this skill?”. Draft/proposal capabilities may be exposed as tools when they create governed drafts only. Activation, rollback, tool-boundary expansion, and authority-changing capabilities require explicit approval/surface action and must not be silently invoked by agent conversation.

| Capability id | Class | Purpose | Side effects |
|---|---|---|---|
| `agent_admin.dashboard.view` | read/evidence | dashboard summary | read trace |
| `agent_admin.agents.search` | read/evidence | list AgentDefinitions | read trace |
| `agent_admin.agents.view` | read/evidence | agent detail | read trace |
| `agent_admin.agents.update_metadata` | command/approval | update lifecycle/owner/model policy metadata | writes AgentDefinition version/state, audit |
| `agent_admin.prompts.view` | read/evidence | prompt document/version payloads and diffs | sensitive read audit |
| `agent_admin.prompts.draft` | proposal | create draft prompt version | draft version, audit |
| `agent_admin.skills.view` | read/evidence | skill document/version payloads and diffs | sensitive read audit |
| `agent_admin.skills.draft` | proposal | create draft skill version | draft version, audit |
| `agent_admin.references.view` | read/evidence | reference documents/versions | sensitive read audit/redaction |
| `agent_admin.references.draft` | proposal | create draft reference version | draft version, audit |
| `agent_admin.manifests.view` | read/evidence | skill/reference manifest assignments | read trace |
| `agent_admin.manifests.propose_change` | proposal/approval | propose skill/reference assignment changes | proposal entity, audit |
| `agent_admin.tool_boundaries.view` | read/evidence | tool boundary detail | read trace |
| `agent_admin.tool_boundaries.propose_change` | proposal/approval | propose tool authority changes | proposal entity, audit |
| `agent_admin.proposals.view` | read/evidence | behavior-change proposal | read trace |
| `agent_admin.proposals.simulate` | workflow | replay/simulate behavior change | simulation workflow, traces |
| `agent_admin.proposals.approve` | approval | approve behavior change | approval audit |
| `agent_admin.versions.activate` | governance/approval | activate approved version/manifest/boundary | active version change, audit |
| `agent_admin.versions.rollback` | governance/approval | rollback to prior active version | active version change, audit |
| `agent_admin.traces.prompt_assembly.view` | trace/audit | view assembly trace | sensitive-read audit |
| `agent_admin.traces.agent_work.view` | trace/audit | view agent work trace | sensitive-read audit |

## Authorization and policy

- Agent admin requires explicit role/capability; auditors may receive read-only redacted views.
- Activation of prompt/skill/reference/manifest/tool-boundary changes requires approval unless a narrow safe policy says otherwise.
- Tool-boundary expansion, data-scope expansion, side-effect expansion, email/tool enablement, policy/gov authority changes, and cross-tenant effects require human approval.
- Prompt/skill text cannot grant authority; backend checks `AuthContext`, `AgentDefinition`, manifest assignment, document status/version, and `ToolPermissionBoundary`.
- Implementation-developed default content is represented as governed draft/active records with provenance and review/change-control rules; upgrades must not overwrite tenant customizations.

## Workstream-agent prompt requirements

`workstream-agent/prompt.md` must define the agent as the agent behavior governance assistant. It must:

- guide users through agent definitions, prompts, skills, references, manifests, boundaries, tests, and traces;
- draft proposed behavior changes with rationale, risk, expected impact, and test/replay suggestions;
- explain authority boundaries and why content cannot grant permissions;
- request surfaces for agents, versions, diffs, manifests, traces, simulations, and proposals;
- refuse direct activation without the required approval capability;
- emit system-message surfaces for unsafe requests, denied authority, pending approval, and activation results.

Runtime skills should cover agent lifecycle, prompt governance, skill/reference governance, manifest design, tool boundaries, deterministic assembly, behavior testing, and trace interpretation.

## Akka realization candidates

- ESE: `AgentDefinitionEntity`, `PromptDocumentEntity`, `SkillDocumentEntity`, `ReferenceDocumentEntity`, `ManifestEntity`, `ToolBoundaryEntity`, `BehaviorProposalEntity`.
- Workflow: behavior proposal approval, simulation/replay, activation/rollback.
- Views: `AgentCatalogView`, `PromptVersionView`, `SkillVersionView`, `ReferenceVersionView`, `ManifestView`, `ToolBoundaryView`, `BehaviorProposalQueueView`, `AgentTraceView`.
- Agent: `AgentAdminAgent` and optional `AgentBehaviorEditorAgent`.
- Tools: governed `readSkill(skillId)`, `readReferenceDoc(referenceId)`, trace readers, proposal draft tools.
- HTTP: `/api/agent-admin/**` surface payload/action endpoints.
- Consumers: governed default setup/audit projection/trace enrichment.

## Tests

Required:

- list/view agents, prompts, skills, references, manifests, tool boundaries;
- draft prompt/skill/reference changes;
- diff review and proposal approval/rejection;
- activation denied without approval;
- tool-boundary expansion approval required;
- deterministic prompt assembly includes compact manifests only;
- `readSkill`/`readReferenceDoc` authorize manifest/version/status/tool boundary and trace allowed/denied loads;
- governed defaults idempotent and do not overwrite tenant customizations;
- audit/work traces emitted for reads, drafts, approvals, activations, rollbacks, denied loads;
- auditors see redacted read-only surfaces;
- workstream agent explains and requests surfaces without granting authority.

## Not ready if

- prompt or skill files are edited directly without governed versions;
- all skills/references are preloaded into every agent prompt;
- one global skill list is used for all agents;
- tool descriptions or prompt text are treated as authorization;
- activation has no approval/audit path;
- prompt assembly and skill/reference loads are untraced.
