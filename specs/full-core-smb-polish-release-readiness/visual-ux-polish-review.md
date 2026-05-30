# Visual UX and Cross-Workstream Polish Review

## Result

- Date: 2026-05-30
- Scope: `templates/ai-first-saas-starter/frontend/src/` shell, workstream rail, structured surfaces, fixtures, contract tests, and responsive/accessibility CSS.
- Release blocker status: **no visual UX release blockers found**.
- Recommendation: proceed to provider/trace/secret boundary audit.

## Commands run

```bash
find templates/ai-first-saas-starter/frontend/src -maxdepth 2 -type f -print | sort
rg -n "My Account|User Admin|Agent Admin|Audit/Trace|Governance/Policy|system_message|blocked_provider_or_runtime|provider-blocked|provider|trace|Trace|surface|workstream|aria-|role=|focus|responsive|fixture|attention|open_authorized_workstream|hidden prompt|secret" templates/ai-first-saas-starter/frontend/src --glob '!**/node_modules/**'
find templates/ai-first-saas-starter/frontend/src -maxdepth 3 -type f \( -name '*.tsx' -o -name '*.ts' -o -name '*.mjs' -o -name '*.css' \) -print | sort | head -200
rg -n "function|export const|describe\(|it\(|test\(" templates/ai-first-saas-starter/frontend/src --glob '!**/node_modules/**' | rg -n "Workstream|Surface|Shell|Rail|My Account|User Admin|Agent Admin|Audit|Governance|system_message|provider|trace|responsive|accessibility|icon" | head -240
rg -n "surface-(my-account|my-profile|my-settings|user-admin|agent-admin|audit-trace|governance-policy)|agent-(my-account|user-admin|agent-admin|audit-trace|governance-policy)|blocked_provider_or_runtime|system_message|traceLinks|Trace links|aria-label|@media \(max-width" templates/ai-first-saas-starter/frontend/src/workstream templates/ai-first-saas-starter/frontend/src/*.test.mjs templates/ai-first-saas-starter/frontend/src/styles --glob '!**/node_modules/**' | head -260
cd templates/ai-first-saas-starter/frontend && npm test -- --run
cd templates/ai-first-saas-starter/frontend && npm run typecheck
git diff --check
```

## Validation results

- `npm test -- --run`: passed, 121/121 tests.
- `npm run typecheck`: passed.
- `git diff --check`: passed.
- Static source review found explicit contract coverage for shell, five core workstreams, `system_message`, `blocked_provider_or_runtime`, trace links, accessible labels, responsive CSS, and no-secret/redaction copy.

## Coverage review

### Shell and workstream identity

- Left rail remains functional-agent/workstream first, not page-first CRUD.
- My Account is excluded from top workstream entries and opened from the lower-left signed-in user tile.
- Core workstreams have descriptor-backed icon metadata and accessible labels: User Admin, Agent Admin, Audit/Trace, and Governance/Policy.
- Rail unseen-response and attention indicators are backend/event-derived UI state, accessible through labels, and cleared on selection.
- Persistent composer remains selected-agent aware and does not become the sole source of truth.

### Structured surfaces and trace affordances

- Canonical renderer covers `markdown_response`, `system_message`, dashboard, list/search, detail/edit, decision, audit timeline, workflow status, governance diff, and outcome surfaces.
- Consequential surfaces include correlation/redaction metadata, capability/action affordances, trace links, stale/error/forbidden states, and action bars.
- Trace links route toward Audit/Trace detail/timeline surfaces near consequential results and actions.

### Workstream-specific review

| Workstream | Review result |
|---|---|
| My Account | Personal attention dashboard, profile/settings, selected context, authorized next actions, redacted trace refs, and lower-left launch are represented. |
| User Admin | Dashboard/list/detail/role/access-review surfaces show invitations, member status, role changes, access review, denials, idempotency/no-op, provider-blocked worker state, and audit traces. |
| Agent Admin | Catalog/detail/prompt/skill/reference/tool/model/seed/test/proposal/provider-blocked/trace surfaces are represented with approval gates, redaction, and no-direct-mutation copy. |
| Audit/Trace | Dashboard/search/detail/timeline/failure-evidence/investigation-guide surfaces preserve trace links, redaction, partial-result/provider evidence, and authorized/deferred actions. |
| Governance/Policy | Dashboard/inventory/proposal/simulation/decision/blocked activation/blocked rollback/impact-analysis/decision-trace surfaces preserve approval authority, advisory simulation boundaries, provider/runtime blockers, and no fake analysis progress. |

### System messages and provider-blocked states

- `system_message` surfaces render safe recovery steps, trace/source references, capability/status metadata, redaction notes, and provider/runtime blocked states.
- Provider-blocked or runtime-deferred worker states are visible as blocked surfaces; they do not render fake successful progress.
- Markdown responses remain sanitized and text-only; structured interactions are not hidden inside markdown once in scope.

### Accessibility and responsive behavior

- Skip link, main landmark, aria labels, tooltip labels, trace-link nav labels, alert/status semantics, focusable surfaces, reduced-motion and narrow-screen CSS are present.
- Contract tests explicitly cover focus, skip link, reduced motion, responsive shell rules, accessible workstream icons, and action/surface labels.

## Findings

### Release blockers

None.

### Non-blocking polish

- The mobile rail CSS includes hidden/off-canvas behavior, but this review did not perform a browser viewport/manual interaction pass. Keep this as a visual QA recommendation rather than a release blocker because source contracts and tests cover narrow-screen rules and no source defect was isolated.

### Intentional deferrals / post-release recommendations

- Richer full-core structured surfaces remain explicit demo/fixture surfaces where the current v0 starter defaults to five core markdown responses; this is acceptable because the source labels fixture/demo boundaries and does not claim those as normal model-backed runtime completion.
- Durable internal workers such as policy-impact analysis and access-review analysis remain blocked/deferred unless real provider/runtime paths exist; the UI represents these as `blocked_provider_or_runtime` rather than successful analysis.

## Release recommendation from this review

Visual UX and cross-workstream polish are acceptable for the current release-readiness task group. No bounded visual source-fix task is needed before the provider/trace/secret audit.
