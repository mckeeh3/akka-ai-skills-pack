---
name: Akka Secure App
description: Role-aware security console for Akka and WorkOS administration.
colors:
  ink: "oklch(25% 0.025 255)"
  ink-strong: "oklch(18% 0.028 255)"
  muted: "oklch(49% 0.032 255)"
  subtle: "oklch(62% 0.026 255)"
  surface: "oklch(98% 0.006 255)"
  surface-raised: "oklch(99% 0.005 255)"
  surface-nav: "oklch(95% 0.014 255)"
  line: "oklch(88% 0.018 255)"
  line-strong: "oklch(78% 0.026 255)"
  accent: "oklch(49% 0.16 263)"
  accent-hover: "oklch(43% 0.17 263)"
  accent-soft: "oklch(94% 0.035 263)"
  accent-ring: "oklch(64% 0.14 263)"
  danger: "oklch(55% 0.18 28)"
  danger-soft: "oklch(95% 0.035 28)"
typography:
  display:
    fontFamily: "Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, sans-serif"
    fontSize: "2rem"
    fontWeight: 820
    lineHeight: 1.05
    letterSpacing: "-0.045em"
  headline:
    fontFamily: "Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, sans-serif"
    fontSize: "1.85rem"
    fontWeight: 820
    lineHeight: 1.12
    letterSpacing: "-0.045em"
  title:
    fontFamily: "Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, sans-serif"
    fontSize: "1.25rem"
    fontWeight: 820
    lineHeight: 1.2
    letterSpacing: "-0.025em"
  body:
    fontFamily: "Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, sans-serif"
    fontSize: "1rem"
    fontWeight: 400
    lineHeight: 1.6
  label:
    fontFamily: "Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, sans-serif"
    fontSize: "0.76rem"
    fontWeight: 820
    lineHeight: 1
    letterSpacing: "0.11em"
rounded:
  xs: "0.35rem"
  sm: "0.7rem"
  md: "0.85rem"
  lg: "1.05rem"
  xl: "1.25rem"
  pill: "999px"
spacing:
  xs: "0.25rem"
  sm: "0.5rem"
  md: "1rem"
  lg: "1.25rem"
  xl: "1.5rem"
  xxl: "2.75rem"
components:
  button-primary:
    backgroundColor: "{colors.accent}"
    textColor: "{colors.surface}"
    rounded: "{rounded.sm}"
    padding: "0.75rem 1rem"
    height: "2.75rem"
  button-primary-hover:
    backgroundColor: "{colors.accent-hover}"
    textColor: "{colors.surface}"
    rounded: "{rounded.sm}"
    padding: "0.75rem 1rem"
    height: "2.75rem"
  nav-item:
    backgroundColor: "transparent"
    textColor: "{colors.muted}"
    rounded: "{rounded.sm}"
    padding: "0.75rem 1rem"
    height: "2.85rem"
  nav-item-active:
    backgroundColor: "{colors.accent-soft}"
    textColor: "{colors.accent-hover}"
    rounded: "{rounded.sm}"
    padding: "0.75rem 1rem"
    height: "2.85rem"
  card:
    backgroundColor: "{colors.surface-raised}"
    textColor: "{colors.ink}"
    rounded: "{rounded.lg}"
    padding: "1.2rem"
  status-chip:
    backgroundColor: "{colors.surface-nav}"
    textColor: "{colors.muted}"
    rounded: "{rounded.pill}"
    padding: "0.25rem 0.65rem"
---

# Design System: Akka Secure App

## 1. Overview

**Creative North Star: "The Role-Aware Console"**

The system is a clean product interface for security administration. It is not trying to perform security through neon, darkness, or theatrical motifs. It earns trust through visible structure, clear state, consistent navigation, and calm surfaces that help users understand what their role permits.

The atmosphere is modern and functional, with a soft layered surface model. The console should feel enterprise-ready without becoming a cold, anonymous enterprise console. It is a template-quality product UI: familiar enough to extend, polished enough to signal production intent.

**Key Characteristics:**
- Restrained light theme with cool tinted neutrals.
- One primary indigo accent used for actions, active navigation, focus, and section signals.
- Soft layered elevation for cards, panels, and hover feedback.
- Dense but readable typography using a single system sans stack.
- Role-aware hierarchy that keeps unavailable complexity out of the way.

## 2. Colors

The palette is restrained: cool tinted neutrals carry most of the surface, while a controlled indigo accent marks action, selection, and focus.

### Primary
- **Authority Indigo**: Primary action, active navigation, section eyebrow text, and focus identity. It should remain rare and operational, never decorative.
- **Pressed Indigo**: Hover and active expression of the primary accent. Use it when an element is interactive or currently selected.
- **Signal Indigo Wash**: Soft selected background for navigation and subtle accent surfaces.
- **Focus Indigo**: Keyboard focus ring and accessibility signal. It must be visible, not hidden behind shadows.

### Neutral
- **Command Ink**: Default text for long-lived interface content.
- **Deep Console Ink**: Headlines, names, values, and other high-confidence text.
- **Administrative Muted**: Supporting text, subtitles, labels, and inactive navigation.
- **Quiet Subtle**: Uppercase section hints and low-priority meta text.
- **Console Surface**: Page background and broad application canvas.
- **Raised Surface**: Cards, panels, and center-card surfaces.
- **Navigation Surface**: Sidebar, chips, code backgrounds, and tonal separation zones.
- **Structural Line**: Borders and dividers. Use it to clarify structure, not to decorate.
- **Strong Structural Line**: Higher-emphasis separators only when the default line is too quiet.

### Tertiary
- **Operational Red**: Error text and destructive state language.
- **Red Recovery Wash**: Error panel background. It should make errors noticeable without alarming the whole page.

