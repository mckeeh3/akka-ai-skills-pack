# Agentic Reconstruction: Small Business DCA-First Approach

## Companion document

This document applies the framework set out in `ai-first-saas-design-framework.md` to a specific case: replacing the SaaS stack of a small business that sells and services office business devices (printers, copiers, plotters). It is intended as source material for further specification work, not as a finished spec.

Read the framework document first if approaching this cold. Terms used here (substrate, surface, clause, disposition tag, teach affordance, etc.) are defined there.

## 1. Scenario

The owner of a small business currently operates with three SaaS applications:

- **CRM** for customer and pipeline management.
- **ERP** for billing, inventory, and back-office operations.
- **DCA** (Data Collection Agent) for telemetry from managed devices, supplies fulfillment, and service dispatch automation.

The owner wants to replace these with applications built on the ai-first model — where internal agents perform most of the work currently performed by humans clicking through screens, and the human's role shifts to directing, reviewing, teaching, and auditing.

A comprehensive PRD already exists for the DCA replacement. It contains some references to agent-based functionality but does not yet operate at the level of the framework above. The opportunity is to use the framework to dramatically deepen what an agent-oriented version of these applications could be.

## 2. Why DCA is the right first target

The DCA stands out from CRM and ERP as the first place to apply the ai-first model. Three reasons.

### 2.1 The work-to-clicks ratio is unusually bad

Most of what a human does in a traditional DCA — confirming meter reads look reasonable, generating fulfillment orders when toner crosses a threshold, escalating fuser warnings, reconciling delivery confirmations — is mechanical pattern-matching against policy. These activities are not screens humans should engage with; they are agent activity, with humans seeing only deviations and outcomes.

The autonomy ceiling is genuinely higher in DCA than in CRM (where humans actively want to be in the customer relationship) or ERP (where audit and control concerns push toward more human gating). DCA is the easiest territory for agents to work autonomously.

### 2.2 The owner's domain knowledge is uncaptured

A shop owner with twenty years on the job knows things like:

- "M608s with steady fuser temp warnings past 180k pages need preventive service even with no error."
- "Globex prefers OEM toner even though margins on aftermarket are better."
- "If Wallace's office misses a meter read it's almost always because their IT changed the network — call before dispatching a tech."
- "Don't auto-ship to the Acme satellite office; their receiving has been unreliable since they moved buildings."

This knowledge currently lives in the owner's head and is re-executed as clicks daily. In an agentic DCA it becomes versioned policy that runs continuously — the IDD inversion applied to a domain that genuinely needs it. Capturing and operationalizing this knowledge is a significant standalone value proposition, independent of any other gain from the rebuild.

### 2.3 Substrate consolidation is possible

The most important reframing: the right move is not three agentic apps replacing three SaaS apps. It is *one event-sourced agentic substrate with three thematic surfaces over it*.

The reason a small business owner has CRM + ERP + DCA at all is that vendors carved up the world along functional lines. The actual business has none of those seams. A service dispatch decision wants:

- Billing context (is this customer over their contract page count?)
- Customer context (last NPS, account tenure, recent escalations)
- Inventory context (do we have the part?)
- Device context (recent telemetry trail, service history)

In a traditional stack these are three databases held together by integration glue and a standing reconciliation problem. In the agentic version they are one event stream, one policy substrate, multiple specialized agents, and multiple thematic surfaces over the same underlying state.

This is where "completely change how to create agent-oriented apps" actually applies. Three agentic apps is incremental. One agentic substrate with thematic surfaces is a categorically different system.

## 3. Substrate model

The agentic substrate consists of:

- **Event stream.** All business-meaningful changes — telemetry readings, supply depletions, fulfillment decisions, service dispatches, customer touchpoints, invoicing events, policy commits — are events, ordered and persistent.
- **Policy network.** A versioned document of intent clauses with stable identifiers, governing how agents act on event streams. Clauses can be structured rules, prose guidance, or reference examples.
- **Agent ensemble.** Specialized agents subscribed to relevant event types. For DCA scope the initial set includes: telemetry agent, supplies agent, service agent, billing agent, customer agent. Each agent operates against a specific policy version and produces further events.
- **Surface views.** Screen archetypes from the framework (command center, deviation review, intent editor, async digest) that lens over the substrate for human supervision. Surfaces are derived state, not source of truth.

