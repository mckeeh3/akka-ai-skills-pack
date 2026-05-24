# Conversation Capture: Production-Ready Five Core v0

After completing the five-core-v0 starter migration, review found that the scaffold validates but still has two unacceptable gaps for a real trial:

1. The workstream message endpoint returns deterministic backend-authored markdown instead of invoking a real AI model.
2. The skills pack has too much tolerance for "kinda/sorta" implementation, mocks, simulations, fixture-only behavior, and deferred production paths.

The user clarified that the AI model must be real because this forces the full workstream-agent implementation path. The skills pack attitude must be: aggressively drive toward production-ready. Akka local runtime is production-like, so the app should implement the real thing locally instead of holding back with mock/simulated behavior.

This queue captures the work needed to make the five-core v0 app production-ready for a real local trial.
