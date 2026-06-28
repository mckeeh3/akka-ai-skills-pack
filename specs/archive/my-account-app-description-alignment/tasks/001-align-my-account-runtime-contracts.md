# MAAD-001: Align My Account runtime contracts

## Goal

Implement the follow-up from the My Account app-description alignment review so the current-intent graph and root runtime agree at the bounded My Account workstream scope.

## Inputs

- `app-description/domains/core-starter/workstreams/my-account/**`
- `app-description/domains/core-starter/capabilities/account-context-and-profile.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/main/java/ai/first/application/foundation/agent/AgentBehaviorSeedLoader.java`
- My Account backend/frontend tests

## Implementation requirements

1. Normalize notification capability/tool naming to the concrete fine-grained ids currently used by runtime.
2. Require My Account chat-tool-plan steps to be explicitly allowed by the active `ToolPermissionBoundary`, not just by catalog membership.
3. Record durable audit/work trace facts for proposal, confirmation, step start/complete/fail/skip/deny/provider-blocked lifecycle events.
4. Return a browser-safe My Account no-access recovery payload for protected workstream APIs when `/api/me` would be in no-access recovery.
5. Expose a notification source-open action that reauthorizes target capability and opens the target surface or returns `surface-my-account-open-denied`.
6. Normalize digest progress surface status text to the app-description vocabulary where needed.

## Validation

```bash
mvn -Dtest='MyAccountBrowserWorkstreamSmokeTest,WorkstreamServiceTest,AgentBehaviorSeedLoaderTest,MyAccountPersonalAttentionDigestServiceTest,MyAccountPersonalAttentionDigestAutonomousAgentTest,DigestExportServiceTest,MyAccountEvidenceToolsTest' test
npm --prefix frontend test -- --run frontend/src/workstream-my-account-vertical.contract.test.mjs
git diff --check
```
