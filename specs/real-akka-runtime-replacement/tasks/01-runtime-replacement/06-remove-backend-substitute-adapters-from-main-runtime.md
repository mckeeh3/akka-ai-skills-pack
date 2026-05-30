# Task: Remove backend substitute adapters from main runtime

## Objective

Delete or move remaining backend substitute adapters and constructors out of `backend/src/main/java` so main runtime source contains only real Akka component-backed implementations for in-scope features.

## Required reads

- AGENTS.md
- specs/real-akka-runtime-replacement/README.md
- specs/real-akka-runtime-replacement/non-akka-runtime-seam-map.md
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/StarterSecurityComponents.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java

## Skills

- none; focused source cleanup and test-quarantine task

## In scope

- Remove `LocalDemo*` and `FailClosed*` repository/sink/adapters from main source after replacement tasks land.
- Remove env/query switches that enable local-demo repositories.
- Remove service constructors that instantiate substitute adapters for normal runtime.
- Add test-only replacements under `backend/src/test/java` only where tests still need isolated fakes.
- Update imports/tests accordingly.

## Out of scope

- Do not delete legitimate fail-closed provider/security errors for missing external configuration.
- Do not remove tests; adapt them to test-only fakes or Akka-backed test runtime.

## Expected outputs

- deleted/moved backend substitute classes
- updated services/constructors/tests
- queue update

## Required checks

- `git diff --check`
- rendered backend test suite or targeted rendered backend tests covering changed areas
- `find templates/ai-first-saas-starter/backend/src/main/java -type f | rg -i "LocalDemo|InMemory|FailClosed.*Repository|FailClosed.*Sink"` must return no production substitute adapters
- `rg -n "AI_FIRST_SAAS_LOCAL_DEMO|local/demo repositories|new LocalDemo|new InMemory" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/README.md`

## Done criteria

- Main backend runtime source no longer contains substitute repository/sink implementations for claimed features.
- Tests still pass using Akka components or test-only fixtures.
- Changes and queue update are committed.

## Commit message

`runtime: remove backend substitute adapters`
