# Workstream: My Account

## Purpose

Give the signed-in human a safe AI-first personal control point for selected authority context, personal attention, profile, named-theme/settings preferences, in-app notifications, governed digest/export requests, and safe recovery from unavailable or denied workstream/source openings.

## Functional agent

Owns `my-account-agent` as its exactly-one user-facing functional-agent binding. Runtime instances are selected-context workstream logs, not page sessions.

## Capability binding

Primary capability: `../../capabilities/account-context-and-profile.md`.

## Attention model

Backend-owned attention includes personal action items, notification acknowledgements, context problems, digest/export status, unavailable-source recovery, and provider/configuration denials visible to the current user. Counts feed the signed-in user rail tile and My Account personal command-center aggregation.

## Readiness posture

This node captures current intent only. Runtime readiness still requires local Akka/API/UI validation and model/provider fail-closed proof where applicable.
