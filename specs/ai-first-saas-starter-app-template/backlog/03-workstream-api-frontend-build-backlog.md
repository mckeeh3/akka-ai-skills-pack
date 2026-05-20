# Build Backlog 03: Workstream API and Frontend Wiring

## Items

1. Define browser-facing DTOs and endpoint contracts for core workstream bootstrap and surfaces.
2. Wire frontend production client to real `/api/...` endpoints while retaining fixture clients for tests/dev fixtures.
3. Implement User Admin dashboard → list/search → detail/edit vertical against real endpoints.
4. Implement Access/Profile, Audit/Trace, Governance/Policy, and Agent Admin surface endpoints to the level available from backend sprints.
5. Add endpoint integration tests for static assets, API route separation, packaged HTML/JS/CSS, and protected API behavior.
6. Add frontend contract/build tests proving real-client wiring, state/error handling, deep links, capability action results, and stale/realtime behavior.

## Completion signal

The canonical workstream UI is no longer only fixture-backed; it can operate against starter app backend APIs.
