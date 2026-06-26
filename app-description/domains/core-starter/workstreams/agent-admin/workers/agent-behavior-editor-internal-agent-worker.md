# Worker: Agent Behavior Editor Internal Agent Worker

## Responsibility

Model-backed internal worker (`AgentBehaviorEditorAgent`) that drafts structured behavior-change proposals for prompts, independently managed skills, governed references, and agent behavior-profile changes such as model config reference, skill assignment, and generated tool assignment updates.

## Inputs

Authorized SaaS admin request, target generated agent/artifact/profile, current active or draft version, same-agent context, compact manifests, current/available skill summaries, static generated tool catalog summaries, safe model/tool-boundary summaries, resolved profile scope, and selected `AuthContext`.

## Outputs

`BehaviorChangeProposal` with proposed full content, proposed diff, summary/rationale, risk classification, authority-expansion flags, suggested tests/replay evidence, and recommended next action.

## Boundaries

It never directly mutates active runtime behavior, approves its own consequential changes, creates/deletes whole agents, creates/edits/deletes generated tool code, grants backend data/model/provider access, expands tenant/customer scope, or bypasses backend authorization. Generated tool assignment proposals remain backend-authorized behavior-profile changes and do not alter generated tool implementations. Unsafe or authority-expanding requests are denied or routed to review/decision-card handling.

## Traces

Proposal, refusal, provider-blocked, tool-boundary-denied, and model-policy-denied outcomes emit durable work/audit trace facts.
