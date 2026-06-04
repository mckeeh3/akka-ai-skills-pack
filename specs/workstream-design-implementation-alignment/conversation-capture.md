# Conversation Capture: Workstream Design/Implementation Alignment

## User goals

The user asked:

1. how strong the understanding of workstreams is;
2. whether workstreams need improvement at design and implementation levels;
3. to audit the current app-description/specs/code;
4. to proceed with targeted design/implementation alignment.

## Audit findings accepted as source context

The audit characterized workstreams as the root authenticated application abstraction for role-authorized functional agents, continuous workstream timelines, structured surfaces, governed backend capabilities, attention, internal worker handoffs, audit/work traces, and outcome accountability.

Key findings:

- The core concept is strong and does not need a wholesale redesign.
- The app-description has five core functional agents, surfaces, workstream expertise bundles, capability maps, UI docs, and traceability maps.
- The implementation has a real workstream shell, functional-agent rail, structured surface renderers, backend workstream endpoints, durable workstream logs/events, attention, governed runtime Agent invocation, provider fail-closed behavior, and tests.
- The main weakness is design-to-implementation alignment rather than lack of implementation.

## Accepted constraints

- Preserve the repository's runtime completion doctrine.
- Model-backed workstream turns must use the governed Akka `Agent` runtime path, not deterministic/model-less normal success.
- Backend authorization, tenant/customer isolation, audit/work traces, and frontend secret boundaries remain mandatory.
- Root app work belongs under root app paths; skills-pack work is out of scope unless a task explicitly targets installable pack guidance.
- Queue tasks should be fresh-session sized and committed one at a time.

## Targeted gaps to address

1. Canonical id consistency: app-description ids such as `my-account-agent` and `my-account-dashboard` need explicit mapping to implementation ids such as `agent-my-account` and `surface-my-account-dashboard`.
2. Exact governed-tool mappings: app-description maps are currently too coarse for many implemented surface actions.
3. Default dashboard loading: backend bootstrap currently returns empty initial items/surfaces, while doctrine expects workstream selection to produce a default dashboard/attention surface.
4. Prompt-entered surface requests: current frontend recognition is mainly dashboard aliases; broader workstream-local aliases should resolve through a backend-authoritative shell request path.
5. Realtime semantics: current `/api/workstream/events` appears finite/replay-oriented; decide and align docs/tests/code for either true continuous SSE or explicit v1 refresh semantics.
6. Readiness docs: review summaries are stale relative to current implementation and include duplicate Governance/Policy wording.
7. Legacy page artifacts: page-style `frontend/src/screens/**` files need review as reference/compatibility/deep-link artifacts, not primary architecture.

## Rejected alternatives / non-goals

- Do not replace workstream architecture with page-first navigation.
- Do not create a generic chatbot flow.
- Do not treat frontend-only badges, fixture data, or hidden UI controls as authority.
- Do not broaden this initiative into full-core SaaS completion or domain-specific features.

## Unresolved questions

No blocking product questions are known. The realtime task must make and record a bounded implementation decision based on current Akka/runtime feasibility.
