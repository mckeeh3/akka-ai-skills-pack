# Policies: Governance/Policy

## Uses

Global policies: `../../../../../global/policies/foundation-security-and-governance.md`.

## Binding

Applies backend-authorization-default-deny, tenant-customer-isolation, redaction-and-export-governance, governed-agent-authority, audit/history retention, idempotent-write safety, and non-overridable platform security.

## Business-governance override policy

SaaS owner defaults define baseline business-governance values. Tenant admins may override business-governance settings for authorized tenant scopes. Tenant overrides win over SaaS defaults. More specific override scopes win over less-specific scopes.

The following controls are hard platform controls and are not overrideable through Governance/Policy: tenant isolation, backend authorization, secret/JWT/provider-key protection, raw prompt/model/provider payload protection, redaction boundaries, audit trace integrity, and platform integrity checks.

Policy evaluation is backend-enforced for protected reads, writes, agent/tool calls, effective-policy calculations, runtime policy decisions, redaction, and frontend-visible payloads.
