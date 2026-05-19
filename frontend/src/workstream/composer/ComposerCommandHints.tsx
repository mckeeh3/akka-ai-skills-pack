type ComposerCommandHintsProps = {
  hints?: string[];
  onUseHint?: (hint: string) => void;
};

const defaultHints = ['Summarize what needs my approval', 'Show users needing access review', 'Open the latest trace'];

export function ComposerCommandHints({ hints = defaultHints, onUseHint }: ComposerCommandHintsProps) {
  return (
    <div className="prompt-row" aria-label="Suggested workstream prompts">
      {hints.map((hint) => (
        <button key={hint} type="button" className="prompt-chip" onClick={() => onUseHint?.(hint)}>
          {hint}
        </button>
      ))}
    </div>
  );
}
