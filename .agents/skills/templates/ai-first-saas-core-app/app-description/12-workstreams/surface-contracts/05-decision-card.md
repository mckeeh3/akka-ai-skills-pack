# Surface Contract: Decision Card

- surface-id: `decision-card`
- type/version: decision-card/v1
- owner functional agent: `governance-policy-agent` (Governance/Policy)
- reusable by: User Admin, Agent Admin, Audit/Trace, and domain-specific reviewer workstreams for approval, exception, access-review, behavior-change, and outcome-review gates.

## Placement and graph role

A decision card is an attention item and action gate. It appears when a recommendation, risky command, policy deviation, behavior change, access risk, or exception needs human judgment before commit or escalation.


## User-visible/internal metadata boundary

Default rendering must use SaaS product language and show only information the current actor needs to decide, act, recover, or understand the business outcome. Internal ids, raw trace/event/correlation data, governed-tool/capability ids, backend component names, prompt/provider/model details, and policy implementation references are implementation metadata. Expose them only in authorized admin, support, auditor, or developer drilldowns, and keep them visually subordinate to user-meaningful labels.

## Payload summary

Payload must include:

- user-visible decision title, affected business object, lifecycle status, recommendation, confidence/risk/impact summary, requested action, due/SLA data, and plain-language reviewer requirement;
- default-visible evidence summary, alternatives, and consequence copy that help the reviewer decide without reading raw trace/policy internals;
- role-gated diagnostic metadata: decision id, source workstream/surface/action ids, selected `AuthContext`, `correlationId`, trace ids, policy/version references, required reviewer role/capability, redacted evidence source refs, and omitted-field markers;
- action descriptors for approve, reject, request changes, defer, escalate, request evidence, open source surface, and open audit trace.

## Compact payload schema

```ts
type DecisionCardData = {
  title: string;
  subjectLabel: string;
  lifecycleStatus: string;
  recommendation: { summary: string; confidence?: number; risk: string; impact: string; alternatives: string[] };
  visibleEvidenceSummary: string;
  dueAt?: string;
  diagnostics?: {
    decisionId: string;
    source: { workstreamId: string; surfaceId: string; actionId?: string; capabilityId?: string };
    evidenceRefs: Array<{ refId: string; refType: string; label: string; redactionMarkers: string[] }>;
    requiredReviewer: { roleOrCapability: string; approvalPolicyRef?: string };
  };
};
```

## Allowed actions

| Action | Capability hint | Qualified exposure | Result surface |
|---|---|---|---|
| Approve decision | `decisions.approve` | browser-tool | success `system_message`, source surface update, workflow progress |
| Reject decision | `decisions.reject` | browser-tool | source surface update and audit trace |
| Request changes | `decisions.request_changes` | browser-tool | proposal/diff update or `system_message` |
| Defer | `decisions.defer` | browser-tool | attention lifecycle update |
| Escalate | `decisions.escalate` | browser-tool | target reviewer queue item |
| Request more evidence | `decisions.evidence.request` | browser-tool, internal-tool | task progress/result surface |
| Open trace/evidence | `audit.traces.view` | browser-tool | `audit-trace-explorer` |

## Action mapping

| actionId | browserToolId | governedToolId | capabilityId | exposure | resultSurfaceId | idempotency | traceRequired |
| --- | --- | --- | --- | --- | --- | --- | --- |
| `decision.approve` | `decision-card.approve` | `decisions.approve` | `governance-decisions-audit` | browser-tool | `system_message`, source surface update, or deferred `task-progress-surface` | decision id + reviewer id + request id | true |
| `decision.reject` | `decision-card.reject` | `decisions.reject` | `governance-decisions-audit` | browser-tool | source surface update or `system_message` | decision id + reviewer id + request id | true |
| `decision.request-changes` | `decision-card.request-changes` | `decisions.request_changes` | `governance-decisions-audit` | browser-tool | deferred `behavior-diff-review` or `system_message` | decision id + reviewer id + request id | true |
| `decision.defer` | `decision-card.defer` | `decisions.defer` | `governance-decisions-audit` | browser-tool | source attention update or `system_message` | decision id + defer reason + request id | true |
| `decision.escalate` | `decision-card.escalate` | `decisions.escalate` | `governance-decisions-audit` | browser-tool | target reviewer queue item or `system_message` | decision id + target reviewer + request id | true |
| `decision.request-evidence` | `decision-card.evidence.request` | `decisions.evidence.request` | `governance-decisions-audit` | browser-tool, internal-tool | deferred `task-progress-surface` or `system_message` | decision id + evidence request id | true |
| `decision.open-trace` | `decision-card.trace.open` | `audit.traces.view` | `governance-decisions-audit` | browser-tool | `audit-trace-explorer` | trace id | true |

## UI states

- `loading`: show decision skeleton without unsafe recommendation text.
- `empty`: no decision selected or decision already resolved.
- `error`: safe category and readable support/reference link; raw trace/correlation detail appears only in authorized diagnostic detail.
- `forbidden`: do not reveal recommendation/evidence to unauthorized reviewers.
- `conflict`: decision changed/resolved by another actor; show refresh and current lifecycle state.
- `approval-needed`: nested approval or escalation required.

## Auth/security

- Reviewer authority is enforced by backend capability, not by visible buttons.
- Evidence is redacted by reviewer role and selected scope.
- Approval/rejection writes are idempotent and audited with decision basis and policy version.
- Agent-authored recommendations cannot grant authority or expand allowed actions.

## Rendering and capability tests

- Decision cards render user-readable evidence, risk, confidence, impact, alternatives, action status, and role-appropriate trace/policy links; raw policy refs and diagnostic ids are hidden or subordinate unless the reviewer role is authorized to inspect them.
- Unauthorized reviewers see forbidden/system-message surfaces without hidden facts.
- Approve/reject/defer/escalate actions invoke correct capability ids, preserve idempotency, and update source/dashboard attention.
- Conflict and already-resolved cases produce safe no-op or conflict system messages.
- Audit/work traces include recommendation source, reviewer, policy basis, action, outcome, and correlation id.
