import { existsSync, readdirSync, statSync } from 'node:fs';
import { join, relative } from 'node:path';

const staticDir = new URL('../../src/main/resources/static-resources/', import.meta.url).pathname;
const warningBytes = 750 * 1024;

if (!existsSync(staticDir)) {
  console.error(`[bundle-size][error] Static resources not found: ${staticDir}`);
  console.error('[bundle-size][hint] Run `npm run build` before `npm run analyze:bundle`.');
  process.exit(1);
}

const files = collectFiles(staticDir).filter((file) => /\.(js|css|html)$/.test(file));
if (files.length === 0) {
  console.error(`[bundle-size][error] No JS/CSS/HTML assets found under ${staticDir}`);
  process.exit(1);
}

const rows = files
  .map((file) => ({ file, bytes: statSync(file).size }))
  .sort((a, b) => b.bytes - a.bytes);
const totalBytes = rows.reduce((sum, row) => sum + row.bytes, 0);

console.log(`[bundle-size] Static JS/CSS/HTML total: ${formatBytes(totalBytes)}`);
for (const row of rows.slice(0, 8)) {
  console.log(`[bundle-size] ${relative(staticDir, row.file)} ${formatBytes(row.bytes)}`);
}

if (totalBytes > warningBytes) {
  console.log(`[bundle-size] Non-blocking residual: total exceeds ${formatBytes(warningBytes)}. Vite may also warn for large chunks; keep tracking post-release unless this becomes operationally relevant.`);
} else {
  console.log(`[bundle-size] Within starter review threshold (${formatBytes(warningBytes)}).`);
}

function collectFiles(dir) {
  return readdirSync(dir).flatMap((entry) => {
    const path = join(dir, entry);
    const stat = statSync(path);
    return stat.isDirectory() ? collectFiles(path) : [path];
  });
}

function formatBytes(bytes) {
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KiB`;
  return `${(bytes / 1024 / 1024).toFixed(2)} MiB`;
}
