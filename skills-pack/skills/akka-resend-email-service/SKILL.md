---
name: akka-resend-email-service
description: Implement the single supported application email service for generated Akka SaaS apps using Resend, including reusable email delivery/outbox, invitation emails, future feature emails, and governed @FunctionTool exposure for agents.
---

# Akka Resend Email Service

Use this skill whenever a generated SaaS app sends email: new-user invitation/account emails, onboarding reminders, decision/approval notifications, digests, operational alerts, or later app-specific email features.

Resend (resend.com) is the only supported production email service in this skills pack. Do not ask provider-selection questions and do not design pluggable production email providers unless the skills pack itself is later extended with another provider-specific skill, examples, and tests.

Pair with:
- `core-saas-foundation` and `akka-saas-invitation-onboarding` for mandatory invite onboarding
- `akka-consumers` for outbox delivery side effects
- `akka-agent-tools` for local/external `@FunctionTool` email tools
- `akka-agent-tool-boundaries` for `ToolPermissionBoundary` grants, side-effect policy, approval gates, and traces
- HTTP/workflow/timer skills when email is sent from APIs, workflows, reminders, or scheduled digests

## Required production and non-production behavior

- Production delivery uses Resend only.
- Required backend-only production settings include at least `RESEND_API_KEY`, `RESEND_FROM_EMAIL` or feature-specific senders such as `INVITE_EMAIL_FROM`, and allowed reply-to/domain settings when used.
- Missing Resend production settings blocks readiness/startup for production email features.
- Local/dev/test must use an explicit captured outbox adapter by default. Automated tests must not send real email.
- Resend keys, webhook secrets, raw invitation tokens, and template secrets are backend-only and must never appear in frontend env files, browser APIs, logs, audit summaries, prompts, or agent output.

## Reusable email capability

Model email sending as a governed capability before choosing the caller surface.

Recommended capability ids:
- `email.send` — generic guarded send for approved feature emails.
- `email.preview` — render/validate a redacted preview without external delivery.
- `email.delivery_status.read` — scoped delivery status lookup.
- `user_onboarding.invitation_email.send` — invitation/account onboarding email delivery.
- Feature-specific capabilities such as `decision.notification_email.send` or `digest.email.send` when policy, templates, recipients, or autonomy differ.

Capability contract requirements:
- actor/caller: human admin, workflow, timer, consumer, or managed agent;
- AuthContext or explicit system/service authority basis;
- tenant/customer scope and recipient authorization;
- template id/version and allowed variable schema;
- idempotency key/correlation id;
- side-effect classification `external_message`;
- approval/autonomy policy;
- AdminAuditEvent or work/tool trace;
- redacted result with provider message id/status only.

## Akka realization pattern

| Part | Responsibility |
|---|---|
| `EmailOutboxMessage` entity/record or projection | Durable intent to send email, tenant/customer scope, template id/version, recipients, idempotency key, status, attempts, last error, Resend message id, and audit metadata. |
| Email delivery `Consumer` | Reads queued email work, calls Resend in production or captured outbox in local/dev/test, updates delivery status, records attempts/failures, and emits audit/trace facts. |
| Feature workflow/timer/API | Creates email outbox work after validating capability authority and idempotency. Invitation flows use `InvitationWorkflow`. |
| Email status view | Scoped admin/operator view for delivery status, failures, attempts, template, feature category, recipient summary, and correlation id. |
| `ResendEmailTools` function tool class | Optional governed agent tool surface for previewing or sending approved emails through the same capability boundary. |

Prefer outbox + consumer delivery over direct provider calls from commands/tools so retries, idempotency, audits, and local captured behavior are consistent.

## Invitation/account email requirements

New user account emails are invitation lifecycle emails:
- invite create/resend queues `user_onboarding.invitation_email.send` work;
- raw invitation tokens are visible only inside the delivery/acceptance boundary;
- admin lists, audit, agent tools, and browser APIs show status and redacted link metadata only;
- delivery failures are visible to authorized admins and auditable;
- duplicate invite/resend/delivery attempts are idempotent by invitation id plus attempt/retry key;
- acceptance still requires WorkOS/AuthKit authentication plus valid invitation/acceptance context.

