# Agent-First SaaS: Design Framework

## Purpose

This document captures the design framework for SaaS applications built around AI agents as primary doers, with humans in directing, reviewing, teaching, and auditing roles. It is intended as source material for creating concrete, agent-readable specifications that guide AI coding tools in constructing such applications.

The framework was developed through an exploration of how traditional SaaS UX must transform when internal AI agents perform most of the work that humans previously performed directly. It is grounded in concrete UI patterns for four screen archetypes, each addressing a distinct temporal mode of human engagement with an agentic system.

A companion document, `dca-agentic-reconstruction.md`, applies this framework to a specific case (replacing CRM + ERP + DCA for a small business owner who sells and services office devices).

## 1. Core thesis: the role inversion

Traditional SaaS UX is built around a single assumption: the application is a tool that helps a human do work faster. Every screen is a workspace; every form is a place for human input; every dashboard is a place for human attention. The unit of design is the task.

When internal agents do the work, that assumption inverts. The human is no longer the one moving information through the system. They are directing the system that moves information. The unit of design becomes the *delegation*: what the agent is allowed to do, on what authority, with what oversight, to what end.

This is not the addition of AI features to a traditional CRUD application. It is a different category of system, with different architectural requirements and different UI primitives.

## 2. The five new primary human roles

In an agentic SaaS application, the human's job decomposes into five primary activities that did not exist as primary in traditional SaaS:

### 2.1 Intent author

Someone must specify what good outcomes look like — goals, constraints, tradeoffs, tone, escalation thresholds, what counts as done. In a traditional CRM this was implicit in human judgment as each ticket was worked. In an agentic system it must become explicit, durable, and editable, because it is the contract the agents execute against.

### 2.2 Reviewer

Agents produce decisions faster than humans can read them. The system's job is to surface the right slice — novel decisions, high-stakes ones, ones the agent itself flagged as uncertain, ones that violate a soft policy — and let everything else flow. Approval is not the default; selective interception is.

### 2.3 Exception handler

When agents reach the edge of their competence or authority, the human takes over. The system must hand off cleanly: full context, prior reasoning, options considered. The handoff itself is a first-class screen.

### 2.4 Coach

Every override, correction, or rejection is a training signal. The system should treat human corrections as edits to policy, not just fixes to a single action. A "fix" in one place permanently changes future behavior everywhere.

### 2.5 Auditor

"Why did this happen?" becomes a frequent and important question. Reasoning traces, decision provenance, and policy attribution must be one click away from any artifact, not buried in logs.

## 3. UI primitive transformations

The shift from operator to director changes which UI primitives are first-class:

| Traditional primitive | Agentic equivalent |
|---|---|
| Forms | Intent statements (sometimes conversational) |
| Lists / tables | Activity streams (data accessible via drill-down) |
| Dashboards | Goal-progress views (outcome-oriented, not metric grids) |
| Workflows | Policy specs (declarative, not imperative) |
| Notifications | Approval requests (with rich reasoning context) |
| Search / navigation | Conversational query plus jump-to-artifact |
| Settings | Versioned policy document |

Tables and lists do not disappear — they remain accessible — but they are no longer the primary view. The agent's interaction with the data is what the human reads.

## 4. Foundational design tensions

Every screen in an agentic SaaS application sits inside two primary tensions that must be explicitly resolved:

### 4.1 Visibility versus anxiety

Showing every agent action creates noise; showing too few creates dread. Progressive disclosure tied to trust calibration is the only way out. Routine agent activity should be visible but compressed; material activity should be surfaced. The compression itself must be transparent ("138 routine actions auto-handled") so the human knows what was hidden.

### 4.2 Speed mismatch

Agents act in seconds; humans review in minutes. The UI must be designed for batch review and asynchronous oversight, not real-time approval-blocking. Otherwise the human becomes the bottleneck the architecture was supposed to eliminate.

Two further tensions to track:

- **Override versus autonomy.** When humans intervene, that signal matters. The UI should make corrections feel like teaching, not just fixing.
- **Plurality versus unified experience.** Multiple specialized agents should not feel like a fragmented multi-tool UX. The human sees one application; the agents are an ensemble underneath.

## 5. The four temporal modes of human engagement

A human supervisor of an agentic system operates in four distinct temporal modes. Each requires its own screen archetype with its own design rules.

| Mode | Question being answered | Screen archetype |
|---|---|---|
| Attending now | What is happening? Where do I focus? | Command Center / Dashboard |
| Deciding now | This needs my judgment — what should I do? | Deviation Review |
| Teaching now | What should "good" mean from now on? | Policy / Intent Editor |
| Catching up | What happened while I was away? | Async Digest |

The remainder of this document specifies each archetype.

## 6. Screen archetype: Command Center (attending now)

### Purpose

A persistent view of present-moment system activity, oriented around the current objective, the ongoing agent activity, the human's pending attention queue, and the ensemble of agents at work.

### Key primitives

