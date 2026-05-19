# Sprint 4: User Admin Reference Vertical

## Goal

Implement one canonical reference vertical that proves classic SaaS UI flows work as structured surfaces in a functional-agent workstream.

## Scope

- Implement User Admin functional agent fixture configuration.
- Implement User Admin dashboard surface.
- Implement User List/search surface.
- Implement User Detail/edit surface with permission-aware fields/actions.
- Ensure click/navigation actions append workstream feedback text such as `Display the user list view` and `Display user account ...`.
- Support composer commands such as `show users` through fixture command handling.
- Demonstrate scrolling back to a previous list surface and choosing another user.
- Link actions to governed capability ids and show audit/trace affordances.

## Out of scope

- Real identity provider integration.
- Complete user administration backend implementation.

## Done criteria

- The reference vertical shows dashboard → list/search → detail/edit using surfaces rather than pages.
- Non-chat actions and chat commands converge on the same surface/capability model.
