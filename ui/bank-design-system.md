# Lloyds Banking Group — Official Design System
**Lloyds Banking Group · Digital Banking Platform**
Version 1.0 · May 2026

---

## 1. Brand Identity

Lloyds Banking Group's digital product language is **trusted, accessible, and modern**. The visual identity draws from the brand's heritage — deep green, clean white surfaces, and the iconic black horse motif — reinterpreted for digital banking with a focus on clarity, warmth, and financial empowerment.

**Design principles:**
- Light surfaces as the default — green used purposefully as a signal, not a background
- Heritage green as the single dominant brand colour; secondary palette used for supporting content and data
- Clarity over density — breathing room and readable hierarchy are non-negotiable
- Rounded, approachable components — not clinical or austere
- Mobile-first layout with fluid scaling to desktop
- Accessible by design at every layer — WCAG 2.1 AA minimum

---

## 2. Colour Palette

### Core Brand
| Token | Hex | Usage |
|---|---|---|
| `--lloyds-green` | `#006A4D` | Primary CTAs, active states, focus rings, key highlights, brand elements |
| `--lloyds-green-dark` | `#005238` | Hover state on green buttons, pressed states |
| `--lloyds-green-light` | `#E6F2EE` | Background tint on callouts, selected row tint, hover on list items |
| `--lloyds-green-mid` | `#00875F` | Secondary interactive elements, progress indicators |
| `--lloyds-text-on-green` | `#FFFFFF` | Text placed ON green backgrounds |

### Surface & Background
| Token | Hex | Usage |
|---|---|---|
| `--bg-app` | `#F5F5F5` | Application shell, page background behind cards |
| `--bg-surface` | `#FFFFFF` | Cards, panels, modals, form containers |
| `--bg-nav` | `#FFFFFF` | Top navigation bar background |
| `--bg-nav-mobile` | `#006A4D` | Mobile bottom nav bar and hamburger menu header |
| `--bg-section` | `#F9F9F9` | Section dividers, subtle alternate row backgrounds |
| `--bg-row-selected` | `#E6F2EE` | Selected list or table row tint |
| `--bg-row-hover` | `#F5F5F5` | Table or list row hover |
| `--bg-overlay` | `rgba(0,0,0,0.40)` | Modal backdrop, sheet overlays |
| `--bg-input` | `#FFFFFF` | Form input backgrounds |
| `--bg-input-disabled` | `#F5F5F5` | Disabled input background |

### Text
| Token | Hex | Usage |
|---|---|---|
| `--text-primary` | `#1A1A1A` | Primary body text, headings on light surfaces |
| `--text-secondary` | `#595959` | Supporting labels, captions, helper text |
| `--text-muted` | `#888888` | Disabled, placeholder, timestamps |
| `--text-on-dark` | `#FFFFFF` | Text on green or dark surfaces |
| `--text-link` | `#006A4D` | Inline links |
| `--text-link-hover` | `#005238` | Hovered inline links |
| `--text-error` | `#C0392B` | Error messages, validation text |
| `--text-success` | `#006A4D` | Success messages, confirmations |
| `--text-warning` | `#9C5A00` | Warning messages |

### Borders & Dividers
| Token | Hex | Usage |
|---|---|---|
| `--border-light` | `#E0E0E0` | Card borders, table row dividers, input default state |
| `--border-medium` | `#CCCCCC` | Stronger dividers, inactive separators |
| `--border-focus` | `#006A4D` | Input and interactive element focus ring |
| `--border-error` | `#C0392B` | Error state input border |
| `--border-success` | `#006A4D` | Success state input border |

### Semantic Status (data tables, transaction feeds, alerts)
| Token | Hex | Usage |
|---|---|---|
| `--status-positive` | `#006A4D` | Positive transactions, credit amounts |
| `--status-positive-bg` | `#E6F2EE` | Positive status badge background |
| `--status-negative` | `#C0392B` | Debit amounts, negative balance |
| `--status-negative-bg` | `#FDECEC` | Negative status badge background |
| `--status-pending` | `#9C5A00` | Pending transactions, processing states |
| `--status-pending-bg` | `#FFF3E0` | Pending badge background |
| `--status-info` | `#1565C0` | Informational notices |
| `--status-info-bg` | `#E3F2FD` | Info badge background |
| `--status-neutral` | `#555555` | Neutral states, archived items |
| `--status-neutral-bg` | `#F5F5F5` | Neutral badge background |

---

## 3. Typography

**Fonts:** GT Ultra Standard Regular (body, labels, captions) · GT Ultra Medium Bold (headings, CTAs, emphasis)

```css
font-family: 'GT Ultra Standard', -apple-system, BlinkMacSystemFont, 'Segoe UI', Arial, sans-serif;
```

