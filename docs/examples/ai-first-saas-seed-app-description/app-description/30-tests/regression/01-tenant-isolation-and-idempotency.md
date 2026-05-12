# Regression Tests: Tenant Isolation and Idempotency

- same object id in different tenants never crosses data boundaries
- user with membership in tenant A cannot read/write tenant B data
- repeated invitation acceptance is safe and does not duplicate active memberships
- repeated approval command on an already decided card is rejected or no-op according to command semantics
- repeated workflow resume after a resolved exception does not duplicate side effects
- view queries remain tenant scoped after projection updates
