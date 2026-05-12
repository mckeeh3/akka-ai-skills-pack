# Boundary and Surface Rules

- public unauthenticated surfaces are limited to health/static frontend/auth bootstrap as explicitly configured
- all tenant APIs require authentication and tenant scope
- SSE/WebSocket subscriptions must enforce tenant and permission filters
- frontend route guards improve UX but do not replace backend checks
- CORS, cookies/tokens, and CSRF posture must match chosen auth mechanism before production deployment
