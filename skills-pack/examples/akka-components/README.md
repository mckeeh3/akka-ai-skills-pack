# Core app Akka component examples

This directory contains a source snapshot of the current runnable core app Java examples from the repository root. The examples are copied here so installed skills can inspect concrete, working Akka Java SDK patterns without treating the target app's `src/**` directory as a skills-library reference path.

These files are read-only pattern references, not a second app baseline and not an independent build module. Do not copy this tree wholesale into a target app or treat it as runnable generated output; use individual files only to inspect concrete Akka Java SDK patterns. Keep them synchronized with the real core app implementation when pack guidance needs concrete code examples.

Included content:

- `src/main/java/ai/first/**` — current core app API, application, and domain examples
- `src/test/java/ai/first/**` — current core app tests
- `src/main/resources/application.conf` — current core app backend configuration example

Do not restore retired mechanics-only domain fixtures in this directory. New examples should come from the actual core app implementation or from narrowly scoped domain-specific follow-up work.
