---
name: ai-first-saas-maturity-model
description: Scope ai-first SaaS implementations by maturity level so coding agents build only the durable goals, agent definitions, policies, audit, replay, security, tests, and UI surfaces appropriate for the product's risk and stage.
---

# ai-first-saas-maturity-model

Use this skill when turning an ai-first SaaS idea, PRD, intake assessment, architecture plan, or backlog into a realistic implementation scope. It prevents prototypes from being overbuilt and prevents production or high-risk systems from skipping governance, audit, security, testing, and replay foundations.

References:

- `docs/ai-first-saas-coding-agent-framework.md` is the canonical conceptual contract and vocabulary.
- `docs/skills-pack-tech-stack.md` defines the target Akka + React/Vite/TypeScript architecture used when maturity choices affect implementation planning.
- `docs/ai-first-saas-ui-patterns.md` defines screen-composition expectations for the six ai-first surfaces.

## Core rule

Choose the lowest maturity level that safely supports the delegated operational work, side effects, data sensitivity, policy obligations, and human-governance needs. Do not equate maturity with company size; a small app that sends regulated communications or moves money may need regulated/high-risk controls, while a large internal demo may remain prototype-level.

## Maturity levels

### 1. Prototype

Use for concept validation, internal demos, workflow discovery, and low-risk experiments with mocked or read-only tools.

Required capabilities:

- **Durable goals:** Persist basic `Goal` and `Objective` records with status, owner, success criteria, and assumptions.
- **Agent definitions:** Define at least one named agent or coordinator with purpose, prompt text, allowed inputs, and explicit non-authority.
- **Policies and approval gates:** Encode lightweight human-readable rules; require manual approval before external or irreversible side effects.
- **Audit events:** Record goal creation, plan generation, agent recommendation, approval, rejection, and manual action events.
- **Traces:** Store concise work summaries, source inputs, tool/data references, recommendation, and visible reasoning factors; raw chat alone is not enough.
- **Replay/simulation:** Optional dry-run or scenario notes; no deterministic replay requirement unless the demo evaluates policy changes.
- **Security and privacy:** Use test data or minimized real data; no secrets in prompts; avoid production credentials and irreversible tool access.
- **Testing/evaluation:** Smoke tests for the happy path, approval blocking, and basic prompt/output shape.
- **UI surfaces:** Minimal Goal-to-Execution Workbench, simple Decision Card/approval UI if decisions occur, and a basic trace/detail view.

Recommended capabilities:

- Mocked tool responses and seed scenarios.
- Simple policy chips showing what the agent may not do.
- Activity stream with `auto`, `approval`, or `fyi` disposition tags.

Advanced/defer:

- Full event sourcing for every object.
- Versioned skill/policy governance center.
- Historical replay, analytics-grade outcome attribution, and complex digest curation.

### 2. MVP

Use for bounded real users, constrained operations, pilot customers, or internal production where agents perform limited work under human supervision.

Required capabilities:

- **Durable goals:** Persist `Goal`, `Objective`, `ExecutionPlan`, task state, owners, constraints, success criteria, and close/cancel reasons.
- **Agent definitions:** Store versioned `AgentDefinition` records with prompt version, assigned skills or capability descriptions, tools allowed, data scopes, authority boundaries, and escalation criteria.
- **Policies and approval gates:** Maintain addressable policy documents or clauses for meaningful boundaries; block side effects behind approval gates when confidence/risk/authority rules require it.
- **Audit events:** Emit structured audit events for goal lifecycle, plan approval, agent start/finish/failure, tool invocation, data access, approval request, human decision, exception, and outcome recording.
- **Traces:** Store `WorkTrace` or decision provenance linked to goal, plan, agent, prompt/policy versions, evidence, tool calls, data access, alternatives considered where relevant, and final action.
- **Replay/simulation:** Provide no-side-effect dry runs for plans and manual replay for high-impact policy or prompt changes; deterministic fixtures are recommended but not always required.
- **Security and privacy:** Enforce tenant isolation, least-privilege tools, PII minimization, secret management, and approval before sensitive side effects.
- **Testing/evaluation:** Tests for policy evaluators, permission checks, approval gates, trace creation, basic adversarial input, and critical UI flows.
- **UI surfaces:** Implement the surfaces needed by actual use: usually Goal-to-Execution Workbench, Command Center, Decision Card, Audit/Work Trace, and a lightweight policy/settings area.

