# Tests: My Account

## Runtime-validation references

These are description-level runtime-validation expectations for the refreshed My Account graph; they do not add executable tests by themselves.

| Runtime-validation id | Path under validation | Graph coverage proof |
|---|---|---|
| `rv-my-account-api-me-auth-context` | WorkOS/AuthKit session or equivalent local auth → `/api/me` → selected `AuthContext` → My Account dashboard/context bootstrap | Proves signed-in member, Tenant-backed Organization/customer membership, browser-safe capability visibility, disabled/no-membership denial, and frontend secret boundary. |
| `rv-my-account-profile-context-surfaces` | signed-in member → My Account route/deep link → protected workstream API → dashboard/profile/settings/context surfaces | Proves surface graph nodes, `surface_action`/`api_call` adapters, account/profile/context read-update tools, no-op/profile validation outcomes, result/system-message surfaces, and selected context refresh behavior. |
| `rv-my-account-agent-assistance-boundary` | signed-in member prompt → `my-account-agent` → deterministic route or bounded plan proposal → backend confirmation path when applicable | Proves the functional agent can explain/propose only; `agent_tool_call` remains read/advisory where described; side effects require surface submit or exact `human_chat_tool_plan` confirmation. |
| `rv-my-account-denial-and-traces` | disabled/inactive/cross-tenant/hidden-context/open-denied requests → protected API/backend authorization → trace refs/result surface | Proves denial behavior, no hidden-target enumeration, tenant/customer isolation, audit/work trace emission, and trace visibility through authorized refs. |

A source-alignment pass may mark these references aligned only after local Akka/API/UI evidence exercises the real protected path rather than fixtures, deterministic demo data, or model-less normal runtime substitutes.

## Acceptance

- Given an authorized caller with selected `AuthContext`, when they open My Account, then `surface-my-account-dashboard` renders as a personal command center with context/authority, top attention counters as the primary attention UI, secondary/collapsed `needsAttention[]` evidence only where useful, control panels, authorized workstream links, redaction, and trace refs.
- Given the caller opens profile, settings, context authority, notification center, digest/export progress/result/blocked, or open-denied surfaces, then each surface returns its declared contract and frontend-safe payload only for the selected context.
- Given a WorkOS/AuthKit-authenticated account has no active memberships or no eligible selected context, when it opens `/api/me` or My Account, then the backend returns a safe no-access/no-selected-context recovery surface with profile/session guidance only and no hidden tenant/customer/workstream enumeration.
- Given an allowed action, when it is submitted with valid input, then the responsible worker (`signed-in-member-human`, `my-account-functional-agent-worker`, or `my-account-system-worker`), actor adapter, governed tool, and capability `account-context-and-profile` or linked notification/digest capability are traceable, the expected structured result is returned, and required traces are emitted.
- Given a named theme selection is changed in My Account settings, then local preview occurs immediately but durable persistence is only reported after the governed backend settings action succeeds.

## Security and negative

- Disabled users, inactive memberships, role/capability denials, and cross-tenant/customer requests are denied without protected-data leakage.
- Profile/settings updates reject unsupported fields, role/capability changes, account-status changes, provider-secret edits, arbitrary CSS/theme injection, and tenant-wide setting changes before mutation.
- Context switching lists only authorized contexts and hidden/invalid targets return forbidden or `not_found_or_redacted` without enumeration.
- Notification lifecycle actions mutate durable personal notification state only and never resolve source attention/tasks/events; snooze expiry, source-resolved projection, duplicate source aggregation, and hidden-source redaction preserve tenant/customer isolation and source authority.
- Digest/export flows fail closed when provider/runtime/governed-tool configuration is missing and never return fake/model-less success in normal runtime.
- Agent/tool calls cannot exceed the governed tool boundary or approval policy, and human `surface_action` availability never implies `agent_tool_call` authority.
- Browser payloads never expose provider secrets, hidden workstream names/counts, hidden notification categories, or hidden authority state.

## Idempotency and observability

- Repeated side-effecting profile/settings, notification lifecycle, digest/export, and review actions do not duplicate effects.
- No-op, validation-error, forbidden, conflict, stale/reconnect, provider-fail-closed, denial, and success outcomes render typed result/system-message/workflow/outcome surfaces with trace/correlation ids.
- Denials, approval-required outcomes, provider fail-closed states, worker/adapter trace emissions, and source-alignment mappings are verifiable through local Akka/API/UI tests or readiness evidence.

## Frontend/style regression

- My Account dashboard uses the current AI-first style guide component anatomy and does not regress to generic dashboard/card-grid navigation.
- Profile/settings/context surfaces use designed structured-surface controls/panels, not browser-default forms or page-first screens.
- Settings exposes named theme choices only; it does not expose `system`, `light`, or `dark` as the user-facing model.
- The notification center preserves the recently revised triage intent and implementation while aligning with shared tokens and responsive rules.


## `human_chat_tool_plan` coverage

- Given deterministic surface routing can safely open or prefill a surface, when a high-confidence no-mutation prompt is submitted, then the router returns that surface first, records the human/system adapter path where applicable, and `human_chat_tool_plan` is not used.
- Given the representative prompt **change my theme to Obsidian Dark** and an authorized selected `AuthContext`, when the chat request is classified as `human_chat_tool_plan`, then the response is a no-mutation plan proposal surface that lists actions `action-update-my-settings`, governed tools `my_account.update_profile_settings`, capabilities `my_account.update_profile_settings`, validated input schema `schema.my-account.settings.update.v1` with `preferredThemeId=obsidian-dark` selected from backend-valid theme options, side effects, approval gates, idempotency, result surfaces `surface-my-settings`, and trace refs.
- Given a proposed plan has not been explicitly confirmed, when the request completes, then no surface action, governed tool, external provider side effect, state mutation, invitation/email/outbox send, policy/agent lifecycle change, trace note append, or settings update has occurred.
- Given the human confirms the exact `planId` and `planSnapshotId`, when backend authorization, lifecycle, tool-boundary, validation, approval, tenant/customer ownership, and idempotency checks pass for every step, then each step executes as an independent transaction boundary and returns the declared result or recovery surface.
- Given a modified, stale, expired, cross-context, cross-tenant/customer, missing-confirmation, out-of-catalog, unsupported-field, hidden-target, provider/runtime/tool-boundary blocked, or unauthorized plan is confirmed, then execution is denied with `chat_tool_plan.system_message.v1`, `noDirectMutation=true`, safe recovery, no hidden-target enumeration, and trace refs.
- Given the same proposal or confirmed step is replayed with the same idempotency key, then the backend returns the existing proposal/result and does not duplicate side effects, traces, notifications, provider calls, or attention items.
- Given a later dependent step fails after an earlier step commits, then the plan result reports completed, failed, skipped, and recovery steps without rolling back committed work unless a cataloged compensating action exists.
- Given provider/model/runtime configuration is missing for model-backed proposal generation, then the workstream returns a typed plan-unavailable/system-message state and trace evidence instead of fake/model-less planning success.
- Given any agent, prompt, skill, reference, frontend state, route, or visible rail item suggests broader authority than the catalog grants, then the backend rejects the plan or step and records a denial trace.
