## Ticket

LS-### — <title> · Requirements: FR-xx / NFR-xx · Screens: SCR-xx

## Summary

<!-- What changed and why, in two or three sentences. -->

## Files changed

<!-- Modules and files touched; confirm nothing outside the ticket's allowed scope. -->

## Design references

<!-- docs/design/screens/… and design-detail sections used. -->

## Localization

- [ ] Every new string exists in `values/` and `values-kn/` (Kannada marked "Needs native review")
- [ ] No string concatenation; positional args and plurals only
- [ ] Kannada layout checked (no clipping, wrapping acceptable)
- New string keys:

## No-hardcoding check

- [ ] No hardcoded text, colours, dimensions, typography, routes, limits, URLs or magic numbers in feature code
- New config values and where they live:

## Tests and evidence

| Check | Command | Result (actually run) |
|---|---|---|
| Build | `./gradlew assembleDebug` / `gradlew.bat assembleDebug` | |
| Unit tests | `./gradlew test` | |
| Lint (HardcodedText, MissingTranslation) | `./gradlew lint` | |
| Translation completeness | `./gradlew checkTranslations` | |
| Screenshots EN / KN | | |
| UI / instrumented | | |

## Accessibility

- [ ] Localized content descriptions, 48dp targets, font scale checked

## Acceptance criteria

- [ ] …

## Remaining issues

<!-- Known gaps, follow-ups, open questions. -->

> Human review required. Only the project owner merges.
