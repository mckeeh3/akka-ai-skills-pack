/**
 * Pure domain code for the service.
 *
 * <p>This package contains AI-reference examples for both event sourced entities and key value
 * entities.
 *
 * <p>Use these patterns:
 *
 * <ul>
 *   <li>immutable state records</li>
 *   <li>commands grouped under descriptive domain types</li>
 *   <li>validators returning deterministic error lists</li>
 *   <li>event sourced helpers returning events</li>
 *   <li>key value helpers returning updated state or no-op decisions</li>
 * </ul>
 *
 * <p>Domain code must not return Akka effects.
 */
package com.example.domain;
