# Surface Contract: Decision Card

- surface-id: `decision-card`
- type/version: decision-card/v1
- owner functional agents: User Admin, Agent Admin, Governance/Policy, or a domain-specific reviewer agent depending on the decision source
- reusable surfaces: may be embedded in attention queues, approval flows, governance diff review, access review, and outcome review surfaces.

## Placement and graph role

A decision card is an attention item and action gate. It appears when a recommendation, risky command, policy deviation, behavior change, access risk, or exception needs human judgment before commit or escalation.

## Payload summary

Payload must include:

- decision id, source workstream/surface/action ids, selected `AuthContext`, `correlationId`, trace ids, policy/version references, and lifecycle status;
- recommendation, evidence, confidence, risk, impact, affected entities, alternatives, requested action, due/SLA data, and required reviewer role/capability;
- redacted evidence bundle with source refs and omitted-field markers;
- action descriptors for approve, reject, request changes, defer, escalate, request evidence, open source surface, and open audit trace.

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

## UI states

- `loading`: show decision skeleton without unsafe recommendation text.
- `empty`: no decision selected or decision already resolved.
- `error`: safe category and trace/correlation link.
- `forbidden`: do not reveal recommendation/evidence to unauthorized reviewers.
- `conflict`: decision changed/resolved by another actor; show refresh and current lifecycle state.
- `approval-needed`: nested approval or escalation required.

## Auth/security

- Reviewer authority is enforced by backend capability, not by visible buttons.
- Evidence is redacted by reviewer role and selected scope.
- Approval/rejection writes are idempotent and audited with decision basis and policy version.
- Agent-authored recommendations cannot grant authority or expand allowed actions.

## Rendering and capability tests

- Decision cards render evidence, risk, confidence, impact, alternatives, policy refs, action status, and trace links.
- Unauthorized reviewers see forbidden/system-message surfaces without hidden facts.
- Approve/reject/defer/escalate actions invoke correct capability ids, preserve idempotency, and update source/dashboard attention.
- Conflict and already-resolved cases produce safe no-op or conflict system messages.
- Audit/work traces include recommendation source, reviewer, policy basis, action, outcome, and correlation id.
