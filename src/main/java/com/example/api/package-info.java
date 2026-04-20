/**
 * Public API layer of the service.
 *
 * <p>Endpoint classes in this package are AI-reference examples showing how to:
 *
 * <ul>
 *   <li>map API requests to entity commands</li>
 *   <li>translate entity validation failures to HTTP responses</li>
 *   <li>use request context for query parameters, request headers, principals, and JWT claims</li>
 *   <li>serve packaged static HTML, static subtrees, and OpenAPI files from endpoint routes</li>
 *   <li>use low-level HTTP request and response APIs when higher-level helpers are not enough</li>
 *   <li>delegate to other HTTP services through {@code HttpClientProvider}</li>
 *   <li>stream SSE responses with explicit ids, reconnect behavior, and view-backed updates</li>
 *   <li>expose bidirectional WebSocket flows</li>
 *   <li>apply internal-only ACLs with method-level overrides where needed</li>
 *   <li>implement gRPC endpoint classes that map protobuf contracts to component calls</li>
 *   <li>use {@code AbstractGrpcEndpoint} for principals, metadata, and JWT-aware request context</li>
 *   <li>secure gRPC endpoints with {@code @JWT} bearer-token validation and claim-aware replies</li>
 *   <li>stream protobuf replies over gRPC from view-backed sources</li>
 *   <li>avoid exposing internal domain state directly as public API types</li>
 *   <li>expose notification streams as SSE with API-specific response records</li>
 * </ul>
 */
package com.example.api;
