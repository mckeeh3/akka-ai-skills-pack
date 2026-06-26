# Realization: Akka components for Audit/Trace

Capability: `audit-and-trace-investigation`.

This map is docs-only. It states the tenant-admin activity-log scope component responsibilities implied by current intent and does not prove implementation alignment.

## Required component responsibilities

| Intent binding | Runtime responsibility |
|---|---|
| Immutable audit trace store | Persist tenant-scoped human request/response, agent request/response, tool-call, denial, detail-view, search, retention-setting, and retention-expiry evidence as immutable records until retention expiry. |
| Audit trace query/read model | Support tenant-admin search over deterministic metadata/summary fields and filters without indexing full payloads. |
| Trace detail read path | Reauthorize tenant-admin access and return authorized full payload detail with human/agent/tool/denial fields. |
| Retention setting state | Store tenant retention setting with default 90 days and allowed 30–365 day updates. |
| Retention expiry process | Remove immutable records only through retention expiry and leave diagnosable retention-expiry evidence that does not reveal expired payloads. |
| Authorization boundary | Enforce selected `AuthContext`, active membership, tenant-admin role/capability, tenant isolation, disabled/inactive denial, and hidden target non-enumeration for every read/mutation. |

## Validation evidence required before build completion

- Component/API tests for immutable record creation and tenant-scoped search/detail reads.
- Component/API tests for tool-call to parent request/response linkage.
- Component/API tests for retention setting default, valid update, invalid bounds, same-value no-op, and audit trace emission.
- Component/API tests for tenant isolation, disabled/inactive user denial, non-admin denial, hidden/expired trace non-enumeration, and secret omission.

## Explicit tenant-admin activity-log scope component exclusions

Do not include export bundle generation, investigation notes, suspicious-activity review state, autonomous audit summaries, or agent-tool trace search authority in this tenant-admin activity-log scope build slice unless later current intent adds them.
