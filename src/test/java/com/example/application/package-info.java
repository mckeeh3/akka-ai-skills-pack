/**
 * Tests for the application layer of the service.
 *
 * <p>This package contains AI-reference examples for:
 *
 * <ul>
 *   <li>event sourced entity unit tests with {@code EventSourcedTestKit}</li>
 *   <li>key value entity unit tests with {@code KeyValueEntityTestKit}</li>
 *   <li>integration tests with {@code TestKitSupport}</li>
 *   <li>HTTP endpoint integration tests using {@code httpClient}</li>
 *   <li>low-level HTTP endpoint tests for raw request and response handling</li>
 *   <li>HTTP client provider endpoint tests for delegated route calls</li>
 *   <li>SSE endpoint tests using {@code SseRouteTester}</li>
 *   <li>WebSocket endpoint tests using {@code WebSocketRouteTester}</li>
 *   <li>view-backed SSE endpoint tests with mocked incoming messages and {@code Awaitility}</li>
 *   <li>JWT-protected endpoint tests with injected bearer tokens</li>
 *   <li>internal-only ACL endpoint tests with impersonated service callers</li>
 *   <li>view integration tests with mocked incoming messages and {@code Awaitility}</li>
 *   <li>end-to-end flows driven through endpoints and consumers</li>
 * </ul>
 */
package com.example.application;
