---
name: ai-first-saas-policy-governance
description: Design governance for versioned prompts, skills, policies, clauses, guardrails, permissions, thresholds, approval gates, precedents, examples, learned rules, and human-approved activation in ai-first SaaS products.
---

# ai-first-saas-policy-governance

Use this skill when a coding agent must specify how an ai-first SaaS product governs agent behavior, policy evolution, skill changes, learned rules, and human approval of behavioral changes.

Reference:

- `docs/ai-first-saas-coding-agent-framework.md` is the canonical conceptual contract and vocabulary for ai-first SaaS, especially the sections on agent skills, compiled runtime context, governance surfaces, policy commits, skill commits, replay, and audit.

## Core rule

Prompts, skills, policies, rules, thresholds, guardrails, and permissions are runtime business logic. They must be versioned, reviewable, testable, auditable, and activated only through explicit governance workflows.

Agents may propose changes, summarize evidence, draft policy language, classify examples, or recommend thresholds. Agents must not self-modify active system prompts, active skills, active policies, active guardrails, approval gates, thresholds, or platform permissions without human authorization. Platform permissions and deployment/activation code must enforce this rule.

## Governance object definitions

Use these distinctions consistently:

| Object | Definition | Typical owner | Enforcement or effect |
|---|---|---|---|
| `PolicyDocument` | Versioned collection of business, compliance, safety, or operational policy clauses. | Policy Owner / Coach, legal, operations lead | Cited by agents and policy evaluators; referenced in decisions and traces. |
| `PolicyClause` | Stable, addressable unit inside a policy document. Use stable clause IDs across versions when the meaning is continuous. | Policy Owner / Coach | Can trigger auto/review/approval/escalate routing, evidence requirements, or denial. |
| `SkillRule` | Versioned instruction, constraint, escalation rule, preference, or example inside an agent skill. | Skill owner or agent team owner | Compiled into agent runtime context; instructs behavior but does not itself enforce security. |
| `Guardrail` | Runtime safety or quality constraint that blocks, transforms, routes, or flags an attempted action or output. | Governance/security owner | Should be evaluated mechanically where possible and emit audit events. |
| `Permission` | Code-enforced authority to use a tool, access data, or perform an action. | Admin/security owner | Enforced by platform authorization, tool registry, scoped clients, workflow gates, or API checks. |
| `Threshold` | Numeric or categorical boundary for confidence, risk, impact, stakes, amount, volume, or sensitivity. | Policy Owner / Coach | Drives auto/review/approval/escalate disposition. |
| `ApprovalGate` | Workflow checkpoint requiring human approval before continuing or performing a side effect. | Reviewer / Approver or Policy Owner | Blocks execution until approved, rejected, revised, expired, or escalated. |
| `Precedent` | Prior human decision plus context/outcome used to guide similar future decisions. | Reviewer / Approver, Policy Owner | Advisory unless converted into policy, rule, threshold, or example. |
| `ReferenceExample` | Positive or negative example used to teach desired behavior or clarify edge cases. | Policy Owner / Coach | Compiled into skill or policy context; supports evaluation and replay. |
| `LearnedRule` | Proposed or approved behavioral rule derived from decisions, outcomes, feedback, or analysis. | Policy Owner / Coach | Draft until approved; active only after commit into a skill, policy, threshold, or guardrail. |
| `PolicyProposal` | Draft change to a policy document or clause. | Human or agent proposer | No runtime effect until approved and committed. |
| `PolicyCommit` | Approved versioned policy change with provenance, diff, author, optional replay result, and activation metadata. | Authorized human | Creates a new active or scheduled policy version. |
| `SkillProposal` | Draft change to skill frontmatter, context, policy bindings, rules, or examples. | Human or agent proposer | No runtime effect until approved and committed. |
| `SkillCommit` | Approved versioned skill or skill-component change with provenance, diff, author, optional replay result, and activation metadata. | Authorized human | Creates a new active or scheduled skill version. |

## Procedure

### 1. Identify governed behavior

