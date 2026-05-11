---
name: ai-first-saas-conversation-to-durable-objects
description: Convert conversational ai-first SaaS input into durable goals, plans, tasks, reports, decisions, policies, and queries with validation, previews, audit events, streaming UI, and Akka + React implementation mapping.
---

# ai-first-saas-conversation-to-durable-objects

Use this skill when a coding agent must design or implement conversational entry points for an ai-first SaaS product. The goal is to turn chat, natural-language commands, voice transcripts, or message-like input into durable, inspectable application objects rather than leaving work trapped in an ephemeral transcript.

References:

- `docs/ai-first-saas-coding-agent-framework.md` is the canonical contract for the ai-first loop, chat rule, durable objects, audit events, approval boundaries, and decision UI expectations.
- `docs/ai-first-saas-ui-patterns.md` defines screen patterns for Goal-to-Execution Workbench, Decision Cards, Command Center, Async Digest, and Audit / Work Trace surfaces.
- `docs/skills-pack-tech-stack.md` defines the target Akka + React/Vite/TypeScript stack, including Akka agents, Workflows, HTTP/SSE/WebSocket streaming, Views, and frontend state.

## Core principle

Conversation is an input and navigation modality, not the source of truth. Every meaningful command must resolve into a typed object with owner, status, validation, permissions, policy context, audit trail, and UI affordances for inspection and control.

```text
conversation input
→ intent classification
→ structured extraction
→ validation and clarification
→ object preview
→ human confirmation or automatic activation when low risk and authorized
→ durable object + workflow/event/audit trace
→ structured UI for monitoring, deciding, teaching, or auditing
```

For high-stakes decisions, policy deviations, irreversible side effects, sensitive data actions, or ambiguous authority boundaries, use structured decision UI rather than chat-only interaction.

## Conversation command categories

Classify each user message into one primary command type before acting.

| Command type | Durable object target | Typical surface after conversion |
| --- | --- | --- |
| `create_goal` | `Goal`, `Objective`, `SuccessCriterion`, `Constraint` | Goal-to-Execution Workbench |
| `generate_plan` | `ExecutionPlan`, `AgentAssignment`, `ApprovalGate`, `ToolRequirement` | Goal-to-Execution Workbench |
| `create_task` | `Task`, `WorkflowStep`, domain-specific work item | Command Center or plan detail |
| `request_report` | `Report`, `Digest`, `OutcomeSummary`, `TraceQuery` | Async Digest, report view, trace view |
| `make_decision` | `Decision`, `HumanFeedback`, `ApprovalRequest` update | Decision Card / Deviation Review |
| `propose_policy` | `PolicyProposal`, `PolicyClause` draft, `ReferenceExample` | Policy / Governance Center |
| `ask_query` | `QueryRequest`, `SearchResult`, `TraceQuery` | Query results with links to artifacts |
| `adjust_threshold` | `ThresholdChangeProposal`, `SimulationRequest` | Governance / Simulation Center |
| `cancel_or_pause` | `GoalPauseRequest`, `WorkflowCommand`, `CancellationRecord` | Command Center with confirmation |
| `teach_from_feedback` | `Precedent`, `ReferenceExample`, `PolicyProposal`, `SkillRuleProposal` | Governance / Learning Center |

If a message mixes categories, either split it into multiple proposed objects or ask a clarification question before creating side effects.

## When chat is appropriate

Use conversational input for:

- stating or refining intent;
- asking clarifying questions;
- drafting goals, plans, reports, policy proposals, and examples;
- querying traces, decisions, outcomes, and status;
- navigating to existing artifacts;
- low-risk commands where policy allows automatic activation.

Do not rely on chat alone for:

- high-stakes approvals or exceptions;
- irreversible or externally visible actions;
- permission, threshold, policy, skill, prompt, or tool-access changes;
- sensitive-data exports, deletion, disclosure, or customer-impacting communication;
- decisions requiring evidence comparison, alternatives, policy clauses, risk, confidence, impact, or precedent.

Those cases must produce a structured preview or Decision Card with explicit controls and audit events.

## Conversion procedure

1. **Capture input context**
   - Record actor, tenant, role, source channel, session ID, timestamp, locale, attached files, selected artifacts, and current page context.
   - Link the input to any active goal, decision card, trace, digest item, or domain record the user was viewing.

2. **Classify intent and risk**
   - Classify command type, target object type, urgency, reversibility, sensitivity, likely side effects, and required human role.
   - Assign a disposition: `auto`, `review`, `approval`, `escalate`, or `fyi`.
   - If classification confidence is low or stakes are high, ask clarification or create a review-only draft.

