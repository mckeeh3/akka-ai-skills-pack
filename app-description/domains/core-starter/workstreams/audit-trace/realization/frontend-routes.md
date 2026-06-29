# Realization: Frontend routes and surfaces for Audit/Trace

Capability: `audit-and-trace-investigation`.

This file records the Audit/Trace frontend realization contract implied by current intent. It is not runtime proof.

## Route/deep-link map

| Route concern | Surface | Contract obligations |
|---|---|---|
| Audit/Trace workstream entry | `surface-audit-trace-dashboard` | Role-authorized left-rail/workstream entry showing investigation, denial, trace-gap, support-access review, runtime-validation evidence, and export attention items. |
| Trace search | `surface-audit-trace-search` | Filters for time, tenant/customer within scope, actor, worker/workstream, category, action, tool/capability/policy/agent refs where visible, adapter/source, status, and correlation/work-trace id. |
| Trace/work-trace detail | `surface-audit-trace-detail` | Safe default detail with progressive disclosure, redaction state, sensitive-detail warning/grant handling, related links, and result surface refs. |
| Correlation timeline | `surface-audit-trace-timeline` | Causation timeline across surface actions, chat plans, agent tool calls, workflows, consumers, API/internal events, policy/approval/support-access/runtime-validation events, and trace gaps. |
| Denial investigation | `surface-audit-trace-denial-investigation` | Denial reason, policy reference, actor adapter, governed tool/capability, AuthContext/support scope, redaction, safe remediation. |
| Support-access review | `surface-audit-trace-support-access-review` | Support-access grant/use/expiry/denial review with tenant/SaaS owner visibility and no self-approval affordance. |
| Investigation summary | `surface-audit-trace-investigation-summary` | Evidence refs, redaction disclaimer, correlation chain, denials, trace gaps, support/export refs, unknowns, and partial-failure states. |
| Export request/result | `surface-audit-trace-export-request` | Redacted export request, approval-required, queued/preparing, ready, denied, expired, failed, and idempotent replay states. |
| Safe feedback | `surface-audit-trace-system-message` | Validation, forbidden, not-found/redacted, approval-required, trace-gap, stale/reconnect, no-op, and partial-failure messages. |

## Composer and confirmation UI

- High-confidence Audit/Trace prompts may open search/detail/timeline/denial/support-access surfaces with visible prefilled filters only; deterministic prefill never submits tools.
- Confirmed read-only chat plans render proposed governed tools, scope, redaction level, expected result surfaces, idempotency/correlation context, and cancel/confirm controls.
- Export/support-access-sensitive flows render approval-required/denied states instead of letting the agent or requester self-approve.

## Frontend API and realtime obligations

- All surface data comes from protected backend APIs; browser route/deep-link availability is advisory only.
- Realtime/projection updates may refresh dashboard attention, export status, trace-gap status, runtime-validation evidence status, and stale/reconnect banners, but every update remains tenant/support scoped.
- Browser payloads never include provider/server secrets, bearer/session tokens, raw prompt/model payloads, hidden cross-tenant identifiers, or frontend-secret material.
- Client-side search indexes must not include full payloads or hidden records.

## Accessibility and frontend-security obligations

- Dashboard cards, filters, rows, pagination, timeline events, detail links, denial/support-access review controls, summary/export actions, and chat confirmation controls are keyboard-operable with visible focus.
- Status, denial, approval, redaction, and trace-gap states are not color-only.
- Error, empty, forbidden, approval-required, validation-error, stale/reconnect, partial-failure, and `not_found_or_redacted` states provide safe recovery text without hidden data leakage.
- Sensitive-detail warnings appear anywhere sensitive payload sections are rendered.

## Explicit frontend exclusions

Do not render autonomous remediation, support-access self-approval, raw sensitive export by default, trace edit/delete, full-payload keyword search, or agent-tool evidence retrieval controls unless backend policy/tool-boundary grants the matching current-intent authority.