From the product requirements, object model, agent roster, and decision workflows, list behavior that requires governance:

- agent system prompts and operating identities;
- assigned skills and skill versions;
- policy documents and stable policy clauses;
- skill rules, examples, preferences, and escalation rules;
- guardrails and validators;
- tool, data, action, and tenant permissions;
- confidence, risk, impact, stakes, and value thresholds;
- approval gates and reviewer roles;
- decision-to-learning paths;
- replay/simulation requirements before activation;
- audit and retention requirements for governance changes.

### 2. Classify each rule-like artifact

For each statement or constraint in the source material, decide what it is:

- **Policy clause** when it represents business/compliance/safety policy that should be cited by stable ID.
- **Skill rule** when it instructs an agent how to perform a capability.
- **Guardrail** when it must block, sanitize, route, or flag behavior at runtime.
- **Permission** when it controls whether an agent can access data, call tools, or perform actions.
- **Threshold** when routing depends on a score, amount, confidence, risk, impact, or sensitivity value.
- **Approval gate** when work must pause for a human decision.
- **Reference example** when it teaches behavior without being a binding rule.
- **Precedent** when it is a prior decision used for guidance.
- **Learned rule** when it is an extracted candidate behavior change requiring review.

Do not collapse all of these into generic prompt text. Prompts and skills instruct; policy evaluators, guardrails, permissions, thresholds, and approval gates enforce or route.

### 3. Define versioning and addressability

Specify identifiers and versioning for every governed object:

- document IDs, clause IDs, rule IDs, threshold IDs, guardrail IDs, permission IDs;
- version IDs and parent version IDs;
- draft, approved, active, scheduled, deprecated, rejected, and archived statuses;
- stable IDs for clauses/rules whose meaning persists across revisions;
- provenance: human edit, teach-from-decision, agent proposal, migration, tuning proposal, incident response;
- effective time, tenant or environment scope, author/approver, and rollback target.

Every agent execution must be able to reference the exact active versions of system prompt, skill, skill components, policies, clauses, rules, thresholds, guardrails, permissions, and approval gates that governed it.

### 4. Design proposal and commit workflows

Separate proposal from activation. A proposed change may be generated by a human, agent, tuning process, replay finding, incident, or migration, but it must not affect active runtime behavior until committed by an authorized human or approved release process.

Minimum governance workflow:

```text
draft/proposed
→ review_requested
→ approved | rejected | needs_revision
→ replay_or_simulation_required? 
→ committed
→ scheduled | active
→ superseded | rolled_back | deprecated | archived
```

For low-risk prototypes, replay may be optional. For production, regulated, high-risk, customer-impacting, or broad permission changes, require replay/simulation or equivalent test evidence before activation.

### 5. Design decision-to-learning flow

Human decisions can improve future behavior, but the system must preserve the distinction between one-time decisions and durable behavioral changes.

Decision outcome paths:

- `one_time`: applies only to the current approval/exception.
- `precedent`: stores the decision and outcome for similar future recommendations.
- `positive_example`: adds an example of desired behavior to a skill or policy draft.
- `negative_example`: adds an example of behavior to avoid.
- `learned_rule_proposal`: creates a draft rule for human review.
- `policy_proposal`: creates a draft policy or clause update.
- `threshold_proposal`: creates a draft threshold change.
- `permission_change_request`: creates an admin/security review item.

Never silently turn an override into active policy. Show the proposed durable effect, diff, impacted agents, impacted actions, replay result if available, and rollback path.

### 6. Define human authorization roles

For each governed object, specify who may draft, review, approve, activate, roll back, and archive it.

Typical roles:

- `Policy Owner / Coach`: policies, clauses, examples, learned rules, thresholds.
- `Reviewer / Approver`: decisions, exceptions, approval gates, precedents.
- `Supervisor`: operational routing and escalation configuration within allowed bounds.
- `Admin / Security Owner`: tool permissions, data scopes, tenant isolation, secrets, sensitive actions.
- `Auditor`: read-only inspection, reports, evidence export, retention checks.
- `Outcome Owner`: outcome-linked threshold adjustments and success metrics.

