# Conversation Capture: Autonomous Agents Integration

## Trigger

The user asked to review the newly released Akka Autonomous Agent component under `akka-context`. After review, the user confirmed this component is an excellent fit for the skills pack, especially for what the pack has been calling background/internal agents.

## Decisions made

1. **Executable coverage is required immediately.**
   The initiative must include executable examples and tests under `src/`, not only docs and skills.

2. **Terminology changes now.**
   Existing pack language around background/internal agents should be revised:
   - `AutonomousAgent` is the canonical fit for durable, task-oriented, model-driven internal/background work.
   - request-based Akka `Agent` remains the right fit for workstream agents and bounded request/response interactions.
   - `Workflow` remains the fit for deterministic orchestration and approval/retry/compensation flow.

3. **Starter/generated-app guidance should switch now.**
   This is not only a future migration note. Generated-app doctrine and starter guidance should use Autonomous Agents for background/internal agent needs where the component semantics fit.

4. **Governance still applies.**
   Autonomous Agent features align well with the skills pack's governed runtime model and should be integrated with:
   - managed agent definitions/profiles;
   - prompt/skill/reference governance;
   - model policy and provider-secret boundaries;
   - tool permission boundaries;
   - authorization and tenant isolation;
   - task/work traces;
   - approval/supervision surfaces.

5. **Default for future generated AI-first SaaS internal work.**
   Autonomous Agents should be the default for long-running background investigations, internal specialist agents, supervision/escalation processors, autonomous monitoring/remediation, batch/review/evaluation loops, and similar internal/background jobs.

6. **Do not over-correct.**
   Not every agent becomes autonomous. Request-based `Agent` remains appropriate for:
   - user-facing workstream turns;
   - bounded one-shot structured responses;
   - simple tool-using assistant calls;
   - Workflow steps with a single model round-trip.

7. **Name collision must be handled.**
   Akka uses `AgentDefinition` in the autonomous-agent API. This pack already uses `AgentDefinition` as a governed managed-agent domain concept. Guidance must distinguish these explicitly.

8. **Verification loop is required.**
   After the first-pass migration, add a verification task that reviews changes and appends follow-up tasks for gaps. If follow-up tasks are added, add another verification task. Once the verification loop is finished, add a task that defines the necessary tasks for additional executable examples.

9. **Every task commits.**
   Each task must be self-contained, run in a fresh harness session, update the queue, and make one focused git commit before it is marked `done`.

## Concerns accepted

- Avoid name confusion between Akka autonomous `AgentDefinition` and the pack's governed managed-agent `AgentDefinition`.
- Avoid making Autonomous Agents the answer for every agent use case.
- Layer governance explicitly; official Akka component semantics do not by themselves solve tenant isolation, authorization, policy, approval, audit, or tool-boundary enforcement.
- Tests must prove async/durable behavior with `TestModelProvider`, `AutonomousAgentTools`, Awaitility, task snapshots, failure paths, and coordination events where relevant.

## Suggested implementation shape accepted

- Create a durable mini-project under `specs/autonomous-agents-integration/`.
- Start with deep official documentation review and source notes.
- Add routing doctrine before broad examples.
- Add a focused Autonomous Agent skill family.
- Update AI-first SaaS doctrine, capability-first doctrine, skills routing, and coverage matrix.
- Add executable local examples/tests.
- End with a verification loop and follow-up example-task-definition task.

## Unresolved questions

None currently block first-pass planning. Later implementation tasks may discover detailed API, dependency, or sample-structure questions and should block/update the queue rather than guess.
