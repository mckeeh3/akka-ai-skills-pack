/**
 * Application-layer Akka components.
 *
 * <p>Classes in this package are AI-reference examples for event sourced entities, key value
 * entities, workflows, consumers, timed actions, and views.
 *
 * <p>Keep components thin:
 *
 * <ul>
 *   <li>validate inputs and choose Akka effects here</li>
 *   <li>delegate business decisions to the domain layer</li>
 *   <li>for event sourced entities, persist events and keep {@code applyEvent} pure</li>
 *   <li>for key value entities, compute a new full state and use {@code updateState}</li>
 *   <li>for timed actions, keep handlers stateless and normalize obsolete timers to successful outcomes</li>
 *   <li>for views, keep query rows explicit and source-specific via {@code TableUpdater}</li>
 *   <li>cover view sources explicitly: event sourced entities, key value entities, workflows, and topics</li>
 *   <li>prefer focused view examples for streaming queries, delete handlers, and snapshot handlers</li>
 * </ul>
 */
package com.example.application;
