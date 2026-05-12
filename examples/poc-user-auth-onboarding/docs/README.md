# Template of empty project

To understand the Akka concepts that are the basis for this example, see [Development Process](https://doc.akka.io/concepts/development-process.html) in the documentation.

This project contains the skeleton to create an Akka service. To understand more about these components, see [Developing services](https://doc.akka.io/sdk/index.html).

You are supposed to change `empty-service` and the package name `com.example` to your own names.

Use Maven to build your project:

```shell
mvn compile
```

## Bootstrap admin users

On startup, the service can create initial invited admin users from the `ADMIN_USERS` environment variable and send invite emails with Resend.

Format:

```text
ADMIN_USERS="email:ROLE:scope,email:ROLE:scope"
```

Examples:

```shell
export ADMIN_USERS="jane@gmail.com:ADMIN:ALL,joe@outlook.com:TENANT_ADMIN:tenant-123"
```

Supported roles/scopes:

```text
ADMIN:ALL                         # alias for APP_ADMIN across all tenants
APP_ADMIN:ALL                     # app admin across all tenants
TENANT_ADMIN:<tenant-id>          # tenant admin for one tenant
CUSTOMER_ADMIN:<tenant-id>/<customer-id>
```

Required for email delivery and first-login account activation:

```shell
export RESEND_API_KEY="re_xxxxxxxxx"
export INVITE_EMAIL_FROM="Acme <onboarding@example.com>"
export INVITE_EMAIL_SUBJECT="Account access information"
export APP_BASE_URL="http://localhost:9000"
export WORKOS_API_KEY="sk_test_or_sk_live_xxxxxxxxx"
```

Behavior:

- startup reads `ADMIN_USERS`
- each email is normalized to lowercase and used as the initial local Akka `userId`
- if the user already exists, startup skips that entry and does not send another email
- if the user does not exist, startup creates an invited `UserAccount` with the configured role/scope
- startup sends a standard invite email that does not mention elevated privileges
- when the invited user signs in with WorkOS using the same email, Akka activates the local account and links the WorkOS subject
- `WORKOS_API_KEY` is used server-side to look up the signed-in WorkOS user's verified email when the JWT access token does not include an `email` claim

For local testing:

```shell
export ADMIN_USERS="you@example.com:ADMIN:ALL"
export RESEND_API_KEY="re_xxxxxxxxx"
export INVITE_EMAIL_FROM="Akka Secure App <onboarding@resend.dev>"
export INVITE_EMAIL_SUBJECT="Account access information"
export APP_BASE_URL="http://localhost:9000"
export WORKOS_API_KEY="sk_test_or_sk_live_xxxxxxxxx"
mvn compile exec:java
```

## WorkOS local redirect configuration

For local Akka-hosted frontend testing, configure WorkOS with this exact Redirect URI:

```text
http://localhost:9000
```

Also add this origin in WorkOS allowed origins if required by your AuthKit environment:

```text
http://localhost:9000
```

Build the frontend with the same redirect URI embedded:

```shell
cd frontend
cp .env.example .env.local
# set VITE_WORKOS_CLIENT_ID in .env.local
npm install
npm run build
```

Then run Akka and browse to `http://localhost:9000`, not `http://127.0.0.1:9000` and not the Vite dev server, unless those exact URLs are also registered in WorkOS.

To start your service locally, run:

```shell
mvn compile exec:java
```

You can use the [Akka Console](https://console.akka.io) to create a project and see the status of your service.

Build container image:

```shell
mvn clean install -DskipTests
```

Install the `akka` CLI as documented in [Install Akka CLI](https://doc.akka.io/reference/cli/index.html).

Deploy the service using the image tag from above `mvn install`:

```shell
akka service deploy empty-service empty-service:tag-name --push
```

Refer to [Deploy and manage services](https://doc.akka.io/operations/services/deploy-service.html) for more information.