- **Objective banner.** Persistent at top. The contract everything else executes against. Replaces the traditional landing page that lists records.
- **Policy chips.** Inline summary of which actions run autonomously, which require review, which require approval. Trust calibration made visible.
- **Activity stream.** Primary scan target. Each item carries a disposition tag (`auto`, `review`, `escalate`, `FYI`). Auto-actions are still visible — audit is preserved even when the obligation to act is not.
- **Approval queue.** What the inbox becomes. Each item carries a reasoning summary inline; the question is "do I trust this judgment?" not "what is this?".
- **Agent roster.** Each agent treated as a quasi-employee: load, status, trust level. Trust is per-agent, not a global on/off toggle.

### Design rules

- No traditional top-level navigation across data entities. Records exist in the data model but are not the primary axis of navigation.
- Stream items make the disposition tag visible per row. Trust calibration appears at the leaf level, not just in settings.
- Approval cards include the agent's reasoning summary inline. The card itself is the context for the decision.
- The roster's trust controls are interactive and per-agent. A global "AI on/off" toggle is the wrong abstraction.

## 7. Screen archetype: Deviation Review (deciding now)

### Purpose

A focused decision view for a single agent action that has reached the edge of its authority. The human's job is not to "do" the work — the agent has already weighed alternatives and made a recommendation — but to interrogate that recommendation.

### Key primitives

- **Deviation statement.** Explicit. Names the violated policy clause by stable identifier, the requested value, and the gap from the ceiling.
- **Recommendation block.** The agent's proposed action with confidence indicator and projected outcome.
- **Reasoning trace.** Structured factors-for and factors-against, not narrative prose. Each factor is a discrete consideration.
- **Alternatives considered.** The decision space the agent explored. Each alternative names the approach, its projected outcome, and the reason it was rejected. This is the most novel primitive on the screen — traditional approval flows hide the decision space.
- **Precedent strip.** The human's own past decisions on similar cases, with outcomes. Calibration data, fed back to support consistency.
- **Decision panel.** Approve / counter / reject buttons, plus an optional note that becomes part of the audit trail.
- **Teach affordance.** A checkbox that turns the human's decision into a policy update applicable to future similar cases. Approving once and updating policy are the same gesture.

### Design rules

- Deviation must cite a stable clause identifier. This requires policy to be addressable — clauses are nodes, not paragraphs.
- Alternatives must be retained from the agent's actual deliberation. The agent runtime must preserve decision-space exploration, not just chosen actions.
- The teach affordance collapses two operations: per-case approval and policy update. The system must safely propagate the rule change without over-generalizing.
- No chat panel. Conversational interfaces are good for ambiguous discovery; they are worse than structured UI for high-stakes decisions where scanning and comparing matter.

## 8. Screen archetype: Policy / Intent Editor (teaching now)

### Purpose

The screen where the human composes and revises the specification that agents execute against. This is not a settings page. It is a substantive authoring environment for the intent layer.

### Key primitives

- **Versioned document.** Every change is a commit. The document is the artifact; agents are stateless executors that bind to a specific version.
- **Recent-changes timeline.** Three distinct provenance types are first-class:
  1. Direct human edits.
  2. Teach-from-decision events (commits authored by the approval flow).
  3. Agent-proposed clauses (pending human review).
- **Mixed structured and prose clauses.** Some intent is best expressed as numeric rules with operators; some as natural-language guidance; some as positive and negative reference examples. All three coexist as clause types within a single document.
- **Clause addressing.** Each clause has a stable identifier (e.g., `R-3.2`, `O-1.1`, `E-2.0`). Required for citation by the deviation review screen and for invocation analytics.
- **Compiled-cleanly indicator.** Static analysis of natural-language clauses to detect ambiguity. Acts as a type-check for intent.
- **Reference examples.** Positive and negative cases pinned to a clause. Few-shot provisioning of agent behavior, lived inside the policy document.
- **Preview impact panel.** Replays the proposed policy version against historical decisions. Reports concrete deltas (e.g., "+12 renewals would have closed without escalation; −5 deviations would have left your queue; 0 net new churn signals"). Regression testing for policy.

### Design rules

- The user works at the intent level. The compiled rules the agents execute are a system concern, not a primary view.
- Agent-proposed clauses are first-class but gated by human review. The system can suggest improvements; humans authorize them.
- Negative examples are as important as positive ones. "Do not repeat" is a core teaching primitive.
- The document has structure (sections, clauses, scopes) but reads as a document, not as code.

## 9. Screen archetype: Async Digest (catching up)

### Purpose

The view a human sees when returning to the system after time away. Compresses a period of autonomous agent activity into something a human can absorb in a few minutes while preserving the ability to drill into anything that matters.

### Key primitives

