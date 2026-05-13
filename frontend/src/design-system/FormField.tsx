import type { InputHTMLAttributes, ReactNode, SelectHTMLAttributes, TextareaHTMLAttributes } from 'react';

export function FormField({ id, label, helper, error, children }: { id: string; label: string; helper?: string; error?: string; children: ReactNode }) {
  return (
    <div className="form-field">
      <label htmlFor={id}>{label}</label>
      {children}
      {helper && !error && <p id={`${id}-helper`} className="field-helper">{helper}</p>}
      {error && <p id={`${id}-error`} className="field-error" role="alert">{error}</p>}
    </div>
  );
}

export function TextInput({ label, helper, error, ...props }: InputHTMLAttributes<HTMLInputElement> & { id: string; label: string; helper?: string; error?: string }) {
  return <FormField id={props.id} label={label} helper={helper} error={error}><input {...props} aria-invalid={error ? 'true' : undefined} aria-describedby={error ? `${props.id}-error` : helper ? `${props.id}-helper` : undefined} /></FormField>;
}

export function TextArea({ label, helper, error, ...props }: TextareaHTMLAttributes<HTMLTextAreaElement> & { id: string; label: string; helper?: string; error?: string }) {
  return <FormField id={props.id} label={label} helper={helper} error={error}><textarea {...props} aria-invalid={error ? 'true' : undefined} aria-describedby={error ? `${props.id}-error` : helper ? `${props.id}-helper` : undefined} /></FormField>;
}

export function SelectField({ label, helper, error, children, ...props }: SelectHTMLAttributes<HTMLSelectElement> & { id: string; label: string; helper?: string; error?: string; children: ReactNode }) {
  return <FormField id={props.id} label={label} helper={helper} error={error}><select {...props} aria-invalid={error ? 'true' : undefined} aria-describedby={error ? `${props.id}-error` : helper ? `${props.id}-helper` : undefined}>{children}</select></FormField>;
}
