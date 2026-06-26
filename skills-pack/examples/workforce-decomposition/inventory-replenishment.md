# Workforce decomposition example: inventory replenishment

This example shows the worker-identification step between business requirements and surfaces/capabilities/components.

## Business requirement

Prevent stockouts by monitoring inventory risk, recommending replenishment, comparing vendors, routing policy deviations for review, and helping authorized humans approve or adjust purchase actions.

## Affected workstream

- `inventory-operations` — owned by the Inventory Operations functional agent.

## Worker roster

### Human workers

| Worker | Responsibility | Authority |
|---|---|---|
| Inventory Manager | Supervises stockout risk, reviews recommendations, adjusts reorder quantities. | Can inspect inventory, request recommendations, approve low-risk replenishment within policy. |
| Procurement Lead | Reviews vendor options and purchase recommendations. | Can approve or modify vendor/order selection within spend/vendor policy. |
| Finance Approver | Reviews high-spend or policy-deviation purchases. | Can approve/reject exceptions. |
| Auditor | Investigates inventory decisions, evidence, policy use, and outcomes. | Read/audit only. |

### Agent workers

| Worker | Type | Responsibility | Authority |
|---|---|---|---|
| Inventory Operations Agent | functional-agent | Owns the user-facing inventory workstream; answers prompts, opens dashboards/surfaces, explains recommendations and denials. | Can request allowed read/proposal capabilities; side effects only through explicit governed-tools and policy. |
| Stockout Monitor Agent | autonomous-agent | Runs scheduled/background stockout-risk analysis and produces attention items. | Can create risk findings and recommendation tasks; cannot purchase. |
| Reorder Recommendation Agent | internal-agent | Drafts reorder options from inventory, demand, vendor, and policy evidence. | Can recommend/propose; cannot approve or execute purchase. |
| Vendor Comparison Agent | internal-agent | Compares vendor options for cost, lead time, reliability, and policy fit. | Can produce evidence and ranked options. |
| Policy Deviation Reviewer | evaluator-agent | Evaluates whether a recommendation violates spend, vendor, or risk policy. | Advisory/gating result; escalates deviations to Finance Approver. |

### System workers

| Worker | Type | Responsibility |
|---|---|---|
| Replenishment Workflow | system | Coordinates recommendation, policy review, approval wait, retry, and final outcome. |
| Inventory Projection View | system | Produces scoped inventory risk, stock, demand, and dashboard read models. |
| Daily Risk Timer | system | Starts periodic Stockout Monitor tasks. |
| Inventory Event Consumer | system | Updates attention/projections from stock movement and replenishment events. |

## Behavior profile examples

Every worker has a behavior profile. The profile shape is the same across worker types, but the reasoning/execution engine differs.

| Worker | Reasoning/execution engine | Instructions/prompt | Skills | Tools/adapters |
|---|---|---|---|---|
| Inventory Manager | human | Human-operating brief for supervising inventory risk, reviewing recommendations, and approving policy-compliant replenishment. | stockout-risk review, reorder adjustment, approval policy, exception escalation | `inventory.replenishment.recommend` via `surface_action` and confirmed `human_chat_tool_plan`; `inventory.replenishment.approve` via `surface_action` only unless chat-plan approval is explicitly accepted. |
| Inventory Operations Agent | model | Workstream agent prompt for explaining inventory state, opening surfaces, interpreting human requests, and proposing safe tool plans. | inventory dashboard navigation, evidence explanation, denial explanation, tool-plan drafting | allowed read/proposal tools through `agent_tool_call`; consequential human-backed execution only through confirmed `human_chat_tool_plan`. |
| Stockout Monitor Agent | model | Background monitoring prompt for finding stockout risks and creating recommendation tasks. | risk detection, trend summarization, attention drafting | risk scan/proposal tools via `agent_tool_call`; no purchase/approval tools. |
| Daily Risk Timer | deterministic | Deterministic instruction to trigger scoped stockout monitoring on the accepted schedule with stale-safe idempotency. | schedule policy, trigger provenance, retry/no-op behavior | `inventory.stockout.scan.start` via `timer_invocation`. |

