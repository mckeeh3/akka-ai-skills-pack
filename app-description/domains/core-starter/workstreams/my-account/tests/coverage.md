# Tests: My Account

## Acceptance

- Given an authorized caller with selected `AuthContext`, when they open My Account, then `surface-my-account-dashboard` renders as a personal command center with context/authority, top attention counters as the primary attention UI, secondary/collapsed `needsAttention[]` evidence only where useful, control panels, authorized workstream links, redaction, and trace refs.
- Given the caller opens profile, settings, context authority, notification center, digest/export progress/result/blocked, or open-denied surfaces, then each surface returns its declared contract and frontend-safe payload only for the selected context.
- Given a WorkOS/AuthKit-authenticated account has no active memberships or no eligible selected context, when it opens `/api/me` or My Account, then the backend returns a safe no-access/no-selected-context recovery surface with profile/session guidance only and no hidden tenant/customer/workstream enumeration.
- Given an allowed action, when it is submitted with valid input, then capability `account-context-and-profile` or the linked notification/digest capability returns the expected structured result and emits required traces.
- Given a named theme selection is changed in My Account settings, then local preview occurs immediately but durable persistence is only reported after the governed backend settings action succeeds.

## Security and negative

- Disabled users, inactive memberships, role/capability denials, and cross-tenant/customer requests are denied without protected-data leakage.
- Profile/settings updates reject unsupported fields, role/capability changes, account-status changes, provider-secret edits, arbitrary CSS/theme injection, and tenant-wide setting changes before mutation.
- Context switching lists only authorized contexts and hidden/invalid targets return forbidden or `not_found_or_redacted` without enumeration.
- Notification lifecycle actions mutate durable personal notification state only and never resolve source attention/tasks/events; snooze expiry, source-resolved projection, duplicate source aggregation, and hidden-source redaction preserve tenant/customer isolation and source authority.
- Digest/export flows fail closed when provider/runtime/governed-tool configuration is missing and never return fake/model-less success in normal runtime.
- Agent/tool calls cannot exceed the governed tool boundary or approval policy.
- Browser payloads never expose provider secrets, hidden workstream names/counts, hidden notification categories, or hidden authority state.

## Idempotency and observability

- Repeated side-effecting profile/settings, notification lifecycle, digest/export, and review actions do not duplicate effects.
- No-op, validation-error, forbidden, conflict, stale/reconnect, provider-fail-closed, denial, and success outcomes render typed result/system-message/workflow/outcome surfaces with trace/correlation ids.
- Denials, approval-required outcomes, provider fail-closed states, and trace emissions are verifiable through local Akka/API/UI tests or readiness evidence.

## Frontend/style regression

- My Account dashboard uses the current AI-first style guide component anatomy and does not regress to generic dashboard/card-grid navigation.
- Profile/settings/context surfaces use designed structured-surface controls/panels, not browser-default forms or page-first screens.
- Settings exposes named theme choices only; it does not expose `system`, `light`, or `dark` as the user-facing model.
- The notification center preserves the recently revised triage intent and implementation while aligning with shared tokens and responsive rules.
