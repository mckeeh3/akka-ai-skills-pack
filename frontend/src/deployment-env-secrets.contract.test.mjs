import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const docs = readFileSync(new URL('../../docs/deployment-env-secrets.md', import.meta.url), 'utf8');
const rootEnvExample = readFileSync(new URL('../../.env.example', import.meta.url), 'utf8');
const frontendEnvExample = readFileSync(new URL('../.env.example', import.meta.url), 'utf8');

const backendOnlyNames = [
  'WORKOS_API_KEY',
  'WORKOS_JWT_ISSUER',
  'WORKOS_JWT_AUDIENCE',
  'ADMIN_USERS',
  'APP_PUBLIC_BASE_URL',
  'RESEND_API_KEY',
  'RESEND_FROM_EMAIL',
  'INVITE_EMAIL_FROM',
  'OPENAI_API_KEY',
  'OPENAI_MODEL_ID',
  'OPENAI_API_BASE_URL'
];

const publicFrontendNames = ['VITE_WORKOS_CLIENT_ID', 'VITE_WORKOS_REDIRECT_URI'];

test('deployment env guide documents required backend and browser configuration boundaries', () => {
  for (const envName of [...backendOnlyNames, ...publicFrontendNames]) {
    assert.match(docs, new RegExp(`\\b${envName}\\b`), `${envName} should be documented`);
  }

  assert.match(docs, /Browser-public values[\s\S]*VITE_/);
  assert.match(docs, /Backend-only secrets[\s\S]*deployment secret manager/);
  assert.match(docs, /frontend\/.env\*/);
  assert.match(docs, /static assets/i);
});

test('frontend env example contains only browser-public WorkOS variables', () => {
  for (const envName of publicFrontendNames) {
    assert.match(frontendEnvExample, new RegExp(`^${envName}=`, 'm'), `${envName} should be public frontend config`);
  }

  for (const envName of backendOnlyNames) {
    assert.doesNotMatch(frontendEnvExample, new RegExp(`^${envName}=`, 'm'), `${envName} must not be assigned in frontend env`);
  }
});

test('root env example keeps backend secrets out of the frontend-only template and documents public Vite values separately', () => {
  for (const envName of backendOnlyNames) {
    assert.match(rootEnvExample, new RegExp(`^${envName}=`, 'm'), `${envName} should be available in root backend template`);
  }

  for (const envName of publicFrontendNames) {
    assert.match(rootEnvExample, new RegExp(`^${envName}=`, 'm'), `${envName} should be documented as public build-time config`);
  }

  assert.match(rootEnvExample, /Only VITE_ variables are browser-public/);
});

test('deployment env guide captures fail-closed provider behavior and ADMIN_USERS owner bootstrap caveat', () => {
  assert.match(docs, /SAAS_OWNER_ADMIN:OWNER/);
  assert.match(docs, /Tenant\/customer admins are created later through governed organization/i);
  assert.match(docs, /env -u ADMIN_USERS mvn test/);
  assert.match(docs, /Production delivery fails closed/i);
  assert.match(docs, /Model invocation fails closed/i);
  assert.match(docs, /Protected `@JWT` APIs deny/i);
});
