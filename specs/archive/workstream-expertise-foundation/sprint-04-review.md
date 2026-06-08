# Sprint 04 Review: Planning Integration for Workstream Expertise

## Review outcome

Sprint 04 is complete. The planning guidance can create self-contained workstream expertise tasks for a new domain-specific functional agent without collapsing the work into vague `make the agent expert` or generic `agent governance` items.

Sprint 05 may proceed to final consistency review.

## Evidence reviewed

- `skills/akka-prd-to-specs-backlog/SKILL.md` now requires PRD/spec/backlog generation to extract or plan each functional agent's workstream expert bundle, including prompt intent, procedural `SkillDocument` entries, factual/process `ReferenceDocument` entries, compact `AgentSkillManifest` and `AgentReferenceManifest`, `ToolPermissionBoundary`, authorized `readSkill` and `readReferenceDoc` loaders, traces, seed/import expectations, UI surfaces, and tests.
- `docs/module-sprint-planning.md` requires sprint and pending-task shapes to preserve the workstream expertise increment for new or changed functional agents and to reject runnable tasks that name only a component, dashboard, module, or vague expertise work without the vertical contract.
- `docs/pending-task-queue.md` now defines bounded task families for governed runtime agent foundation work and for every materially changed domain-specific functional agent with LLM behavior.
- `docs/solution-plan-to-implementation-queue.md` requires implementation queues to carry workstream expert bundle scope and split prompt, skill, reference, manifest, boundary, loader, UI/governance, trace, and test work when one task would be too broad.
- `docs/workstream-expertise-model.md` provides the canonical bundle contract that these planning artifacts preserve.

## Sample validation: domain-specific workstream

Hypothetical input: a generated SaaS app adds a `Customer Success Renewal Agent` that helps customer-success managers prepare renewal-risk reviews, gather account evidence, draft next actions, and escalate risky discounts or commitments for approval.

The current planning guidance would produce a runnable task sequence like this rather than one broad agent task:

### Sample task outline

1. **Define Customer Success Renewal expert bundle**
   - functional agent: `customer-success-renewal-agent`
   - surface/actions: renewal-risk dashboard, account evidence card, renewal decision card
   - capability ids/classes: renewal risk query, account evidence summary, renewal action proposal, approval-gated discount escalation
   - expertise scope: bundle id, prompt intent, procedural skill families, reference families, capability map, authority profile, denial rules, surfaces, traces, governance owner, tests
   - output: app-description workstream expertise file and traceability links
   - checks: docs/spec validation and `git diff --check`

2. **Seed default renewal expertise content**
   - expertise scope: default prompt, renewal-risk triage skill, account-evidence synthesis skill, escalation-drafting skill, renewal-policy reference, pricing/discount reference, customer-health reference, compact skill/reference manifests, provenance/checksum expectations
   - output: governed seed resources or fixtures; no direct bypass of governed storage
   - checks: seed manifest/fixture contract tests and `git diff --check`

3. **Implement or plan loader and boundary enforcement**
   - expertise scope: `readSkill(skillId)`, `readReferenceDoc(referenceId)`, separate `read_skill` and `read_reference` grants, assigned-load authorization, unassigned/cross-tenant/inactive/missing-boundary denials, token/redaction limits
   - authority: skill/reference text cannot grant discount authority, customer scope, approval rights, or tool access
   - output: runtime guidance/code task for loader/boundary behavior
   - checks: assigned and denied load tests, boundary denial tests, trace emission tests

4. **Add expertise governance UI surfaces**
   - surfaces: compact manifest summary, reference evidence drawer, denied-load history, trace links, steward review state, approval decision card
   - output: frontend/API/realtime tasks anchored to the selected style guide and capability-backed actions
   - checks: rendering, forbidden-state, trace-link, and frontend secret-boundary tests

5. **Add expertise test pack**
   - tests: assigned skill/reference loads, unassigned denials, missing-boundary denials, no authority expansion from prompt/skill/reference text, tenant/customer isolation, approval-gated side effects, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, and `AgentWorkTrace` visibility
   - output: contract/runtime tests linked to the backlog and pending queue
   - checks: targeted test command plus `git diff --check`

This sample is self-contained enough for fresh-session execution because each task has a functional agent, surface/action or explicit non-UI trigger, capability context, AuthContext/authority rules, expertise artifacts, selected runtime/UI concern, expected outputs, and checks. It also preserves the doctrine distinction between skills, references, capabilities, tools, boundaries, traces, and tests.

## Remaining refinement areas

No Sprint 05 blocker was found.

Refinements to keep visible for final consistency review:

1. Search for older planning or readiness text that still treats a functional agent as ready with only a prompt, generic chat surface, or broad tool list.
2. Search for stale chatbot/minimum-starter wording that could route new domain workstreams away from functional-agent, surface, capability, and expertise-task planning.
3. Confirm that all entry routing paths name `akka-agent-reference-governance` where reference-document work is the dominant task, not only `akka-agent-skill-governance`.

## Queue adjustments

No new pending tasks are required before Sprint 05. Existing `TASK-WEF-05-001` should consume this review and run the final consistency review.

## Checks

Required check:

```text
git diff --check
```

Text-search proof for this review should cover workstream expertise tasks, self-contained fresh-session execution, `ReferenceDocument`, `AgentReferenceManifest`, `readReferenceDoc`, `ReferenceLoadTrace`, and rejection of vague `make the agent expert` / `agent governance` tasks.
