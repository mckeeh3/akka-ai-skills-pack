---
name: akka-grpc-proto-design
description: Design Akka Java SDK gRPC protobuf contracts for service evolution, common protobuf types, and external proto reuse. Use when the main task is .proto structure rather than Java endpoint logic.
---

# Akka gRPC Proto Design

Use this skill when the main work is in `.proto` files.

## Required reading

Read these first if present:
- `akka-context/sdk/grpc-endpoints.html.md`
- `../../../src/main/proto/com/example/api/grpc/shopping_cart_grpc_endpoint.proto`
- `../../../src/main/proto/com/example/api/grpc/internal_status_grpc_endpoint.proto`
- `../../../pom.xml`

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

## Repository examples

- `shopping_cart_grpc_endpoint.proto`
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
