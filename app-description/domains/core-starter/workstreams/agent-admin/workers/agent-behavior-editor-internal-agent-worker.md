# Worker: Agent Behavior Editor Internal Agent Worker

## Responsibility

Model-backed internal worker (`AgentBehaviorEditorAgent`) that drafts structured behavior-change proposals for prompts, skills, and governed references.

## Inputs

Authorized SaaS admin request, target agent/artifact, current active or draft version, same-agent context, compact manifests, safe model/tool-boundary summaries, and selected `AuthContext`.

## Outputs

`BehaviorChangeProposal` with proposed full content, proposed diff, summary/rationale, risk classification, authority-expansion flags, suggested tests/replay evidence, and recommended next action.

## Boundaries

It never directly mutates active runtime behavior, approves its own consequential changes, grants new tools/data/model/provider access, expands tenant/customer scope, or bypasses backend authorization. Unsafe or authority-expanding requests are denied or routed to review/decision-card handling.

## Traces

Proposal, refusal, provider-blocked, tool-boundary-denied, and model-policy-denied outcomes emit durable work/audit trace facts.
