# Surface Contract: Agent Governance Center

- surface-id: `agent-governance-center`
- type/version: governance-workspace/v1
- owner functional agent: `agent-admin-agent` (Agent Admin)
- reusable by: `governance-policy-agent` for policy/approval review and by Audit/Trace for scoped evidence drill-ins.

## Placement and graph role

This surface is the governance workspace for managed agent definitions, prompts, skills, references, manifests, tool boundaries, behavior proposals, tests, simulations, approvals, and activation history.


## User-visible/internal metadata boundary

Default rendering must use SaaS product language and show only information the current actor needs to decide, act, recover, or understand the business outcome. Internal ids, raw trace/event/correlation data, governed-tool/capability ids, backend component names, prompt/provider/model details, and policy implementation references are implementation metadata. Expose them only in authorized admin, support, auditor, or developer drilldowns, and keep them visually subordinate to user-meaningful labels.

## Payload summary

Payload must include:

- selected `AuthContext`, governance scope, `correlationId`, trace ids, redaction profile, freshness marker;
- agent catalog/detail summaries: AgentDefinition status, owner/steward, authority level, active model config ref, active prompt/skill/reference manifests, tool-boundary summary, lifecycle state, and risk flags;
- proposal/diff cards for prompt, skill, reference, manifest, tool boundary, model policy, and behavior-profile changes;
- prompt assembly, skill load, reference load, tool-boundary, model invocation, and work-trace links;
- action descriptors with capability ids, governed-tool ids, browser-tool/agent-tool exposure labels, approval requirements, idempotency, and result surfaces.

## Compact payload schema

```ts
type AgentGovernanceCenterData = {
  authContext: SurfaceAuthContext;
  governanceScope: { tenantId: string; selectedContextId: string; redactionProfile: string };
  agentSummaries: Array<{ agentDefinitionId: string; displayName: string; lifecycleState: string; ownerSteward: string; authorityLevel: string; modelConfigRef?: string; riskFlags: string[] }>;
  artifactSummaries: Array<{ artifactId: string; artifactType: string; activeVersion?: string; draftCount: number; reviewRequired: boolean; omittedFieldKeys: string[] }>;
  proposalSummaries: Array<{ proposalId: string; proposalType: string; status: string; risk: string; decisionId?: string; traceIds: string[] }>;
};
```

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

## Action mapping

| actionId | browserToolId | governedToolId | capabilityId | exposure | resultSurfaceId | idempotency | traceRequired |
| --- | --- | --- | --- | --- | --- | --- | --- |
| `agent-governance.search-agents` | `agent-governance.agents.search` | `agents.catalog.search` | `managed-agent-foundation` | browser-tool, agent-tool | `agent-governance-center` | query fingerprint | true |
| `agent-governance.open-agent-detail` | `agent-governance.agent.open` | `agents.detail.read` | `managed-agent-foundation` | browser-tool, surface-request | deferred `agent-detail-card` | agent definition id | true |
| `agent-governance.propose-behavior-edit` | `agent-governance.behavior.propose` | `agents.behavior.propose` | `managed-agent-foundation` | browser-tool, agent-tool | deferred `behavior-diff-review` or `decision-card` | proposal request id | true |
| `agent-governance.review-proposal` | `agent-governance.behavior.review` | `agents.behavior.review` | `governance-decisions-audit` | browser-tool | `decision-card` or `system_message` | proposal id + reviewer id + request id | true |
| `agent-governance.run-safe-test` | `agent-governance.safe-test.run` | `agents.behavior.test` | `managed-agent-foundation` | browser-tool | deferred `safe-test-console-result` or `system_message` | test run request id | true |
| `agent-governance.open-document-version` | `agent-governance.document-version.open` | `agents.documents.read` | `managed-agent-foundation` | browser-tool | deferred `agent-version-card` | document id + version id | true |
| `agent-governance.open-tool-boundary-trace` | `agent-governance.tool-boundary-trace.open` | `audit.traces.view` | `governance-decisions-audit` | browser-tool | `audit-trace-explorer` | trace id | true |



Action mappings must preserve the shared tool-use contract: `governedToolId`, actor adapter/source (`surface_action`, `human_chat_tool_plan`, `agent_tool_call`, API/workflow/timer/consumer/MCP/internal), `confirmationRequired`, `approvalPolicy`, idempotency key, transaction boundary, result/partial-failure behavior, `traceSource`, and `traceRequired`. If this surface exposes only the browser-tool adapter, state `surface_action` and keep any chat/agent adapter in the workstream tool catalog instead of duplicating business semantics.

## UI states

- `loading`: governance workspace skeleton without showing stale secrets.
- `empty`: no managed agents or no proposals in selected scope.
- `error`: safe category and readable support/reference label; raw `correlationId` appears only in authorized diagnostic detail.
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