GT Ultra is a humanist typeface with warmth and legibility at all sizes. Standard Regular carries all reading text; Medium Bold drives hierarchy and key actions.

### Type Scale

| Role | Size | Weight | Line Height | Color |
|---|---|---|---|---|
| Display / Hero | 32px | GT Ultra Medium Bold (700) | 1.2 | `#1A1A1A` |
| Page title | 24px | GT Ultra Medium Bold (700) | 1.25 | `#1A1A1A` |
| Section heading | 20px | GT Ultra Medium Bold (700) | 1.3 | `#1A1A1A` |
| Card heading | 17px | GT Ultra Medium Bold (700) | 1.35 | `#1A1A1A` |
| Body (default) | 16px | GT Ultra Standard Regular (400) | 1.6 | `#1A1A1A` |
| Body small | 14px | GT Ultra Standard Regular (400) | 1.5 | `#1A1A1A` |
| Label / Caption | 13px | GT Ultra Standard Regular (400) | 1.4 | `#595959` |
| Micro / Legal | 12px | GT Ultra Standard Regular (400) | 1.4 | `#888888` |
| Button label | 16px | GT Ultra Medium Bold (700) | 1.0 | varies |
| Nav label | 13px | GT Ultra Standard Regular (400) | 1.0 | `#1A1A1A` |
| Nav label (active) | 13px | GT Ultra Medium Bold (700) | 1.0 | `#006A4D` |
| Amount / Figure | 24px | GT Ultra Medium Bold (700) | 1.1 | `#1A1A1A` |
| Amount small | 18px | GT Ultra Medium Bold (700) | 1.2 | `#1A1A1A` |
| Badge / Pill | 12px | GT Ultra Medium Bold (700) | 1.0 | varies |

### Typography Usage Notes

- Use Medium Bold exclusively for headings, CTAs, balances, and emphasis — not for body copy
- Credit amounts use `--status-positive` green; debit amounts use `--status-negative` red
- Never set body copy below 14px; legal/micro text floors at 12px
- Letter spacing: default (0) for all roles except Badge/Pill which uses `letter-spacing: 0.02em`
- Text on `--lloyds-green` backgrounds always uses `#FFFFFF`
- Do not set justified alignment — left-align all prose

---

## 4. Spacing

Base unit: **4px**. All spacing is multiples of 4.

```
4px   — xs   (internal icon gap, tight badge padding)
8px   — sm   (chip padding, inline label spacing)
12px  — md   (list item padding, compact row padding)
16px  — lg   (card padding, form field spacing)
20px  — xl-  (section spacing within a card)
24px  — xl   (between cards, major section gaps)
32px  — 2xl  (page-level horizontal padding)
40px  — 3xl  (top-of-page hero spacing)
48px  — 4xl  (large section dividers)
```

### Layout Grid

| Breakpoint | Columns | Gutter | Margin |
|---|---|---|---|
| Mobile (< 480px) | 4 | 16px | 16px |
| Tablet (480–768px) | 8 | 20px | 24px |
| Desktop (768–1200px) | 12 | 24px | 32px |
| Wide (> 1200px) | 12 | 24px | auto (max-width 1180px) |

---

## 5. Components

### 5.1 Top Navigation Bar (Desktop)

```
Height: 64px
Background: #FFFFFF
Border-bottom: 1px solid #E0E0E0
Box-shadow: 0 1px 4px rgba(0,0,0,0.06)
```

Contains: Black Horse logo left-aligned, primary nav items horizontally centred (or left-grouped), utility actions right-aligned (notifications, profile, settings).

**Logo:**
- Black Horse SVG mark in `#006A4D` or full-colour
- Wordmark: "Lloyds" in `#1A1A1A`, bold condensed
- Minimum clear space: 12px all sides

**Nav items:**
```css
.nav-item {
  color: #595959;
  font-size: 14px;
  font-family: 'GT Ultra Standard', sans-serif;
  font-weight: 400;
  padding: 0 16px;
  height: 64px;
  display: flex;
  align-items: center;
  border-bottom: 3px solid transparent;
  text-decoration: none;
  transition: color 0.15s ease;
}
.nav-item:hover {
  color: #1A1A1A;
}
.nav-item.active {
  color: #006A4D;
  border-bottom-color: #006A4D;
  font-weight: 700;
  font-family: 'GT Ultra Medium Bold', sans-serif;
}
```

### 5.2 Mobile Navigation Bar

```
Height: 60px
Background: #FFFFFF
Border-top: 1px solid #E0E0E0
Position: fixed, bottom
```

Up to five tab items. Icon above label layout.

