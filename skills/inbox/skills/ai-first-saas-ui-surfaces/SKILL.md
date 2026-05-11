---
name: ai-first-saas-ui-surfaces
description: Generate screen specifications for the six required ai-first SaaS UI surfaces, mapping each surface to human roles, temporal modes, durable objects, sections, actions, audit events, and states.
---

# ai-first-saas-ui-surfaces

Use this skill when a coding agent must create UI specifications, screen maps, component plans, frontend tickets, or visual acceptance criteria for an ai-first SaaS product.

References:

- `docs/ai-first-saas-coding-agent-framework.md` is the canonical conceptual contract and vocabulary for ai-first SaaS, especially human roles, temporal modes, required surfaces, durable objects, decision provenance, audit, and governance expectations.
- `docs/ai-first-saas-ui-patterns.md` is the companion pattern reference for screen composition, component patterns, image-to-surface mappings, and visual acceptance criteria.

## Core rule

AI-first UI is not module navigation plus a chatbot. Every major screen must be centered on delegation, supervision, decision, teaching, catch-up, audit, or outcomes. Conversational controls may exist, but high-impact work must resolve into durable, inspectable objects with structured action controls and audit events.

## Required six surfaces

Every ai-first SaaS product should explicitly account for these surfaces. A prototype may combine surfaces in one screen, but the screen spec must still name which surface responsibilities are present and which are deferred.

| Surface | Temporal mode | Primary human roles | Purpose |
|---|---|---|---|
| Goal-to-Execution Workbench | `delegating_now` | Intent Author, Supervisor, Policy Owner | Convert intent into a durable goal and editable execution plan. |
| Command Center / Mission Control | `attending_now` | Supervisor, Reviewer, Outcome Owner, Auditor | Supervise active autonomous work and focus attention. |
| Deviation Review / Decision Card | `deciding_now` | Reviewer / Approver, Exception Handler, Policy Owner, Auditor | Resolve approval requests, exceptions, and policy deviations. |
| Agent Skills / Policy / Governance / Learning Center | `teaching_now` | Policy Owner / Coach, Intent Author, Auditor, Outcome Owner | Author, test, approve, and audit prompts, skills, policies, rules, thresholds, and learned behavior. |
| Async Digest / Executive Briefing | `catching_up` | Supervisor, Reviewer, Outcome Owner, Auditor | Compress autonomous activity while the human was away into a high-signal briefing. |
| Audit and Work Trace | `auditing_later` | Auditor, Exception Handler, Policy Owner, Reviewer | Explain why something happened, who/what authorized it, and what evidence, policies, tools, and decisions contributed. |

## Procedure

### 1. Identify product context and UI scope

Read the product requirements and extract:

- product objective and delegated operational work;
- human roles and permissions;
- maturity level and risk level;
- domain entities that must appear in UI;
- ai-first substrate objects already specified or needed;
- actions agents may perform automatically, under review, with approval, or only by escalation;
- audit, policy, and trace obligations.

If product requirements describe only dashboards, forms, records, or chat, translate them into ai-first surfaces before drafting screens.

### 2. Map screens to temporal modes

For each proposed screen, assign one primary temporal mode:

- `delegating_now`: user defines objectives and approves plans.
- `attending_now`: user monitors current autonomous work.
- `deciding_now`: user resolves one judgment, approval, exception, or policy deviation.
- `teaching_now`: user changes future agent behavior.
- `catching_up`: user reviews compressed activity after time away.
- `auditing_later`: user investigates provenance and authorization.

Avoid mixing multiple modes in one screen unless the product is a prototype or the combination is explicitly justified. If combining modes, keep the primary attention path clear.

### 3. Map each surface to durable objects

Every visible element must be backed by durable objects or read models. At minimum, map each screen to:

- goals, objectives, execution plans, tasks, agents, agent assignments;
- policies, policy clauses, guardrails, thresholds, approval gates;
- decisions, approval requests, exceptions, recommendations, evidence, alternatives, precedents;
- activity events, work traces, audit events, tool invocations, data access events;
- skill/prompt/policy versions where governance is visible;
- outcome metrics and outcome links where success is being evaluated.

