---
name: ai-first-saas-intake
description: Assess PRDs, specs, meeting notes, rough product ideas, or existing SaaS requirements to decide whether a product should be treated as ai-first SaaS and identify the next framework skills to invoke.
---

# ai-first-saas-intake

Use this skill when consuming product requirements, notes, PRDs, existing SaaS descriptions, or early product ideas and deciding whether the work should follow the ai-first SaaS framework.

Reference: `docs/ai-first-saas-coding-agent-framework.md` is the canonical conceptual contract and shared vocabulary. Use it for definitions of ai-first SaaS, human governance roles, delegated work, required surfaces, and the ai-first operating loop.

## Purpose

Determine whether the product is:

1. **AI-first SaaS**: agents perform operational work under explicit policies while humans supervise, decide exceptions, teach, and audit;
2. **Conventional SaaS with AI features**: humans remain the primary operators and AI assists with isolated generation, summarization, search, or recommendations;
3. **Not enough information yet**: the requirements do not clarify delegation, authority, risk, governance, or durable agentic objects.

The intake result should guide scoping, risk posture, UI surfaces, object modeling, governance, and the next skill(s) a coding agent should load.

## Intake procedure

### 1. Identify source material and assumptions

- Name the source inputs reviewed: PRD, spec, notes, customer interview, existing app, issue, or rough idea.
- Separate stated requirements from inferred assumptions.
- Flag missing information that affects ai-first classification.

### 2. Extract the product objective

Capture:

- target users and organizations;
- primary business outcome;
- recurring operational work the product supports;
- success criteria or metrics;
- current manual workflow, if described.

### 3. Identify delegated operational work

Ask:

- What work is expected to be performed by agents rather than humans?
- Is the agent expected to plan, decide, act, communicate, monitor, reconcile data, or only draft/summarize?
- Does the agent work on durable goals over time, or only answer one-off prompts?
- What tools, systems, data, or external side effects would the agent need?
- What decisions can the agent make autonomously?
- What decisions must remain human-owned?

Classify delegated work as:

- `none`: no operational delegation;
- `assistive`: AI drafts, summarizes, searches, or suggests while humans execute;
- `bounded_operations`: agents execute low-risk tasks under rules;
- `supervised_autonomy`: agents plan and act, with review/approval/escalation gates;
- `high_stakes_autonomy`: agents affect regulated, financial, safety, legal, privacy, employment, healthcare, or other high-risk outcomes.

### 4. Identify human governance roles

Map required human users to framework roles:

- Intent Author: defines goals and success criteria;
- Supervisor: monitors active autonomous work;
- Reviewer / Approver: decides high-impact or uncertain actions;
- Exception Handler: resolves blocked, ambiguous, or risky cases;
- Policy Owner / Coach: changes policies, examples, thresholds, and skills;
- Auditor: investigates why work happened and whether it was authorized;
- Outcome Owner: validates business value and outcomes.

For each role, note whether it is explicit in the source material, implied, or missing.

### 5. Identify policy, approval, and audit needs

Ask:

- What policies, rules, contracts, regulations, customer commitments, brand constraints, or internal standards apply?
- Which actions require approval before execution?
- Which events must be audit-complete?
- What evidence must be available for review?
- What trace must be retained for later accountability?
- Are data privacy, tenant isolation, PII, security, or retention obligations implied?
- Can human corrections become future examples, precedents, thresholds, or policy updates?

### 6. Recommend maturity and risk posture

Choose an initial implementation posture:

- `prototype`: explore the loop with limited data, mocked tools, and explicit human review;
- `mvp`: durable goals, basic policies, approval gates, traces, and core surfaces for bounded use;
- `production`: versioned agents/prompts/skills/policies, enforceable permissions, audit events, tests, and replay for important changes;
- `regulated_or_high_risk`: strict approval gates, retention/redaction, policy provenance, detailed replay/simulation, adversarial testing, and compliance-grade audit.

Explain why. Do not overbuild low-risk assistive products, but do not under-scope systems where agents perform consequential work.

### 7. Determine likely required surfaces

Map needs to the six framework surfaces:

- Goal-to-Execution Workbench: needed when humans delegate durable objectives or approve plans;
- Command Center / Mission Control: needed when humans supervise ongoing autonomous work;
- Deviation Review / Decision Card: needed for approvals, exceptions, policy deviations, or high-stakes recommendations;
- Agent Skills / Policy / Governance / Learning Center: needed when prompts, policies, skills, examples, or thresholds must be governed;
- Async Digest / Executive Briefing: needed when agents perform background work while humans are away;
- Audit and Work Trace: needed whenever accountability, evidence, data access, tool use, or decision provenance matter.

If the product only needs a subset initially, state what is required now vs later.

### 8. Decide ai-first applicability

Use these decision rules:

