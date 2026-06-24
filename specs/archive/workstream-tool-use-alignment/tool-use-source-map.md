# Workstream Tool Use Source Map

## Scope and method

Task: `TASK-WTUA-01-001` audit only. This source map records existing `skills-pack/` guidance that affects governed workstream tool use for human-backed and AI-backed actors. It does not rewrite canonical docs or skill guidance.

Search basis:

- Required canonical reads from the task brief.
- Targeted repository searches over `skills-pack/docs`, `skills-pack/skills`, `skills-pack/templates`, `skills-pack/examples`, and `skills-pack/tools` for `governed-tool`, `governed tool`, `tool boundary`, `ToolPermissionBoundary`, `surface action`, `browser-tool`, `agent-tool`, `FunctionTool`, `MCP-tool`, `direct mutation`, `no mutation`, `direct command`, `surface routing`, `confirmation`, `approval`, `human-backed`, `AI-backed`, and `requestedBy`.

Finding classes:

- **aligned** — already supports governed tools/capabilities, actor adapters, authorization, approval, idempotency, or traces.
- **needs refinement** — generally compatible, but should name the new shared workstream tool model, human chat tool-plan adapter, confirmation, transaction boundary, or trace source more explicitly.
- **potentially conflicting** — wording can be read as a global prohibition on direct chat-mediated tool execution, or as tools being AI-only, even if the local intent is safer surface routing.
- **out of scope** — mentions tools/confirmation/approval in a local implementation or unrelated sense that does not require this mini-project to edit it unless a later consistency pass finds concrete contradictions.

## High-level audit result

The pack is already mostly capability-first and tool-aware. The strongest existing alignment is in:

- `skills-pack/docs/agent-workstream-application-architecture.md`
- `skills-pack/docs/workstream-contract.md`
- `skills-pack/docs/structured-surface-contracts.md`
- `skills-pack/docs/capability-first-backend-architecture.md`
- `skills-pack/docs/requirements-to-workstream-development-process.md`
- `skills-pack/docs/current-intent-model.md`

The main gap is not missing security guidance. It is that the canonical docs and skills do not consistently define **human chat as a governed, confirmed natural-language tool-plan adapter** alongside structured surfaces and model-facing agent-tools. Current guidance strongly recommends deterministic composer-to-surface routing and no-mutation prefill behavior. That remains correct for the router, but later tasks should reconcile it with the accepted direction: a selected workstream agent may parse a human chat request, propose a detailed tool plan, require explicit confirmation, and then execute only the selected workstream agent's governed tools through deterministic backend authorization, idempotency, policy/approval, transaction, trace, and result-surface semantics.

## Canonical docs findings

