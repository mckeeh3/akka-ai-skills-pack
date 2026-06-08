# Sprint 9: AI-First Packaging and Terminology Finalization

## Sprint goal

Close the remaining high-level AI-first migration hygiene items before deeper Akka component-skill revisions. This sprint focuses on canonical doctrine freshness, installed-pack coherence, release artifact/version alignment, and consistent use of **AI-first** terminology.

## Dependencies

- Sprints 1 through 8 complete.
- AI-first routing, doctrine, app-description, planning, packaging, and executable reference slice work are already present.

## Scope

This sprint is limited to high-level pack consistency. Do not perform broad Akka component guidance rewrites here.

Included work:

1. Update stale canonical doctrine language that still treats AI-first companion skills as future/planned.
2. Remove or rewrite installed-pack-facing references to source-only archive/provenance paths.
3. Package all docs referenced by installed skills.
4. Regenerate or update release/dist metadata and bump pack version if appropriate.
5. Rename active files/directories that still use legacy terminology to `ai-first`.
6. Scan active repository content for remaining legacy wording and change it to `ai-first` where it is not required historical provenance.

## Acceptance behavior

- The canonical doctrine points to existing AI-first skills, not future planned skills.
- Installed-pack docs do not instruct users to read source-only migration archive paths.
- Installed skills do not reference docs omitted by the installer.
- Pack version/release artifacts reflect the post-migration state or clearly document regeneration requirements.
- Active docs/examples consistently use `ai-first`; historical archive/provenance content may retain original wording only when intentionally preserved.

## Done criteria

- All Sprint 9 pending tasks are `done` or explicitly superseded/deferred.
- Each task has a git commit, per queue rules.
- No broad component-skill rewrites are performed beyond terminology/path/doc packaging consistency.
