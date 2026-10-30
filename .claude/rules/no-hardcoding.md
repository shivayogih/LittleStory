---
paths:
  - "app/**/*.kt"
  - "core/ui/**/*.kt"
  - "feature/**/*.kt"
---

# No hardcoding in UI code

A violation blocks review (`./gradlew checkNoHardcoding`, Detekt `MagicNumber`).

- No literal colours, `dp`, `sp`, `TextStyle(...)`, radii, elevation or motion values: use `LittleStoryTheme.colors/spacing/dimens/typography` from `:core:designsystem` (add a token there if one is missing).
- No user-facing text or content descriptions in Kotlin: string resources in the owning module, English and Kannada.
- Limits, options and timings live in `AppConfig`; feature availability in `FeatureFlags`; routes are typed route objects.
- A value a platform API forces you to inline goes in a named constant with a comment saying why.
- Features never depend on each other or on `:data`.