Recommended capabilities:

- Akka Workflows for long-running goal execution and approval pause/resume.
- Event Sourced Entities for high-value goals, decisions, policies, or audit-critical objects; Key Value Entities for simpler current-state records.
- Views for approval queues, active goals, activity streams, and trace lookup.
- Basic Async Digest if agents run while humans are away.

Advanced/defer:

- Fully governed skill component versioning.
- Automated replay diffing across large historical datasets.
- Sophisticated trust calibration and outcome attribution.

### 3. Production

Use for customer-facing systems or business-critical internal systems where agents perform recurring operational work with real side effects and measurable outcomes.

Required capabilities:

- **Durable goals:** Full lifecycle for goals, objectives, execution plans, tasks, exceptions, outcomes, and rollback/reversibility records where applicable.
- **Agent definitions:** Versioned system prompts, assigned skill versions, compiled agent context snapshots/references, tool/data/action permissions, per-action thresholds, and audit requirements.
- **Policies and approval gates:** Versioned policy documents with stable clause IDs, policy invocation records, approval thresholds, escalation rules, policy proposals/commits, and human authorization before active policy/skill/prompt changes.
- **Audit events:** Audit-complete event catalog covering agent, policy, tool, data, decision, exception, approval, outcome, replay, and governance events.
- **Traces:** Queryable traces linking every material action to goal, plan, task, agent, prompt/skill/policy versions, evidence, tool invocations, data access, disposition, approval, and outcome.
- **Replay/simulation:** Historical replay or simulation for high-impact prompt, skill, policy, and threshold changes with side-effect isolation and result diffs.
- **Security and privacy:** Code-enforced permissions, tenant isolation, redaction, retention/deletion/export rules, prompt-injection defenses, untrusted tool-output handling, provider data controls, and security audit events.
- **Testing/evaluation:** Regression evals, golden traces, replay tests, permission/approval tests, UI acceptance tests, prompt-injection tests, calibration tests, and outcome validation.
- **UI surfaces:** All six framework surfaces should exist or have an explicit product-specific reason for omission: Workbench, Command Center, Decision Card, Governance Center, Async Digest, and Audit/Work Trace.

Recommended capabilities:

- Event Sourced Entities for audit-critical lifecycle state; Workflows for orchestration, retries, compensation, and approval pause/resume; Consumers for notifications/projections; Timed Actions for reminders, expirations, digest generation, and retries.
- CQRS Views for decision queues, command center read models, policy history, traces, replay results, and outcome metrics.
- Streaming HTTP/SSE/WebSocket updates for active agent work in React/Vite/TypeScript UI.

Advanced/defer unless risk requires:

- Compliance-grade retention attestations.
- Formal model/provider comparison programs.
- Exhaustive historical replay before every low-impact copy or threshold tweak.

### 4. Regulated/high-risk production

Use when agent work affects legal/compliance determinations, healthcare, safety, employment, credit, finance, regulated communications, minors, protected classes, material customer harm, or other high-stakes outcomes.

Required capabilities:

- **Durable goals:** Compliance-aware goal/objective/plan records with accountable owners, jurisdiction/regulatory context, retention class, data classification, and explicit closure/appeal/remediation paths.
- **Agent definitions:** Strictly versioned and approved prompts, skills, tools, data scopes, model/provider configuration, thresholds, and compiled context; changes require human governance and effective dates.
- **Policies and approval gates:** Machine-checkable and human-readable policies with stable clauses, mandatory approval gates for sensitive side effects, separation of duties where needed, and complete proposal-to-commit provenance.
- **Audit events:** Tamper-evident or append-only audit records with retention controls, redaction strategy, export/reporting support, and clear distinction between audit events, raw model transcripts, and application logs.
- **Traces:** Compliance-grade decision provenance including evidence, policy clauses, data accessed, tools, agent versions, alternatives, structured rationale, human approvers, overrides, outcomes, and remediation/rollback state.
- **Replay/simulation:** Mandatory no-side-effect replay/simulation for material prompt, skill, policy, threshold, model, or tool changes using saved inputs and deterministic or controlled fixtures; approval required before activation.
- **Security and privacy:** Defense-in-depth enforcement, tenant/data isolation, least privilege, sensitive-data minimization, encryption/secrets controls, provider data-governance review, deletion/export workflows, abuse monitoring, and incident procedures.
- **Testing/evaluation:** Documented eval suites, adversarial testing, calibration monitoring, policy conformance tests, human-in-the-loop workflow tests, accessibility tests for decision surfaces, and release gates tied to risk acceptance.
- **UI surfaces:** All six surfaces are expected, with enhanced Decision Cards, Governance Center, Audit/Work Trace, and digest/queue ranking by stakes/value-at-risk.

Recommended capabilities:

- Event Sourced Entities or equivalent append-only histories for goals, decisions, policy/governance, approvals, and audit-critical state.
- Formal review workflows in Akka Workflows for approval, exception, remediation, and policy commit processes.
- Dedicated audit/export views and operational monitoring for failed enforcement checks.

Advanced capabilities:

- Independent evaluator agents or LLM-as-judge pipelines with human review of evaluator drift.
- Continuous calibration datasets split by segment/risk class.
- Compliance evidence packages generated from traces and audit events.

## Cross-level capability matrix

Use these labels when scoping a backlog:

- `required`: must be implemented for this maturity level before real use.
- `recommended`: should be implemented soon or when the corresponding workflow appears.
- `advanced`: defer unless the domain risk, scale, or regulator/customer requirement justifies it.

```yaml
maturity_capability_matrix:
  prototype:
    durable_goals: required
    agent_definitions: required_basic
    policies_and_approval_gates: required_lightweight
    audit_events: required_minimal
    traces: required_summary
    replay_simulation: recommended_dry_run
    security_privacy: required_minimized_data
    testing_evaluation: required_smoke
    ui_surfaces: required_minimal_subset
  mvp:
    durable_goals: required
    agent_definitions: required_versioned_basic
    policies_and_approval_gates: required
    audit_events: required_structured
    traces: required
    replay_simulation: recommended_for_high_impact_changes
    security_privacy: required_enforced_baseline
    testing_evaluation: required_core_paths
    ui_surfaces: required_workflow_subset
  production:
    durable_goals: required_full_lifecycle
    agent_definitions: required_versioned_and_auditable
    policies_and_approval_gates: required_versioned_clause_based
    audit_events: required_audit_complete
    traces: required_queryable
    replay_simulation: required_for_high_impact_changes
    security_privacy: required_defense_in_depth
    testing_evaluation: required_regression_evals
    ui_surfaces: required_all_or_explicitly_waived
  regulated_high_risk_production:
    durable_goals: required_compliance_context
    agent_definitions: required_governed_release
    policies_and_approval_gates: required_mandatory_and_separation_aware
    audit_events: required_tamper_evident_or_append_only
    traces: required_compliance_grade
    replay_simulation: required_release_gate
    security_privacy: required_regulated_controls
    testing_evaluation: required_documented_release_gate
    ui_surfaces: required_all_enhanced
```

## Procedure

1. **Start from intake.** Use the product's delegated work, side effects, data sensitivity, human roles, policy requirements, and audit obligations.
2. **Assign a maturity level.** Pick prototype, MVP, production, or regulated/high-risk production.
3. **State the risk drivers.** Name the facts that force the level: external side effects, sensitive data, irreversible actions, legal obligations, scale, customer impact, or governance complexity.
4. **Scope each capability.** For durable goals, agents, policies, audit, traces, replay, security, tests, and UI, mark required/recommended/advanced.
5. **Map to stack decisions.** Identify likely Akka components and React surfaces appropriate for the level.
6. **Defer deliberately.** List advanced capabilities that should not be built yet and the trigger that would make them necessary.
7. **Produce backlog guidance.** Give sequencing advice that lets implementation agents build the substrate before advanced UI or automation.

## Stack mapping prompts

