# Agent Roles and Authority Foundation

## Human roles

| Role | Responsibility in this app |
|---|---|
| Dealer owner / policy owner | Sets business objectives, approves policy and threshold changes, reviews high-impact decisions, and decides whether automation is creating value at acceptable risk. |
| Operations supervisor | Monitors customer/device/collector lifecycle progress, blocked gates, service risk, onboarding, and offboarding. |
| Supplies / inventory owner | Owns stocking rules, shipment exceptions, high-cost approvals, and abnormal consumption review. |
| Billing owner | Reviews missing reads, usage anomalies, final meter reads, billing batch readiness, and invoice-impacting exceptions. |
| Service manager | Reviews SLA risk, emergency dispatch, repeated faults, replacement recommendations, and technician assignment exceptions. |
| Customer success manager | Reviews churn risk, renewal/expansion opportunities, and sensitive customer communications. |
| Data steward / admin | Owns retention, anonymization/deletion, access revocation, integration credentials, and audit package completeness. |

## Delegated work

The app may delegate routine operational work to bounded agents when required data is present and policy allows.

Examples of delegated work:

- summarize overnight activity and rank waiting decisions;
- create onboarding and offboarding plans from known contract/device/customer facts;
- monitor DCA collector health and stale telemetry;
- forecast supply depletion and prepare eligible shipments;
- prepare service tickets from faults and SLA risk;
- prepare billing batches and highlight missing or anomalous reads;
- detect customer risk or upgrade opportunities;
- propose policy improvements from repeated human decisions;
- create audit packages and trace summaries.

## Retained human authority

Humans retain authority for consequential, ambiguous, or policy-changing actions.

Approval or review is required for:

- unusual contract terms or device substitutions during onboarding;
- manual meter baseline acceptance;
- shipment above configured cost thresholds;
- abnormal consumption above policy thresholds;
- customer communication exceptions or sensitive customer-risk outreach;
- emergency dispatch outside contract terms;
- billing batch approval when reads are missing, anomalous, or manually adjusted;
- customer archival, retention hold, deletion, or anonymization decisions;
- DCA removal/deauthorization if ownership, contract, or access status is uncertain;
- activation of policy, threshold, permission, prompt, or skill changes that expand automation authority.

## Agent family routing anchors

The detailed agent-team design and capability contracts refine exact permissions, tools, evidence, and escalation thresholds for these agent families:

- Owner Briefing Agent;
- Onboarding Agent;
- Install Coordinator Agent;
- DCA Monitoring Agent;
- Fleet Health Agent;
- Supplies Agent;
- Meter and Billing Agent;
- Contract and Policy Agent;
- Customer Success Agent;
- Inventory Agent;
- Offboarding Agent.

## Authority rules

1. Agents act inside explicit lifecycle, contract, policy, and permission boundaries.
2. Agents may recommend or prepare human decisions even when they cannot complete the action.
3. Agents must explain confidence, risk, impact, policy trigger, and evidence for escalated decisions.
4. Agents must pause rather than guess when lifecycle state, entitlement, ownership, customer status, or telemetry freshness is uncertain.
5. Human decisions can create precedents or policy proposals, but governed commit remains human-controlled unless a future policy explicitly grants a safe autonomous boundary.

## Fail-safe behavior

When an agent lacks required data or authority, it must:

- keep the affected lifecycle state unchanged or move it to an explicit exception state;
- create an exception or decision item for the accountable human role;
- link all available evidence and missing-data reasons;
- suppress unsafe automated side effects such as shipment, billing, deletion, deauthorization, or customer communication;
- preserve an audit trace for the blocked attempt.