- **Time scope and compression statistic.** "Last 16 hours · 138 routine actions auto-handled · 0 policy violations." A trust contract: agents did a lot of work without you, and stayed inside the lines.
- **Outcome-shaped headline.** The single most important fact, framed as progress against an objective rather than as activity volume. Includes a sparkline or trajectory to contextualize whether the moment is a blip or a pattern.
- **Curated stories (typically three).** Material events worth knowing. Journalistic structure: usually one closure (validation of past effort), one new risk (something the human couldn't have known), and one retro (system reflecting on its own performance, sometimes surfacing an agent self-critique).
- **Outcome tracking on prior decisions.** Past human decisions are tracked to outcomes ("your three decisions yesterday: two closed positive, one in progress"). Closes the feedback loop traditional SaaS rarely closes.
- **Stakes-ranked queue.** Pending items ranked by value-at-risk, not chronology. Requires the system to estimate stakes per item.
- **Conversational fallback.** A single input for "anything not in this brief." De-emphasized; if used heavily, the curation isn't pulling its weight.

### Design rules

- Curation transparency is mandatory. Hide nothing about what was hidden.
- Routine activity is aggressively compressed. The compression footnote is the right granularity for routine; individual rendering is wrong.
- The headline is a system-level judgment. Picking which fact is most important is an agent task in itself.
- Three stories, not five or ten. More than three, and the brief stops being a brief.

## 10. Architectural through-line

All four screen archetypes depend on the system retaining substantially more state than a traditional SaaS application does:

- **Decisions remember why they were made.** Reasoning, alternatives considered, confidence, the policy version under which the agent was operating. Required by deviation review and audit.
- **Policy remembers when and how it changed.** Versioned commits with provenance (human edit, teach-from-decision, agent proposal). Required by intent editor and replay.
- **Outcomes remember which decisions led to them.** Required by digest's "your decisions yesterday" tracking and by precedent surfacing.
- **Routine activity is recorded but compressed for display.** Audit-complete, attention-light.

Event sourcing is the natural fit. Every decision is an event; every policy change is an event; every outcome is an event. Replay capability is required for the policy editor's preview-impact feature, which simulates a proposed policy version against historical decision events.

Bolting these screens onto a typical CRUD-shaped backend produces hollow results because the substrate lacks the memory the new UX requires.

## 11. Substrate requirements

Any ai-first SaaS application must provide the following substrate capabilities. These are intended as gates against which downstream specifications can be validated.

1. **Event-sourced state.** All meaningful changes are events. State is derivable from event history.
2. **Decision provenance.** Each agent decision retains its inputs, alternatives considered, scoring, chosen action, and policy version.
3. **Versioned, addressable policy.** Policy is a document with stable clause identifiers, full version history, and the ability to bind a specific version to a specific agent execution.
4. **Multi-source policy commits.** Direct edits, teach-from-decision events, and agent proposals all flow through the same versioning mechanism, with provenance preserved per commit.
5. **Replay capability.** Historical decisions can be re-evaluated under a hypothetical alternative policy version. Required for preview-impact.
6. **Static intent analysis.** Natural-language policy clauses are checked for ambiguity and compiled into agent-executable rules, with the compilation result observable.
7. **Per-clause invocation analytics.** Each clause tracks how often it is referenced and applied.
8. **Per-action disposition tagging.** Every agent action is tagged with its disposition (`auto`, `review`, `escalate`, `FYI`) at decision time, derived from policy.
9. **Stakes estimation.** The system can rank pending items by value-at-risk, not chronology.
10. **Curation classifier.** The system can identify which agent activity is material versus routine, and which past decisions are worth surfacing as outcomes.

## 12. Design template for new screens

When designing a new screen for an agentic SaaS application, work through the following questions:

1. **Which temporal mode does this screen address?** Attending, deciding, teaching, or catching up? A single screen should not span multiple modes.
2. **What is the human's role on this screen?** Author, reviewer, exception handler, coach, or auditor? Each role has different attention and information needs.
3. **What is being delegated and what is being retained?** What does the agent do; what does the human decide?
4. **What state must be retained to make this screen possible?** Decision provenance, policy versions, outcome links, replay data?
5. **What is the compression strategy?** Routine compressed to what granularity; material surfaced how?
6. **Where does the teach affordance live?** Every screen where the human exercises judgment should have a path to encode that judgment as policy.
7. **What is deliberately *not* on this screen?** Identifying what to leave out is as important as what to include.

## 13. Glossary of terms

- **Agentic substrate.** The unified event stream, policy network, and agent ensemble that underlies an ai-first application. Distinct from the *surfaces* (screens) that lens over it.
- **Clause.** A single addressable unit of policy. May be structured, prose, or examples-based. Has a stable identifier.
- **Disposition tag.** The per-action label assigned at decision time indicating whether the action ran autonomously, requires review, was escalated, or is informational only.
- **Material event.** An agent activity worth surfacing to the human; contrasted with *routine activity* which is compressed.
- **Policy commit.** A versioned change to the policy document, with one of three provenances: direct edit, teach-from-decision, or agent proposal.
- **Replay.** The ability to re-evaluate historical decisions under an alternative policy version, used for preview-impact.
- **Surface.** A screen archetype that lenses over the substrate. Surfaces are derived state, not source of truth.
- **Teach affordance.** A UI control that turns a single human decision into a durable policy update.
- **Trust calibration.** The mechanism, expressed both in policy chips and per-agent settings, by which the human controls how much agent activity runs without intervention.
