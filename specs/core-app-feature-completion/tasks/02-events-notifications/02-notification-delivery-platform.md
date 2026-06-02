# Task Brief: Notification Delivery Platform Foundation

## Objective

Implement broad notification platform foundations beyond the current in-app notification center while avoiding false production-channel claims.

## Required reads

- `specs/core-app-feature-completion/README.md`
- `specs/core-app-feature-completion/pending-questions.md`
- `specs/core-app-feature-completion/sprints/02-events-notifications-sprint.md`
- `templates/ai-first-saas-starter/README.md`
- `docs/capability-first-backend-architecture.md`
- `skills/akka-consumers/SKILL.md`
- `skills/akka-timed-actions/SKILL.md`
- `skills/akka-http-endpoints/SKILL.md`

## In scope

- Notification channel registry, preferences, delivery attempt records, retry/no-op/idempotency behavior, analytics summaries, and captured local/test outboxes.
- Provider-neutral external channel types for webhook, SMS, mobile push, Slack, and Teams.
- Fail-closed production behavior when a channel provider is not configured.
- Frontend/API surfaces that accurately label available channels and blocked/unconfigured states.

## Out of scope

- Real provider-specific SMS/push/Slack/Teams adapters unless Q-001 is resolved.

## Checks

- `git diff --check`
- focused backend notification/channel tests
- frontend tests/typecheck/build if surfaces change
- provider-missing fail-closed checks
- `tools/validate-ai-first-saas-starter-fullstack.sh`

## Done criteria

- Notification platform state works locally through Akka-backed paths at provider-neutral scope, and unavailable production channels cannot report successful delivery.