```css
.mobile-nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  color: #888888;
  font-size: 11px;
  padding: 8px 4px;
  text-decoration: none;
}
.mobile-nav-item .nav-icon {
  width: 24px;
  height: 24px;
  color: #888888;
}
.mobile-nav-item.active {
  color: #006A4D;
}
.mobile-nav-item.active .nav-icon {
  color: #006A4D;
}
```

### 5.3 Buttons

**Primary (Green CTA):**
```css
.btn-primary {
  background: #006A4D;
  color: #FFFFFF;
  font-size: 16px;
  font-weight: 700;
  font-family: 'GT Ultra Medium Bold', sans-serif;
  height: 52px;
  padding: 0 28px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: background 0.15s ease;
  min-width: 120px;
}
.btn-primary:hover { background: #005238; }
.btn-primary:focus-visible {
  outline: 2px solid #006A4D;
  outline-offset: 3px;
}
.btn-primary:disabled {
  background: #CCCCCC;
  color: #888888;
  cursor: not-allowed;
}
```

**Secondary (Outline):**
```css
.btn-secondary {
  background: transparent;
  color: #006A4D;
  font-size: 16px;
  font-weight: 700;
  font-family: 'GT Ultra Medium Bold', sans-serif;
  height: 52px;
  padding: 0 28px;
  border: 2px solid #006A4D;
  border-radius: 8px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: background 0.15s ease, border-color 0.15s ease;
}
.btn-secondary:hover {
  background: #E6F2EE;
  border-color: #005238;
  color: #005238;
}
.btn-secondary:disabled {
  border-color: #CCCCCC;
  color: #CCCCCC;
  cursor: not-allowed;
}
```

**Ghost / Tertiary (Text link-style):**
```css
.btn-ghost {
  background: transparent;
  color: #006A4D;
  font-size: 16px;
  font-weight: 700;
  font-family: 'GT Ultra Medium Bold', sans-serif;
  height: 52px;
  padding: 0 16px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  text-decoration: underline;
  text-underline-offset: 3px;
}
.btn-ghost:hover { color: #005238; }
```

**Destructive:**
```css
.btn-destructive {
  background: #C0392B;
  color: #FFFFFF;
  /* Same dimensions as .btn-primary */
}
.btn-destructive:hover { background: #A93226; }
```

**Icon Button:**
```css
.btn-icon {
  background: #FFFFFF;
  border: 1px solid #E0E0E0;
  border-radius: 8px;
  width: 44px;
  height: 44px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #595959;
  transition: background 0.12s ease, border-color 0.12s ease;
}
.btn-icon:hover {
  background: #F5F5F5;
  border-color: #CCCCCC;
  color: #1A1A1A;
}
```

**Button Sizes:**

| Variant | Height | Font | Padding H | Border Radius |
|---|---|---|---|---|
| Large | 56px | 18px | 32px | 10px |
| Default | 52px | 16px | 28px | 8px |
| Medium | 44px | 15px | 20px | 8px |
| Small | 36px | 13px | 16px | 6px |

### 5.4 Form Inputs

```css
.input {
  width: 100%;
  height: 52px;
  padding: 0 16px;
  border: 1.5px solid #CCCCCC;
  border-radius: 8px;
  font-size: 16px;
  font-family: 'GT Ultra Standard', sans-serif;
  font-weight: 400;
  color: #1A1A1A;
  background: #FFFFFF;
  transition: border-color 0.15s ease, box-shadow 0.15s ease;
  -webkit-appearance: none;
}
.input::placeholder {
  color: #888888;
}
.input:focus {
  outline: none;
  border-color: #006A4D;
  box-shadow: 0 0 0 3px rgba(0, 106, 77, 0.15);
}
.input.error {
  border-color: #C0392B;
  box-shadow: 0 0 0 3px rgba(192, 57, 43, 0.10);
}
.input:disabled {
  background: #F5F5F5;
  color: #888888;
  border-color: #E0E0E0;
  cursor: not-allowed;
}
```

**Input with label pattern:**
```css
.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 20px;
}
.field-label {
  font-size: 14px;
  font-weight: 700;
  font-family: 'GT Ultra Medium Bold', sans-serif;
  color: #1A1A1A;
}
.field-helper {
  font-size: 13px;
  color: #595959;
  margin-top: 4px;
}
.field-error {
  font-size: 13px;
  color: #C0392B;
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}
```

**Select / Dropdown:**
```css
.select {
  /* Inherits .input */
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='%23595959' stroke-width='2'%3E%3Cpolyline points='6 9 12 15 18 9'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 14px center;
  padding-right: 44px;
}
```

**Textarea:**
```css
.textarea {
  /* Inherits .input */
  height: auto;
  min-height: 120px;
  padding: 14px 16px;
  resize: vertical;
  line-height: 1.6;
}
```