## Other app feature emails

For future app-specific features, reuse the same Resend email foundation:
- create a feature-specific capability when recipients, templates, policy, or side effects differ;
- define template ids/versions and allowed variable schemas;
- require opt-out/preference checks where the product supports notification preferences;
- preserve tenant/customer scope and recipient authorization;
- use the captured outbox adapter in local/dev/test;
- add delivery status, failure visibility, idempotency, audit, and trace tests.

## Agent tool exposure with `@FunctionTool`

Email can be exposed to agents only as a governed side-effecting capability. Use local/external Akka function tools, not prompt-only instructions.

Tool design rules:
- stable tool ids such as `email.preview` and `email.send_resend`;
- `toolCategory`: `local_function` or `external_side_effect`;
- `capabilityId`: `email.preview`, `email.send`, or feature-specific email capability;
- side-effect level: `external_message`;
- autonomy defaults to `proposal_only` or `approval_required` unless an accepted narrow policy grants bounded autonomous sends;
- tool arguments should name template id/version, recipient account ids or scoped recipient refs, feature object refs, and approved variables; avoid raw arbitrary recipient lists or raw HTML unless explicitly allowed;
- resolve AuthContext, correlation id, tenant/customer scope, and idempotency outside model-controlled text where possible;
- enforce `ToolPermissionBoundary`, recipient authorization, template allowlist, policy/approval, and rate/abuse controls before queueing email;
- return `approval_required`, `forbidden`, `validation_failed`, `queued`, or `delivered/captured` shapes without exposing secrets or raw tokens;
- emit AgentWorkTrace/ToolInvocationTrace and AdminAuditEvent for allowed, denied, and approval-required calls.

Example shape:

```java
public final class ResendEmailTools {
  @FunctionTool(description = "Preview an approved email template for the selected tenant/customer context. No email is sent.")
  public EmailPreviewResult previewEmail(PreviewEmailRequest request) {
    // enforce AuthContext + ToolPermissionBoundary + template allowlist
    // render redacted preview using approved template/version
  }

  @FunctionTool(description = "Queue an approved email for Resend delivery. External message side effect; requires email.send capability, recipient scope, idempotency key, audit, and approval when policy requires it.")
  public EmailSendResult queueEmailForResend(SendEmailRequest request) {
    // enforce AuthContext + ToolPermissionBoundary + policy/approval
    // create EmailOutboxMessage; Consumer sends through Resend or captured adapter
  }
}
```

Register with `.tools(resendEmailTools)` only for agents whose active `ToolPermissionBoundary` grants the relevant tool id/capability.

## Testing checklist

- production readiness fails when required Resend settings are missing;
- local/dev/test captured outbox records emails without external delivery;
- invite email create/resend queues correct outbox work and never exposes raw tokens outside delivery/acceptance boundary;
- Resend success records provider/message id, attempts, status, audit, and trace facts;
- Resend failure records safe error summary, failure status, retry eligibility, audit, and admin visibility;
- duplicate delivery/retry is idempotent;
- cross-tenant/customer recipients are forbidden;
- disabled or unauthorized actors and agents cannot send email;
- `@FunctionTool` preview/send tests cover allowed, forbidden, approval-required, validation-failed, idempotent, and traced outcomes;
- frontend bundles and browser-safe APIs contain no Resend keys, webhook secrets, raw invite tokens, or provider payloads.

## Anti-patterns

Avoid:
- introducing another production email provider;
- treating email delivery as optional for production onboarding;
- calling Resend directly from browser code;
- letting agents send arbitrary raw emails or raw HTML without template/policy controls;
- using prompt text or tool descriptions as authorization;
- hiding delivery failures only in logs;
- bypassing outbox/idempotency/audit for direct provider calls.
