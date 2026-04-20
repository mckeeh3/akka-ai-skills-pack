---
name: akka-view-query-patterns
description: Design Akka Java SDK View query methods, result wrapper records, aliases, and pagination-friendly response shapes. Use when the task is mainly about query structure and mapping.
---

# Akka View Query Patterns

Use this skill when the main problem is shaping the query result and Java return type.

## Required reading

Read these first if present:
- `akka-context/reference/views/concepts/result-mapping.html.md`
- `akka-context/reference/views/concepts/pagination.html.md`
- `akka-context/reference/views/syntax/query.html.md`
- `akka-context/reference/views/syntax/select.html.md`
- `../../../src/main/java/com/example/application/DraftCartsByCheckedOutView.java`

## Core rules

1. Query methods accept 0 or 1 parameter.
2. Multi-row results should usually return a wrapper record, not `List<Row>` directly.
3. Use `SELECT * AS <field>` so the alias matches the wrapper field name.
4. If you include pagination metadata, add explicit aliases such as `total_count() AS totalCount`.
5. Keep Java field names aligned with query aliases exactly.
6. Use a request record when a query needs multiple parameters.
7. Add `ORDER BY` for stable pagination behavior.
8. For streaming queries, keep the row shape simple and stable for downstream SSE or gRPC forwarding.

## Canonical patterns

### 1. Multi-row wrapper

Query:
```sql
SELECT * AS carts
FROM draft_carts_by_checked_out
WHERE checkedOut = :checkedOut
ORDER BY cartId
```

Java:
```java
public record DraftCartSummaries(List<DraftCartSummary> carts) {}
```

### 2. Offset pagination wrapper

Query:
```sql
SELECT * AS carts
FROM draft_carts_by_checked_out
WHERE checkedOut = :checkedOut
ORDER BY cartId
OFFSET :offset
LIMIT :pageSize
```

Java:
```java
public record FindPage(boolean checkedOut, int offset, int pageSize) {}
public record DraftCartPage(List<DraftCartSummary> carts) {}
```

Repository example:
- `DraftCartsByCheckedOutView#getCartsPage`

### 3. Non-updating stream query

Query:
```sql
SELECT *
FROM shopping_cart_audit
WHERE deleted = :deleted
ORDER BY cartId
```

Java:
```java
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

## Anti-patterns

Avoid:
- `QueryEffect<List<Row>>` for multi-row queries
- aliases that do not match Java record fields
- multiple scalar parameters when one request record is clearer
- pagination without stable ordering
- exposing domain-only names when the API needs a query-specific result shape

## Review checklist

Before finishing, verify:
- the query aliases match the Java fields exactly
- wrapper records are used for collections and pagination metadata
- request records are used when more than one parameter is required
- `ORDER BY` is present when using `OFFSET`/`LIMIT`