### 5.5 Cards & Panels

**Standard Card:**
```css
.card {
  background: #FFFFFF;
  border: 1px solid #E0E0E0;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.card-title {
  font-size: 17px;
  font-weight: 700;
  font-family: 'GT Ultra Medium Bold', sans-serif;
  color: #1A1A1A;
}
.card-subtitle {
  font-size: 13px;
  color: #595959;
  margin-top: 2px;
}
```

**Account Summary Card (Hero variant):**
```css
.card-account {
  background: #006A4D;
  border-radius: 16px;
  padding: 24px;
  color: #FFFFFF;
}
.card-account .account-type {
  font-size: 13px;
  font-family: 'GT Ultra Standard', sans-serif;
  opacity: 0.8;
  margin-bottom: 8px;
}
.card-account .account-balance {
  font-size: 36px;
  font-weight: 700;
  font-family: 'GT Ultra Medium Bold', sans-serif;
  letter-spacing: -0.02em;
}
.card-account .account-number {
  font-size: 13px;
  opacity: 0.7;
  margin-top: 16px;
}
```

**Clickable / Action Card:**
```css
.card-action {
  /* Inherits .card */
  cursor: pointer;
  transition: box-shadow 0.15s ease, transform 0.12s ease;
  text-decoration: none;
  display: block;
}
.card-action:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.10);
  transform: translateY(-1px);
}
```

### 5.6 Data Table

```css
.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  font-family: 'GT Ultra Standard', sans-serif;
}
.data-table thead th {
  text-align: left;
  padding: 12px 16px;
  font-size: 13px;
  font-weight: 700;
  font-family: 'GT Ultra Medium Bold', sans-serif;
  color: #595959;
  border-bottom: 2px solid #E0E0E0;
  white-space: nowrap;
  background: #FFFFFF;
}
.data-table tbody td {
  padding: 14px 16px;
  border-bottom: 1px solid #E0E0E0;
  color: #1A1A1A;
  vertical-align: middle;
}
.data-table tbody tr:hover td {
  background: #F5F5F5;
}
.data-table tbody tr.selected td {
  background: #E6F2EE;
}
.data-table input[type="checkbox"] {
  accent-color: #006A4D;
  width: 16px;
  height: 16px;
  border-radius: 4px;
}
/* Amount columns — right-align financial values */
.data-table .col-amount {
  text-align: right;
  font-weight: 700;
  font-family: 'GT Ultra Medium Bold', sans-serif;
}
.data-table .col-amount.positive { color: #006A4D; }
.data-table .col-amount.negative { color: #C0392B; }
```

### 5.7 Transaction List Row

Common pattern for mobile and desktop transaction feeds.

```css
.transaction-row {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #E0E0E0;
  gap: 12px;
  background: #FFFFFF;
  cursor: pointer;
  transition: background 0.12s ease;
}
.transaction-row:hover { background: #F5F5F5; }

.transaction-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: #F5F5F5;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 20px;
}
.transaction-info {
  flex: 1;
  min-width: 0;
}
.transaction-merchant {
  font-size: 15px;
  font-weight: 400;
  color: #1A1A1A;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.transaction-date {
  font-size: 13px;
  color: #888888;
  margin-top: 2px;
}
.transaction-amount {
  font-size: 16px;
  font-weight: 700;
  font-family: 'GT Ultra Medium Bold', sans-serif;
  flex-shrink: 0;
}
.transaction-amount.credit { color: #006A4D; }
.transaction-amount.debit { color: #1A1A1A; }
```

### 5.8 Badges & Status Pills

```css
.badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  font-family: 'GT Ultra Medium Bold', sans-serif;
  letter-spacing: 0.02em;
  white-space: nowrap;
}
/* Semantic variants */
.badge-positive  { background: #E6F2EE; color: #005238; border: 1px solid #A8D5C5; }
.badge-negative  { background: #FDECEC; color: #A32920; border: 1px solid #F5B7B1; }
.badge-pending   { background: #FFF3E0; color: #9C5A00; border: 1px solid #FFCC80; }
.badge-info      { background: #E3F2FD; color: #1565C0; border: 1px solid #90CAF9; }
.badge-neutral   { background: #F5F5F5; color: #595959; border: 1px solid #E0E0E0; }
.badge-green-solid { background: #006A4D; color: #FFFFFF; border: none; }

/* Notification dot (red) */
.badge-notification {
  background: #C0392B;
  color: #FFFFFF;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 11px;
  padding: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
```

### 5.9 Alert / Notification Banners

