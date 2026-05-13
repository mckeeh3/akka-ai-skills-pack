import type { ReactNode } from 'react';
import { Button } from './Button';

export function CommandStrip({ title, description, prompts, sendLabel = 'Send command preview', onSend }: { title: string; description: string; prompts: string[]; sendLabel?: string; onSend?: () => void }) {
  return (
    <section className="command-strip" aria-labelledby="command-strip-title">
      <div className="ai-mark" aria-hidden="true">✦</div>
      <div>
        <h2 id="command-strip-title">{title}</h2>
        <p>{description}</p>
        <div className="prompt-row" aria-label="Example prompts">
          {prompts.map((prompt) => <button key={prompt} type="button" className="prompt-chip">{prompt}</button>)}
        </div>
      </div>
      <Button className="icon-button" aria-label={sendLabel} onClick={onSend}>➤</Button>
    </section>
  );
}

export function CommandStripLayout({ children }: { children: ReactNode }) {
  return <div className="command-strip-layout">{children}</div>;
}
