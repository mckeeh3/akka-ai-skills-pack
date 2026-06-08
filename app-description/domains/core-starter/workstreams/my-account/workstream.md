# Workstream: My Account

## Purpose

Give the signed-in human a safe personal control point for profile, settings, selected context, personal attention, notifications, and governed digest/export requests.

## Functional agent

Owns `my-account-agent` as its exactly-one user-facing functional-agent binding. Runtime instances are selected-context workstream logs, not page sessions.

## Capability binding

Primary capability: `../../capabilities/account-context-and-profile.md`.

## Attention model

Backend-owned attention includes personal action items, notification acknowledgements, context problems, digest/export status, and provider/configuration denials visible to the current user. Counts feed the left rail and, where personal, My Account aggregation.

## Readiness posture

This node captures current intent only. Runtime readiness still requires local Akka/API/UI validation and model/provider fail-closed proof where applicable.