A human text request such as “prepare a reorder for SKU-123” is interpreted by the Inventory Operations Agent through the Inventory Manager behavior profile when the signed-in user is acting as Inventory Manager. The agent may propose a tool plan using the human worker's skills and tools, but consequential governed-tool execution remains human-backed and confirmation-bound.

## Responsibility matrix

| Work unit | Primary worker | Supporting workers | Reviewer/approver | Result surface/event |
|---|---|---|---|---|
| Detect stockout risk | Stockout Monitor Agent | Inventory Projection View, Daily Risk Timer | Inventory Manager | Stockout attention item + dashboard update |
| Draft reorder recommendation | Reorder Recommendation Agent | Vendor Comparison Agent | Inventory Manager | Reorder recommendation surface |
| Check policy fit | Policy Deviation Reviewer | Replenishment Workflow | Finance Approver when deviation | Policy review result / decision card |
| Approve normal replenishment | Inventory Manager | Inventory Operations Agent | Procurement Lead if vendor change | Approval result surface |
| Approve exception | Finance Approver | Policy Deviation Reviewer | Auditor read-only | Exception decision card + audit trace |
| Investigate outcome | Auditor | Inventory Operations Agent | none | Audit timeline / evidence surface |

## Shared governed-tool examples

| Governed-tool | Human adapter | Agent adapter | Notes |
|---|---|---|---|
| `inventory.replenishment.recommend` | Inventory Manager clicks `Generate recommendation` on risk detail; optional confirmed `human_chat_tool_plan` when the manager asks the workstream agent to prepare it. | Inventory Operations Agent may request a recommendation when allowed by tool boundary. | Same capability, separate trace source; human chat-plan execution uses the human worker behavior profile. |
| `inventory.replenishment.approve` | Inventory Manager or Finance Approver submits approval surface. | Not exposed to AI workers by default. | Agent can draft or route, not approve. |
| `inventory.policy.evaluateDeviation` | Human opens policy check from recommendation. | Policy Deviation Reviewer invokes as evaluator/internal tool. | Produces evidence and escalation result. |
| `inventory.audit.openTrace` | Auditor opens audit timeline. | Inventory Operations Agent may summarize allowed trace evidence. | Redaction differs by actor. |

## Authority and runtime boundaries

- All workers have behavior profiles: instructions/prompt, skills, tools, policies/rubrics/examples, evidence profile, assistance mode, and trace obligations.
- Human surface availability does not grant AI tool authority. Each `agent_tool_call` needs an explicit tool-boundary entry, scoped AuthContext or service authority, approval/autonomy policy, and trace source.
- Workflow, timer, consumer, and internal paths should be modeled as system-worker actor adapters (`workflow_step`, `timer_invocation`, `consumer_reaction`, `internal_call`) that invoke the same governed tools with provenance and idempotency.
- Provider or security configuration gaps fail closed with actionable user/system messages and audit/work traces; they are not replaced by mock/model-less normal runtime behavior.
- Runtime-ready evidence would need the real worker → harness → actor adapter → governed tool → capability → Akka/API/UI path, including denial, trace, and outcome checks.

## Akka realization candidates

- Inventory/replenishment decisions and audit-grade approvals: EventSourcedEntity.
- Replenishment orchestration and approval waits: Workflow.
- Inventory risk/dashboard/search payloads: View.
- Scheduled stockout monitoring: Timer + AutonomousAgent task.
- Reorder recommendation, vendor comparison, policy review: Akka Agents with structured responses.
- Inventory events and attention updates: Consumers.
- Browser workstream surfaces: React/Vite workstream shell hosted by Akka HTTP endpoints.

## Readiness questions

- What spend/vendor thresholds require Finance Approver review?
- Can Inventory Manager approve all normal replenishments or only within location/category scope?
- Which evidence fields are visible to Inventory Manager versus Auditor?
- Which recommendations can be generated automatically versus only on human request?
