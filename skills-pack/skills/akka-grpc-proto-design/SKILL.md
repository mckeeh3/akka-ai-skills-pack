---
name: akka-grpc-proto-design
description: Design Akka Java SDK gRPC protobuf contracts for service evolution, common protobuf types, and external proto reuse. Use when the main task is .proto structure rather than Java endpoint logic.
---

# Akka gRPC Proto Design

Use this skill when the main work is in `.proto` files.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. For this skill, require the task/app-description/spec/backlog to name or explicitly defer the relevant functional agent/internal trigger, capability, AuthContext/scope, DTOs, side effects, audit/work traces, and tests before implementing generated SaaS runtime code. If those inputs are absent, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/grpc-endpoints.html.md`
- target project path: pom.xml

## Use this pattern when

- defining a new gRPC service contract in `src/main/proto`
- choosing message shapes for unary or streaming methods
- planning schema evolution without breaking wire compatibility
- using `google.protobuf` common message types
- consuming protobuf definitions from another service or library

## Core rules

1. Put service contracts in `src/main/proto`.
2. Set `option java_multiple_files = true`.
3. Set an explicit `option java_package = "..."`.
4. Treat field tag numbers as permanent wire identifiers.
5. Never reuse removed field numbers.
6. Prefer `reserved` for removed fields and `deprecated = true` for fields being phased out.
7. Use common protobuf types such as `google.protobuf.Timestamp` and `google.protobuf.StringValue` when they match the API.
8. Remember that renames can be wire compatible but are not source compatible.

## Generated SaaS protobuf contract rules

For generated SaaS APIs, design `.proto` methods as exposure surfaces for accepted capabilities, not as standalone RPC mechanics:
- include or derive tenant/customer scope and caller/service-principal context at the endpoint boundary;
- carry idempotency/correlation identifiers for commands, workflow starts, approvals, and retries;
- use response/status messages that can represent validation failure, authorization denial, approval-needed, stale/conflict, and safe redacted errors;
- include trace or audit reference fields when the API returns consequential decisions, evidence, or workflow state;
- keep redacted browser/agent-safe DTOs separate from internal component state;
- specify streaming resume, event id, ordering, and stale semantics for surface or workstream streams.


## Repository examples

- `workstream_event_grpc_endpoint.proto`
  - `google.protobuf.Timestamp`
  - `google.protobuf.StringValue`
  - `reserved` field number and field name
  - deprecated field example
- `internal_status_grpc_endpoint.proto`
  - `google.protobuf.Empty`
  - `google.protobuf.StringValue`
  - reserved field number and field name

## External protobuf types

### Common Google protobuf types
These are available by default for gRPC endpoint projects:
- `google.protobuf.Empty`
- `google.protobuf.Timestamp`
- `google.protobuf.StringValue`
- other `google.protobuf.*` common types

### Other external protobuf descriptors
If another service publishes `.proto` files in a Java artifact, unpack them before code generation. Example Maven shape:

```xml
<plugin>
  <artifactId>maven-dependency-plugin</artifactId>
  <executions>
    <execution>
      <id>unpack-additional-proto-dependencies</id>
      <phase>initialize</phase>
      <goals>
        <goal>unpack</goal>
      </goals>
      <configuration>
        <artifactItems>
          <artifactItem>
            <groupId>com.google.api.grpc</groupId>
            <artifactId>proto-google-common-protos</artifactId>
            <version>2.61.3</version>
            <type>jar</type>
            <outputDirectory>${project.build.directory}/proto</outputDirectory>
            <includes>**/*.proto</includes>
          </artifactItem>
        </artifactItems>
      </configuration>
    </execution>
  </executions>
</plugin>
```

## Review checklist

Before finishing, verify:
- `.proto` files are under `src/main/proto`
- `java_package` is explicit
- streaming methods use `returns (stream Reply)`
- removed fields are `reserved`
- deprecated fields are marked explicitly when kept on the wire
- common protobuf types are used intentionally rather than ad hoc strings
