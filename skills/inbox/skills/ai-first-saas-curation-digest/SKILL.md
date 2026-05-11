---
name: ai-first-saas-curation-digest
description: Design material/routine activity classification, curation, async digests, pending queues, drill-downs, and tests for compressed but audit-complete ai-first SaaS briefings.
---

# ai-first-saas-curation-digest

Use this skill when a coding agent must specify how an ai-first SaaS product compresses autonomous activity into high-signal Command Center summaries, async digests, executive briefings, notifications, or catch-up screens.

References:

- `docs/ai-first-saas-coding-agent-framework.md` is the canonical contract for ai-first SaaS objects, disposition tags, curation classifiers, async digest surfaces, audit traces, and routine/material activity.
- `docs/ai-first-saas-ui-patterns.md` defines Async Digest / Executive Briefing screen-composition patterns and component expectations.
- `docs/skills-pack-tech-stack.md` defines the Akka + React/Vite/TypeScript stack for Consumers, Views, Timed Actions, notifications, APIs, and frontend digest screens.

## Core principle

Compress routine agent activity for human attention, but never compress away accountability. Every digest statistic, curated story, pending item, and omitted class of activity must be traceable back to audit-complete events and work traces.

A valid digest answers:

1. What time window is covered?
2. What material happened?
3. What routine work was compressed?
4. What needs human attention now, ranked by stakes?
5. What was omitted and why?
6. Where can the user drill down to the complete trace?

## Routine vs material classification

Classify activity at event or aggregate level before rendering a digest.

- **Routine activity**: expected, policy-compliant, low-risk, low-stakes, non-blocking work that should be counted, sampled, and retained for audit rather than shown item-by-item.
- **Material event**: activity that meaningfully changes state, risk, outcome, customer impact, policy posture, goal progress, or human decision requirements.

Material events usually include:

- approval requests, exceptions, escalations, blocked actions, policy deviations, security/privacy concerns;
- goal, plan, task, or outcome state changes with meaningful impact;
- high-stakes or high-value actions, even if approved by policy;
- agent failures, retries exhausted, tool/data access anomalies, or missing evidence;
- human decisions, overrides, and decisions whose outcomes are now known;
- proposed policy, prompt, skill, threshold, or permission changes.

Routine events usually include:

- successful low-risk tool calls within policy;
- routine data enrichment, classification, summarization, or validation;
- expected task progress updates that do not alter risk or require attention;
- repeated heartbeat/status events already represented by an aggregate count.

When uncertain, classify as material or `review_candidate`; do not silently omit ambiguous events.

## Curation classifier contract

Define a classifier that produces durable, explainable curation records. The classifier may combine deterministic rules, thresholds, model judgment, and human feedback, but the output must be structured.

```yaml
curation_classification:
  id: string
  source_event_id: string
  source_trace_id: string
  goal_id: string | null
  agent_id: string | null
  classified_at: datetime
  classifier_version_id: string
  classification: routine | material | review_candidate | suppressed_duplicate
  disposition: auto | review | approval | escalate | fyi
  materiality_score: number
  stakes_rank: trivial | routine | material | major | existential
  reasons:
    - code: policy_deviation | outcome_change | approval_needed | high_stakes | user_preference | duplicate | routine_within_policy | anomaly | deadline | prior_decision_outcome
      explanation: string
  compression_group_id: string | null
  digest_eligible: boolean
  notification_eligible: boolean
  trace_link: string
```

Classifier inputs should include event type, disposition tag, goal priority, stakes/value-at-risk, risk/confidence/impact scores, policy clauses invoked, agent trust level, customer/entity tier, deadline, reversibility, outcome impact, duplicate/similarity signals, and user digest preferences.

## Digest time windows and triggers

Specify digest cadence by role and workflow. Common windows:

- `since_last_seen`: default catch-up window per user and workspace.
- `daily`: executive briefing and outcome summary.
- `shift`: operational handoff window.
- `weekly`: policy/outcome trend digest.
- `incident_or_goal_window`: scoped digest for a single incident, account, case, or goal.

Digest generation triggers:

- user opens the async digest surface;
- scheduled Timed Action creates a daily/shift/weekly briefing;
- material event threshold is crossed;
- pending approval SLA/deadline approaches;
- user asks conversationally for a catch-up summary, which should resolve to a durable digest/report object.

## Digest content rules

A digest should be short enough to scan but complete enough to trust.

Required content:

- declared time window and scope;
- compressed activity statistics;
- policy violation or exception count;
- outcome-shaped headline;
- approximately three curated stories;
- prior human decision outcomes when available;
- stakes-ranked pending queue;
- omitted-item explanations;
- links to full traces and filtered activity history.

Prefer approximately three curated stories. Use fewer when little happened; use more only when the window contains multiple unrelated high-stakes material stories. Do not fill the digest with low-value stories just to reach three.

## Compressed activity statistics

Track counts and summaries without showing every routine event.

```yaml
compressed_activity_stats:
  total_events: number
  routine_events_compressed: number
  material_events_surfaced: number
  suppressed_duplicates: number
  agent_actions_auto_completed: number
  approvals_created: number
  exceptions_created: number
  policy_violations: number
  tool_invocations: number
  data_access_events: number
  goals_advanced: number
  outcomes_recorded: number
  compression_ratio: string
  top_compression_groups:
    - group_id: string
      label: string
      count: number
      representative_trace_link: string
```

Do not use compression as a substitute for retention. Compressed events must remain queryable in audit traces and activity history.

## Curated story structure

Curated stories should be narrative summaries backed by durable objects, not free-floating prose.

```yaml
curated_story:
  id: string
  title: string
  story_type: outcome_update | exception_cluster | goal_progress | policy_issue | agent_failure | customer_risk | decision_outcome | operational_win
  why_selected: string
  related_goal_ids: string[]
  related_agent_ids: string[]
  related_event_ids: string[]
  related_trace_ids: string[]
  summary: string
  evidence:
    - source_object_type: audit_event | work_trace | outcome_metric | approval_request | exception | decision | domain_record
      source_object_id: string
      summary: string
      link: string
  impact:
    stakes_rank: trivial | routine | material | major | existential
    affected_entities: string[]
    outcome_metric_ids: string[]
  recommended_human_action: none | inspect | approve | decide | escalate | update_policy | acknowledge
  trace_link: string
```

## Stakes-ranked pending queues

Pending queues in digests must rank attention by stakes/value-at-risk, deadline, risk, confidence, reversibility, and policy severity, not chronology alone.

```yaml
pending_queue_item:
  object_type: approval_request | exception | decision_card | policy_proposal | threshold_review | failed_task
  object_id: string
  title: string
  required_role: supervisor | reviewer | exception_handler | policy_owner | auditor | outcome_owner
  stakes_rank: trivial | routine | material | major | existential
  value_at_risk: string | null
  due_at: datetime | null
  risk_label: low | medium | high | critical
  confidence_label: low | medium | high | very_high | null
  reason_waiting: string
  recommended_action: string
  trace_link: string
```

## Omitted-item explanations

Every digest needs a transparent explanation of what was compressed or omitted.

Examples:

- `128 routine enrichment events were compressed because they completed within policy under clause OPS-12.`
- `22 duplicate status updates were omitted; the latest state is reflected in the goal progress story.`
- `No low-risk FYI items are shown because your digest preference hides FYIs unless they affect outcomes.`

Include a `View all compressed activity` or filtered trace link for each major omitted group.

## Drill-down requirement

Users must be able to drill down from every compressed summary to audit-complete traces.

Minimum drill-down paths:

- digest headline -> filtered activity/audit view for the window;
- compressed statistic -> event group view with representative and full list;
- curated story -> WorkTrace, related AuditEvents, domain record, and outcome metrics;
- pending queue item -> DecisionCard/ApprovalRequest/Exception plus trace;
- omitted explanation -> filtered list of omitted/compressed events;
- policy count -> policy invocations and clause-level events.

Never make the digest the only record of what happened.

## Akka + React implementation mapping

Backend design:

- Use Consumers to classify incoming audit, workflow, task, decision, outcome, policy, tool, and data-access events as routine/material/review-candidate.
- Use Views for digest read models: per-user catch-up windows, material event feeds, compressed activity groups, curated stories, pending queues, and omitted-item groups.
- Use Timed Actions for scheduled digest generation, reminder notifications, stale pending-item escalation, and digest preference refresh.
- Use Workflows when digest generation requires multi-step summarization, human acknowledgement, or approval-routing side effects.
- Use Event Sourced Entities for governed curation rules, digest preferences, classifier versions, and digest/report objects when history and replay matter.
- Use Key Value Entities for current digest snapshots only when source events and audit history are durable elsewhere.
- Expose HTTP APIs for digest retrieval, story drill-down, pending queue actions, acknowledgement, and preference updates; use SSE/WebSocket when live digest updates or notification streams are needed.