| File | Current signal | Classification | Edit priority | Notes for later tasks |
|---|---|---:|---:|---|
| `skills-pack/docs/ai-first-saas-application-architecture.md` | Defines secure AI-first SaaS chain, governed-tools inside capabilities, browser/agent/internal exposure channels, managed-agent runtime, tool boundaries, and traces. | aligned | P0 for WTUA-02 | Add compact shared governed tool flow that includes structured surface adapter, human chat confirmed tool-plan adapter, AI agent-tool adapter, APIs/MCP/workflows/timers/consumers. Clarify each tool invocation as an independently authorized transaction boundary. |
| `skills-pack/docs/agent-workstream-application-architecture.md` | Already has human-backed and AI-backed actors sharing a governed workstream tool catalog; states surfaces are human tool-use interface and gives a user-admin invitation example from both surface and AI prompt. | needs refinement | P0 for WTUA-02 | This is the best canonical home for the full actor-adapter model. Add explicit `human_chat_tool_plan` path: propose detailed plan, bind confirmation to plan, execute individual governed tools, report partial failures/result surfaces. |
| `skills-pack/docs/workstream-contract.md` | Requires human-backed/AI-backed actor adapters, capability/governed-tool map, actor refs, trace sources, and says human surface availability alone does not grant model tool availability. | needs refinement | P0 for WTUA-02 | Extend required fields with workstream tool catalog and actor adapters: surface action, human-chat confirmed plan, agent-tool, internal/API/MCP. Add confirmation/transaction/idempotency/partial-failure evidence fields. |
| `skills-pack/docs/structured-surface-contracts.md` | Strongly aligned: surfaces are the human-backed actor's tool-use interface; actions map to browser-tool/governed-tool/capability; same governed tool may be exposed as agent/internal/API/MCP. | aligned | P1 for WTUA-02 | Preserve no-mutation prefill guidance. Add cross-reference that surfaces are one human adapter, while confirmed chat plans are another human adapter sharing the same governed tool ids. |
| `skills-pack/docs/capability-first-backend-architecture.md` | Strongly aligned: capability → governed-tool → exposure channel; browser-tool, agent-tool, internal-tool, MCP-tool; same auth/idempotency/audit across channels. | needs refinement | P0 for WTUA-02 | Add human-chat tool-plan exposure channel/adapter language or qualify it as a request-based workstream Agent turn that invokes governed-tools only after human confirmation. Add multi-step plan transaction/partial-failure obligations. |
| `skills-pack/docs/workstream-surface-intent-routing.md` | Defines deterministic composer routing as safe, no-mutation prefill before model-backed chat. Says router must not submit commands and future direct tools need separate governance. | potentially conflicting | P0 for WTUA-02 | Keep this for the router, but add explicit boundary: the no-mutation rule applies to deterministic surface routing, not to a separately modeled, confirmed human chat tool plan with backend tool execution. |
| `skills-pack/docs/requirements-to-workstream-development-process.md` | Excellent workstream-to-governed-tool sequence; defines browser-tool/agent-tool/internal-tool and required capability/tool fields. | needs refinement | P1 for WTUA-02 / WTUA-06 | Add human chat tool-plan adapter to the canonical requirements process and pending-task vertical contract fields. |
| `skills-pack/docs/current-intent-model.md` | Search hits show human-backed actor exposure and tool/capability realization paths. | needs refinement | P1 for WTUA-02 / WTUA-03 | Later app-description/intent tasks should ensure current-intent graph can bind one governed tool to surface, human chat plan, and agent-tool adapters without duplicating semantics. |
| `skills-pack/docs/intent-compiler.md` and `skills-pack/docs/intent-to-realization-flow.md` | Mentioned as canonical routing sources; likely need traceability wording once doctrine is updated. | needs refinement | P2 for WTUA-02 / WTUA-03 | Search/repair after canonical docs updated so intent nodes can represent tool catalog, actor adapters, confirmation, trace, and validation. |
| `skills-pack/docs/workstream-expertise-model.md` | Says functional agent is not ready with prompt/chat/tools alone; requires governed expertise, manifests, tool boundary, traces. | aligned | P2 for WTUA-04 / WTUA-05 | Add familiarity with human chat tool-plan protocol and warning that expertise text does not grant authority. |
| `skills-pack/docs/workstream-ui-reference-architecture.md` | Aligns stream request/action-feedback/result surfaces and action rendering with confirmation, idempotency, browser-tool/governed-tool/capability mapping. | needs refinement | P2 for WTUA-05 | Add chat-mediated plan review/confirmation/result/partial-failure surface UX. |
| `skills-pack/docs/web-ui-ux-patterns.md`, `web-ui-api-contract-patterns.md`, `web-ui-quality-checklist.md`, `web-ui-frontend-decomposition.md`, `web-ui-docs-index.md` | UI docs already require browser-tools, capability-backed protected actions, confirmations, denials, trace links. | needs refinement | P2 for WTUA-05 | Add human-chat plan review, confirmation, partial failure, and result-surface requirements where UI/app shell guidance is edited. |
| `skills-pack/docs/governed-agent-substrate.md`, `agent-runtime-invocation-pattern.md`, `agent-component-selection-guide.md`, `agent-coverage-matrix.md` | Governed runtime and model/tool path docs likely align with ToolPermissionBoundary and traces. | needs refinement | P1 for WTUA-04 | Ensure they distinguish governed workstream tool ids from Akka `@FunctionTool` exposure and cover human-requested/agent-planned/confirmed execution. |
| `skills-pack/docs/autonomous-agent-worker-runtime-pattern.md` and `autonomous-agents-api-notes.md` | Autonomous worker docs mention tool boundaries/approval/provider fail-closed. | aligned | P2 for WTUA-04 | Only edit if later agent-skill pass finds tool catalog or trace-source gaps. |
| `skills-pack/docs/core-ai-first-saas-foundation.md`, `core-saas-identity-tenancy-admin.md`, `core-saas-owner-tenant-billing.md`, `foundation-layer-coverage-matrix.md`, `full-core-foundation-readiness.md`, `security-*` docs | Foundation/security docs enforce auth, invitations, admin/audit, ToolPermissionBoundary, approval, traces. | aligned | P2 for WTUA-05 / WTUA-07 | Preserve security language. Add cross-reference only if needed to avoid implying prompt/chat grants authority. |
| `skills-pack/docs/examples/**` and example domain docs | Many examples already mention browser-tool, agent-tool, human-confirmed agent-tool, no mutation, and confirmation/approval. | needs refinement | P2 for WTUA-06 | Use examples to validate wording after canonical docs; update only examples that imply surface-only mutation or lack shared governed tool ids. |

