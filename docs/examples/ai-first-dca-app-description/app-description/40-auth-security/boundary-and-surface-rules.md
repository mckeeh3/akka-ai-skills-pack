# Boundary and Surface Rules

## Route boundaries

Public routes:

```text
/              frontend app entry
/assets/**     generated React/Vite assets
/favicon.ico   generated/static browser asset if present
```

Protected routes:

```text
/api/...
```

Rules:

- Static frontend assets may be public.
- Backend API routes require bearer JWT unless explicitly documented otherwise.
- Browser API calls should use same-origin relative `/api/...` URLs when Akka hosts the frontend.
- Avoid overlapping wildcard static routes. Prefer explicit app entry routes, hash/internal navigation, or documented non-wildcard routes.

## Frontend UX boundaries

- Role-aware navigation is a convenience, not an authorization layer.
- Unauthorized, forbidden, disabled-account, loading, empty, stale, and error states are required UI states.
- Decision cards must show policy/evidence/risk/trace context before approve/reject/suppress actions.
- Users must be able to understand whether they are acting as themselves or, if a future impersonation feature is approved, as an effective user.

## Backend enforcement boundaries

- Endpoint methods must call a central authorization helper or component boundary for role/scope checks.
- List/query endpoints must filter by tenant/customer scope server-side.
- Mutation endpoints must validate target scope and allowed role transitions server-side.
- Admin, policy, and high-impact AI-first decisions must write audit/work/decision trace facts.
- Frontend UI state, cached `/api/me` responses, and hidden buttons must never be treated as proof of authority by backend handlers.

## Integration boundaries

- WorkOS authenticates; Akka authorizes.
- Email invite delivery is an adapter boundary; seed behavior must still be testable without real email delivery.
- External DCA, ERP, billing, supplier, and service integrations remain deferred for the seed foundation.
- Agent tools must be narrower than broad administrative APIs whenever possible.

## PoC adaptation note

The PoC's frontend/backend integration guide is the reference for React/Vite build output served by Akka and same-origin API calls. The DCA seed app should adapt that structure while preserving the selected DCA UI style guide and AI-first supervision surfaces.