Do not create UI components that imply missing substrate behavior. If a component shows policy, confidence, risk, authority, or audit links, specify the source object and required fields.

### 4. Specify required sections and actions

For each surface, define sections and user actions. Use these defaults unless product-specific constraints require changes.

#### Goal-to-Execution Workbench

Required sections:

- intent or goal statement;
- success criteria and constraints;
- generated execution plan;
- assigned agents and responsibilities;
- required data, tools, and permissions;
- risk/confidence assessment;
- approval gates;
- expected timeline or phases;
- simulation or dry-run result when warranted;
- activation controls.

Actions:

- edit goal;
- edit success criteria;
- edit plan;
- change agent assignment;
- request simulation;
- approve/activate plan;
- defer;
- cancel.

Minimum audit events:

- `goal_created`;
- `goal_updated`;
- `execution_plan_generated`;
- `execution_plan_edited`;
- `execution_plan_approved`;
- `execution_plan_activated`;
- `plan_activation_cancelled`.

#### Command Center / Mission Control

Required sections:

- objective or operating-scope banner;
- autonomy and policy chips;
- key outcome/risk indicators;
- activity stream with disposition tags;
- approval and exception queue;
- agent roster with status, authority, load, trust level, and active tasks;
- compressed routine activity summary;
- drill-down links to traces, decisions, and records.

Actions:

- open trace;
- open decision card;
- reassign or pause agent within permission;
- adjust goal or priority;
- acknowledge FYI;
- filter by disposition, agent, policy, risk, or goal;
- escalate issue.

Minimum audit events:

- `command_center_viewed` when required by compliance;
- `activity_item_opened` when required by compliance;
- `agent_paused`;
- `goal_priority_changed`;
- `exception_escalated`;
- `approval_request_opened`.

#### Deviation Review / Decision Card

Required sections:

- decision title and type;
- deviation or exception statement;
- agent recommendation;
- confidence, risk, impact, and stakes;
- policy trigger with stable clause ID;
- evidence list;
- structured reasoning factors for and against;
- alternatives considered;
- similar precedents and outcomes;
- action panel;
- teaching/learning affordance;
- trace and audit links.

Actions:

- approve;
- reject;
- counterproposal;
- ask agent to revise;
- request more evidence;
- escalate;
- mark one-time decision;
- create precedent;
- propose example, learned rule, threshold change, or policy update.

Minimum audit events:

- `decision_card_created`;
- `approval_requested`;
- `human_decision_recorded`;
- `evidence_requested`;
- `counterproposal_submitted`;
- `decision_escalated`;
- `precedent_created`;
- `policy_or_skill_proposal_created_from_decision`.

#### Agent Skills / Policy / Governance / Learning Center

Required sections:

- agent roster with prompt, skill, tool, data, and authority bindings;
- versioned system prompt view/editor;
- skill editor with frontmatter, context, policies, and rules;
- skill-to-agent assignment controls;
- generated runtime context preview;
- versioned policy document editor with stable clause IDs;
- reference examples and guardrails;
- agent-proposed changes awaiting approval;
- ambiguity/static-analysis warnings;
- diff and recent changes timeline;
- replay/simulation impact panel;
- commit, discard, rollback, and activation controls.

Actions:

- draft prompt/skill/policy change;
- approve/reject proposal;
- assign skill to agent;
- update threshold or approval gate;
- run replay/simulation;
- commit version;
- schedule activation;
- roll back;
- inspect invocation analytics.

Minimum audit events:

- `prompt_proposal_created`;
- `skill_proposal_created`;
- `policy_proposal_created`;
- `replay_started`;
- `replay_completed`;
- `prompt_committed`;
- `skill_committed`;
- `policy_committed`;
- `version_activated`;
- `version_rolled_back`;
- `agent_skill_assignment_changed`.

#### Async Digest / Executive Briefing

Required sections:

- time window;
- outcome-shaped headline;
- routine activity compression statistic;
- material event count and policy violation count;
- approximately three curated stories;
- prior human decision outcomes;
- stakes-ranked pending queue;
- curation explanation including omitted categories;
- conversational follow-up input;
- links to full trace/activity history.

Actions:

- open curated story;
- open pending decision;
- drill down to full trace;
- ask follow-up question;
- change digest window;
- tune curation preference through governed settings;
- mark briefing reviewed.

Minimum audit events:

- `digest_generated`;
- `digest_viewed` when required by compliance;
- `digest_item_opened`;
- `curation_feedback_recorded`;
- `pending_decision_opened_from_digest`.

#### Audit and Work Trace

Required sections:

- trace summary answering who/what/when/why/how-authorized;
- chronological timeline;
- agent steps and handoffs;
- tool invocations;
- data access events;
- evidence considered;
- policy versions and clause invocations;
- recommendations and decisions;
- approvals, overrides, and human comments;
- final action;
- outcome link;
- rollback/reversibility status;
- export or report controls where appropriate.

Actions:

- expand event details;
- filter by agent, policy, tool, data, or decision;
- open source evidence;
- open policy version;
- open related decision card;
- export audit report;
- initiate rollback if permitted;
- flag trace for review.

Minimum audit events:

- `work_trace_created`;
- `trace_viewed` when required by compliance;
- `audit_report_exported`;
- `rollback_requested`;
- `rollback_performed`;
- `trace_flagged_for_review`.

### 5. Define empty, loading, error, and blocked states

For each screen include state specifications:

- **Empty:** what appears before agents have created goals, plans, events, decisions, policies, digests, or traces.
- **Loading:** progressive rendering for slow agent runs, simulations, searches, traces, and digest generation.
- **Error:** data unavailable, tool unavailable, failed replay, missing policy version, permission denied, or trace corruption.
- **Blocked:** action cannot proceed because approval is required, policy is ambiguous, permissions are missing, data scope is denied, or an agent run is paused.

Each state should tell the human what happened, what remains safe, what action is available, and whether an audit event was emitted.

### 6. Define cross-surface navigation

Specify how users move among surfaces through durable artifacts:

```text
Goal → ExecutionPlan → Command Center activity → Decision Card → Teach affordance → Policy/Skill proposal → Replay result → Audit Trace → Outcome
```

Every decision card should link to its trace. Every activity item should link to a trace or source artifact. Every policy chip should link to a policy clause/version. Every digest item should link to the compressed events and full work trace.

### 7. Produce the screen specification

Use the output format below for each screen or surface. Keep it concrete enough to drive React components, API contracts, backend read models, audit events, and UI acceptance tests.

## Required output format

```yaml
ui_surface_spec:
  product_name: string
  maturity_level: prototype | mvp | production | regulated_or_high_risk
  screen_id: string
  screen_name: string
  surface_type: goal_to_execution_workbench | command_center | decision_card | governance_learning_center | async_digest | audit_work_trace | combined
  primary_temporal_mode: delegating_now | attending_now | deciding_now | teaching_now | catching_up | auditing_later
  secondary_temporal_modes:
    - delegating_now | attending_now | deciding_now | teaching_now | catching_up | auditing_later
  human_roles:
    - role: intent_author | supervisor | reviewer_approver | exception_handler | policy_owner_coach | auditor | outcome_owner
      permissions_needed:
        - string
      primary_jobs_to_be_done:
        - string

  purpose: string
  user_questions_answered:
    - string

  durable_objects:
    primary:
      - object_type: Goal | Objective | ExecutionPlan | Agent | ApprovalRequest | Decision | Exception | PolicyDocument | PolicyClause | WorkTrace | AuditEvent | OutcomeMetric | string
        fields_needed:
          - string
        source_read_model_or_api: string
    supporting:
      - object_type: string
        fields_needed:
          - string
        source_read_model_or_api: string

  layout_sections:
    - id: string
      name: string
      purpose: string
      required: boolean
      backing_objects:
        - string
      components:
        - ObjectiveBanner | PolicyChip | AgentRosterCard | ActivityStreamItem | ApprovalQueueItem | DecisionCard | EvidenceList | AlternativesConsidered | PrecedentStrip | TeachAffordance | PolicyClauseEditor | ReplayImpactPanel | DigestStoryCard | WorkTraceTimeline | string
      visible_fields:
        - string
      interactions:
        - string
      audit_events_emitted:
        - string

  actions:
    - id: string
      label: string
      actor_roles_allowed:
        - string
      preconditions:
        - string
      approval_or_permission_required: string
      result_objects_created_or_updated:
        - string
      audit_event: string
      success_feedback: string
      failure_feedback: string

  attention_and_curation:
    routine_activity_handling: hidden | compressed | listed | not_applicable
    material_event_rules:
      - string
    queue_ranking: chronology | risk | stakes | due_time | custom
    compression_explanation_required: boolean

  state_specs:
    empty:
      message: string
      available_actions:
        - string
    loading:
      progressive_content:
        - string
      timeout_or_retry_behavior: string
    error:
      error_cases:
        - case: string
          user_message: string
          recovery_action: string
          audit_event: string | null
    blocked:
      blocked_cases:
        - case: string
          reason_shown_to_user: string
          required_resolution: string
          audit_event: string | null

  audit_and_trace_requirements:
    links_required:
      - from_component: string
        to_artifact: string
    audit_events:
      - event_type: string
        emitted_when: string
        payload_fields:
          - string
    trace_fields_visible:
      - string

  acceptance_criteria:
    - string

  open_questions:
    - string
```

