# Sprint 01: Real Akka Runtime Replacement

## Objective

Remove the remaining non-Akka normal-runtime seams from the AI-first SaaS starter and replace them with real Akka component-backed runtime paths. Test doubles may remain only in test source or explicitly test-only assets.

## Source context

- User decision: all non-Akka substitute/defaults must be replaced with real Akka components.
- Prior mini-project: `specs/full-core-smb-runtime-durability-remediation/` improved gating but allowed local/demo adapters; this sprint supersedes that compromise for normal runtime.
- Current starter contains `LocalDemo*`, `FailClosed*`, fixture clients, and constructors that still instantiate non-Akka runtime adapters.

## Ordered work areas

1. Produce a strict source map classifying every remaining local-demo/fail-closed/fixture/default seam and identify its Akka component replacement.
2. Replace identity, account/profile/settings, membership/role/capability, and bootstrap admin state with Akka components and views.
3. Replace workstream log, audit trace, access-review task, and governance policy stores with Akka components and views/workflows where appropriate.
4. Ensure invitation, governed-agent behavior, and agent runtime traces are always bound to their Akka-backed implementations in normal runtime.
5. Remove normal-runtime local/demo/fail-closed constructors and adapters from main source, or move any required test doubles to test source.
6. Quarantine frontend fixtures so production runtime cannot select them.
7. Update docs/skills/template README/release notes to state real Akka replacement is mandatory.
8. Verify with rendered starter tests, frontend checks, scans, and queue completion review.

## Acceptance criteria

- Source scans show no `LocalDemo`, `Substitute`, fixture, mock, fake, canned, or model-less substitute wired in `backend/src/main/java` or production frontend runtime paths.
- Any remaining mocks/fakes/fixtures live under tests or explicit test assets only.
- Normal runtime service construction requires real Akka component-backed repositories/sinks/invokers.
- Workstream features persist and read state through Akka components at the stated starter scope.
- README/skills/docs no longer describe replacing non-Akka substitute adapters as optional later hardening.
- Validation commands in the verification task pass or append bounded follow-up tasks before a new terminal verification task.

## Handoff notes

Prefer small vertical backend replacement tasks over a single broad rewrite. When a store has existing Akka component classes, bind and test them first. When no component exists, add the minimal Akka entity/view/workflow needed for the starter's claimed feature scope.
