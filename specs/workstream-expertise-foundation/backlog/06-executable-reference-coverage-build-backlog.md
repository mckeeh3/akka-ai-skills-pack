# Backlog 06: Executable Reference-Governance Coverage

## Outcome

The starter backend proves the workstream expertise reference path in code and tests rather than only in docs/fixtures.

## Backlog items

1. **Add reference domain state and seed import**
   - Add `ReferenceDocument` and `AgentReferenceManifest` records or a clearly named interim equivalent.
   - Extend `AgentDefinition` and repository state with reference manifest identity/version.
   - Import User Admin reference resources as governed records with provenance and idempotent behavior.

2. **Implement runtime reference manifest assembly and loader**
   - Render separate compact skill and reference manifest sections.
   - Implement `readReferenceDoc(referenceId)` with safe denials and trace emission.
   - Enforce separate `READ_REFERENCE` grants.

3. **Add backend tests for reference governance**
   - Cover assigned reference success, unassigned/inactive/wrong-scope denials where feasible, missing boundary grant, disabled agent denial, compact-manifest-only behavior, and trace facts.

4. **Update coverage docs and validation hooks**
   - Update `docs/agent-coverage-matrix.md` and starter validation references after executable coverage exists.

5. **Sprint review**
   - Confirm the starter now demonstrates the first-class reference-governance path or record remaining implementation gaps.
