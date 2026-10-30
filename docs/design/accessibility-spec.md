# LittleStory — Accessibility Specification

Purpose: set the accessibility targets for the app, record measured contrast for the design tokens, and list the design gaps that implementation must fix.

Status: Draft for owner review — P1, 29 Oct 2026

Sources: master orchestrator brief §8.4, §17.5; `LittleStory_Figma_Design_Details.md` §4; `design-tokens.json`; workbook NFR-04, TC-032, TC-053–TC-055, LS-123; WCAG 2.1 AA success criteria 1.4.3 (text contrast 4.5:1, large text 3:1) and 1.4.11 (non-text contrast 3:1). Contrast ratios below were computed on 29 Oct 2026 from the token hex values with the WCAG relative-luminance formula.

## 1. Targets

| Area | Target | Verified by |
| --- | --- | --- |
| Touch targets | ≥ 48 × 48 dp for every interactive element (visual size may be smaller, e.g. 42 dp icon buttons, chips, day buttons) | TC-053, Accessibility Scanner |
| Text contrast | ≥ 4.5:1 normal text, ≥ 3:1 for text ≥ 18.66 sp bold or ≥ 24 sp | Token table below, LS-123 |
| Non-text contrast | ≥ 3:1 for focus indicators, input borders that identify a control, toggle states, progress tracks | Token table below |
| Labels | Every interactive element has a localized label; icon-only buttons require `contentDescription` (component API enforces it) | TC-054 |
| TalkBack order | Logical top-to-bottom, start-to-end; merged descriptions for cards | TC-054 |
| States | Selected, checked, disabled and expanded states exposed as semantics, not colour alone | TC-041, LS-016 |
| Font scaling | Usable at 200 % system font scale in English and Kannada; Reader A−/A+ adds to, not replaces, system scale | TC-032 |
| Motion | Infinite twinkle animations stop when system animations are off | TC-055 |
| Announcements | Validation errors and Story-ready ribbon announced once; generation progress announced politely at stage changes, not every tick | LS-081, LS-083, LS-084 |
| Language | All labels and announcements localized; Kannada TalkBack depends on a Kannada TTS voice — limitation documented when absent (R-13) | TC-054 |

## 2. Measured contrast (token pairs)

| Foreground | Background | Ratio | Use | Result |
| --- | --- | --- | --- | --- |
| text #25213A | cream #FFF9F0 | 14.78:1 | Body text | Pass |
| text #25213A | white #FFFFFF | 15.47:1 | Text on cards | Pass |
| text #25213A | surface #F8F5FB | 14.33:1 | Segmented control | Pass |
| muted #716B80 | cream #FFF9F0 | 4.87:1 | Secondary text 10–12 sp | Pass (narrow margin) |
| muted #716B80 | white #FFFFFF | 5.10:1 | Secondary text on cards | Pass |
| muted #716B80 | surface #F8F5FB | 4.72:1 | Inactive segment | Pass (narrow) |
| muted #716B80 | success-bg #EAF5EE | 4.57:1 | Note body text | Pass (narrow) |
| nav-inactive #817B8D | white #FFFFFF | 4.08:1 | Inactive bottom-nav labels 10 sp | **Fail** |
| nav-inactive #817B8D | cream #FFF9F0 | 3.89:1 | Same on cream | **Fail** |
| reader-body #393348 | reader bg #FFFAF2 | 11.61:1 | Story paragraphs | Pass |
| white | indigo #35256F | 12.76:1 | Primary buttons, hero | Pass |
| indigo #35256F | soft-lavender #EAE3FF | 10.29:1 | Secondary buttons, chips | Pass |
| indigo #35256F | cream #FFF9F0 | 12.19:1 | Links | Pass |
| indigo-2 #4B388B | cream #FFF9F0 | 8.96:1 | Eyebrows | Pass |
| white | night #17152F | 17.73:1 | Night screens | Pass |
| yellow #FFD977 | night #17152F | 13.04:1 | Kickers on dark | Pass |
| yellow #FFD977 | indigo #35256F | 9.39:1 | Kicker on hero | Pass |
| lavender #A995F5 | night #17152F | 7.01:1 | Active Create tab on dark | Pass |
| skip #D8D0F5 | night #17152F | 12.04:1 | Onboarding Skip | Pass |
| success #287A54 | success-bg #EAF5EE | 4.70:1 | Trust badge text | Pass (narrow) |
| success #287A54 | white #FFFFFF | 5.25:1 | Success text; toggle on vs knob | Pass |
| error #B42318 | white #FFFFFF | 6.57:1 | Form error | Pass |
| error #B42318 | cream #FFF9F0 | 6.28:1 | Error banner | Pass |
| favourite #9D3158 | favourite-bg #FFEDF2 | 6.19:1 | Active heart | Pass |
| lavender #A995F5 | white #FFFFFF | 2.53:1 | Focus ring, selected outline | **Fail (non-text 3:1)** |
| lavender #A995F5 | line #E9E4EC | 2.02:1 | Reading progress fill vs track | **Fail (non-text 3:1)** |
| line #E9E4EC | white #FFFFFF | 1.25:1 | Input and card borders | **Fail if the border is the only control indicator** |

