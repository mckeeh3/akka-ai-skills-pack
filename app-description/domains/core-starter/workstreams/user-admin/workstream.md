# Workstream: User Admin

## Purpose

Administer users, memberships, invitations, roles, support access, access reviews, identity review, and admin audit summaries within authorized tenant/customer scope.

## Functional agent

Owns `user-admin-agent` as its exactly-one user-facing functional-agent binding. Runtime instances are selected-context workstream logs, not page sessions.

## Capability binding

Primary capability: `../../capabilities/user-and-access-administration.md`.

## Attention model

Backend-owned attention includes pending invitations, risky role/support-access changes, stale access review findings, delivery failures, identity/relink exceptions, last-admin risks, and approval-required decisions. Counts feed the left rail and, where personal, My Account aggregation.

## Readiness posture

This node captures current intent only. Runtime readiness still requires local Akka/API/UI validation and model/provider fail-closed proof where applicable.