Frontend design:

- Implement an Async Digest / Executive Briefing screen with time-window selector, outcome headline, compression stats, curated story cards, prior decision outcomes, and stakes-ranked pending queue.
- Use React/Vite/TypeScript typed API clients for digest snapshots, drill-down links, and queue actions.
- Provide loading, empty, stale, error, and permission-denied states.
- Keep the digest concise; move exhaustive detail to drill-down traces and filtered activity views.
- Support conversational follow-up for queries, but resolve high-stakes actions into structured decision cards or approval screens.

## Tests for compression quality

Required tests:

- over-compression tests: material events, high-stakes items, policy violations, exceptions, and outcome changes must not disappear into routine counts;
- under-compression tests: routine low-risk event floods should not dominate the digest;
- classifier boundary tests for materiality thresholds, duplicate suppression, user preferences, deadlines, and stakes rankings;
- traceability tests proving every statistic, story, pending item, and omission group links to underlying audit events/work traces;
- pending queue ranking tests proving stakes and deadlines outrank pure recency;
- digest window tests for `since_last_seen`, daily, shift, weekly, and scoped windows;
- UI tests proving users can drill from summaries to full traces and decision cards;
- regression/replay tests when curation classifier, preferences, policy thresholds, prompts, or skills change.

Monitoring signals:

- user frequently opens full trace immediately after digest;
- missed high-stakes events later discovered outside digest;
- too many digest items dismissed as irrelevant;
- pending approvals aging despite appearing in digest;
- high manual search rate after catch-up;
- user feedback marking stories as missing, noisy, or incorrectly ranked.

## Digest output template

When applying this skill, produce this artifact:

```yaml
curation_digest_spec:
  product_or_workflow: string
  digest_surfaces:
    - name: async_digest | executive_briefing | command_center_summary | notification_digest | scoped_objective_digest
      human_roles: string[]
      temporal_mode: catching_up | attending_now
      time_windows: string[]
      primary_objects: string[]
  curation_classifier:
    classifier_version_id: string
    inputs: string[]
    classifications: [routine, material, review_candidate, suppressed_duplicate]
    materiality_rules:
      - rule: string
        examples: string[]
    routine_rules:
      - rule: string
        examples: string[]
    safe_fallback: string
  digest_output:
    time_window:
      start: datetime
      end: datetime
      scope: string
    headline:
      text: string
      outcome_metric_ids: string[]
    compressed_activity_stats:
      routine_events_compressed: number
      material_events_surfaced: number
      policy_violations: number
      approvals_created: number
      exceptions_created: number
      agent_actions_auto_completed: number
      trace_link: string
    curated_stories:
      - title: string
        story_type: string
        why_selected: string
        summary: string
        related_objects: string[]
        recommended_human_action: string
        trace_link: string
    prior_decision_outcomes:
      - decision_id: string
        prior_choice: string
        observed_outcome: string
        trace_link: string
    pending_queue:
      - title: string
        object_type: string
        object_id: string
        stakes_rank: string
        due_at: datetime | null
        reason_waiting: string
        recommended_action: string
        trace_link: string
    omitted_explanations:
      - group_label: string
        count: number
        reason_omitted: string
        filtered_trace_link: string
  akka_mapping:
    entities: string[]
    consumers: string[]
    views: string[]
    timed_actions: string[]
    workflows: string[]
    endpoints: string[]
  frontend_requirements:
    screens: string[]
    components: string[]
    drill_downs: string[]
    states: [loading, empty, stale, error, permission_denied]
  tests:
    - name: string
      type: over_compression | under_compression | ranking | traceability | windowing | ui | replay
      expected_result: string
```

## Acceptance checklist

- [ ] Material vs routine classification is defined with safe fallback behavior.
- [ ] Curation classifier inputs and structured outputs are specified.
- [ ] Digest time windows and triggers are explicit.
- [ ] Compressed activity statistics are shown and traceable.
- [ ] Omitted-item explanations are visible and link to filtered traces.
- [ ] Digest includes approximately three curated stories when appropriate.
- [ ] Pending queues are ranked by stakes/value-at-risk, deadline, and risk, not chronology alone.
- [ ] Every digest item links to full audit-complete traces.
- [ ] Akka Consumers, Views, Timed Actions, notifications/endpoints, and React digest screens are mapped.
- [ ] Tests cover over-compression, under-compression, ranking, traceability, windowing, UI, and replay regression.
