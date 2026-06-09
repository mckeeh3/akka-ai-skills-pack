# Deferred Typed Surfaces

This template intentionally defines process-level surface-ready baselines, not complete app-level foundation workstream implementations. The typed surfaces below are referenced by the foundation surface graph as likely implementation targets, but remain deferred until an app-development cleanup pass defines their full payload schemas, actions, capabilities, tests, and runtime paths.

`markdown_response` and `system_message` may be used as first-slice fallbacks only when the user outcome is explanatory, blocked, deferred, or safely recoverable. They must not hide missing typed surfaces for consequential decisions, forms, tables, workflow/task progress, governed document review, or policy evidence once those interactions are in implementation scope.

| Deferred surface id | Expected type/version | Referenced from | Intended owner | Fallback allowed in template | Completion trigger |
| --- | --- | --- | --- | --- | --- |
| `my-account-profile-card` | profile/settings-card/v1 | `my-account-dashboard` | `my-account-agent` | `markdown_response` or `system_message` | Profile/settings editing becomes an app-level implementation task. |
| `my-account-settings-card` | settings-card/v1 | `my-account-dashboard` | `my-account-agent` | `markdown_response` or `system_message` | Notification, locale, or account settings are implemented. |
| `invitation-draft-form` | form/review/v1 | `user-admin-dashboard`, `user-admin-user-list` | `user-admin-agent` | `system_message` for deferred/unavailable draft; `decision-card` for approval | Invitation creation/resend/revoke runtime path is implemented. |
| `task-progress-surface` | task-progress/v1 | dashboard investigations, evidence requests, internal workers | owning source workstream agent | `system_message` for started/deferred/failed progress | Durable workflow/AutonomousAgent task progress is user-visible. |
| `task-result-surface` | task-result/v1 | dashboard investigations, evidence requests, internal workers | owning source workstream agent | `markdown_response` for non-consequential summary; `decision-card` for approvals | Durable internal worker output drives decisions, exceptions, or follow-up actions. |
| `agent-detail-card` | governed-record-detail/v1 | `agent-governance-center` | `agent-admin-agent` | `markdown_response` for read-only explanation | Managed AgentDefinition lifecycle/detail UI is implemented. |
| `agent-version-card` | governed-version-card/v1 | `agent-governance-center` | `agent-admin-agent` | `markdown_response` for read-only explanation | Prompt/skill/reference/version inspection is implemented. |
| `behavior-diff-review` | diff/review/v1 | `agent-governance-center`, `decision-card` | `agent-admin-agent` | `decision-card` for approval summary | Behavior editing proposal/review runtime is implemented. |
| `safe-test-console-result` | test-result/v1 | `agent-governance-center` | `agent-admin-agent` | `system_message` for blocked/deferred tests | Governed safe test console is implemented. |
| `trace-export-status` | export-status/v1 | `audit-trace-explorer` | `audit-trace-agent` | `system_message` for approval/deferred/failure | Scoped trace export workflow is implemented. |
| `policy-clause-card` | policy-clause/v1 | `agent-governance-center`, `decision-card`, domain-specific review surfaces | `governance-policy-agent` | `markdown_response` for read-only clause summary | Policy document/version review is implemented. |

## App-level cleanup boundary

Completing these surfaces is application-development work, not skills-pack maintenance. Future app-level cleanup tasks should select one minimum implementable workstream slice, define the deferred surface contract, map every action to governed capabilities/tools, implement the Akka/API/UI path, and record runtime validation evidence before raising readiness.
