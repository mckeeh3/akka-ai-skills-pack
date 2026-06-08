# Conversation Capture: Workstream Attention Event Producers v2

## User goals

The user asked what comes next after all `workstream-attention-backbone-v1` pending tasks were completed. The answer identified the next logical milestone: use the shared attention backbone as an operational event/producer substrate.

The user then said “go ahead,” accepting creation of a new mini-project queue.

## Accepted context

- v1 is complete at its stated scope.
- The repository now has a shared backend-owned attention backbone in the starter/reference assets.
- v1 intentionally left future event consumers, timers, realtime streams, digests, and AutonomousAgent task notifications out of scope.

## Decisions made

- Create a v2 mini-project focused on **Workstream Attention Event Producers**.
- Preserve one shared attention backbone; v2 adds producers, scheduled checks, task-state integration, and update delivery.
- Backend attention state remains authoritative; frontend notification/refresh mechanisms are presentation/update channels only.
- Do not fake AutonomousAgent/model-backed work. Where starter workers are blocked/provider-fail-closed, attention should represent that real state honestly.

## Candidate v2 slices

- Domain/service producers for invitation delivery failures/resolutions, governance proposal/approval state, audit/provider failure evidence, and Agent Admin provider readiness.
- Timed checks for expiry/staleness/overdue starter cases.
- Worker/task attention for blocked, failed, stuck, or completed-with-review-needed internal tasks.
- Rail/My Account/workstream attention update delivery through refresh, polling, SSE, or existing shell mechanisms.
- Documentation updates that distinguish v1 backbone from v2 producers and future full event backbone work.

## Rejected alternatives / non-goals

- Do not create separate per-workstream attention queues.
- Do not make frontend-only badges authoritative.
- Do not turn this into a whole event-sourcing platform rewrite.
- Do not claim model-backed AutonomousAgent behavior is complete through deterministic/demo substitutes.

## Risks

- Producer tasks can accidentally expand into every domain workflow. Keep v2 to bounded starter cases.
- Realtime can become too broad. Prefer the smallest honest update path that preserves backend authority.
- Docs must be updated carefully to avoid contradicting v1 completion status.

## Unresolved questions

No blocking questions remain for a bounded starter/reference v2. Implementation tasks may select the smallest honest local mechanism for producer/update delivery while preserving runtime completion doctrine.
