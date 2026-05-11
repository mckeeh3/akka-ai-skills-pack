# Lifecycle Foundation

## Purpose

This file records the first behavior-level state model foundation for the agent-first DCA example. Detailed transition rules and workflow files will be added in later tasks.

## Customer lifecycle states

```text
Lead / Prospect
Acquired / Contract Pending
Onboarding Planned
Installation In Progress
DCA Validation
Operational / Active Service
At Risk / Exception State
Renewal / Expansion
Reduction / Device Change
Offboarding Planned
Removal In Progress
Decommissioned
Archived
```

## Device lifecycle states

```text
Planned
Ordered / Allocated
Staged
Install Scheduled
Installed
DCA Discovered
Operational
Monitored
Needs Service
Supply Risk
Under Review
Replacement Candidate
Removal Scheduled
Removed
Decommissioned
Returned / Disposed / Reassigned
Archived
```

## DCA collector lifecycle states

```text
Required
Install Scheduled
Installed
Connected
Discovering Devices
Healthy
Offline
Needs Update
Misconfigured
Removal Scheduled
Removed
Deauthorized
Archived
```

## Gate examples

### Installation to operational

A customer must not become `Operational / Active Service` until all required checks are complete or explicitly deferred:

- planned devices are installed or deferred;
- required DCA collector is installed and reporting;
- devices are discovered and mapped;
- devices are mapped to contract terms;
- initial meter baselines are captured;
- supply monitoring is enabled;
- service coverage and SLA are confirmed;
- customer contacts are confirmed;
- installation tickets are closed or deferred.

### Operational to offboarding

A customer may enter `Offboarding Planned` when the relationship, contract, or device reduction path is known and open service, supply, billing, ownership, and removal implications have been reviewed.

### Removal to archived

A customer must not become `Archived` until device disposition, final reads, final billing, collector deauthorization/removal, integration/token revocation, open workflow closure, supply-shipment handling, portal access, retention policy, and audit package requirements are resolved.

## Agentic behavior rule

Lifecycle transitions are not passive field updates. Any transition that affects service, supplies, billing, retention, customer communication, or ownership must be backed by evidence, policy checks, retained-human approval where required, and traceable work results.
