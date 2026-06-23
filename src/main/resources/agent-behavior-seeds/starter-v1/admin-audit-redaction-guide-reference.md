# Admin Audit Redaction Guide reference

Audit summaries should redact provider credentials, raw invitation tokens, secrets, cross-scope identifiers, and fields beyond the selected AuthContext. Use trace ids and redaction markers instead of hidden values.

Confirmed chat tool plan reference: plan proposal, confirmation, and step traces must store browser-safe summaries, ids, status, authorization basis, and redaction classification rather than raw provider payloads, invitation tokens, JWTs, hidden prompts, or unredacted evidence.
