import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import type { ComponentPropsWithoutRef, ReactNode } from 'react';
import type { MarkdownResponseData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type MarkdownResponseSurfaceProps = {
  envelope: SurfaceEnvelope<MarkdownResponseData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

const unsafeSchemePattern = /^(?:javascript|data|vbscript):/i;
const allowedAbsoluteSchemePattern = /^(?:https?:|mailto:)/i;

function sanitizeHref(rawHref: string) {
  const trimmed = rawHref.trim().replace(/[\u0000-\u001f\u007f\s]+/g, '');
  const decoded = trimmed.replace(/&colon;/gi, ':');
  if (!trimmed || unsafeSchemePattern.test(decoded)) {
    return undefined;
  }
  if (allowedAbsoluteSchemePattern.test(decoded) || decoded.startsWith('/') || decoded.startsWith('#')) {
    return trimmed;
  }
  return undefined;
}

function sanitizedLanguageClass(className?: string) {
  return className
    ?.split(/\s+/)
    .filter((candidate) => /^language-[\w-]+$/.test(candidate))
    .join(' ') || undefined;
}

function SafeLink({ href, children }: ComponentPropsWithoutRef<'a'>) {
  const safeHref = sanitizeHref(href ?? '');
  return safeHref
    ? <a href={safeHref} rel="noopener noreferrer" target="_blank">{children}</a>
    : <span className="blocked-link" aria-label="Unsafe link blocked">{children}</span>;
}

function SafeImage({ alt }: ComponentPropsWithoutRef<'img'>) {
  return <span className="blocked-image" aria-label="Image blocked">{alt ? `Image omitted: ${alt}` : 'Image omitted'}</span>;
}

function SafeCode({ className, children, ...props }: ComponentPropsWithoutRef<'code'>) {
  return <code className={sanitizedLanguageClass(className)} {...props}>{children}</code>;
}

const markdownComponents = {
  a: SafeLink,
  img: SafeImage,
  code: SafeCode,
  table: ({ children }: { children?: ReactNode }) => <div className="markdown-table-scroll"><table>{children}</table></div>
};

export function renderMarkdownToSanitizedElements(markdown: string) {
  return (
    <ReactMarkdown remarkPlugins={[remarkGfm]} skipHtml components={markdownComponents}>
      {markdown}
    </ReactMarkdown>
  );
}

export function MarkdownResponseSurface({ envelope }: MarkdownResponseSurfaceProps) {
  if (envelope.redaction.omittedFieldKeys?.includes('markdown')) {
    return <SurfaceStateFrame state={{ status: 'forbidden', message: 'This response was redacted for the selected context.', recovery: 'Open an authorized context or ask for a safe summary.' }} />;
  }

  const markdown = envelope.data.markdown?.trim() ?? '';
  if (!markdown) {
    return <SurfaceStateFrame state={{ status: 'empty', message: 'No markdown response is available for this workstream item.' }} />;
  }

  const sanitizedBlocks = renderMarkdownToSanitizedElements(markdown);

  return (
    <section className="structured-surface surface-frame markdown_response markdown-response-only" data-surface-id={envelope.surfaceId} aria-label="Model response">
      <article className="markdown-response-surface">
        <div className="markdown-response-content">{sanitizedBlocks}</div>
      </article>
    </section>
  );
}
