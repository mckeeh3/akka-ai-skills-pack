# Conversation Capture: Notification Email Delivery Channel

## User request

After Notification Platform Foundation completed, the assistant recommended the next delivery-channel slice: email notifications. The user said:

> go ahead. make sure to use the resend service for emails.

## Decision

Create a mini-project for the Notification Email Delivery Channel and explicitly require the existing Resend service for production email delivery.

## Constraints

- Production email uses Resend.
- Local/dev/test uses captured outbox behavior.
- Email is preference-controlled and category-limited.
- Hidden/redacted workstreams/items must not leak by email.
- Missing Resend configuration fails closed; no fake production success.
