package com.example.application.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class DcaSeedFrontendEndpointIntegrationTest extends TestKitSupport {

  @Test
  void servesAuthenticatedReactShellAndBuiltAssets() {
    var page = httpClient.GET("/").responseBodyAs(String.class).invoke();

    assertTrue(page.status().isSuccess());
    assertTrue(page.body().contains("AI-first DCA Seed Console"));
    assertTrue(page.body().contains("/assets/"));

    var jsPath = firstMatch(page.body(), "src=\"([^\"]+\\.js)\"");
    var cssPath = firstMatch(page.body(), "href=\"([^\"]+\\.css)\"");

    var js = httpClient.GET(jsPath).responseBodyAs(String.class).invoke();
    assertTrue(js.status().isSuccess());
    assertTrue(js.body().contains("/api/me"));
    assertTrue(js.body().contains("Authorization"));
    assertTrue(js.body().contains("Backend APIs still enforce authorization"));
    assertTrue(js.body().contains("/ui/supplies"));

    var css = httpClient.GET(cssPath).responseBodyAs(String.class).invoke();
    assertTrue(css.status().isSuccess());
    assertTrue(css.body().contains("--color-primary:#2563eb") || css.body().contains("--color-primary: #2563eb"));
    assertTrue(css.body().contains("prefers-color-scheme:dark") || css.body().contains("prefers-color-scheme: dark"));
    assertTrue(css.body().contains(":focus-visible"));
  }

  @Test
  void frontendBuildDoesNotExposeBackendSecrets() {
    var page = httpClient.GET("/").responseBodyAs(String.class).invoke();
    var jsPath = firstMatch(page.body(), "src=\"([^\"]+\\.js)\"");
    var js = httpClient.GET(jsPath).responseBodyAs(String.class).invoke();
    var content = page.body() + "\n" + js.body();

    assertFalse(content.contains("WORKOS_API_KEY"));
    assertFalse(content.contains("RESEND_API_KEY"));
    assertFalse(content.contains("ADMIN_USERS="));
    assertFalse(content.contains("INVITE_EMAIL_FROM"));
    assertFalse(content.contains("sk_test_"));
    assertFalse(content.contains("sk_live_"));
    assertFalse(content.contains("re_x"));
  }

  @Test
  void faviconRouteIsPublicAndApiRouteRemainsSeparate() {
    var favicon = httpClient.GET("/favicon.ico").invoke();
    assertTrue(favicon.status().isSuccess());

    var apiFailure = org.junit.jupiter.api.Assertions.assertThrows(
        RuntimeException.class,
        () -> httpClient.GET("/api/me").responseBodyAs(String.class).invoke());
    assertTrue(
        apiFailure.getMessage().contains("400")
            || apiFailure.getMessage().contains("401")
            || apiFailure.getMessage().contains("403"),
        apiFailure.getMessage());
  }

  private static String firstMatch(String body, String regex) {
    var matcher = Pattern.compile(regex).matcher(body);
    assertTrue(matcher.find(), "Expected match for " + regex + " in " + body);
    return matcher.group(1);
  }
}