## Focused skills findings

| File/family | Current signal | Classification | Edit priority | Notes for later tasks |
|---|---|---:|---:|---|
| `skills-pack/skills/README.md` | Routing map already emphasizes workstream → surface routing → governed-tools → capabilities and focused skill families. | needs refinement | P1 for WTUA-05 / WTUA-06 | Add the shared tool/actor-adapter doctrine to canonical handoff order without expanding default skill loading. |
| `skills-pack/skills/capability-first-backend/SKILL.md` | Mirrors capability doctrine and exposure channels; strong backend enforcement. | needs refinement | P0 for WTUA-05 | Add human-chat tool-plan adapter and transaction boundary expectations to output expectations/tests. |
| `skills-pack/skills/agent-workstream-apps/SKILL.md` | Already frames functional agents, workstreams, structured surfaces, tools, policies, traces. | needs refinement | P0 for WTUA-05 | Add workstream tool catalog owned by functional agent plus surface/human-chat/agent-tool adapters and confirmation protocol. |
| `skills-pack/skills/akka-agent-tool-boundaries/SKILL.md` | Strong ToolPermissionBoundary enforcement before tool registration/invocation. | aligned | P0 for WTUA-04 | Add human-requested agent-planned tool execution as a runtime mode/source that still checks boundary and records requestedBy/confirmation. |
| `skills-pack/skills/akka-agents/SKILL.md`, `akka-agent-tools/SKILL.md`, `akka-agent-component-tools/SKILL.md`, `akka-agent-mcp-tools/SKILL.md`, `akka-agent-work-trace/SKILL.md`, `akka-agent-testing/SKILL.md` | Tool mechanics and trace skills mention FunctionTool/MCP/component tools, approvals, denials, and traces. | needs refinement | P0 for WTUA-04 | Distinguish governed workstream tool from Akka tool exposure; cover human chat plan proposal/confirmation/execution/partial-failure trace. |
| `skills-pack/skills/akka-agent-governed-documents/SKILL.md`, `akka-agent-skill-governance/SKILL.md`, `akka-agent-reference-governance/SKILL.md`, `akka-agent-behavior-*`, `akka-agent-model-governance` | Governed behavior docs protect prompts/skills/references/tool grants. | aligned | P1 for WTUA-04 | Touch only where needed to state prompt/skill text cannot expand the workstream tool catalog or bypass confirmation. |
| `skills-pack/skills/app-descriptions/SKILL.md`, `app-generate-app/SKILL.md` | Route broad generated SaaS work through workstreams, surfaces, capabilities, UI. | needs refinement | P0 for WTUA-03 | Require current-intent artifacts to record governed tool id once and bind it to surface action, human-chat plan adapter, agent-tool, and other exposures. |
| `skills-pack/skills/app-description-functional-agent-modeling/SKILL.md` | Defines functional agents as vertical application areas, not chat sessions; references surface routing. | needs refinement | P0 for WTUA-03 | Add workstream agent bounded tool catalog and actor adapters as required modeling outputs. |
| `skills-pack/skills/app-description-surface-modeling/SKILL.md` | Requires prefill-only routes and no mutation for deterministic surface routing. | potentially conflicting | P0 for WTUA-03 | Preserve for routing, but clarify this does not prohibit confirmed chat tool execution via separate governed tool-plan adapter. |
| `skills-pack/skills/app-description-capability-modeling/SKILL.md`, `app-description-auth-security/SKILL.md`, `app-description-observability/SKILL.md`, `app-description-test-specification/SKILL.md`, `app-description-readiness-assessment/SKILL.md` | Capability/security/observability/test skills mention tools, data, approvals, traces. | needs refinement | P1 for WTUA-03 | Add fields/tests for actor adapter, confirmation, idempotency, transaction boundary, partial failure, `requestedBy`, and trace source. |
| `skills-pack/skills/ai-first-saas/SKILL.md` | Strong anti-chatbot and foundation routing; preserves capability-first modeling. | needs refinement | P1 for WTUA-05 | Add accepted human chat tool-plan path while preserving anti-chatbot rule. |
| `skills-pack/skills/ai-first-saas-ui-surfaces/SKILL.md` | Says do not make chat the primary control surface for consequential actions. | potentially conflicting | P0 for WTUA-05 | Rephrase to: structured surfaces remain the default/primary, but confirmed human chat tool plans are allowed when governed and produce review/result surfaces. |
| `skills-pack/skills/ai-first-saas-worker-decomposition/SKILL.md`, `ai-first-saas-audit-trace/SKILL.md`, `ai-first-saas-decision-cards/SKILL.md`, `ai-first-saas-policy-governance/SKILL.md`, `ai-first-saas-admin-agents/SKILL.md` | Worker, trace, decision, policy, admin skills already carry authority/approval/traces. | needs refinement | P1 for WTUA-05 | Add human-backed chat adapter as source/worker path and ensure decision/approval remains distinct from user confirmation. |
| `skills-pack/skills/core-saas-foundation/SKILL.md` | Foundation security and SaaS Foundation App guidance. | aligned | P2 for WTUA-05 | Cross-reference only if necessary. |
| Akka component families: `akka-http-*`, `akka-grpc-*`, `akka-mcp-*`, `akka-workflow-*`, `akka-consumers`, `akka-timed-*`, `akka-views`, `akka-*-entities` | Many already describe routes/consumers/timers/workflows as exposure channels for capabilities with auth/idempotency/audit. | aligned | P2 for WTUA-07 | Likely no broad edits needed; consistency pass should only repair terminology drift. |
| `skills-pack/skills/akka-backlog-to-pending-tasks/SKILL.md`, `akka-backlog-item-to-task-brief/SKILL.md`, `akka-prd-to-specs-backlog/SKILL.md`, `project-discussed-idea-to-pending-project/SKILL.md`, pending queue/question skills | Planning skills likely need vertical contract fields carried into task briefs. | needs refinement | P0 for WTUA-06 | Add governed tool ids, actor adapters, confirmation/approval, transaction/idempotency, trace, result surface, and validation evidence requirements. |
| Stage 1 business intent skills | They intentionally avoid workstream/tool decomposition. | out of scope | P3 | No edit unless they accidentally promote chat/tool implementation. |

