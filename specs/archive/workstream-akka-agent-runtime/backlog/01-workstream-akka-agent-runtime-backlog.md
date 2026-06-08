# Workstream Akka Agent Runtime Backlog

## Build sequence

1. Add hard regression inventory/guards that expose the current gap.
2. Implement a concrete Akka Agent component for governed workstream responses.
3. Wire browser/API message submission through the Akka Agent runtime path.
4. Add local smoke/manual validation for real provider-backed execution.
5. Update starter docs and queue language so future agents cannot call service-level provider calls or fake seams equivalent to a fully implemented Akka Agent workstream.

## Acceptance summary

The starter is not complete until a generated project contains an Akka Agent component used by the normal workstream message path and a maintainer can validate a prompt from the browser/API path against a configured real model provider.
