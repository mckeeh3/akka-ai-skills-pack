import { forwardRef, type InputHTMLAttributes, type ReactNode, type SelectHTMLAttributes, type TextareaHTMLAttributes } from 'react';

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

export const TextInput = forwardRef<HTMLInputElement, InputHTMLAttributes<HTMLInputElement> & { id: string; label: string; helper?: string; error?: string }>(function TextInput({ label, helper, error, className, ...props }, ref) {
  return <FormField id={props.id} label={label} helper={helper} error={error}><input ref={ref} className={['designed-control', className].filter(Boolean).join(' ')} {...props} aria-invalid={error ? 'true' : undefined} aria-describedby={error ? `${props.id}-error` : helper ? `${props.id}-helper` : undefined} /></FormField>;
});

export const TextArea = forwardRef<HTMLTextAreaElement, TextareaHTMLAttributes<HTMLTextAreaElement> & { id: string; label: string; helper?: string; error?: string }>(function TextArea({ label, helper, error, className, ...props }, ref) {
  return <FormField id={props.id} label={label} helper={helper} error={error}><textarea ref={ref} className={['designed-control', className].filter(Boolean).join(' ')} {...props} aria-invalid={error ? 'true' : undefined} aria-describedby={error ? `${props.id}-error` : helper ? `${props.id}-helper` : undefined} /></FormField>;
});

export const SelectField = forwardRef<HTMLSelectElement, SelectHTMLAttributes<HTMLSelectElement> & { id: string; label: string; helper?: string; error?: string; children: ReactNode }>(function SelectField({ label, helper, error, children, className, ...props }, ref) {
  return <FormField id={props.id} label={label} helper={helper} error={error}><select ref={ref} className={['designed-control', className].filter(Boolean).join(' ')} {...props} aria-invalid={error ? 'true' : undefined} aria-describedby={error ? `${props.id}-error` : helper ? `${props.id}-helper` : undefined}>{children}</select></FormField>;
});