## Templates, examples, and tools findings

| Path/family | Current signal | Classification | Edit priority | Notes for later tasks |
|---|---|---:|---:|---|
| `skills-pack/templates/ai-first-saas-core-app/app-description/12-workstreams/workstream-manifest.json` | Template manifest already contains workstream/surface/capability/governed-tool mapping concepts. | needs refinement | P0 for WTUA-06 | Add or verify fields for bounded workstream tool catalog, actor adapters, human chat confirmation, idempotency/transaction, and trace source. |
| `skills-pack/templates/ai-first-saas-core-app/app-description/12-workstreams/surface-contracts/*.md` | Surface contracts include browser-tool/governed-tool/capability mappings and some agent-tool exposures. | aligned | P1 for WTUA-06 | Update only representative contracts if canonical fields change. User Admin contract is the likely best example for human chat invitation flow. |
| `skills-pack/templates/ai-first-saas-core-app/app-description/12-workstreams/workstream-expertise/*.md` | Workstream expertise files name tool boundaries and human confirmation/approval in places. | needs refinement | P1 for WTUA-06 | Add agent familiarity with human chat tool-plan protocol and refusal to execute unconfirmed/out-of-catalog tools. |
| `skills-pack/templates/ai-first-saas-core-app/app-description/70-traceability/*.md` | Traceability maps connect surfaces/capabilities. | needs refinement | P1 for WTUA-06 | Add actor adapter/source columns if not already present. |
| `skills-pack/templates/ai-first-saas-core-app/app-description/55-ui/*.md` | UI templates describe structured surface rendering and style. | needs refinement | P2 for WTUA-06 | Add review/confirmation/result surface semantics for chat tool plans if canonical UI fields change. |
| `skills-pack/examples/web-ui/ai-first-workstream-enterprise/*.html` | UI examples show command strips, approval surfaces, system messages, dashboards. | needs refinement | P2 for WTUA-06 | Consider one example showing a proposed chat tool plan, confirmation, and partial-failure result surface. |
| `skills-pack/examples/akka-components/**` | Example runtime code includes ToolPermissionBoundary, WorkstreamRuntimeAgent, AgentRuntimeService, traces, and no-direct-mutation tests. | aligned | P2 for WTUA-06 / WTUA-07 | Update only if docs add a concrete example contract; avoid runtime-example churn in this docs alignment project unless contradictions are found. |
| `skills-pack/examples/ai-first-semantic-slices/**`, `skills-pack/examples/workforce-decomposition/**` | Concept examples mention decisions/traces/workers. | out of scope | P3 | Search during consistency repair only. |
| `skills-pack/tools/validate-workstream-contracts.sh`, `validate-workstream-manifest.py`, `validate-surface-contracts.sh`, `validate-pending-task-workstream-contract.sh` | Validators check workstream/surface/action/capability/governed-tool structure. | needs refinement | P0 for WTUA-06 | Add lightweight checks for governed tool id, actor adapter/exposure channel, confirmation/approval, idempotency/transaction, trace source when fields are introduced. |
| `skills-pack/tools/validate-runtime-completion-evidence.py` | Runtime evidence validator, less directly about tool-use modeling. | out of scope | P3 | Keep unless planning/queue evidence schema changes. |