```css
.alert {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  border-radius: 10px;
  font-size: 14px;
  line-height: 1.5;
  border: 1px solid;
}
.alert-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
  margin-top: 1px;
}
.alert-title {
  font-weight: 700;
  font-family: 'GT Ultra Medium Bold', sans-serif;
  margin-bottom: 4px;
}

.alert-success {
  background: #E6F2EE;
  border-color: #A8D5C5;
  color: #004D36;
}
.alert-error {
  background: #FDECEC;
  border-color: #F5B7B1;
  color: #922B21;
}
.alert-warning {
  background: #FFF3E0;
  border-color: #FFCC80;
  color: #7D4600;
}
.alert-info {
  background: #E3F2FD;
  border-color: #90CAF9;
  color: #0D47A1;
}
```

### 5.10 Progress / Step Indicator

Used for multi-step journeys (onboarding, application flows, KYC).

```css
.step-bar {
  display: flex;
  align-items: center;
  gap: 0;
  margin-bottom: 32px;
}
.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  flex: 1;
  position: relative;
}
.step:not(:last-child)::after {
  content: '';
  position: absolute;
  top: 16px;
  left: 50%;
  right: -50%;
  height: 2px;
  background: #E0E0E0;
  z-index: 0;
}
.step.completed::after { background: #006A4D; }

.step-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #F5F5F5;
  border: 2px solid #E0E0E0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
  font-family: 'GT Ultra Medium Bold', sans-serif;
  color: #888888;
  position: relative;
  z-index: 1;
}
.step.active .step-circle {
  background: #006A4D;
  border-color: #006A4D;
  color: #FFFFFF;
}
.step.completed .step-circle {
  background: #006A4D;
  border-color: #006A4D;
  color: #FFFFFF;
}
.step-label {
  font-size: 12px;
  color: #888888;
  text-align: center;
}
.step.active .step-label {
  color: #006A4D;
  font-weight: 700;
  font-family: 'GT Ultra Medium Bold', sans-serif;
}
.step.completed .step-label { color: #006A4D; }
```

### 5.11 Progress Bar (Linear)

Used for savings goals, loan repayment progress, spending limits.

```css
.progress-track {
  width: 100%;
  height: 8px;
  background: #E0E0E0;
  border-radius: 100px;
  overflow: hidden;
}
.progress-fill {
  height: 100%;
  background: #006A4D;
  border-radius: 100px;
  transition: width 0.4s ease;
}
.progress-fill.warning { background: #FF9800; }
.progress-fill.danger { background: #C0392B; }
```

### 5.12 Toggle / Switch

```css
.toggle {
  position: relative;
  width: 52px;
  height: 28px;
}
.toggle input { opacity: 0; width: 0; height: 0; }
.toggle-track {
  position: absolute;
  inset: 0;
  background: #CCCCCC;
  border-radius: 100px;
  cursor: pointer;
  transition: background 0.2s ease;
}
.toggle input:checked + .toggle-track { background: #006A4D; }
.toggle-thumb {
  position: absolute;
  top: 3px;
  left: 3px;
  width: 22px;
  height: 22px;
  background: #FFFFFF;
  border-radius: 50%;
  box-shadow: 0 1px 4px rgba(0,0,0,0.2);
  transition: transform 0.2s ease;
}
.toggle input:checked ~ .toggle-thumb { transform: translateX(24px); }
```

### 5.13 Chips / Tags

```css
.chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 6px;
  border: 1px solid;
  font-size: 12px;
  font-weight: 400;
  font-family: 'GT Ultra Standard', sans-serif;
  white-space: nowrap;
}
/* Category chips */
.chip-default  { color: #595959; background: #F5F5F5; border-color: #E0E0E0; }
.chip-green    { color: #004D36; background: #E6F2EE; border-color: #A8D5C5; }
.chip-blue     { color: #0D47A1; background: #E3F2FD; border-color: #90CAF9; }
.chip-amber    { color: #7D4600; background: #FFF3E0; border-color: #FFCC80; }
.chip-red      { color: #922B21; background: #FDECEC; border-color: #F5B7B1; }
/* Removable chip */
.chip-remove {
  margin-left: 4px;
  color: inherit;
  opacity: 0.6;
  cursor: pointer;
  font-size: 14px;
  line-height: 1;
}
.chip-remove:hover { opacity: 1; }
```

### 5.14 Modal / Bottom Sheet

