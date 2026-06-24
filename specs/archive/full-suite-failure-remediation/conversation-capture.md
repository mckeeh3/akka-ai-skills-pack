# Conversation Capture: Full Suite Failure Remediation

## Background

The Workstream Chat Tool Catalog Expansion mini-project closed successfully after targeted verification. Its final verification notes recorded that the expansion-specific tests passed, but full-suite runs still had pre-existing failures outside the expansion scope.

The assistant recommended creating a focused mini-project for those failures so future verification is cleaner.

## User request

The user said:

```text
go ahead and do 2. Create a mini-project for pre-existing full-suite failures
```

## Planning decision

Create a root-app mini-project under:

```text
specs/full-suite-failure-remediation/
```

This project is app-realization work, not skills-pack maintenance.

## Initial failure sources

Use `specs/workstream-chat-tool-catalog-expansion/verification-notes.md` as the initial source of known failures, but the first queued task must rerun/reproduce the current suite because the failure list may have changed.

## Safety constraints

- Preserve tenant/customer scoping, backend authorization, audit/work traces, provider fail-closed behavior, frontend secret boundaries, deterministic surface routing, and confirmed chat-tool semantics.
- Do not weaken tests to hide failures.
- If a test is stale, update the accepted current-intent/app-description/spec evidence and then update the test.
- Do not count fake/model-less normal runtime as satisfying model-backed or autonomous runtime expectations.
