# Pending Tasks: My Account App-description Alignment

## Queue policy

- Execute one task per fresh harness session.
- Mark the task `in-progress` before implementation edits and `done` only after validation passes.

### MAAD-001: Align My Account app-description delta with runtime contracts

- status: done
- source: My Account app-description alignment review requested after current-intent changes
- task brief: `specs/my-account-app-description-alignment/tasks/001-align-my-account-runtime-contracts.md`
- scope:
  - normalize My Account notification capability names between app-description and runtime
  - enforce per-tool `ToolPermissionBoundary` checks for My Account `human_chat_tool_plan` steps
  - emit durable trace/audit facts for `human_chat_tool_plan.*` lifecycle events
  - add protected workstream API no-access recovery payloads for My Account/no-active-context cases
  - expose notification source-opening reauthorization from the notification center
  - normalize personal digest progress status vocabulary where it differs from the surface contract
- required checks:
  - `mvn -Dtest='MyAccountBrowserWorkstreamSmokeTest,WorkstreamServiceTest,AgentBehaviorSeedLoaderTest,MyAccountPersonalAttentionDigestServiceTest,MyAccountPersonalAttentionDigestAutonomousAgentTest,DigestExportServiceTest,MyAccountEvidenceToolsTest' test`
  - `npm --prefix frontend test -- --run frontend/src/workstream-my-account-vertical.contract.test.mjs`
  - `git diff --check`
- done criteria:
  - app-description/runtime naming and boundary checks are aligned
  - My Account tests cover the adjusted contracts
  - residual model/provider caveat remains fail-closed