### Named Rules
**The One Accent Rule.** Authority Indigo is the only brand accent. Do not add teal, cyan, purple gradients, or extra dashboard colors unless a data visualization requires a deliberate semantic scale.

**The Tinted Neutral Rule.** Neutrals are cool and slightly chromatic. Pure black, pure white, and flat gray are prohibited because they make the console feel generic.

## 3. Typography

**Display Font:** Inter with ui-sans-serif, system-ui, Apple, BlinkMacSystemFont, and Segoe UI fallbacks.
**Body Font:** Inter with the same system stack.
**Label/Mono Font:** No distinct mono family is used; code inherits the product sans and gets a tonal container.

**Character:** The typography is compact, precise, and native-feeling. It uses weight, letter spacing, and restrained size changes rather than display type or decorative pairing.

### Hierarchy
- **Display** (820, 2rem, 1.05): Center-card titles and entry states. Use sparingly where the user is outside the app shell.
- **Headline** (820, 1.85rem, 1.12): Page titles inside the console.
- **Title** (820, 1.25rem, 1.2): Important values, compact summaries, and content emphasis.
- **Body** (400, 1rem, 1.6): Explanatory copy and panel text. Cap prose at roughly 70ch.
- **Label** (820, 0.76rem, 0.11em, uppercase): Eyebrows and section labels. Use for orientation, never for long text.

### Named Rules
**The Native Product Rule.** Use one sans family. Display fonts, novelty fonts, and label-specific typefaces are forbidden in this console.

**The Weight Before Size Rule.** Increase confidence through weight and spacing before increasing type size. Oversized dashboard typography is not part of this system.

## 4. Elevation

The system uses soft layered surfaces throughout. Depth comes from a hybrid of tonal separation, borders, and low-opacity shadows. Panels are quietly lifted from the page, while hover states can tighten the shadow and move by 1px to confirm interactivity.

### Shadow Vocabulary
- **Soft Ambient** (`0 18px 48px oklch(38% 0.04 255 / 0.12)`): Center cards and prominent raised containers.
- **Tight Surface** (`0 8px 22px oklch(38% 0.04 255 / 0.10)`): Panels, info cards, and button hover feedback.

### Named Rules
**The Layered Console Rule.** Surfaces may lift, but they must stay quiet. If the shadow is the first thing a user notices, it is too strong.

**The Border Plus Shadow Rule.** Raised elements use both a subtle border and a soft shadow. Shadow without structure makes the UI feel blurry and generic.

## 5. Components

### Buttons
- **Shape:** Gently rounded control shape (0.7rem) with a minimum height of 2.75rem.
- **Primary:** Authority Indigo background with near-surface text, strong weight, and compact padding (0.75rem 1rem).
- **Hover / Focus:** Hover shifts to Pressed Indigo, adds Tight Surface shadow, and translates up 1px. Focus uses a 3px Focus Indigo outline with a 3px offset.
- **Secondary / Ghost / Tertiary:** Not established yet. If added, they must keep the same radius, height, focus ring, and type weight.

### Chips
- **Style:** Status chips use Navigation Surface, Administrative Muted text, pill radius, a Structural Line border, and compact padding.
- **State:** Chips communicate status only. Do not turn status chips into decorative badges.

### Cards / Containers
- **Corner Style:** Soft container radius (1.05rem), with center-card moments using the larger 1.25rem radius.
- **Background:** Raised Surface for content panels and cards.
- **Shadow Strategy:** Tight Surface for normal panels, Soft Ambient for entry and configuration cards.
- **Border:** Structural Line always accompanies raised containers.
- **Internal Padding:** Standard panel padding is 1.2rem. Entry cards can scale from 1.5rem to 2.5rem.

### Inputs / Fields
- **Style:** Dedicated input components are not implemented yet. When added, they should use Raised Surface, Structural Line, 0.7rem radius, and the same focus ring as buttons.
- **Focus:** Focus must use the 3px Focus Indigo outline, not a hidden border-only change.
- **Error / Disabled:** Error uses Operational Red text and Red Recovery Wash. Disabled controls should lower contrast and keep the same shape.

### Navigation
- **Style:** Sidebar navigation is tonal and familiar: Navigation Surface background, full-width rounded items, muted text by default.
- **Hover:** Hover adds a Raised Surface background and Structural Line border without decorative color.
- **Active:** Active state uses Signal Indigo Wash with Pressed Indigo text and an inset line. It should be unmistakable but not loud.
- **Mobile:** Sidebar navigation becomes a horizontal scroll row below the top bar at narrow widths.

## 6. Do's and Don'ts

### Do:
- **Do** keep Authority Indigo rare and functional: primary actions, active navigation, focus, and section signals only.
- **Do** use cool tinted neutrals for every broad surface. The console should look designed even before the accent appears.
- **Do** pair borders with soft shadows on panels so the interface feels layered, not foggy.
- **Do** keep touch targets at or above 44px where practical.
- **Do** use visible keyboard focus with the 3px Focus Indigo ring.
- **Do** preserve reduced-motion support for all transitions.

### Don't:
- **Don't** make this a clean enterprise console where every surface looks interchangeable. It must stay role-aware and purposeful.
- **Don't** use neon cyber-security aesthetics, playful consumer styling, generic dark SaaS gloss, or cluttered legacy admin dashboards.
- **Don't** introduce teal, cyan, purple gradients, glassmorphism, or decorative security motifs.
- **Don't** use pure black, pure white, or untinted gray as the primary neutral vocabulary.
- **Don't** use side-stripe borders, gradient text, or repeated identical icon-card grids.
- **Don't** hide focus states or rely on color alone to communicate role, status, or errors.
