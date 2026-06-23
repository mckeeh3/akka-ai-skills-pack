---
name: akka-agent-behavior-editing
description: Implement AgentBehaviorEditorAgent flows for governed prompt, skill, manifest, tool-boundary, policy, rubric, and example changes with structured proposals, proposed diff review, risk classification, draft versions, decision-card routing, and authority-expansion denial.
---

# Akka Agent Behavior Editing

Use this skill when generated AI-first SaaS apps need an agent-mediated maintenance path for runtime agent behavior: prompts, skills, skill manifests, tool permission boundaries, policies, rubrics, reference examples, or related governed documents.

This skill defines the `AgentBehaviorEditorAgent` responsibility and proposal contract. It does not replace focused governance skills; route to `akka-agent-prompt-governance`, `akka-agent-skill-governance`, `akka-agent-governed-documents`, `akka-agent-tool-boundaries`, `akka-agent-model-governance`, `ai-first-saas-policy-governance`, and the target artifact implementation skill as needed.

## Required reading

Read these first if present:
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/governed-agent-substrate.md`
- `../docs/agent-runtime-invocation-pattern.md`
- `../akka-agents/SKILL.md`
- `../akka-agent-behavior-profiles/SKILL.md`
- `../akka-agent-governed-documents/SKILL.md`
- `../akka-agent-prompt-governance/SKILL.md`
- `../akka-agent-skill-governance/SKILL.md`
- `../akka-agent-structured-responses/SKILL.md`
- `../akka-agent-testing/SKILL.md`
- `../ai-first-saas-decision-cards/SKILL.md`
- `../ai-first-saas-policy-governance/SKILL.md`

## Use when the request mentions

- `AgentBehaviorEditorAgent`, editing agent, behavior editor, or agent-mediated maintenance
- proposed diff, draft version, review/approval, or behavior-change proposal
- prompt, skill, manifest, tool-boundary, policy, rubric, or example changes drafted by an agent
- authority expansion, tool expansion, data-access expansion, or approval-boundary change detection
- proposal queues, behavior-change decision cards, diff review UI, or test/replay suggestions

## Responsibility boundary

`AgentBehaviorEditorAgent` may:
- interpret a human behavior-change request in the caller's tenant-scoped `AuthContext`;
- identify affected `AgentDefinition`, `PromptDocument`, `SkillDocument`, `AgentSkillManifest`, `ToolPermissionBoundary`, policy, rubric, or example records;
- draft a structured proposed diff and rationale;
- classify risk and flag authority expansion attempts;
- suggest tests, replay cases, and evidence to review;
- create draft versions or proposal records through governed backend capabilities;
- route proposals to review/approval or a decision card.

It must not:
- directly mutate active prompt, skill, manifest, tool-boundary, policy, or agent authority state;
- grant tool, data, tenant/customer, role, scope, approval, model authority, or confirmed human chat execution through text;
- bypass backend authorization, review, activation, plan confirmation, trace, or tenant-isolation checks;
- approve its own consequential authority expansion unless a narrow product policy explicitly allows it.

## Structured output contract

Use `akka-agent-structured-responses` for a typed proposal response. Keep the schema small and stable:

```text
BehaviorChangeProposal
- proposalId or clientRequestId
- tenantId
- requestedByAccountId
- targetAgentDefinitionIds[]
- affectedDocuments[]
  - artifactType: prompt | skill | manifest | tool_boundary | policy | rubric | example | agent_definition
  - artifactId
  - currentVersion or currentRef
  - proposedVersionKind: draft | manifest_proposal | boundary_proposal | policy_proposal
- proposedDiffs[]
  - artifactId
  - diffFormat: unified | json_patch | summary
  - proposed diff
  - human-readable summary
- rationale
- riskClassification: low | medium | high | blocked
- authorityExpansion
  - detected: true | false
  - expansionTypes: tool | data | tenant_scope | role_scope | approval | autonomy | model | policy
  - requiredApprovals[]
  - denialReason when blocked
