# Audit Trace source alignment

Lifecycle: ../lifecycle.md
Last reviewed: 2026-06-29
Alignment state: stale-description-changed

This file records candidate source alignment for the refreshed Audit/Trace investigation current-intent graph. It is not proof that implementation currently matches the updated app-description. The graph now covers tenant admin and SaaS support/support-access investigation; search/detail/timeline/correlation/denial/support-access/export/runtime-validation surfaces; confirmed read-only chat plans; bounded agent tool calls; system projection/consumer/internal/API adapters; and trace-gap/runtime-validation evidence.

## Alignment entries

| Entry id | App-description files | Implementation files | Test / validation files | Last aligned evidence | Notes |
| --- | --- | --- | --- | --- | --- |
| `audit-trace.investigation-graph` | `../workstream.md`, `../access.md`, `../behavior.md`, `../workers/**`, `../agents/functional-agent.md`, `../surfaces/surfaces.md`, `../tools/governed-tools.md`, `../policies/policy-bindings.md`, `../traces/work-traces.md`, `../tests/coverage.md`, `akka-components.md`, `api-contracts.md`, `frontend-routes.md`, `../../../capabilities/audit-and-trace-investigation.md` | Candidate paths: `src/main/java/ai/first/application/foundation/audit/**`, `src/main/java/ai/first/application/foundation/agent/**`, `src/main/java/ai/first/application/foundation/workstream/**`, `src/main/java/ai/first/api/coreapp/**`, `frontend/src/workstream/**`, `frontend/src/api/**` | Candidate paths: `src/test/java/ai/first/application/foundation/audit/**`, `src/test/java/ai/first/application/foundation/agent/**`, `src/test/java/ai/first/application/foundation/workstream/**`, `frontend/src/*audit*contract*.mjs`, `frontend/src/workstream*contract*.mjs`, runtime-validation evidence for Audit/Trace scenarios | 2026-06-29 app-description review only; no implementation alignment evidence recorded. | Focused source-alignment must verify worker/adapter bindings; governed tools; AuthContext/support access; dashboard/search/detail/timeline/denial/support-access/summary/export surfaces; API/frontend route mappings; trace ingestion/projection/correlation/gap behavior; confirmed chat plans; bounded agent tool calls; runtime-validation evidence links; and redaction/denial/export tests. |

## Runtime-validation evidence to link

Future validation runs should link these scenario ids/evidence into Audit/Trace when executed:

- `audit-trace.search-detail.allowed`
- `audit-trace.trace-read.denied`
- `audit-trace.redaction.frontend-secret-boundary`
- `audit-trace.chat-plan.confirmed-read-only`
- `audit-trace.agent-tool.boundary-allowed-denied`
- `audit-trace.support-access.review`
- `audit-trace.export.redacted-approval-denial-idempotency`
- `audit-trace.correlation.cross-workstream`
- `audit-trace.trace-gap.detected`
- `audit-trace.provider-config.fail-closed`
- `audit-trace.source-alignment.evidence-link`

## Unmapped current-intent files

- None known for the refreshed Audit/Trace description slice.

## Unmapped implementation files

- Unknown until a focused source-alignment review is run.

## Alignment notes

- Current state is `stale-description-changed` because this docs-only refresh broadened and clarified the Audit/Trace graph after prior implementation evidence.
- Do not use this source-alignment file as runtime-readiness evidence. Runtime readiness still requires automated checks and real local API/UI validation for the selected scope.
