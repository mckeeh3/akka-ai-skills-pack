# Notification Email Channel Contract

## Status and scope

Implementation contract for `TASK-NEDC-01-001`. This slice adds a governed **email notification delivery channel** on top of the completed in-app notification foundation.

The email channel is a delivery layer over authorized notification/source state. It does not replace `NotificationItem`, source attention, workstream events, personal digest task state, policy decisions, audit traces, or My Account in-app notification lifecycle state.

## Core rule

```text
authorized notification/source state
→ email channel eligibility check with AuthContext, recipient, preference, category, and redaction proof
→ durable EmailNotificationDelivery / EmailOutboxMessage intent
→ local/dev/test captured outbox OR production Resend delivery
→ audit/work trace and redacted delivery status
```

Production email delivery uses **Resend** (`resend.com`) through the reusable starter `ResendEmailService` boundary. Local/dev/test delivery uses explicit **captured outbox** behavior. Missing production Resend configuration must **fail closed** with actionable errors; it must not record fake production email success.

## Non-goals and future boundary

Out of scope:

- SMS, mobile push, Slack/Teams, webhooks, marketing email, broad analytics, and provider selection;
- email delivery for hidden, unauthorized, cross-tenant, stale, or redacted source state;
- frontend-composed email content, frontend delivery decisions, or browser-visible provider details;
- suppressing required source attention, policy decisions, audit evidence, or backend denials through email preferences;
- direct service/provider calls that bypass the governed email channel, outbox, authorization, idempotency, and audit path.

Future SMS/push/webhook channels must be separate governed delivery-channel contracts with their own preferences, provider configuration, fail-closed behavior, redaction, idempotency, audit, and tests.

## Channel and category model

Email channel id: `email`.

Email is not automatically enabled by the in-app notification foundation. A notification category may be visible in-app while still not eligible for email.

Starter email category allowlist:

| Category | Email eligibility | Notes |
|---|---|---|
| `digest_ready` | allowed | Personal attention digest or outcome summary is ready for review. |
| `attention_required` | allowed only when high severity or explicitly email-worthy | Must not leak hidden source titles/counts. |
| `digest_blocked` / provider blocked | allowed | Use safe operational wording for provider/runtime blockers. |
| `provider_readiness` | allowed for high-severity attention only | No provider secrets or raw config values. |
| `policy_or_governance` | allowed when recipient has governance capability | Approval/decision summary only. |
| `audit_or_security` | allowed for high-severity/security-review attention | Redacted evidence refs only. |
| `workstream_update` | disabled by default | May be included only in digest-style summaries. |
| `invitation_delivery` | not part of notification email channel | Invitations continue to use the existing invitation Resend/outbox path. |

Implementation may start with a smaller allowlist, but it must deny non-allowlisted categories by default and audit the denial/no-op.

## Email delivery intent schema

Suggested backend DTO/domain names:

- `EmailNotificationDelivery`
- `EmailNotificationPreference`
- `EmailNotificationProjectionInput`
- `EmailNotificationContent`
- `EmailNotificationRecipient`
- `EmailNotificationDeliveryStatus`
- `EmailNotificationOutboxMessage`

Required `EmailNotificationDelivery` fields:

| Field | Meaning |
|---|---|
| `deliveryId` | Stable delivery id for this channel attempt/result. |
| `tenantId` / `customerId` | Scope copied from trusted notification/source state. |
| `accountId` | Recipient account. |
| `normalizedRecipientEmail` | Backend-resolved account email; never caller supplied alone. |
| `selectedContextId` | AuthContext basis used for eligibility. |
| `channel` | `email`. |
| `category` | Allowlisted notification category. |
| `sourceNotificationId` | Optional source `NotificationItem.notificationId`. |
| `sourceRefs` / `traceRefs` | Browser/email-safe references after redaction. |
| `requiredCapabilityId` | Capability needed to read/open the source. |
| `owningWorkstreamId` | Source workstream when visible to the recipient. |
| `subject` | Redacted, browser/email-safe short label. |
| `previewText` | Redacted summary safe for inbox previews. |
| `bodyText` / `bodyHtml` | Backend-rendered safe content, never raw source payload. |
| `surfaceRef` | Optional authorized open target; reauthorized on click/open. |
| `redactionLevel` | `full`, `summary_only`, or `not_found_or_redacted`. |
| `dedupeKey` | Stable semantic idempotency key. |
| `deliveryAttemptId` | Attempt id; stable for command replay. |
| `outboxId` | Durable outbox message id. |
| `provider` | `resend` for production, `captured_outbox` for local/dev/test. |
| `providerMessageId` | Resend id or captured outbox id; safe to expose only in admin/test views. |
| `status` | See delivery status table. |
| `safeErrorSummary` | Redacted failure reason. |
| `correlationId` | Request/event/task trace correlation. |
| `createdAt` / `updatedAt` | Audit timestamps. |