Use separation of duties for high-risk changes. The agent that proposed a change should not be the sole approver of that change, and a user should not receive approval powers merely because they initiated a goal.

### 7. Specify audit events

Governance changes must emit audit events as first-class product events, not only application logs.

Capture at least:

- proposal created/updated/submitted;
- review requested;
- approved/rejected/needs revision;
- replay/simulation started/completed/waived;
- commit created;
- version activated/scheduled/superseded/deprecated/rolled back;
- permission or threshold changed;
- approval gate created/triggered/resolved;
- precedent/example/learned rule created from decision;
- agent attempted unauthorized self-modification or forbidden activation.

Each event should include actor, actor type, governed object ID/version, diff summary, affected agents/actions, source decision or incident, reviewer/approver, timestamp, tenant/environment, and trace links.

### 8. Produce the governance specification

Use the output format below. Keep it concrete enough to drive backend models, workflows, UI governance center screens, audit traces, and tests.

## Required output format

```yaml
policy_governance_spec:
  product_name: string
  maturity_level: prototype | mvp | production | regulated_or_high_risk
  governance_principles:
    - string

  governed_objects:
    - id: string
      type: system_prompt | policy_document | policy_clause | skill | skill_frontmatter | skill_context | skill_rule | guardrail | permission | threshold | approval_gate | precedent | reference_example | learned_rule
      purpose: string
      owner_role: string
      stable_identifier_required: boolean
      versioned: boolean
      active_runtime_effect: instructs | enforces | routes | blocks | cites | advisory | none_until_committed
      allowed_proposers:
        - human_role | agent_id | system_process
      allowed_approvers:
        - human_role
      activation_requires:
        human_authorization: boolean
        replay_or_simulation: required | recommended | optional | not_applicable
        tests: string[]
        separation_of_duties: boolean
      rollback_or_supersession: string

  object_definitions:
    policies:
      clause_id_scheme: string
      clause_types:
        - structured_rule | prose_guidance | positive_example | negative_example | threshold | permission | escalation | data_tool_boundary
      required_fields:
        - string
    skills:
      component_versioning: string
      rule_types:
        - instruction | constraint | escalation | preference | positive_example | negative_example
      required_fields:
        - string
    guardrails_permissions_thresholds:
      enforcement_components:
        - string
      required_fields:
        - string

  state_transitions:
    policy_proposal_to_commit:
      states:
        - draft
        - review_requested
        - approved
        - rejected
        - needs_revision
        - replay_required
        - committed
        - scheduled
        - active
        - superseded
        - rolled_back
        - archived
      transitions:
        - from: string
          to: string
          actor: string
          guard_condition: string
          audit_event: string
    skill_proposal_to_commit:
      states:
        - draft
        - review_requested
        - approved
        - rejected
        - needs_revision
        - replay_required
        - committed
        - scheduled
        - active
        - superseded
        - rolled_back
        - archived
      transitions:
        - from: string
          to: string
          actor: string
          guard_condition: string
          audit_event: string
    decision_to_learning:
      states:
        - decision_recorded
        - learning_candidate_created
        - classified_as_precedent_or_example_or_rule_or_policy
        - human_review_requested
        - approved
        - rejected
        - committed_or_stored_as_advisory
      transitions:
        - from: string
          to: string
          actor: string
          guard_condition: string
          audit_event: string
    agent_proposed_change_to_activation:
      states:
        - agent_proposal_created
        - queued_for_human_review
        - human_approved
        - human_rejected
        - replay_or_tests_completed
        - committed
        - active
      transitions:
        - from: string
          to: string
          actor: string
          guard_condition: string
          audit_event: string

  approval_and_authorization_matrix:
    - governed_object_type: string
      draft_allowed_by:
        - string
      approve_allowed_by:
        - string
      activate_allowed_by:
        - string
      rollback_allowed_by:
        - string
      agent_self_modification_allowed: false
      notes: string

  decision_to_learning_rules:
    - decision_type: string
      allowed_learning_options:
        - one_time | precedent | positive_example | negative_example | learned_rule_proposal | policy_proposal | threshold_proposal | permission_change_request
      requires_human_confirmation: boolean
      requires_replay_before_activation: boolean
      audit_events:
        - string

  governance_audit_events:
    - event_type: string
      emitted_when: string
      required_payload_fields:
        - actor_id
        - actor_type
        - governed_object_id
        - governed_object_version_id
        - parent_version_id
        - diff_summary
        - source_decision_id
        - affected_agent_ids
        - affected_action_types
        - approver_id
        - replay_result_id
        - tenant_id
        - environment
        - created_at

  open_questions:
    - string
```