## Surface-specific acceptance checks

### Goal-to-Execution Workbench

- [ ] Natural-language intent becomes a durable `Goal` object.
- [ ] The `ExecutionPlan` is editable before activation.
- [ ] Agent assignments, required tools/data, permissions, risks, and approval gates are visible.
- [ ] Activation records policy/prompt/skill versions where applicable and emits an audit event.

### Command Center / Mission Control

- [ ] Current objective and attention priorities are visible within seconds.
- [ ] Activity items have disposition tags: `auto`, `review`, `approval`, `escalate`, or `fyi`.
- [ ] Routine activity is compressed but drillable.
- [ ] Approval/exception items include enough data to open a complete decision card.
- [ ] Agent roster links status to authority, tools, policy bindings, and traces.

### Deviation Review / Decision Card

- [ ] The card cites a stable policy clause ID.
- [ ] Recommendation, evidence, confidence, risk, impact, alternatives, and precedents are visible.
- [ ] Available human actions are explicit and permission-aware.
- [ ] Human decisions create audit events and may create one-time decisions, precedents, examples, or proposals.
- [ ] The UI stores structured rationale and alternatives, not hidden model chain-of-thought.

### Governance / Learning Center

- [ ] Prompts, skills, policies, clauses, rules, thresholds, and approval gates are versioned.
- [ ] Agent-proposed changes require human approval before activation.
- [ ] Replay or simulation is available for high-impact changes.
- [ ] Commits preserve diff, provenance, author, affected agents/actions, and activation status.
- [ ] Tool, data, and action permissions are shown as platform-enforced boundaries, not merely prompt instructions.

### Async Digest / Executive Briefing

- [ ] The digest declares its time window.
- [ ] Routine work compression statistics are visible.
- [ ] Material events and approximately three curated stories are shown.
- [ ] Pending items are ranked by stakes, risk, due time, or another explicit rule.
- [ ] Every compressed story links to audit-complete traces.

### Audit and Work Trace

- [ ] The trace answers who, what, when, why, and how authorized.
- [ ] Policy versions, stable clause IDs, tool invocations, and data access events are visible.
- [ ] Decisions link to outcomes when available.
- [ ] Rollback or reversibility status is explicit.
- [ ] Audit data is generated from execution events, not reconstructed only from raw logs.

## Review checklist

Before finalizing UI specifications, verify:

- [ ] Each screen maps to one primary temporal mode and explicit human roles.
- [ ] Each screen names durable backing objects and source APIs/read models.
- [ ] The six required surfaces are present, intentionally combined, or explicitly deferred.
- [ ] Screens are objective-, decision-, policy-, trace-, or outcome-centered rather than module-centered.
- [ ] Agent activity is visible, curated, and traceable.
- [ ] Policy boundaries and authority limits appear where decisions/actions occur.
- [ ] Approval items include evidence, risk, confidence, impact, and policy trigger data.
- [ ] Empty/loading/error/blocked states are specified.
- [ ] High-impact actions use structured UI, not chat-only interaction.
- [ ] Every material action creates or links to audit events and work traces.