Notes:

- The design pack expected muted text to fail; measurement shows it passes on every measured background, though with little margin. Do not lighten it further.
- Contrast for text drawn over illustrations and story-art gradients (featured card, story covers, category hero) cannot be computed from tokens alone; check per screen in LS-123.

## 3. Required changes (Proposed — need owner approval because they alter the design)

| # | Gap | Proposal | Ticket |
| --- | --- | --- | --- |
| AX-01 | Nav-inactive label 3.89–4.08:1 | Use `textSecondary` #716B80 (4.87:1 on cream, 5.10:1 on white) for inactive labels, or a darker nav token | LS-019, LS-123 |
| AX-02 | Lavender focus ring 2.53:1 on white | Keep lavender as fill/halo; add a 2 dp indigo (#35256F) focus outline for keyboard/switch focus | LS-015, LS-016 |
| AX-03 | Reading progress lavender on line 2.02:1 | Use indigo for progress fill, or accept as decorative because the page indicator "1 of 5" text conveys progress (preferred, no visual change) | LS-090 |
| AX-04 | Input borders 1.25:1 | Fields already have labels and a filled white surface on cream; focused/error states add contrast. Accept, but error state must not rely on colour alone (icon + text) | LS-016 |
| AX-05 | Toggles nested inside row buttons (SCR-13) | Separate touch targets; row and toggle each have their own semantics | LS-110, TC-041 |
| AX-06 | Chips and day buttons lack selected semantics | `selected`/`toggleableState` semantics; day buttons read full day names | LS-016, LS-111 |
| AX-07 | Targets under 48 dp (42 dp icon buttons, chips, 43×25 toggle) | Keep visual size, extend touch area to 48 dp | LS-015, LS-016 |
| AX-08 | No reduced-motion handling | Motion tokens honour system animation scale | LS-012 |
| AX-09 | Overlapping 15 s glyphs (SCR-11a) | Redraw icons; labels "Rewind 15 seconds" / "Forward 15 seconds" | LS-094 |
| AX-10 | No visible focus style on most buttons | Focus indication on all interactive components | LS-015 |

## 4. Kannada-specific checks

- Kannada text at 9–10 sp (eyebrows, kickers, nav labels) may be hard to read; minimum sizes for Kannada are defined in `docs/localization/kannada-typography.md`.
- Content descriptions and announcements exist in `values-kn/`; no English fallback for accessibility strings in release builds.
- TalkBack in Kannada requires a Kannada TTS voice on the device; test on a device with one, and document the result.

## 5. Test approach

| Check | Tool | When |
| --- | --- | --- |
| Semantics, labels, touch-target size | Compose UI tests with semantics matchers | Every screen ticket |
| Contrast and targets on device | Accessibility Scanner | LS-123 |
| TalkBack traversal EN and KN | Manual, recorded in the PR | Screen tickets, LS-123 |
| Font scale 200 % | Screenshot tests at fontScale 2.0, EN and KN | LS-122 |
| Reduced motion | UI test with animations disabled | LS-012, TC-055 |