Content must exclude raw JWTs, session cookies, WorkOS ids/details unless policy-safe, Resend secrets, model/provider secrets, invitation tokens/token hashes, hidden prompt text, raw tool payloads, raw provider payloads, cross-tenant labels, hidden workstream names, and full sensitive evidence.

## Preferences

`EmailNotificationPreference` is backend-owned and scoped to signed-in account plus tenant/customer/AuthContext where applicable.

Required fields:

| Field | Meaning |
|---|---|
| `preferenceId` | Stable account + scope + channel + category id. |
| `tenantId` / `customerId` | Preference scope. |
| `accountId` | Owner. |
| `channel` | `email`. |
| `category` | Allowlisted category or `all_email`. |
| `enabled` | User/category opt-in state. Security-critical notices may be non-disableable only if documented. |
| `minimumPriority` | Lowest priority eligible for email. |
| `quietHours` | Optional safe deferral window with timezone. |
| `muteUntil` | Optional temporary mute. |
| `digestMode` | `immediate`, `batched`, or `disabled` where supported. |
| `updatedAt` / `updatedBy` | Audit fields. |
| `correlationId` | Update correlation. |

Preference guardrails:

- email must be opt-in or explicitly policy-required per category;
- category APIs must not enumerate hidden workstreams, hidden categories, or source existence;
- preference updates require a backend capability such as `notification.email.update_preferences`;
- quiet-hours deferral must not create fake success; deferred messages remain pending/deferred with audit evidence;
- disabling email does not disable in-app notifications, source attention, decisions, audit evidence, or authorization denials.

## Eligibility, authorization, and redaction

Before enqueueing email, the backend must prove:

1. authenticated recipient account exists and has a deliverable email address;
2. selected AuthContext is valid for the tenant/customer scope;
3. recipient has active membership and required source capability;
4. source notification/input is visible to this recipient and not hidden/redacted away;
5. category is in the email category allowlist;
6. email preference permits this category, priority, and timing;
7. quiet-hours or deferral policy has been applied;
8. content is backend-rendered and redacted for email;
9. dedupe/idempotency key is stable;
10. audit/work trace will be emitted for enqueue, send, capture, deny, defer, duplicate, and fail outcomes.

Unauthorized, hidden, cross-tenant, stale, preference-disabled, or non-allowlisted inputs must not reveal titles, counts, source ids, workstream names, or recipient candidates. They should no-op or return `not_found_or_redacted` according to caller context and emit a redacted audit/work trace.

## Idempotency

Delivery dedupe key pattern:

```text
notification:email:<tenantId>:<customerId-or-none>:<accountId>:<category>:<source-family>:<source-id>:<semantic-kind>
```

Rules:

- duplicate projection/command with the same dedupe key must not create duplicate active deliveries;
- replay of the same `deliveryAttemptId` returns the existing delivery result or no-op trace;
- a content update may revise a pending/deferred outbox item only when source evidence is newer and still authorized;
- terminal sent/captured/failed attempts are not silently overwritten;
- stale outbox messages are audited as terminal ignore/no-op instead of retried forever;
- provider retry uses a bounded retry budget and preserves safe failure visibility.

## Outbox, Resend, and fail-closed provider boundary

Use the existing starter email foundation shape:

- production adapter: `ResendEmailService` / Resend HTTP boundary;
- local/dev/test adapter: captured outbox;
- durable intent: `EmailOutboxMessage` or a notification-specific wrapper that maps to the shared outbox;
- delivery status: redacted status plus provider/captured id where safe.

Production requirements:

- provider is fixed to Resend; do not introduce provider selection;
- `RESEND_API_KEY` and `RESEND_FROM_EMAIL` or feature-specific approved from-address configuration must be backend-only;
- missing or blank required Resend configuration fails closed before external delivery;
- production mode must not fall back to captured outbox or fake production email success;
- Resend HTTP/API errors record redacted `safeErrorSummary`, audit evidence, and retry/failure state;
- provider secrets and raw provider payloads never reach browser DTOs, frontend bundles, email content, or unredacted traces.