## State transition guidance

### Policy proposal to policy commit

```text
Draft PolicyProposal
→ Review Requested
→ Approved or Rejected or Needs Revision
→ Replay/Simulation Required when risk warrants
→ PolicyCommit Created
→ New PolicyVersion Scheduled or Activated
→ Prior Version Superseded
→ Optional Rollback creates a new commit pointing to prior approved behavior
```

Required checks:

- stable clause IDs are preserved or intentionally retired;
- changed clauses have diff summaries;
- affected agents, skills, actions, and approval gates are identified;
- activation is authorized by a human with the correct role;
- audit events link proposal, review, replay, commit, and activation.

### Skill proposal to skill commit

```text
Draft SkillProposal
→ Component Diffs Prepared
→ Review Requested
→ Approved or Rejected or Needs Revision
→ Replay/Simulation Required when risk warrants
→ SkillCommit Created
→ New composed SkillVersion Scheduled or Activated
→ Prior SkillVersion Superseded
```

Required checks:

- frontmatter, context, policy bindings, rules, and examples are versioned independently;
- the composed skill version references exact component versions;
- affected agents are known;
- generated runtime context preview is available;
- active agents cannot receive the new skill version until approval and activation.

### Decision to precedent/example/policy update

```text
Human Decision Recorded
→ Learning Candidate Created
→ Candidate Classified
→ Human Confirms Durable Use
→ Stored as Precedent/Example or Proposed as Rule/Policy/Threshold Change
→ Optional Replay/Test
→ Commit if approved
```

Required checks:

- the original decision remains immutable;
- one-time decisions do not automatically become general rules;
- outcomes are linked when available;
- future decision cards can cite precedents or examples by stable ID;
- policy updates require policy proposal and commit workflow.

### Agent-proposed change to human-approved activation

```text
Agent Proposal Created
→ Queued for Human Review
→ Human Approves/Rejects/Requests Revision
→ Replay/Tests Completed or Waived by Authorized Human
→ Commit Created by Authorized Workflow
→ Version Activated/Scheduled
```

Required checks:

- the proposal has no active runtime effect before approval;
- the proposing agent cannot activate its own proposal;
- permission changes require admin/security authorization;
- high-impact changes require replay/simulation or documented waiver;
- forbidden self-modification attempts are denied and audited.

## Review checklist

Before finalizing a governance design, verify:

- [ ] Policy, policy clause, skill rule, guardrail, permission, threshold, approval gate, precedent, reference example, and learned rule are distinct concepts.
- [ ] Every governed object has owner, versioning, status, provenance, and audit requirements.
- [ ] Stable clause/rule IDs exist where decisions or traces must cite rules.
- [ ] Proposal and commit workflows are separate from runtime activation.
- [ ] Agent-proposed changes require human authorization before activation.
- [ ] Agents cannot self-modify active prompts, skills, policies, guardrails, thresholds, approval gates, or permissions.
- [ ] Prompt and skill text are not treated as security boundaries; platform enforcement handles permissions and side effects.
- [ ] Decision-to-learning paths distinguish one-time decision, precedent, example, learned rule proposal, policy proposal, threshold proposal, and permission change request.
- [ ] Replay/simulation or documented waiver exists for high-impact changes.
- [ ] Governance audit events can reconstruct who proposed, reviewed, approved, committed, activated, superseded, or rolled back each change.
