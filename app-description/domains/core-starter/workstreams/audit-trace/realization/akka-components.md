# Realization: Akka components for Audit/Trace

Capability: `audit-and-trace-investigation`.

This map is docs-only. It points to current implementation evidence and does not change runtime behavior.

## Component and service evidence

| Intent binding | Akka / Java evidence | Notes |
|---|---|---|
| Admin audit and trace repository | `src/main/java/ai/first/application/foundation/audit/AuditTraceService.java`, `AuditTraceRepository.java`, `AkkaAuditTraceRepository.java`, `AdminAuditView.java` | Authoritative evidence access must be scoped, redacted, and audited. |
| Agent/runtime trace evidence | `src/main/java/ai/first/application/foundation/agent/AgentRuntimeTraceEntity.java`, `AgentRuntimeTraceView.java`, `AgentRuntimeTraceSink.java` | Connects prompt/skill/reference/model/tool/data/policy activity to investigation timelines. |
| Workstream event/log trace | `src/main/java/ai/first/application/foundation/workstream/DurableWorkstreamLogEntity.java`, `DurableWorkstreamEventRepositoryEntity.java`, `WorkstreamEventAttentionConsumer.java` | Correlates shell actions, agent replies, surface actions, and attention updates. |
| Audit summary worker | `src/main/java/ai/first/application/coreapp/audit/AuditTraceSummaryService.java`, `AuditTraceSummaryAutonomousAgent.java`, `DurableAuditTraceSummaryTaskRepositoryEntity.java` | Summaries are bounded, trace-linked, and fail closed when provider config is absent. |
| Evidence tools | `AuditTraceEvidenceTools.java` | Agent-visible evidence reads must use authorized, redacted query shapes. |

## Validation evidence

- `src/test/java/ai/first/application/foundation/audit/AdminAuditViewTest.java`
- `src/test/java/ai/first/application/foundation/agent/AgentRuntimeTraceEntityTest.java`
- `src/test/java/ai/first/application/foundation/agent/AgentRuntimeTraceViewTest.java`
- `src/test/java/ai/first/application/foundation/workstream/DurableWorkstreamLogEntityTest.java`
- `src/test/java/ai/first/application/coreapp/audit/AuditTraceSummaryServiceTest.java`
- `src/test/java/ai/first/application/coreapp/audit/AuditTraceSummaryAutonomousAgentTest.java`
- `frontend/src/workstream-audit-trace-vertical.contract.test.mjs`

## Gaps / caveats

- Redacted export and sensitive evidence access require policy gates; descriptions alone do not prove export readiness.
- Local demo/fail-closed repositories are test support or failure-path proof, not normal runtime substitutes.
