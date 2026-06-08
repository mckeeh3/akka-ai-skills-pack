# Conversation Capture

## Source discussion

After the User Admin, Agent Admin, Audit/Trace, Governance/Policy, and My Account full-core SMB queues were completed, the assistant recommended a cross-workstream polish and release-readiness mini-project. The user agreed and asked to proceed.

## Accepted decisions

- Create `specs/full-core-smb-polish-release-readiness/`.
- Treat this as integrated release readiness, not a new feature workstream.
- Validate the full starter as a coherent SMB baseline.
- Preserve runtime completion doctrine, provider fail-closed behavior, no-secret boundaries, and visual quality requirements.
- Capture intentional deferrals rather than silently expanding release scope.

## Constraints

- Target is `templates/ai-first-saas-starter/` as the executable baseline.
- Workstream + structured surface UX remains the only product architecture.
- Missing provider/model config must fail closed with actionable surfaces/traces.
- No deterministic/model-less successful normal responses for model-backed guidance or workers.
- One task per fresh harness context.

## Risks

- Integrated validation may uncover broad issues. Split fixes into bounded tasks rather than making one giant remediation task.
- Release polish can drift into new features. Keep scope to coherence, validation, docs, and release blockers.
- Docs may overclaim worker or provider behavior. Handoff must distinguish completed runtime paths from intentional deferrals.
