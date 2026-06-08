# Workstream: Audit/Trace

## Purpose

Search, inspect, explain, redact, summarize, export, and annotate audit/work trace evidence for the selected scope.

## Functional agent

Owns `audit-trace-agent` as its exactly-one user-facing functional-agent binding. Runtime instances are selected-context workstream logs, not page sessions.

## Capability binding

Primary capability: `../../capabilities/audit-and-trace-investigation.md`.

## Attention model

Backend-owned attention includes failed or denied protected actions, export approval needs, suspicious activity, provider failures, unresolved investigation notes, and trace gaps. Counts feed the left rail and, where personal, My Account aggregation.

## Readiness posture

This node captures current intent only. Runtime readiness still requires local Akka/API/UI validation and model/provider fail-closed proof where applicable.