3. **Extract structured fields**
   - Convert natural language into typed fields for the target object.
   - Preserve user wording as source evidence, but normalize dates, owners, constraints, policy references, and success criteria.
   - Identify missing required fields and conflicting instructions.

4. **Validate and authorize**
   - Validate schema, business rules, tenant boundaries, permissions, policies, approval gates, and data/tool scopes.
   - Distinguish model-inferred content from user-confirmed content.
   - Never use a prompt as the enforcement boundary; use backend authorization and policy evaluation.

5. **Clarify when needed**
   - Ask focused questions only for fields needed to safely create, activate, or route the object.
   - Prefer multiple-choice or editable structured suggestions over open-ended back-and-forth when the answer affects execution.
   - Store clarifications as part of the conversion trace.

6. **Preview before activation**
   - Show a structured object preview with extracted fields, assumptions, unresolved questions, required permissions, approval gates, risk/confidence, affected artifacts, and planned side effects.
   - Let the user edit fields before activation.
   - For low-risk, reversible, authorized actions, preview may be compact or skipped only if policy explicitly allows.

7. **Persist and emit events**
   - Create the durable object and initial state transitions.
   - Emit audit events for input received, classification, extraction, clarifications, preview shown, confirmation, object creation, activation, rejection, or cancellation.
   - Link the conversation segment to the created artifact and its WorkTrace.

8. **Route to the right surface**
   - After conversion, move the user to the durable object's primary surface: workbench for goals/plans, decision card for approvals, governance center for policy changes, command center for active work, audit trace for query explanations.
   - Keep chat available as a follow-up affordance, not the only control plane.

## Clarification rules

Ask clarification when:

- required fields are missing for the target object;
- the user names ambiguous domain records, customers, accounts, or policies;
- the request implies side effects but does not specify authority, scope, or timing;
- extracted constraints conflict with active policies or permissions;
- the command may create high-stakes, irreversible, or externally visible action;
- confidence in command type or object mapping is below the configured threshold.

Clarification questions should include:

- the specific missing or ambiguous field;
- why it matters for safety, policy, or execution;
- suggested choices when possible;
- an option to save as draft instead of activating.

## Object preview requirements

A valid preview includes:

- target object type and proposed title;
- normalized structured fields;
- original user instruction excerpt;
- assumptions and inferred values;
- unresolved questions;
- policies and clauses likely to apply;
- agent/team assignments if execution is planned;
- tool/data permissions required;
- risk, confidence, impact, stakes, and reversibility;
- approval gates and human roles needed;
- actions available: edit, ask follow-up, save draft, simulate, activate, cancel.

High-stakes previews must route to a Decision Card or governance approval flow before side effects occur.

## Audit events

At minimum, define audit events for:

```yaml
audit_events:
  - ConversationInputReceived
  - ConversationIntentClassified
  - ConversationFieldsExtracted
  - ConversationClarificationRequested
  - ConversationClarificationAnswered
  - ConversationObjectPreviewGenerated
  - ConversationObjectPreviewEdited
  - ConversationObjectConfirmed
  - ConversationObjectCreated
  - ConversationActivationRequested
  - ConversationActivationApproved
  - ConversationActivationRejected
  - ConversationCommandCancelled
  - ConversationRoutedToStructuredDecision
```

Each event should include `tenant_id`, `actor_id`, `session_id`, `source_message_id`, `target_object_type`, `target_object_id` when available, `classifier_version_id`, `extractor_version_id`, `policy_version_id`, `risk_score`, `confidence_score`, and `trace_id`.

## Akka + React implementation mapping

Backend:

- Use an **Akka agent** for intent classification, structured extraction, clarification drafting, and preview summarization.
- Use **Event Sourced Entities** for high-value objects such as `Goal`, `ExecutionPlan`, `Decision`, `PolicyProposal`, and audit-critical conversion sessions.
- Use **Key Value Entities** for low-risk draft previews or current conversation session state when full event sourcing is unnecessary.
- Use **Workflows** for multi-step conversion flows: classify → clarify → preview → confirm → create object → activate or pause for approval.
- Use **Consumers** to create audit records, update projections, notify users, and trigger follow-on planning after object creation.
- Use **Views** for conversation session lists, drafts awaiting confirmation, created-object lookup, and Command Center queues.
- Use **Timed Actions** for expiring stale previews, reminding users about pending clarifications, and cancelling abandoned activation requests.
- Use **HTTP endpoints** for command submission, preview edits, confirmation, and artifact navigation.
- Use **SSE or WebSocket streaming** for long-running extraction, plan generation, simulation, and activation progress.
- Expose **MCP endpoints** only for safe tool/resource access with the same permission and audit boundaries.

