# TASK-FSFR-09-001: Repair attention producer failure

## Purpose

Fix any remaining attention producer failure after governance lifecycle repairs.

## Required reads

- `AGENTS.md`
- `specs/full-suite-failure-remediation/README.md`
- `specs/full-suite-failure-remediation/failure-inventory.md`
- AttentionProducerService source/test files named by inventory
- Governance/Policy files if named by inventory

## Skills

- `ai-first-saas-ui-surfaces`
- `ai-first-saas-decision-cards`
- `capability-first-backend`

## Expected outputs

- attention producer implementation/test/current-intent repair
- queue update

## Required checks

- `git diff --check`
- targeted AttentionProducerService test
- governance attention tests if needed

## Done criteria

- Governance submit attention is produced/resolved without leaking unauthorized or cross-tenant data.
- Changes and queue update are committed.