Local/dev/test requirements:

- captured outbox records the intended email without external delivery;
- captured body/link is available only through test/developer-safe paths, never production admin UI by default;
- automated tests must not send real email;
- captured outbox success is labeled `captured`, not `sent`.

## Delivery status lifecycle

| Status | Meaning | Idempotency |
|---|---|---|
| `not_eligible` | Auth/category/preference/redaction denied email. | Re-evaluate only on new source/preference evidence. |
| `deferred` | Quiet-hours or digest batching delayed delivery. | Re-deferral updates schedule only when authorized. |
| `queued` | Durable outbox intent created. | Duplicate enqueue returns existing outbox id. |
| `captured` | Local/dev/test captured outbox recorded. | Duplicate attempt returns captured id. |
| `sent` | Resend accepted production message. | Replay returns provider message id; no duplicate send. |
| `failed` | Provider/config/content failure after bounded policy. | Replay returns failure unless new attempt is authorized. |
| `cancelled` | Source expired or visibility/preference changed before send. | Re-cancel is no-op with trace. |

## Capability and governed-tool ids

Capability grouping: `notification.email`.

| Governed-tool id | Class | Exposure | Purpose |
|---|---|---|---|
| `notification.email.evaluate_delivery` | command/internal | consumer-tool, timer-tool, internal-tool | Check source, authorization, preferences, allowlist, redaction, and enqueue/defer/no-op. |
| `notification.email.enqueue` | command/internal | internal-tool | Create durable email outbox intent for an eligible redacted message. |
| `notification.email.deliver_outbox` | command/internal | consumer-tool | Deliver queued email through Resend or captured outbox according to mode. |
| `notification.email.get_delivery_status` | read/evidence | API, internal-tool | Return authorized redacted delivery status. |
| `notification.email.list_my_preferences` | read/evidence | browser-tool, API | Return current account email preference summary without hidden enumeration. |
| `notification.email.update_preferences` | command | browser-tool, API | Update authorized email preferences. |
| `notification.email.preview` | read/evidence | internal-tool, optional governed agent-tool | Preview redacted email content only for authorized admin/test contexts. |

Related in-app notification capabilities (`notification.list_my_account_center`, lifecycle actions) and source capabilities remain separate authorities.

## My Account email preference surface

The My Account notification center may add an email preferences section after backend support exists.

Surface contract requirements:

- labels email as a separate delivery channel from in-app notifications;
- renders backend-derived `EmailNotificationPreference` summaries only;
- exposes category toggles only for visible allowlisted categories;
- shows quiet-hours/digest settings only when backend supports them;
- never displays Resend configuration, provider secrets, raw full email bodies, hidden categories, or hidden source refs;
- update actions map to `notification.email.update_preferences`;
- preference updates return correlation/audit refs and current backend state.

## Audit and observability

Emit audit/work trace evidence for:

- eligibility allowed/denied/no-op;
- preference read/update;
- enqueue, duplicate enqueue, deferred delivery, cancellation;
- captured outbox write;
- Resend send success with safe provider id;
- Resend/config/provider failure with redacted error;
- stale outbox ignore, retry exhaustion, and terminal failure;
- unauthorized direct status/preview reads.

Trace payloads must include tenant/customer/account scope, AuthContext basis, capability id, governed-tool id, dedupe key, correlation id, status, safe error summary, and redacted source refs. They must not include Resend API keys, raw provider payloads, raw JWTs, invitation tokens/token hashes, hidden source details, or full sensitive email bodies.

## Implementation handoff

Backend tasks should implement or adapt:

- notification-specific email delivery DTOs and preferences under the starter backend domain/application security or notification package;
- delivery evaluation service that consumes `NotificationItem`/`NotificationProjectionInput` and source refs;
- shared outbox mapping to existing `EmailOutboxMessage` where practical;
- Resend production and captured outbox delivery through the existing `ResendEmailService` boundary;
- My Account API fields/actions for email preference summaries;
- tests for preferences, category allowlist, redaction, idempotency, audit, fail-closed missing Resend configuration, captured outbox behavior, and no fake production email success.

Frontend tasks should implement:

- My Account email preference rendering from backend-shaped data;
- action descriptors for preference updates;
- empty/redacted/error states;
- tests proving no active unsupported SMS/push/webhook controls and no provider/secret leakage.
