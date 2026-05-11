# TASK-08-006: Implement supplies command-center and decision-card web UI

## Purpose

Add a minimal AI-first supervision UI surface for the supplies autopilot reference slice.

## Required reads

- `AGENTS.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/web-ui-style-guide.md`
- `docs/examples/ai-first-dca-app-description/app-description/55-ui/README.md`
- `specs/ai-first-skills-pack-migration/sprints/08-executable-ai-first-reference-slice-sprint.md`
- `specs/ai-first-skills-pack-migration/backlog/08-executable-ai-first-reference-slice-build-backlog.md`
- supplies endpoint/API files from `TASK-08-005`

## Scope

- Add a supplies command-center screen for risk rows, pending decisions, and recent auto/suppressed shipments.
- Add a decision-card detail screen with evidence, risk, confidence, impact, alternatives, policy triggers, action controls, and trace link.
- Add typed API client calls, loading/empty/error/success/action-pending states, and basic accessible/responsive layout.
- Add frontend build/smoke tests and route/static asset tests as appropriate.

## Non-goals

- No full DCA navigation shell beyond what is needed for the reference slice.
- No full policy governance center or digest UI.
- Do not invent a new visual theme if an app-description/spec style is absent; block or add a style-selection question instead.

## Skills

- `ai-first-saas`
- `ai-first-saas-ui-surfaces`
- `akka-web-ui-apps`
- `akka-web-ui-ux-design`
- `akka-web-ui-frontend-project`
- `akka-web-ui-api-client`
- `akka-web-ui-state-rendering`
- `akka-web-ui-forms-validation`
- `akka-web-ui-accessibility-responsive`
- `akka-web-ui-testing`
- `akka-http-endpoints`
- `akka-http-endpoint-web-ui`
- `akka-http-endpoint-testing`

## Expected outputs

- Minimal supplies UI files and Akka static hosting updates.
- Frontend/API smoke tests.

## Required checks

- Run frontend build/test command if a frontend project exists.
- Run route/static hosting tests.
- Verify decision-card UI never hides policy/evidence/trace context behind a generic approve button.

## Done criteria

- The UI prioritizes supervision and decision quality over CRUD navigation.
- Approval/rejection/suppression actions show evidence, policy, risk/confidence, and trace context.