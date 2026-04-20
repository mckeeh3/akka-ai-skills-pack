/**
 * Application-layer Akka components.
 *
 * <p>Classes in this package are AI-reference examples for both event sourced entities and key
 * value entities.
 *
 * <p>Keep components thin:
 *
 * <ul>
 *   <li>validate inputs and choose Akka effects here</li>
 *   <li>delegate business decisions to the domain layer</li>
 *   <li>for event sourced entities, persist events and keep {@code applyEvent} pure</li>
 *   <li>for key value entities, compute a new full state and use {@code updateState}</li>
 * </ul>
 */
package com.example.application;
