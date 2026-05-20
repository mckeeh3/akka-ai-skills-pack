import type { ButtonHTMLAttributes, AnchorHTMLAttributes, ReactNode } from 'react';

type ButtonTone = 'primary' | 'secondary' | 'ghost' | 'danger';

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  tone?: ButtonTone;
  icon?: ReactNode;
};

export function Button({ tone = 'primary', icon, children, className = '', type = 'button', ...props }: ButtonProps) {
  return (
    <button type={type} className={`ds-button ${tone} ${className}`.trim()} {...props}>
      {icon && <span className="button-icon" aria-hidden="true">{icon}</span>}
      <span>{children}</span>
    </button>
  );
}

type ButtonLinkProps = AnchorHTMLAttributes<HTMLAnchorElement> & {
  tone?: ButtonTone;
  icon?: ReactNode;
};

export function ButtonLink({ tone = 'primary', icon, children, className = '', ...props }: ButtonLinkProps) {
  return (
    <a className={`ds-button ${tone} ${className}`.trim()} {...props}>
      {icon && <span className="button-icon" aria-hidden="true">{icon}</span>}
      <span>{children}</span>
    </a>
  );
}