```css
/* Overlay */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.40);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
}
/* Desktop modal */
.modal {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 32px;
  width: 100%;
  max-width: 480px;
  box-shadow: 0 8px 40px rgba(0,0,0,0.20);
  position: relative;
}
.modal-title {
  font-size: 20px;
  font-weight: 700;
  font-family: 'GT Ultra Medium Bold', sans-serif;
  color: #1A1A1A;
  margin-bottom: 8px;
}
.modal-close {
  position: absolute;
  top: 16px;
  right: 16px;
  /* Uses .btn-icon */
}
/* Mobile bottom sheet */
.bottom-sheet {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #FFFFFF;
  border-radius: 20px 20px 0 0;
  padding: 24px 24px 40px;
  box-shadow: 0 -4px 24px rgba(0,0,0,0.12);
  z-index: 1001;
}
.bottom-sheet-handle {
  width: 40px;
  height: 4px;
  background: #E0E0E0;
  border-radius: 2px;
  margin: 0 auto 20px;
}
```

### 5.15 List Items (Menu / Settings)

```css
.list-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-bottom: 1px solid #E0E0E0;
  cursor: pointer;
  background: #FFFFFF;
  text-decoration: none;
  transition: background 0.12s ease;
}
.list-item:hover { background: #F5F5F5; }
.list-item:last-child { border-bottom: none; }

.list-item-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: #E6F2EE;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #006A4D;
  font-size: 18px;
  flex-shrink: 0;
}
.list-item-label {
  flex: 1;
  font-size: 15px;
  color: #1A1A1A;
}
.list-item-secondary {
  font-size: 13px;
  color: #595959;
  margin-top: 2px;
}
.list-item-chevron {
  color: #888888;
  font-size: 16px;
  flex-shrink: 0;
}
```

### 5.16 Tabs (Sub-navigation)

```css
.tab-bar {
  display: flex;
  border-bottom: 2px solid #E0E0E0;
  background: #FFFFFF;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}
.tab {
  display: inline-flex;
  align-items: center;
  padding: 0 20px;
  height: 48px;
  font-size: 14px;
  font-family: 'GT Ultra Standard', sans-serif;
  color: #595959;
  border-bottom: 3px solid transparent;
  margin-bottom: -2px;
  white-space: nowrap;
  cursor: pointer;
  transition: color 0.15s ease;
  text-decoration: none;
}
.tab:hover { color: #1A1A1A; }
.tab.active {
  color: #006A4D;
  border-bottom-color: #006A4D;
  font-weight: 700;
  font-family: 'GT Ultra Medium Bold', sans-serif;
}
```

### 5.17 Breadcrumb

```css
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #595959;
  margin-bottom: 24px;
  flex-wrap: wrap;
}
.breadcrumb a {
  color: #006A4D;
  text-decoration: none;
}
.breadcrumb a:hover { text-decoration: underline; }
.breadcrumb-separator {
  color: #CCCCCC;
  font-size: 12px;
}
.breadcrumb-current {
  color: #1A1A1A;
  font-weight: 400;
}
```

### 5.18 Chat / AI Financial Assistant Interface

Specific to the AI financial assistant product.

```css
/* Chat container */
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #F5F5F5;
}
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
/* User message bubble */
.message-user {
  align-self: flex-end;
  max-width: 80%;
  background: #006A4D;
  color: #FFFFFF;
  padding: 12px 16px;
  border-radius: 18px 18px 4px 18px;
  font-size: 15px;
  line-height: 1.5;
}
/* Assistant message bubble */
.message-assistant {
  align-self: flex-start;
  max-width: 85%;
  background: #FFFFFF;
  color: #1A1A1A;
  padding: 12px 16px;
  border-radius: 4px 18px 18px 18px;
  font-size: 15px;
  line-height: 1.6;
  border: 1px solid #E0E0E0;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}
/* Typing indicator */
.message-typing {
  /* Same as .message-assistant */
  display: flex;
  gap: 4px;
  padding: 14px 16px;
  align-items: center;
}
.typing-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #888888;
  animation: typingBounce 1.2s ease-in-out infinite;
}
.typing-dot:nth-child(2) { animation-delay: 0.15s; }
.typing-dot:nth-child(3) { animation-delay: 0.30s; }
@keyframes typingBounce {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-5px); }
}
/* Chat input bar */
.chat-input-bar {
  padding: 12px 16px;
  background: #FFFFFF;
  border-top: 1px solid #E0E0E0;
  display: flex;
  gap: 10px;
  align-items: flex-end;
}
.chat-input {
  flex: 1;
  min-height: 44px;
  max-height: 120px;
  padding: 10px 14px;
  border: 1.5px solid #E0E0E0;
  border-radius: 22px;
  font-size: 15px;
  font-family: 'GT Ultra Standard', sans-serif;
  resize: none;
  outline: none;
}
.chat-input:focus {
  border-color: #006A4D;
  box-shadow: 0 0 0 3px rgba(0,106,77,0.12);
}
.chat-send-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: #006A4D;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFFFFF;
  flex-shrink: 0;
}
.chat-send-btn:disabled {
  background: #E0E0E0;
  cursor: not-allowed;
}
/* Quick reply chips */
.quick-replies {
  display: flex;
  gap: 8px;
  padding: 8px 16px;
  overflow-x: auto;
}
.quick-reply {
  padding: 8px 14px;
  border-radius: 20px;
  border: 1.5px solid #006A4D;
  background: transparent;
  color: #006A4D;
  font-size: 13px;
  font-weight: 700;
  font-family: 'GT Ultra Medium Bold', sans-serif;
  white-space: nowrap;
  cursor: pointer;
  transition: background 0.12s ease;
}
.quick-reply:hover { background: #E6F2EE; }
```

