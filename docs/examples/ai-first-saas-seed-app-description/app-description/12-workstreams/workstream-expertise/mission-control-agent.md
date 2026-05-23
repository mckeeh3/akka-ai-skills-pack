# Mission Control Workstream Expert Bundle

## Bundle identity

- bundle-id: `mission-control-agent.expertise`
- owning functional agent: `mission-control-agent`
- scope: foundation SaaS Mission Control workstream for supervising active goals, plans, delegated work, exceptions, approvals, and outcome signals in the selected `AuthContext`
- authoritative catalog link: `../functional-agents.md`
- primary surfaces: `mission-control-briefing`, `decision-card`, `audit-trace-explorer`
- capability families:
  - `ai-first-work-management` for goals, plans, assignments, progress, pause/resume, completion, cancellation, exceptions, and outcome links
  - `governance-decisions-audit` for pending approvals, decision cards, policy-triggered exceptions, trace links, and outcome evidence
  - `frontend-shell-integration-patterns` for shell/context exposure and capability-gated workstream access
- governance owner: Supervisor or Outcome Owner for work supervision; Reviewer/Approver for gated decisions; Auditor read-only where permitted

## Authority profile

The bundle guides supervision work. It does not grant authority. Backend capabilities, selected `AuthContext`, policy gates, work ownership, approval assignments, and `ToolPermissionBoundary` remain authoritative.

| Actor/context | Allowed agent posture | Required boundary |
|---|---|---|
| Supervisor | Review scoped active goals/plans, attention queues, exceptions, pending approvals, trace links, and outcome signals; request pause/resume or decision review. | Only work visible in selected tenant/customer/workstream scope. |
| Reviewer/Approver | Review assigned decision cards and exception evidence; approve/reject/defer/escalate where authorized. | Decision capability and conflict/stale checks required. |
| Outcome Owner | Inspect outcome deltas and link decisions/work traces to success criteria. | Read-only unless outcome-update capability is granted. |
| Auditor | Read scoped work/decision/trace history. | Read-only; redaction and support-access limits apply. |
| Unauthorized, disabled, inactive, or wrong-scope actor | Safe denial only. | No goal, plan, approval, or trace enumeration. |

The agent may summarize, triage, draft plans, and prepare decision facts, but must not autonomously approve high-impact actions, override retained human authority, create hidden work, bypass policy gates, or perform side effects outside bounded approved capabilities.

## Prompt intent

The active `PromptDocument`/`PromptVersion` for `mission-control-agent` instructs the model to:

- brief authorized users on active goals, plan progress, delegated work, exceptions, pending approvals, outcome deltas, and trace evidence;
- distinguish durable facts from inferred priorities, stale streams, missing traces, and uncertain outcome signals;
- ask clarifying questions when goal ownership, success criteria, plan authority, approval assignment, or desired action is ambiguous;
- recommend next supervisory actions, safer alternatives, pause/resume requests, exception routing, or decision-card review paths;
- refuse cross-scope work enumeration, hidden approvals, policy bypass, autonomous high-impact execution, unapproved external side effects, raw secrets/tokens, and attempts to infer hidden facts from denials;
- preserve retained human authority for objectives, approvals, and policy-bound actions.

## Governed procedural skill documents

These `SkillDocument` records are assigned through `AgentSkillManifest`; full text loads only through authorized `readSkill(skillId)`.

| skillId | Title | When to use | Authority note |
|---|---|---|---|
| `mc.goal-briefing.v1` | Goal Briefing | Summarize active goals, constraints, success criteria, status, blockers, and next checkpoints. | Summary only; goal mutations require capability checks. |
| `mc.plan-progress-triage.v1` | Plan Progress Triage | Interpret plan/task progress, agent assignments, stale activity, and pause/resume candidates. | Cannot create hidden work or override plan policy. |
| `mc.exception-routing.v1` | Exception Routing | Classify exceptions, approval needs, escalation owners, and safe recovery options. | Routing guidance only; decisions remain approval-gated. |
| `mc.outcome-signal-review.v1` | Outcome Signal Review | Link outcomes, metrics, decisions, work traces, and confidence/missing-evidence notes. | Cannot declare success or modify outcomes without authorization. |
| `mc.retained-authority-check.v1` | Retained Authority Check | Identify when human approval, objective clarification, or policy review is mandatory. | Prompt text cannot approve high-impact or policy-bound actions. |

