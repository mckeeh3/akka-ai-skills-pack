# Sprint 01: User Admin Surface Conformance Cleanup

## Goal

Bring the implemented User Admin structured surfaces into conformance with the workstream/surface concepts captured in app-description and the prior navigation-tree mini-project.

## Scope

- App-description cleanup decisions and traceability.
- Backend surface envelope/action/payload cleanup.
- Frontend structured-surface rendering cleanup.
- Legacy admin screen retirement or absorption.
- Full-stack regression tests and terminal verification.

## Ordered implementation sequence

1. Align app-description and planning artifacts so implementation tasks inherit canonical ids/types/actions/states.
2. Repair backend payloads and result surfaces to be backend-authored and canonical.
3. Repair backend user detail/invitation detail/task routing and typed system-message outcomes.
4. Repair frontend rendering and retire legacy page behavior.
5. Add/repair tests across backend and frontend.
6. Verify the mini-project done state and append follow-ups if needed.

## Validation posture

Use focused checks per task, with terminal verification expected to run or review:

```bash
git diff --check
mvn -q -Dtest=WorkstreamServiceTest test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
```

Broader checks such as `mvn test` or `npm --prefix frontend run build` may be appended by verification if the touched scope warrants it.
