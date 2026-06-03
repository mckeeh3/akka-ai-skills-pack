# Upstream Merge Guide

This repository is intended to work as a reusable core app baseline that product teams can fork and extend.

## Merge-friendly strategy

1. Keep `ai.first` as the default Java package unless your product deliberately performs a package rename.
2. Put product behavior in domain-specific extension paths.
3. Keep core edits small, stable, and hook-oriented.
4. Record product changes in `app-description/extensions/<domain>/` and `specs/extensions/<domain>/`.
5. Run root backend and frontend checks before merging upstream changes and after conflict resolution.

## Recommended branches

- `main` or `core-upstream`: tracks this core app baseline.
- `product/<domain>`: contains domain-specific extension work.
- short-lived task branches: one pending-task or feature slice per branch when practical.

## Conflict resolution priorities

When upstream core changes conflict with domain changes:

1. Preserve security, authorization, tenant/customer scoping, audit/work traces, and provider fail-closed behavior.
2. Prefer upstream core foundation changes when they improve shared runtime behavior.
3. Move product-specific logic back into extension paths if it drifted into core files.
4. Introduce or update a small core registry/hook when both upstream and domain code need the same integration point.
5. Re-run runtime checks that cover the affected backend, frontend, auth, and workstream surfaces.

## Checks after an upstream merge

Run at least:

```bash
mvn test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
git diff --check
```

Add targeted smoke/manual runtime checks when the merge affects auth, `/api/me`, workstream APIs, managed-agent runtime, notification/email behavior, provider configuration, or frontend shell behavior.
