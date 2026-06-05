# Surface Contract: Agent Governance Center

- surface-id: `agent-governance-center`
- type/version: governance-workspace/v1
- owner functional agents: `agent-admin-agent` and `governance-policy-agent`
- reusable surfaces: behavior diff/proposal review, decision cards, audit trace explorer, prompt/skill/reference/version cards.

## Placement and graph role

This surface is the governance workspace for managed agent definitions, prompts, skills, references, manifests, tool boundaries, behavior proposals, tests, simulations, approvals, and activation history.

## Payload summary

Payload must include:

- selected `AuthContext`, governance scope, `correlationId`, trace ids, redaction profile, freshness marker;
- agent catalog/detail summaries: AgentDefinition status, owner/steward, authority level, active model config ref, active prompt/skill/reference manifests, tool-boundary summary, lifecycle state, and risk flags;
- proposal/diff cards for prompt, skill, reference, manifest, tool boundary, model policy, and behavior-profile changes;
- prompt assembly, skill load, reference load, tool-boundary, model invocation, and work-trace links;
- action descriptors with capability ids, governed-tool ids, browser-tool/agent-tool exposure labels, approval requirements, idempotency, and result surfaces.

## Allowed actions

| Action | Capability hint | Qualified exposure | Result surface |
|---|---|---|---|
| Search/list managed agents | `agents.catalog.search` | browser-tool, agent-tool | update governance center |
| Open agent detail | `agents.detail.read` | browser-tool surface-request | agent detail/version card |
| Propose behavior edit | `agents.behavior.propose` | browser-tool, agent-tool | diff/proposed-change review |
| Review/approve/reject proposal | `agents.behavior.review` | browser-tool | decision card or activation system message |
| Run safe test console | `agents.behavior.test` | browser-tool | test result surface and traces |
| Open prompt/skill/reference version | `agents.documents.read` | browser-tool | version card/diff |
| Open tool-boundary trace | `audit.traces.view` | browser-tool | `audit-trace-explorer` |

## UI states

- `loading`: governance workspace skeleton without showing stale secrets.
- `empty`: no managed agents or no proposals in selected scope.
- `error`: safe category and `correlationId`.
- `forbidden`: no hidden agent/document existence leakage.
- `conflict`: proposal/version changed; require refresh.
- `approval-needed`: high-risk change requires reviewer decision.
- `partial-data`: redacted prompt/reference/trace fields visible with omitted markers.

## Auth/security

- Behavior documents, manifests, and tool boundaries are versioned governed records; prompt/skill text cannot grant authority.
- Authority expansion requests are denied or routed to explicit approval policy.
- Provider credentials, secret refs, raw prompt internals beyond authorized view, and hidden tool bindings are not sent to unauthorized browsers.
- Runtime assembly reads active governed versions, not static filesystem defaults.

## Rendering and capability tests

- Agent catalog/detail, prompt/skill/reference/manifest/tool-boundary variants preserve owner, lifecycle, version, risk, and trace semantics.
- Behavior edit proposals produce diff/review/decision surfaces with approval and denial behavior.
- Authority expansion attempts fail closed and emit audit/work traces.
- Safe test console uses governed test capability and never bypasses tool boundaries.
- PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, and denial traces are linkable through Audit/Trace.
