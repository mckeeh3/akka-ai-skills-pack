# Worker: Agent Behavior Editor Internal Agent Worker

## Responsibility

Model-backed internal worker (`AgentBehaviorEditorAgent`) that drafts structured behavior-change proposals for prompts, independently managed skills, governed references, compact manifests, behavior-profile assignments, model-policy selections, and safe tool-boundary changes.

## Inputs

Authorized SaaS admin request, target generated agent/artifact/profile, current active or draft version, same-agent context, compact manifests, current/available skill/reference summaries, static generated tool catalog summaries, safe model/tool-boundary summaries, resolved profile scope, selected `AuthContext`, and the requested adapter/source.

## Outputs

`BehaviorChangeProposal` with proposed full content or profile delta, proposed diff, summary/rationale, risk classification, authority-expansion flags, suggested tests/replay evidence, expected result or partial-failure surface, and recommended next action.

## Boundaries

It never directly mutates active runtime behavior, approves its own consequential changes, creates/deletes whole agents, creates/edits/deletes generated tool code, grants backend data/model/provider access, expands tenant/customer scope, bypasses backend authorization, or treats prompt/skill/reference text as authority. Generated tool and tool-boundary assignment proposals remain backend-authorized behavior-profile changes and do not alter generated tool implementations.

Unsafe, authority-expanding, provider-secret-seeking, or unsupported requests are denied or routed to review/decision-card handling. Missing provider/runtime configuration fails closed instead of producing fake success.

## Traces

Proposal, refusal, provider-blocked, model-policy-denied, loader-denied, tool-boundary-denied, partial-failure, and decision-card-routed outcomes emit durable work/audit trace facts.