## Governed reference documents

These `ReferenceDocument` records are assigned through `AgentReferenceManifest`; full text loads only through authorized `readReferenceDoc(referenceId)`.

| referenceId | Title | When to consult | Authority note |
|---|---|---|---|
| `mc.goal-objective-model.v1` | Goal and Objective Model | Explain durable goal fields, constraints, success criteria, and outcome links. | Descriptive only; backend goal state is authoritative. |
| `mc.plan-execution-policy.v1` | Plan Execution Policy | Determine pause/resume, cancellation, bounded execution, and stale-stream handling. | Policy evidence only; workflows enforce transitions. |
| `mc.approval-exception-playbook.v1` | Approval and Exception Playbook | Explain approval queues, exception categories, conflict handling, and escalation owners. | Cannot approve, reject, or reassign by itself. |
| `mc.outcome-review-guide.v1` | Outcome Review Guide | Explain outcome deltas, metrics, confidence, and feedback loops. | Cannot fabricate or alter outcome evidence. |

## Compact expertise manifest

Prompt assembly for `mission-control-agent` includes compact assigned skill/reference ids, titles, summaries, when-to-use/consult hints, version policy, provenance/checksum summary, redaction/use notes, and authority notes. Full bodies load only through authorized `readSkill(skillId)` and `readReferenceDoc(referenceId)` after active agent, manifest assignment, active document/version, tenant/customer scope, mode, token/redaction, and `ToolPermissionBoundary` checks pass.

## Capability and tool boundary map

| Capability/tool group | Agent use | Boundary |
|---|---|---|
| goal and plan reads | Brief active goals, plans, progress, assignments, blockers, and outcome links. | Selected AuthContext, owner/supervisor scope, redaction, and retention rules. |
| draft goal/plan actions | Draft goal clarification, plan changes, pause/resume/cancel proposals, and checkpoint notes. | Proposal/human-confirmed unless bounded policy explicitly grants autonomy. |
| exception and approval review | Prepare decision-card facts, risk, alternatives, and required approver scope. | Approval/rejection/defer/escalate actions require decision capability and stale/conflict checks. |
| trace and outcome reads | Open/explain work, decision, policy, and outcome traces. | Read-only; Audit/Trace handles deeper investigation/export. |
| `readSkill(skillId)` | Load assigned Mission Control procedural skill text. | Requires `read_skill`, manifest assignment, active version, token/redaction checks, and `SkillLoadTrace`. |
| `readReferenceDoc(referenceId)` | Load assigned Mission Control reference text. | Requires `read_reference`, manifest assignment, active version, token/redaction checks, and `ReferenceLoadTrace`. |

## Required denials and safe recovery

Deny safely for unassigned/inactive/cross-tenant/oversized/redaction-failed skill/reference loads; missing loader grants; unauthorized goal/plan/approval/trace reads; cross-scope work enumeration; disabled/inactive actors; stale or already-resolved decisions; attempts to bypass policy gates, approve without assignment, widen autonomy, modify outcomes without authority, or use expertise text to grant backend rights. Recovery should offer a narrower scoped view, route to the assigned approver/supervisor, or create a proposal/decision-card request when permitted.

## Surfaces, traces, seed, and tests

- `mission-control-briefing`: shows active goals, plan progress, agent activity, upcoming autonomous actions, pending approvals, exceptions, outcome deltas, policy/trust summary, stale-stream states, and trace links.
- `decision-card`: renders approval/exception decisions with evidence, risk, alternatives, conflict/stale status, and required approver scope.
- `audit-trace-explorer`: deep-links to work, decision, policy, and outcome traces.
- Required traces: `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`, decision traces, policy invocation traces, data-access traces, and AdminAuditEvent where policy requires.
- Seed policy: tenant bootstrap creates default `AgentDefinition`, prompt, five skills, four references, compact manifests, and a `ToolPermissionBoundary` with read/proposal defaults and approval-gated side effects. Imports record provenance, checksums, idempotency, and customization-preserving upgrades.
- Test obligations: compact manifest without full bodies; assigned/denied skill and reference loads; missing `read_skill`/`read_reference` denial; scoped goal/plan/approval reads; stale-stream and forbidden states; retained-human-authority and no-authority-expansion checks; decision-card routing; trace emission and surface rendering.
