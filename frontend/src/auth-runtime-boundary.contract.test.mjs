import assert from 'node:assert/strict';
import { readFileSync, existsSync } from 'node:fs';
import test from 'node:test';

const main = readFileSync(new URL('./main.tsx', import.meta.url), 'utf8');
const httpWorkstreamApi = readFileSync(new URL('./api/HttpWorkstreamApiClient.ts', import.meta.url), 'utf8');

const forbiddenFrontendSecretNames = [
  'WORKOS_API_KEY',
  'WORKOS_JWT_ISSUER',
  'WORKOS_JWT_AUDIENCE',
  'ADMIN_USERS',
  'RESEND_API_KEY',
  'INVITE_EMAIL_FROM'
];

test('WorkOS/AuthKit frontend fails closed until the public client id is configured', () => {
  assert.match(main, /VITE_WORKOS_CLIENT_ID/);
  assert.match(main, /hasConfiguredWorkosClient/);
  assert.match(main, /Configure WorkOS\/AuthKit/);
  assert.match(main, /normal frontend runtime does not provide a fixture mode/i);
  assert.match(main, /AuthKitProvider clientId=\{workosClientId\} redirectUri=\{workosRedirectUri\}/);
});

test('frontend WorkOS/AuthKit boundary uses only public Vite configuration and bearer-token API calls', () => {
  assert.match(main, /VITE_WORKOS_REDIRECT_URI/);
  assert.match(main, /window\.location\.origin/);
  assert.match(httpWorkstreamApi, /headers\.set\('Authorization', `Bearer \$\{token\}`\)/);
  assert.match(httpWorkstreamApi, /headers\.set\('X-Selected-Context-Id', this\.selectedContextId\)/);
});

test('frontend source does not reference backend-only provider secrets', () => {
  const frontendRuntimeSources = [main, httpWorkstreamApi];
  for (const source of frontendRuntimeSources) {
    for (const secretName of forbiddenFrontendSecretNames) {
      assert.doesNotMatch(source, new RegExp(secretName), `${secretName} must stay backend-only`);
    }
  }
});

test('normal frontend entrypoint does not import fixture clients or fixture data', () => {
  assert.doesNotMatch(main, /from ['"].*__tests__\/fixtures/);
  assert.doesNotMatch(main, /import .*FixtureWorkstreamApiClient|import .*FixtureApiClient/);
  assert.ok(existsSync(new URL('./__tests__/fixtures/api/FixtureWorkstreamApiClient.ts', import.meta.url)), 'fixture clients remain available only under test assets');
});