- Treat as **ai-first SaaS** if agents own operational work over time, act under policy, require human governance, and produce auditable outcomes.
- Treat as **conventional SaaS with AI features** if AI only assists humans inside otherwise manual CRUD/workflow screens and does not need durable goals, execution plans, approval gates, policy-bound authority, or audit traces.
- Treat as **hybrid / candidate ai-first** if the current spec is conventional but contains a clear path to delegated operational work.
- Treat as **insufficient information** if delegation, authority, risk, and human governance are not described.

## Required output format

Produce this structured intake assessment:

```yaml
agent_first_saas_intake:
  product_name: string
  source_material_reviewed:
    - string
  one_sentence_summary: string
  classification: agent_first_saas | conventional_saas_with_ai_features | hybrid_candidate | insufficient_information
  confidence: high | medium | low

  rationale:
    why_this_classification: string
    agent_first_signals:
      - string
    conventional_ai_feature_signals:
      - string
    missing_or_ambiguous_information:
      - string

  delegated_operational_work:
    level: none | assistive | bounded_operations | supervised_autonomy | high_stakes_autonomy
    work_items:
      - work: string
        currently_human_owned: boolean
        proposed_agent_role: plan | decide | act | monitor | communicate | analyze | recommend | other
        side_effects: none | internal_state_change | external_communication | financial | legal_compliance | customer_impacting | safety_or_health | other
        autonomy_candidate: auto | review | approval | escalate | not_enough_info

  human_governance_roles:
    - role: intent_author | supervisor | reviewer_approver | exception_handler | policy_owner_coach | auditor | outcome_owner
      need: explicit | implied | missing | not_needed
      responsibilities:
        - string

  policy_approval_audit_needs:
    policies_or_rules_implied:
      - string
    approval_gates_needed:
      - string
    audit_or_trace_requirements:
      - string
    sensitive_data_or_security_concerns:
      - string
    learning_or_policy_update_paths:
      - string

  maturity_recommendation:
    level: prototype | mvp | production | regulated_or_high_risk
    reason: string
    avoid_overbuilding_notes:
      - string
    do_not_skip_notes:
      - string

  likely_surfaces:
    goal_to_execution_workbench: required_now | recommended_later | not_needed | unknown
    command_center_mission_control: required_now | recommended_later | not_needed | unknown
    deviation_review_decision_card: required_now | recommended_later | not_needed | unknown
    agent_skills_policy_governance_learning_center: required_now | recommended_later | not_needed | unknown
    async_digest_executive_briefing: required_now | recommended_later | not_needed | unknown
    audit_and_work_trace: required_now | recommended_later | not_needed | unknown
    notes:
      - string

  next_skills_to_invoke:
    - skill: ai-first-saas-maturity-model | ai-first-saas-object-model | ai-first-saas-agent-team-design | ai-first-saas-policy-governance | ai-first-saas-ui-surfaces | ai-first-saas-decision-cards | ai-first-saas-audit-trace | ai-first-saas-runtime-orchestration | ai-first-saas-permission-enforcement | ai-first-saas-replay-simulation | ai-first-saas-security-privacy | ai-first-saas-testing-evaluation | ai-first-saas-backlog-decomposition | ai-first-saas-outcomes-metrics | ai-first-saas-risk-confidence-calibration | ai-first-saas-curation-digest | ai-first-saas-conversation-to-durable-objects | ai-first-saas-worked-example
      reason: string

  clarification_questions:
    - string
```

## Applicability checklist

Before concluding a product is ai-first, verify at least several of these are true:

- [ ] There are durable goals or objectives for agents to pursue.
- [ ] Agents plan or execute work, not only generate text.
- [ ] Agents need tools, data scopes, or action permissions.
- [ ] Some actions are autonomous and others require review, approval, or escalation.
- [ ] Human decisions can become precedents, examples, policy changes, or threshold changes.
- [ ] Work must be traceable to agents, policies, evidence, tools, and outcomes.
- [ ] The UI must support supervision, decision, teaching, catch-up, or audit.

If most answers are false, recommend a conventional SaaS design with narrow AI assistance instead of forcing the ai-first substrate.

## Intake questions to ask stakeholders

Use these when source material is incomplete:

1. What operational work should the product take off a human's plate?
2. Which agent actions should be automatic, reviewed after the fact, blocked for approval, or escalated?
3. Which decisions must always remain human-owned?
4. What policies, rules, contracts, regulatory requirements, or brand constraints govern the work?
5. What evidence would a human need before approving a recommendation?
6. What should happen when the agent is uncertain, blocked, or outside its authority?
7. What must be auditable later, and for whom?
8. What sensitive data, tenant boundaries, or security constraints apply?
9. How should human corrections affect future agent behavior?
10. What outcomes prove the agent labor is producing value?
```
