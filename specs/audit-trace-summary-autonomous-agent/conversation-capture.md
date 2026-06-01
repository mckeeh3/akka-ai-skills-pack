# Conversation Capture: Audit/Trace Summary AutonomousAgent

## User request

After AutonomousAgent worker pattern extraction completed, the assistant recommended the next worker vertical: Audit/Trace scheduled audit summary. The user said:

> go ahead

## Decision

Create a mini-project for Audit/Trace Summary AutonomousAgent.

## Rationale

This is the best next worker because it exercises scheduled/summary-style background work, trace evidence/redaction, audit reviewer attention, result surfaces, v3 events, and provider fail-closed behavior.

## Constraints

- Start with a bounded Audit/Trace worker, not a general digest platform.
- Normal success must use a concrete Akka `AutonomousAgent` task.
- Missing provider/model configuration fails closed.
- Summary output is advisory and must not mutate audit records, policies, users, or agent behavior directly.