## Prioritized file lists for later tasks

### WTUA-02: canonical doctrine updates

Priority order:

1. `skills-pack/docs/agent-workstream-application-architecture.md`
2. `skills-pack/docs/capability-first-backend-architecture.md`
3. `skills-pack/docs/workstream-surface-intent-routing.md`
4. `skills-pack/docs/workstream-contract.md`
5. `skills-pack/docs/structured-surface-contracts.md`
6. `skills-pack/docs/ai-first-saas-application-architecture.md`
7. `skills-pack/docs/requirements-to-workstream-development-process.md`
8. `skills-pack/docs/current-intent-model.md`
9. `skills-pack/docs/intent-compiler.md`
10. `skills-pack/docs/intent-to-realization-flow.md`
11. `skills-pack/docs/workstream-expertise-model.md`
12. `skills-pack/docs/workstream-ui-reference-architecture.md`

Canonical edits should establish these terms before skill-family edits: `governed workstream tool`, `workstream tool catalog`, `human surface action/browser-tool adapter`, `human_chat_tool_plan` or equivalent, `agent-tool adapter`, `requestedBy`, `trace source`, `confirmation bound to plan`, `transaction boundary per tool invocation`, and `partial failure/result surface`.

### WTUA-03: app-description and intent skills

Priority order:

1. `skills-pack/skills/app-descriptions/SKILL.md`
2. `skills-pack/skills/app-description-functional-agent-modeling/SKILL.md`
3. `skills-pack/skills/app-description-surface-modeling/SKILL.md`
4. `skills-pack/skills/app-description-capability-modeling/SKILL.md`
5. `skills-pack/skills/app-description-auth-security/SKILL.md`
6. `skills-pack/skills/app-description-observability/SKILL.md`
7. `skills-pack/skills/app-description-test-specification/SKILL.md`
8. `skills-pack/skills/app-description-readiness-assessment/SKILL.md`
9. `skills-pack/skills/app-description-ui/SKILL.md`
10. `skills-pack/skills/app-generate-app/SKILL.md`
11. `skills-pack/docs/app-description-skill-output-contracts.md`
12. `skills-pack/docs/current-intent-model.md` follow-up if canonical task did not cover graph fields.

### WTUA-04: agent, tool-boundary, and trace skills

Priority order:

1. `skills-pack/skills/akka-agent-tool-boundaries/SKILL.md`
2. `skills-pack/skills/akka-agents/SKILL.md`
3. `skills-pack/skills/akka-agent-tools/SKILL.md`
4. `skills-pack/skills/akka-agent-component-tools/SKILL.md`
5. `skills-pack/skills/akka-agent-mcp-tools/SKILL.md`
6. `skills-pack/skills/akka-agent-work-trace/SKILL.md`
7. `skills-pack/skills/akka-agent-testing/SKILL.md`
8. `skills-pack/skills/akka-agent-governed-documents/SKILL.md`
9. `skills-pack/skills/akka-agent-skill-governance/SKILL.md`
10. `skills-pack/skills/akka-agent-reference-governance/SKILL.md`
11. `skills-pack/skills/akka-agent-behavior-profiles/SKILL.md`
12. `skills-pack/skills/akka-agent-behavior-editing/SKILL.md`
13. `skills-pack/skills/akka-autonomous-agent-governance/SKILL.md`
14. `skills-pack/skills/akka-autonomous-agent-tasks/SKILL.md`

### WTUA-05: workstream, SaaS, and UI skills