- validationFindings[]
- testSuggestions[]
- replaySuggestions[]
- decisionCardRequired: true | false
- recommendedNextAction: create_draft | request_review | create_decision_card | deny | ask_clarification
```

Prefer returning proposal intent first, then calling governed capabilities to create draft versions or proposal records. Never expose provider secrets, hidden platform instructions, or cross-tenant content in the structured response.

## Normal flow

```text
human behavior-change request
→ authorize caller and selected tenant/customer context
→ resolve active or target AgentDefinition and governed artifact metadata
→ AgentBehaviorEditorAgent drafts structured proposal and proposed diff
→ backend validates scope, lifecycle, content safety, and authority expansion
→ create draft version or proposal record, not active behavior
→ show protected diff/review UI or decision card
→ reviewer approves, rejects, requests changes, or escalates
→ activation uses governed prompt/skill/manifest/tool-boundary commands
→ emit audit/work traces for proposal, denial, review, approval, activation, and rejection
```

## Risk classification

Classify as **blocked** when the request attempts cross-tenant access, secret exfiltration, hidden-policy override, unauthorized role/scope change, or direct activation without authority.

Classify as **high** when the proposal changes governed tool catalog membership, tool/data access, human chat tool-plan authority, AI-backed `agent_tool_call` exposure, autonomy level, approval boundaries, billing/security/admin capabilities, external side effects, model/provider policy, or tenant-wide policy.

Classify as **medium** when the proposal changes core task instructions, skill content used by consequential workflows, evaluation rubrics, or reference examples that alter behavior.

Classify as **low** when the proposal is copy-editing, clarification, formatting, or non-behavioral metadata and does not affect authority, evidence, tools, policies, or outputs.

## Akka component mapping

Prefer:
- `AgentBehaviorEditorAgent` as a Java `Agent` with one structured command returning `BehaviorChangeProposal` or a narrower proposal record.
- Governed document entities for prompt, skill, policy, rubric, and example drafts.
- `AgentSkillManifestEntity` and `ToolPermissionBoundary` proposal commands for manifest/tool changes.
- Workflow for review/approval, decision-card routing, retries, deadlines, and escalation.
- Views for proposal queues, affected artifacts, review state, and risk filters.
- HTTP endpoints and web UI for protected request, proposed diff, review, approval, rejection, and activation surfaces.
- Consumers for audit/work-trace emission and trace enrichment.

## Decision-card routing

Create or require a decision card when:
- authority expansion is detected;
- risk is high;
- a tool boundary, data scope, approval boundary, model policy, role capability, or tenant/customer scope changes;
- simulation/replay evidence is required before activation;
- reviewers need alternatives, risk/impact, policy citations, and explicit approve/reject/modify actions.

Decision cards must show proposed diff, rationale, affected agents/documents, risk, impact, confidence, authority expansion flags, required approvals, test/replay suggestions, and trace links.

## Tests

Plan deterministic tests for:
- low-risk prompt draft proposal creates a draft version, not active content;
- skill proposed diff includes rationale, tests, and affected agents;
- manifest or tool-boundary authority expansion routes to decision card and approval;
- unauthorized authority expansion is denied and audited;
- disabled or archived AgentDefinition cannot receive runtime-impacting activation;
- cross-tenant target artifact is denied;
- secret-like content is blocked or escalated;
- approval activates through governed commands only;
- rejection leaves active versions unchanged;
- PromptAssemblyTrace, SkillLoadTrace, and AgentWorkTrace or audit records link the proposal, review, activation, and denial events where applicable.

Use `TestModelProvider` for the editing agent's structured proposal output. Assert backend validators deny unsafe proposals even if the model returns an apparently valid diff.

## Review checklist

Before finishing, verify:
- `AgentBehaviorEditorAgent` has a bounded responsibility and one structured output shape
- proposed diff, draft version, review/approval, and activation are separate steps
- authority expansion cannot be granted by prompt/skill/reference/manifest/tool-boundary text
- high-risk changes route to a decision card or approval workflow, including changes to confirmed `human_chat_tool_plan` or AI-backed agent-tool exposure
- governed artifact commands enforce tenant scope, lifecycle, and permissions
- proposal, denial, review, approval, activation, and rejection are audited/traced
- tests cover allowed drafts, blocked expansion, cross-tenant denial, and unchanged active behavior after rejection
