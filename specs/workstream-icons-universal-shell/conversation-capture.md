# Conversation Capture: Workstream Icons and My Account Dashboard

## My Account dashboard

The My Account workstream is opened by clicking the signed-in user located in the lower-left rail. It should include Profile and Settings surfaces, and its default dashboard should answer:

> What do I need to do next?

The dashboard should include:

- Profile and Settings shortcuts;
- a personal queue of cross-workstream items needing the user's action;
- compact workstream status panels for every accessible workstream;
- a single large number per workstream panel: `items needing your attention`;
- an icon/open affordance for each workstream panel;
- hover/focus tooltip or accessible label that reveals the full workstream name.

The My Account dashboard is a lightweight attention/navigation hub. Detailed item lists remain in each workstream's own dashboard.

## Workstream icons

Workstream icons should be part of the universal shell concept. Each workstream receives an icon selected or generated from its workstream name/domain responsibility. Icons are used for compact workstream buttons and status panels, while tooltips/accessibility labels preserve the full workstream name.

Examples:

- ERP Procurement: cart or purchase-order icon
- ERP Inventory: package/warehouse icon
- ERP Finance: invoice/currency icon
- CRM Sales Pipeline: rising chart icon
- CRM Customer Success: heart/health icon
- Field Service: wrench/truck icon
- Governance/Policy: shield/checklist icon
- Audit/Trace: timeline/search icon

## Process decision

Workstream icons should be treated as a core shell feature, minimally implemented in v0, then extended per domain workstream.

The real feature process should be:

1. Initial core app defines the icon metadata contract.
2. v0 starter implements simple usable icon behavior.
3. Each new domain workstream selects/finalizes its icon as part of initial workstream implementation.

## Initial proof test

Use the skills pack/starter to create a v0 app and verify icons appear in the left rail for:

- User Admin
- Agent Admin
- Audit/Trace
- Governance/Policy

My Account remains represented by the signed-in user tile, not a top-rail workstream button.