Priority order:

1. `skills-pack/skills/agent-workstream-apps/SKILL.md`
2. `skills-pack/skills/capability-first-backend/SKILL.md`
3. `skills-pack/skills/ai-first-saas-ui-surfaces/SKILL.md`
4. `skills-pack/skills/ai-first-saas/SKILL.md`
5. `skills-pack/skills/ai-first-saas-worker-decomposition/SKILL.md`
6. `skills-pack/skills/ai-first-saas-audit-trace/SKILL.md`
7. `skills-pack/skills/ai-first-saas-decision-cards/SKILL.md`
8. `skills-pack/skills/ai-first-saas-policy-governance/SKILL.md`
9. `skills-pack/skills/core-saas-foundation/SKILL.md`
10. `skills-pack/skills/akka-web-ui-apps/SKILL.md`
11. `skills-pack/skills/akka-web-ui-ux-design/SKILL.md`
12. `skills-pack/skills/akka-web-ui-state-rendering/SKILL.md`
13. `skills-pack/skills/akka-web-ui-forms-validation/SKILL.md`
14. `skills-pack/skills/akka-web-ui-accessibility-responsive/SKILL.md`
15. `skills-pack/skills/README.md`

### WTUA-06: planning, templates, examples, and validation assets

Priority order:

1. `skills-pack/skills/akka-prd-to-specs-backlog/SKILL.md`
2. `skills-pack/skills/akka-solution-decomposition/SKILL.md`
3. `skills-pack/skills/akka-backlog-to-pending-tasks/SKILL.md`
4. `skills-pack/skills/akka-backlog-item-to-task-brief/SKILL.md`
5. `skills-pack/skills/project-discussed-idea-to-pending-project/SKILL.md`
6. `skills-pack/skills/akka-pending-task-queue-maintenance/SKILL.md`
7. `skills-pack/docs/pending-task-queue.md`
8. `skills-pack/docs/pending-question-queue.md`
9. `skills-pack/templates/ai-first-saas-core-app/app-description/12-workstreams/workstream-manifest.json`
10. `skills-pack/templates/ai-first-saas-core-app/app-description/12-workstreams/surface-contracts/03-user-admin-user-list.md`
11. `skills-pack/templates/ai-first-saas-core-app/app-description/12-workstreams/workstream-expertise/user-admin-agent.md`
12. `skills-pack/templates/ai-first-saas-core-app/app-description/70-traceability/surface-to-capability-map.md`
13. `skills-pack/tools/validate-workstream-manifest.py`
14. `skills-pack/tools/validate-workstream-contracts.sh`
15. `skills-pack/tools/validate-surface-contracts.sh`
16. `skills-pack/tools/validate-pending-task-workstream-contract.sh`
17. `skills-pack/examples/web-ui/ai-first-workstream-enterprise/system-message-states.html`
18. `skills-pack/examples/web-ui/ai-first-workstream-enterprise/user-admin-list.html`
19. `skills-pack/examples/akka-components/src/main/java/ai/first/application/foundation/agent/AgentRuntimeService.java` only if example code comments/tests conflict with final doctrine.

### WTUA-07: consistency repair pass

Search and repair only concrete contradictions after WTUA-02 through WTUA-06. Recommended search patterns:

- `direct command authority`
- `future separately-governed agent tools`
- `must not submit`
- `Do not make chat the primary control surface`
- `agent tools` near `surface action`
- `surface routing` near `no mutation`
- `prompt text` near `authority`
- `human-confirmed agent-tool`
- `ToolPermissionBoundary`
- `requestedBy`

Likely repair targets if still inconsistent:

1. `skills-pack/docs/workstream-surface-intent-routing.md`
2. `skills-pack/skills/app-description-surface-modeling/SKILL.md`
3. `skills-pack/skills/ai-first-saas-ui-surfaces/SKILL.md`
4. `skills-pack/docs/workstream-ui-reference-architecture.md`
5. `skills-pack/docs/examples/**`
6. `skills-pack/templates/**/workstream-expertise/*.md`
7. `skills-pack/examples/**` comments or README snippets

## Residual risks for this audit

- Search output is broad and terminology-based; later edits should still review each targeted file before changing wording.
- Some files classified `aligned` may still need small cross-references after canonical doctrine changes to prevent drift.
- The precise runtime implementation pattern for confirmed human chat tool execution remains intentionally future-facing; this mini-project should align guidance/contracts, not implement root app runtime behavior.
