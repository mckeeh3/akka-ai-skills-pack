package ai.first.architecture;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class JavaPackageBoundaryTest {
  private static final Path MAIN_JAVA = Path.of("src/main/java");
  private static final Pattern FOUNDATION_OR_COREAPP_PACKAGE = Pattern.compile(
      "(?m)^package ai\\.first\\.(api|application|domain)\\.(foundation|coreapp)(\\.|;)");
  private static final Pattern BUSINESS_IMPORT = Pattern.compile(
      "(?m)^import ai\\.first\\.(api|application|domain)\\.business\\.");
  private static final Pattern FOUNDATION_API_OR_DOMAIN_PACKAGE = Pattern.compile(
      "(?m)^package ai\\.first\\.(api|domain)\\.foundation(\\.|;)");
  private static final Pattern COREAPP_IMPORT = Pattern.compile(
      "(?m)^import ai\\.first\\.(api|application|domain)\\.coreapp\\.");
  private static final Pattern PACKAGE_DECLARATION = Pattern.compile("(?m)^package ([^;]+);");
  private static final Pattern BUSINESS_ROOT_PACKAGE = Pattern.compile(
      "^ai\\.first\\.(api|application|domain)\\.business$");
  private static final Pattern BUSINESS_AREA_PACKAGE = Pattern.compile(
      "^ai\\.first\\.(api|application|domain)\\.business\\.[a-z][a-z0-9]*(\\..*)?$");

  @Test
  void foundationAndCoreAppDoNotImportBusinessPackages() throws IOException {
    var violations = new ArrayList<String>();

    for (var source : productionJavaSources()) {
      var text = Files.readString(source);
      if (FOUNDATION_OR_COREAPP_PACKAGE.matcher(text).find() && BUSINESS_IMPORT.matcher(text).find()) {
        violations.add(source.toString());
      }
    }

    assertTrue(violations.isEmpty(), "foundation/coreapp packages must not import business packages: " + violations);
  }

  @Test
  void foundationApiAndDomainDoNotImportCoreAppPackages() throws IOException {
    var violations = new ArrayList<String>();

    for (var source : productionJavaSources()) {
      var text = Files.readString(source);
      if (FOUNDATION_API_OR_DOMAIN_PACKAGE.matcher(text).find() && COREAPP_IMPORT.matcher(text).find()) {
        violations.add(source.toString());
      }
    }

    assertTrue(violations.isEmpty(), "foundation api/domain packages must not import coreapp packages: " + violations);
  }

  @Test
  void businessPackagesUseNamedAreaBelowBusinessRoot() throws IOException {
    var violations = new ArrayList<String>();

    for (var source : productionJavaSources()) {
      var text = Files.readString(source);
      var packageMatcher = PACKAGE_DECLARATION.matcher(text);
      if (!packageMatcher.find()) {
        continue;
      }
      var packageName = packageMatcher.group(1);
      if (!packageName.contains(".business")) {
        continue;
      }
      if (BUSINESS_ROOT_PACKAGE.matcher(packageName).matches() && !source.endsWith(Path.of("business/package-info.java"))) {
        violations.add(source + " uses the documentation-only business root package");
      } else if (!BUSINESS_ROOT_PACKAGE.matcher(packageName).matches()
          && !BUSINESS_AREA_PACKAGE.matcher(packageName).matches()) {
        violations.add(source + " must use ai.first.<layer>.business.<area>");
      }
    }

    assertTrue(violations.isEmpty(), "business packages must include an area segment: " + violations);
  }

  private static List<Path> productionJavaSources() throws IOException {
    try (var stream = Files.walk(MAIN_JAVA)) {
      return stream
          .filter(path -> path.toString().endsWith(".java"))
          .sorted()
          .toList();
    }
  }
}
