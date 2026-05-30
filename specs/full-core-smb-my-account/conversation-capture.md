# Conversation Capture

## Source discussion

After all pending `full-core-smb-*` tasks were completed, the assistant recommended My Account as the next workstream. The user agreed and asked to proceed.

## Accepted decisions

- Create `specs/full-core-smb-my-account/` as the next mini-project.
- Sequence My Account after User Admin, Agent Admin, Audit/Trace, and Governance/Policy so it can aggregate real attention, authority, trace, provider, and decision signals.
- Preserve the lower-left user tile/email launch model; do not add My Account as a normal top-rail workstream.
- Keep deterministic services responsible for `/api/me`, context resolution, authority filtering, profile/settings validation, attention aggregation, shell navigation authorization, trace redaction, and audit/trace.
- Use governed request/response Akka Agent behavior for MyAccountAgent guidance, with provider fail-closed behavior.
- Introduce a personal digest worker only after deterministic personal-attention foundations exist and lifecycle semantics justify it.

## Constraints

- Target is `templates/ai-first-saas-starter/` as the executable baseline.
- Workstream + structured surface UX remains the only product architecture.
- Missing provider/model config must fail closed with actionable surfaces/traces.
- No deterministic/model-less successful normal responses for model-backed MyAccountAgent guidance or workers.
- Visual quality and runtime validation are first-class acceptance criteria.
- One task per fresh harness context.

## Risks

- My Account can accidentally leak hidden workstreams through attention aggregation. Backend filtering must be authoritative.
- Context and authority copy can be mistaken for permission grants. UI and agent guidance explain authority but never grant it.
- Personal settings can expand into identity-provider administration. Keep the SMB slice to self-service profile/settings preferences.
- MyAccountAgent can accidentally imply it changed context/profile/authority. It may explain or guide; deterministic capabilities commit changes.

## Unresolved questions

No blocking product question is required to start. The implementation-map task must determine the current starter's My Account and `/api/me` source shape and whether existing attention/trace-link primitives are sufficient or need deterministic foundations first.
