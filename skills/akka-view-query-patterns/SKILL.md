---
name: akka-view-query-patterns
description: Design Akka Java SDK View query methods, result wrapper records, aliases, pagination, supported ORDER BY/index shapes, and optional-filter access paths. Use when the task is mainly about query structure and mapping.
---

# Akka View Query Patterns

Use this skill when the main problem is shaping the query result and Java return type.

## Required reading

Read these first if present:
- `akka-context/reference/views/concepts/result-mapping.html.md`
- `akka-context/reference/views/concepts/pagination.html.md`
- `akka-context/reference/views/syntax/query.html.md`
- `akka-context/reference/views/syntax/select.html.md`
- `akka-context/reference/views/syntax/order-by.html.md`
- `../../../src/main/java/com/example/application/DraftCartsByCheckedOutView.java`

## Core rules

1. Query methods accept 0 or 1 parameter.
2. Multi-row results should usually return a wrapper record, not `List<Row>` directly.
3. Use `SELECT * AS <field>` so the alias matches the wrapper field name.
4. If you include pagination metadata, add explicit aliases such as `total_count() AS totalCount`.
5. Keep Java field names aligned with query aliases exactly.
6. Use a request record when a query needs multiple parameters.
7. Add `ORDER BY` for stable pagination behavior, but only on columns that are indexed by the query.
8. Every non-SSE `ORDER BY` column must also appear in the same query method's `WHERE` conditions. Add an explicit equality/range condition such as `column >= :minColumn` when you need a deterministic tie-break sort column.
9. Do not use optional-filter `OR` patterns such as `(:customerId = '' OR customerId = :customerId)`; create separate query methods for the filtered and unfiltered access paths so each query has explicit indexes.
10. For view queries consumed by SSE endpoints, do **not** include `ORDER BY`; SSE view events are delivered in created/event order. Use a dedicated unsorted `QueryStreamEffect` query for SSE and keep sorted/paginated `QueryEffect` methods separate.
11. For streaming queries, keep the row shape simple and stable for downstream SSE or gRPC forwarding.

## ORDER BY and index rule

This rule applies to non-SSE queries. For view queries that are forwarded to SSE with `serverSentEventsForView(...)`, omit `ORDER BY` entirely because SSE emits view events in created/event order.

Akka can reject a View query at runtime with an error like:

```text
AK-00101 ... This query isn't supported yet. ORDER BY columns must also be indexed (part of the WHERE conditions).
```

Prevent this by checking every generated query:

- If the query says `ORDER BY a`, the `WHERE` clause must contain a condition on `a`.
- If the query says `ORDER BY a, b`, the `WHERE` clause must contain conditions on both `a` and `b`.
- Existing equality filters such as `tenantId = :tenantId` do not index unrelated tie-break columns such as `deviceId`.
- A lower/upper bound can be used to make a sort column explicit, for example `deviceId >= :minDeviceId`.
- Do not add a meaningless bound if it changes the business result set; instead choose a supported sort order, add a real query parameter, or split into a purpose-built View/query.

## Canonical patterns

### 1. Multi-row wrapper

Query:
```sql
SELECT * AS carts
FROM draft_carts_by_checked_out
WHERE checkedOut = :checkedOut
  AND cartId >= :minCartId
ORDER BY cartId
```

Java:
```java
public record FindCheckedOutCarts(boolean checkedOut, String minCartId) {}
public record DraftCartSummaries(List<DraftCartSummary> carts) {}
```

Call with `minCartId = ""` only when cart ids are non-empty strings and lexicographic lower-bound behavior is correct.

### 2. Offset pagination wrapper

Query:
```sql
SELECT * AS carts
FROM draft_carts_by_checked_out
WHERE checkedOut = :checkedOut
  AND cartId >= :minCartId
ORDER BY cartId
OFFSET :offset
LIMIT :pageSize
```

Java:
```java
public record FindPage(boolean checkedOut, String minCartId, int offset, int pageSize) {}
public record DraftCartPage(List<DraftCartSummary> carts) {}
```

Repository example:
- `DraftCartsByCheckedOutView#getCartsPage`

### 3. Non-updating non-SSE stream query

Use this only when the stream is collected directly or forwarded through a protocol that supports sorted query streams. If the same data is exposed as SSE, create a separate no-`ORDER BY` stream query.

Query:
```sql
SELECT *
FROM shopping_cart_audit
WHERE deleted = :deleted
  AND cartId >= :minCartId
ORDER BY cartId
```

Java:
```java
public record FindByDeleted(boolean deleted, String minCartId) {}

public QueryStreamEffect<AuditRow> streamByDeleted(FindByDeleted request) {
  return queryStreamResult();
}
```

Repository example:
- `ShoppingCartAuditView#streamByDeleted`

### 4. Offset pagination with total count

When the client needs total rows as well as a page slice, follow the same wrapper pattern and add an alias for `total_count()`.

Query:
```sql
SELECT * AS carts, total_count() AS totalCount
FROM draft_carts_by_checked_out
WHERE checkedOut = :checkedOut
  AND cartId >= :minCartId
ORDER BY cartId
OFFSET :offset
LIMIT :pageSize
```

Java:
```java
public record DraftCartPageWithTotal(List<DraftCartSummary> carts, int totalCount) {}
```

For this pattern, read:
- `akka-context/reference/views/concepts/pagination.html.md`
- `akka-context/reference/views/syntax/functions/total-count.html.md`

### 5. Optional filter as explicit query methods

Avoid optional `OR` filters. Model each access path as its own query method.

Bad:
```sql
SELECT * AS consumables
FROM consumable_usage_report
WHERE tenantId = :tenantId
  AND (:customerId = '' OR customerId = :customerId)
  AND lowestConsumablePercent <= :maxPercent
ORDER BY lowestConsumablePercent, deviceId
OFFSET :offset
LIMIT :pageSize
```

Good unfiltered access path:
```sql
SELECT * AS consumables
FROM consumable_usage_report
WHERE tenantId = :tenantId
  AND lowestConsumablePercent <= :maxPercent
  AND deviceId >= :minDeviceId
ORDER BY lowestConsumablePercent, deviceId
OFFSET :offset
LIMIT :pageSize
```

Good customer-filtered access path:
```sql
SELECT * AS consumables
FROM consumable_usage_report
WHERE tenantId = :tenantId
  AND customerId = :customerId
  AND lowestConsumablePercent <= :maxPercent
  AND deviceId >= :minDeviceId
ORDER BY lowestConsumablePercent, deviceId
OFFSET :offset
LIMIT :pageSize
```

Pass an empty-string lower bound such as `minDeviceId = ""` only when ids are non-empty strings and that ordering is correct for the domain.

## Anti-patterns

Avoid:
- `QueryEffect<List<Row>>` for multi-row queries
- aliases that do not match Java record fields
- multiple scalar parameters when one request record is clearer
- pagination without stable ordering
- `ORDER BY` in a view query consumed by SSE
- `ORDER BY` columns that are missing from the same non-SSE query's `WHERE` conditions
- optional-filter `OR` expressions instead of separate query methods per access path
- exposing domain-only names when the API needs a query-specific result shape

## Review checklist

Before finishing, verify:
- the query aliases match the Java fields exactly
- wrapper records are used for collections and pagination metadata
- request records are used when more than one parameter is required
- `ORDER BY` is present when using `OFFSET`/`LIMIT`, except for SSE-backed queries where `ORDER BY` must be omitted
- every non-SSE `ORDER BY` column also appears in the same query's `WHERE` conditions
- SSE-backed view queries have no `ORDER BY`
- optional filters are represented as separate query methods, not `OR` branches
