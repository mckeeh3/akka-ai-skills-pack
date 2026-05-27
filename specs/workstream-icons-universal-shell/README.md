# Workstream Icons Universal Shell Migration

## Purpose

Make workstream icons a first-class universal shell feature for generated AI-first SaaS workstream applications.

The initial proof target is a generated v0 starter app where the left rail shows icons for these core workstreams:

- User Admin
- Agent Admin
- Audit/Trace
- Governance/Policy

My Account remains launched from the signed-in user tile in the lower-left rail and uses account/avatar presentation rather than being listed among the top rail workstream buttons.

## Target architecture

Workstream icons are essential universal shell visual affordances, even though they are cosmetic rather than authorization-bearing. They are not frontend letter initials. They are workstream metadata exposed through the shell/bootstrap contract and rendered through an approved SVG/icon-library registry or semantic SVG fallback anywhere the universal shell links to workstreams:

- left rail launchers;
- My Account dashboard workstream status panels;
- workstream switchers or command palettes;
- surface actions that open another workstream;
- tooltips, accessible labels, and trace/debug displays.

Each visible workstream should have stable icon metadata:

```ts
type WorkstreamIconDescriptor = {
  workstreamId: string;
  displayName: string;
  iconId: string;
  visualHint: string;
  accentColorToken: string;
  tooltip: string;
  ariaLabel: string;
  assetRef?: string;
};
```

## Delivery strategy

Implement in layers:

1. **Initial core app / v0 contract** — require and seed icon metadata for the core workstreams.
2. **Starter/template code** — expose icon descriptors in `/api/me`/fixture/bootstrap data and render them in the left rail.
3. **Domain workstream additions** — require each new domain workstream to choose or generate an icon descriptor when the workstream is first defined, and render it through the shared SVG registry/semantic fallback.
4. **Proof** — scaffold/use the v0 starter and verify User Admin, Agent Admin, Audit/Trace, and Governance/Policy appear in the rail with icons and accessible names/tooltips.

## Proof command

Run the deterministic scaffold proof from the skills-pack source repository:

```bash
tools/prove-workstream-icons-v0.sh
```

Expected result:

- the script scaffolds `templates/ai-first-saas-starter` into a temporary target;
- User Admin, Agent Admin, Audit/Trace, and Governance/Policy each have `WorkstreamIconDescriptor` metadata and rendered rail icon affordance wiring;
- the rendered rail item code uses descriptor-backed icon data attributes, tooltip markup, and accessible labels;
- My Account remains available from the lower-left signed-in user tile and is filtered out of the normal top rail.

Use `--keep` to retain the scaffold target for inspection.

## Non-goals

- Do not build a complex image-generation pipeline for icons in v0.
+- Do not render normal workstream icons as letters or initials; unknown workstreams use the generic/derived SVG registry fallback.
- Do not make icons the authorization mechanism; backend visibility/capability checks remain authoritative.
- Do not move My Account into the normal top rail.
- Do not allow icon color alone to communicate status or authority.
