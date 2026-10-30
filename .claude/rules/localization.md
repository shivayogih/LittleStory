---
paths:
  - "**/res/values*/strings.xml"
  - "docs/localization/**"
---

# Localization

- English (`values/`) is the default and fallback; Kannada (`values-kn/`) must have every key. Missing Kannada falls back to English, never a raw key.
- Positional arguments (`%1$s`) and plurals; never concatenate sentences. Brand name only is `translatable="false"`.
- Kannada written by an agent is **Draft — needs native review**; list new keys for the Translations sheet and never mark them Approved.
- ViewModels expose `UiText`, not strings. Check with `./gradlew checkTranslations`.
- Kannada text needs line height of at least 1.4, no negative tracking, no uppercase transform, and room to wrap at 200% font scale.
