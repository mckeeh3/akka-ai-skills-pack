# Conversation Capture

## Source discussion

After all pending `full-core-smb-*` tasks were completed, the assistant recommended Governance/Policy as the next workstream. The user agreed and asked to proceed.

## Accepted decisions

- Create `specs/full-core-smb-governance-policy/` as the next mini-project.
- Sequence Governance/Policy after User Admin, Agent Admin, and Audit/Trace because those workstreams now provide access, behavior, tool-boundary, provider, worker, and investigation evidence.
- Preserve SMB scope; avoid enterprise compliance, policy-as-code, legal hold, SIEM, and governance-office scope creep.
- Keep deterministic services responsible for policy evaluation, proposal lifecycle, simulation normalization, approval/activation/rollback, idempotency, authorization, redaction, and audit/trace.
- Use governed request/response Akka Agent behavior for GovernancePolicyAgent explanations and proposal guidance, with provider fail-closed behavior.
- Introduce policy-impact analysis worker only after deterministic policy/proposal/simulation foundations exist.

## Constraints

- Target is `templates/ai-first-saas-starter/` as the executable baseline.
- Workstream + structured surface UX remains the only product architecture.
- Missing provider/model config must fail closed with actionable surfaces/traces.
- No deterministic/model-less successful normal responses for model-backed GovernancePolicyAgent guidance or workers.
- Visual quality and runtime validation are first-class acceptance criteria.
- One task per fresh harness context.

## Risks

- Governance/Policy can become too broad. Keep the first implementation map focused on SMB policy posture, proposals, simulations, decisions, and evidence links.
- Policy and behavior-authority changes can expand authority. Deterministic lifecycle and human approval must own transitions.
- Simulation output can be mistaken for approval. It must be advisory and deterministic only.
- GovernancePolicyAgent can accidentally imply it changed policy. It may explain or draft, but deterministic lifecycle capabilities commit changes.

## Unresolved questions

No blocking product question is required to start. The implementation-map task must determine the starter's current Governance/Policy source shape and whether existing policy/proposal primitives are sufficient or need a deterministic foundation task first.