There is no separate database for "CRM data" versus "ERP data" versus "DCA data." There is one event stream and one policy network. The thematic surfaces simply lens different slices of the same substrate.

## 4. The four surfaces, applied to DCA-first scope

### 4.1 Command Center (attending now)

- **Objective banner.** Examples: SLA target, contract margin floor, on-time fulfillment rate, service response time.
- **Activity stream.** Telemetry-driven actions, fulfillment decisions, service dispatches, billing reconciliations.
- **Approval queue.** Out-of-policy supply orders, unusual service requests, customer-side anomalies the agent cannot resolve, contract overage decisions.
- **Agent roster.** Telemetry agent, supplies agent, service agent, billing agent, customer agent — each with per-agent trust levels.

### 4.2 Deviation Review (deciding now)

Example case: "Customer is at 110% of contract page count and supplies agent wants to ship anyway; recommendation justified by tenure and prior negotiated overage handling."

The screen shows:

- Customer payment history, contract terms, margin impact.
- Alternatives considered: ship and bill overage; ship and waive; deny and notify; partial fulfillment.
- Precedent strip: past owner decisions on similar overage cases with outcomes.
- Teach affordance: updates contract-handling policy if owner approves.

### 4.3 Intent Editor (teaching now)

Sample clauses to model:

- `S-1.0 Supplies fulfillment thresholds` — structured per device class.
- `S-2.1 Customer-specific supply preferences` — by-account overrides (e.g., Globex/OEM rule).
- `D-3.0 Service dispatch authority` — when to dispatch, when to call first, when to escalate.
- `B-1.0 Contract overage handling` — prose guidance with reference examples.
- `T-2.0 Telemetry anomaly classification` — structured detection rules.
- `C-1.0 Customer outreach tone` — prose guidance per customer segment.

Reference examples: past dispatches, fulfillments, and overage decisions tagged as good or to-not-repeat. The Acme satellite office and Wallace IT-change rules from §2.2 become reference cases or per-customer override clauses.

Preview impact: replay against the last 90 days of telemetry and decisions before committing.

### 4.4 Async Digest (catching up)

- **Compression statistic.** Example: "Last 16 hours: 247 telemetry events processed, 14 supply orders placed within policy, 0 policy violations."
- **Headline.** Progress on the operating objective (SLA, margin, response time).
- **Stories.** Typically one positive outcome (a service call closed cleanly), one new risk (a customer device entering pre-failure pattern), one retro (an agent self-flagging that a fulfillment was too aggressive on margin).
- **Pending queue.** Stakes-ranked: a contract renewal in 36 hours; three deviations to review; an agent-proposed clause for a new device class.

## 5. Cautions

### 5.1 The PRD is not waste

The existing DCA PRD encodes domain detail not available from general training: this owner's specific equipment mix, contract structures, supplier relationships, customer types, the operational rituals that have to become policy. It should be treated as the *oracle*, not as the artifact to be discarded.

The right relationship is:

- The PRD enumerates capabilities the system must support.
- The agentic substrate is the architectural container.
- Each PRD capability becomes a regression check: "can the substrate, with appropriate policy and agents, do this?"

This preserves the domain knowledge while changing the architecture. The PRD is reference material for spec generation, not a competing design.

### 5.2 Architecture as procrastination

Re-architecting can be a way to defer shipping. The recommended path is to build one tightly-scoped slice end-to-end as the agentic substrate, prove it works, and then expand. Building the substrate "in general" without a specific slice driving it is a known failure mode.

## 6. Recommended initial slice: supplies fulfillment

Supplies fulfillment is the right first slice for several reasons:

- **High volume.** The agentic loop runs many times per day, generating data quickly.
- **Well-bounded.** The decision surface (when to ship what to whom from which supplier) is comparatively narrow.
- **Clean loop.** Telemetry → depletion projection → policy → ordering decision → invoice impact → owner sees outcome in digest. Every step produces an event. The full event-sourced loop is exercised end-to-end.
- **High value.** Wrong fulfillment decisions are expensive (overship, undership, wrong supplier, wrong margin).
- **Strong policy texture.** Owner-specific knowledge ("Globex prefers OEM," "don't auto-ship to Acme satellite") naturally encodes as clauses, exercising the structured + prose + examples mix that the framework requires.

### 6.1 Slice scope

The supplies fulfillment slice end-to-end includes:

1. Telemetry ingestion from managed devices (page counts, supply levels, error logs).
2. Depletion projection per device per supply type.
3. Policy clauses governing thresholds, supplier choice, customer-specific overrides, contract context, and escalation triggers.
4. A supplies agent that subscribes to depletion events, applies policy, and produces fulfillment events.
5. Integration with at least one supplier order channel.
6. Invoice integration so that fulfillment events affect billing state.
7. Command center stream rendering of supply activity.
8. Deviation review for out-of-policy fulfillments.
9. Async digest reporting of routine fulfillment volume and material exceptions.
10. Intent editor scoped to the supplies-related clauses, with preview impact replay.

If this slice works, the substrate is real. If it doesn't, the architecture is wrong and that is now known early.

### 6.2 Expansion sequence

After supplies fulfillment, the natural sequence is:

1. **Service dispatch.** Same substrate, new agent and clauses, leveraging telemetry and customer context already present.
2. **Contract and billing reconciliation.** Adds ERP-shaped surfaces over the same substrate. Most of the data is already flowing because supplies fulfillment requires it.
3. **Customer relationship surfaces.** CRM-shaped surfaces over the same substrate, leveraging the complete customer context the substrate already maintains.
4. **Sales-side workflows.** Quoting, prospecting, pipeline — the last surface to add because it benefits most from the substrate's full operational history.

At no point during this sequence does data need to be synchronized across boundaries — there are no boundaries.

## 7. Open questions

These should be resolved before further specification work proceeds:

1. **PRD status.** Is the existing DCA PRD being actively implemented, shopped to investors, or sitting? The right next move differs in each case.
2. **Single-customer or product?** Is this build targeting one specific owner, or the archetype across many small businesses? Single-customer can encode idiosyncrasies as policy directly. Product needs an authoring layer per owner, with default-policy templates and tenant isolation.
3. **Equipment and customer specifics.** The PRD presumably details equipment mix and customer profile; these become test fixtures for the substrate.
4. **Legacy integration during transition.** While the substrate is being built, the owner still operates. What integrations or import paths are needed during the migration window? Telemetry from existing DCA platforms? Historical billing data?
5. **Agent ensemble composition.** Which specialized agents are needed for the supplies fulfillment slice alone, and which can be deferred?
6. **Trust calibration starting points.** What disposition (`auto`, `review`, `escalate`, `FYI`) should each policy clause carry by default for the first deployment? Trust is earned over time but must start somewhere.
7. **Supplier integration depth.** Native API, EDI, screen-scraping, or human-in-the-loop ordering? The substrate's fulfillment events need a destination.

## 8. Next specification steps

The two documents in this set (`ai-first-saas-design-framework.md` and this one) provide:

- A conceptual framework with screen archetypes, primitives, and substrate requirements.
- A domain-specific application with first-slice scope and expansion path.

The next layer of specification, intended as input for AI coding harnesses, should include:

- **Per-agent specifications.** Subscriptions, decision authority, output events, interactions with other agents.
- **Per-clause policy templates.** Structured and prose forms, with examples for each clause type.
- **Event schema for the supplies fulfillment slice.** Names, payloads, ordering guarantees, retention.
- **Surface specifications.** Mapping framework primitives to concrete UI components for this domain, including data shapes per component.
- **Test cases drawn from the PRD.** Each PRD capability framed as a substrate regression check.
- **Migration spec.** How the existing DCA's data and operational state is imported into the substrate during transition.

These downstream specs are where the PRD's domain detail, the framework's design rules, and the substrate's architectural requirements converge into something a coding harness can act on.