---

## 6. Iconography

- Icon library: Lucide, Heroicons, or custom SVG set — all outline style
- Standard sizes: 16px (inline), 20px (list item), 24px (nav), 28px (feature icon)
- On green backgrounds: white `#FFFFFF`
- On light backgrounds: `#595959` (default), `#006A4D` (active / branded context)
- On nav bar: `#888888` (inactive), `#006A4D` (active)
- Feature icon containers: 44–56px circle or rounded-square, background `#E6F2EE`, icon in `#006A4D`
- Notification badge: `#C0392B` circle, white number, top-right position on icon
- Stroke width: 1.5–2px — do not use filled icons except in specific UI contexts (notification dot, close button)

### Common Icon Map

| Context | Icon | Size |
|---|---|---|
| Home / Overview | house | 24px |
| Transactions | list | 24px |
| Payments / Send | send | 24px |
| Cards | credit-card | 24px |
| Savings | piggy-bank | 24px |
| Investments | trending-up | 24px |
| Chat / Assistant | message-circle | 24px |
| Settings | settings | 24px |
| Notifications | bell | 24px |
| Close | x | 20px |
| Back / Chevron | chevron-left | 20px |
| Forward | chevron-right | 16px |
| Search | search | 20px |
| Filter | sliders | 18px |
| Help | help-circle | 20px |
| Success | check-circle | 20px |
| Error | alert-circle | 20px |
| Warning | alert-triangle | 20px |
| Info | info | 20px |
| Lock | lock | 18px |
| Download | download | 18px |
| External link | external-link | 14px |

---

## 7. Elevation & Shadow

Lloyds digital uses subtle depth. Shadows suggest interactivity and layering without drama.

```css
--shadow-none:    none;
--shadow-xs:      0 1px 2px rgba(0,0,0,0.06);
--shadow-sm:      0 1px 4px rgba(0,0,0,0.08);
--shadow-md:      0 2px 8px rgba(0,0,0,0.10);
--shadow-lg:      0 4px 16px rgba(0,0,0,0.12);
--shadow-xl:      0 8px 32px rgba(0,0,0,0.14);
--shadow-modal:   0 8px 40px rgba(0,0,0,0.20);
```

| Layer | Shadow | Used for |
|---|---|---|
| Flat | `--shadow-none` | Table rows, list items, dividers |
| Raised | `--shadow-sm` | Cards (default) |
| Floating | `--shadow-md` | Sticky headers, floating action buttons |
| Overlay | `--shadow-lg` | Dropdowns, tooltips, popovers |
| Modal | `--shadow-modal` | Modals, bottom sheets |

---

## 8. Border Radius

Lloyds components use generous, friendly radii — consistent with a warm digital banking brand.

```css
--radius-xs:   4px;    /* Table cells, tiny chips */
--radius-sm:   6px;    /* Small buttons, input tags */
--radius-md:   8px;    /* Buttons (default), inputs, small cards */
--radius-lg:   12px;   /* Standard cards, panels */
--radius-xl:   16px;   /* Account cards, modals */
--radius-2xl:  20px;   /* Bottom sheets */
--radius-full: 9999px; /* Pills, badges, toggles, fully rounded buttons */
```

---

## 9. Motion

Transitions are purposeful and restrained. This is a financial product — motion serves orientation and feedback, not entertainment.

```css
--transition-fast:   0.12s ease;
--transition-base:   0.20s ease;
--transition-slow:   0.35s ease;
--transition-spring: 0.30s cubic-bezier(0.34, 1.56, 0.64, 1.0);
```

| Use case | Duration | Easing |
|---|---|---|
| Hover / focus states | 0.12s | ease |
| Button presses | 0.12s | ease |
| Dropdowns / menus | 0.20s | ease |
| Card hover lift | 0.15s | ease |
| Modals / bottom sheets | 0.30s | ease-out |
| Step transitions | 0.30s | ease |
| Typing indicator dots | 1.20s | ease-in-out, infinite |

