# Boundary and Surface Rules

- public unauthenticated surfaces are limited to health/static frontend/auth bootstrap as explicitly configured
- all tenant APIs require authentication and tenant scope
- SSE/WebSocket subscriptions must enforce tenant and permission filters
- frontend route guards improve UX but do not replace backend checks
- CORS, cookies/tokens, and CSRF posture must match chosen auth mechanism before production deployment
- Email delivery is a backend-only Resend service boundary. Production sends through Resend (resend.com); local/dev/test uses captured outbox behavior. Resend keys, webhook secrets, raw invite tokens, and provider payloads never reach frontend assets, browser-safe APIs, prompts, agent output, or unredacted audit views.
- Agent email tools are governed `@FunctionTool` capability surfaces: they must enforce AuthContext, `ToolPermissionBoundary`, tenant/customer recipient scope, template allowlists, idempotency, approval/autonomy policy, and tool/work traces before queueing Resend email.
