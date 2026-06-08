# Conversation Capture

## User concern

The user asked whether the workstream agent is implemented with an Akka Agent. Inspection showed that the starter workstream path currently uses `WorkstreamService -> AgentRuntimeService -> ModelProviderClient`; it has governed runtime records and a real provider adapter, but no concrete `akka.javasdk.agent.Agent` subclass for the user-facing workstream agent path in the starter template.

The user clarified that the objective of v0 is to use a real AI model and that this core feature was specifically requested as fully implemented. The user asked why features keep being marked complete without full frontend-to-backend implementation and how to fix it.

## Root cause to address

- Guidance says real runtime path is required, but template/regression checks did not force the workstream response path through an Akka Agent component.
- Unit tests permit fake providers for test isolation, but there is no hard guard preventing service-level/fake-provider seams from being treated as normal runtime completion.
- Completion gates need code-level and test-level enforcement, not only documentation.

## Decision

Use a focused multi-task migration queue because this affects architecture, backend runtime, frontend/backend validation, starter docs, and regression tests. Each task must be self-contained for a fresh harness session and must be committed before being marked done.