Do not animate page-level transitions. No parallax, no decorative motion. Loading states use skeleton screens or a single Lloyds-green spinner.

---

## 10. Page Layout

### Desktop Layout
```
┌──────────────────────────────────────────────────────────┐ ← Top nav (64px, #FFFFFF)
├──────────────────────────────────────────────────────────┤
├────────────┬─────────────────────────────────────────────┤ ← Content area
│ Left nav   │   Main content panel                        │
│ (240px)    │   (fluid, bg: #F5F5F5, padding: 32px)       │
│ bg:#FFFFFF │                                             │
│ border-r   │   ┌─────────────────────────────────────┐   │
│ 1px solid  │   │ Card / Panel (bg: #FFFFFF)           │   │
│ #E0E0E0    │   │ border-radius: 12px                  │   │
│            │   └─────────────────────────────────────┘   │
└────────────┴─────────────────────────────────────────────┘
```

### Mobile Layout
```
┌───────────────────────────────┐
│  Top bar (56px, #FFFFFF)      │ ← Logo centre, back icon left, action icon right
├───────────────────────────────┤
│  Content (fluid, #F5F5F5)     │ ← padding: 16px
│  Cards / list items           │
│  bg: #FFFFFF, radius: 12px    │
│                               │
│                               │
│                               │
├───────────────────────────────┤
│  Bottom nav (60px, #FFFFFF)   │ ← 4–5 icon + label tabs
└───────────────────────────────┘
```

### Content Max Width

```css
.content-container {
  max-width: 1180px;
  margin: 0 auto;
  padding: 0 32px;
}
/* Mobile */
@media (max-width: 768px) {
  .content-container {
    padding: 0 16px;
  }
}
```

---

## 11. Accessibility

- All interactive elements meet WCAG 2.1 AA contrast (minimum 4.5:1 for body, 3:1 for large text)
- `#006A4D` on `#FFFFFF` passes AA at all sizes
- `#FFFFFF` on `#006A4D` passes AA at all sizes
- Focus states: `outline: 2px solid #006A4D; outline-offset: 3px` on all interactive elements — never suppress `outline` without providing a visible replacement
- Error states always combine colour + icon + text — never colour alone
- Touch targets: minimum 44×44px on mobile for all interactive elements
- Checkboxes and toggles use `accent-color: #006A4D`
- Screen reader support: all icons include `aria-label` or adjacent visible text; decorative icons use `aria-hidden="true"`
- Form inputs always have associated `<label>` elements (not placeholder-only)
- Avoid motion that could trigger vestibular disorders — respect `prefers-reduced-motion`:

```css
@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after {
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
  }
}
```

---

## 12. Loading States

**Skeleton Screen (preferred over spinners for content areas):**
```css
.skeleton {
  background: linear-gradient(90deg, #F0F0F0 25%, #E8E8E8 50%, #F0F0F0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.4s ease-in-out infinite;
  border-radius: 6px;
}
@keyframes shimmer {
  0%   { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
.skeleton-text  { height: 16px; width: 60%; margin-bottom: 8px; }
.skeleton-title { height: 24px; width: 40%; margin-bottom: 16px; }
.skeleton-card  { height: 120px; width: 100%; border-radius: 12px; }
```

**Spinner (for button loading states and small contexts):**
```css
.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255,255,255,0.35);
  border-top-color: #FFFFFF;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
.spinner.dark {
  border: 2px solid rgba(0,106,77,0.20);
  border-top-color: #006A4D;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
```

---

## 13. Empty States

Used when a section has no data (no transactions, no notifications).

```css
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  text-align: center;
  gap: 12px;
}
.empty-state-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: #E6F2EE;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #006A4D;
  font-size: 28px;
  margin-bottom: 8px;
}
.empty-state-title {
  font-size: 18px;
  font-weight: 700;
  font-family: 'GT Ultra Medium Bold', sans-serif;
  color: #1A1A1A;
}
.empty-state-body {
  font-size: 14px;
  color: #595959;
  line-height: 1.6;
  max-width: 280px;
}
```

---

## 14. Logo Usage

**Official mark:**
- Black Horse SVG in `#006A4D` on light backgrounds
- White variant on dark / green backgrounds
- Wordmark: "Lloyds" in GT Ultra Medium Bold
- Minimum clear space: 12px all sides
- Minimum size: 28px height for mark alone; 100px width for full lockup
- Do not rotate, recolour, or apply effects to the mark

**Approved contexts:**
- Nav bar: full-colour on white background
- Green header or hero: white variant on `#006A4D`
- Print / monochrome: solid black

---

*Maintained by the Lloyds Banking Group Digital Product Design team. Reflects the Lloyds digital banking platform as of May 2026.*
