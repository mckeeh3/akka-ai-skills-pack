import { existsSync, rmSync } from 'node:fs';

const staticDir = new URL('../../src/main/resources/static-resources/', import.meta.url).pathname;
const assetsDir = new URL('../../src/main/resources/static-resources/assets/', import.meta.url).pathname;
const indexHtml = new URL('../../src/main/resources/static-resources/index.html', import.meta.url).pathname;

if (existsSync(assetsDir)) rmSync(assetsDir, { recursive: true, force: true });
if (existsSync(indexHtml)) rmSync(indexHtml, { force: true });
console.log(`[bundle-clean] Removed previous Vite assets from ${staticDir}; preserved non-Vite static files such as favicon.ico.`);