When maturity affects implementation planning, answer:

- Which objects need Event Sourced Entities because temporal history, auditability, or replay matters?
- Which objects can start as Key Value Entities because current state is enough?
- Which long-running processes need Akka Workflows for pause/resume, approvals, retries, compensation, or failure handling?
- Which projections need Views for Command Center, approval queues, audit lookup, digest, or outcome metrics?
- Which Consumers react to audit events, tool results, policy changes, or outcome updates?
- Which Timed Actions schedule reminders, expirations, retry loops, or digest generation?
- Which HTTP/gRPC/MCP endpoints expose APIs, UI updates, tools, resources, or prompts?
- Which React/Vite/TypeScript surfaces are required now, and which can be a thin placeholder?

## Required output format

```yaml
agent_first_saas_maturity_assessment:
  product_or_feature: string
  recommended_level: prototype | mvp | production | regulated_high_risk_production
  confidence: high | medium | low
  rationale:
    delegated_work: string
    side_effects: string[]
    data_sensitivity: string
    policy_or_regulatory_drivers: string[]
    human_governance_needs: string[]
    why_not_lower: string
    why_not_higher: string

  capability_scope:
    durable_goals:
      status: required | recommended | advanced
      scope: string[]
    agent_definitions:
      status: required | recommended | advanced
      scope: string[]
    policies_and_approval_gates:
      status: required | recommended | advanced
      scope: string[]
    audit_events:
      status: required | recommended | advanced
      scope: string[]
    traces:
      status: required | recommended | advanced
      scope: string[]
    replay_simulation:
      status: required | recommended | advanced
      scope: string[]
    security_privacy:
      status: required | recommended | advanced
      scope: string[]
    testing_evaluation:
      status: required | recommended | advanced
      scope: string[]
    ui_surfaces:
      status: required | recommended | advanced
      required_now:
        - goal_to_execution_workbench | command_center_mission_control | deviation_review_decision_card | agent_skills_policy_governance_learning_center | async_digest_executive_briefing | audit_and_work_trace
      deferred:
        - surface: string
          trigger: string

  stack_implications:
    event_sourced_entities:
      - object: string
        reason: string
    key_value_entities:
      - object: string
        reason: string
    workflows:
      - process: string
        reason: string
    views:
      - view: string
        consumers: string[]
    consumers_or_timed_actions:
      - component: string
        trigger: string
        responsibility: string
    endpoints:
      - endpoint: string
        type: http | grpc | mcp | streaming
        responsibility: string
    frontend_work:
      - surface: string
        implementation_depth: placeholder | minimal | full

  backlog_guidance:
    build_first:
      - string
    build_next:
      - string
    defer:
      - item: string
        defer_until: string
    explicit_non_goals:
      - string

  acceptance_criteria:
    - string

  open_questions:
    - string
```

## Backlog sequencing guidance

- Build durable goal, plan, decision, policy, audit, and trace foundations before polishing advanced dashboards.
- Implement mechanical approval and permission checks before giving agents real side-effect tools.
- Add trace and audit creation in the execution path, not as a later reporting feature.
- Add replay/simulation before allowing frequent high-impact prompt, skill, policy, or threshold changes.
- Build all UI surfaces at the maturity depth required now: a minimal trace view is better than a polished command center with no audit substrate.
- For prototypes, prefer mocked tools and visible assumptions over production-grade governance scaffolding.
- For regulated/high-risk systems, do not defer governance, audit, security, calibration, or release-gate tests.

## Red flags

Escalate the maturity recommendation upward if any of these are true:

- Agents take irreversible, financial, legal, safety, healthcare, employment, or customer-impacting actions.
- Agents access sensitive data, secrets, regulated records, or cross-tenant information.
- Human approvals are required by policy, contract, or regulation.
- Users must later prove why a decision happened and who authorized it.
- Prompt, skill, policy, model, or threshold changes could materially change customer outcomes.
- The product depends on users trusting compressed summaries of autonomous work.

Escalate downward or defer scope if:

- The product only drafts text for humans to copy manually.
- All tools are mocked or read-only.
- No real side effects occur.
- The team is validating product desirability before implementing production controls.
