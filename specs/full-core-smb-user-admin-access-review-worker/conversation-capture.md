# Conversation Capture

## Source discussion

After all pending `full-core-smb-*` tasks were completed, the assistant inspected current queues and predecessor contracts. The next recommended step was the fourth User Admin vertical slice from `specs/full-core-smb-user-admin/user-admin-vertical-contracts.md`: the access-review worker candidate.

The user agreed and asked to proceed.

## Accepted decisions

- Create a new mini-project at `specs/full-core-smb-user-admin-access-review-worker/`.
- Focus on a durable access-review worker for User Admin, not another request/response guidance turn.
- Use deterministic User Admin foundations for authorization, evidence, lifecycle, idempotency, audit, trace, and all mutations.
- Use governed internal worker / `AutonomousAgent` behavior only where the lifecycle justifies it.
- Preserve provider fail-closed behavior; missing provider/config cannot produce a successful model-less normal result.
- Actual access changes recommended by the worker must route through deterministic User Admin actions and human authorization.

## Constraints

- Target is `templates/ai-first-saas-starter/` as the executable baseline.
- SMB scope only; avoid enterprise access certification/campaign scope creep.
- Workstream + structured surface UX is the only product architecture.
- Visual quality, traceability, provider failure clarity, and no-secret leakage remain acceptance criteria.
- One task per fresh harness context.

## Risks

- Worker output might accidentally become an access-mutation path. Keep mutation execution out of the worker and route recommendations to deterministic actions.
- Access review can expand into enterprise IAM/certification workflows. Keep the slice SMB-simple: start a scoped review, track progress, summarize risk, and let a human accept/reject results.
- Existing starter runtime may not yet have enough internal-worker foundation. The first task must inspect source boundaries before writing implementation tasks.
- Provider-blocked behavior must be explicit and trace-linked, not hidden behind demo output.

## Unresolved questions

No blocking product question is required to begin. The implementation-map task must determine whether the existing starter has enough `AutonomousAgent`/internal-worker scaffolding or whether the first source-edit task must add a minimal governed worker foundation for this slice.
