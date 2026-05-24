import type { ReactNode } from 'react';
import type { MarkdownResponseData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type MarkdownResponseSurfaceProps = {
  envelope: SurfaceEnvelope<MarkdownResponseData>;
  onAction?: (action: SurfaceAction, surfaceId: string) => void;
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

function tokenizeInlineMarkdown(input: string): ReactNode[] {
  const nodes: ReactNode[] = [];
  const inlinePattern = /(\[[^\]]+\]\([^)]+\)|`[^`]+`|\*\*[^*]+\*\*|\*[^*]+\*)/g;
  let cursor = 0;
  let tokenIndex = 0;
  for (const match of input.matchAll(inlinePattern)) {
    if (match.index > cursor) {
      nodes.push(input.slice(cursor, match.index));
    }
    const token = match[0];
    const key = `inline-${tokenIndex++}`;
    const link = token.match(/^\[([^\]]+)\]\(([^)]+)\)$/);
    if (link) {
      const safeHref = sanitizeHref(link[2]);
      nodes.push(safeHref ? <a key={key} href={safeHref} rel="noopener noreferrer" target="_blank">{link[1]}</a> : <span key={key} className="blocked-link" aria-label="Unsafe link blocked">{link[1]}</span>);
    } else if (token.startsWith('`')) {
      nodes.push(<code key={key}>{token.slice(1, -1)}</code>);
    } else if (token.startsWith('**')) {
      nodes.push(<strong key={key}>{token.slice(2, -2)}</strong>);
    } else if (token.startsWith('*')) {
      nodes.push(<em key={key}>{token.slice(1, -1)}</em>);
    }
    cursor = match.index + token.length;
  }
  if (cursor < input.length) {
    nodes.push(input.slice(cursor));
  }
  return nodes;
}

function sanitizedLanguageClass(language: string) {
  const safeLanguage = language.trim().match(/^[\w-]+$/)?.[0];
  return safeLanguage ? `language-${safeLanguage}` : undefined;
}

export function renderMarkdownToSanitizedElements(markdown: string) {
  const lines = markdown.replace(/\r\n?/g, '\n').split('\n');
  const blocks: ReactNode[] = [];
  let paragraph: string[] = [];
  let listItems: string[] = [];
  let inCodeBlock = false;
  let codeLanguage = '';
  let codeLines: string[] = [];

  const flushParagraph = () => {
    if (paragraph.length > 0) {
      blocks.push(<p key={`p-${blocks.length}`}>{tokenizeInlineMarkdown(paragraph.join(' '))}</p>);
      paragraph = [];
    }
  };
  const flushList = () => {
    if (listItems.length > 0) {
      blocks.push(<ul key={`ul-${blocks.length}`}>{listItems.map((item, index) => <li key={`${index}:${item}`}>{tokenizeInlineMarkdown(item)}</li>)}</ul>);
      listItems = [];
    }
  };
  const flushCodeBlock = () => {
    blocks.push(<pre key={`pre-${blocks.length}`}><code className={sanitizedLanguageClass(codeLanguage)}>{codeLines.join('\n')}</code></pre>);
    codeLines = [];
    codeLanguage = '';
  };

  for (const line of lines) {
    const codeFence = line.match(/^```\s*([\w-]+)?\s*$/);
    if (codeFence) {
      flushParagraph();
      flushList();
      if (inCodeBlock) {
        flushCodeBlock();
        inCodeBlock = false;
      } else {
        inCodeBlock = true;
        codeLanguage = codeFence[1] ?? '';
      }
      continue;
    }

    if (inCodeBlock) {
      codeLines.push(line);
      continue;
    }

    if (!line.trim()) {
      flushParagraph();
      flushList();
      continue;
    }

    const heading = line.match(/^(#{1,4})\s+(.+)$/);
    if (heading) {
      flushParagraph();
      flushList();
      const level = heading[1].length + 2;
      const headingNodes = tokenizeInlineMarkdown(heading[2]);
      if (level === 3) {
        blocks.push(<h3 key={`h-${blocks.length}`}>{headingNodes}</h3>);
      } else if (level === 4) {
        blocks.push(<h4 key={`h-${blocks.length}`}>{headingNodes}</h4>);
      } else if (level === 5) {
        blocks.push(<h5 key={`h-${blocks.length}`}>{headingNodes}</h5>);
      } else {
        blocks.push(<h6 key={`h-${blocks.length}`}>{headingNodes}</h6>);
      }
      continue;
    }

    const list = line.match(/^[-*]\s+(.+)$/);
    if (list) {
      flushParagraph();
      listItems.push(list[1]);
      continue;
    }

    if (line.startsWith('> ')) {
      flushParagraph();
      flushList();
      blocks.push(<blockquote key={`quote-${blocks.length}`}>{tokenizeInlineMarkdown(line.slice(2))}</blockquote>);
      continue;
    }

    paragraph.push(line.trim());
  }

  flushParagraph();
  flushList();
  if (inCodeBlock) {
    flushCodeBlock();
  }

  return blocks;
}

export function MarkdownResponseSurface({ envelope, onAction }: MarkdownResponseSurfaceProps) {
  if (envelope.redaction.omittedFieldKeys?.includes('markdown')) {
    return <SurfaceStateFrame state={{ status: 'forbidden', message: 'This response was redacted for the selected context.', recovery: 'Open an authorized context or ask for a safe summary.' }} />;
  }

  const markdown = envelope.data.markdown?.trim() ?? '';
  if (!markdown) {
    return <SurfaceStateFrame state={{ status: 'empty', message: 'No markdown response is available for this workstream item.' }} />;
  }

  const sanitizedBlocks = renderMarkdownToSanitizedElements(markdown);

  return (
    <SurfaceStateFrame envelope={envelope}>
      <article className="markdown-response-surface" data-correlation-id={envelope.correlationId} data-producing-agent-id={envelope.data.producingAgentId} data-workstream-entry-id={envelope.data.workstreamEntryId}>
        {envelope.data.title && <h4>{envelope.data.title}</h4>}
        {envelope.data.summary && <p className="surface-summary">{envelope.data.summary}</p>}
        <div className="markdown-response-content">{sanitizedBlocks}</div>
        {(envelope.traceIds.length > 0 || envelope.data.sourceRefs?.length) && (
          <aside className="markdown-response-trace" aria-label="Markdown response trace and source references">
            {envelope.traceIds.length > 0 && (
              <p>Trace links: {envelope.traceIds.map((traceId, index) => (
                <a key={traceId} href={`/ui?traceId=${encodeURIComponent(traceId)}`} rel="noopener noreferrer">{index > 0 ? ', ' : ''}{traceId}</a>
              ))}</p>
            )}
            {envelope.data.sourceRefs?.length ? (
              <ul>
                {envelope.data.sourceRefs.map((sourceRef) => (
                  <li key={`${sourceRef.refType}:${sourceRef.refId}`}>{sourceRef.label} <span>({sourceRef.refType})</span></li>
                ))}
              </ul>
            ) : null}
          </aside>
        )}
      </article>
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}