Frontend:

- Build React/Vite/TypeScript components for `ConversationComposer`, `ClarificationPanel`, `ObjectPreviewCard`, `StreamingExtractionProgress`, `ActivationChecklist`, and `ArtifactLinkPanel`.
- Maintain optimistic UI only for drafts; do not show high-stakes side effects as complete until backend events confirm authorization and persistence.
- Stream partial extraction and planning progress, but mark fields as `draft`, `inferred`, `validated`, `needs_clarification`, or `confirmed`.
- Route users from chat to the appropriate structured surface after confirmation.

## Streaming UI behavior

For long-running object creation:

- show phases: received, classifying, extracting, validating, checking policy, generating preview, waiting for clarification, creating object, activating workflow;
- stream partial fields with provenance labels rather than pretending they are final;
- allow cancellation before side effects;
- keep a persistent link to the eventual durable artifact;
- handle retries idempotently using a client command ID and backend conversion session ID;
- surface errors as recoverable states with retained draft data.

## Conversion output template

Use this template when specifying a conversation-to-object flow.

```yaml
conversation_conversion_spec:
  flow_name: string
  source_channels:
    - chat | voice_transcript | email | command_bar | digest_followup | decision_followup
  actor_roles_allowed:
    - intent_author | supervisor | reviewer | exception_handler | policy_owner | auditor | outcome_owner
  command_types_supported:
    - create_goal | generate_plan | create_task | request_report | make_decision | propose_policy | ask_query | adjust_threshold | cancel_or_pause | teach_from_feedback

  target_objects:
    - object_type: Goal | ExecutionPlan | Task | Report | Decision | PolicyProposal | QueryRequest | ThresholdChangeProposal | Precedent
      required_fields: string[]
      optional_fields: string[]
      default_status: draft | pending_review | active | pending_approval
      primary_surface: goal_to_execution_workbench | command_center | decision_card | governance_center | async_digest | audit_trace

  classification:
    classifier_version_id: string
    confidence_thresholds:
      auto_create_draft: number
      ask_clarification: number
      require_human_review: number
    risk_routing_rules:
      auto: string[]
      review: string[]
      approval: string[]
      escalate: string[]

  extraction_schema:
    fields:
      - name: string
        type: string
        required: boolean
        source: explicit_user_text | inferred | default | selected_context | policy_lookup
        validation: string
    missing_field_strategy: ask_clarification | save_draft | reject
    conflict_strategy: ask_clarification | prefer_policy | route_to_decision_card

  preview:
    required: boolean
    required_sections:
      - extracted_fields
      - assumptions
      - unresolved_questions
      - policy_context
      - permissions_required
      - risk_confidence_impact
      - approval_gates
      - side_effects
    user_actions:
      - edit | save_draft | simulate | activate | send_for_approval | cancel

  activation:
    allowed_without_preview: boolean
    side_effect_boundary: none_before_confirmation | reversible_only | approval_required
    approval_required_when: string[]
    idempotency_key: client_command_id | conversion_session_id

  audit:
    conversion_trace_id: string
    events_required: string[]
    transcript_retention: string
    redaction_rules: string[]
    links_created:
      - source_message_to_object
      - object_to_work_trace
      - object_to_policy_invocation

  akka_mapping:
    agents: string[]
    entities: string[]
    workflows: string[]
    views: string[]
    consumers: string[]
    streaming_endpoint: HTTP_SSE | WebSocket | none

  react_mapping:
    screens_or_components: string[]
    state_labels:
      - draft | inferred | validating | needs_clarification | validated | confirmed | activating | active | failed
    navigation_after_confirmation: string

  tests:
    - scenario: string
      input: string
      expected_object: string
      expected_clarification: string | null
      expected_audit_events: string[]
      expected_surface: string
```

## Quality checklist

- [ ] Conversational input creates or updates durable typed objects, not only transcript text.
- [ ] The flow records classification, extraction, validation, preview, confirmation, and creation events.
- [ ] Missing or ambiguous high-impact fields trigger clarification.
- [ ] Users can inspect and edit object previews before activation when risk warrants it.
- [ ] High-stakes decisions route to Decision Cards or structured governance UI, not chat-only approval.
- [ ] Permissions, policies, and approval gates are enforced in backend code.
- [ ] Streaming progress distinguishes inferred draft fields from validated or confirmed fields.
- [ ] Created objects link back to the source conversation and forward to WorkTrace/AuditEvent records.
- [ ] React UI routes users to the appropriate structured surface after conversion.
- [ ] Tests cover ambiguous commands, mixed-intent commands, permission failures, stale previews, cancellation, idempotent retry, and high-stakes decision routing.
