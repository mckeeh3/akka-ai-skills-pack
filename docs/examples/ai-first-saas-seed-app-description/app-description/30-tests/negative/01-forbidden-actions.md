# Negative Tests: Forbidden Actions

- unauthenticated browser API request is rejected
- authenticated user without active membership is rejected
- member without admin role cannot invite users or assign elevated roles
- agent cannot execute a tool without permission grant
- agent cannot activate policy changes directly
- reviewer without approval permission cannot decide a decision card
- suspended tenant rejects normal user actions
